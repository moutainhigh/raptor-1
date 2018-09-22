package com.mo9.raptor.service.impl;

import com.mo9.raptor.entity.DictDataEntity;
import com.mo9.raptor.repository.DictDataRepository;
import com.mo9.raptor.repository.DictTypeRepository;
import com.mo9.raptor.service.DictService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

/**
 * Created by jyou on 2018/9/17.
 *
 * @author jyou
 */
@Service
public class DictServiceImpl implements DictService {

    @Resource
    private DictTypeRepository dictTypeRepository;

    @Resource
    private DictDataRepository dictDataRepository;

    @Override
    public List<DictDataEntity> findAllDictDataByTypeNo(String dictTypeNo){
        return dictDataRepository.findAllDictDataByTypeNo(dictTypeNo);
    }

    @Override
    public DictDataEntity findDictData(String dictTypeNo, String dictDataNo) {
        return dictDataRepository.findByDictTypeNoAndDictDataNo(dictTypeNo, dictDataNo);
    }

    @Override
    public String findDictName(String dictTypeNo, String dictDataNo) {
        DictDataEntity dictData = findDictData(dictTypeNo, dictDataNo);
        return dictData.getName();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public DictDataEntity updateDictData(DictDataEntity dictData) {
        DictDataEntity entity = dictDataRepository.save(dictData);
        return entity;
    }

}
