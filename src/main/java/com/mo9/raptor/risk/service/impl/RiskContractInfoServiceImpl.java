package com.mo9.raptor.risk.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.mo9.raptor.entity.UserEntity;
import com.mo9.raptor.risk.entity.TRiskContractInfo;
import com.mo9.raptor.risk.repo.RiskContractInfoRepository;
import com.mo9.raptor.risk.service.RiskContractInfoService;
import com.mo9.raptor.utils.MobileUtil;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by jyou on 2018/10/18.
 *
 * @author jyou
 */
@Service
public class RiskContractInfoServiceImpl implements RiskContractInfoService {

    private Logger logger = LoggerFactory.getLogger(RiskContractInfoServiceImpl.class);

    @Resource
    private RiskContractInfoRepository riskContractInfoRepository;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createAll(String contractData, UserEntity userEntity) {
        logger.info("手机通讯录，存放mycat开始，userCode={},data是否为空={}", userEntity.getUserCode(), contractData == null ? true : false);
        if(StringUtils.isBlank(contractData)){
            logger.warn("手机通讯录数据获取为空，userCode={}", userEntity.getUserCode());
            return;
        }
        Object json = JSON.parse(contractData);
        JSONObject jsonObject = null;
        JSONArray jsonArray = null;
        if(json instanceof JSONObject){
            jsonObject = (JSONObject) json;
        }else if(json instanceof JSONArray){
            jsonArray = (JSONArray) json;
        }else {
            logger.warn("手机通讯录数据格式不符合要求无法解析，userCode={}", userEntity.getUserCode());
            return;
        }
        List<TRiskContractInfo> list = new ArrayList<>();
        if(jsonObject != null){
            list = parseContractObject(jsonObject, list, userEntity.getMobile(), userEntity.getUserCode());
        }

        if(jsonArray != null){
            list = parseContractArray(jsonArray, list, userEntity.getMobile(), userEntity.getUserCode());
        }
        if(list == null || list.size() == 0){
            return;
        }
        List<String> contractMobilesList = new ArrayList<>();
        list.forEach(t -> contractMobilesList.add(t.getContractMobile()));
        List<TRiskContractInfo> existContractInfos = riskContractInfoRepository.findByMobileAndContractMobilesList(userEntity.getMobile(), contractMobilesList);
        logger.info("手机通讯存放mycat,已存在条数size={}, userCode={}", existContractInfos == null ? 0 : existContractInfos.size(), userEntity.getUserCode());
        if(existContractInfos != null && existContractInfos.size() > 0){
            list = list.stream().filter(t -> isNotExist(t.getContractMobile(), existContractInfos)).collect(Collectors.toList());
        }
        logger.info("手机通讯存放mycat数据条数={},userCode={}", list.size(), userEntity.getUserCode());
        riskContractInfoRepository.saveAll(list);
    }

    private boolean isNotExist(String contractMobile, List<TRiskContractInfo> existContractInfos) {
        int index = -1;
        for(int i = 0 ; i < existContractInfos.size() ; i++){
            TRiskContractInfo tRiskContractInfo = existContractInfos.get(i);
            String contractMobile1 = tRiskContractInfo.getContractMobile();
            if(contractMobile1 != null && contractMobile1.equals(contractMobile)){
                index = i;
                break;
            }
        }
        return index == -1 ? true : false;

    }

    private List<TRiskContractInfo> parseContractArray(JSONArray jsonArray, List<TRiskContractInfo> list, String mobile, String userCode) {
        if(jsonArray == null || jsonArray.size() == 0){
            return list;
        }
        Set<String> mobileSet = new HashSet<>();
        jsonArray.forEach(obj ->{
            JSONObject json = (JSONObject) obj;
            String contractMobile = MobileUtil.processMobile(json.containsKey("contact_mobile") ? json.getString("contact_mobile") : null);
            if(StringUtils.isNotBlank(contractMobile) && !mobileSet.contains(contractMobile)){
                TRiskContractInfo t = new TRiskContractInfo();
                t.setContractMobile(MobileUtil.processMobile(json.containsKey("contact_mobile") ? json.getString("contact_mobile") : null));
                t.setContractName(json.containsKey("contact_name") ? json.getString("contact_name") : null);
                t.setMobile(mobile);
                t.setUserCode(userCode);
                t.setCreateTime(System.currentTimeMillis());
                t.setUpdateTime(System.currentTimeMillis());
                list.add(t);
                mobileSet.add(contractMobile);
            }
        });
        return list;
    }

    /**
     * 解析jsonObject形式通讯录数据
     * @param jsonObject
     * @param list
     * @return
     */
    private List<TRiskContractInfo> parseContractObject(JSONObject jsonObject, List<TRiskContractInfo> list, String mobile, String userCode) {
        if(!jsonObject.containsKey("contact")){
            return list;
        }
        JSONArray array = jsonObject.getJSONArray("contact");
        return parseContractArray(array, list, mobile, userCode);
    }
}
