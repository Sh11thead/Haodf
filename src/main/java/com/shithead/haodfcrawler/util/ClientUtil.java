package com.shithead.haodfcrawler.util;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.GzipDecompressingEntity;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Shulin.kang on 2014/12/16.
 */
public class ClientUtil {
    private static CloseableHttpClient client = HttpClientBuilder.create().build();

    public static byte[] getBinaryByUrl(String url){
        try{
            CloseableHttpResponse res = client.execute(new HttpGet(url));
            HttpEntity entity = res.getEntity();
            byte[] ret = null;
            if(res.getEntity()!=null && res.getEntity().getContentEncoding()!=null && res.getEntity().getContentEncoding().getValue().toLowerCase().contains("gzip")){
                ret = EntityUtils.toByteArray(new GzipDecompressingEntity(entity));
            }else{
                ret =  EntityUtils.toByteArray(entity);
            }
            res.close();
            return ret;

        }catch (IOException e){
            return null;
        }

    }

    public static String getUrlContentForceGzipDepress(String url)  {
        try{
            CloseableHttpResponse res = client.execute(new HttpGet(url));
            HttpEntity entity = res.getEntity();
            String ret = null;
            ret = EntityUtils.toString(new GzipDecompressingEntity(entity));
            res.close();
            return ret;

        }catch (IOException e){
            return null;
        }
    }

    public static String getUrlContent(String url)  {
        try{
            CloseableHttpResponse res = client.execute(new HttpGet(url));
            HttpEntity entity = res.getEntity();
            String ret = null;
            if(res.getEntity()!=null && res.getEntity().getContentEncoding()!=null && res.getEntity().getContentEncoding().getValue().toLowerCase().contains("gzip")){
                ret = EntityUtils.toString(new GzipDecompressingEntity(entity));
            }else{
                ret =  EntityUtils.toString(entity);
            }
            res.close();
            return ret;

        }catch (IOException e){
            return null;
        }
        }

    public static String postUrlWithParameters(String url,Map<String,String> param)  {
        try{
        List<NameValuePair> nvplist = new ArrayList<NameValuePair>();
        for(String key:param.keySet()){
            nvplist.add(new BasicNameValuePair(key,param.get(key)));
        }
        HttpPost hp = new HttpPost(url);
        hp.setEntity(new UrlEncodedFormEntity(nvplist));
        CloseableHttpResponse res = client.execute(hp);
        HttpEntity entity = res.getEntity();
        String ret =  EntityUtils.toString(entity);
        res.close();
        return ret;

    }catch (IOException e){
        return null;
    }
    }

}
