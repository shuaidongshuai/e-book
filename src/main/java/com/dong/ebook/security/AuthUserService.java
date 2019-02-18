package com.dong.ebook.security;

import com.dong.ebook.common.UserRole;
import com.dong.ebook.dao.UserDao;
import com.dong.ebook.model.User;
import com.dong.ebook.model.UserExample;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class AuthUserService implements UserDetailsService {

    @Autowired
    UserDao userDao;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserExample userExample = new UserExample();
        UserExample.Criteria criteria = userExample.createCriteria();
        criteria.andUsernameEqualTo(username);
        List<User> users = userDao.selectByExample(userExample);
        if (users.size() == 0) {
            throw new UsernameNotFoundException("User not found for name:" + username);
        }

        return new AuthUser(users.get(0));
    }

    /**
     * 获取当前用户
     * @return
     */
    public User getCurUser(){
        AuthUser authUser = null;
        SecurityContext context = SecurityContextHolder.getContext();
        if(context != null){
            Authentication authentication = context.getAuthentication();
            if(authentication != null){
                Object principal = authentication.getPrincipal();
                if(principal instanceof AuthUser){
                    authUser = (AuthUser)principal;
                }
            }
        }
        if(authUser == null){
            return null;
        }
        return authUser.getUser();
    }

    public User getCurUser(Long id, String username){
        return checkUser(id, username);
    }

    /**
     * 检查当前用户是否是表单上传中的用户
     * 管理员例外
     * @param id
     * @param username
     * @return
     */
    public User checkUser(Long id, String username){
        User curUser = getCurUser();
        if(UserRole.ADMIN.equals(curUser.getRole()) || UserRole.SUPERADMIN.equals(curUser.getRole())){
            return curUser;
        }
        if(!curUser.getId().equals(id) || username != null && !curUser.getUsername().equals(username)){
            throw new RuntimeException("当前用户id = " + curUser.getId() + "尝试获取别的用户数据");
        }
        return curUser;
    }

    /**
     * 更新当前用户的信息
     * @param user
     * @return
     */
    public void updateUser(User user){
        String password = user.getPassword();
        String sex = user.getSex();
        String email = user.getEmail();
        String phoneNumber = user.getPhoneNumber();
        Date birthday = user.getBirthday();
        String role = user.getRole();
        String nickname = user.getNickname();
        Boolean islock = user.getIslock();
        String avatar = user.getAvatar();
        String introduction = user.getIntroduction();
        User curUser = getCurUser();
        if(password != null){
            curUser.setPassword(password);
        }
        if(sex != null){
            curUser.setSex(sex);
        }
        if(email != null){
            curUser.setEmail(email);
        }
        if(phoneNumber != null){
            curUser.setPhoneNumber(phoneNumber);
        }
        if(birthday != null){
            curUser.setBirthday(birthday);
        }
        if(role != null){
            curUser.setRole(role);
        }
        if(nickname != null){
            curUser.setNickname(nickname);
        }
        if(islock != null){
            curUser.setIslock(islock);
        }
        if(avatar != null){
            curUser.setAvatar(avatar);
        }
        if(introduction != null){
            curUser.setIntroduction(introduction);
        }
    }
}
