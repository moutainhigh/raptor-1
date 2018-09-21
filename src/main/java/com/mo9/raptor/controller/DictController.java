package com.mo9.raptor.controller;

import com.mo9.raptor.bean.BaseResponse;
import com.mo9.raptor.entity.DictDataEntity;
import com.mo9.raptor.service.DictService;
import org.apache.commons.lang.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * Created by jyou on 2018/9/21.
 *
 * @author jyou
 */
@RestController
@RequestMapping(value = "/dict")
public class DictController {

    @Resource
    private DictService dictService;


    @RequestMapping(value = "/update_daily_lend_amount")
    public BaseResponse<DictDataEntity> updateDailyLendAmount(@RequestParam("password") String password, @RequestParam("dictTypeNo")String dictTypeNo, @RequestParam("dictDataNo")String dictDataNo
                                        , @RequestParam("value")String value, @RequestParam(value = "newDictDataNo", required = false)String newDictDataNo){

        BaseResponse<DictDataEntity> response = new BaseResponse<DictDataEntity>();

        //检验密码，Md5Util.getMD5("raptor_daily_lend_amount_2018-09-21~").toUpperCase()
        if(!"36371A55255778008E2D554DB35902C3".equals(password)){
            response.setMessage("密码错误");
            return response;
        }
        DictDataEntity dictData = dictService.findDictData(dictTypeNo, dictDataNo);
        if(dictData == null){
            response.setMessage("数据配置不存在");
            return response;
        }
        dictData.setName(value);
        if(StringUtils.isNotBlank(newDictDataNo)){
            dictData.setDictDataNo(newDictDataNo);
        }
        DictDataEntity entity = dictService.updateDictData(dictData);
        return response.buildSuccessResponse(entity);
    }


}
