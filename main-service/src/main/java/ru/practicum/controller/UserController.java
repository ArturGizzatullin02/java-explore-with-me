package ru.practicum.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import ru.practicum.dto.NewUserRequest;
import ru.practicum.dto.UserDto;
import ru.practicum.service.UserService;

import java.util.Collection;
import java.util.List;

@Controller
@Slf4j
@RequiredArgsConstructor
@Validated
public class UserController {

    private final UserService userService;

    @PostMapping("/admin/users")
    public UserDto createUser(@RequestBody @Valid NewUserRequest userCreateDto) {
        log.info("createUser for {} started", userCreateDto);
        UserDto userDto = userService.saveUser(userCreateDto);
        log.info("createUser for {} finished", userDto);
        return userDto;
    }

    @GetMapping("/admin/users")
    public Collection<UserDto> getUsersByIds(@RequestParam List<Long> ids,
                                             @RequestParam(defaultValue = "0") int from,
                                             @RequestParam(defaultValue = "10") int size) {
        log.info("getUsersByIds for {} {} {} started", ids, from, size);
        Collection<UserDto> userDtos = userService.getUsers(ids, from, size);
        log.info("getUsersByIds for {} {} {} finished", ids, from, size);
        return userDtos;
    }

    @DeleteMapping("/admin/users/{id}")
    public void deleteUser(@PathVariable long id) {
        log.info("deleteUser for {} started", id);
        userService.deleteUser(id);
        log.info("deleteUser for {} finished", id);
    }
}
