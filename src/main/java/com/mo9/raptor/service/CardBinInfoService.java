package com.mo9.raptor.service;

import com.mo9.raptor.entity.CardBinInfoEntity;

/**
 * Created by jyou on 2018/9/18.
 *
 * @author jyou
 */
public interface CardBinInfoService {

    /**
     * 根据银行卡bin查询所属银行信息
     * @param cardPrefix
     * @return
     */
    CardBinInfoEntity findByCardPrefix(String cardPrefix);
}
