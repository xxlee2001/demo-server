package com.yonyou.iuap.demo.treecard.exception;

import com.yonyou.iuap.ucf.common.exception.BusinessException;
import com.yonyou.iuap.ucf.common.exception.CommonExceptionConstants;
import com.yonyou.iuap.ucf.common.exception.ExceptionMessageUtil;
import org.apache.commons.lang3.StringUtils;

/**
*@author haojh@yonyou.com
*@date  2019年8月16日---下午2:02:59
*@problem
*@answer
*@action
*/
public class TreecardBusinessException extends BusinessException {
	
	private Long errorCode;
	
	public TreecardBusinessException(Long errorCode) {
		super(errorCode);
	}
	
	public TreecardBusinessException(Long errorCode, Throwable cause) {
		super(errorCode, null, cause, null);
	}

	public TreecardBusinessException(Long errorCode, String message) {
		super(errorCode, message, null, null);
	}

	public TreecardBusinessException(Long errorCode, String message, Throwable cause) {
		super(errorCode, message, cause, null);
	}

	public TreecardBusinessException(Long errorCode, Object[] args) {
		super(errorCode, null, null, args);
	}

	public TreecardBusinessException(Long errorCode, Object[] args, Throwable cause) {
		super(errorCode, null, cause, args);
	}

	public TreecardBusinessException(String message) {
		super(message);
	}

	public TreecardBusinessException(String message, Throwable cause) {
		super(message, cause);
		errorCode = CommonExceptionConstants.NO_ERROR_CODE;
	}

	public TreecardBusinessException(Long errorCode, String message, Throwable cause, Object[] args) {
		super(StringUtils.isEmpty(message)
				? ExceptionMessageUtil.getErrorMessage(String.valueOf(errorCode), args)
				: message, cause);
		if (errorCode == null) {
			this.errorCode = CommonExceptionConstants.NO_ERROR_CODE;
		} else {
			this.errorCode = errorCode;
		}
	}

	/**
	 * @return the errorCode
	 */
	public Long getErrorCode() {
		return errorCode;
	}
	
	
}
