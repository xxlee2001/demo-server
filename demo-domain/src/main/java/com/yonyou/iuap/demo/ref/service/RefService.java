package com.yonyou.iuap.demo.ref.service;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Maps;
import com.yonyou.cloud.utils.StringUtils;
import com.yonyou.iuap.baseservice.support.tree.service.TreeService;
import com.yonyou.iuap.context.InvocationInfoProxy;
import com.yonyou.iuap.demo.allowance.po.PostLevelPO;
import com.yonyou.iuap.demo.allowance.service.PostLevelService;
import com.yonyou.iuap.demo.treecard.po.OrgsPO;
import com.yonyou.iuap.demo.treecard.service.OrgsService;
import com.yonyou.iuap.ucf.dao.support.SqlParam;
import com.yonyou.iuap.ucf.dao.support.UcfPageRequest;
import com.yonyou.ucf.mdd.common.model.Pager;
import com.yonyou.ucf.mdd.common.model.RefDataParam;
import com.yonyou.ucf.mdd.ref.api.IReferDataApi;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author haojh@yonyou.com
 * @date 2019年9月3日---下午4:38:36
 * @problem
 * @answer
 * @action
 */
@Service
public class RefService implements IReferDataApi {

    @Autowired
    private OrgsService orgsService;

    @Autowired
    private TreeService treeService;

    @Autowired
    private PostLevelService postLevelService;

    /**
     * 表型参照的数据查询接口实现
     *
     * @param refDataParam
     * @return
     */
    @Override
    public Pager getGridData(RefDataParam refDataParam) {
        String key = refDataParam.getLikeValue();
        String refCode = refDataParam.getRefEntity().code;
        SqlParam searchParams = SqlParam.of();
        if (StringUtils.isNotEmpty(key)) {
            searchParams.and(SqlParam.of().like("name", key).or().like("code", key));
        }
//        增加过滤条件 ，过滤租户
//        searchParams.and(SqlParam.of().eq("tenant_id",InvocationInfoProxy.getTenantid()));
        List<Map<String, Object>> list = new ArrayList<>();
        int pageIndex = refDataParam.getPage().getPageIndex() - 1;
        int pageSize = refDataParam.getPage().getPageSize();
        long totalElemets = 0;
        if ("cn_post_level".equals(refCode)) {//岗位职级
            Page<PostLevelPO> postLevels = postLevelService.selectAllByPage(UcfPageRequest.of(pageIndex, pageSize), searchParams);
            totalElemets = postLevels.getTotalElements();
            if (postLevels.hasContent()) {
                for (PostLevelPO postLevelPO : postLevels.getContent()) {
                    Map map = Maps.newHashMap();
                    map.put("id", postLevelPO.getId());
                    map.put("code", postLevelPO.getCode());
                    map.put("name", postLevelPO.getName());
                    list.add(map);
                }
            }
        }

        Pager pager = new Pager(refDataParam.getPage().getPageIndex(), refDataParam.getPage().getPageSize(), (int) totalElemets, list);
        return pager;
    }

    /**
     * 树形参照的数据查询实现
     *
     * @param refDataParam
     * @return
     */
    @Override
    public List<? extends Map<String, Object>> getTreeData(RefDataParam refDataParam) {
        String key = refDataParam.getLikeValue();
        String refCode = refDataParam.getRefEntity().code;
        SqlParam searchParams = SqlParam.of();
        List<Map<String, Object>> list = new ArrayList<>();
        if (StringUtils.isNotEmpty(key)) {
            searchParams.and(SqlParam.of().like("name", key).or().like("code", key));
        }
        if ("cn_demo_dept".equals(refCode)) {//部门
            searchParams.and(SqlParam.of().eq("orgtype", 2).and().eq("enable", "1"));
            List<OrgsPO> orgList = orgsService.queryList(searchParams);
            if (!CollectionUtils.isEmpty(orgList)) {
                Map<String, List<OrgsPO>> orgMap = treeService.convertTree(orgList);
                for (OrgsPO o : orgMap.get("")) {
                    Map<String, Object> mapOrgs = JSON.parseObject(JSON.toJSONString(o));
                    list.add(mapOrgs);
                }
            }
        }


        return list;
    }

}
