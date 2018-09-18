package com.mo9.raptor.service.impl;

import com.mo9.raptor.entity.CardBinInfoEntity;
import com.mo9.raptor.repository.CardBinInfoRepository;
import com.mo9.raptor.service.CardBinInfoService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * Created by jyou on 2018/9/18.
 *
 * @author jyou
 */
@Service
public class CardBinInfoServiceImpl implements CardBinInfoService {

    @Resource
    private CardBinInfoRepository cardBinInfoRepository;


    @Override
    public CardBinInfoEntity findByCardPrefix(String cardPrefix) {
        return cardBinInfoRepository.findByCardPrefix(cardPrefix);
    }
}
