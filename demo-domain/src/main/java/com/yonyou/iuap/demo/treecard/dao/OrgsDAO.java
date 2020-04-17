package com.yonyou.iuap.demo.treecard.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;

import com.yonyou.iuap.demo.treecard.po.OrgsPO;
import com.yonyou.iuap.ucf.dao.BaseDAO;

@Repository
@Mapper
public interface OrgsDAO extends BaseDAO<OrgsPO,String> {
    /**
     * @param enable
     * @param innercode
     * @param tenantid
     */
	@Update("update demo_org_orgs set enable =#{enable} where innercode like CONCAT(#{innercode}, '%') and tenant_id=#{tenantid}")
   void updateChildrenStatus(@Param("enable") Integer enable, @Param("innercode") String innercode, @Param("tenantid") String tenantid);
}
