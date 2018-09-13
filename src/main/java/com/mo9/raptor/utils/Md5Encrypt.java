/**
 * File: Md5Encrypt.java
 * Description: Md5加密数据
 * Copyright 2010 GamaxPay. All rights reserved
 *  
 */
package com.mo9.raptor.utils;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;



/**
 * 
 * 用Md5进行数据加密 
 * @author Jacky Zhou
 */
public class Md5Encrypt {
	private static final Logger logger = LoggerFactory.getLogger(Md5Encrypt.class);

	private static Md5Encrypt md5 = new Md5Encrypt();
	
	private Md5Encrypt(){		
	}

	public static Md5Encrypt getInstance(){
		return md5;
	}
	
	/**
	 * 对所有商户的参数进行加密
	 * @param params 所有将加密的参数
	 * @param privateKey 需要对参数加密的私钥
	 * @return    加密以后得到的字符串对象
	 */
	public static String sign(Map<String, String> params, String privateKey){	
		List<String> keys = new ArrayList<String>(params.keySet());
		Collections.sort(keys);
		StringBuffer content = new StringBuffer();
		for (int i = 0; i < keys.size(); i++) {
			String key = (String) keys.get(i);
			String value = "";
//			try {
//				value = URLDecoder.decode((String) params.get(key), "UTF-8");
//			} catch (UnsupportedEncodingException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
			value = (String) params.get(key);
			if (key == null || key.equalsIgnoreCase("sign")
					|| key.equalsIgnoreCase("sign_type")){
				continue;
			}
			content.append(key + "=" + value + "&");
		}
		String linkedContent = content.toString().substring(0, content.lastIndexOf("&"));
		String signcontent = linkedContent + privateKey;		
		return encrypt(signcontent);
		
	}
	
	/**
	 * 对商户传进来的金币选择参数进行签名 
	 * <p>字符串拼接顺序: business + notify_url +  payer_id + merchant_fields + coinsPackage + privateKey</p>
	 * @param business 商户邮箱
	 * @param notify_url  商户Notify_Url 
	 * @param payer_id 玩家在商户系统中的注册id
	 * @param merchant_fields 商户宽展参数
	 * @param coinsPackage 商户传进来的金币数组 : {农民币,欧元,Gamax币}
	 * @param privateKey 商户私钥
	 * @return 签名后的字符串
	 */
	public static String sellerCoinsChoiceSign(String business, String notify_url, String payer_id, String  merchant_fields, String coinsPackage ,String privateKey) {	
		StringBuffer buffer = new StringBuffer();
		buffer.append(business);
		buffer.append(notify_url);
		buffer.append(payer_id);
		buffer.append(merchant_fields);
		buffer.append(coinsPackage);
		buffer.append(privateKey);
		return encrypt(buffer.toString());
	}
	
	/**
	 * 对商户传进来的金币选择参数进行签名 
	 * <p>字符串拼接顺序: business + notify_url +  payer_id + merchant_fields + coinsPackage + privateKey</p>
	 * @param business 商户邮箱
	 * @param notify_url  商户Notify_Url 
	 * @param payer_id 玩家在商户系统中的注册id
	 * @param merchant_fields 商户宽展参数
	 * @param coinsPackage 商户传进来的金币数组 : {农民币,欧元,Gamax币}
	 * @param privateKey 商户私钥
	 * @return 签名后的字符串
	 */
	public static String sellerCoinsPackageSign(String business, String notify_url, String payer_id, String  merchant_fields, String coinsPackage , String currency_code, String privateKey) {	
		StringBuffer buffer = new StringBuffer();
		buffer.append(business);
		buffer.append(notify_url);
		buffer.append(payer_id);
		buffer.append(merchant_fields);
		buffer.append(coinsPackage);
		buffer.append(currency_code);
		buffer.append(privateKey);
		return encrypt(buffer.toString());
	}
	
	/**
	 * 对商户传进来的参数进行签名
	 * <p>字符串拼接顺序: merchantEmail + invoiceNo + amount + privateKey</p>
	 * @param invoiceNo 商户系统中的唯一订单号
	 * @param merchantEmail 商户在GamaxPay中注册的邮箱号
	 * @param amount 此次交易的金额
	 * @param privateKey 商户的私钥
	 * @return 签名后的字符串
	 */
	public static String sellerRequestParamsSign(String invoiceNo, String merchantEmail, String amount, String privateKey){	
		StringBuffer buffer = new StringBuffer();
		buffer.append(merchantEmail);
		buffer.append(invoiceNo);
		buffer.append(amount);
		buffer.append(privateKey);
		return encrypt(buffer.toString());
	}
	
	/**
	 * 对GamaxPay处理完后回传给商户的参数进行签名
	 * <p>字符串拼接顺序:merchantEmail + txNumber + amount + timestamp + privateKey</p>
	 * @param merchantEmail  商户在GamaxPay中注册的邮箱号
	 * @param amount 此次交易的金额
	 * @param privateKey 商户的私钥
	 * @param txNumber 在GamaxPay中生成的唯一交易号
	 * @param timestamp 在GamaxPay中交易完成时生成的时间戳
	 * @return 签名后的字符串
	 */
	public static String gamaxReturnParamsSign(String merchantEmail, String amount, String txNumber, String timestamp, String privateKey){	
		StringBuffer buffer = new StringBuffer();
		buffer.append(merchantEmail);
		buffer.append(txNumber);
		buffer.append(amount);
		buffer.append(timestamp);
		buffer.append(privateKey);
		return encrypt(buffer.toString());
	}
	
	/**
	 * 
	 * @param paytoemail 商家的email
	 * @param payerId    玩家在商家系统中唯一的id
	 * @param amount     交易的金额
	 * @param currency   交易的货币类型
	 * @param tradeNo    交易的订单号
	 * @param key        商家的签名key
	 * @return           MD5签名
	 */
	public static String getSign(String paytoemail, String payerId, String amount, String currency, String tradeNo, String key){	
		StringBuffer buffer = new StringBuffer();
		buffer.append(paytoemail);
		buffer.append(payerId);
		buffer.append(amount);
		buffer.append(currency);
		buffer.append(tradeNo);
		buffer.append(key);
		return encrypt(buffer.toString());		
	};

	
	
	
	
	/**
	 * 对传入的字符串数据进行MD5加密
	 * @param source	字符串数据
	 * @return   加密以后的数据
	 */
	public static String encrypt(String source) {
		MessageDigest md = null;		
		byte[] bt = null;
		try {
			bt = source.getBytes("UTF-8");
			md = MessageDigest.getInstance("MD5");
			md.update(bt);
			return BytesHexTransform.bytesToHexString(md.digest()); 
		} catch (NoSuchAlgorithmException e) {
			logger.error("非法摘要算法", e);
			throw new RuntimeException(e);	
		}catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return null;
	}
	
	/**
	 * 对传入的字符串数据进行MD5加密
	 * @param source	字符串数据
	 * @return   加密以后的数据
	 */
	public static String encryptGBK(String source) {
		MessageDigest md = null;		
		byte[] bt = null;
		try {
			bt = source.getBytes("GBK");
			md = MessageDigest.getInstance("MD5");
			md.update(bt);
			return BytesHexTransform.bytesToHexString(md.digest()); 
		} catch (NoSuchAlgorithmException e) {
			logger.error("非法摘要算法", e);
			throw new RuntimeException(e);	
		}catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return null;
	}
	
	public static void main(String[] args) throws Exception {
	    Map<String,String> params = new HashMap<String,String>();
        params.put("m", "newPay");
        params.put("channel", "yilianpay");
        params.put("subchannel", "wap");
        params.put("amount", "0.10");
        params.put("mobile", "19999999999");
        params.put("remark", "callbackUrl:http://www.baidu.com");
        params.put("extral", "6225768710265339|曹辉|511322198609296756|");
        params.put("bankmobile", "15618550929");
        params.put("return_url", "http://www.baidu.com");
        String mysig = Md5Encrypt.sign(params, "643138394F10DA5E9647709A3FA8DD7F");
        params.put("sign", mysig);
        StringBuffer url = new StringBuffer("http://eric.local.mo9.com/gateway/pay.shtml?");
        for(Map.Entry<String, String> entry:params.entrySet())
        {
            url.append("&"+entry.getKey()+"="+entry.getValue());
        }
        System.out.println(url);

	}
}
