package tw.elliot.trick07.json;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import tw.elliot.trick07.model.User;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@SpringBootTest
class SensitiveDataConvertTest {

	@Autowired
	private ObjectMapper objectMapper;

	@Test
	public void test() {
		User user = new User();

		user.setName("AA");
		user.setId("You can't see me");
		user.setPhone("Another field can't see.");
		try {
			System.out.println(objectMapper.writeValueAsString(user));
		} catch (JsonProcessingException e) {
			throw new RuntimeException(e);
		}
		//log.info("object [{}]", user);
	}
}