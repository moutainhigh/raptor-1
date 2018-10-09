package com.mo9.raptor.service;

import com.mo9.raptor.bean.vo.CommonUserInfo;

import java.util.Map;

/**
 * Created by xtgu on 2018/9/25.
 * @author xtgu
 */
public interface CommonService {

    /**
     * 查询用户相关信息
     * @param nowStr
     * @return
     */
    Map<String , Integer> findUserInfo(String nowStr);

    /**
     * 查询放款相关数据
     * @param nowStr
     * @return
     */
    Map<String , Integer> findLoanInfo(String nowStr);

    /**
     * 查询还款相关数据
     * @param nowStr
     * @return
     */
    Map<String , Integer> findRepayInfo(Long nowStr);
}
