package com.shithead.haodfcrawler.exe;

import com.shithead.haodfcrawler.db.SimpleDao;
import com.shithead.haodfcrawler.util.CityUtils;
import com.shithead.haodfcrawler.util.ClientUtil;
import com.shithead.haodfcrawler.util.HtmlUtils;
import com.shithead.haodfcrawler.util.RegexUtils;
import com.shithead.haodfcrawler.vo.*;
import org.htmlparser.Node;
import org.htmlparser.NodeFilter;
import org.htmlparser.Parser;
import org.htmlparser.filters.AndFilter;
import org.htmlparser.filters.CssSelectorNodeFilter;
import org.htmlparser.filters.NotFilter;
import org.htmlparser.filters.TagNameFilter;
import org.htmlparser.util.ParserException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Created by Shulin.kang on 2014/12/15.
 */
public class InitData {
    private static final Logger logger = LoggerFactory.getLogger(InitData.class);
    public  static void main(String[] args) throws InterruptedException {
   //     parseBlog(ClientUtil.getUrlContent("http://drxiao.haodf.com/"));
        //      parsePayment(ClientUtil.getUrlContent("http://liwenhui.haodf.com/payment/newintro"));
        //      parseZixun("liwenhui");
        //       parsehuanyouhui("liwenhui");
/*        initData("糖尿病","http://www.haodf.com/jibing/tangniaobing/daifu.htm");
        initData("冠心病","http://www.haodf.com/jibing/guanxinbing/daifu.htm");
        initData("乳腺癌","http://www.haodf.com/jibing/ruxianai/daifu.htm");
        List<Doctor> doctorList = SimpleDao.getInstance().selectDoctorByKind(new Doctor());
        for(int i=0;i<doctorList.size();i++){
            Doctor d = doctorList.get(i);
            logger.info("正在分析第:{}个医生的基础信息",i+1);
            parseBlog(d.getUname(),true,"nonsese");
        }*/
    }
    public static void initData(String desease,String firstvisitwebsite) throws InterruptedException {
        String mainPage = ClientUtil.getUrlContent(firstvisitwebsite);
        //构造解析器
        Parser p = Parser.createParser(mainPage,"utf-8");
        // 取得页数
        String pstr = "font class=\"black pl5 pr5\">(.+?)</font>";
        String pagenumstr  = RegexUtils.searchAndFind(pstr, mainPage);
        Integer pageNum = 0;
        if(pagenumstr!=null){
            pageNum = Integer.parseInt(pagenumstr);
        }
        for(int i=0;i<pageNum;i++){
            if(i==0){
                parseAndSave(mainPage,desease);
            }else{
                logger.info("begin to parse info of page {}",i+1);
                String findwebsite = RegexUtils.searchAndFind("^(.+?).htm",firstvisitwebsite);
                parseAndSave(ClientUtil.getUrlContent(findwebsite+"_" + (i + 1) + ".htm"),desease);
            }
            Thread.sleep(1000);
        }
    }

    public static void analyzeCity(String hospitalname,String url){
        String mainPage =  ClientUtil.getUrlContent(url);
        HospitalInfo hospitalInfo =new HospitalInfo();
        hospitalInfo.setHospitalName(hospitalname);
        try{
            Parser p = Parser.createParser(mainPage,"utf-8");
            NodeFilter nodeFilter = new CssSelectorNodeFilter("div[class='panelA_blue']");
            String html = p.parse(nodeFilter).toHtml();
            p = Parser.createParser(html,"utf-8");
            nodeFilter = new CssSelectorNodeFilter("li[class='item']");
            html = p.parse(nodeFilter).toHtml();
            String level = RegexUtils.searchAndFind("</a>\\((.+?)\\)</p>",html);
            String address =  RegexUtils.searchAndFind("地　　址：</nobr>(.+?)</td>",mainPage);
            logger.info("level:{}",level);
            hospitalInfo.setLevel(level);
            hospitalInfo.setAddress(address);
            String city =null;
            city = CityUtils.guessCity(hospitalname);
            if(city==null){
                city = CityUtils.guessCity(address);
            }
            logger.info("city:{}",city);
            hospitalInfo.setCity(city);
            SimpleDao.getInstance().insert("insertHospitalinfo",hospitalInfo);
        }catch (Exception e ){
            e.printStackTrace();
        }
    }



    public static  void parsehuanyouhui(String name,String actioncode){
        PaymentData paymentData = new PaymentData();
        paymentData.setUname(name);
        paymentData.setActioncode(actioncode);

        String mainPage = ClientUtil.getUrlContent("http://"+name+".haodf.com/huanyouhui/index.html");
        try{
            Parser p = Parser.createParser(mainPage,"utf-8");
            NodeFilter nodeFilter = new CssSelectorNodeFilter("div[class='mt20']");
            String html = p.parse(nodeFilter).toHtml();
            String groupnum = RegexUtils.searchAndFind("font class=\"orange1 ml5 mr5\">(.+?)</font>个",html);
            logger.info("groupnum:{}",groupnum);
            paymentData.setGroupnum(groupnum);
            p.reset();
            nodeFilter = new CssSelectorNodeFilter("ul[class='clearfix fs pa_friend_list']");
            org.htmlparser.util.NodeList ndlist = p.parse(nodeFilter);
            Integer peoplenum = 0;
            Integer topicnum = 0;
            if(ndlist.size()==1){
                Node n = ndlist.elementAt(0);
                org.htmlparser.util.NodeList childrens = n.getChildren();
                for(int i=0;i<childrens.size();i++){
                    //logger.info(childrens.elementAt(i).toHtml());
                    String innerhtml = childrens.elementAt(i).toHtml();
                    String penum = RegexUtils.searchAndFind("成员<span class=\"ml5 mr5 orange1\">(.+?)</span>",innerhtml);
                    String tnum =  RegexUtils.searchAndFind("话题<span class=\"ml5 mr5 organge1\">(.+?)</span>",innerhtml);
                    try{peoplenum+=Integer.parseInt(penum);
                        topicnum+=Integer.parseInt(tnum);
                    }catch (Exception e){}
                }
            }
            logger.info("peoplenum:{},topicnum:{}",peoplenum,topicnum);
            if(peoplenum!=null){
                paymentData.setPeoplenum(peoplenum.toString());
            }
            if(topicnum!=null){
                paymentData.setTopicnum(topicnum.toString());
            }
            paymentData.setPayments(parsePayment(name));
            SimpleDao.getInstance().insert("insertPaymentData",paymentData);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    private static String parsePayment(String uname){
        String mainPage = ClientUtil.getUrlContent("http://"+uname+".haodf.com/payment/newintro");
        //构造解析器
        try{
            Parser p = Parser.createParser(mainPage,"utf-8");
            NodeFilter nodeFilter = new CssSelectorNodeFilter("ul[class='tel-service-tab clearfix fb']");
            org.htmlparser.util.NodeList ndlist = p.parse(nodeFilter);
            if(ndlist!=null && ndlist.size()>0){
                Node doctorNode = ndlist.elementAt(0);
                String html = doctorNode.toHtml();
                String pays = RegexUtils.searchAndFind("最新订单\\((.+?)\\)",html);
                logger.info("payments:{}",pays);
                return pays;
            }

        }catch (Exception e){e.printStackTrace();}
        return null;
    }

    public  static void parseZixun(String doctname,String acitoncode){
        ServiceData serviceData = new ServiceData();
        serviceData.setUname(doctname);
        serviceData.setActioncode(acitoncode);

        String mainPage = ClientUtil.getUrlContent("http://"+doctname+".haodf.com/zixun/list.htm");
        try{
            //解析第一頁的信息先
            Parser p = Parser.createParser(mainPage,"utf-8");
            NodeFilter nodeFilter = new CssSelectorNodeFilter("p[class='fl pt10']");
            org.htmlparser.util.NodeList ndlist = p.parse(nodeFilter);
            if(ndlist!=null && ndlist.size()>0){
                Node doctorNode = ndlist.elementAt(0);
                String html = doctorNode.toHtml();
                String allPatients = RegexUtils.searchAndFind("<span class=\"f14 orange1\">(.+?)</span>",html);
                logger.info("allPatients:{}",allPatients);
                serviceData.setAllpatients(allPatients);
            }
            Integer pageNo = 1;
            String pageNos = RegexUtils.searchAndFind("共&nbsp;(.+?)&nbsp;页",mainPage);
            try {
                if(pageNos==null||"".equals(pageNos)){
                    pageNo =1;
                }
                else{
                    pageNo = Integer.parseInt(pageNos);
                }
            }catch (Exception e){
                logger.error("error pageNo:{}",pageNos);
            }
            logger.info("pageNo:{}",pageNo);
/*            //限制爲10頁
            if(pageNo>0) pageNo =10;*/
            Integer allQues = 0;
            Integer allRes = 0;
            for(int i=1;i<=pageNo;i++){
                String mainPagenext;
                if(i==1){
                    mainPagenext = mainPage;
                }else{
                    mainPagenext = ClientUtil.getUrlContent("http://"+doctname+".haodf.com/zixun/list.htm?type=&p="+pageNo);
                }
                Parser listp = Parser.createParser(mainPage,"utf-8");
                NodeFilter filterlist = new CssSelectorNodeFilter("table[width='100%']");
                String listhtml =  listp.parse(filterlist).toHtml();
                listp = Parser.createParser(listhtml,"utf-8");
                filterlist = new CssSelectorNodeFilter("tr[class='zixun_list_title']");
                filterlist = new NotFilter(filterlist);
                filterlist = new AndFilter(new TagNameFilter("tr"),filterlist);
                org.htmlparser.util.NodeList ndlistreal =listp.parse(filterlist);
                logger.debug("size:{}", ndlistreal.size());
                for(int j=0;j<ndlistreal.size();j++){
                    logger.debug("j:{},size:{}", j, ndlistreal.size());
                    Node no =  ndlistreal.elementAt(j);
                    String nodehtml = no.toHtml();
                    String yihuan = RegexUtils.searchAndFind("<font class=\"green3 pl5 pr5\">(.+?)</font>",nodehtml);
                    logger.debug("醫患:{}", yihuan);
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
            }
            logger.info("allQues:{},allRespon:{}",allQues,allRes);
            if(allQues!=null)serviceData.setAllques(allQues.toString());
            if(allRes!=null)serviceData.setAllres(allRes.toString());
            SimpleDao.getInstance().insert("insertServiceData",serviceData);
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }



    public static void parseBlog(String dname,boolean isFirstrun,String actioncode){
        DoctorInfo doctorInfo = new DoctorInfo();
        doctorInfo.setUname(dname);
        DoctorData doctorData = new DoctorData();
        doctorData.setUname(dname);
        doctorData.setActioncode(actioncode);
        //begin
        String mainPage = ClientUtil.getUrlContent("http://"+dname+".haodf.com");
        //构造解析器
        Parser p = Parser.createParser(mainPage, "utf-8");
        NodeFilter liteFilter;
        org.htmlparser.util.NodeList ndlist;
        //先抓個人統計信息
        try{
            if(isFirstrun==false){
                NodeFilter selfCalculateFilter = new CssSelectorNodeFilter("p[class='f18 mr_title']");
                ndlist = p.parse(selfCalculateFilter);
                if(ndlist!=null && ndlist.size()>0){
                    Node doctorNode = ndlist.elementAt(0);
                    String html = doctorNode.toHtml();
                    String totalView = "li>总 访 问：<span class=\"orange1 pr5\">(.+?)</span>";
                    String thanks = "<li>感 谢 信：<span class=\"orange1 pr5\">(.+?)</span>";
                    String warminggifts = "<li>心意礼物：<span class=\"orange1 pr5\">(.+?)</span>";
                    String votecount = "<li>患者投票：<span class=\"orange1 pr5\">(.+?)</span>票</li>";
                    String totalArticle = "总 文 章：<span class=\"orange1 pr5\">(.+?)</span>篇";
                    String to = RegexUtils.searchAndFind(totalView, mainPage);
                    to= to.replace(",","");
                    logger.info("总 访 问：{}", to);
                    doctorData.setTotalview(to);
                    String th = RegexUtils.searchAndFind(thanks, mainPage);
                    logger.info("感 谢 信：{}",th);
                    doctorData.setThanks(th);
                    String wa = RegexUtils.searchAndFind(warminggifts, mainPage);
                    logger.info("心意礼物：{}",wa);
                    doctorData.setWarminggifts(wa);
                    String vo = RegexUtils.searchAndFind(votecount, mainPage);
                    logger.info("患者投票：{}",vo);
                    doctorData.setVotecount(vo);
                    String ar = RegexUtils.searchAndFind(totalArticle, mainPage);
                    logger.info("总 文 章：{}",ar);
                    doctorData.setTotalarticle(ar);
                }
                //解析愛心值貢獻值
                p.reset();
                liteFilter = new CssSelectorNodeFilter("ul[class='doc_info_ul1']");
                ndlist = p.parse(liteFilter);
                if(ndlist!=null && ndlist.size()>0){
                    Node doctorNode = ndlist.elementAt(0);
                    String html = doctorNode.toHtml();
                    String lovingheart = "爱心值:(.+?)分";
                    String contribute = "href=\"http://www.haodf.com/info/scoreinfo_doctor.php\" >(.+?)</a>";
                    String lo = RegexUtils.searchAndFind(lovingheart, mainPage);
                    logger.info("爱心值{}",lo);
                    doctorData.setLovingheart(lo);
                    String co = RegexUtils.searchAndFind(contribute, mainPage);
                    logger.info("医生贡献值{}",co);
                    doctorData.setContribute(co);
                    SimpleDao.getInstance().insert("insertDoctorData",doctorData);
                }
            }
            //男女？
            if(isFirstrun==true){
                p.reset();
                liteFilter = new CssSelectorNodeFilter("div[class='fs pr15 pb10 pl15']");
                ndlist = p.parse(liteFilter);
                if(ndlist!=null && ndlist.size()>0){
                    Node doctorNode = ndlist.elementAt(0);
                    String html = doctorNode.toHtml();
                    if(html.contains("男")){
                        logger.info("sex:男");
                        doctorInfo.setSex("男");
                    }
                    if(html.contains("女")){
                        logger.info("sex:女");
                        doctorInfo.setSex("女");
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
                            doctorInfo.setName(name);
                            String titleAndEdu = nameAndtitleAndEducation.substring(firstindex+12);
                            if(titleAndEdu.contains(" ")){
                                String[] strarray = titleAndEdu.split(" ");
                                String title = strarray[0];
                                logger.info("title:{}",title);
                                doctorInfo.setTitle(title);
                                if(strarray.length>1){
                                    String edu = strarray[1];
                                    logger.info("edu:{}",edu);
                                    doctorInfo.setEdu(edu);
                                }
                            }
                            else {
                                logger.info("title:{}",titleAndEdu);
                                doctorInfo.setTitle(titleAndEdu);
                            }
                        }

                    }else {
                        logger.info("name:{}",nameAndtitleAndEducation);
                        doctorInfo.setName(nameAndtitleAndEducation);
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
                    doctorInfo.setHospitalName(hosname);
                    doctorInfo.setHospitalUrl(hoswebsite);
                    SimpleDao.getInstance().insert("insertDoctorInfo",doctorInfo);
                    analyzeCity(hosname, hoswebsite);
                }
            }

        }catch (Exception e){
            e.printStackTrace();
        }

    }
    private static void parseAndSave(String mainPage,String desease){
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
                doctor.setDisease(desease);
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
