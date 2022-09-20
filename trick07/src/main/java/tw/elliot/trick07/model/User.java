package tw.elliot.trick07.model;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;
import tw.elliot.trick07.json.SensitiveDataConvert;

@Data
public class User {
	private String name;

	@JsonSerialize(using = SensitiveDataConvert.class)
	private String id;

	@SensitiveData
	private String phone;
}
