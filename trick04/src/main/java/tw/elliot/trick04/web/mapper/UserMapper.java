package tw.elliot.trick04.web.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;
import tw.elliot.trick04.web.dto.UserDto;
import tw.elliot.trick04.web.vo.UserVo;

@Mapper
public interface UserMapper {
	UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

	@Mappings({
			@Mapping(source = "vo.userId", target = "id"),
			@Mapping(source = "vo.userName", target = "name")
	})
	UserDto voToDto(UserVo vo);

	@Mappings({
			@Mapping(source = "dto.id", target = "userId"),
			@Mapping(source = "dto.name", target = "userName")
	})
	UserVo dtoToVo(UserDto dto);
}
