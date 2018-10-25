package com.mo9.raptor.service.impl;

import com.mo9.raptor.entity.SpreadChannelEntity;
import com.mo9.raptor.repository.SpreadChannelRepository;
import com.mo9.raptor.service.SpreadChannelService;
import com.mo9.raptor.utils.log.Log;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author zma
 * @date 2018/9/29
 */
@Service
public class SpreadChannelServiceImpl implements SpreadChannelService{
    private static Logger logger = Log.get();

    @Autowired
    private SpreadChannelRepository spreadChannelRepository;

    @Override
    public SpreadChannelEntity findByLoginNameAndPassword(String userName, String password) {
        return spreadChannelRepository.findByLoginNameAndPasswordNotDelete(userName,password);
    }

    @Override
    public List<SpreadChannelEntity> findAll() {
        List<SpreadChannelEntity> all = spreadChannelRepository.findAllNotDelete();
        return all;
    }

    @Override
    public boolean checkSourceIsAllow(String source) {
        List<SpreadChannelEntity> list = findAll();
        if(list == null || list.size() == 0){
            return false;
        }
        for (SpreadChannelEntity entity: list){
            String source1 = entity.getSource();
            logger.info("支持渠道" + source1 + "进入渠道" + source);
            if(source.equals(source1)){
                return true;
            }
        }
        return false;
    }
}
