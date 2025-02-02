package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.dto.UserCreateDto;
import ru.practicum.dto.UserFullDto;
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
    public UserFullDto saveUser(UserCreateDto userCreateDto) {
        log.info("saveUser for {} started", userCreateDto);
        User user = mapper.map(userCreateDto, User.class);
        userRepository.save(user);
        UserFullDto userFullDto = mapper.map(user, UserFullDto.class);
        log.info("saveUser for {} finished", userFullDto);
        return userFullDto;
    }

    @Override
    public void deleteUser(long id) {
        log.info("deleteUser for {} started", id);
        userRepository.deleteById(id);
        log.info("deleteUser for {} finished", id);
    }

    @Override
    public Collection<UserFullDto> getUsers(List<Long> ids, int from, int size) {
        log.info("getUsers for {} started", ids);
        PageRequest page = PageRequest.of(from > 0 ? from / size : 0, size);
        Page<User> usersByIds = userRepository.findAllByIdIn(ids, page);
        List<UserFullDto> userFullDtos = usersByIds.stream()
                .map(user -> mapper.map(user, UserFullDto.class))
                .toList();
        log.info("getUsers for {} finished", ids);
        return userFullDtos;
    }
}
