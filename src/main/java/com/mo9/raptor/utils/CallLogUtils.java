package com.mo9.raptor.utils;

import okhttp3.Request;
import okhttp3.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import okhttp3.OkHttpClient;
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

//    public static void main(String[] args) {
//        okHttpClient = new OkHttpClient();
//        String sessId = "5jomophlia5k1l22fcj781b225";
//        String phoneNumber = "18357534013";
//        String url = "http://bmp.dianhua.cn/report/list?cid=317&begin=2018-09-25&end=2018-09-27&status=&status_report=&reportNo=&telNo=" + phoneNumber;
//        String value = getSidByMobile(sessId, phoneNumber, okHttpClient);
////        Document doc = Jsoup.parse(str);
////        String value = doc.select("table.table").select("tbody").select("input.item-checkbox").attr("value");
//        System.out.println("---------> "+value);
//    }

    public String getSidByMobile(String sessId, String phoneNumber, OkHttpClient okHttpClient) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        Calendar calendar = new GregorianCalendar();
        calendar.setTime(new Date());
        String end = sdf.format(calendar.getTime());
        calendar.add(Calendar.DAY_OF_MONTH, -30);
        String begin = sdf.format(calendar.getTime());

        String url = "http://bmp.dianhua.cn/report/list?cid=317&begin=" + begin + "&end=" + end + "&status=&status_report=&reportNo=&telNo=" + phoneNumber;

        String str = doGetHttp(url, sessId, okHttpClient);
//        System.out.println(str);
        Document doc = Jsoup.parse(str);
        String value = doc.select("table.table > tbody")
                .select("tr > td").first().select("span")
                .text();
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

