package com.yonyou.iuap.demo.treecard.controller;

import com.alibaba.fastjson.JSONObject;
import com.yonyou.iuap.baseservice.support.util.CommonTools;
import com.yonyou.iuap.context.InvocationInfoProxy;
import com.yonyou.iuap.demo.ref.service.RefService;
import com.yonyou.iuap.demo.treecard.exception.TreecardBusinessException;
import com.yonyou.iuap.demo.treecard.po.OrgsPO;
import com.yonyou.iuap.demo.treecard.service.OrgsService;
import com.yonyou.iuap.ucf.common.i18n.MessageUtils;
import com.yonyou.iuap.ucf.common.rest.CommonResponse;
import com.yonyou.iuap.ucf.dao.support.SqlParam;
import com.yonyou.ucf.mdd.common.model.RefDataParam;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 说明：树表结构 基础Controller——提供数据增、删、改、查、导入导出等rest接口
 */
@RestController
@RequestMapping(value = "/treecard")
public class OrgsController {
    private Logger logger = LoggerFactory.getLogger(OrgsController.class);
    @Autowired
    private OrgsService orgsService;
    @Autowired
    private RefService refService;

    /**
     * 单条查询
     *查询部门类的接口添加id为空的容错
     * @return 表的业务实体
     */
    @RequestMapping(value = "/getvo", method = RequestMethod.GET)
    public Object getVo(@RequestParam(required = false, value = "id") Serializable id) {
        if (id==null||"".equals(id)) {
            return CommonResponse.ofSuccess();
        }
        OrgsPO vo = orgsService.findUnique("id", id);
        return CommonResponse.ofSuccess(vo);
    }

    /**
     * 单条保存
     *
     * @param vo
     * @return 表的业务实体
     */
    @RequestMapping(value = "/savevo", method = RequestMethod.POST)

    public Object saveVo(@RequestBody OrgsPO vo) {
        if (orgsService.checkForExist(vo)) {//code唯一性数据校验

            logger.warn(MessageUtils.getMessage("ja.iuap.treecard.0001"));
            return CommonResponse.ofFail(new TreecardBusinessException(CommonTools.buildErrorCode(2L), MessageUtils.getMessage("ja.iuap.treecard.0001")));
        }
        if (StringUtils.isEmpty(vo.getId())) {
            SqlParam searchParams = SqlParam.of();
            searchParams.eq("id", vo.getParentid());
            List<OrgsPO> deptlist = orgsService.queryList(searchParams);
            if (CollectionUtils.isEmpty(deptlist)) {//不存在父节点时
                vo = orgsService.InitOrgVo(vo, null);//初始化部门必要字段
            } else {//存在父节点时
                if (deptlist.size() == 1) {
                	OrgsPO parentNode = deptlist.get(0);
                	if(parentNode.getEnable()==1){
                         vo = orgsService.InitOrgVo(vo, parentNode);//初始化部门必要字段
                         /**新增部门成功后如果父节点是end节点则重置状态*/
                         if (parentNode.getIsEnd() == 1) {
                             parentNode.setIsEnd(0);
                         }
                         orgsService.save(parentNode);
                	}else {
                		return CommonResponse.ofFail(new TreecardBusinessException(CommonTools.buildErrorCode(3L), MessageUtils.getMessage("ja.iuap.treecard.0002")));
					}
                   
                } else {
                    return CommonResponse.ofFail(new TreecardBusinessException(CommonTools.buildErrorCode(3L), MessageUtils.getMessage("ja.iuap.con.0003")));
                }
            }
        }else {
        	 OrgsPO orgsEntity = orgsService.findUnique("id", vo.getId());
            /**如果当前节点有子节点则同时停用子节点 enable:0未启用，1启用，2停用*/
            if (orgsEntity.getIsEnd() == 0 && orgsEntity.getEnable() == 2) {
                orgsService.updateChildrenDeptStatus(2, orgsEntity.getInnercode(), InvocationInfoProxy.getTenantid());
            } else if (orgsEntity.getIsEnd() == 0 && orgsEntity.getEnable() == 1) {
                orgsService.updateChildrenDeptStatus(1, orgsEntity.getInnercode(), InvocationInfoProxy.getTenantid());
            }
		}
        return CommonResponse.ofSuccess(orgsService.save(vo));
    }

    /**
     * 更新停用启用状态，停用的时候，如果停用父节点，需要同时停用子节点
     *
     * @param
     * @return 表的业务实体
     */
    @RequestMapping(value = "/updatestatus", method = RequestMethod.GET)
    public Object updateStatus(String id, Integer enable, Boolean includChild) {
        OrgsPO vo = orgsService.findUnique("id", id);
        vo.setEnable(enable);
        Object result = CommonResponse.ofSuccess(orgsService.save(vo));
        /**如果当前节点有子节点则同时停用子节点 enable:0未启用，1启用，2停用*/
        if (vo.getIsEnd() == 0 && enable == 2) {
            orgsService.updateChildrenDeptStatus(2, vo.getInnercode(), InvocationInfoProxy.getTenantid());
        } else if (vo.getIsEnd() == 0 && enable == 1 && includChild) {
            orgsService.updateChildrenDeptStatus(1, vo.getInnercode(), InvocationInfoProxy.getTenantid());
        }
        return result;
    }

    /**
     * 单条删除
     *
     * @return 删除成功的实体
     */
    @RequestMapping(value = "/delevo", method = RequestMethod.POST)
    public Object deleVo(@RequestBody String... Ids) {
        if (Ids.length == 0) {
            return CommonResponse.ofFail(new TreecardBusinessException(CommonTools.buildErrorCode(3L), MessageUtils.getMessage("ja.iuap.con.0004")));
        }
        int count = 0;
        for (String Id : Ids) {
            count += this.orgsService.delete(Id);
        }
        JSONObject result = new JSONObject();
        result.put("count", count);
        return CommonResponse.ofSuccess(result);
    }

    /**
     * 根据组织id过去组织下部门树
     */
    @RequestMapping(value = "/getdepttree", method = RequestMethod.GET)
    public Object getDeptTree(@RequestParam(required = false, value = "pid") String pid, @RequestParam(required = false, value = "searchValue") String searchValue, @RequestParam(required = false, value = "level") Integer level) {
        JSONObject result = new JSONObject();
        List<OrgsPO> deptList_result = new ArrayList<>();
        SqlParam searchParams = SqlParam.of();
//        增加过滤条件 ，过滤租户
//        searchParams.eq("tenant_id", InvocationInfoProxy.getTenantid());
        if (StringUtils.isNotEmpty(searchValue)) {//树的模糊查询
            deptList_result = this.orgsService.dataSearch(searchValue);
        } else {
            if (level == 0) {//树全加载
                List<OrgsPO> deptlist = orgsService.queryList(searchParams);
                if (!CollectionUtils.isEmpty(deptlist)) {
                    deptList_result = orgsService.convertTreeDataNew(deptlist);
                }
            } else {//树懒加载
                if (StringUtils.isEmpty(pid)) {//默认pid,返回第一层数据
                    searchParams.eq("level", 1);
                } else {
                    searchParams.eq("parentid", pid);
                }
                deptList_result = orgsService.queryList(searchParams);
            }
        }
        result.put("data", deptList_result);
        return CommonResponse.ofSuccess(result);
    }

    /**
     * 参照
     *
     * @return
     */
    @RequestMapping(value = "/orgsRefTree", method = RequestMethod.GET)
    public Object orgsRefTree() {

        return refService.getTreeData(new RefDataParam());


    }
}
