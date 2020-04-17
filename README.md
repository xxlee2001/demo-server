# 经典示例后端工程`iuap-pap-demo-be`

    本文以下内容基于iuap5-cn分支进行讲解和说明，具体代码请参考项目源码  

## 第一部分 对接diwork

### pom文件修改

1. 添加diwork以及rpc的maven依赖，在根pom.xml中添加依赖管理，负责jar包版本。

```xml
<dependency>
    <groupId>com.yonyou.cloud.middleware</groupId>
    <artifactId>mwclient</artifactId>
    <version>5.0.0-SNAPSHOT</version>
    <type>pom</type>
</dependency>
<dependency>
    <groupId>com.yonyou.diwork</groupId>
    <artifactId>diwork-sdk</artifactId>
    <version>4.0.0-SNAPSHOT</version>
</dependency>
```
2. 在demo-receipt-app项目pom.xml中添加具体jar依赖

```xml
<dependency>
    <groupId>com.yonyou.diwork</groupId>
    <artifactId>diwork-sdk</artifactId>
</dependency>
<dependency>
    <groupId>com.yonyou.cloud.middleware</groupId>
    <artifactId>mwclient</artifactId>
    <type>pom</type>
</dependency>
```
### 配置spring boot启动扫描

1. 为BootApplication配置注解

```java
@ComponentScan(basePackages = {"com.yonyou"})   //扫描"com.yonyou"
@MapperScan(basePackages = "com.yonyou", annotationClass = MyBatisRepository.class, factoryBean = UcfMapperFactoryBean.class)//baseservice 扫描特定MyBatisRepository注解
@EnableTransactionManagement
@SpringBootApplication
@EnableSwagger2
@Slf4j
@ServletComponentScan   //添加扫描servletComponent注解
public class ReceiptApplication extends SpringBootServletInitializer {}
```

2. 添加diwork拦截器DiWorkRequestListener

```java
/**
 * 集成diwork，配置请求上下文拦截器
 */
@Bean
public FilterRegistrationBean RequestListener() {
    FilterRegistrationBean registrationBean = new FilterRegistrationBean();
    registrationBean.setFilter(new DiworkRequestListener());
    registrationBean.addUrlPatterns("/*");
    return registrationBean;
}
```
### 登录上下文处理

根据ucf规范，与应用上下文有关，均在app层进行处理。获取上下文信息的方式为：

```java
String tenantId = InvocationInfoProxy.getTenantid();
String userId = InvocationInfoProxy.getUserid();
```

![diwork环境下集成业务单据](doc/README/1561450005820.jpg)

### 开发环境处理

#### 开发环境单点校验

@Order=20
com.yonyou.diwork.verifier.ProductModeSupportVerifier
开发环境下,当应用和diwork环境无法保持同域(localhost/127.0.0.1)时;可在url上挂接参数wb_at=sessionId来获取用户身份

该方法实现方式原理：ProductModeSupportVerifier#getSession方法中，通过在url中获取sessionId，使用rpc请求远程获取完整session信息，完成session验证。

```java
/**
* 通过url中的wb_at参数获取session信息，完成校验。
*/
public DiworkSessionBean getSession(HttpServletRequest request, HttpServletResponse response, DiworkSessionVerifierChain chain) throws BusinessException {
        if (!DiworkRuntimeEnvironment.isProductMode() && "GET".equalsIgnoreCase(request.getMethod())) {
            String token = request.getParameter("wb_at");
            if (StringUtils.isNotBlank(token)) {
                DiworkSessionBean session = this.sessionManager.getSessionBean(token);
                if (session != null) {
                    session.setClientIp(DiworkWebUtil.getRemortIP(request));
                    Cookie cookie = new Cookie("wb_at", token);
                    cookie.setHttpOnly(true);
                    cookie.setPath("/");
                    cookie.setDomain(request.getServerName());
                    response.addCookie(cookie);
                    return session;
                }
            }
        }

        return chain.getSession(request, response);
    }
```

this.sessionManager.getSessionBean(token)方法通过rpc请求获取完成session信息

```java
@RemoteCall("workbench-session@9194af05-6466-4f8c-8c41-40768e0ee4f5")
public interface ISessionManagerService {
    DiworkSessionBean getSessionBean(String var1);
}

```
经过上述处理后，在本地环境中url后挂接参数wb_at=sessionId可正常访问后台服务

![开发环境下集成业务单据](doc/README/1561450329555.jpg)


#### 模拟登录方式（郝剑华）

**TODO**

## 第二部分 业务日志对接

### pom文件修改

服务需要注册到平台微服务框架，在第一部分对接diwork后，服务已经注册到平台微服务平台，还需要增加业务日志sdk，在根pom.xml中添加依赖管理jar包版本。
```
<dependency>
    <groupId>com.yonyou.iuap.log</groupId>
    <artifactId>iuap-log-sdk</artifactId>
    <version>1.0.0-SNAPSHOT</version>
</dependency>
```

在demo-businesstripapply-domain项目pom.xml中添加具体jar依赖
```
<dependency>
    <groupId>com.yonyou.iuap.log</groupId>
    <artifactId>iuap-log-sdk</artifactId>
</dependency>
```

在app层项目的Service层注入业务日志服务接口

```
@Autowired
private IBusinessLogService businessLogService;
```

业务日志需要参数较多，可以将其封装到方法中，便于重复调用，示例如下：
```
	/**
	 * 记录业务日志
	 */
	private void saveBusinessLog(OperCodeTypes operCodeTypes,  Object newObject, String busiObjTypeCode, String busiObjTypeName, String busiObjIdFieldName, String busiObjCodeFieldName) {
		if (newObject == null || operCodeTypes == null) {
			logger.error("Parameters of businesslog are null");
			return;
		}
		// 转换数据对象数据类型
		if (newObject.getClass().isArray()) {
			newObject = Arrays.asList(newObject);
		}
		String tenantId = InvocationInfoProxy.getTenantid();
		String userId = InvocationInfoProxy.getUserid();
		Date operationDate = new Date();
		// 元数据id
		String mdId = null;
		// 业务对象名称字段名，若无名称字段可填null或""
		String busiObjNameFieldName = null;
		// 操作类型名称，有默认值根据operCodeTypes枚举自动获取
		String operationName = null;
		// 业务日志操作详情，有默认值
		String detail = null;
		// 系统标识
		String sysId = InvocationInfoProxy.getSysid();
		try {
			if (newObject instanceof Collection) {
				Collection newObjectCollection = (Collection) newObject;
				if (newObjectCollection.isEmpty()) {
					logger.error("newObjectCollection is empty");
					return;
				}
				BusinessArrayObject businessArrayObject = BusiObjectBuildUtil.buildBusiArrayObject(mdId,
						busiObjTypeCode, busiObjTypeName, newObjectCollection, tenantId, userId, operCodeTypes,
						operationName, detail, operationDate, busiObjIdFieldName, busiObjCodeFieldName,
						busiObjNameFieldName, sysId);
				businessLogService.saveBusinessLog(businessArrayObject);
			} else {
				BusinessObject businessObject = BusiObjectBuildUtil.buildBusinessObject(mdId, busiObjTypeCode,
						busiObjTypeName, newObject, tenantId, userId, operCodeTypes, operationName, detail,
						operationDate, busiObjIdFieldName, busiObjCodeFieldName, busiObjNameFieldName, sysId);
				businessLogService.saveBusinessLog(businessObject);
			}
		} catch (Exception e) {
			logger.error("BusinessLog is recorded exception");
		}
	}

```

在Service中方法的最后调用saveBusinessLog方法记录日志



## 第三部分 对接diwork云打印

### 准备工作

对接云打印首先需要准备云打印预制，其中包括打印模板相关数据和打印业务对象的数据。

#### 打印模板相关的数据

用于在打印模板管理中新增或修改打印模板，需要将如下数据发给打印负责人，直接从数据库中插入数据。

##### 应用数据格式

| code | name(zh_cn) | name2(en) | name3（zh_tw）| order（显示顺序）|parentcode（即领域编码）|
|--------|--------|--------|--------|--------|--------|--------|
|249f0f51-c02a-4f17-a549-78e0dd702f88|院一体化平台|yuanyitihuapingtai|院一体化平台|1|OA|

##### 分类数据格式

| code | name(zh_cn) | name2(en) | name3（zh_tw）| order（显示顺序）|parentcode（即领域编码）|
|--------|--------|--------|--------|--------|--------|--------|
|businesstripapply|出差申请单|chuchaishenqingdan|出差申请单|1|249f0f51-c02a-4f17-a549-78e0dd702f88|

打印模板相关数据预制成功后，如下图：
![diwork环境下集成云打印](doc/README/cloud_print1.png)

#### 打印业务对象

借助接口访问工具，向云打印服务中添加预制数据，将数据插入到云打印的数据库中。需要注意的是，在插入数据时，需要有diwork的登陆信息，并且切换到相应的租户，然后进行插入。

POST请求，云打印接口访问url：https://print-daily.yyuap.com/diworkprint/bo/saveBo
根据PO数据实体，制作打印BO的参数如下：
```
{
	"boStr":{
		"bo":{
			"bo_attrs":[
				{
					"code":"id",
					"fieldtype":"VARCHAR",
					"isPrimary":"1",
					"name":"出差申请单主键"
				},
				{
					"code":"tenantId",
					"fieldtype":"VARCHAR",
					"isPrimary":"0",
					"name":"租户id"
				},
				{
					"code":"staffId",
					"fieldtype":"VARCHAR",
					"isPrimary":"0",
					"name":"人员ID"
				},
				{
					"code":"staffName",
					"fieldtype":"VARCHAR",
					"isPrimary":"0",
					"name":"人员名称"
				},
				{
					"code":"staffCode",
					"fieldtype":"VARCHAR",
					"isPrimary":"0",
					"name":"人员编码"
				},
				{
					"code":"orgId",
					"fieldtype":"VARCHAR",
					"isPrimary":"0",
					"name":"组织ID"
				},
				{
					"code":"orgName",
					"fieldtype":"VARCHAR",
					"isPrimary":"0",
					"name":"组织名称"
				},
				{
					"code":"deptId",
					"fieldtype":"VARCHAR",
					"isPrimary":"0",
					"name":"部门ID"
				},
				{
					"code":"deptName",
					"fieldtype":"VARCHAR",
					"isPrimary":"0",
					"name":"部门名称"
				},
				{
					"code":"tripTypeId",
					"fieldtype":"VARCHAR",
					"isPrimary":"0",
					"name":"出差类别ID"
				},
								{
					"code":"tripTypeIdEnumValue",
					"fieldtype":"VARCHAR",
					"isPrimary":"0",
					"name":"出差类别名称"
				},
				{
					"code":"applyDate",
					"fieldtype":"VARCHAR",
					"isPrimary":"0",
					"name":"申请日期"
				},
				{
					"code":"tripBegintime",
					"fieldtype":"VARCHAR",
					"isPrimary":"0",
					"name":"出差开始时间"
				},
				{
					"code":"tripEndtime",
					"fieldtype":"VARCHAR",
					"isPrimary":"0",
					"name":"出差结束时间"
				},
				{
					"code":"tripDay",
					"fieldtype":"VARCHAR",
					"isPrimary":"0",
					"name":"出差时长"
				},
				{
					"code":"minUnit",
					"fieldtype":"VARCHAR",
					"isPrimary":"0",
					"name":"出差时间单位"
				},
				{
					"code":"cost",
					"fieldtype":"VARCHAR",
					"isPrimary":"0",
					"name":"出差费用"
				},
				{
					"code":"handover",
					"fieldtype":"VARCHAR",
					"isPrimary":"0",
					"name":"交接人"
				},
				{
					"code":"remark",
					"fieldtype":"VARCHAR",
					"isPrimary":"0",
					"name":"出差说明"
				},
				{
					"code":"filePath",
					"fieldtype":"VARCHAR",
					"isPrimary":"0",
					"name":"附件"
				},
				{
					"code":"approveStatus",
					"fieldtype":"VARCHAR",
					"isPrimary":"0",
					"name":"审批状态标识"
				},
				{
					"code":"approveStatusEnumValue",
					"fieldtype":"VARCHAR",
					"isPrimary":"0",
					"name":"审批状态"
				},
				{
					"code":"applyType",
					"fieldtype":"VARCHAR",
					"isPrimary":"0",
					"name":"申请单类型"
				},
				{
					"code":"approver",
					"fieldtype":"VARCHAR",
					"isPrimary":"0",
					"name":"审批人"
				},
				{
					"code":"instanceId",
					"fieldtype":"VARCHAR",
					"isPrimary":"0",
					"name":"流程实例ID"
				},
				{
					"code":"approveType",
					"fieldtype":"VARCHAR",
					"isPrimary":"0",
					"name":"审批类型"
				},
				{
					"code":"extension",
					"fieldtype":"VARCHAR",
					"isPrimary":"0",
					"name":"元数据扩展字段"
				},				
				{
					"code":"createUser",
					"fieldtype":"VARCHAR",
					"isPrimary":"0",
					"name":"创建人"
				},
				{
					"code":"lastModifyUser",
					"fieldtype":"VARCHAR",
					"isPrimary":"0",
					"name":"修改人"
				},
				{
					"code":"lastModified",
					"fieldtype":"VARCHAR",
					"isPrimary":"0",
					"name":"修改时间"
				},
				{
					"code":"applicant",
					"fieldtype":"VARCHAR",
					"isPrimary":"0",
					"name":"申请人ID"
				},
				{
					"code":"applySort",
					"fieldtype":"VARCHAR",
					"isPrimary":"0",
					"name":"申请种类标识"
				},
				{
					"code":"deptApplyId",
					"fieldtype":"VARCHAR",
					"isPrimary":"0",
					"name":"申请部门ID"
				},
				{
					"code":"applicantName",
					"fieldtype":"VARCHAR",
					"isPrimary":"0",
					"name":"申请人姓名"
				},
				{
					"code":"isRevoked",
					"fieldtype":"VARCHAR",
					"isPrimary":"0",
					"name":"是否销差"
				},
				{
					"code":"revokeInstanceId",
					"fieldtype":"VARCHAR",
					"isPrimary":"0",
					"name":"销差流程ID"
				},
				{
					"code":"revokeApproveStatus",
					"fieldtype":"VARCHAR",
					"isPrimary":"0",
					"name":"销差审批状态标识"
				},
				{
					"code":"billCode",
					"fieldtype":"VARCHAR",
					"isPrimary":"0",
					"name":"编码"
				},
				{
					"code":"travelType",
					"fieldtype":"VARCHAR",
					"isPrimary":"0",
					"name":"出差地点类别标识"
				},
				{
					"code":"beginDate",
					"fieldtype":"VARCHAR",
					"isPrimary":"0",
					"name":"出差开始时间"
				},
				{
					"code":"endDate",
					"fieldtype":"VARCHAR",
					"isPrimary":"0",
					"name":"出差结束时间"
				},
				{
					"code":"bpmState",
					"fieldtype":"VARCHAR",
					"isPrimary":"0",
					"name":"BPM状态标识"
				},
				{
					"code":"bpmStatusEnumValue",
					"fieldtype":"VARCHAR",
					"isPrimary":"0",
					"name":"BPM状态"
				},
				{
					"code":"processInstId",
					"fieldtype":"VARCHAR",
					"isPrimary":"0",
					"name":"流程实例ID"
				}
			],
			"bo_code":"businesstripapply",
			"bo_name":"出差申请单",
			"sub_bos":[
				{
					"bo_attrs":[
						{
							"code":"id",
							"fieldtype":"VARCHAR",
							"isPrimary":"0",
							"name":"主键"
						},
						{
							"code":"tenantId",
							"fieldtype":"VARCHAR",
							"isPrimary":"0",
							"name":"租户ID"
						},
						{
							"code":"mainId",
							"fieldtype":"VARCHAR",
							"isPrimary":"1",
							"name":"出差单主表ID"
						},
						{
							"code":"staffId",
							"fieldtype":"VARCHAR",
							"isPrimary":"0",
							"name":"人员ID"
						},
						{
							"code":"tripTypeId",
							"fieldtype":"VARCHAR",
							"isPrimary":"0",
							"name":"出差类型ID"
						},
						{
							"code":"tripBegintime",
							"fieldtype":"VARCHAR",
							"isPrimary":"0",
							"name":"出差开始时间"
						},
						{
							"code":"tripEndtime",
							"fieldtype":"VARCHAR",
							"isPrimary":"0",
							"name":"出差结束时间"
						},
						{
							"code":"tripDay",
							"fieldtype":"VARCHAR",
							"isPrimary":"0",
							"name":"出差时长"
						},
						{
							"code":"minUnit",
							"fieldtype":"VARCHAR",
							"isPrimary":"0",
							"name":"出差时长单位"
						},
						{
							"code":"destination",
							"fieldtype":"VARCHAR",
							"isPrimary":"0",
							"name":"目的地"
						},
						{
							"code":"vehicle",
							"fieldtype":"VARCHAR",
							"isPrimary":"0",
							"name":"交通方式"
						},
						{
							"code":"approveStatus",
							"fieldtype":"VARCHAR",
							"isPrimary":"0",
							"name":"审批状态标识"
						},
						{
							"code":"approvestatusEnumValue",
							"fieldtype":"VARCHAR",
							"isPrimary":"0",
							"name":"审批状态"
						},
						{
							"code":"source",
							"fieldtype":"VARCHAR",
							"isPrimary":"0",
							"name":"出发地"
						},
						{
							"code":"createUser",
							"fieldtype":"VARCHAR",
							"isPrimary":"0",
							"name":"创建人"
						},
						{
							"code":"createTime",
							"fieldtype":"VARCHAR",
							"isPrimary":"0",
							"name":"创建时间"
						},
						{
							"code":"lastModifyUser",
							"fieldtype":"VARCHAR",
							"isPrimary":"0",
							"name":"修改人"
						},
						{
							"code":"lastModified",
							"fieldtype":"VARCHAR",
							"isPrimary":"0",
							"name":"修改时间"
						}
					],
					"bo_code":"BusinessTripApplyDetailPO",
					"bo_name":"出差申请单详细"
				}
			]
		}
	}
}
```

业务对象预制数据加入后，如下图：
![diwork环境下集成云打印](doc/README/cloud_print2.png)


### diwork云打印集成接口

	日常环境地址：https://print-daily.yyuap.com
	正式地址：https://print.yonyoucloud.com

#### diwork调用方式

	https://print-daily.yyuap.com/diworkprint

1、打印模板设计

    请求方式：  GET
    URI：	design/getDesign
    请求参数：
       租户 ： tenantId
       模板编码 : printcode
       语言：lang
    
    返回：设计器页




示例使用：

	tenantId：GET请求，后端接口(/businesstripapply/getSessionContextInfo)
	printcode：OA_businesstripapply
	lang：cn
	
	示例代码：
	前端跳转页面url：https://print-daily.yyuap.com/diworkprint/design/getDesign?printcode=OA_businesstripapply&lang=cn&tenantId=默认租户

打印模板设计页面，如下：

![diwork环境下集成云打印](doc/README/cloud_print3.png)

2、打印预览

    请求方式：  GET
    URI：  /design/getPreview
    请求参数：
       租户 ： tenantId
       模板编码 : printcode
       服务URL : serverUrl
       服务参数：params
       发送类型 : sendType 默认传5
       语言：lang （可以不传）
    
    返回：预览页

示例使用：

	tenantId：GET请求，后端接口(/businesstripapply/getSessionContextInfo)
	printcode：OA_businesstripapply
	serverUrl：http://ip:port/iuap-ucf-demo-be/businesstripapply/dataForPrint
	params：encodeURIComponent(JSON.stringify({id:"id"}))
	
	示例代码：
	前端跳转页面url：https://print-daily.yyuap.com/diworkprint/design/getPreview?printcode=OA_businesstripapply&lang=cn&tenantId=%E9%BB%98%E8%AE%A4%E7%A7%9F%E6%88%B7&serverUrl=http://wbalone-dev.yyuap.com:7777/iuap-ucf-demo-be/businesstripapply/dataForPrint&params=%7B%22id%22%3A%228462742fc1b34bc79aae798bbfc60f68%22%7D&sendType=5

打印预览页面，如下：

![diwork环境下集成云打印](doc/README/cloud_print4.png)

打印预览页面能显示出来，表示diwork云打印已经接入完成。

**接入diwork云打印时，需要注意：**

在单据页面点击打印预览时，diwork打印服务需要回调示例服务接口获取打印数据，在回调时的url地址如果是域名，需要在开发者中心的DNS中增加此域名的解析配置，否则会默认解析到另一台服务器上，导致云打印服务获取数据连接超时。

## 第四部分 元数据接入diwork VS u8c
 

<font>1219和u8c的区别?</font>

u8c精简了一些，去掉了一些人力用的老字段。u8c新增支持：导出XML，导出SQL，导出java，直接通过XML导入。

不同点：对接云打印时u8c不能直接建打印要求格式的元数据（主子表结构），需要找高菘手动修改。

        

<font>有没有开放设计器?</font>

diwor组件1219设计器地址： [https://ms-daily.yyuap.com/metadata-server/form/index.html#/metadata/index](https://ms-daily.yyuap.com/metadata-server/form/index.html#/metadata/index)

u8c组件设计器地址：[http://u8cms-daily.yyuap.com/metadata/web/u8c/metadata/index.html?#/metadata/index?id=8f7ad435-3326-4ad0-9819-de187381fae5](http://u8cms-daily.yyuap.com/metadata/web/u8c/metadata/index.html?#/metadata/index?id=8f7ad435-3326-4ad0-9819-de187381fae5)

注：

*   先建组件，再在组件下建实体，实体下建属性。

*   标签分为：组件标签，实体标签，属性标签，具体使用什么要看服务怎么约定。

<font>元数据集成的xml根文件有没有变化?</font>

1219不支持调用本地元数据（xml），只有u8c支持。

  

<font>参照，枚举等等特殊数据类型的使用（比如翻译）有什么变化？</font>

枚举类型的处理是一样的，实体类型的处理方式不一样，比如打印对接。

![1.png](.\readmePic\1.png)

![2.png](.\readmePic\2.png)


所属应用：由高菘帮忙初始化进入，可以u8c平台中的名称相同也可以不同，不发生关联，只做分组管理用

编码、名称、所属模块自定义

领域：领域code（可以找董博查）

![3.png](.\readmePic\3.png)

![4.png](.\readmePic\4.png)

![5.png](.\readmePic\5.png)

![6.png](.\readmePic\6.png)
只填写必填，其他是特定领域使用，可以不填写。

![7.png](.\readmePic\7.png)

![8.png](.\readmePic\8.png)
如果和数据库中的表有关系，填字段名

![9.png](.\readmePic\9.png)

![10.png](.\readmePic\10.png)
业务日志对接过程中要配置编码，名称标签，否则在日志列表中不显示，如下图

![11.png](.\readmePic\11.png)

![12.png](.\readmePic\12.png)
