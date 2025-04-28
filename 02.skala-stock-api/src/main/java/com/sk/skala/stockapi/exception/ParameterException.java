package com.sk.skala.stockapi.exception;

import com.sk.skala.stockapi.config.Error;
import com.sk.skala.stockapi.tools.StringTool;
import lombok.Getter;

@Getter
public class ParameterException extends RuntimeException {
	private final int code;

	public ParameterException(String... parameters) {
		super(Error.PARAMETER_MISSED.getMessage() + ": " + StringTool.join(parameters));
		this.code = Error.PARAMETER_MISSED.getCode();
	}
}
