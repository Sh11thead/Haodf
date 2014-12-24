package com.shithead.haodfcrawler.exe;

import com.shithead.haodfcrawler.db.SimpleDao;
import com.shithead.haodfcrawler.vo.Doctor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Created by Shulin.kang on 2014/12/24.
 */
public class Crawl {
    private static final Logger logger = LoggerFactory.getLogger(Crawl.class);

    public static void main(String[] args){
        String code ="firsttrike";
        List<Doctor> doctorList = SimpleDao.getInstance().selectDoctorByKind(new Doctor());
        for(int i=0;i<doctorList.size();i++){
            Doctor d = doctorList.get(i);
            logger.info("正在收集第:{}个医生的基础信息",i+1);
            InitData.parseBlog(d.getUname(), false, code);
            logger.info("正在收集第:{}个医生的患友会以及订单信息",i+1);
            InitData.parsehuanyouhui(d.getUname(),code);
            logger.info("正在收集第:{}个医生的患者服务区信息",i+1);
            InitData.parseZixun(d.getUname(),code);
        }
    }
}
