package com.mo9.raptor.service.impl;

import com.mo9.raptor.entity.CardBinInfoEntity;
import com.mo9.raptor.repository.CardBinInfoRepository;
import com.mo9.raptor.service.CardBinInfoService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

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
        List<CardBinInfoEntity> list =  cardBinInfoRepository.findByCardPrefix(cardPrefix);
        return list == null || list.size() == 0 ? null : list.get(0);
    }
}
