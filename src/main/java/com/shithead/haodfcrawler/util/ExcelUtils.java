package com.shithead.haodfcrawler.util;

import com.shithead.haodfcrawler.exe.Excel;
import com.shithead.haodfcrawler.exe.Excel.Rendered;
import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Created by Shulin.kang on 2014/12/26.List
 */
public class ExcelUtils {
    private static Logger logger = LoggerFactory.getLogger(ExcelUtils.class);
    public static void main(String[] args) throws IOException, WriteException {
    }

    public static String  generateFile(String actionCode,List<Rendered> li1,List<Rendered> li2,List<Rendered> li3 ) throws IOException, WriteException {
        actionCode = actionCode.replace("/","");
        String path = ExcelUtils.class.getResource("/").getPath();
        logger.info(path);
        WritableWorkbook book  =  Workbook.createWorkbook(new File(path+"/"+actionCode + ".xls"));
        WritableSheet sheettnb = book.createSheet("糖尿病", 0);
        WritableSheet sheetgxb = book.createSheet("冠心病", 1);
        WritableSheet sheetrxa = book.createSheet("乳腺癌", 2);
        initSheet(sheettnb);
        initSheet(sheetgxb);
        initSheet(sheetrxa);
        for(int i=0;i<li1.size();i++){
            Rendered r = li1.get(i);
            insertLine(sheettnb,r,i+1);
        }
        for(int j=0;j<li2.size();j++){
            Rendered r = li2.get(j);
            insertLine(sheetgxb,r,j+1);
        }
        for(int k=0;k<li3.size();k++){
            Rendered r = li3.get(k);
            insertLine(sheetrxa,r,k+1);
        }
        book.write();
        book.close();
        return path+"/"+actionCode + ".xls";
    }
    private static void insertLine(WritableSheet sheet,Rendered r,int i) throws WriteException {
        Label label = new Label(0, i, r.getUname());
        Label label1 = new Label(1, i, r.getName());
        Label label2 = new Label(2, i, r.getSex());
        Label label3 = new Label(3, i, r.getTitle());
        Label label4 = new Label(4, i, r.getEdu());
        Label label5 = new Label(5, i, r.getHospital());
        Label label6 = new Label(6, i, r.getLevel());
        Label label7 = new Label(7, i, r.getCity());
        Label label8 = new Label(8, i, r.getTotalview());
        Label label9 = new Label(9, i, r.getThanks());
        Label label10 = new Label(10, i, r.getGifts());
        Label label11= new Label(11, i, r.getVotes());
        Label label12 = new Label(12, i, r.getArticles());
        Label label13 = new Label(13, i, r.getLovings());
        Label label14 = new Label(14, i, r.getContributes());
        Label label15 = new Label(15, i, r.getPayments());
        Label label16 = new Label(16, i, r.getGroupnum());
        Label label17 = new Label(17, i, r.getGrouppeoplenum());
        Label label18 = new Label(18, i, r.getGroupTopics());
        Label label19 = new Label(19, i, r.getServicetopics());
        Label label20 = new Label(20, i, r.getAnswers());
        Label label21= new Label(21, i, r.getAsks());
        sheet.addCell(label);sheet.addCell(label1);sheet.addCell(label2);sheet.addCell(label3);
        sheet.addCell(label4);sheet.addCell(label5);sheet.addCell(label6);sheet.addCell(label7);
        sheet.addCell(label8);sheet.addCell(label9);sheet.addCell(label10);sheet.addCell(label11);
        sheet.addCell(label12);sheet.addCell(label13);sheet.addCell(label14);sheet.addCell(label15);
        sheet.addCell(label16);sheet.addCell(label17);sheet.addCell(label18);sheet.addCell(label19);
        sheet.addCell(label20);sheet.addCell(label21);
    }


    private static void initSheet(WritableSheet sheet) throws WriteException {
        Label label = new Label(0, 0, "域名前缀");
        Label label1 = new Label(1, 0, "姓名");
        Label label2 = new Label(2, 0, "性别");
        Label label3 = new Label(3, 0, "头衔");
        Label label4 = new Label(4, 0, "任职");
        Label label5 = new Label(5, 0, "医院名称");
        Label label6 = new Label(6, 0, "医院等级");
        Label label7 = new Label(7, 0, "医院城市");
        Label label8 = new Label(8, 0, "总访问量");
        Label label9 = new Label(9, 0, "感谢数");
        Label label10 = new Label(10, 0, "礼物数");
        Label label11= new Label(11, 0, "患者投票");
        Label label12 = new Label(12, 0, "总文章");
        Label label13 = new Label(13, 0, "爱心");
        Label label14 = new Label(14, 0, "贡献值");
        Label label15 = new Label(15, 0, "总订单");
        Label label16 = new Label(16, 0, "小组个数");
        Label label17 = new Label(17, 0, "小组总人数");
        Label label18 = new Label(18, 0, "小组总话题数");
        Label label19 = new Label(19, 0, "患者服务区话题数");
        Label label20 = new Label(20, 0, "总回答数量");
        Label label21= new Label(21, 0, "总提问数量");
        sheet.addCell(label);sheet.addCell(label1);sheet.addCell(label2);sheet.addCell(label3);
        sheet.addCell(label4);sheet.addCell(label5);sheet.addCell(label6);sheet.addCell(label7);
        sheet.addCell(label8);sheet.addCell(label9);sheet.addCell(label10);sheet.addCell(label11);
        sheet.addCell(label12);sheet.addCell(label13);sheet.addCell(label14);sheet.addCell(label15);
        sheet.addCell(label16);sheet.addCell(label17);sheet.addCell(label18);sheet.addCell(label19);
        sheet.addCell(label20);sheet.addCell(label21);
    }
}
