package ru.java_bot.anecdotal_bot.service;

import ru.java_bot.anecdotal_bot.model.UserAuthority;

import java.util.List;

public interface UserService {

    void registration(String username, String password);

    void changeUserRole(Long userId, UserAuthority newAuthority);

    List<UserAuthority> getUserRoles(Long userId);

}
