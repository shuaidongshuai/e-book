package com.dong.ebook.config;

import com.dong.ebook.security.AuthUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.access.AccessDeniedHandler;

@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    private static final String KEY = "shuaidong";


    @Autowired(required = false)
    private AccessDeniedHandler accessDeniedHandler;

    @Autowired
    private AuthUserService authUserService;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable()
                .authorizeRequests()
                    .antMatchers("/", "/register", "/login", "/userspace/**", "/blog/**", "/error", "/search/**").permitAll()
                    .antMatchers("/user/**").hasAnyRole("USER", "ADMIN", "SUPERADMIN")
                    .antMatchers("/admin/**").hasAnyRole("ADMIN", "SUPERADMIN")
                    .antMatchers("/**").hasAnyRole("SUPERADMIN")
                    .anyRequest().authenticated()
                .and()
                .formLogin()
                    .loginPage("/login")
                    .permitAll()
                .and()
                .logout()
                    .permitAll()
                .and()
                    .rememberMe()
                    .key(KEY)
                .and()
                    .exceptionHandling().accessDeniedHandler(accessDeniedHandler);
    }

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        //认证信息存储在内存
        /*
        auth.inMemoryAuthentication().passwordEncoder(new BCryptPasswordEncoder()).withUser("dong")
                .password(new BCryptPasswordEncoder().encode("dong")).roles("admin").and()
                .passwordEncoder(new BCryptPasswordEncoder()).withUser("anthony")
                .password(new BCryptPasswordEncoder().encode("dong")).roles("super_admin");
        */
        //存储在数据库中
        auth.userDetailsService(authUserService).passwordEncoder(new BCryptPasswordEncoder());
    }

    @Override
    public void configure(WebSecurity web) {
        //解决静态资源被拦截的问题
        web.ignoring().antMatchers("/css/**", "/fonts/**", "/images/**", "/js/**", "/favicon.ico", "/mavonEditor/**");
    }
}
