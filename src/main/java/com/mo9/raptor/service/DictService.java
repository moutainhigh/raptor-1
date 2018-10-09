package com.mo9.raptor.service;

import com.mo9.raptor.entity.DictDataEntity;

import java.util.List;

/**
 * Created by jyou on 2018/9/17.
 *
 * @author jyou
 *
 * 数据字典服务类
 */
public interface DictService {

    /**
     * 根据数据字典父表no查询所有子表data数据
     * @param dictTypeNo
     * @return
     */
    List<DictDataEntity> findAllDictDataByTypeNo(String dictTypeNo);

    /**
     * 查询指定数据字典数据
     * @param dictTypeNo
     * @param dictDataNo
     * @return
     */
    DictDataEntity findDictData(String dictTypeNo, String dictDataNo);

    /**
     * 查询指定数据字典数据值
     * @param dictTypeNo
     * @param dictDataNo
     * @return
     */
    String findDictName(String dictTypeNo, String dictDataNo);

    /**
     * 修改数据字典
     * @param dictData
     * @return
     */
    DictDataEntity updateDictData(DictDataEntity dictData);
}
