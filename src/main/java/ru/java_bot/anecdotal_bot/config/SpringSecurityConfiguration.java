package ru.java_bot.anecdotal_bot.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import ru.java_bot.anecdotal_bot.model.UserAuthority;

@Slf4j
@Configuration
@EnableWebSecurity
public class SpringSecurityConfiguration {
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(expressionInterceptUrlRegistry -> expressionInterceptUrlRegistry.requestMatchers("/registration", "/login").permitAll() //Разрешить все запросы на /login
                        .requestMatchers(HttpMethod.POST, "/jokes").hasAuthority(UserAuthority.USER.getAuthority())
                        .requestMatchers(HttpMethod.GET, "/jokes/**").permitAll()//Разрешить GET запросы для всех
                        .requestMatchers(HttpMethod.DELETE,"/jokes").hasAuthority(UserAuthority.MODERATOR.getAuthority())
                        .requestMatchers(HttpMethod.PUT,"/jokes").hasAuthority(UserAuthority.MODERATOR.getAuthority())
                        .anyRequest().hasAuthority(UserAuthority.ADMIN.getAuthority()))
                .formLogin(Customizer.withDefaults()) //Стандартная форма логина
 .csrf(AbstractHttpConfigurer::disable); //Выключение защиты cross-site refrences - чтобы вы могли слать запросы из браузеров (Postman считается браузером)
        return http.build();
    }
    //Стандартный шифровальщик паролей
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
