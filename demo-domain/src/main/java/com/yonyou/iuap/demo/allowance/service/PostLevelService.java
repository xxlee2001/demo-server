package com.yonyou.iuap.demo.allowance.service;

import com.yonyou.iuap.baseservice.intg.service.GenericUcfService;
import com.yonyou.iuap.baseservice.intg.support.ServiceFeature;
import com.yonyou.iuap.demo.allowance.dao.PostLevelDAO;
import com.yonyou.iuap.demo.allowance.po.PostLevelPO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.yonyou.iuap.baseservice.intg.support.ServiceFeature.AUDIT;

/**
 * @Author guoxh
 * @Date 2020/2/17 18:27
 * @Desc
 **/
@Service
public class PostLevelService extends GenericUcfService<PostLevelPO,String> {

    private PostLevelDAO postLevelDAO;

    @Autowired
    public PostLevelService(PostLevelDAO postLevelDAO){
        this.postLevelDAO = postLevelDAO;
        super.setGenericMapper(postLevelDAO);
    }

    @Override
    protected ServiceFeature[] getFeats() {
        return new ServiceFeature[] { AUDIT};
    }
}
