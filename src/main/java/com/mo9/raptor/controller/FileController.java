package com.mo9.raptor.controller;

import com.mo9.raptor.bean.BaseResponse;
import com.mo9.raptor.bean.ReqHeaderParams;
import com.mo9.raptor.bean.req.FileReq;
import com.mo9.raptor.entity.UserEntity;
import com.mo9.raptor.enums.ResCodeEnum;
import com.mo9.raptor.service.UserService;
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

/**
 * Created by jyou on 2018/9/17.
 *
 * @author jyou
 * 文件处理类
 */
@RestController
@RequestMapping(value = "/file")
public class FileController {
    private static Logger logger = LoggerFactory.getLogger(FileController.class);

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
    public BaseResponse<String> fileUpload(HttpServletRequest request, @Validated FileReq fileReq){
        BaseResponse<String> response = new BaseResponse<String>();
        String userCode = request.getHeader(ReqHeaderParams.ACCOUNT_CODE);
        MultipartFile file = fileReq.getFile();
        try{
            UserEntity userEntity = userService.findByUserCodeAndDeleted(userCode, false);
            if(userEntity == null ){
                logger.warn("文件上传-->用户不存在");
                return response.buildFailureResponse(ResCodeEnum.USER_NOT_EXIST);
            }
            if (file.getSize() > 1024 * 1024 * 2) {
                logger.warn("文件上传-->大小超过限制");
                return response.buildFailureResponse(ResCodeEnum.FILE_SIZE_TOO_MAX);
            }
            FileStreamTransformer fileStreamTransformer = SpringMultipartFileTransformer.transformer(file);
            String url = ossFileUpload.upload(fileStreamTransformer);
            return response.buildSuccessResponse(url);
        }catch (Exception e){
            logger.error("文件上传出现异常-->系统内部异常", e);
            return response.buildFailureResponse(ResCodeEnum.EXCEPTION_CODE);
        }
    }
}
