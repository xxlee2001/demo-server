package com.yonyou.iuap.demo.treecard.po;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.yonyou.iuap.baseservice.entity.AuditTrail;
import com.yonyou.iuap.baseservice.entity.annotation.BizLogs;
import com.yonyou.iuap.baseservice.support.tree.entity.TreeBasePO;

import javax.persistence.Column;
import javax.persistence.Table;


/**
 * @Description
 * @Author  zhangxbk
 * @String 2019-08-21
 */
@BizLogs(idField = "id",codeField = "code",nameField = "name",typeCode = "orgs",typeName = "自定义树卡")
@JsonIgnoreProperties(ignoreUnknown = true)
@Table ( name ="demo_org_orgs")
public class OrgsPO extends TreeBasePO<OrgsPO> implements AuditTrail {
	/**
	 * 组织类型，1：组织，2：部门
	 */
	@Column(name = "orgtype" )
	private Integer orgtype;

	/**
	 * 负责人
	 */
	@Column(name = "principal" )
	private String principal;

	/**
	 * 生效时间
	 */
	@Column(name = "effectivedate" )
	private String effectivedate;

	/**
	 * 失效日期
	 */
	@Column(name = "expirationdate" )
	private String expirationdate;

	/**
	 * 修改人
	 */
	@Column(name = "last_modify_user" )
	private String lastModifyUser;

	/**
	 * 修改时间
	 */
	@Column(name = "last_modified" )
	private String lastModified;

	/**
	 * 创建人
	 */
	@Column(name = "create_user" )
	private String createUser;

	/**
	 * 创建时间
	 */
	@Column(name = "create_time" )
	private String createTime;

	/**
	 * 部门性质
	 */
	@Column(name = "depttype" )
	private String depttype;
	
	/**
	 * 所属组织主键
	 */
	@Column(name = "orgid" )
	private String orgid;
	/**
	 * 所属组织父类主键(所属的末级组织)
	 */
	@Column(name = "parentorgid" )
	private String parentorgid;

	/**
	 * 父级部门名称
	 */
	@Column(name = "parent_name" )
	private String parentName;

	public Integer getOrgtype() {
		return orgtype;
	}

	public void setOrgtype(Integer orgtype) {
		this.orgtype = orgtype;
	}

	public String getPrincipal() {
		return principal;
	}

	public void setPrincipal(String principal) {
		this.principal = principal;
	}

	public String getEffectivedate() {
		return effectivedate;
	}

	public void setEffectivedate(String effectivedate) {
		this.effectivedate = effectivedate;
	}

	public String getExpirationdate() {
		return expirationdate;
	}

	public void setExpirationdate(String expirationdate) {
		this.expirationdate = expirationdate;
	}

	public String getParentorgid() {
		return parentorgid;
	}

	public void setParentorgid(String parentorgid) {
		this.parentorgid = parentorgid;
	}

	@Override
	public String getLastModifyUser() {
		return lastModifyUser;
	}

	@Override
	public void setLastModifyUser(String lastModifyUser) {
		this.lastModifyUser = lastModifyUser;
	}

	@Override
	public String getLastModified() {
		return lastModified;
	}

	public void setLastModified(String lastModified) {
		this.lastModified = lastModified;
	}

	@Override
	public String getCreateUser() {
		return createUser;
	}

	@Override
	public void setCreateUser(String createUser) {
		this.createUser = createUser;
	}

	@Override
	public String getCreateTime() {
		return createTime;
	}

	@Override
	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}

	public String getParentName() {
		return parentName;
	}

	public void setParentName(String parentName) {
		this.parentName = parentName;
	}

	public String getDepttype() {
		return depttype;
	}

	public void setDepttype(String depttype) {
		this.depttype = depttype;
	}

	public String getOrgid() {
		return orgid;
	}

	public void setOrgid(String orgid) {
		this.orgid = orgid;
	}
	
	
}