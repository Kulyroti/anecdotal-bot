package ru.java_bot.anecdotal_bot.repository;

import org.springframework.data.repository.CrudRepository;
import ru.java_bot.anecdotal_bot.model.UserRole;

public interface UserRoleRepository extends CrudRepository<UserRole, Long> {
}
