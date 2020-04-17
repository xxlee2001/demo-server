package com.yonyou.iuap.demo.bpm.common.server.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.yonyou.iuap.context.InvocationInfoProxy;
import com.yonyou.iuap.ucf.common.exception.BusinessException;
import com.yonyou.iuap.ucf.common.rest.CommonResponse;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import yonyou.bpm.rest.BpmRest;
import yonyou.bpm.rest.BpmRests;
import yonyou.bpm.rest.exception.RestException;
import yonyou.bpm.rest.param.BaseParam;
import yonyou.bpm.rest.request.historic.HistoricTaskQueryParam;

import java.util.HashMap;
import java.util.Map;

/**
*@author haojh@yonyou.com
*@date  2019年9月2日---下午7:40:49
*@problem
*@answer
*@action
*/
@RestController
@RequestMapping(value="bpmcommonserver")
public class BpmCommonServerController {


	private Logger logger = LoggerFactory.getLogger(BpmCommonServerController.class);
	
	@Value("${bpmRest.server}")
	private String serverUrl;
	@Value("${bpmRest.tenant}")
	private String tenant;
	@Value("${bpmRest.token}")
	private String token;
	@Value("${bpmRest.source}")
	private String source;
	
	private static final String APPROVE_LOCALE = "approve_locale";


    /**
     * 根据流程任务id获取单据详情url
     * @param taskId 指派提交参数对象:任务ID
     * @return 流程提交是否成功
     */
    @RequestMapping(value = "/bpmSubmit/getFormUrl", method = RequestMethod.GET)
    public Object getFormUrl(@RequestParam String taskId,
                             @RequestParam String businessKey,
                             @RequestParam String taskFlag,
                             @RequestParam String processInstanceId) {
        String url = this.getFormUrl(taskId,taskFlag,processInstanceId) + businessKey;
        Map<String, Object> result = new HashMap();
        if (StringUtils.isBlank(url)) {
            return CommonResponse.ofFail(new BusinessException(2L, "修改单据状态失败，回滚当前流程！"));
        } else {
            result.put("formUrl", url);
            return CommonResponse.ofSuccess(result);
        }
    }

    /**
     * 根据taskId获取单据服务打开url
     * @param taskId 流程任务id
     * @return 单据打开url
     * TODO 日志输出
     */
    public String getFormUrl(String taskId,String taskFlag,String processInstanceId) {
        String formUrl = "";
        try {
            JsonNode result =null;
//            if(taskFlag.equals("todo")){
//                TaskQueryParam taskQueryParam = new TaskQueryParam();
//                taskQueryParam.setTaskId(taskId);
//                taskQueryParam.setIncludeProcessVariables(true);
//                result = (JsonNode) this.bpmRestService(InvocationInfoProxy.getUserid()).getTaskService().queryTasks(taskQueryParam);
//            }else{
//                HistoricTaskQueryParam historyQueryParam = new HistoricTaskQueryParam();
//                historyQueryParam.setTaskId(taskId);
//                historyQueryParam.setIncludeProcessVariables(true);
//                result = (JsonNode) this.bpmRestService(InvocationInfoProxy.getUserid()).getHistoryService().getHistoricTaskInstances(historyQueryParam);
//            }

            HistoricTaskQueryParam historyQueryParam = new HistoricTaskQueryParam();
            historyQueryParam.setIncludeProcessVariables(true);
            historyQueryParam.setProcessInstanceId(processInstanceId);
            result = (JsonNode) this.bpmRestService(InvocationInfoProxy.getUserid()).getHistoryService().getHistoricTaskInstances(historyQueryParam);

            if(result.get("data").size()==0){
                throw new BusinessException("查询不到此流程");
            }
            ArrayNode proVariables = (ArrayNode) result.get("data").get(0).get("variables");
            if (proVariables != null) {
                for (int i = 0; i < proVariables.size(); i++) {
                    JsonNode variable = proVariables.get(i);
                    if ("formUrl".equals(variable.get("name").asText())) {
                        formUrl = variable.get("value").asText();
                        break;
                    }
                }
            }
        } catch (RestException e) {
            logger.error("");
            return formUrl;
        }
        return formUrl;
    }
	
    public BpmRest bpmRestService(String userId) {
        BaseParam baseParam = new BaseParam();
        baseParam.setOperatorID(userId);
        logger.error("初始化流程参数operator：" + userId );
        baseParam.setServer(serverUrl);
        logger.error("初始化流程参数serverUrl：" + serverUrl );
        baseParam.setTenant(tenant);
        logger.error("初始化流程参数tenant：" +  tenant);
        baseParam.setClientToken(token);
        baseParam.setSource(source);
        logger.error("初始化流程参数source：" +  source);
        String language = InvocationInfoProxy.getParameter(APPROVE_LOCALE);
        if (StringUtils.isBlank(language)) {
            baseParam.setLanguage(InvocationInfoProxy.getLocale());
        } else {
            baseParam.setLanguage(language);
        }
        String tenantId = InvocationInfoProxy.getTenantid();
        logger.error("租户id：" +  tenantId);
        if (StringUtils.isNotBlank(tenantId)) {
            baseParam.setTenantLimit(tenantId);
        } else {
            throw new BusinessException("无法获取当前租户信息");
        }
        BpmRest bpmRest = BpmRests.getBpmRest(baseParam);
        return bpmRest;
    }
}
