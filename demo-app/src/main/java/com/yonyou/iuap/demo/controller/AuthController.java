/**
 * 
 */
package com.yonyou.iuap.demo.controller;

import com.alibaba.fastjson.JSONObject;
import com.yonyou.diwork.service.auth.IDiWorkPermissionService;
import com.yonyou.diwork.service.pub.IButtonPermissionService;
import com.yonyou.iuap.context.InvocationInfoProxy;
import com.yonyou.iuap.ucf.common.rest.CommonResponse;
import com.yonyou.workbench.model.ButtonVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/**
*@author haojh@yonyou.com
*@date  2019年9月11日---下午3:23:21
*@problem
*@answer
*@action
*/
@RestController
@RequestMapping(value = "/auth")
public class AuthController {

    @Autowired
    private IDiWorkPermissionService permissionService;
    @Autowired
    private IButtonPermissionService buttonPermissionService;

    /**
     * 注册按钮权限
     * @param vo
     * @return
     */
    @RequestMapping(value = "/addbutton", method = RequestMethod.POST)
    public void addButton(@RequestBody ButtonVO vo) {
        buttonPermissionService.create(vo);
    }

    /**
     * 通过serviceId获取按钮列表
     * @param vo
     * @return
     */
    @RequestMapping(value = "/getbuttonList", method = RequestMethod.GET)
    public Object getbuttonList(@RequestBody ButtonVO vo) {
        List<String> list = new ArrayList<>();
        list.add("457bd0a3-b9a9-45a7-81d5-e49ebd8107c5");
        List<ButtonVO> listResult = buttonPermissionService.getButtonPermByServiceIds(list,"w71jqo2a","diwork");
        return JSONObject.toJSON(listResult);
    }

    /**
     * 通过按钮vo获取按钮列表
     * @param vo
     * @return
     */
    @RequestMapping(value = "/deletebutton", method = RequestMethod.POST)
    public Object deletebutton(@RequestBody ButtonVO vo) {
        buttonPermissionService.delete(vo);
        return  CommonResponse.ofSuccess();
    }

    /**
     * 获取按钮权限
     *
     * @param  servicecode
     * @return
     */
    @RequestMapping(value = "/getauthperm", method = RequestMethod.GET)
    public Object getAuthPermList(String servicecode) {
        servicecode = StringUtils.isEmpty(servicecode)?"CONTRACT":servicecode;
        List<String> buttonIdList = permissionService.getPermissionButtons(InvocationInfoProxy.getTenantid(),InvocationInfoProxy.getUserid(),servicecode);
        JSONObject result = new JSONObject();
        result.put("authperm", buttonIdList);
        return CommonResponse.ofSuccess(result);
    }
}
