package com.mo9.raptor.repository;

import com.mo9.raptor.entity.CardBinInfoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Created by jyou on 2018/9/18.
 *
 * @author jyou
 */
public interface CardBinInfoRepository extends JpaRepository<CardBinInfoEntity,Long> {

    /**
     * 根据银行卡bin查询所属银行信息
     * @param cardPrefix
     * @return
     */
    List<CardBinInfoEntity> findByCardPrefix(String cardPrefix);

}
