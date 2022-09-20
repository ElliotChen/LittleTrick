package tw.elliot.trick04.web.common;

import lombok.Getter;

@Getter
public enum ErrorEnum {
	RUNTIME(9001, "Unknown Exception"),
	PARAMETER(8001, "");

	private int code;
	private String message;

	ErrorEnum(int code, String message) {
		this.code = code;
		this.message = message;
	}

}
