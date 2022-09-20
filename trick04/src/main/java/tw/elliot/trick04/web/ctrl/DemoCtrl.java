package tw.elliot.trick04.web.ctrl;

import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tw.elliot.trick04.web.common.ApplicationResult;
import tw.elliot.trick04.web.dto.UserDto;
import tw.elliot.trick04.web.mapper.UserMapper;
import tw.elliot.trick04.web.vo.UserVo;

import java.util.ArrayList;
import java.util.List;

/**
 * @author elliot
 */

@Slf4j
@RestController
@RequestMapping("/demo")
public class DemoCtrl {
	@Autowired
	private Gson gson;
	@GetMapping("/first")
	public String first() {
		return "Great";
	}

	@GetMapping("/second")
	public String second() {
		throw new RuntimeException("demo");
		//return "";
	}

	@PostMapping("/create")
	public UserVo createUser(@Validated UserVo vo) {
		UserDto userDto = UserMapper.INSTANCE.voToDto(vo);
		userDto.setId(String.valueOf(System.currentTimeMillis()));
		return UserMapper.INSTANCE.dtoToVo(userDto);
	}
	@GetMapping("/findAll")
	public List<UserVo> findAllUsers() {
		List<UserVo> users = new ArrayList<UserVo>();
		UserVo vo = new UserVo();
		vo.setUserId("1111");
		vo.setUserName("aname");
		vo.setState("alive");
		users.add(vo);

		log.info("users[{}]", gson.toJson(users));
		//return ApplicationResult.success(users);
		return users;
	}
}
