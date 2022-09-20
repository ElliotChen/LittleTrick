package tw.elliot.trick04.web.common;

import lombok.Data;

/**
 * @author elliot
 */
@Data
public class ApplicationResult<T> {
	private int code;
	private String message;

	private T data;

	public ApplicationResult() {

	}

	public ApplicationResult(int code, String message) {
		this.code = code;
		this.message = message;
	}

	public static <T> ApplicationResult<T> success(T data) {
		ApplicationResult result = new ApplicationResult(0, "");
		result.setData(data);

		return result;
	}

	public static ApplicationResult failed(int code, String message) {
		return new ApplicationResult(code, message);
	}

	public static ApplicationResult create(ErrorEnum e) {
		return new ApplicationResult(e.getCode(), e.getMessage());
	}
}
