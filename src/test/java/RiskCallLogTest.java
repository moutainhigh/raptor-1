import com.alibaba.fastjson.JSONObject;
import com.mo9.raptor.risk.entity.TRiskCallLog;
import com.mo9.raptor.risk.entity.TRiskTelBill;
import com.mo9.raptor.risk.entity.TRiskTelInfo;
import com.mo9.raptor.risk.repo.RiskCallLogRepository;
import com.mo9.raptor.risk.repo.RiskTelInfoRepository;
import com.mo9.raptor.risk.service.RiskCallLogService;
import com.mo9.raptor.risk.service.RiskTelBillService;
import com.mo9.raptor.risk.service.RiskTelInfoService;
import com.mo9.raptor.utils.httpclient.HttpClientApi;
import com.mo9.raptor.utils.httpclient.bean.HttpResult;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author wtwei .
 * @date 2018/9/17 .
 * @time 10:15 .
 */
@EnableAspectJAutoProxy
public class RiskCallLogTest extends BaseTest{
    
    @Resource
    private RiskCallLogService riskCallLogService;
    @Resource
    private RiskTelInfoService riskTelInfoService;
    @Resource
    private RiskTelBillService riskTelBillService;
    
    @Resource
    private RiskTelInfoRepository riskTelInfoRepository;

    @Autowired
    private HttpClientApi httpClientApi;
    
    @Test
    public void save(){
        for (int i = 0; i < 1000; i++) {
            TRiskCallLog callLog = new TRiskCallLog();
            BigInteger prex = new BigInteger("13890800000");

            BigInteger mobile = prex.add( new BigInteger(i+ ""));
            
            callLog.setMobile(mobile.toString());

            System.out.println(callLog.getMobile());
            callLog.setCallTel("990011");
            riskCallLogService.save(callLog);
        }
        
    }
    
    @Test
    public void saveTelInfo(){
        for (int i = 0; i < 1000; i++) {
            TRiskTelInfo callLog = new TRiskTelInfo();
            callLog.setMobile("1590000100" + i);
            callLog.setAddress("浦东");
            riskTelInfoService.save(callLog);
        }
    }

    @Test
    public void saveTelBill(){
        for (int i = 0; i < 1000; i++) {
            TRiskTelBill callLog = new TRiskTelBill();
            callLog.setMobile("1590000100" + i);
            callLog.setBillZengzhifei("16");
            riskTelBillService.save(callLog);
        }
    }
    
    @Test
    public void batchSave(){
        List<TRiskTelBill> riskTelBillList = new ArrayList<>();
        for (int i = 0; i < 1000; i++) {
            TRiskTelBill callLog = new TRiskTelBill();
            callLog.setMobile("181900100" + i);
            callLog.setBillZengzhifei("16");
            riskTelBillList.add(callLog);
        }
        
        riskTelBillService.batchSave(riskTelBillList);
    }
    
    @Test
    public void updateTelInfo(){
        TRiskTelInfo telInfo = riskTelInfoService.findByMobile("18501635120");

        System.out.println(telInfo.getFullName());
        
        telInfo.setAddress("上海浦东");
        
        riskTelInfoRepository.saveAndFlush(telInfo);
        
        
    }


    @Test
    public void testDianhuaCallback() {
//        String saveUrl = "http://127.0.0.1/raptorApi/risk/save_call_log";
        String saveUrl = "https://riskclone.mo9.com/raptorApi/risk/save_call_log";
        try {
            String jsonStr = "{ " +
                    "  \"status\": 0, " +
                    "  \"msg\": \"success\", " +
                    "  \"data\": { " +
                    "    \"tel\": \"15826472370\", " +
                    "    \"uid\": \"\", " +
                    "    \"sid\": \"SID3a3fc98a8f364d07a625f5dd6e3c75c3\", " +
                    "    \"call_log_missing_month_list\": [], " +
                    "    \"call_log_possibly_missing_month_list\": [], " +
                    "    \"call_log_ part _missing_month_list\": [], " +
                    "    \"phone_bill_missing_month_list\": [], " +
                    "    \"call_log\": [ " +
                    "      { " +
                    "        \"call_cost\": \"1.20\", " +
                    "        \"call_time\": \"12-07 09:59:33\", " +
                    "        \"call_method\": \"被叫\", " +
                    "        \"call_type\": \"国内漫游\", " +
                    "        \"call_to\": \"\", " +
                    "        \"call_from\": \"北北京\", " +
                    "        \"call_duration\": \"00⼩小时02分02秒\", " +
                    "        \"call_tel\": \"95510\" " +
                    "      } " +
                    "    ], " +
                    "    \"bill\": [ " +
                    "      { " +
                    "        \"bill_amount\": \"5.76\", " +
                    "        \"bill_zengzhifei\": \"0.00\", " +
                    "        \"bill_qita\": \"0.00\", " +
                    "        \"bill_package\": \"1.08\", " +
                    "        \"bill_ext_sms\": \"0.00\", " +
                    "        \"bill_daishoufei\": \"0.00\", " +
                    "        \"bill_ext_data\": \"0.00\", " +
                    "        \"bill_month\": \"201704\", " +
                    "        \"bill_ext_calls\": \"4.68\" " +
                    "      }, " +
                    "      { " +
                    "        \"bill_amount\": \"10.86\", " +
                    "        \"bill_zengzhifei\": \"0.00\", " +
                    "        \"bill_qita\": \"0.00\", " +
                    "        \"bill_package\": \"10.00\", " +
                    "        \"bill_ext_sms\": \"0.10\", " +
                    "        \"bill_daishoufei\": \"0.00\", " +
                    "        \"bill_ext_data\": \"0.00\", " +
                    "        \"bill_month\": \"201703\", " +
                    "        \"bill_ext_calls\": \"0.76\" " +
                    "      }, " +
                    "      { " +
                    "        \"bill_amount\": \"7.80\", " +
                    "        \"bill_zengzhifei\": \"0.00\", " +
                    "        \"bill_qita\": \"0.00\", " +
                    "        \"bill_package\": \"7.04\", " +
                    "        \"bill_ext_sms\": \"0.00\", " +
                    "        \"bill_daishoufei\": \"0.00\", " +
                    "        \"bill_ext_data\": \"0.00\", " +
                    "        \"bill_month\": \"201702\", " +
                    "        \"bill_ext_calls\": \"0.76\" " +
                    "      } " +
                    "    ], " +
                    "    \"tel_info\": { " +
                    "      \"open_date\": \"2012-06-18\", " +
                    "      \"full_name\": \"邢证\", " +
                    "      \"id_card\": \"\", " +
                    "      \"address\": \"北京大兴\" " +
                    "    } " +
                    "  } " +
                    "}";

            System.out.println(jsonStr);

            HttpResult resJson = httpClientApi.doPostJson(saveUrl, jsonStr);
            System.out.println(resJson.getCode());
            System.out.println(resJson.getData());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
