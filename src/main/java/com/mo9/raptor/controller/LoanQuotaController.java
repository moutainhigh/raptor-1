package com.mo9.raptor.controller;

import com.alibaba.fastjson.JSONObject;
import com.mo9.raptor.bean.BaseResponse;
import com.mo9.raptor.bean.res.ProductRes;
import com.mo9.raptor.entity.LoanProductEntity;
import com.mo9.raptor.service.LoanProductService;
import com.mo9.raptor.utils.log.Log;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by xtgu on 2018/9/16.
 * @author xtgu
 */
@RestController
@RequestMapping("/loan_quota")
public class LoanQuotaController {

    private static Logger logger = Log.get();
    @Autowired
    private LoanProductService productService ;

    /**
     * 查询系统是否开启
     * @return
     */
    @GetMapping("/list")
    public BaseResponse<JSONObject> systemSwitch(HttpServletRequest request , HttpServletResponse res){
        BaseResponse<JSONObject> response = new BaseResponse<JSONObject>();
        JSONObject returnJson = new JSONObject();
        List<LoanProductEntity> productEntityList = productService.findNotDelete();
        List<ProductRes> productResList = setProductRes(productEntityList);
        returnJson.put("loan" , productResList);
        response.setData(returnJson);
        return response ;
    }

    /**
     * 封装返回参数
     * @param productEntityList
     * @return
     */
    private List<ProductRes> setProductRes(List<LoanProductEntity> productEntityList) {
        List<ProductRes> list = new ArrayList<ProductRes>();
        for(LoanProductEntity productEntity : productEntityList){
            ProductRes productRes = new ProductRes() ;
            productRes.setLoanable(productEntity.getAmount());
            productRes.setInterest(productEntity.getInterest());
            productRes.setActuallyGet(productEntity.getActuallyGetAmount());
            productRes.setPeriod(productEntity.getPeriod());
            productRes.setDueFee(productEntity.getPenaltyForDay());
            productRes.setRenewFee(productEntity.getRenewalBaseAmount()) ;
            list.add(productRes) ;
        }
        return list ;
    }

}
