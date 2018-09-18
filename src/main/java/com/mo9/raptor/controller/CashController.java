package com.mo9.raptor.controller;

import com.mo9.raptor.bean.BaseResponse;
import com.mo9.raptor.entity.CardBinInfoEntity;
import com.mo9.raptor.enums.ResCodeEnum;
import com.mo9.raptor.service.CardBinInfoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by jyou on 2018/9/18.
 *
 * @author jyou
 */
@RestController
@RequestMapping(value = "/cash")
public class CashController {
    private static Logger logger = LoggerFactory.getLogger(CashController.class);
    @Resource
    private CardBinInfoService cardBinInfoService;

    @GetMapping(value = "/fetch_card_bank_name")
    public BaseResponse<Map<String, Object>> fetchCardBankName(@RequestParam("card") String card){
        BaseResponse<Map<String, Object>> response = new BaseResponse<Map<String, Object>>();
        Map<String, Object> map = new HashMap<String, Object>();
        try{
            if(card.length() < 6){
                logger.warn("银行卡小于6位，不符合要求card={}", card);
                return response.buildFailureResponse(ResCodeEnum.BANK_VERIFY_ERROR);
            }
            card = card.substring(0, 6);
            CardBinInfoEntity cardBinInfoEntity = cardBinInfoService.findByCardPrefix(card);
            if(cardBinInfoEntity == null){
                logger.warn("银行卡查询不存在，card={}", card);
                return response.buildFailureResponse(ResCodeEnum.BANK_CARD_NOT_EXIST);
            }
            map.put("cardBankName", cardBinInfoEntity.getCardBank());
            return response.buildSuccessResponse(map);
        }catch (Exception e){
            logger.error("获取银行卡名称出现异常card={}", card ,e);
            return response.buildFailureResponse(ResCodeEnum.EXCEPTION_CODE);
        }
    }
}
