package tw.elliot.trick04.web.vo;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class UserVo {
	private String userId;
	@NotBlank(message="user name can't be blank.")
	private String userName;
	private String state;
}
