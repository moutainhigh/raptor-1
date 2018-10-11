package com.mo9.raptor;

import com.alibaba.fastjson.JSONObject;
import com.mo9.raptor.controller.LoanOrderControllerTest;
import com.mo9.raptor.utils.Md5Encrypt;
import com.mo9.raptor.utils.httpclient.HttpClientApi;
import com.mo9.raptor.utils.httpclient.bean.HttpResult;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by xzhang on 2018/9/30.
 */
@EnableAspectJAutoProxy
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {RaptorApplicationTest.class}, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class offlineRepayTest {

    private static final Logger logger = LoggerFactory.getLogger(offlineRepayTest.class);


    @Autowired
    private HttpClientApi httpClientApi;

    @Test
    public void offlineRepay() {
        List<String[]> listStringArray = new ArrayList<String[]>();

//        listStringArray.add(new String[]{"13032258586", "TTYQ-231928460354928640", "0B5F6742D206F2D62463550C7EF87BF1", "POSTPONE", "287", "张轩", "线下还款"});
//        listStringArray.add(new String[]{"13610643501", "TTYQ-230116681102536704", "5F4E6520ACE11343A30A852D1B2FEBCC", "POSTPONE", "407", "张轩", "线下还款"});
//        listStringArray.add(new String[]{"13616229125", "TTYQ-231462252270727168", "C147478250D89516CB9E74C13787E50D", "POSTPONE", "287", "张轩", "线下还款"});
//        listStringArray.add(new String[]{"13794529038", "TTYQ-231568962754850816", "4AF661ACB9CE67C7371E3F62CF700B0D", "POSTPONE", "287", "张轩", "线下还款"});
//        listStringArray.add(new String[]{"13909892200", "TTYQ-229657513480568832", "B4E170AD0F86DC22488638142E95B751", "REPAY", "1007", "张轩", "线下还款"});
//        listStringArray.add(new String[]{"13939773310", "TTYQ-232040272706543616", "2956FFA84EDEB0521F301952F4F3C40D", "REPAY", "1037", "张轩", "线下还款"});
//        listStringArray.add(new String[]{"15042285657", "TTYQ-231929670369685504", "C8E8909FB2B06F528CE6769153E6B3A3", "POSTPONE", "287", "张轩", "线下还款"});
//        listStringArray.add(new String[]{"15605550100", "TTYQ-232073595701051392", "8C0F4FEBECB42D2B09362013D95282A0", "POSTPONE", "287", "张轩", "线下还款"});
//        listStringArray.add(new String[]{"15619383818", "TTYQ-229504880555999232", "AA8B8B7C91BCF47EE40AB1BC32B46488", "POSTPONE", "287", "张轩", "线下还款"});
        listStringArray.add(new String[]{"15926091444", "TTYQ-231594900251099136", "6458AC7C4CE9A751842BDF1FC8708758", "REPAY", "1067", "张轩", "线下还款"});
        listStringArray.add(new String[]{"15996000028", "TTYQ-232062514840608768", "43CC356C9CF6E9BECF779C8E1DCFBE70", "POSTPONE", "287", "张轩", "线下还款"});
        listStringArray.add(new String[]{"17803883520", "TTYQ-231565761141940224", "392DCBB021C8F9C42A64B2956077DDC8", "POSTPONE", "287", "张轩", "线下还款"});
        listStringArray.add(new String[]{"18234181854", "TTYQ-231580916387028992", "E1B272549CFECC84026F9B94F25C003A", "POSTPONE", "287", "张轩", "线下还款"});
        listStringArray.add(new String[]{"18342151155", "TTYQ-231927982548193280", "5CFD8F7E1DDCB14C7AE7CF1462512C7C", "POSTPONE", "287", "张轩", "线下还款"});
        listStringArray.add(new String[]{"18607888789", "TTYQ-232074428580773888", "92BFF07B74663C12D11E94CBC85D3401", "POSTPONE", "257", "张轩", "线下还款"});
        listStringArray.add(new String[]{"18775899614", "TTYQ-229377872379387904", "74A1891AA9849871C86C25FE6951EFBA", "POSTPONE", "287", "张轩", "线下还款"});
        listStringArray.add(new String[]{"18990973897", "TTYQ-232079069464903680", "64362ADA877F5686B6E918AE6F956E0A", "POSTPONE", "287", "张轩", "线下还款"});
        listStringArray.add(new String[]{"18685970666", "TTYQ-232069293217562624", "27E3321E4C9BB6C358B3D0ED67C32AE5", "POSTPONE", "287", "张轩", "线下还款"});



        List<Map<String, String>> params = new ArrayList<Map<String, String>>();
        for (String[] strings : listStringArray) {
            Map<String, String> map = new HashMap<String, String>();
            map.put("orderId", strings[1]);
            map.put("userCode", strings[2]);
            map.put("type", strings[3]);
            map.put("amount", strings[4]);
            map.put("creator", strings[5]);
            map.put("reliefReason", strings[6]);
            String resultSign = Md5Encrypt.sign(map, "TWlBfbVtgmJb6tlYeWuTl2N26xtKT5SX");
            map.put("sign", resultSign);
            params.add(map);
        }

        //String url = "http://localhost/raptorApi/offline/repay";
        String url = "https://www.mo9.com/raptorApi/offline/repay";

        for (Map<String, String> param : params) {
            try {
                HttpResult httpResult = httpClientApi.doPostJson(url, JSONObject.toJSONString(param));
                logger.info(httpResult.getData());
                //Thread.sleep(30000);
            } catch (Exception e) {
                logger.error("错误 ", e);
            }
        }
    }
}
