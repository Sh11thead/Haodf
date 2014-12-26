package com.shithead.haodfcrawler.thread.task;

import com.shithead.haodfcrawler.util.ClientUtil;
import com.shithead.haodfcrawler.util.RegexUtils;
import lombok.Data;
import org.htmlparser.Node;
import org.htmlparser.NodeFilter;
import org.htmlparser.Parser;
import org.htmlparser.filters.AndFilter;
import org.htmlparser.filters.CssSelectorNodeFilter;
import org.htmlparser.filters.NotFilter;
import org.htmlparser.filters.TagNameFilter;
import org.slf4j.Logger;

import java.util.concurrent.Callable;

/**
 * 患友会的线程类
 * Created by Shulin.kang on 2014/12/26.
 */
@Data
public class HyhTask implements Callable<String> {
    private static final Logger logger = org.slf4j.LoggerFactory.getLogger(HyhTask.class);
    public static String origin;
    private String name;
    private Integer pageNo;

    @Override
    public String call() throws Exception {
        String mainPagenext;
        if(pageNo==1){
            mainPagenext = origin;
        }else{
            mainPagenext = ClientUtil.getUrlContent("http://" + name + ".haodf.com/zixun/list.htm?type=&p=" + pageNo);
        }
        Parser listp = Parser.createParser(mainPagenext,"utf-8");
        NodeFilter filterlist = new CssSelectorNodeFilter("table[width='100%']");
        String listhtml =  listp.parse(filterlist).toHtml();
        listp = Parser.createParser(listhtml,"utf-8");
        filterlist = new CssSelectorNodeFilter("tr[class='zixun_list_title']");
        filterlist = new NotFilter(filterlist);
        filterlist = new AndFilter(new TagNameFilter("tr"),filterlist);
        org.htmlparser.util.NodeList ndlistreal =listp.parse(filterlist);
        Integer allRes=0;
        Integer allQues =0;
        for(int j=0;j<ndlistreal.size();j++){
            Node no =  ndlistreal.elementAt(j);
            String nodehtml = no.toHtml();
            String yihuan = RegexUtils.searchAndFind("<font class=\"green3 pl5 pr5\">(.+?)</font>", nodehtml);
            String[] yihuanarray = yihuan.split("/");
            if(yihuanarray.length==2){
                String yi = yihuanarray[0];
                String huan = yihuanarray[1];
                try{
                    allRes += Integer.valueOf(yi);
                    allQues +=Integer.valueOf(huan);
                }catch(Exception e){
                    logger.info("醫患纍加錯誤!!");
                }
            }
        }
        return  allRes+","+allQues;
    }
}
