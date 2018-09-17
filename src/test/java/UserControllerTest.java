import com.alibaba.fastjson.JSONObject;
import com.mo9.raptor.RaptorApplicationTest;
import com.mo9.raptor.service.BankService;
import com.mo9.raptor.utils.GatewayUtils;
import com.mo9.raptor.utils.Md5Encrypt;
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
import java.util.Random;

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
    @Test
    public void auditStatus() {
        try {
            String url = "user/get_audit_status";
            Map<String, String> header = new HashMap<>();
            header.put("Account-Code","asdfkalsdkff");
            String resJson = httpClientApi.doGetByHeader(localUrl+url, header);
            System.err.println(resJson);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
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



}
