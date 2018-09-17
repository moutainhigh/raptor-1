import com.alibaba.fastjson.JSONObject;
import com.mo9.raptor.RaptorApplicationTest;
import com.mo9.raptor.service.BankService;
import com.mo9.raptor.utils.GatewayUtils;
import com.mo9.raptor.utils.httpclient.HttpClientApi;
import com.mo9.raptor.utils.httpclient.bean.HttpResult;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by ycheng on 2018/9/16.
 *
 * @author ycheng
 */
@EnableAspectJAutoProxy
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {RaptorApplicationTest.class}, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class PayOrderControllerTest {

    @Autowired
    private HttpClientApi httpClientApi;

    @Autowired
    private BankService bankService;

    @Autowired
    private GatewayUtils gatewayUtils;

    private static  final String localUrl = "http://192.168.14.114:8010/raptorApi/";

    /**
     * 发送登录短信验证码
     */
    @Test
    public void getRepayChannels() {

        try {
            String url = "cash/get_repay_channels";
            String resJson = httpClientApi.doGet(localUrl+url);
            System.out.println(resJson);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     用户验证码登录*/
    @Test
    public void repay() {

        try {
            String mobile = "13564546025";
            JSONObject json = new JSONObject();
            json.put("channelType", 3);
            json.put("bankCard", "6226090216324281");
            json.put("bankMobile", mobile);
            json.put("userName", "程暘");
            json.put("idCard", "310115199011182510");
            json.put("orderId", "SMALL-WHITE-MOUSE-226026477836177408");
            String url = "cash/repay";

            Map<String , String > headMap = new HashMap<String , String>();
            headMap.put("Account-Code","AA20A480E526D644D13D9AC5593D268E");


            HttpResult resJson = httpClientApi.doPostJson(localUrl+url, json.toJSONString(),headMap);
            System.out.println(resJson.getCode());
            System.out.println(resJson.getData());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
