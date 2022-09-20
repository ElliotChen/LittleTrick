package tw.elliot.trick06.model;

import com.google.gson.annotations.JsonAdapter;
import lombok.Data;

@Data
public class User {
	private String name;

	@JsonAdapter(SensitiveData.class)
	private String id;
}
