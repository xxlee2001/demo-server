package com.yonyou.iuap.demo.allowance.dao;

import com.yonyou.iuap.demo.allowance.po.PostLevelPO;
import com.yonyou.iuap.ucf.dao.BaseDAO;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * @Author guoxh
 * @Date 2020/2/17 18:26
 * @Desc
 **/
@Mapper
@Repository
public interface PostLevelDAO extends BaseDAO<PostLevelPO,String> {
}
