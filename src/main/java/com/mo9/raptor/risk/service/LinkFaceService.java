package com.mo9.raptor.risk.service;

/**
 * @author yngong
 */
public interface LinkFaceService {
    double preventHack(String userCode, String imageUrl);

    double judgeOnePerson(String userCode, String imageUrl, String idNumber, String name);

    double judgeIdCardPolice(String userCode, String imageUrl, String idNumber, String name);
}
