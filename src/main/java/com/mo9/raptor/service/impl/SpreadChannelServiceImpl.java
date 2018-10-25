package com.mo9.raptor.service.impl;

import com.mo9.raptor.entity.SpreadChannelEntity;
import com.mo9.raptor.repository.SpreadChannelRepository;
import com.mo9.raptor.service.SpreadChannelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author zma
 * @date 2018/9/29
 */
@Service
public class SpreadChannelServiceImpl implements SpreadChannelService{

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
            if(source.equals(source1)){
                return true;
            }
        }
        return false;
    }
}
