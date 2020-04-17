package com.yonyou.iuap.demo.contract;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.httpclient.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.junit.Test;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;

import yonyou.bpm.rest.BpmRest;
import yonyou.bpm.rest.BpmRests;
import yonyou.bpm.rest.exception.RestException;
import yonyou.bpm.rest.param.BaseParam;
import yonyou.bpm.rest.request.RestVariable;
import yonyou.bpm.rest.request.category.CategoryResourceParam;
import yonyou.bpm.rest.request.form.FormFieldQueryParam;
import yonyou.bpm.rest.request.form.FormFieldResourceParam;
import yonyou.bpm.rest.request.form.FormQueryParam;
import yonyou.bpm.rest.request.form.FormResourceParam;
import yonyou.bpm.rest.request.identity.BasicDataResourceParam;
import yonyou.bpm.rest.request.identity.BasicdataQueryParam;
import yonyou.bpm.rest.request.identity.TenantResourceParam;
import yonyou.bpm.rest.request.repository.ProcessDefinitionModelQuery;
import yonyou.bpm.rest.request.task.TaskQueryParam;

/**
 * CLASSNAME:PACKAGE_NAME.BpmSDKTest
 * DESCRIBTION:
 * 流程开发态预置数据过程：
 * 1、创建物理租户
 * 2、创建安全租户，获取token
 * 3、创建子应用
 * 4、创建单据类型
 * 5、创建流程模型
 * 6、同步表单和表单字段
 * 7、回调注册
 * @author eason<mailto:liuychk @ yonyou.com>
 * @version 1.0
 * @create 2019/7/2-10:03 AM
 */
public class BpmSDKTest {

    //BPM服务地址
//    private static  String bpmserver="http://ys.demo01-on-premises.yyuap.com/";

    private static  String bpmserver="http://172.20.52.29/";
    //默认安全租户
    private static String bpmtanent = "uapbpm";
    private static String bpmtoken = "4d2e4ded04777f466509b2efaf07eb5aa52397d0f68a77c664aa305a4c10613e";
    //流程注册应用source
    private static String source = "u8c";//approve:devPlat
    //业务租户
    private static String limitTenant="tqden74q";
    //当前操作用户
    private static String operatorId = "ec4326d0-d490-4d5a-9105-260b51fd16d0";
    
    /**
     * getBpmService:初始化rest参数. <br/>
     * @author haojh@yonyou.com 
     * @return
     */
    public static BpmRest getBpmService(){
        BaseParam baseParam=new BaseParam();
        baseParam.setOperatorID(operatorId);	//当前用户
        baseParam.setServer(bpmserver + "/ubpm-web-rest/");
        baseParam.setTenant(bpmtanent);
        baseParam.setSource(source);
        baseParam.setClientToken(bpmtoken);
        baseParam.setTenantLimitId(limitTenant+"_"+source);
        return BpmRests.getBpmRest(baseParam);
    }

    /**
     * 创建流程source
     */
//    @Test
    public void initSubApp(){
        TenantResourceParam tenantResourceParam=new TenantResourceParam();
        tenantResourceParam.setId(limitTenant);
        tenantResourceParam.setName("测试应用");
        tenantResourceParam.setCode(source);
        //默认U8C
        tenantResourceParam.setTenantContent("{\"systemCode\":\"diwork\", \"enableNewDesigner\":true,\"formSource\":\"u8c\",\"isU8c\":\"true\"}");
        tenantResourceParam.setType(1);//1是应用
        try {
            Object resp = getBpmService().getIdentityService().saveTenant(tenantResourceParam);
            System.out.println(resp.toString());
        } catch (RestException e) {
            e.printStackTrace();
        }
    }

    /**
     * 审批中心注册应用
     * @throws ClientProtocolException
     * @throws IOException
     */
    @Test
    public void registSource4ApproveCenter() throws ClientProtocolException, IOException{
        /*
         * appId(diwork applicationCode):561a9b33-4e36-4505-ac0e-be1b2561317e
         * mUrl(移动端详情地址)
         * webUrl(web端详情地址):
         * source(应用source)
         */
        String appName = "开发示例";
        String appCode = "CNPF"; //diwork中应用code
        String webUrl = "http://172.20.52.248/iuap5-sample-code-skip/ucf-publish/skip"; //审批中心打开单据详情页
    	String content = "{\"appId\":\"" + appCode + "\",\"appName\":\"" + appName + "\",\"mUrl\":\"\",\"webUrl\":\"" + webUrl + "\",\"source\":\"" + source + "\"}";
    	String application = RegisterDigestUtils.encode(content, limitTenant);
    	CloseableHttpClient httpClient = HttpClients.createDefault();
    	HttpPost httpPost = new HttpPost(bpmserver + "/approvecenter/register/applications");
    	StringEntity entity = new StringEntity("{\"tenant\":\"" + limitTenant + "\",\"application\": \"" + application + "\"}", "utf-8");
        entity.setContentEncoding("UTF-8");
        entity.setContentType("application/json");
        httpPost.setEntity(entity);
    	
    	CloseableHttpResponse result = httpClient.execute(httpPost);

        {
            String str = "";
            if (result.getStatusLine().getStatusCode() == HttpStatus.SC_OK)
            try
            {
                // 读取服务器返回过来的json字符串数据
                str = EntityUtils.toString(result.getEntity(), "utf-8");
            }
            catch (Exception e)
            {
            	System.out.println("post请求提交失败:" + e.getMessage());
            }
        }
    }
    
    
    private String categoryName="一主多子_采购合同";
    private String categoryCode="multi_contract";
    
    /**
     * 1_step：创建单据类型（1219版本里是分类）
     * createCategory:(这里用一句话描述这个方法的作用). <br/> 
     * @author haojh@yonyou.com
     */
//    @Test
    public void createCategory(){
        try {
            CategoryResourceParam categoryResourceParam = new CategoryResourceParam();
            categoryResourceParam.setName(categoryName);
            categoryResourceParam.setCode(categoryCode);
            categoryResourceParam.setSource(source);
            JsonNode result = (JsonNode) getBpmService().getCategoryService().insertCategory(categoryResourceParam);
            //result = {"errcode":0,"errmsg":"ok","id":"3ffe4516-b988-11e9-973e-c255c3bf7297","url":null,"tenantId":"a7o82kew_caep","revision":0,"status":null,"code":"multi_contract","name":"一主多子_采购合同"}
            System.out.println(result);
        } catch (RestException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * 第二步是创建流程模型
     * 3_step:同步表单（819里是交易类型）
     * processcenter_946d3654,
     * category:68ce0c6b-9ed4-11e9-85fd-6ad22ff97c2a
     * model:c8599fc5-9ed5-11e9-85fd-6ad22ff97c2a
     * from:
     * result返回formId，formField方法注册使用
     */
    
    private String modelId = "0ca5527a-b989-11e9-973e-c255c3bf7297";
    
//    @Test
    public void syncForm(){
        FormResourceParam formResourceParam =new FormResourceParam();
        formResourceParam.setModelId(modelId); //流程模型id
        formResourceParam.setTitle("采购合同单据"); //标题
        formResourceParam.setCode("multi_contract_form"); //编码
        formResourceParam.setDescription("采购合同单据"); //描述

        try {
            Object result = getBpmService().getFormService().saveForm(formResourceParam);
            //{"errcode":0,"errmsg":"ok","id":"5498b49d-b98a-11e9-973e-c255c3bf7297","url":null,"tenantId":"a7o82kew_caep","revision":0,"status":null,"code":"multi_contract_form","title":"采购合同单据","pk_org":null,"name":"采购合同单据"}
            System.out.println(result.toString());
        } catch (RestException e) {
            e.printStackTrace();
        }
    }
    
    
//    @Test
    public void queryForm(){
        FormQueryParam formQueryParam = new FormQueryParam();
        formQueryParam.setModelId(modelId);
        try {
            Object result = getBpmService().getFormService().queryForms(formQueryParam);
            //{"data":[{"errcode":0,"errmsg":"ok","id":"5498b49d-b98a-11e9-973e-c255c3bf7297","url":null,"tenantId":"a7o82kew_caep","revision":1,"status":null,"code":"multi_contract_form","title":"采购合同单据","pk_org":null,"pId":null,"modelId":"0ca5527a-b989-11e9-973e-c255c3bf7297","processKey":null,"processId":null,"activityId":null,"description":"采购合同单据","fieldsOrder":null,"createTime":"2019-08-08T11:12:18.000+08:00","modifyTime":"2019-08-08T11:12:18.000+08:00","phoneEnabled":false,"category":"0a605c81-9c73-11e9-b64f-7ac9e7734b44","categoryCode":"defaultCategory","categoryName":"其他","fields":null,"subForms":null,"formContent":null,"tableName":null,"source":null,"formId":"5498b49d-b98a-11e9-973e-c255c3bf7297","name":"采购合同单据"}],"total":1,"start":0,"sort":null,"order":null,"size":1,"errcode":0,"errmsg":"ok"}
            System.out.println(result.toString());
        } catch (RestException e) {
            e.printStackTrace();
        }
    }
    
    
    /**
     * 4_step:同步表单字段
     * syncFormFileds:(这里用一句话描述这个方法的作用). <br/> 
     * 分支条件需要什么字段就传
     * @author haojh@yonyou.com
     */
//    @Test
    public void syncFormFileds(){
        List<FormFieldResourceParam> fields = new ArrayList();

        //下拉字段示例
        FormFieldResourceParam selectFormFieldResourceParam = new FormFieldResourceParam();
        selectFormFieldResourceParam = new FormFieldResourceParam();
        selectFormFieldResourceParam.setFormId("5498b49d-b98a-11e9-973e-c255c3bf7297"); //表单id
        selectFormFieldResourceParam.setName("出差地点类别"); //字段名称
        selectFormFieldResourceParam.setCode("travelType"); //字段编码
        selectFormFieldResourceParam.setTypeName("select"); //字段类型
        selectFormFieldResourceParam.setSelectOptions(new String[]{"1","2"});//1:省内；2：省外
        selectFormFieldResourceParam.setModelId(modelId); //流程模型id
        fields.add(selectFormFieldResourceParam);

        //数字字段示例
        FormFieldResourceParam numberFormFieldResourceParam = new FormFieldResourceParam();
        numberFormFieldResourceParam = new FormFieldResourceParam();
        numberFormFieldResourceParam.setFormId("a993258d-a088-11e9-85fd-6ad22ff97c2a"); //表单id
        numberFormFieldResourceParam.setName("出差时长"); //字段名称
        numberFormFieldResourceParam.setCode("tripDay"); //字段编码
        numberFormFieldResourceParam.setTypeName("number"); //字段类型
        numberFormFieldResourceParam.setModelId("c8599fc5-9ed5-11e9-85fd-6ad22ff97c2a"); //流程模型id
        fields.add(numberFormFieldResourceParam);

        //参照字段示例        staffid
        /*
        FormFieldResourceParam refFormFieldResourceParam = new FormFieldResourceParam();
        refFormFieldResourceParam = new FormFieldResourceParam();
        refFormFieldResourceParam.setFormId("ab1d91e2-9c96-11e9-b64f-7ac9e7734b44"); //表单id
        refFormFieldResourceParam.setName("出差人"); //字段名称
        refFormFieldResourceParam.setCode("staffid"); //字段编码
        refFormFieldResourceParam.setTypeName("reference"); //字段类型
        refFormFieldResourceParam.setRefCode("staff");
        refFormFieldResourceParam.setRefUrl("https://wbalone-dev.yyuap.com/iuap5-sample-code-fe/refmdd/");
        refFormFieldResourceParam.setModelId("87919767-9c90-11e9-b64f-7ac9e7734b44"); //流程模型id
        fields.add(refFormFieldResourceParam);
        */

        try {
            Object result  = getBpmService().getFormService().saveFormFields(fields);
            System.out.println(result.toString());
        } catch (RestException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * 5_step:注册回调地址
     * insertBasicData:(这里用一句话描述这个方法的作用). <br/> 
     * diwork 1219版本适用.<br/> 
     * TODO(这里描述这个方法的执行流程 – 可选).<br/> 
     * TODO(这里描述这个方法的使用方法 – 可选).<br/> 
     * TODO(这里描述这个方法的注意事项 – 可选).<br/> 
     * 
     * @author haojh@yonyou.com
     */
    @Test
    public void insertBasicData(){
        try {
            BasicDataResourceParam basicDataResourceParam = new BasicDataResourceParam();
            basicDataResourceParam.setCode("completet.12345");
            basicDataResourceParam.setName("采购合同监听");
            basicDataResourceParam.setSource(source);
//            basicDataResourceParam.setTenantId(limitTenant);
            basicDataResourceParam.setToken("mddcallback");
            basicDataResourceParam.setUrl("http://172.20.52.248/demo-server/contract/bpmCallback/endListener");
            /*
             * process_listener : 流程终止监听
             * process_start_listener : 流程启用监听
             * message_send : 流程活动监听
             */
            basicDataResourceParam.setType("process_listener");//process_start_listener:process_listener:message_send
            Object result = getBpmService().getIdentityService().insertBasicData(basicDataResourceParam);
            System.out.println(result.toString());
        } catch (RestException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * 
     * registListenService: 注册监听事件. <br/> 
     * TODO(这里描述这个方法适用条件 – 可选).<br/> 
     * TODO(这里描述这个方法的执行流程 – 可选).<br/> 
     * TODO(这里描述这个方法的使用方法 – 可选).<br/> 
     * TODO(这里描述这个方法的注意事项 – 可选).<br/> 
     * 
     * @author haojh@yonyou.com
     */
    public void registListenService(){
    	BasicDataResourceParam param = new BasicDataResourceParam();
    	param.setCode("contract-manager-");
    	param.setName("采购主管审批监听");
    	param.setType("activity_select");
    	param.setToken(bpmtoken);
//    	param.setUrl(url);
//    	param.setTenantId(tenantId);
//    	param.setSource(source);
//    	param.setCategory(category);
    	
//    	this.getBpmService().getIdentityService().insertBasicData(param);
    	
    	
    }
    

//    @Test
    public void queryFormFileds(){
        FormFieldQueryParam formFieldQueryParam = new FormFieldQueryParam();
        formFieldQueryParam.setFormId("5498b49d-b98a-11e9-973e-c255c3bf7297");
        try {
            Object result = getBpmService().getFormService().queryFormFields(formFieldQueryParam);
            System.out.println(result.toString());
        } catch (RestException e) {
            e.printStackTrace();
        }
    }

//    @Test
    public void deleteFormFileds(){
        try {
            Object result = getBpmService().getFormService().deleteFormField("519e5e729cce11e9b64f7ac9e7734b44");//参数是什么？
            System.out.println(result.toString());
        } catch (RestException e) {
            e.printStackTrace();
        }
    }


//    @Test
    public void queryBasicData(){
        try {
            BasicdataQueryParam basicdataQueryParam = new BasicdataQueryParam();
            basicdataQueryParam.setTenantId(bpmtanent);
            basicdataQueryParam.setSource(source);
            Object result = getBpmService().getIdentityService().queryBasicdatas(basicdataQueryParam);
            System.out.println(result.toString());
        } catch (RestException e) {
            e.printStackTrace();
        }
    }
    
    

//    @Test
    public void deleteBasicData(){
        try {
            Object result = getBpmService().getIdentityService().deleteBasicData("multi_contract_start",bpmtanent);
            System.out.println(result.toString());
        } catch (RestException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取modelinfo，从info中获取processkey来提交流程
     */
//    @Test
    public void queryModelInfo(){

    }

//    @Test
    public void startBpmProcessinst(){
        try {
            RestVariable procVariable1 = new RestVariable();
            procVariable1.setName("traveltype"); //对应表单字段编码
            procVariable1.setType("string"); //变量类型
            procVariable1.setValue("1");
            List<RestVariable> variables = new ArrayList();
            variables.add(procVariable1);
            JsonNode result = (JsonNode) getBpmService().getRuntimeService().startProcessInstanceByKey ("processcenter_ad8686a2", "111",variables);
            System.out.println(result);
        } catch (RestException e) {
            e.printStackTrace();
        }
    }
    
    //如何获取最新流程定义
//    @Test
    public void queryProcessbycategory(){
        try {
            ProcessDefinitionModelQuery processDefinitionModelQuery = new ProcessDefinitionModelQuery();
            processDefinitionModelQuery.setCategory("1357000026525952");//CONTRACTS
            JsonNode result = (JsonNode) getBpmService().getRepositoryService().getProcessDefinitionModels(processDefinitionModelQuery);
            System.out.println(result);
        } catch (RestException e) {
            e.printStackTrace();
        }

    }

//    @Test
    public void getTaskInfo(){
        String formUrl = "";
        try {
            TaskQueryParam taskQueryParam = new TaskQueryParam();
            taskQueryParam.setTaskId("ea42243c-9fb9-11e9-85fd-6ad22ff97c2a");
            taskQueryParam.setIncludeProcessVariables(true);
            JsonNode result = (JsonNode) getBpmService().getTaskService().queryTasks(taskQueryParam);
            System.out.println(result);
            ArrayNode proVariables = (ArrayNode) result.get("data").get(0).get("variables");
            System.out.println(proVariables);
            if(proVariables != null){
                for(int i=0; i<proVariables.size(); i++){
                    JsonNode variable = proVariables.get(i);
                    if("formUrl".equals(variable.get("name"))){
                        formUrl = variable.get("value").asText();
                        break;
                    }
                }
            }
        } catch (RestException e) {
            e.printStackTrace();
        }
    }
}
