package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.dto.NewUserRequest;
import ru.practicum.dto.UserDto;
import ru.practicum.exception.UserAlreadyExistsException;
import ru.practicum.model.User;
import ru.practicum.repository.UserRepository;

import java.util.Collection;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    private final ModelMapper mapper;

    @Override
    public UserDto saveUser(NewUserRequest userCreateDto) {
        log.info("saveUser for {} started", userCreateDto);
        User user = mapper.map(userCreateDto, User.class);
        if (userRepository.existsByEmail(userCreateDto.getEmail())) {
            throw new UserAlreadyExistsException(String
                    .format("User with email %s already exists", userCreateDto.getEmail()));
        }
        userRepository.save(user);
        UserDto userDto = mapper.map(user, UserDto.class);
        log.info("saveUser for {} finished", userDto);
        return userDto;
    }

    @Override
    public void deleteUser(long id) {
        log.info("deleteUser for {} started", id);
        userRepository.deleteById(id);
        log.info("deleteUser for {} finished", id);
    }

    @Override
    public Collection<UserDto> getUsers(List<Long> ids, int from, int size) {
        log.info("getUsers for {} started", ids);
        PageRequest page = PageRequest.of(from / size , size);
        if (ids == null || ids.isEmpty()) {
            Page<User> users = userRepository.findAll(page);
            List<UserDto> userDtos = users.stream()
                    .map(user -> mapper.map(user, UserDto.class))
                    .toList();
            log.info("getUsers for {} finished", ids);
            return userDtos;
        }
        Page<User> usersByIds = userRepository.findAllByIdIn(ids, page);
        List<UserDto> userDtos = usersByIds.stream()
                .map(user -> mapper.map(user, UserDto.class))
                .toList();
        log.info("getUsers for {} finished", ids);
        return userDtos;
    }
}
