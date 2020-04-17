package com.yonyou.iuap.demo.utils;

import cn.hutool.core.util.ReflectUtil;
import com.yonyou.iuap.bd.pub.param.Direction;
//import com.yonyou.iuap.demo.allowance.dto.AllowanceSearchVO;
import com.yonyou.iuap.ucf.dao.support.SqlParam;
import com.yonyou.iuap.ucf.dao.utils.FieldUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SearchSupportUtil {

	private static Logger logger = LoggerFactory.getLogger(SearchSupportUtil.class);

//	public static SqlParam processSqlParams(AllowanceSearchVO searchVO, Class<?> entityClass,SqlParam sqlParams) {
//		if (CollectionUtils.isNotEmpty(searchVO.getWhereParam())) {
//			@SuppressWarnings("all")
//			List<Map<String, Object>> wheres = searchVO.getWhereParam();
//			for (Map<String, Object> statment : wheres) {
//				String keyStr = String.valueOf(statment.get(WhereParams.key.name()));
//				Object valueObj = statment.get(WhereParams.value.name());
//				String conditionStr = String.valueOf(statment.get(WhereParams.condition.name()));
//				/**关键参数缺一不可*/
//				if (StringUtils.isEmpty(keyStr) || StringUtils.isEmpty(valueObj)) {
//					logger.warn("reading incomplete whereParams [" + keyStr + ":" + valueObj + "]");
//					throw new RuntimeException("recieving incomplete whereParams [" + keyStr + ":" + valueObj + "]");
//				}
//				Field keyField = ReflectUtil.getField(entityClass, keyStr.toString());
//				if (keyField == null) {
//					logger.warn("finding none field [" + keyStr + "] in model class[" + entityClass + "]!!");
//					throw new RuntimeException(
//							"thre is no field [" + keyStr + "] in model class[" + entityClass + "]!!");
//				}
//				String keyColunm = FieldUtil.getColumnName(keyField);
//				if (StringUtils.isEmpty(conditionStr)) {
//					sqlParams.eq(keyColunm, valueObj);
//				} else if (conditionStr.equalsIgnoreCase(MatchParams.LIKE.name())) {
//					sqlParams.like(keyColunm, String.valueOf(valueObj));
//				} else if (conditionStr.equalsIgnoreCase(MatchParams.ULIKE.name())) {
//					sqlParams.notLike(keyColunm, String.valueOf(valueObj));
//				} else if (conditionStr.equalsIgnoreCase(MatchParams.EQ.name())) {
//					sqlParams.eq(keyColunm, valueObj);
//				} else if (conditionStr.equalsIgnoreCase(MatchParams.UEQ.name())) {
//					sqlParams.ne(keyColunm, valueObj);
//				} else if (conditionStr.equalsIgnoreCase(MatchParams.LLIKE.name())) {
//					sqlParams.like(keyColunm, "%" + String.valueOf(valueObj));
//				} else if (conditionStr.equalsIgnoreCase(MatchParams.RLIKE.name())) {
//					sqlParams.like(keyColunm, String.valueOf(valueObj) + "%");
//				} else {
//					sqlParams.eq(keyColunm, valueObj);
//				}
//			}
//		}
//		if (CollectionUtils.isNotEmpty(searchVO.getOrderByParam())) {
//			@SuppressWarnings("all")
//            List<Map<String, String>> sorts = searchVO.getOrderByParam();
//            if (sorts.size()>0){
//            	List<String> fields = new ArrayList<String>();
//                for (Map<String, String> sort : sorts) {
//                    if (sort.keySet().size() > 0 && sort.keySet().toArray()[0] != null) {
//                        Field keyField = ReflectUtil.getField(entityClass, sort.keySet().toArray()[0].toString());
//                        if (keyField==null){
//                            throw new RuntimeException("cannot find field "+sort.keySet().toArray()[0].toString()+" in  model [" + entityClass + "] ");
//                        }
//                        if(sort.get(sort.keySet().toArray()[0]).toString().equalsIgnoreCase(Direction.DESC)){
//                        	fields.add(FieldUtil.getColumnName(keyField)+" "+Direction.DESC);
//                        }else {
//                        	fields.add(FieldUtil.getColumnName(keyField)+" "+Direction.ASC);
//						}
//                    }
//                }
//                sqlParams.orderBy(fields.toArray( new String[]{}));
//            }else {
//                logger.debug("receiving none sort param in sortMap of querying =>"+entityClass);
//            }
//        }
//		return sqlParams;
//
//	}
}

enum WhereParams {
	condition, key, value;
}

enum MatchParams {
	LIKE,ULIKE,EQ, UEQ, LLIKE, RLIKE;
}
