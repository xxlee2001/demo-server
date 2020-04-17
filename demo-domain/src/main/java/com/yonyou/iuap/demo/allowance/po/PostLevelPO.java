package com.yonyou.iuap.demo.allowance.po;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.yonyou.iuap.ucf.dao.BasePO;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Table;

/**
 * @Author guoxh
 * @Date 2020/2/17 18:15
 * @Desc
 **/
@JsonIgnoreProperties(ignoreUnknown = true)
@Table(name = "demo_ranks")
@Data
public class PostLevelPO extends BasePO {

    @Column(name = "code")
    private String code;
    @Column(name = "type")
    private String type;
    @Column(name = "type2")
    private String type2;
    @Column(name = "type3")
    private String type3;
    @Column(name = "type4")
    private String type4;
    @Column(name = "type5")
    private String type5;
    @Column(name = "type6")
    private String type6;

    @Column(name = "grade")
    private int grade;
    @Column(name = "name")
    private String name;
    @Column(name = "create_time")
    private String createTime;
    @Column(name = "create_user")
    private String createUser;
    @Column(name = "last_modified")
    private String lastModified;
    @Column(name = "last_modify_user")
    private String lastModifyUser;
}
