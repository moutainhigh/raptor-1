import com.alibaba.fastjson.JSONObject;
import com.mo9.raptor.RaptorApplicationTest;
import com.mo9.raptor.service.BankService;
import com.mo9.raptor.utils.GatewayUtils;
import com.mo9.raptor.utils.httpclient.HttpClientApi;
import com.mo9.raptor.utils.httpclient.bean.HttpResult;
import org.junit.Before;
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
public class UserControllerTest {

    @Autowired
    private HttpClientApi httpClientApi;

    @Autowired
    private BankService bankService;

    @Autowired
    private GatewayUtils gatewayUtils;

    private static  final String localUrl = "http://192.168.14.114:8010/raptorApi/";

    private static  final String localHostUrl = "http://localhost/raptorApi/";

    Map<String, String> headers = new HashMap<>();

    @Before
    public void before(){
        headers.put("Account-Code", "AA20A480E526D644D13D9AC5593D268E");
        headers.put("Client-Id", "503");
    }

    /**
     * 发送登录短信验证码
     */
    @Test
    public void sendCode() {

        try {
            String mobile = "13564546025";
            JSONObject json = new JSONObject();
            json.put("mobile", mobile);
            String url = "auth/send_login_code";
            HttpResult resJson = httpClientApi.doPostJson(localUrl+url, json.toJSONString());
            System.out.println(resJson.getCode());
            System.out.println(resJson.getData());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     用户验证码登录*/
    @Test
    public void signIn() {

        try {
            String mobile = "13564546025";
            JSONObject json = new JSONObject();
            json.put("mobile", mobile);
            json.put("code", "688204");
            String url = "auth/login_by_code";
            HttpResult resJson = httpClientApi.doPostJson(localUrl+url, json.toJSONString());
            System.out.println(resJson.getCode());
            System.out.println(resJson.getData());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     获取账户审核状态
     */
    /**
     修改账户身份认证信息
     */
    /**
     提交手机通讯录
     */
    /**
     修改账户银行卡信息
     */
    @Test
    public void modifyBankCard() {

        try {
            JSONObject json = new JSONObject();
            json.put("cardName", "程暘");
            json.put("bankName", "招商银行");
            json.put("cardMobile", "13564546025");
            json.put("card", "6226090216324281");
            String url = "auth/modify_bank_card_info";
            HttpResult resJson = httpClientApi.doPostJson(localUrl+url, json.toJSONString());
            System.out.println(resJson.getCode());
            System.out.println(resJson.getData());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     登出
     */
    @Test
    public void testLogout() throws IOException {
        HttpResult httpResult = httpClientApi.doPostJson(localHostUrl + "/user/logout", null, headers);
        System.out.println(httpResult.getCode());
        System.out.println(httpResult.getData());
    }


}
