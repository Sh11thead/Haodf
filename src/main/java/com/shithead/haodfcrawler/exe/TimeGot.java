package com.shithead.haodfcrawler.exe;

import com.shithead.haodfcrawler.db.SimpleDao;
import com.shithead.haodfcrawler.vo.Doctor;
import com.shithead.haodfcrawler.vo.DoctorDate;
import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Created by kangshulin on 2015/3/17.
 */
public class TimeGot {
    private static Logger logger = LoggerFactory.getLogger(TimeGot.class);

    public static void main(String[] args) throws IOException, WriteException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm");
        //这段注释掉了,已经往数据库里面写入了.
/*        List<Doctor> doctorList = SimpleDao.getInstance().selectDoctorByKind(new Doctor());
        for(int i=0;i<doctorList.size();i++){
            Doctor d = doctorList.get(i);
            logger.info("正在收集第:{}个医生的,该医生的代号:{}",i+1,d.getUname());
            InitData.parseTime(d.getUname());
        }*/
        logger.info("讀取數據中");
        Doctor condition = new Doctor();
        condition.setDisease("糖尿病");
        List<Doctor> tangniao = SimpleDao.getInstance().selectDoctorByKind(condition);
        condition.setDisease("冠心病");
        List<Doctor> guanxin = SimpleDao.getInstance().selectDoctorByKind(condition);
        condition.setDisease("乳腺癌");
        List<Doctor> ruxian = SimpleDao.getInstance().selectDoctorByKind(condition);

        String path = TimeGot.class.getResource("/").getPath();
        logger.info(path);
        WritableWorkbook book  =  Workbook.createWorkbook(new File(path + "/" + "onlyonce" + ".xls"));
        WritableSheet sheettnb = book.createSheet("糖尿病", 0);
        WritableSheet sheetgxb = book.createSheet("冠心病", 1);
        WritableSheet sheetrxa = book.createSheet("乳腺癌", 2);
        initSheet(sheetgxb);
        initSheet(sheettnb);
        initSheet(sheetrxa);

        for(int i=0;i<tangniao.size();i++){
            RenderedLite rl = new RenderedLite();
            rl.setCname(tangniao.get(i).getUname());
            rl.setRealName(tangniao.get(i).getName());
            DoctorDate doctorDate = (DoctorDate) SimpleDao.getInstance().select("selectDoctorDateByUname",tangniao.get(i).getUname());
            if(doctorDate!=null){
                rl.setDate(sdf.format(doctorDate.getDate()));
            }

            insertLine(sheettnb,rl,i+1);
        }

        for(int j=0;j<guanxin.size();j++){
            RenderedLite rl = new RenderedLite();
            rl.setCname(guanxin.get(j).getUname());
            rl.setRealName(guanxin.get(j).getName());
            DoctorDate doctorDate = (DoctorDate) SimpleDao.getInstance().select("selectDoctorDateByUname", guanxin.get(j).getUname());
            if(doctorDate!=null){
            rl.setDate(sdf.format(doctorDate.getDate()));}
            insertLine(sheetgxb,rl,j+1);
        }

        for(int i=0;i<ruxian.size();i++){
            RenderedLite rl = new RenderedLite();
            rl.setCname(ruxian.get(i).getUname());
            rl.setRealName(ruxian.get(i).getName());
            DoctorDate doctorDate = (DoctorDate) SimpleDao.getInstance().select("selectDoctorDateByUname", ruxian.get(i).getUname());
            if(doctorDate!=null) {
                rl.setDate(sdf.format(doctorDate.getDate()));
            }
            insertLine(sheetrxa, rl, i + 1);
        }

        book.write();
        book.close();

    }


    private static void initSheet(WritableSheet sheet) throws WriteException {
        Label label = new Label(0, 0, "域名前缀");
        Label label1 = new Label(1, 0, "姓名");
        Label label2 = new Label(2, 0, "开通时间");
        sheet.addCell(label);sheet.addCell(label1);sheet.addCell(label2);
    }
    @Data
    public static class RenderedLite{
        private String cname;
        private String realName;
        private String Date;
    }
    private static void insertLine(WritableSheet sheet,RenderedLite r,int i) throws WriteException {
        Label label = new Label(0, i, r.getCname());
        Label label1 = new Label(1, i, r.getRealName());
        Label label2 = new Label(2, i, r.getDate());
        sheet.addCell(label);sheet.addCell(label1);sheet.addCell(label2);
    }

}
