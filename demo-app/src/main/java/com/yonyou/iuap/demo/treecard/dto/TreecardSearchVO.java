package com.yonyou.iuap.demo.treecard.dto;

import com.yonyou.iuap.baseservice.support.entity.vo.BaseSearchVO;

/**
*@author haojh@yonyou.com
*@date  2019年8月16日---下午4:23:01
*@problem
*@answer
*@action
*/
public class TreecardSearchVO extends BaseSearchVO {

    private String code;
    private String name;
    /**
     * 简称
     */
    private String shortname;
    /**
     * orgtype= 1 的是 组织 orgtype =2 的 是 部门
     */
    private Integer orgtype;
    private String innercode;

    /**
     * 所属组织主键
     */
    private String parentid;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getShortname() {
        return shortname;
    }

    public void setShortname(String shortname) {
        this.shortname = shortname;
    }

    public Integer getOrgtype() {
        return orgtype;
    }

    public void setOrgtype(Integer orgtype) {
        this.orgtype = orgtype;
    }

    public String getParentid() {
        return parentid;
    }

    public void setParentid(String parentid) {
        this.parentid = parentid;
    }

    public String getInnercode() {
        return innercode;
    }

    public void setInnercode(String innercode) {
        this.innercode = innercode;
    }
}
