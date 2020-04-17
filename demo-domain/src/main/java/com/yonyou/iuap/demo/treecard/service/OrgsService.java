package com.yonyou.iuap.demo.treecard.service;

import static com.yonyou.iuap.baseservice.intg.support.ServiceFeature.AUDIT;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.yonyou.iuap.baseservice.intg.service.GenericUcfService;
import com.yonyou.iuap.baseservice.intg.support.ServiceFeature;
import com.yonyou.iuap.context.InvocationInfoProxy;
import com.yonyou.iuap.demo.treecard.dao.OrgsDAO;
import com.yonyou.iuap.demo.treecard.po.OrgsPO;
import com.yonyou.iuap.demo.treecard.util.UUIDGenerateUtil;
import com.yonyou.iuap.ucf.dao.support.SqlParam;

/**
 * 基础CRUD及主子表联合操作服务,分离insert和update 以便RPC权限控制
 */
@Service
public class OrgsService extends GenericUcfService<OrgsPO,String> {

	private static final String NAME="name";
	private static final String INNERCODE="innercode";
	
	
	@Autowired
	private OrgsDAO orgsDAO;
	
	@Autowired
	public void setOrgsDAO(OrgsDAO orgsDAO) {
		this.orgsDAO = orgsDAO;
		super.setGenericMapper(orgsDAO);
	}

	/**
	 * 更新子部门状态
	 * @param enable
	 * @param innercode
	 * @param tenantId
	 */
	public void updateChildrenDeptStatus (Integer enable,String innercode,String tenantId){
		 orgsDAO.updateChildrenStatus(enable,innercode,tenantId);
	}

	/**
	 * 初始化部门必要字段
	 * @param vo
	 * @param parentNode
	 * @return
	 */
	public OrgsPO InitOrgVo(OrgsPO vo, OrgsPO parentNode){
		/*innercode唯一性验证*/
		String innerCode = InnerCodeValidation(parentNode);
		if(parentNode==null){
			//组织下的一级部门,数据初始化,父id为primary_parentid
			vo.setInnercode(innerCode);
        	vo.setLevel(1);
    		vo.setParentid("");
		}else{
			/**innercode 规则为四位一级，本层级为上一层级的innercode+随机4位UUID*/
			vo.setInnercode(innerCode);
			/**属于第几个层级*/
			vo.setLevel(innerCode.length() / 4);
			/**新增部门默认是end节点*/
		}
		vo.setIsEnd(1);
		vo.setTenantId(InvocationInfoProxy.getTenantid());
		return vo;
	}

	/**
	 * @param vo
	 * 新增或者编辑时组织部门编码code重复性验证
	 * @return
	 */
	public Boolean checkForExist(OrgsPO vo) {
		SqlParam searchParams = SqlParam.of();
		searchParams.eq("code", vo.getCode());
		List<OrgsPO> deptlist = queryList(searchParams);
		return deptlist.size() > 1 || (deptlist.size() == 1 && !deptlist.get(0).getId().equals(vo.getId()));
	}

	/**
     * @CAU 可插拔设计
     * @return 向父类 GenericIntegrateService 提供可插拔的特性声明
     */
    @Override
    protected ServiceFeature[] getFeats() {
        return new ServiceFeature[] { AUDIT, ServiceFeature.BIZLOGS};
    }
    
    public List<OrgsPO> dataSearch(String searchValue) {
		SqlParam searchParams = SqlParam.of();
		searchParams.like(NAME, searchValue);
		List<OrgsPO> rtnVal = super.queryList(searchParams);
		if (rtnVal != null && !rtnVal.isEmpty()){
			SqlParam sqlParam = SqlParam.of();
			sqlParam.in(INNERCODE, this.getParentInnerCode(rtnVal));
			rtnVal = super.queryList(sqlParam);
			return this.convertTreeDataNew(rtnVal);
		}
		return new ArrayList<>();
	}
    
    /**
	 * 构造树
	 * @param nodes
	 * @return
	 * TODO 封装并提供
	 */
    public List<OrgsPO> convertTreeDataNew(List<OrgsPO> nodes) {
		List<OrgsPO> Orgss = new ArrayList<>();
		Map<String, OrgsPO> idMap = new HashMap<>(nodes.size()*4/3+1);
		Map<String, List<OrgsPO>> pidMap = new HashMap<>(nodes.size()*4/3+1);
		//构建基础数据
		for (OrgsPO Orgs : nodes) {
			idMap.put(Orgs.getId(), Orgs);
			List<OrgsPO> list = pidMap.get(Orgs.getParentid());
			if (list==null) {
				list=new ArrayList<>();
				pidMap.put(Orgs.getParentid(), list);
			}
			list.add(Orgs);
		}
		//组装树
		Set<Entry<String, List<OrgsPO>>> entrySet = pidMap.entrySet();
		for (Entry<String, List<OrgsPO>> entry : entrySet) {
			String pid = entry.getKey();
			OrgsPO Orgs = idMap.get(pid);
			if (Orgs!=null) {
				Orgs.setChildren(entry.getValue());
			}
		}
		for (int i = 0; i < nodes.size(); i++) {
			if("".equals(nodes.get(i).getParentid())){
				Orgss.add(nodes.get(i));
			}
		}
		return Orgss;
	}
	/**
	 * 根据模糊查询的结果 获取所有相关的分级innerCode	
	 * @param Orgss
	 * @return
	 */
	private List<String> getParentInnerCode(List<OrgsPO> Orgss) {
		Set<String> set = new HashSet<>();
		// code每4位表示一级
		int level = 4;
		for (OrgsPO Orgs : Orgss){
			String innercode = Orgs.getInnercode();
			for (int i = 1;i <= innercode.length() / level;i ++){
				String parentCode = innercode.substring(0, i * level);
				set.add(parentCode);
			}
		}
		List<String> codeList = new ArrayList<String>();
		for (String code : set){
			codeList.add(code);
		}
		return codeList;
	}
	
	private String InnerCodeValidation(OrgsPO parentNode) {
		String generateShortUuid = UUIDGenerateUtil.generateShortUuid();
		String innercode;
		if(parentNode==null){
			innercode = generateShortUuid;
		}else {
			innercode = parentNode.getInnercode()+generateShortUuid;
		}
		SqlParam searchParams = SqlParam.of().eq("innercode",innercode);
		List<OrgsPO> list = orgsDAO.list(searchParams);
		if(CollectionUtils.isNotEmpty(list)){
			return this.InnerCodeValidation(parentNode);
		}
		return innercode;
		
	}
	
}
