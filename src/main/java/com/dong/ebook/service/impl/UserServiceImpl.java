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
import java.util.Date;
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
    @Override
    public ResponseUserDto addUser(RequestUserDto requestUserDto) {
        ResponseUserDto responseUser = checkAddUser(requestUserDto);
        if(!responseUser.isSuccess()){
            return responseUser;
        }
        responseUser.setSuccess(false);

        User user = dozerBeanMapper.map(requestUserDto, User.class);
        //username不能一样
        User findUser = findUserByUsername(requestUserDto.getUsername());
        if (findUser != null) {
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
        //时间
        Date date = new Date();
        user.setCreateTime(date);
        user.setModifyTime(date);

        userDao.insertSelective(user);

        responseUser.setSuccess(true);
        return responseUser;
    }

    /**
     * 更新用户
     *
     * @param requestUserDto
     */
    @Override
    public ResponseCommonDto updateUserById(RequestUserDto requestUserDto) {
        ResponseCommonDto responseCommonDto = new ResponseCommonDto();
        responseCommonDto.setSuccess(false);
        try {
            ResponseCommonDto response = checkUpdateUser(requestUserDto);
            if(!response.isSuccess()){
                return response;
            }
        }catch (Exception e){
            logger.warn("updateUserById 有人直接提交了空数据");
            responseCommonDto.setErrorMsg("参数不能为空");
            return responseCommonDto;
        }

        //检查是否修改了密码
        String password = requestUserDto.getPassword();
        String newPassword = requestUserDto.getNewPassword();
        if(password != null && newPassword != null){
            //密码使用Bcrypt加密
            BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
            boolean matches = bCryptPasswordEncoder.matches(password, authUserService.getCurUser().getPassword());
            if(matches){
                String encodePassword = bCryptPasswordEncoder.encode(newPassword);
                requestUserDto.setPassword(encodePassword);
            } else{
                responseCommonDto.setErrorMsg("密码输入错误");
                return responseCommonDto;
            }
        }else if(password == null && newPassword != null){
            responseCommonDto.setErrorMsg("密码不能为空");
            return responseCommonDto;
        }else if(password != null && newPassword == null){
            responseCommonDto.setErrorMsg("新密码不能为空");
            return responseCommonDto;
        }

        User user = dozerBeanMapper.map(requestUserDto, User.class);
        updateUserById(user);

        responseCommonDto.setSuccess(true);
        return responseCommonDto;
    }

    /**
     * 查询用户
     *
     * @param id
     * @return
     */
    @Override
    public ResponseUserDto findUserDtoById(Long id) {
        User user = findUserById(id);
        ResponseUserDto responseUser = user2ResponseUser(user);
        return responseUser;
    }

    @Override
    public User findUserById(Long id) {
        return userDao.selectByPrimaryKey(id);
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

    public User findUserByUsername(String username) {
        UserExample userExample = new UserExample();
        UserExample.Criteria criteria = userExample.createCriteria();
        criteria.andUsernameEqualTo(username);
        List<User> users = userDao.selectByExample(userExample);
        if (users.size() == 0) {
            return null;
        } else if(users.size() > 1){
            throw new RuntimeException("findByUsername user.size=" + users.size());
        }
        return users.get(0);
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

        User user = new User();
        user.setId(userId);
        if(userStatus.equals("正常")){
            user.setIslock(true);
        } else if(userStatus.equals("加锁")){
            user.setIslock(false);
        }
        updateUserById(user);
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

        User user = new User();
        user.setId(userId);
        if(userRole.equals(UserRole.ADMINSHOW)){
            user.setRole(UserRole.USER);
        } else if(userRole.equals(UserRole.USERSHOW)){
            user.setRole(UserRole.ADMIN);
        }
        updateUserById(user);
        responseCommonDto.setSuccess(true);
        return responseCommonDto;
    }

    @Override
    public ResponseCommonDto saveAvatar(Long userId, String avatarUrl) {
        User user = new User();
        user.setId(userId);
        user.setAvatar(avatarUrl);
        updateUserById(user);

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

    public void updateUserById(User user) {
        //检查用户权限
        User curUser = authUserService.checkUser(user.getId(), null);

        user.setModifyTime(new Date());

        userDao.updateByPrimaryKeySelective(user);

        //修改自己信息立马更新缓存，修改别的用户不更新缓存
        if(curUser.getId().equals(user.getId())){
            authUserService.updateUser(user);
        }
    }

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

    public ResponseCommonDto changeCheck(Long userId){
        ResponseCommonDto responseCommonDto = new ResponseCommonDto();
        responseCommonDto.setSuccess(false);

        //不能改自己
        User curUser = authUserService.getCurUser();
        if(curUser.getIslock()){
            responseCommonDto.setErrorMsg("你已被上锁，不能修改任何信息");
            return responseCommonDto;
        }
        if(curUser.getId().equals(userId)){
            responseCommonDto.setErrorMsg("不允许修改自己状态");
            return responseCommonDto;
        }
        User user = findUserById(userId);
        if(user.getRole().equals(UserRole.ADMINSHOW) && curUser.getRole().equals(UserRole.ADMINSHOW)){
            responseCommonDto.setErrorMsg("管理员不允许修改管理员");
            return responseCommonDto;
        }
        responseCommonDto.setSuccess(true);
        return responseCommonDto;
    }

    /**
     * 由于profile页面form表单是直接提交的，所以会上传一些空字符串
     * @param requestUserDto
     */
    public ResponseCommonDto checkUpdateUser(RequestUserDto requestUserDto){
        ResponseCommonDto responseCommonDto = new ResponseCommonDto();
        responseCommonDto.setSuccess(false);
        User curUser = authUserService.getCurUser();
        if(!curUser.getId().equals(requestUserDto.getId())){
            responseCommonDto.setErrorMsg("不能串改他人信息");
            return responseCommonDto;
        }

        String username = requestUserDto.getUsername();
        String password = requestUserDto.getPassword();
        String newPassword = requestUserDto.getNewPassword();
        String email = requestUserDto.getEmail();
        String nickname = requestUserDto.getNickname();
        String phoneNumber = requestUserDto.getPhoneNumber();
        String sex = requestUserDto.getSex();

        if(username != null && !curUser.getUsername().equals(username)){
            responseCommonDto.setErrorMsg("不能修改账号");
            return responseCommonDto;
        }

        if(nickname.isEmpty()){
            requestUserDto.setNickname(null);
        }else{
            if(nickname.length() < 1 || nickname.length() > 10){
                responseCommonDto.setErrorMsg("昵称长度必须在1-10以内");
                return responseCommonDto;
            }
        }

        if(email.isEmpty()){
            requestUserDto.setEmail(null);
        }else{
            if(email.length() < 6 || email.length() > 20){
                responseCommonDto.setErrorMsg("邮箱长度必须在6-20以内");
                return responseCommonDto;
            }
        }

        if(phoneNumber.isEmpty()){
            requestUserDto.setPhoneNumber(null);
        }else{
            if(phoneNumber.length() < 3 || phoneNumber.length() > 30){
                responseCommonDto.setErrorMsg("电话号长度必须3-30以内");
                return responseCommonDto;
            }
        }

        if(password.isEmpty()){
            requestUserDto.setPassword(null);
        }

        if(newPassword.isEmpty()){
            requestUserDto.setNewPassword(null);
        }else{
            if(newPassword.length() < 6 || newPassword.length() > 20){
                responseCommonDto.setErrorMsg("新密码长度必须在6-20以内");
                return responseCommonDto;
            }
        }

        if(!("girl".equals(sex) || "boy".equals(sex))){
            responseCommonDto.setErrorMsg("性别错误");
            return responseCommonDto;
        }

        responseCommonDto.setSuccess(true);
        return responseCommonDto;
    }

    private ResponseUserDto checkAddUser(RequestUserDto requestUserDto){
        ResponseUserDto responseUser = new ResponseUserDto();
        responseUser.setSuccess(false);
        String username = requestUserDto.getUsername();
        String password = requestUserDto.getPassword();
        String email = requestUserDto.getEmail();
        Date birthday = requestUserDto.getBirthday();
        String nickname = requestUserDto.getNickname();
        String sex = requestUserDto.getSex();
        String phoneNumber = requestUserDto.getPhoneNumber();
        if(requestUserDto == null){
            responseUser.setErrorMsg("请求参数为空");
        }else if(username == null){
            responseUser.setErrorMsg("用户名为空");
            return responseUser;
        }else if(password == null){
            responseUser.setErrorMsg("密码为空");
            return responseUser;
        }else if(email == null){
            responseUser.setErrorMsg("邮箱为空");
            return responseUser;
        }else if(birthday == null){
            responseUser.setErrorMsg("生日为空");
            return responseUser;
        }else if(nickname == null){
            responseUser.setErrorMsg("昵称为空");
            return responseUser;
        }else if(sex == null){
            responseUser.setErrorMsg("性别为空");
            return responseUser;
        }else if(phoneNumber == null){
            responseUser.setErrorMsg("电话号为空");
            return responseUser;
        }

        if(username.length() < 6 || username.length() > 30){
            responseUser.setErrorMsg("用户名长度必须在6-30以内");
            return responseUser;
        }else if(password.length() < 6 || password.length() > 20){
            responseUser.setErrorMsg("密码长度必须在6-20以内");
            return responseUser;
        }else if(email.length() < 6 || email.length() > 20){
            responseUser.setErrorMsg("邮箱长度必须在6-20以内");
            return responseUser;
        }else if(nickname.length() < 1 || nickname.length() > 10){
            responseUser.setErrorMsg("昵称长度必须在1-10以内");
            return responseUser;
        }else if(!("girl".equals(sex) || "boy".equals(sex))){
            responseUser.setErrorMsg("性别错误");
            return responseUser;
        }else if(phoneNumber.length() < 3 || phoneNumber.length() > 30){
            responseUser.setErrorMsg("电话号长度必须3-30以内");
            return responseUser;
        }
        responseUser.setSuccess(true);
        return responseUser;
    }
}
