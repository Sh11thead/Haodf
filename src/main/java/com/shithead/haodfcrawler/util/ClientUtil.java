package com.shithead.haodfcrawler.util;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

import java.io.IOException;

/**
 * Created by Shulin.kang on 2014/12/16.
 */
public class ClientUtil {
    private static CloseableHttpClient client = HttpClientBuilder.create().build();

    public static String getUrlContent(String url)  {
        try{
            CloseableHttpResponse res = client.execute(new HttpGet(url));
            HttpEntity entity = res.getEntity();
            String ret =  EntityUtils.toString(entity);
            res.close();
            return ret;

        }catch (IOException e){
            return null;
        }
    }

}
