package com.dong.ebook.service.impl;

import com.dong.ebook.common.UserRole;
import com.dong.ebook.dao.UserDao;
import com.dong.ebook.dto.*;
import com.dong.ebook.model.User;
import com.dong.ebook.model.UserExample;
import com.dong.ebook.security.AuthUserService;
import com.dong.ebook.service.UserService;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.log4j.Logger;
import org.dozer.DozerBeanMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {
    private static Logger logger = Logger.getLogger(UserServiceImpl.class);

    @Autowired
    private DozerBeanMapper dozerBeanMapper;

    @Autowired
    private UserDao userDao;

    @Autowired
    private AuthUserService authUserService;

    /**
     * 添加用户
     *
     * @param requestUserDto
     * @return
     */
    public ResponseUserDto add(RequestUserDto requestUserDto) {
        ResponseUserDto responseUser = new ResponseUserDto();
        responseUser.setSuccess(false);
        if (requestUserDto == null) {
            responseUser.setErrorMsg("RequestUserDto is null");
            return responseUser;
        }
        User user = dozerBeanMapper.map(requestUserDto, User.class);
        //username不能一样
        ResponseUserDto responseUserDto = findByUsername(requestUserDto.getUsername());
        if (responseUserDto.getUser() != null) {
            responseUser.setErrorMsg("用户名已存在");
            return responseUser;
        }
        //默认角色USER
        user.setRole(UserRole.USER);
        //不加锁
        user.setIslock(false);
        //密码使用Bcrypt加密
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        String encodePassword = bCryptPasswordEncoder.encode(user.getPassword());
        user.setPassword(encodePassword);
        //设置默认头像
        user.setAvatar("/images/avatarDefault.png");

        userDao.insertSelective(user);

        responseUser.setSuccess(true);
        return responseUser;
    }

    /**
     * 删除用户（只有管理员有权限）
     * super_admin -> admin -> user
     *
     * @param id
     */
    public ResponseCommonDto delete(Long id) {
        userDao.deleteByPrimaryKey(id);
        ResponseCommonDto responseCommonDto = new ResponseCommonDto();
        responseCommonDto.setSuccess(true);
        return responseCommonDto;
    }

    /**
     * 更新用户
     *
     * @param requestUserDto
     */
    public ResponseCommonDto updateById(RequestUserDto requestUserDto) {
        //检查用户权限
        User curUser = authUserService.checkUser(requestUserDto.getId(), null);

        User user = dozerBeanMapper.map(requestUserDto, User.class);
        userDao.updateByPrimaryKeySelective(user);

        //修改自己信息立马更新缓存，修改别的用户不更新缓存
        if(curUser.getId().equals(user.getId())){
            authUserService.updateUser(user);
        }

        ResponseCommonDto responseCommonDto = new ResponseCommonDto();
        responseCommonDto.setSuccess(true);
        return responseCommonDto;
    }

    /**
     * 查询用户
     *
     * @param id
     * @return
     */
    public ResponseUserDto findById(Long id) {
        User user = userDao.selectByPrimaryKey(id);
        ResponseUserDto responseUser = user2ResponseUser(user);
        return responseUser;
    }

    @Override
    public ResponseUserDto findByNickname(String nickname) {
        UserExample userExample = new UserExample();
        UserExample.Criteria criteria = userExample.createCriteria();
        criteria.andNicknameEqualTo(nickname);
        List<User> users = userDao.selectByExample(userExample);
        if (users.size() == 0) {
            return new ResponseUserDto();
        }
        if (users.size() > 1) {
            logger.error("findByNickname user.size=" + users.size());
            return new ResponseUserDto();
        }
        ResponseUserDto responseUser = user2ResponseUser(users.get(0));
        return responseUser;
    }

    @Override
    public ResponseUserDto findByUsername(String username) {
        ResponseUserDto responseUser = new ResponseUserDto();
        responseUser.setSuccess(false);
        UserExample userExample = new UserExample();
        UserExample.Criteria criteria = userExample.createCriteria();
        criteria.andUsernameEqualTo(username);
        List<User> users = userDao.selectByExample(userExample);
        if (users.size() > 1) {
            logger.error("findByUsername user.size=" + users.size());
            responseUser.setErrorMsg("user number=" + users.size());
            return responseUser;
        }else if(users.size() == 0){
            responseUser.setUser(null);
        } else {
            responseUser = user2ResponseUser(users.get(0));
        }
        return responseUser;
    }

    /**
     * 根据权限查询用户
     * @param pageNum
     * @param pageSize
     * @return
     */
    @Override
    public ResponseUserListDto managerFindList(int pageNum, int pageSize) {
        return managerFindList(pageNum, pageSize, null);
    }

    @Override
    public ResponseUserListDto managerFindList(int pageNum, int pageSize, String username){
        ResponseUserListDto responseUserListDto = new ResponseUserListDto();
        responseUserListDto.setSuccess(false);

        Page<User> page = PageHelper.startPage(pageNum, pageSize);
        UserExample userExample = new UserExample();
        UserExample.Criteria criteria = userExample.createCriteria();
        //不能查询超级管理员
        criteria.andRoleNotEqualTo(UserRole.SUPERADMIN);
        //模糊查询
        if(username != null && !username.isEmpty()){
            criteria.andUsernameLike(username);
        }
        //根据权限获取list
        User curUser = authUserService.getCurUser();
        if(curUser == null){
            responseUserListDto.setErrorMsg("未登录");
            return responseUserListDto;
        }
        if (UserRole.ADMIN.equals(curUser.getRole())) {
            criteria.andRoleNotEqualTo(UserRole.ADMIN);
        } else if(UserRole.USER.equals(curUser.getRole())) {
            responseUserListDto.setErrorMsg("权限不够");
            return responseUserListDto;
        }
        userDao.selectByExample(userExample);
        PageInfo pageInfo = new PageInfo(page.getResult());
        return assembleResponseUserListDto(pageInfo);
    }

    @Override
    public ResponseUserDto user2ResponseUser(User user) {
        ResponseUserDto responseUserDto = new ResponseUserDto();
        responseUserDto.setSuccess(false);
        if(user == null){
            return responseUserDto;
        }
        UserDto userDto = user2dto(user);
        responseUserDto.setUser(userDto);
        responseUserDto.setSuccess(true);
        return responseUserDto;
    }

    @Override
    public ResponseUserListDto assembleResponseUserListDto(PageInfo pageInfo) {
        List<UserDto> userDtos = users2dto(pageInfo.getList());

        pageInfo.setList(userDtos);

        ResponseUserListDto responseUserListDto = new ResponseUserListDto();
        responseUserListDto.setPageInfo(pageInfo);
        responseUserListDto.setSuccess(true);
        return responseUserListDto;
    }

    @Override
    public ResponseCommonDto changeStatus(Long userId, String userStatus) {
        if(userId == null || userStatus == null){
            throw new RuntimeException("changeStatus parameter is null");
        }

        ResponseCommonDto responseCommonDto = changeCheck(userId);
        if(!responseCommonDto.isSuccess()){
            return responseCommonDto;
        }

        RequestUserDto requestUserDto = new RequestUserDto();
        requestUserDto.setId(userId);
        if(userStatus.equals("正常")){
            requestUserDto.setIslock(true);
        } else if(userStatus.equals("加锁")){
            requestUserDto.setIslock(false);
        }
        updateById(requestUserDto);
        responseCommonDto.setSuccess(true);
        return responseCommonDto;
    }

    @Override
    public ResponseCommonDto changeRole(Long userId, String userRole) {
        if(userId == null || userRole == null){
            throw new RuntimeException("changeRole parameter is null");
        }

        ResponseCommonDto responseCommonDto = changeCheck(userId);
        if(!responseCommonDto.isSuccess()){
            return responseCommonDto;
        }

        RequestUserDto requestUserDto = new RequestUserDto();
        requestUserDto.setId(userId);
        if(userRole.equals(UserRole.ADMINSHOW)){
            requestUserDto.setRole(UserRole.USER);
        } else if(userRole.equals(UserRole.USERSHOW)){
            requestUserDto.setRole(UserRole.ADMIN);
        }
        updateById(requestUserDto);
        responseCommonDto.setSuccess(true);
        return responseCommonDto;
    }

    @Override
    public ResponseCommonDto saveAvatar(Long userId, String avatarUrl) {
        User user = new User();
        user.setId(userId);
        user.setAvatar(avatarUrl);
        userDao.updateByPrimaryKeySelective(user);

        ResponseCommonDto responseCommonDto = new ResponseCommonDto();
        responseCommonDto.setSuccess(true);
        return responseCommonDto;
    }

    @Override
    public UserDto user2dto(User user) {
        UserDto userDto = dozerBeanMapper.map(user, UserDto.class);
        if (user.getIslock()) {
            userDto.setStatus("加锁");
        } else {
            userDto.setStatus("正常");
        }
        if(UserRole.USER.equals(user.getRole())){
            userDto.setRole(UserRole.USERSHOW);
        } else if (UserRole.ADMIN.equals(user.getRole())) {
            userDto.setRole(UserRole.ADMINSHOW);
        }
        return userDto;
    }

    @Override
    public List<UserDto> users2dto(List<User> users) {
        List<UserDto> userDtos = new ArrayList<>();
        for (User user : users) {
            UserDto userDto = user2dto(user);
            userDtos.add(userDto);
        }
        return userDtos;
    }

    public ResponseCommonDto changeCheck(Long userId){
        ResponseCommonDto responseCommonDto = new ResponseCommonDto();
        responseCommonDto.setSuccess(false);

        //不能改自己
        User curUser = authUserService.getCurUser();
        if(curUser == null){
            responseCommonDto.setErrorMsg("未登录");
            return responseCommonDto;
        }
        if(curUser.getId().equals(userId)){
            responseCommonDto.setErrorMsg("不允许修改自己状态");
            return responseCommonDto;
        }
        ResponseUserDto responseUserDto = findById(userId);
        if(responseUserDto.getUser().getRole().equals(UserRole.ADMINSHOW) && curUser.getRole().equals(UserRole.ADMINSHOW)){
            responseCommonDto.setErrorMsg("管理员不允许修改管理员");
            return responseCommonDto;
        }
        responseCommonDto.setSuccess(true);
        return responseCommonDto;
    }
}
