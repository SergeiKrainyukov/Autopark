package com.example.demo3.security;

import com.example.demo3.model.entity.ManagerEntity;
import com.example.demo3.repository.ManagersRepository;
import com.example.demo3.view.LoginView;
import com.vaadin.flow.spring.security.VaadinWebSecurity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import static com.example.demo3.common.Strings.ROLE_MANAGER;

@EnableWebSecurity
@Configuration
public class SecurityConfig extends VaadinWebSecurity {

    @Autowired
    private ManagersRepository managersRepository;

    private class SimpleInMemoryUserDetailsManager extends InMemoryUserDetailsManager {
        public SimpleInMemoryUserDetailsManager() {
            for (ManagerEntity managerEntity : managersRepository.findAll()) {
                createUser(ManagerEntity.withUsername(managerEntity.getUsername())
                        .password("{noop}" + managerEntity.getPassword())
                        .roles(ROLE_MANAGER)
                        .build());
            }
        }
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.httpBasic().and()
                .authorizeRequests().antMatchers(
                        "/enterprises/**",
                        "/createVehicle/**",
                        "/deleteVehicle/**",
                        "/updateVehicle/**",
                        "/createRandomVehicles/**",
                        "/createEnterprise/**",
                        "/geopoints",
                        "/trip",
                        "/trips",
                        "/generateTrip",
                        "/report"
                ).hasRole(ROLE_MANAGER)
                .and()
                .csrf()
                .ignoringRequestMatchers(
                        new AntPathRequestMatcher("/createVehicle/**"),
                        new AntPathRequestMatcher("/deleteVehicle/**"),
                        new AntPathRequestMatcher("/updateVehicle/**"),
                        new AntPathRequestMatcher("/createRandomVehicles/**"),
                        new AntPathRequestMatcher("/createEnterprise/**"),
                        new AntPathRequestMatcher("/geopoints/**"),
                        new AntPathRequestMatcher("/trip"),
                        new AntPathRequestMatcher("/trips"),
                        new AntPathRequestMatcher("/generateTrip"),
                        new AntPathRequestMatcher("/report")
                );

        super.configure(http);

        setLoginView(http, LoginView.class);
    }

    @Bean
    public InMemoryUserDetailsManager enterprisesService() {
        return new SimpleInMemoryUserDetailsManager();
    }
}
