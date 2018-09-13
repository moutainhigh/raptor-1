import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.mo9.raptor.RaptorApplicationTest;
import com.mo9.raptor.utils.Md5Encrypt;
import com.mo9.raptor.utils.httpclient.HttpClientApi;
import com.mo9.raptor.utils.httpclient.bean.HttpResult;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * Created by xtgu on 2018/9/13.
 * @author xtgu
 */
@EnableAspectJAutoProxy
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {RaptorApplicationTest.class}, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class GuTest {

    @Autowired
    private HttpClientApi httpClientApi ;

    /**
     * 放款
     */
    @Test
    public void loan (){

        int dd = 1 ;
        for (int i = 1; i <= dd; i++) {
            try {
                String notifyUrl = "https://riskclone.mo9.com/riskportal/limit/order/paymentCallBack.a";
                //	String url = "http://localhost/gateway/proxypay/pay.mhtml";
                //String url = "https://new.mo9.com/gateway/proxypay/pay.mhtml";
                String url =  "http://localhost:8081/gateway/proxypay/pay.mhtml";
                //String url = "http://guxt.local.mo9.com/gateway/proxypay/queryOrderStatus.mhtml";
                String key = "werocxofsdjnfksdf892349729lkfnnmgn/x,.zx=9=-MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBAJGLeWVIS3wo0U2h8lzWjiq5RJJDi14hzsbxxwedhqje123";
                Map<String, String> payParams = new HashMap<String, String>();
                payParams.put("bizSys", "RAPTOR");
                Random random = new Random();
                payParams.put("invoice",  "990354"+ random.nextInt(9)+ random.nextInt(9)+ random.nextInt(9)+random.nextInt(9)+ random.nextInt(9)+ random.nextInt(9));
                payParams.put("notifyUrl", notifyUrl);
                payParams.put("cardNo","6228482938103729839");
                payParams.put("usrName", "李伟");
                payParams.put("idCard", "411221199312062149");
                payParams.put("mobile", "13560084836");
                payParams.put("openBank", "建设银行");
                payParams.put("prov", "未知");
                payParams.put("city", "未知");
                payParams.put("subBank", "建设银行");
                payParams.put("transAmt", "0.01");
                payParams.put("attach", "1490685960032");
                JSONObject jsonParams = new JSONObject();
                jsonParams.put("loan_term", "14");
                jsonParams.put("property", "男");
                payParams.put("purpose", "FAST放款");
                payParams.put("extraParameter", jsonParams.toJSONString());
                String sign = Md5Encrypt.sign(payParams, key);
                payParams.put("sign", sign);
                //HttpResult resJson = httpClientApi.doPost(url, payParams);
                HttpResult resJson1 = httpClientApi.doPostJson(url, JSON.toJSONString(payParams));
                System.err.println(resJson1);
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

    }

}
