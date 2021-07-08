package com.feiyongjing.living_bill.controller;

import com.feiyongjing.living_bill.enity.LoginResponse;
import com.feiyongjing.living_bill.enity.Response;
import com.feiyongjing.living_bill.enity.User;
import com.feiyongjing.living_bill.exception.HttpException;
import com.feiyongjing.living_bill.service.AuthService;
import com.feiyongjing.living_bill.service.BillService;
import com.feiyongjing.living_bill.service.UserContext;
import com.feiyongjing.living_bill.service.UserService;
import com.feiyongjing.living_bill.utils.Encryption;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.regex.Pattern;

import static org.springframework.http.HttpStatus.*;

@RestController
@RequestMapping("/api/v1")
public class AuthController {
    private static Pattern TEL_PATTERN = Pattern.compile("^1\\d{10}$");
    private static Pattern PASSWORD_PATTERN = Pattern.compile("^\\w{6,16}$");
    private AuthService authService;
    private UserService userService;
    private BillService billService;
    private Encryption encryption;

    @Autowired
    public AuthController(AuthService authService, UserService userService, BillService billService, Encryption encryption) {
        this.authService = authService;
        this.userService = userService;
        this.billService = billService;

        this.encryption = encryption;
    }

    @PostMapping("/logon")
    public Response<User> logon(@RequestBody UsernameAndPassword usernameAndPassword, HttpServletResponse response) {
        try {
            paramCheck(usernameAndPassword);
//            String encryptedPassword = bCryptPasswordEncoder.encode(usernameAndPassword.getPassword());
            String encryptedPassword = encryption.String2SHA256StrJava(usernameAndPassword.password);
            User user = new User(usernameAndPassword.getUsername(), encryptedPassword, "默认图像");
            authService.createUser(user);
            return Response.of(CREATED.value(), "注册成功", userService.getUserByUsername(usernameAndPassword.username));
        } catch (HttpException e) {
            return Response.of(BAD_REQUEST.value(), e.getMessage(), null);
        } catch (DuplicateKeyException e) {
            return Response.of(BAD_REQUEST.value(), "用户名已注册", null);
        }
    }

    private void paramCheck(UsernameAndPassword usernameAndPassword) {
        if (usernameAndPassword.username == null) {
            throw HttpException.preconditionFailed("用户名为空");
        }
        if (usernameAndPassword.password == null) {
            throw HttpException.preconditionFailed("密码为空");
        }
        if (!TEL_PATTERN.matcher(usernameAndPassword.username).matches()) {
            throw HttpException.preconditionFailed("用户名不符合格式");
        }
        if (!PASSWORD_PATTERN.matcher(usernameAndPassword.password).matches()) {
            throw HttpException.preconditionFailed("密码不符合格式");
        }
    }

    @PostMapping("/login")
    public Response<User> login(@RequestBody UsernameAndPassword usernameAndPassword) {
        try {
            paramCheck(usernameAndPassword);
            String username = usernameAndPassword.getUsername();
//            String encryptedPassword = bCryptPasswordEncoder.encode(usernameAndPassword.getPassword());
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

    @GetMapping("/status")
    public Response<User> loginStatus() {
        User user = UserContext.getCurrentUser();
        if (user == null) {
            return Response.of(OK.value(), "未登录", null);
        } else {
            return Response.of(OK.value(), "已登录", user);
        }
    }

    @GetMapping("/logout")
    public void logout() {
        SecurityUtils.getSubject().logout();
    }

    @GetMapping("/user")
    public Response<LoginResponse> getLoginUser() {
        User user = UserContext.getCurrentUser();
        long billTotalNumber = billService.getTotalBillNumberByUserId(user.getId());
        int totalNumberOfDays = userService.getUserFirstCreatedBillTimeByUserId(user.getId());
        return Response.of(OK.value(), "成功获取资源", new LoginResponse(billTotalNumber, totalNumberOfDays, user));
    }

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
