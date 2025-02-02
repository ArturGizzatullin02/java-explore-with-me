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
import ru.practicum.dto.UserCreateDto;
import ru.practicum.dto.UserFullDto;
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
    public UserFullDto createUser(@RequestBody @Valid UserCreateDto userCreateDto) {
        log.info("createUser for {} started", userCreateDto);
        UserFullDto userFullDto = userService.saveUser(userCreateDto);
        log.info("createUser for {} finished", userFullDto);
        return userFullDto;
    }

    @GetMapping("/admin/users")
    public Collection<UserFullDto> getUsersByIds(@RequestParam List<Long> ids,
                                               @RequestParam(defaultValue = "0") int from,
                                               @RequestParam(defaultValue = "10") int size) {
        log.info("getUsersByIds for {} {} {} started", ids, from, size);
        Collection<UserFullDto> userFullDtos = userService.getUsers(ids, from, size);
        log.info("getUsersByIds for {} {} {} finished", ids, from, size);
        return userFullDtos;
    }

    @DeleteMapping("/admin/users/{id}")
    public void deleteUser(@PathVariable long id) {
        log.info("deleteUser for {} started", id);
        userService.deleteUser(id);
        log.info("deleteUser for {} finished", id);
    }
}
