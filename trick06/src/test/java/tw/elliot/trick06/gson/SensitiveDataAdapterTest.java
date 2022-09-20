package tw.elliot.trick06.gson;

import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import tw.elliot.trick06.model.User;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Slf4j
class SensitiveDataAdapterTest {

	@Autowired
	public Gson gson;

	@Test
	public void test() {
		User user = new User();
		user.setName("TT");
		user.setId("you can't see me.");
		log.info("log: {}",gson.toJson(user));
	}
}