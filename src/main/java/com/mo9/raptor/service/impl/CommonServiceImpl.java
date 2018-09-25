package com.mo9.raptor.service.impl;

import com.mo9.raptor.bean.vo.CommonUserInfo;
import com.mo9.raptor.repository.CommonRepository;
import com.mo9.raptor.service.CommonService;
import com.mo9.raptor.utils.log.Log;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * Created by xtgu on 2018/9/25.
 * @author xtgu
 */
@Service
public class CommonServiceImpl  implements CommonService {
    private static Logger logger = Log.get();

    @Autowired
    private CommonRepository commonRepository ;

    @Override
    public Map<String , Integer> findUserInfo(String nowStr) {
        return commonRepository.findUserInfo(nowStr);
    }

    @Override
    public Map<String, Integer> findLoanInfo(String nowStr) {
        return commonRepository.findLoanInfo(nowStr);
    }

    @Override
    public Map<String, Integer> findRepayInfo(String nowStr) {
        return commonRepository.findRepayInfo(nowStr);
    }
}
