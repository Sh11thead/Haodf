package com.shithead.haodfcrawler.exe;

import com.shithead.haodfcrawler.db.SimpleDao;
import com.shithead.haodfcrawler.thread.ThreadServ;
import com.shithead.haodfcrawler.thread.task.HyhTask;
import com.shithead.haodfcrawler.util.ClientUtil;
import com.shithead.haodfcrawler.util.RegexUtils;
import com.shithead.haodfcrawler.vo.ServiceData;
import org.apache.commons.lang.time.StopWatch;
import org.htmlparser.Node;
import org.htmlparser.NodeFilter;
import org.htmlparser.Parser;
import org.htmlparser.filters.AndFilter;
import org.htmlparser.filters.CssSelectorNodeFilter;
import org.htmlparser.filters.NotFilter;
import org.htmlparser.filters.TagNameFilter;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Shulin.kang on 2014/12/26.
 */
public class Test {
    private static final Logger logger = org.slf4j.LoggerFactory.getLogger(Test.class);
    public static void main(String[] args){
        StopWatch watch = new StopWatch();
        watch.start();
        parseZixun("drshenzhujun", "shit");
        logger.info("{}",watch.getTime());
    }

    public  static void parseZixun(String doctname,String acitoncode){
        ServiceData serviceData = new ServiceData();
        serviceData.setUname(doctname);
        serviceData.setActioncode(acitoncode);

        String mainPage = ClientUtil.getUrlContent("http://" + doctname + ".haodf.com/zixun/list.htm");
        try{
            //解析第一頁的信息先
            Parser p = Parser.createParser(mainPage,"utf-8");
            NodeFilter nodeFilter = new CssSelectorNodeFilter("p[class='fl pt10']");
            org.htmlparser.util.NodeList ndlist = p.parse(nodeFilter);
            if(ndlist!=null && ndlist.size()>0){
                Node doctorNode = ndlist.elementAt(0);
                String html = doctorNode.toHtml();
                String allPatients = RegexUtils.searchAndFind("<span class=\"f14 orange1\">(.+?)</span>", html);
                if(allPatients==null){
                    allPatients = RegexUtils.searchAndFind("class=\"pl5 gray2\">\\((.+?)\\)</span", html);
                }
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
            HyhTask.origin = mainPage;
            List<HyhTask> hyhTaskListli  =new ArrayList<HyhTask>();
            for(int i=1;i<=pageNo;i++){
                HyhTask task = new HyhTask();
                task.setName(doctname);
                task.setPageNo(i);
                hyhTaskListli.add(task);
            }
            List<Integer> resli = ThreadServ.getInstance().runAndgetConsult(hyhTaskListli);
            if(resli!=null&&resli.size()==2){
                allRes = resli.get(0);
                allQues =resli.get(1);
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

}
