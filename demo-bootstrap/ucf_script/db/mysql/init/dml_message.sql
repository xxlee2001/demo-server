-- 此脚本作为开发示例预制数据参考使用。
-- 严禁将此脚本直接或修改后在数据库中执行,如由上述行为引发问题,概不支持。
###示例预警的消息模版###
INSERT INTO `message_template_type` (`id`, `application`, `business_variables`, `create_date`, `description`, `document_id`, `domain`, `group_code`, `inner_code`, `metadata_id`, `modification_date`, `parent_id`, `sys_id`, `tenant_id`, `ts`, `type_code`, `type_name`) VALUES ('8dd67a29d12d429084cc70fa9a1e8df2', 'CNPF', '[{\"businessCode\":\"data\",\"businessName\":\"业务数据\",\"type\":\"List\",\"items\":[{\"businessCode\":\"outerCtBillcode\",\"businessName\":\"外部合同号\",\"type\":\"Primitive\"},{\"businessCode\":\"subscribeDate\",\"businessName\":\"合同日期\",\"type\":\"Primitive\"},{\"businessCode\":\"purUserId\",\"businessName\":\"采购员ID\",\"type\":\"Primitive\"},{\"businessCode\":\"purPersonName\",\"businessName\":\"采购员姓名\",\"type\":\"Primitive\"},{\"businessCode\":\"id\",\"businessName\":\"采购合同ID\",\"type\":\"Primitive\"}]},{\"businessCode\":\"url\",\"businessName\":\"url\",\"type\":\"Primitive\"},{\"businessCode\":\"count\",\"businessName\":\"记录个数\",\"type\":\"Primitive\"}]', NULL, NULL, NULL, 'DEVPLAT', 'prewarning', NULL, '2df7c250-aa00-4532-a8f4-80478f03e5a8', NULL, NULL, 'diwork', 'prewarnSys', NULL, 'cn_demo_contract_warning ', '采购合同预警');
INSERT INTO `message_template` (`id`, `type_id`, `template_code`, `template_name`, `sys_id`, `tenant_id`, `tenant_default`, `description`, `enable`, `meta_data_type_mapper`, `tag`, `create_date`, `modification_date`, `ts`) VALUES ('402809536cf522f7016cf6bf5d54000d', '8dd67a29d12d429084cc70fa9a1e8df2', 'cn_demo_contract_warning+##zXWwurHu', 'remain、', 'diwork', 'system', NULL, '', '1', '{}', NULL, now(), NULL, '1567507832147');
INSERT INTO `message_template_content` VALUES ('402809536cf522f7016cf6bf5e16000e', '402809536cf522f7016cf6bf5d54000d', 'mail', 'zh_CN', 'html', '采购合同预警天数', '<p>有[count]个采购合同即将到期，请尽快处理</p><p></p><table border=1 class=\'table-editor\' data-type=\'List\' style=\'color: #333; border-collapse: collapse;border-color:#333\' id=\'table\'><thead style=\'background: #d8d5d5\'><tr><th>外部合同号</th><th>合同日期</th><th>采购员姓名</th></tr></thead><tbody><tr><td>[data.outerCtBillcode]</td><td>[data.subscribeDate]</td><td>[data.purPersonName]</td></tr></tbody></table><p>[SYS_DATE][SYS_TIME]</p>', 0, NULL, NULL, NULL, NULL, '{\"blocks\":[{\"key\":\"3vtfh\",\"text\":\"有[记录个数]个采购合同即将到期，请尽快处理\",\"type\":\"unstyled\",\"depth\":0,\"inlineStyleRanges\":[],\"entityRanges\":[],\"data\":{}},{\"key\":\"3gp1e\",\"text\":\"\",\"type\":\"unstyled\",\"depth\":0,\"inlineStyleRanges\":[],\"entityRanges\":[],\"data\":{}},{\"key\":\"6khva\",\"text\":\"外部合同号合同日期采购员姓名[data.outerCtBillcode][data.subscribeDate][data.purPersonName]\",\"type\":\"table-block\",\"depth\":0,\"inlineStyleRanges\":[],\"entityRanges\":[],\"data\":{\"theadData\":[\"外部合同号\",\"合同日期\",\"采购员姓名\"],\"tbodyData\":[\"[data.outerCtBillcode]\",\"[data.subscribeDate]\",\"[data.purPersonName]\"]}},{\"key\":\"1hrhn\",\"text\":\"[系统日期][系统时间]\",\"type\":\"unstyled\",\"depth\":0,\"inlineStyleRanges\":[],\"entityRanges\":[],\"data\":{}}],\"entityMap\":{}}', NULL);
INSERT INTO `message_template_content` VALUES ('402809536cf522f7016cf6bf5e1b000f', '402809536cf522f7016cf6bf5d54000d', 'sms', 'zh_CN', 'text', '【采购合同时间预警】', '您有[count]个采购合同即将到期，请尽快处理。[SYS_DATE][SYS_TIME]', 0, NULL, NULL, NULL, NULL, '{\"blocks\":[{\"key\":\"8diku\",\"text\":\"您有[记录个数]个采购合同即将到期，请尽快处理。[系统日期][系统时间]\",\"type\":\"unstyled\",\"depth\":0,\"inlineStyleRanges\":[],\"entityRanges\":[],\"data\":{}}],\"entityMap\":{}}', NULL);


