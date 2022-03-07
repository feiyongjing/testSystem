package com.feiyongjing.exam.controller;

import com.feiyongjing.exam.enity.Response;
import com.feiyongjing.exam.enity.User;
import com.feiyongjing.exam.exception.HttpException;
import com.feiyongjing.exam.service.UserContext;
import com.feiyongjing.exam.service.UserService;
import com.feiyongjing.exam.utils.Encryption;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import static org.springframework.http.HttpStatus.*;

/**
 * 注册登录登出功能
 */
@RestController
@RequestMapping("/api/v1")
public class AuthController {
    private static Pattern TEL_PATTERN = Pattern.compile("^1\\d{10}$");
    private static Pattern PASSWORD_PATTERN = Pattern.compile("^\\w{6,16}$");
    private UserService userService;
    private Encryption encryption;

    @Autowired
    public AuthController(UserService userService, Encryption encryption) {
        this.userService = userService;
        this.encryption = encryption;
    }

    /**
     *
     * @return 登录页面
     */
    @GetMapping("/login.html")
    public ModelAndView loginModel() {
        Map<String, Object> model = new HashMap<>();
        model.put("item", "//localhost:8080/api/v1/logon.html");
        model.put("item1", "用户注册");
        model.put("item2", "登录");
        model.put("item3", "//localhost:8080/api/v1/login");
        return new ModelAndView("index", model);
    }

    /**
     *
     * @return 登出页面
     */
    @GetMapping("/logon.html")
    public ModelAndView logonModel() {
        Map<String, Object> model = new HashMap<>();
        model.put("item", "//localhost:8080/api/v1/login.html");
        model.put("item1", "用户登录");
        model.put("item2", "注册用户");
        model.put("item3", "//localhost:8080/api/v1/logon");
        return new ModelAndView("index", model);
    }

    /**
     * 注册功能
     *
     * @param usernameAndPassword 用户名和密码
     * @return 用户注册信息
     */
    @PostMapping("/logon")
    public Response<User> logon(@RequestBody UsernameAndPassword usernameAndPassword) {
        try {
            paramCheck(usernameAndPassword);
            String encryptedPassword = encryption.String2SHA256StrJava(usernameAndPassword.password);
            User user = new User(usernameAndPassword.getUsername(), encryptedPassword, "默认图像");
            userService.createUser(user);
            return Response.of(CREATED.value(), "注册成功", userService.getUserByUsername(usernameAndPassword.username));
        } catch (HttpException e) {
            return Response.of(BAD_REQUEST.value(), e.getMessage(), null);
        } catch (DuplicateKeyException e) {
            return Response.of(BAD_REQUEST.value(), "用户名已注册", null);
        }
    }

    private void paramCheck(UsernameAndPassword usernameAndPassword) {
        if (usernameAndPassword.username == null) {
            throw HttpException.preconditionFailed("用户名不能是空");
        }
        if (usernameAndPassword.password == null) {
            throw HttpException.preconditionFailed("密码不能是空");
        }
        if (!TEL_PATTERN.matcher(usernameAndPassword.username).matches()) {
            throw HttpException.preconditionFailed("用户名不符合格式");
        }
        if (!PASSWORD_PATTERN.matcher(usernameAndPassword.password).matches()) {
            throw HttpException.preconditionFailed("密码不符合格式");
        }
    }

    /**
     * 登录功能
     *
     * @param usernameAndPassword 用户名和密码
     * @return 用户登录信息
     */
    @PostMapping("/login")
    public Response<User> login(@RequestBody UsernameAndPassword usernameAndPassword) {
        try {
            paramCheck(usernameAndPassword);
            String username = usernameAndPassword.getUsername();
            String encryptedPassword = encryption.String2SHA256StrJava(usernameAndPassword.password);
            UsernamePasswordToken token = new UsernamePasswordToken(usernameAndPassword.getUsername(), encryptedPassword);
            token.setRememberMe(true);
            SecurityUtils.getSubject().login(token);
            return Response.of(OK.value(), "登录成功", userService.getUserByUsername(username));
        } catch (HttpException e) {
            return Response.of(FORBIDDEN.value(), e.getMessage(), null);
        } catch (IncorrectCredentialsException e) {
            return Response.of(FORBIDDEN.value(), "用户名密码不匹配", null);
        }
    }

    /**
     * 登录状态查询
     *
     * @return 登录状态信息
     */
    @GetMapping("/status")
    public Response<User> loginStatus() {
        User user = UserContext.getCurrentUser();
        if (user == null) {
            return Response.of(OK.value(), "未登录", null);
        } else {
            return Response.of(OK.value(), "已登录", user);
        }
    }

    /**
     * 退出登录功能
     */
    @PostMapping("/logout")
    public void logout() {
        SecurityUtils.getSubject().logout();
    }


xxxx
ff
xxx

//    @GetMapping("/user")
//    public Response<LoginResponse> getLoginUser() {
//        User user = UserContext.getCurrentUser();
//        long billTotalNumber = billService.getTotalBillNumberByUserId(user.getId());
//        int totalNumberOfDays = userService.getUserFirstCreatedBillTimeByUserId(user.getId());
//        return Response.of(OK.value(), "成功获取资源", new LoginResponse(billTotalNumber, totalNumberOfDays, user));
//    }

    public static class UsernameAndPassword {
        private String username;
        private String password;

        public UsernameAndPassword(String username, String password) {
            this.username = username;
            this.password = password;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }
    }
}
