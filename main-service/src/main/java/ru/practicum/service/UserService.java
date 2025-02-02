package ru.practicum.service;

import ru.practicum.dto.UserCreateDto;
import ru.practicum.dto.UserFullDto;

import java.util.Collection;
import java.util.List;

public interface UserService {

    UserFullDto saveUser(UserCreateDto userCreateDto);

    void deleteUser(long id);

    Collection<UserFullDto> getUsers(List<Long> ids, int from, int size);
}
