package com.mo9.raptor.service;

/**
 * Created by ycheng on 2018/08/01.
 *
 * @author ycheng
 */
public interface DingTalkService {

    void sendNotice(String title, String message);

    void sendText(String message);

}
