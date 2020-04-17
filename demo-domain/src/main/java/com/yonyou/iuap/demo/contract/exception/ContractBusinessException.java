package com.yonyou.iuap.demo.contract.exception;

import org.apache.commons.lang3.StringUtils;

import com.yonyou.iuap.ucf.common.exception.BusinessException;
import com.yonyou.iuap.ucf.common.exception.CommonExceptionConstants;
import com.yonyou.iuap.ucf.common.exception.ExceptionMessageUtil;

/**
*@author haojh@yonyou.com
*@date  2019年8月16日---下午2:02:59
*@problem
*@answer
*@action
*/
public class ContractBusinessException extends BusinessException {
	
	private Long errorCode;
	
	public ContractBusinessException(Long errorCode) {
		super(errorCode);
	}
	
	public ContractBusinessException(Long errorCode, Throwable cause) {
		super(errorCode, null, cause, null);
	}

	public ContractBusinessException(Long errorCode, String message) {
		super(errorCode, message, null, null);
	}

	public ContractBusinessException(Long errorCode, String message, Throwable cause) {
		super(errorCode, message, cause, null);
	}

	public ContractBusinessException(Long errorCode, Object[] args) {
		super(errorCode, null, null, args);
	}

	public ContractBusinessException(Long errorCode, Object[] args, Throwable cause) {
		super(errorCode, null, cause, args);
	}

	public ContractBusinessException(String message) {
		super(message);
	}

	public ContractBusinessException(String message, Throwable cause) {
		super(message, cause);
		errorCode = CommonExceptionConstants.NO_ERROR_CODE;
	}

	public ContractBusinessException(Long errorCode, String message, Throwable cause, Object[] args) {
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
