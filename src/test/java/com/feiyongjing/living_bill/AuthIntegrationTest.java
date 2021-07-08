package com.feiyongjing.living_bill;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.feiyongjing.living_bill.controller.AuthController;
import com.feiyongjing.living_bill.controller.CostTypeController;
import com.feiyongjing.living_bill.enity.CostType;
import com.feiyongjing.living_bill.enity.LoginResponse;
import com.feiyongjing.living_bill.enity.Response;
import com.feiyongjing.living_bill.enity.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.UnsupportedEncodingException;

import static com.feiyongjing.living_bill.controller.AuthController.UsernameAndPassword;
import static com.feiyongjing.living_bill.utils.InitialCostType.incomeTypeArray;
import static com.feiyongjing.living_bill.utils.InitialCostType.spendingTypeArray;
import static org.springframework.http.HttpStatus.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = LivingBillApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = {"spring.config.location=classpath:test-application.yml"})
public class AuthIntegrationTest extends AbstractIntegrationTest {

    @Test
    public void testLogonSuccessful() throws JsonProcessingException, UnsupportedEncodingException {
        UsernameAndPassword usernameAndPassword = new UsernameAndPassword("13966666664", "444444");

        HttpResponse httpResponse = doHttpResponse("/api/v1/logon", "POST", usernameAndPassword, null);

        int statusCode = httpResponse.getCode();
        Response<User> logonResponse = objectMapper.readValue(httpResponse.getBody(), new TypeReference<Response<User>>() {
        });

        Assertions.assertEquals(OK.value(), statusCode);
        Assertions.assertEquals(CREATED.value(), logonResponse.getStatusCode());
        Assertions.assertEquals("注册成功", logonResponse.getMessage());
        Assertions.assertEquals(usernameAndPassword.getUsername()
                , logonResponse.getDate().getName());
        Assertions.assertEquals("默认图像", logonResponse.getDate().getAvatarUrl());

        String cookie = loginAndGetCookie(usernameAndPassword).cookie;

        Assertions.assertEquals(incomeTypeArray.length, getAllCostTypeByBillType("收入", cookie).size());
        Assertions.assertEquals(spendingTypeArray.length, getAllCostTypeByBillType("支出", cookie).size());
    }


    @Test
    public void testLogonFailed() throws JsonProcessingException {
        UsernameAndPassword usernameAndPassword = new UsernameAndPassword("13966666661", "111111");

        usernameAndPassword.setUsername(null);
        testLogonFailed(usernameAndPassword, "用户名为空");
        usernameAndPassword.setUsername("139666666633");
        testLogonFailed(usernameAndPassword, "用户名不符合格式");
        usernameAndPassword.setUsername("13966666661");
        testLogonFailed(usernameAndPassword, "用户名已注册");

        usernameAndPassword.setPassword(null);
        testLogonFailed(usernameAndPassword, "密码为空");
        usernameAndPassword.setPassword("00000000000000011");
        testLogonFailed(usernameAndPassword, "密码不符合格式");
    }

    public void testLogonFailed(UsernameAndPassword usernameAndPassword, String message) throws JsonProcessingException {
        HttpResponse httpResponse = doHttpResponse("/api/v1/logon", "POST", usernameAndPassword, null);

        int statusCode = httpResponse.getCode();
        Response<User> response = objectMapper.readValue(httpResponse.getBody(), new TypeReference<Response<User>>() {
        });

        Assertions.assertEquals(OK.value(), statusCode);
        Assertions.assertEquals(BAD_REQUEST.value(), response.getStatusCode());
        Assertions.assertEquals(message, response.getMessage());
        Assertions.assertNull(response.getDate());
    }

    @Test
    public void testLoginSuccessful() throws JsonProcessingException {
        UsernameAndPassword usernameAndPassword = new UsernameAndPassword("13966666661", "111111");
        Response<User> response = loginAndGetCookie(usernameAndPassword).response;

        Assertions.assertEquals(OK.value(), response.getStatusCode());
        Assertions.assertEquals("登录成功", response.getMessage());
        Assertions.assertEquals(usernameAndPassword.getUsername()
                , response.getDate().getName());
        Assertions.assertEquals("默认图像", response.getDate().getAvatarUrl());

    }

    @Test
    public void testLoginFailed() throws JsonProcessingException {
        UsernameAndPassword usernameAndPassword = new UsernameAndPassword("13966666661", "000000");
        usernameAndPassword.setUsername(null);
        testLoginFailed(usernameAndPassword, "用户名为空");
        usernameAndPassword.setUsername("139666666611");
        testLoginFailed(usernameAndPassword, "用户名不符合格式");
        usernameAndPassword.setUsername("13966666661");

        usernameAndPassword.setPassword(null);
        testLoginFailed(usernameAndPassword, "密码为空");
        usernameAndPassword.setPassword("0000 0000 0000 0000");
        testLoginFailed(usernameAndPassword, "密码不符合格式");
        usernameAndPassword.setPassword("000000");
        testLoginFailed(usernameAndPassword, "用户名密码不匹配");

    }

    public void testLoginFailed(UsernameAndPassword usernameAndPassword, String message) throws JsonProcessingException {
        Response<User> response = login(usernameAndPassword, null).response;

        Assertions.assertEquals(FORBIDDEN.value(), response.getStatusCode());
        Assertions.assertEquals(message, response.getMessage());
        Assertions.assertNull(response.getDate());
    }

    @Test
    public void testLogout() throws JsonProcessingException {
        UsernameAndPassword usernameAndPassword = new UsernameAndPassword("13966666661", "111111");
        String cookie = loginAndGetCookie(usernameAndPassword).cookie;

        HttpResponse httpResponse = doHttpResponse("/api/v1/logout", "GET", null, cookie);

        int statusCode = httpResponse.getCode();
        Assertions.assertEquals(OK.value(), statusCode);

        Assertions.assertFalse(isLogin(cookie));
        Assertions.assertFalse(isLogin(null));
    }

    @Test
    public void getLoginUserTest() throws JsonProcessingException {
        AuthController.UsernameAndPassword usernameAndPassword = new AuthController.UsernameAndPassword("13966666661", "111111");

        String cookie = loginAndGetCookie(usernameAndPassword).cookie;
        HttpResponse httpResponse = doHttpResponse("/api/v1/user", "GET",null, cookie);

        int statusCode = httpResponse.getCode();
        Response<LoginResponse> response = objectMapper.readValue(httpResponse.getBody(), new TypeReference<Response<LoginResponse>>() {
        });
        Assertions.assertEquals(OK.value(), statusCode);
        Assertions.assertEquals(OK.value(), response.getStatusCode());
        Assertions.assertEquals("成功获取资源", response.getMessage());
        Assertions.assertNotNull(response.getDate());
        Assertions.assertEquals(1L, response.getDate().getUser().getId());
    }

}
