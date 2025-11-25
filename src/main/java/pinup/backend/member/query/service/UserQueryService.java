package pinup.backend.member.query.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pinup.backend.member.command.domain.Users;
import pinup.backend.member.command.repository.MemberCommandRepository;
import pinup.backend.member.query.dto.UserDto;
import pinup.backend.member.query.mapper.UserMapper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class UserQueryService {

    private final UserMapper userMapper;
    private final MemberCommandRepository memberCommandRepository;

    public List<UserDto> getAllUsers() {
        return userMapper.findAllUsers();
    }

    public Map<String, Object> getUsers(int page, int size) {

        int offset = (page - 1) * size;

        List<UserDto> users = userMapper.findUsersWithPaging(size, offset);
        int totalElements = userMapper.countUsers();

        int totalPages = (int) Math.ceil((double) totalElements / size);

        Map<String, Object> result = new HashMap<>();
        result.put("content", users);
        result.put("page", page);
        result.put("size", size);
        result.put("totalElements", totalElements);
        result.put("totalPages", totalPages);

        return result;
    }

    public UserDto getUserById(int id) {
        return userMapper.findUserById(id);
    }

    public List<UserDto> getSuspendedUsers() {
        return memberCommandRepository.findByStatus(Users.Status.SUSPENDED)
                .stream()
                .map(user -> {
                    UserDto dto = new UserDto();
                    dto.setUserId(user.getUserId());
                    dto.setName(user.getName());
                    dto.setNickname(user.getNickname());
                    dto.setEmail(user.getEmail());
                    dto.setStatus(user.getStatus().name());
                    return dto;
                })
                .toList();
    }

}
