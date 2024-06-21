package ru.java_bot.anecdotal_bot.model;

import org.springframework.security.core.GrantedAuthority;

public enum UserAuthority implements GrantedAuthority {

    USER,
    MODERATOR,
    ADMIN;

    @Override
    public String getAuthority() {
        return this.name();
    }
}
