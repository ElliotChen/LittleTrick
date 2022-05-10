package tw.elliot.trick05.ctrl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tw.elliot.trick05.model.User;
import tw.elliot.trick05.service.UserService;

import java.util.List;

@RestController
@RequestMapping("/trace")
public class TracingCtrl {

	@Autowired
	private UserService userService;

	@RequestMapping("/users")
	public List<User> findAllUsers() {
		return userService.findAllUsers();
	}

	@RequestMapping("/echo")
	public String echo() {
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
		return "connected";
	}
}
