package com.mo9.raptor.controller;

import com.alibaba.fastjson.JSONObject;
import com.mo9.raptor.bean.BaseResponse;
import com.mo9.raptor.bean.ReqHeaderParams;
import com.mo9.raptor.bean.req.FileReq;
import com.mo9.raptor.entity.UserEntity;
import com.mo9.raptor.enums.ResCodeEnum;
import com.mo9.raptor.service.UserService;
import com.mo9.raptor.utils.log.Log;
import com.mo9.raptor.utils.oss.OSSFileUpload;
import com.mo9.raptor.utils.upload.FileStreamTransformer;
import com.mo9.raptor.utils.upload.SpringMultipartFileTransformer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by jyou on 2018/9/17.
 *
 * @author jyou
 * 文件处理类
 */
@RestController
@RequestMapping(value = "/file")
public class FileController {
    private static Logger logger = Log.get();

    @Autowired
    private OSSFileUpload ossFileUpload;

    @Autowired
    private UserService userService;

    /**
     * 文件上传
     * @param request
     * @param fileReq
     * @return
     */
    @PostMapping("/upload")
    public BaseResponse<Map<String, Object>> fileUpload(HttpServletRequest request, @Validated FileReq fileReq){
        BaseResponse<Map<String, Object>> response = new BaseResponse<Map<String, Object>>();
        Map<String, Object> map = new HashMap<String, Object>();
        String userCode = request.getHeader(ReqHeaderParams.ACCOUNT_CODE);
        MultipartFile file = fileReq.getFile();
        try{
            UserEntity userEntity = userService.findByUserCodeAndDeleted(userCode, false);
            if(userEntity == null ){
                logger.warn("文件上传-->用户不存在userCode={}", userCode);
                return response.buildFailureResponse(ResCodeEnum.USER_NOT_EXIST);
            }
            if (file.getSize() > 1024 * 1024 * 2) {
                logger.warn("文件上传-->大小超过限制userCode={}", userCode);
                return response.buildFailureResponse(ResCodeEnum.FILE_SIZE_TOO_MAX);
            }
            FileStreamTransformer fileStreamTransformer = SpringMultipartFileTransformer.transformer(file);
            String url = ossFileUpload.upload(fileStreamTransformer);
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("path", url);
            map.put("entity", jsonObject);
            logger.info("文件上传-->上传成功userCode={}", userCode);
            return response.buildSuccessResponse(map);
        }catch (Exception e){
            logger.error("文件上传出现异常-->系统内部异常", e);
            return response.buildFailureResponse(ResCodeEnum.EXCEPTION_CODE);
        }
    }
}
