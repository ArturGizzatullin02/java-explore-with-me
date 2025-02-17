package ru.practicum.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.dto.NewUserRequest;
import ru.practicum.dto.UserDto;
import ru.practicum.service.UserService;

import java.util.Collection;
import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@Validated
@RequestMapping("/admin/users")
public class AdminUserController {

    private final UserService userService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto createUser(@RequestBody @Valid NewUserRequest userCreateDto) {
        log.info("createUser for {} started", userCreateDto);
        UserDto userDto = userService.saveUser(userCreateDto);
        log.info("createUser for {} finished", userDto);
        return userDto;
    }

    @GetMapping
    public Collection<UserDto> getUsersByIds(@RequestParam(required = false) List<Long> ids,
                                             @RequestParam(required = false, defaultValue = "0") int from,
                                             @RequestParam(required = false, defaultValue = "10") int size) {
        log.info("getUsersByIds for {} {} {} started", ids, from, size);
        Collection<UserDto> userDtos = userService.getUsers(ids, from, size);
        log.info("getUsersByIds for {} {} {} finished", ids, from, size);
        return userDtos;
    }

    @DeleteMapping("/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable long userId) {
        log.info("deleteUser for {} started", userId);
        userService.deleteUser(userId);
        log.info("deleteUser for {} finished", userId);
    }
}
