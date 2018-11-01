package com.mo9.raptor.risk.rule;

import com.alibaba.fastjson.JSON;

/**
 * Created by jyou on 2018/10/31.
 *
 * @author jyou
 */
public class IRuleEntity {

    public String version = "V1.0.1";

    /**
     * 实际值
     */
    public String actualVal;

    /**
     * 预期值
     */
    public String expectedVal;


    /**
     * 中文描述
     */
    public String enName;

    /**
     * 英文名称
     */
    public String cnName;

    /**
     * 执行结果，是否通过
     */
    public boolean hit = false;

    public IRuleEntity buildVersion(String version){
        this.version = version;
        return this;
    }

    public IRuleEntity buildActualVal(String actualVal){
        this.actualVal = actualVal;
        return this;
    }

    public IRuleEntity buildExpectedVal(String expectedVal){
        this.expectedVal = expectedVal;
        return this;
    }

    public IRuleEntity buildCnName(String cnName){
        this.cnName = cnName;
        return this;
    }

    public IRuleEntity buildEnName(String enName){
        this.enName = enName;
        return this;
    }

    public IRuleEntity buildHit(boolean hit){
        this.hit = hit;
        return this;
    }

    @Override
    public String toString() {
        return "IRuleEntity{" +
                "version='" + version + '\'' +
                ", actualVal='" + actualVal + '\'' +
                ", expectedVal='" + expectedVal + '\'' +
                ", enName='" + enName + '\'' +
                ", cnName='" + cnName + '\'' +
                ", hit=" + hit +
                '}';
    }

    public static void main(String[] args) {
        IRuleEntity ruleEntity = new IRuleEntity().buildVersion("1.0.5");
        System.out.println(JSON.toJSONString(ruleEntity));
    }
}
