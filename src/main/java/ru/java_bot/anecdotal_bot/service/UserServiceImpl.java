package ru.java_bot.anecdotal_bot.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.java_bot.anecdotal_bot.exception.UsernameAlreadyExistsException;
import ru.java_bot.anecdotal_bot.model.User;
import ru.java_bot.anecdotal_bot.model.UserAuthority;
import ru.java_bot.anecdotal_bot.model.UserRole;
import ru.java_bot.anecdotal_bot.repository.UserRepository;
import ru.java_bot.anecdotal_bot.repository.UserRoleRepository;

import java.util.List;
import java.util.stream.Collectors;

@Transactional
@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserDetailsService, UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserRoleRepository userRoleRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException(username));
    }


    @Override
    public void registration(String username, String password) {
        if (userRepository.findByUsername(username).isEmpty()) {
            User user = userRepository.save(
                    new User()
                            .setId(null)
                            .setUsername(username)
                            .setPassword(passwordEncoder.encode(password))
                            .setLocked(false)
                            .setExpired(false)
                            .setEnabled(true)
            );
            userRoleRepository.save(new UserRole(null, UserAuthority.USER, user));
        } else {
            throw new UsernameAlreadyExistsException();
        }
    }

    @Override
    public void changeUserRole(Long userId, UserAuthority newAuthority) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

        UserRole userRole = user.getUserRoles().get(0); // предполагаем, что у пользователя всегда одна роль
        userRole.setUserAuthority(newAuthority);

        userRepository.save(user);
    }

    @Override
    public List<UserAuthority> getUserRoles(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

        return user.getUserRoles().stream().map(UserRole::getUserAuthority).collect(Collectors.toList());
    }
}
