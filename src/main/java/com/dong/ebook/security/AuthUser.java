package com.dong.ebook.security;

import com.dong.ebook.model.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class AuthUser implements UserDetails {
    private static final long serialVersionUID = 1L;
    private User user;

    public AuthUser(User user) {
        this.user = user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> list = new ArrayList<>();
        //SecurityExpressionRoot里面默认前缀ROLE_
        list.add(new SimpleGrantedAuthority(user.getRole()));
        return list;
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        //前端展示nickname不超过6个字符
        String nickname = user.getNickname();
        if(nickname.length() > 6){
            nickname = nickname.substring(0, 6) + "...";
        }
        return nickname;
    }

    /**
     * 当前账号是否已经过期
     * @return
     */
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    /**
     * 当前账号是否被锁
     * @return
     */
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    /**
     * 当前账号证书（密码）是否过期
     * @return
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    /**
     * 当前账号是否被禁用
     * @return
     */
    @Override
    public boolean isEnabled() {
        return true;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
