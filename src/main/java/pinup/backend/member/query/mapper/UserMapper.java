package pinup.backend.member.query.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import pinup.backend.member.query.dto.UserDto;

import java.util.List;

@Mapper
public interface UserMapper {

    List<UserDto> findAllUsers();

    List<UserDto> findUsersWithPaging(@Param("size") int size, @Param("offset") int offset);

    int countUsers();

    UserDto findUserById(int id);
}
