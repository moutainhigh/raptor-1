package com.mo9.raptor.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.aliyun.oss.OSSClient;
import com.mo9.raptor.bean.BaseResponse;
import com.mo9.raptor.engine.enums.StatusEnum;
import com.mo9.raptor.entity.BankEntity;
import com.mo9.raptor.entity.CardBinInfoEntity;
import com.mo9.raptor.entity.UserContactsEntity;
import com.mo9.raptor.entity.UserEntity;
import com.mo9.raptor.enums.ResCodeEnum;
import com.mo9.raptor.service.BankService;
import com.mo9.raptor.service.CardBinInfoService;
import com.mo9.raptor.service.UserContactsService;
import com.mo9.raptor.service.UserService;
import com.mo9.raptor.utils.DateUtils;
import com.mo9.raptor.utils.Md5Util;
import com.mo9.raptor.utils.RandomUtils;
import com.mo9.raptor.utils.log.Log;
import com.mo9.raptor.utils.oss.OSSProperties;
import com.mo9.risk.app.entity.User;
import com.mo9.risk.service.RiskAuditService;
import com.mo9.risk.service.RiskContractInfoService;
import org.slf4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.*;
import java.util.*;

/**
 * Created by jyou on 2018/9/29.
 *
 * @author jyou
 *         对外暴露接口
 */
@Controller
@RequestMapping(value = "/outside")
public class OutsideController {

    private static final String salt = "rtsDDcogZcPCu!NYkfgfjQq6O;~2Brtr";
    private static Logger logger = Log.get();
    //    @Value("${raptor.url}")
    private String raptorUrl;

    @Resource
    private UserService userService;

    @Resource
    private BankService bankService;

    @Resource
    private CardBinInfoService cardBinInfoService;

    @Resource
    private RiskContractInfoService riskContractInfoService;

    @Resource
    private UserContactsService userContactsService;

    @Resource
    private RiskAuditService riskAuditService;

    @Resource
    private OSSProperties ossProperties;

    @Value("${raptor.sockpuppet}")
    private String sockpuppet;

    private int LIMIT = 0;
    /**
     * 拉黑用户
     *
     * @param userCode
     * @param desc
     * @param sign
     * @return
     */
    @GetMapping(value = "/to_black_user")
    @ResponseBody
    public BaseResponse<Boolean> toBlackUser(@RequestParam("userCode") String userCode, @RequestParam("desc") String desc, @RequestParam("sign") String sign) {
        BaseResponse<Boolean> response = new BaseResponse<Boolean>();
        try {
            String str = userCode + desc + salt;
            String md5 = Md5Util.getMD5(str);
            if (!md5.equals(sign)) {
                return response.buildFailureResponse(ResCodeEnum.SIGN_CHECK_ERROR);
            }
            UserEntity userEntity = userService.findByUserCodeAndDeleted(userCode, false);
            if (userEntity == null) {
                return response.buildFailureResponse(ResCodeEnum.USER_NOT_EXIST);
            }
            if (!StatusEnum.PASSED.name().equals(userEntity.getStatus())) {
                return response.buildFailureResponse(ResCodeEnum.NOT_SUPPORT_TO_BLACK);
            }
            userService.toBlackUser(userEntity, desc);
            return response.buildSuccessResponse(true);
        } catch (Exception e) {
            Log.error(logger, e, "拉黑用户----->>>>发生异常,userCode={}", userCode);
            return response.buildFailureResponse(ResCodeEnum.EXCEPTION_CODE);
        }
    }

    /**
     * 暂时无用 TODO
     *
     * @param model
     * @param source
     * @param subSource
     * @return
     */
    @GetMapping(value = "/to_source_login")
    public String toSourceLogin(Model model, @RequestParam("source") String source, @RequestParam("subSource") String subSource) {
        model.addAttribute("source", source);
        model.addAttribute("subSource", subSource);
        model.addAttribute("host", raptorUrl);
        return "/test";
    }


    @GetMapping("/update_all_bank_name")
    @ResponseBody
    public BaseResponse<Boolean> updateAllBankName(String password) {
        BaseResponse<Boolean> response = new BaseResponse<Boolean>();
        if (!password.equals("mo9@2018")) {
            return response.buildFailureResponse(ResCodeEnum.INVALID_SIGN);
        }
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                List<BankEntity> list = bankService.findAll();
                if (list == null || list.size() == 0) {
                    return;
                }

                for (BankEntity bankEntity : list) {
                    String bankNo = bankEntity.getBankNo();
                    if (org.apache.commons.lang.StringUtils.isBlank(bankNo) || bankNo.length() < 6) {
                        continue;
                    }
                    CardBinInfoEntity cardBinInfoEntity = cardBinInfoService.findByCardPrefix(bankNo.substring(0, 6));
                    if (cardBinInfoEntity == null) {
                        bankEntity.setBankName("未知");
                    } else {
                        String bankName = bankEntity.getBankName();
                        String cardBank = cardBinInfoEntity.getCardBank();
                        if (cardBank.equals(bankName)) {
                            continue;
                        }
                        bankEntity.setBankName(cardBank);
                    }
                    bankService.save(bankEntity);
                }
            }
        });
        t.start();
        return response.buildSuccessResponse(true);
    }

    @PostMapping("/update_mobile_contact")
    @ResponseBody
    public BaseResponse<Boolean> updateAllMobileContact(@RequestParam("file") MultipartFile file){
        BaseResponse<Boolean> response = new BaseResponse<Boolean>();
        if(file.getSize() <= 0){
            return response.buildFailureResponse(ResCodeEnum.EXCEPTION_CODE);
        }
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    logger.info("开始执行update_mobile_contact,size={}", file.getSize());
                    InputStream inputStream = file.getInputStream();
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                    String userCode = null;
                    while((userCode = bufferedReader.readLine()) != null){
                        logger.info("开始执行用户的通讯录匹配,userCode={}", userCode);
                        UserEntity userEntity = userService.findByUserCode(userCode);
                        try {
                            UserContactsEntity userContactsEntity = userContactsService.findLatelyUserContactByUserCode(userCode);
                            if(userEntity == null || userContactsEntity == null){
                                logger.info("同步通讯录重新执行，信息查询不存在userCode={}", userCode);
                                continue;
                            }
                            riskContractInfoService.createAll(userContactsEntity.getContactsList(), userCode, userEntity.getMobile());
                        }catch (Exception e){
                            logger.error("同步通讯录重新执行出现异常,userCode={}", userCode, e);
                        }
                        logger.info("用户同步通讯录执行完毕，mobile={}", userEntity.getMobile());
                    }

                }catch (Exception e){
                    logger.error("同步通讯录重新执行出现异常", e);
                }
                logger.info("同步通讯录重新执行，执行完毕");
            }
        });
        t.start();
        response.setMessage("操作成功");
        return response;
    }

    @RequestMapping("/manual_audit")
    @ResponseBody
    public BaseResponse<Boolean> manualAudit(@RequestParam("file") MultipartFile file, @RequestParam("password")String password,
                                             @RequestParam(value = "limit", required = false, defaultValue = "1000")String limit){
        BaseResponse<Boolean> response = new BaseResponse<Boolean>();
        if (!password.equals("mo9@2018")) {
            return response.buildFailureResponse(ResCodeEnum.INVALID_SIGN);
        }

        if(file.getSize() == 0){
            return response.buildSuccessResponse(true);
        }
        Integer limitNum = Integer.valueOf(limit);
        Thread t = new Thread(() -> {
            List<JSONObject> list = new ArrayList<>();
            try {
                InputStream inputStream = file.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                String userCode = null;
                String dateStr = DateUtils.formartDate(new Date());
                while((userCode = bufferedReader.readLine()) != null){
                    userCode = userCode.trim();
                    JSONObject res = riskAuditService.manualAudit(userCode);
                    if(res != null){
                        JSONObject json = new JSONObject();
                        json.put(userCode, res);
                        list.add(json);
                    }
                    if(list.size() % limitNum == 0){
                        //上传到oss
                        String fileName = ossProperties.getCatalogCallRule() + "/" + sockpuppet + "-" + dateStr + "-" + System.currentTimeMillis() + ".json";
                        uploadFile2Oss(JSON.toJSONString(list), fileName);
                        list = new ArrayList();
                    }
                }
                if(list.size() > 0){
                    //上传到oss
                    String fileName = ossProperties.getCatalogCallRule() + "/" + sockpuppet + "-" + dateStr + "-" + System.currentTimeMillis() + ".json";
                    uploadFile2Oss(JSON.toJSONString(list), fileName);
                }
            } catch (IOException e) {
                logger.error("手动触发风控规则-->获取文件流出现异常", e);
            }
        });


        t.start();
        return response.buildSuccessResponse(true);
    }

    @GetMapping("/test")
    @ResponseBody
    public String test() {
        return String.valueOf(LIMIT);
    }

    private void uploadFile2Oss(String str, String fileName){

        try {
            OSSClient ossClient = new OSSClient(ossProperties.getWriteEndpoint(), ossProperties.getAccessKeyId(), ossProperties.getAccessKeySecret());
            ossClient
                    .putObject(
                            ossProperties.getBucketName(),
                            fileName,
                            new ByteArrayInputStream(str.getBytes())
                    );
            ossClient.shutdown();

            StringBuilder sb = new StringBuilder();
            sb.append(ossProperties.getHttpPrefix())
                    .append(ossProperties.getReadEndpoint().substring(ossProperties.getHttpPrefix().length()))
                    .append("/").append(fileName);
            logger.info("手动触发风控规则-->上传oss,日期：{}，返回地址：{}" ,DateUtils.formartDate(new Date()) ,sb.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
