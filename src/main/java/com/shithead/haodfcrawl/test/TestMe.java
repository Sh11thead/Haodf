package com.shithead.haodfcrawl.test;

import com.shithead.haodfcrawler.db.SimpleDao;
import com.shithead.haodfcrawler.util.ClientUtil;
import com.shithead.haodfcrawler.util.HtmlUtils;
import com.shithead.haodfcrawler.vo.Doctor;
import junit.framework.Test;
import org.apache.commons.io.FileUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.htmlparser.Node;
import org.htmlparser.NodeFilter;
import org.htmlparser.Parser;
import org.htmlparser.Tag;
import org.htmlparser.filters.AndFilter;
import org.htmlparser.filters.CssSelectorNodeFilter;
import org.htmlparser.filters.TagNameFilter;
import org.htmlparser.nodes.TagNode;
import org.htmlparser.tags.Div;
import org.htmlparser.util.ParserException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.NodeList;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Shulin.kang on 2014/12/15.
 */
public class TestMe {
    private static final Logger logger = LoggerFactory.getLogger(TestMe.class);
    public  static void main(String[] args) throws InterruptedException {
        String mainPage = ClientUtil.getUrlContent("http://www.haodf.com/jibing/yigan/daifu.htm");
        //构造解析器
        Parser p = Parser.createParser(mainPage,"utf-8");
        // 取得页数
        String pstr = "font class=\"black pl5 pr5\">(.+?)</font>";
        String pagenumstr  = searchAndFind(pstr,mainPage);
        Integer pageNum = 0;
        if(pagenumstr!=null){
            pageNum = Integer.parseInt(pagenumstr);
        }
        for(int i=0;i<pageNum;i++){
            if(i==0){
                praseAndSave(mainPage);
            }else{
                praseAndSave(ClientUtil.getUrlContent("http://www.haodf.com/jibing/yigan/daifu_"+(i+1)+".htm"));
            }
            Thread.sleep(3000);
        }

    }
    private static String searchAndFind(String pstr,String content){
        Pattern pattern = Pattern.compile(pstr);
        Matcher matcher =pattern.matcher(content);
        if(matcher.find()){
            return matcher.group(1).trim();
        }else{
            return null;
        }
    }

    private static void praseAndSave(String mainPage){
        //构造解析器
        Parser p = Parser.createParser(mainPage,"utf-8");
        NodeFilter doctorsFilter = new CssSelectorNodeFilter("li[class='hp_doc_box']");
        try{
            org.htmlparser.util.NodeList ndlist = p.parse(doctorsFilter);
            for(int i=0;i<ndlist.size();i++){
                Node doctorNode = ndlist.elementAt(i);
                String searchHtml = doctorNode.toHtml();
                //姓名
                String pstrname = "class=\"blue_a3\">(.+?)</a>";
                //头衔
                String pstrtitle = "<span class=\"ml15\">(.+?)</span>";
                //医院
                String pstrhos = "</span></p>([\\s\\S]*?)<br>";
                //好评数目
                String pstrgood="患者好评<span class=\"text_u\">(.+?)</span>票";
                //回复数目
                String pstrreply="近两周回复<span class=\"text_u\">(.+?)</span>问";
                //经验数目
                String pstrexp="患者看病经验<span class=\"text_u\">(.+?)</span>条</a>";
                //website
                String pstrwebsite = "href=\"(.+?)\" class=\"blue_a3\"";
                //blog
                String pstrblog = "href=\"(.+?)\" class=\"home_btn\">个人网站";

                Doctor doctor = new Doctor();
                doctor.setName(searchAndFind(pstrname, searchHtml));
                doctor.setHospital(HtmlUtils.toText(searchAndFind(pstrhos, searchHtml)));
                doctor.setTitle(searchAndFind(pstrtitle, searchHtml));
                doctor.setGoodCount(searchAndFind(pstrgood, searchHtml));
                doctor.setReplyCount(searchAndFind(pstrreply, searchHtml));
                doctor.setPatientExpCount(searchAndFind(pstrexp,searchHtml));
                doctor.setWebsite(searchAndFind(pstrwebsite,searchHtml));
                doctor.setBlog(searchAndFind(pstrblog,searchHtml));
                logger.info(doctor.toString());
            }
        }catch (ParserException e) {
            e.printStackTrace();
        }

    }


}
