package com.mo9.raptor.service;

/**
 * Created by jyou on 2018/10/22.
 *
 * @author jyou
 */
public interface ShixinService {
    /**
     * 根据姓名喝idCard查询信息是否存在
     * @param cardNum
     * @param iname
     * @return
     */
    long findByCardNumAndIname(String cardNum, String iname);
}
