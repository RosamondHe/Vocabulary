package com.swufestu.vocabulary;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class NetOperator {  //从网络获取数据

    //API
    //iCiBaURL1+要查的单词+iCiBaURL2  就构成了金山查单词的URL
    public final static String iCiBaURL1 = "https://dict-co.iciba.com/api/dictionary.php?w=";
    public final static String iCiBaURL2 = "&key=54A9DE969E911BC5294B70DA8ED5C9C4"; //申请的key

    public static InputStream getInputStreamByUrl(String urlStr) {
        URL url;
        InputStream stream = null; //输入流必须先初始化
        HttpURLConnection connection; //网络请求

        //设置超时时间
        try {
            url = new URL(urlStr);
            connection = (HttpURLConnection) url.openConnection(); //得到的连接对象问题，需要转换强制类型
            connection.setConnectTimeout(8000); //连接主机的超时时间
            connection.setReadTimeout(10000); //从主机读取数据的超时时间
            stream = connection.getInputStream(); //得到输入流
        } catch (Exception e) {
            e.printStackTrace();
        }
        return stream; //返回输入流
    }
}
