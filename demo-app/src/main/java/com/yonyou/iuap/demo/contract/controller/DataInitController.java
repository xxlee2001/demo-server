package com.yonyou.iuap.demo.contract.controller;


import com.fasterxml.jackson.databind.node.ObjectNode;
import com.yonyou.iuap.context.InvocationInfoProxy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import yonyou.bpm.rest.BpmRest;
import yonyou.bpm.rest.BpmRests;
import yonyou.bpm.rest.exception.RestException;
import yonyou.bpm.rest.param.BaseParam;
import yonyou.bpm.rest.request.identity.BasicDataResourceParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * 说明：初始化数据
 *
 * @author zhangxbk
 * @date 2019-10-29
 */
@RestController
@RequestMapping(value = "/datainit")
public class DataInitController{

    private Logger logger = LoggerFactory.getLogger(DataInitController.class);

    @Value("${baseservice.url}")
    private String dburl;
    @Value("${spring.datasource.username}")
    private String dbusername;
    @Value("${spring.datasource.password}")
    private String dbpassword;
    @Value("${bpmRest.server}")
    private String bpmserver;
    @Value("${bpmRest.tenant}")
    private String bpmtanent;
    @Value("${bpmRest.token}")
    private String bpmtoken;
    @Value("${bpmRest.source}")
    private String source;
    @Value("${local.domain}")
    private String localDomain;
    @Value("${pom.approveCallbackUrl.url}")
    private String approveDetail;
    @Value("${approveRest.server}")
    private  String approveCenterServer;

    /**
     * 初始化云打印数据
     *
     * @param
     * @return
     */
    @RequestMapping(value = "/initdata", method = RequestMethod.POST)
    @ResponseBody
    public Object initData(HttpServletRequest request, HttpServletResponse response , @RequestBody Map<String, Object> params) throws Exception {
        Map<String, Object> result = new HashMap<String, Object>();
        result.put("status", 0);
        result.put("msg", "开通失败");
        if(null == params || params.isEmpty() || !params.containsKey("tenantId") || params.get("tenantId") == null) {
            return result;
        }
        try {
            this.initUiMeta((String)params.get("tenantId"));
            boolean insertBasicResult = this.insertBasicData(params.get("tenantId").toString());
            if (insertBasicResult) {
                result.put("status", 1);
                result.put("msg", "开通成功");
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return result;
    }

    /**
     * 创建回调监听
     * @throws RestException
     * @param tenantId
     */
    private boolean insertBasicData(String tenantId) {
        boolean flag = false;
        BasicDataResourceParam basicDataResourceParam = new BasicDataResourceParam();
        basicDataResourceParam.setCode("cnpf.callback");
        basicDataResourceParam.setName("开发示例采购合同结束监听");
        basicDataResourceParam.setSource(source);
        basicDataResourceParam.setCategory("CNPF.f78f919a-c356-4bbe-81b7-12e52e59f72a");
//        basicDataResourceParam.setTenantId(tenantId);
        basicDataResourceParam.setToken("mddcallback");
        basicDataResourceParam.setUrl(localDomain + "/demo-server/contract/bpmCallback/endListener");
        /*
         * process_listener : 流程终止监听
         * process_start_listener : 流程启用监听
         * message_send : 流程活动监听
         */
        basicDataResourceParam.setType("process_listener");//process_start_listener:process_listener:message_send
        ObjectNode result = null;
        try {
            result = (ObjectNode) getBpmService(tenantId).getIdentityService().insertBasicData(basicDataResourceParam);
            int errorcode = result.findValue("errcode").intValue();
            if(0 == errorcode) {
                flag = true;
            }
        } catch (RestException e) {
            logger.info("注册回调地址异常！" , e.getCause());
        }
        logger.info(result.toString());
        return flag;
    }

    /**
     * getBpmService:初始化rest参数. <br/>
     * @author haojh@yonyou.com
     * @return
     * @param tenantId
     */
    private BpmRest getBpmService(String tenantId){
        BaseParam baseParam=new BaseParam();
        baseParam.setOperatorID(InvocationInfoProxy.getUserid());	//当前用户
        baseParam.setServer(bpmserver);
        baseParam.setTenant(bpmtanent);
        baseParam.setSource(source);
        baseParam.setClientToken(bpmtoken);
        baseParam.setTenantLimitId(tenantId+"_"+source);
        return BpmRests.getBpmRest(baseParam);
    }

    private void initUiMeta(String tenantId) {
        Connection dbConnection = null;
        PreparedStatement ps = null;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        try {
            dbConnection = DriverManager.getConnection(dburl, dbusername, dbpassword);
            if (dbConnection != null) {
                // sql init
                String sqlPath = Thread.currentThread().getContextClassLoader().getResource("tenantinitsql").getPath();
                File file = new File(sqlPath);
                for (File sqlFile : file.listFiles()) {
                    String sql = readString(sqlFile);
                    ps = dbConnection.prepareStatement(sql);
                    ps.setString(1, tenantId);
                    ps.execute();
                }

            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static String readString(File file)
    {
        String str="";
        try {
            FileInputStream in=new FileInputStream(file);
            // size 为字串的长度 ，这里一次性读完
            int size=in.available();
            byte[] buffer=new byte[size];
            in.read(buffer);
            in.close();
            str=new String(buffer,"utf-8");
        } catch (IOException e) {
            // TODO Auto-generated catch block
            return null;
        }
        return str;
    }
}
