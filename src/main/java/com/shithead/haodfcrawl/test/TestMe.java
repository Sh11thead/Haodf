package com.shithead.haodfcrawl.test;

import com.shithead.haodfcrawler.db.SimpleDao;
import com.shithead.haodfcrawler.util.ClientUtil;
import com.shithead.haodfcrawler.util.HtmlUtils;
import com.shithead.haodfcrawler.util.RegexUtils;
import com.shithead.haodfcrawler.vo.Doctor;
import org.htmlparser.Node;
import org.htmlparser.NodeFilter;
import org.htmlparser.Parser;
import org.htmlparser.filters.CssSelectorNodeFilter;
import org.htmlparser.util.ParserException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Shulin.kang on 2014/12/15.
 */
public class TestMe {
    private static final Logger logger = LoggerFactory.getLogger(TestMe.class);
    public  static void main(String[] args) throws InterruptedException {
/*        String mainPage = ClientUtil.getUrlContent("http://www.haodf.com/jibing/yigan/daifu.htm");
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
                parseAndSave(mainPage);
            }else{
                logger.info("begin to parse info of page {}",i+1);
                parseAndSave(ClientUtil.getUrlContent("http://www.haodf.com/jibing/yigan/daifu_" + (i + 1) + ".htm"));
            }
            Thread.sleep(3000);
        }*/

        parseBlog(ClientUtil.getUrlContent("http://drxiao.haodf.com/"));

    }


    private static void parseBlog(String mainPage){
        //构造解析器
        Parser p = Parser.createParser(mainPage,"utf-8");
        //先抓個人統計信息
        try{
            NodeFilter selfCalculateFilter = new CssSelectorNodeFilter("p[class='f18 mr_title']");
            org.htmlparser.util.NodeList ndlist = p.parse(selfCalculateFilter);
            if(ndlist!=null && ndlist.size()>0){
                Node doctorNode = ndlist.elementAt(0);
                String html = doctorNode.toHtml();
                String totalView = "li>总 访 问：<span class=\"orange1 pr5\">(.+?)</span>";
                String thanks = "<li>感 谢 信：<span class=\"orange1 pr5\">(.+?)</span>";
                String warminggifts = "<li>心意礼物：<span class=\"orange1 pr5\">(.+?)</span>";
                String votecount = "<li>患者投票：<span class=\"orange1 pr5\">(.+?)</span>票</li>";
                String totalArticle = "总 文 章：<span class=\"orange1 pr5\">(.+?)</span>篇";
                logger.info("总 访 问：{}", RegexUtils.searchAndFind(totalView, mainPage));
                logger.info("感 谢 信：{}",RegexUtils.searchAndFind(thanks, mainPage));
                logger.info("心意礼物：{}",RegexUtils.searchAndFind(warminggifts, mainPage));
                logger.info("患者投票：{}",RegexUtils.searchAndFind(votecount, mainPage));
                logger.info("总 文 章：{}",RegexUtils.searchAndFind(totalArticle, mainPage));
            }
            //解析愛心值貢獻值
            p.reset();
            NodeFilter liteFilter = new CssSelectorNodeFilter("ul[class='doc_info_ul1']");
            ndlist = p.parse(liteFilter);
            if(ndlist!=null && ndlist.size()>0){
                Node doctorNode = ndlist.elementAt(0);
                String html = doctorNode.toHtml();
                String lovingheart = "爱心值:(.+?)分";
                String contribute = "href=\"http://www.haodf.com/info/scoreinfo_doctor.php\" >(.+?)</a>";
                logger.info("爱心值{}",RegexUtils.searchAndFind(lovingheart, mainPage));
                logger.info("医生贡献值{}",RegexUtils.searchAndFind(contribute, mainPage));
            }
            //男女？
            p.reset();
            liteFilter = new CssSelectorNodeFilter("div[class='fs pr15 pb10 pl15']");
            ndlist = p.parse(liteFilter);
            if(ndlist!=null && ndlist.size()>0){
                Node doctorNode = ndlist.elementAt(0);
                String html = doctorNode.toHtml();
                if(html.contains("男")){
                    logger.info("sex:男");
                }
                if(html.contains("女")){
                    logger.info("sex:女");
                }
            }

            //医生教育水平
            p.reset();
            liteFilter = new CssSelectorNodeFilter("h3[class='doc_name f22 fl']");
            ndlist = p.parse(liteFilter);
            if(ndlist!=null && ndlist.size()>0){
                String nameAndtitleAndEducation = ndlist.elementAt(0).toPlainTextString();
                if(nameAndtitleAndEducation.contains("&nbsp;&nbsp;")){
                    int firstindex = nameAndtitleAndEducation.indexOf("&nbsp;&nbsp;");
                    if(firstindex!=-1){
                        String name = nameAndtitleAndEducation.substring(0,firstindex);
                        logger.info("name:{}",name);
                        String titleAndEdu = nameAndtitleAndEducation.substring(firstindex+12);
                        if(titleAndEdu.contains(" ")){
                            String[] strarray = titleAndEdu.split(" ");
                            String title = strarray[0];
                            logger.info("title:{}",title);
                            if(strarray.length>1){
                                String edu = strarray[1];
                                logger.info("edu:{}",edu);
                            }
                        }
                        else {
                            logger.info("title:{}",titleAndEdu);
                        }
                    }

                }else {
                    logger.info("name:{}",nameAndtitleAndEducation);
                }
            }
            //医院名称以及医院详情地址
            p.reset();
            liteFilter = new CssSelectorNodeFilter("div[class='doc_hospital']");
            ndlist = p.parse(liteFilter);
            if(ndlist!=null && ndlist.size()>0){
                String html = ndlist.elementAt(0).toHtml();
                String hosname = RegexUtils.searchAndFind("<p><a href=\"(.*?)\" target=\"_blank\">(.*?)</a>",html,2);
                String hoswebsite = RegexUtils.searchAndFind("<p><a href=\"(.*?)\" target=\"_blank\">(.*?)</a>",html,1);
                logger.info("hosname:{},hoswebsite:{}",hosname,hoswebsite);
            }

        }catch (Exception e){
            e.printStackTrace();
        }

    }
    private static void parseAndSave(String mainPage){
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
                String pstrhos = "</span></p>([\\s\\S]+?)<br>";
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
                doctor.setName(RegexUtils.searchAndFind(pstrname, searchHtml));
                doctor.setHospital(HtmlUtils.toText(RegexUtils.searchAndFind(pstrhos, searchHtml)));
                doctor.setTitle(RegexUtils.searchAndFind(pstrtitle, searchHtml));
                doctor.setGoodCount(RegexUtils.searchAndFind(pstrgood, searchHtml));
                doctor.setReplyCount(RegexUtils.searchAndFind(pstrreply, searchHtml));
                doctor.setPatientExpCount(RegexUtils.searchAndFind(pstrexp, searchHtml));
                doctor.setWebsite(RegexUtils.searchAndFind(pstrwebsite, searchHtml));
                String blog = RegexUtils.searchAndFind(pstrblog, searchHtml);
                doctor.setBlog(blog);
                if(blog!=null && !blog.equals("")){
                   doctor.setUname(RegexUtils.searchAndFind("http://(.+?).haodf.com/", blog));
                }
                logger.info(doctor.toString());
                SimpleDao.getInstance().insert("insertDoctor",doctor);
            }
        }catch (ParserException e) {
            e.printStackTrace();
        }

    }


}
