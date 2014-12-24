package com.shithead.haodfcrawler.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * Created by Shulin.kang on 2014/12/15.
 */
@Data
public class Doctor implements Serializable
{
    private Integer id;
    private String name;
    private String uname;
    private String website;
    private String goodCount;
    private String replyCount;
    private String patientExpCount;
    private String title;
    private String hospital;
    private String blog;
    private String disease;
}
