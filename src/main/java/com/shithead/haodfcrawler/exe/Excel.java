package com.shithead.haodfcrawler.exe;

import com.shithead.haodfcrawler.db.SimpleDao;
import com.shithead.haodfcrawler.util.ExcelUtils;
import com.shithead.haodfcrawler.util.MailUtil;
import com.shithead.haodfcrawler.vo.*;
import jxl.Workbook;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Shulin.kang on 2014/12/26.
 */
public class Excel {
    private  static Logger logger = LoggerFactory.getLogger(Excel.class);
    public static void main(String[] args) throws IOException, WriteException {
        if(args.length!=1){
            logger.info("please call like Excel {actioncode}");
            return;
        }
        andSend(args[0]);

    }
    public static void andSend(String actioncode) throws IOException, WriteException {
        actioncode.replace("/","");
        String fileName = new Excel().generateFile(actioncode);
        MailUtil mu =new MailUtil("smtp.qq.com","kslksl@qq.com","VPS","kslksl","loveyoung","youngl66@163.com",actioncode,actioncode);
        mu.addAttachfile(fileName);
        mu.send();
    }

    public String generateFile(String actioncode) throws IOException, WriteException {
        logger.info("讀取數據中");
        Doctor condition = new Doctor();
        condition.setDisease("糖尿病");
        List<Doctor> tangniao = SimpleDao.getInstance().selectDoctorByKind(condition);
        condition.setDisease("冠心病");
        List<Doctor> guanxin = SimpleDao.getInstance().selectDoctorByKind(condition);
        condition.setDisease("乳腺癌");
        List<Doctor> ruxian = SimpleDao.getInstance().selectDoctorByKind(condition);
        logger.info("準備生成糖尿病的醫生報表,size:{},收集數據中...",tangniao.size());
        List<Rendered> tangniaoRender = collect(tangniao,actioncode);
        logger.info("準備生成冠心病的醫生報表,size:{},收集數據中...", guanxin.size());
        List<Rendered> guanxinRender = collect(guanxin,actioncode);
        logger.info("準備生成乳腺癌的醫生報表,size:{},收集數據中...",ruxian.size());
        List<Rendered> ruxianRender = collect(ruxian,actioncode);
        return ExcelUtils.generateFile(actioncode,tangniaoRender,guanxinRender,ruxianRender);
    }

    public List<Rendered> collect(List<Doctor> docli,String actioncode){
        List<Rendered> ret = new ArrayList<Rendered>();
        for(Doctor d:docli){
            Rendered re = new Rendered();
            re.setUname(d.getUname());
            //獲取基礎信息
            Map<String,String> conditionMap = new HashMap<String, String>();
            conditionMap.put("uname",d.getUname());
            conditionMap.put("actioncode",actioncode);
            DoctorInfo di =(DoctorInfo) SimpleDao.getInstance().select("selectDoctorInfo",d.getUname());
            if(di!=null){
                if (di.getHospitalName()!=null){
                    re.setHospital(di.getHospitalName());
                    HospitalInfo hi=(HospitalInfo) SimpleDao.getInstance().select("selectHospitalInfo",di.getHospitalName());
                    if(hi!=null){
                        re.setLevel(hi.getLevel());
                        re.setCity(hi.getCity());
                    }else {
                        logger.warn("{}沒有Hospital詳細信息",d.getUname());
                    }
                }
                re.setName(di.getName());
                re.setSex(di.getSex());
                re.setTitle(di.getTitle());
                re.setEdu(di.getEdu());
            }
            //DoctorData
            DoctorData dd = (DoctorData) SimpleDao.getInstance().select("selectDoctorData",conditionMap);
            if(dd!=null){
               re.setTotalview(dd.getTotalview());
                re.setThanks(dd.getThanks());
                re.setGifts(dd.getWarminggifts());
                re.setVotes(dd.getVotecount());
                re.setArticles(dd.getTotalarticle());
                re.setLovings(dd.getLovingheart());
                re.setContributes(dd.getContribute());
            }
            //PaymentData
            PaymentData pd = (PaymentData) SimpleDao.getInstance().select("selectPaymentData",conditionMap);
            if(pd!=null){
                re.setPayments(pd.getPayments());
                re.setGroupnum(pd.getGroupnum());
                re.setGrouppeoplenum(pd.getPeoplenum());
                re.setGroupTopics(pd.getTopicnum());
            }
            //Servicedata
            ServiceData sd= (ServiceData)SimpleDao.getInstance().select("selectServiceData",conditionMap);
            if(sd!=null)
            {
                re.setServicetopics(sd.getAllpatients());
                re.setAsks(sd.getAllques());
                re.setAnswers(sd.getAllres());
            }
            ret.add(re);
        }
        return ret;
    }
    @Data
    public static class Rendered{
        private String uname;
        private String name;
        private String sex;
        private String title;
        private String edu;
        private String hospital;
        private String level;
        private String city;
        private String totalview;
        private String thanks;
        private String gifts;
        private String votes;
        private String articles;
        private String lovings;
        private String contributes;
        private String payments;
        private String groupnum;
        private String grouppeoplenum;
        private String groupTopics;
        private String servicetopics;
        private String asks;
        private String answers;
    }
}
