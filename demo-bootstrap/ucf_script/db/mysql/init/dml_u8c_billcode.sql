-- 此脚本作为开发示例预制数据参考使用。
-- 严禁将此脚本直接或修改后在数据库中执行,如由上述行为引发问题,概不支持。
###CN示例编码规则 预制系统数据 支持退码##
INSERT INTO `aa_billnumber`(`autoid`, `tenant_id`, `orgId`, `cbillnum`, `cbillname`, `csubid`, `ballowhandwork`, `brepeatredo`, `istartnumber`, `iseriallen`, `billnumLen`, `billnumInit`, `billnumTruncatType`, `billnumFillType`, `billnumFillMark`, `billnumMode`, `billnumRule`, `isReuse`, `sysid`, `datatype`, `rulecode`, `rulename`, `dr`, `pubts`, `yhtTenantId`, `sntype`) VALUES ('59', NULL, '-1', 'iuap5cn.contract.contract_cncontract_Contract', 'CN采购合同', NULL, '0', '1', '1', '4', '4', '1', '0','0', '0', '1', '1', '0', 'diwork', 2, 'iuap5cn.contract.contract_cncontract_Contract', NULL, '0', now(), 'system', '0');
INSERT INTO `aa_billnumber`(`autoid`, `tenant_id`, `orgId`, `cbillnum`, `cbillname`, `csubid`, `ballowhandwork`, `brepeatredo`, `istartnumber`, `iseriallen`, `billnumLen`, `billnumInit`, `billnumTruncatType`, `billnumFillType`, `billnumFillMark`, `billnumMode`, `billnumRule`, `isReuse`, `sysid`, `datatype`, `rulecode`, `rulename`, `dr`, `pubts`, `yhtTenantId`, `sntype`) VALUES ('60', NULL, '-1', 'iuap5cn.allowance.Allowance', 'CN津贴管理', NULL, '0', '1', '1', '4', '4', '1', '0', '0', '0', '1', '1', '0', 'diwork', 2, 'iuap5cn.allowance.Allowance', NULL, '0', now(), 'system','0');

###系统级别编码规则 所有租户可用 租户若修改则自动复制到该租户下一份###
INSERT INTO `aa_billprefix`(`autoid`, `tenant_id`, `orgId`, `cprefix`, `iprefixlen`, `cprefixrule`, `cprefixseed`, `iorder`, `bfix`, `cprefixid`, `cprefixtype`, `cprefixsep`, `cfieldname`, `csourcename`, `ipurpose`, `fillstyle`, `fillsign`, `billnumberid`, `dr`, `pubts`, `cbillnum`, `yhtTenantId`, `formula`, `formuladisplay`) VALUES ('24688', NULL, '-1', '常量', '2', 'CN', '', '1', '0', '', '2', '', '', NULL, '0', '0', '0', '59', NULL, '2020-02-28 14:27:52', 'iuap5cn.contract.contract_cncontract_Contract', 'system', NULL, NULL);
INSERT INTO `aa_billprefix`(`autoid`, `tenant_id`, `orgId`, `cprefix`, `iprefixlen`, `cprefixrule`, `cprefixseed`, `iorder`, `bfix`, `cprefixid`, `cprefixtype`, `cprefixsep`, `cfieldname`, `csourcename`, `ipurpose`, `fillstyle`, `fillsign`, `billnumberid`, `dr`, `pubts`, `cbillnum`, `yhtTenantId`, `formula`, `formuladisplay`) VALUES ('24689', NULL, '-1', '系统时间', '4', 'yyMM', 'yM', '2', '1', '', '3', '', '', NULL, '0', '0', '0', '59', NULL, '2020-02-28 14:27:52', 'iuap5cn.contract.contract_cncontract_Contract', 'system', NULL, NULL);
INSERT INTO `aa_billprefix`(`autoid`, `tenant_id`, `orgId`, `cprefix`, `iprefixlen`, `cprefixrule`, `cprefixseed`, `iorder`, `bfix`, `cprefixid`, `cprefixtype`, `cprefixsep`, `cfieldname`, `csourcename`, `ipurpose`, `fillstyle`, `fillsign`, `billnumberid`, `dr`, `pubts`, `cbillnum`, `yhtTenantId`, `formula`, `formuladisplay`) VALUES ('24690', NULL, '-1', '常量', '2', 'CN', '', '1', '0', '', '2', '', '', NULL, '0', '0', '0', '60', NULL, '2020-02-28 14:28:11', 'iuap5cn.allowance.Allowance', 'system', NULL, NULL);
INSERT INTO `aa_billprefix`(`autoid`, `tenant_id`, `orgId`, `cprefix`, `iprefixlen`, `cprefixrule`, `cprefixseed`, `iorder`, `bfix`, `cprefixid`, `cprefixtype`, `cprefixsep`, `cfieldname`, `csourcename`, `ipurpose`, `fillstyle`, `fillsign`, `billnumberid`, `dr`, `pubts`, `cbillnum`, `yhtTenantId`, `formula`, `formuladisplay`) VALUES ('24691', NULL, '-1', '系统时间', '6', 'yyyyMM', 'yM', '2', '1', '', '3', '', '', NULL, '0', '0', '0', '60', NULL, '2020-02-28 14:28:11', 'iuap5cn.allowance.Allowance', 'system', NULL, NULL);

