package com.mo9.raptor.utils;

import com.mo9.raptor.utils.log.Log;
import okhttp3.Request;
import okhttp3.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import okhttp3.OkHttpClient;
import org.slf4j.Logger;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;


/**
 * @author wtwei .
 * @date 2018/9/27 .
 * @time 15:40 .
 */
public class CallLogUtils {
    private static OkHttpClient okHttpClient;
    
    Logger logger = Log.get();

//    public static void main(String[] args) {
//        CallLogUtils utils = new CallLogUtils();
//        
//        okHttpClient = new OkHttpClient();
//        String sessId = "ovv9q2ebg61c4a379g6gic1p51";
//        String phoneNumber = "18616297200";
//        String value = utils.getSidByMobile(sessId, phoneNumber, okHttpClient);
//        System.out.println("---------> " + value);
//    }

    public String getSidByMobile(String sessId, String phoneNumber, OkHttpClient okHttpClient) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        Calendar calendar = new GregorianCalendar();
        calendar.setTime(new Date());
        String end = sdf.format(calendar.getTime());
        calendar.add(Calendar.DAY_OF_MONTH, -30);
        String begin = sdf.format(calendar.getTime());

        String url = "http://bmp.dianhua.cn/report/list?cid=317&begin=" + begin + "&end=" + end + "&status=&status_report=&reportNo=&telNo=" + phoneNumber;

        String value = null ;
        try {
            String str = doGetHttp(url, sessId, okHttpClient);
            System.out.println(str);
            Document doc = Jsoup.parse(str);
            value = doc.select("table.table > tbody")
                    .select("tr > td").first().select("span")
                    .text();
        } catch (Exception e) {
            logger.error("sid爬虫失败", e);
        }
        return value;
    }


    private String doGetHttp(String url, String sessId, OkHttpClient okHttpClient) {
        String str = "";
        try {
            Response response = okHttpClient.newCall(new Request.Builder()
                    .header("Cookie", "SESSID=" + sessId)
                    .url(url).get().build()).execute();
            str = response.body().string();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return str;
    }
}

