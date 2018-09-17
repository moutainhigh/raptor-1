package com.mo9.raptor.repository;

import com.mo9.raptor.entity.DictDataEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Created by jyou on 2018/9/17.
 *
 * @author jyou
 */
public interface DictDataRepository extends JpaRepository<DictDataEntity,Long> {

    /**
     * 根据数据字典父表no查询所有子表data数据
     * @param dictTypeNo
     * @return
     */
    @Query("select t from DictDataEntity t where t.dictTypeNo = ?1")
    List<DictDataEntity> findAllDictDataByTypeNo(String dictTypeNo);

    DictDataEntity findByDictTypeNoAndDictDataNo(String dictTypeNo, String dictDataNo);
}
