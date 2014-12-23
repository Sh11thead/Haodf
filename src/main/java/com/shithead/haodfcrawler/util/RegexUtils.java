package com.shithead.haodfcrawler.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by ksl29_000 on 2014/12/23.
 */
public class RegexUtils {
    public static String searchAndFind(String pstr,String content){
       return searchAndFind(pstr,content,1);
    }

    public static String searchAndFind(String pstr,String content,Integer group){
        Pattern pattern = Pattern.compile(pstr);
        Matcher matcher =pattern.matcher(content);
        if(matcher.find()){
            return matcher.group(group).trim();
        }else{
            return null;
        }
    }
}
