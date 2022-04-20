package tw.elliot.trick04.web.ctrl;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tw.elliot.trick04.web.dto.UserDto;
import tw.elliot.trick04.web.mapper.UserMapper;
import tw.elliot.trick04.web.vo.UserVo;

/**
 * @author elliot
 */
@RestController
@RequestMapping("/demo")
public class DemoCtrl {

	@GetMapping("/first")
	public String first() {
		return "Great";
	}

	@GetMapping("/second")
	public String second() {
		throw new RuntimeException("demo");
		//return "";
	}

	@GetMapping("/create")
	public UserVo createUser(@Validated UserVo vo) {
		UserDto userDto = UserMapper.INSTANCE.voToDto(vo);
		userDto.setId(String.valueOf(System.currentTimeMillis()));
		return UserMapper.INSTANCE.dtoToVo(userDto);
	}
}
