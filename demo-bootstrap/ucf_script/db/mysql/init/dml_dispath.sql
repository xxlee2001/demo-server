-- 此脚本作为开发示例预制数据参考使用。
-- 严禁将此脚本直接或修改后在数据库中执行,如由上述行为引发问题,概不支持。
###调度任务-类型###
INSERT INTO `dispatch_taskway`(`id`, `code`, `name`, `url`, `classname`, `taskwayclassid`, `warning`, `active`, `createtime`, `sysid`, `tenantid`, `name2`, `name3`, `name4`, `name5`, `name6`, `name_ext`, `groupid`, `type_level`, `type_explain`) VALUES ('674d3625c0ee4104b75445f630c88023', 'task_03', '调度任务_采购合同', '{{#website.protocol}}://{{#website.domain}}{{#ctx.main}}/contract/checkCreateTime', NULL, NULL, 0, 1, now(), 'diwork', 'system', '', '', '', '', '', NULL, 'CNPF', 1, '调度任务_采购合同');
###调度任务-参数###
INSERT INTO `dispatch_taskparam`(`id`, `code`, `name`, `paramvalue`, `taskwayid`, `type`, `refCode`, `name2`, `name3`, `name4`, `name5`, `name6`, `name_ext`, `is_null`, `ref_code`, `is_show`, `ref_use_param`, `cond_explain`) VALUES ('b643248589a146aa835c39b17fecae68', 'day_count', '天数', '', '674d3625c0ee4104b75445f630c88023', 0, NULL, '', '', '', '', '', NULL, 1, '', 1, 0, '');

