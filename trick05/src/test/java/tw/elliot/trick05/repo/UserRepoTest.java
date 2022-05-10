package tw.elliot.trick05.repo;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.Assert;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class UserRepoTest {
	@Autowired
	private UserRepo repo;

	@Test
	public void test() {
		Assert.notNull(repo, "Got no UserRepo instance");
	}
}