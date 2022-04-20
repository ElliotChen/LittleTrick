package tw.elliot.trick04.web.common;

public enum ErrorEnum {
	RUNTIME(9001, "Unknown Exception"),
	PARAMETER(8001, "");

	private int code;
	private String message;

	ErrorEnum(int code, String message) {
		this.code = code;
		this.message = message;
	}

	public int getCode() {return this.code;}
	public String getMessage() {return this.message;}
}
