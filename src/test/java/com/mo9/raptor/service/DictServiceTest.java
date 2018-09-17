package com.mo9.raptor.service;

import com.mo9.raptor.RaptorApplicationTest;
import com.mo9.raptor.entity.DictDataEntity;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.List;

/**
 * Created by jyou on 2018/9/17.
 *
 * @author jyou
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {RaptorApplicationTest.class}, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class DictServiceTest {

    @Resource
    private DictService dictService;

    @Test
    public void testFindAllDictDataByTypeNo(){
        List<DictDataEntity> list = dictService.findAllDictDataByTypeNo("test_no1");
        System.out.println(list.size());
    }

    @Test
    public void testFindDictData(){
        DictDataEntity dictData = dictService.findDictData("test_no1", "data_no2");
        System.out.println(dictData.getName());
    }
}
