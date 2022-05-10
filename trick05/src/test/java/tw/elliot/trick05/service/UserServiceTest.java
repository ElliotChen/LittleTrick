package tw.elliot.trick05.service;

import org.springframework.util.Assert;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class UserServiceTest {

	@Autowired
	private UserService userService;

	@Test
	public void test() {
		Assert.notNull(userService, "Got no user service");
	}
}