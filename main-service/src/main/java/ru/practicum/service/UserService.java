package ru.practicum.service;

import ru.practicum.dto.NewUserRequest;
import ru.practicum.dto.UserDto;

import java.util.Collection;
import java.util.List;

public interface UserService {

    UserDto saveUser(NewUserRequest userCreateDto);

    void deleteUser(long id);

    Collection<UserDto> getUsers(List<Long> ids, int from, int size);
}
