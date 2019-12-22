package com.chase.alipay.controller;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.alipay.api.request.AlipayTradeQueryRequest;
import com.alipay.api.response.AlipayTradeQueryResponse;
import com.sun.jndi.toolkit.url.Uri;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;


@Controller
@RequestMapping("/pay")
public class PayController {

    private AlipayClient alipayClient;
    {
        //AlipayClient全局唯一
        alipayClient = new DefaultAlipayClient(
               // "https://openapi.alipay.com/gateway.do",//正式环境支付宝网关
                " https://openapi.alipaydev.com/gateway.do",//支付宝沙箱环境下网关
                "2016092500591832",//沙箱环境下的appid
                //商户应用私钥
                "MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQCsBivxwAUZ+ASanTIybJyV5LpziE84B71NQx36yNlrSuNKPueKCAwgz5t88ChI68rzBZnnp44kqWuO9PqixncIFSj6NlHjAOPlX6lcy+LCiaki6UGCNEbpxNk8b8pGeYcG1QKF8q2zQ1uVj0tKO0FAb/cspTS5NCpugEY618uztVVupOp6+ZUjfxm9y0xgy47qAFD3JjT4YsC0fNoeCem7WfqjXElkWdN9VWyZZNdu0AA0jZDaV1NrY+QrfNGUkLvddTyKi454lmsEPzomI57WzRbT46GiGlfNjOgtplxBX4lVZdzpMCPruZLxVD7pEwqP9TeBwFA6L2wDWxPpsaF/AgMBAAECggEAbG/3g4znqlcNj+lHm0Gb82Ebp3hld4xpaCrG2y+A3oF2hXbhvcEU5Bn6iedZfZ4lcMw3GDAnCjVB9/u1gqZYol+0VQWo2M/W2OBUgoqDht4AIGGpScpqaTINPmu5tnMlCsl7rbQsirQLJ1wMud5gY54w0ZPSiGy50q5Jlw4w+jZrW06p+4zFHNk7bBXAfCBq39OtYVr4z4Klgio47bR01lv9aay0VzC001OE3RyTpFMyO5qQlDWhJg9k23uQqzBIb2Mm2mGHQ7jYyvQMBPO0V3ZJ5JN5aC8Mt/diFXSB/TmIobzlO+okdXhBLYQIGehRp/PnJ5IQtNFf11Nnt1etAQKBgQDgVmf4edE29p1yuqccyvXr/mTxC4gWveOwSo5giKWcqRGooDy/+7p9K7Zq7sIXMRgf7DUtXibR1Nla/pp4JoDC398F8tgh7iZlyLlcc2N6nHdOJwVI8rXk7LK+qHeV/gpMu5iDHq7lLaO5ZraBXkkKtdzHBx5zmgFnPXllU8T1ewKBgQDETZ3hFgk7NzP+OsGOD8fpHZYjHIuDsOg6nvo0baZRXe1g2WsZi7CDtB9aM6Y3aU0NmcP83+TSt8CaXBNzkCnT6UnuCBUsdWri512q+lZLBlDRbp60uKjt+gKmw4GEUaNUOjB0mwRQ/Pcr/y6PjN5aLI1XIA2Vl9aj817QB/LKzQKBgQCWMoOt4rFmYTsNI1bbei+ob0xZ7mSRBVW6JFCq5RIsjy2Zfp7t0rYLNAcQFQaxzFbgmJ8J3u2SszVLLQdyxiSTVJvXOb3nCbF/mCIyebby0ms2Uf3B125c9PS/nb9jFxECSt9TemmSWzow2Un714qDtzs3rdZ/g27JFml+o9VMnQKBgEg/7QDgNGuYCMnkZELeKgzxt8WahvrkTMTuqFZNmS5PbMP9ob6YPFJtJDD125J/bj24P3s+CKFJwljN1Nbu04ZMUcqLLQfc+4ch8sawNryMSYibt0v6vyo/n2QzhuLljUC93gG84FzSjkKiPEfXaCs2RJsxj+5kITrcoyPcGQ9tAoGAY1dlSCwXsGbC39bCWfBNKEi2qULbP/NdZbnZBfRAx7l3QCM0eE05K10ZV1N/COIbU0qX50kIaShiAG+juIIZIJxZ62NoK81XxuDUo0jJjINa2CLDQkgjAAX1bP6wKwWe2u986S4983SZTG/sPZg7Wfx3y0ZmNl3RpZMbYt4rPlM=",
                "json",
                "UTF-8",
                //支付宝公钥
                "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAqf8s0RVCQpdt6aYwYiVQsq9vBS81xBuqdpNoFOcSKk0f+1iDkiDDj5DQdLoKRWARiqQb9hbKyJO0EWlN/XHPjqaFqWCUelJpRLlgYKIUE/8HmHntZ9SNbpiActKu5m+h+7EPVR1e5OX9VbbEXXxzE939ELZiYhMbLzz4d9um5zohC6Heuz3iV8kkYKxQlqY5okatie9uN9sZT0hUQvKDG+E7DKBu9SaBqxyId8/OI2L5hrZzRWD+fdFv9mm3MxcCzzXuPsOtcuq+hO4oYXtMILkMlvtMIfTHjIaRXXDph8FO9WHaRPsGGpdVS6Yi6CACYvAeJUcBkgDEwKLZDbVq8wIDAQAB",
                "RSA2");
    }

    @RequestMapping("/alipay")
    public void pay(String orderid,HttpServletResponse response) throws IOException {
       //获得初始化的AlipayClient
        AlipayTradePagePayRequest alipayRequest = new AlipayTradePagePayRequest();//创建API对应的request
        //支付完成之后需要跳转的地址
        alipayRequest.setReturnUrl("http://www.jd.com");
        alipayRequest.setNotifyUrl("http://verygoodwlk.xicp.net/pay/notifypay");//在公共参数中设置回跳和通知地址
        alipayRequest.setBizContent("{" +
                "    \"out_trade_no\":\""+ orderid+"\"," +
                "    \"product_code\":\"FAST_INSTANT_TRADE_PAY\"," +
                "    \"total_amount\":88.88," +
                "    \"subject\":\"Iphone6 16G\"," +
                "    \"body\":\"Iphone6 16G\"," +
                "    \"passback_params\":\"\"," +
                "    \"extend_params\":{" +
                "    \"sys_service_provider_id\":\"2088511833207846\"" +
                "    }"+
                "  }");//填充业务参数
        System.out.println(alipayRequest);
        String form="";
        try {
            System.out.println(alipayClient);
            form = alipayClient.pageExecute(alipayRequest).getBody(); //调用SDK生成表单
        } catch (AlipayApiException e) {
            e.printStackTrace();
        }
        response.setContentType("text/html;charset=utf-8");
        response.getWriter().write(form);//直接将完整的表单html输出到页面
        response.getWriter().flush();
        response.getWriter().close();

    }

    /**
     * 去支付
     * @return
     */
    @RequestMapping("/topay")
    public String topay(String orderid, Model model){
        model.addAttribute("orderid", orderid);
        
        return "topay";
    }

    /**
     * 主动发送请求判断是否支付成功！！！
     * @return
     */
    @RequestMapping("/ispay")
    public String ispay(String orderid) throws AlipayApiException {

        //通过订单号判断是否支付成功 - 发送请求给支付宝主动确认
        AlipayTradeQueryRequest request = new AlipayTradeQueryRequest();
        request.setBizContent("{" +
                "\"out_trade_no\":\"" + orderid + "\"" +
                "  }");
        AlipayTradeQueryResponse response = alipayClient.execute(request);
        if(response.isSuccess()){
            System.out.println("调用成功");
            if(response.getTradeStatus().equals("TRADE_SUCCESS")){
                //支付成功，修改订单状态
                System.out.println("修改订单状态");

            }
        } else {
            System.out.println("调用失败");
        }
        return "index";
    }

//    public String ss()  {
//        Intent intent = new Intent(Intent.ACTION_VIEW);
//        Uri uri = null;
//        try {
//        uri = Uri.parse("alipays://platformapi/startapp?saId=10000007&qrcode=" + URLEncoder.encode("https://qr.alipay.com/bax04659wociie67ugya204a", "UTF-8”));
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//        }
//        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        intent.setData(uri);
//        startActivity(intent);
//
//        return null;
//    }
}
