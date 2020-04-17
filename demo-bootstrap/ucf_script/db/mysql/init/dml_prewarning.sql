-- 此脚本作为开发示例预制数据参考使用。
-- 严禁将此脚本直接或修改后在数据库中执行,如由上述行为引发问题,概不支持。
###预警类型###
INSERT INTO `prewarning_type` (`id`, `type_code`, `type_name`, `type_mode`, `type_url`, `type_active`, `sys_id`, `tenant_id`, `create_time`, `type_name2`, `type_name3`, `type_name4`, `type_name5`, `type_name6`, `type_name_ext`, `group_id`, `type_level`, `report_name`, `type_explain`, `type_domain_code`, `is_data_permission`) VALUES ('8dd67a29d12d429084cc70fa9a1e8df2', 'cn_demo_contract_warning ', '采购合同预警', 1, '{{#website.protocol}}://{{#website.domain}}{{#ctx.main}}/contract/contractwarning', 1, 'diwork', 'system', now(), '', '', '', '', '', NULL, 'CNPF', 1, NULL, NULL, 'DEVPLAT', NULL);
###预警类型参数###
INSERT INTO `prewarning_type_condition` (`id`, `cond_code`, `cond_name`, `cond_name2`, `cond_name3`, `cond_name4`, `cond_name5`, `cond_name6`, `cond_name_ext`, `data_type`, `ref_code`, `isnull`, `cond_value`, `type_id`, `is_prewarning`, `is_show`, `ref_use_param`, `cond_explain`) VALUES ('3ac64fde8c7e4bd5a1423b7a635618cc', 'days', '采购合同预警天数', '', '', '', '', '', NULL, '2', '', '1', '', '8dd67a29d12d429084cc70fa9a1e8df2', '1', '1', '0', '');

