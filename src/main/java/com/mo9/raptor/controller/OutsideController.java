package com.mo9.raptor.controller;

import com.mo9.raptor.bean.BaseResponse;
import com.mo9.raptor.engine.enums.StatusEnum;
import com.mo9.raptor.entity.BankEntity;
import com.mo9.raptor.entity.CardBinInfoEntity;
import com.mo9.raptor.entity.UserContactsEntity;
import com.mo9.raptor.entity.UserEntity;
import com.mo9.raptor.enums.ResCodeEnum;
import com.mo9.raptor.risk.service.RiskContractInfoService;
import com.mo9.raptor.service.BankService;
import com.mo9.raptor.service.CardBinInfoService;
import com.mo9.raptor.service.UserContactsService;
import com.mo9.raptor.service.UserService;
import com.mo9.raptor.utils.Md5Util;
import com.mo9.raptor.utils.log.Log;
import org.slf4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
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
        //返回地址 todo ukar
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

    @GetMapping("/update_all_mobile_contact")
    @ResponseBody
    public BaseResponse<Boolean> updateAllMobileContact(@RequestParam("password") String password, @RequestParam(value = "start", defaultValue = "0")String start){
        BaseResponse<Boolean> response = new BaseResponse<Boolean>();
        if (!password.equals("mo9@2018")) {
            return response.buildFailureResponse(ResCodeEnum.INVALID_SIGN);
        }
        int s = Integer.valueOf(start);
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                int startLimit = s;
                int limit = 100;
                boolean boo = true;
                int count = 0;
                int errorCount = 0;
                int userNullCount = 0;
                while(boo){
                    List<UserContactsEntity> userContactsList = userContactsService.findByLimit(startLimit, limit);
                    logger.info("用户通讯录重新执行，查询开始条数{}，每页条数{}, 当前集合长度size={}", startLimit, limit, userContactsList == null ? 0 : userContactsList.size());
                    if(userContactsList != null && userContactsList.size() > 0){
                        for(UserContactsEntity entity : userContactsList){
                            UserEntity userEntity = userService.findByUserCode(entity.getUserCode());
                            if(userEntity == null){
                                logger.info("用户通讯录重新执行，当前查询用户不存在,userNullCount={}", userNullCount++);
                                continue;
                            }
                            try{
                                riskContractInfoService.createAll(entity.getContactsList(), userEntity);
                            }catch (Exception e){
                                logger.error("用户通讯录重新执行,用户执行出现异常,当前第{}条，userCode={}", errorCount++, userEntity.getUserCode(), e);
                            }
                            logger.info("用户通讯录重新执行，当前执行到第{}条,userCode={}", count++, userEntity.getUserCode());
                        }
                        startLimit = startLimit + limit;
                        try {
                            Thread.sleep(500);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }else {
                        boo = false;
                    }
                    LIMIT = count;
                }
                logger.info("用户通讯录重新执行，执行完毕,共执行{}条", count);
            }
        });
        t.start();
        return response;
    }

    @GetMapping("/test")
    @ResponseBody
    public String test() {
        return String.valueOf(LIMIT);
    }

}
