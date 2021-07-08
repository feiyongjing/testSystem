package com.feiyongjing.living_bill;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.feiyongjing.living_bill.controller.AuthController;
import com.feiyongjing.living_bill.enity.CostType;
import com.feiyongjing.living_bill.enity.Response;
import com.feiyongjing.living_bill.enity.User;
import com.github.kevinsawicki.http.HttpRequest;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.MediaType;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.feiyongjing.living_bill.utils.InitialCostType.*;
import static org.springframework.http.HttpStatus.OK;

public class AbstractIntegrationTest {
    public static ObjectMapper objectMapper = new ObjectMapper();
    @Autowired
    Environment environment;
    public UserLoginResponse loginAndGetCookie(AuthController.UsernameAndPassword usernameAndPassword) throws JsonProcessingException {
        Assertions.assertFalse(isLogin(null));
        UserLoginResponse userLoginResponse = login(usernameAndPassword, null);
        Assertions.assertTrue(isLogin(userLoginResponse.cookie));
        return userLoginResponse;
    }
    public UserLoginResponse login(AuthController.UsernameAndPassword usernameAndPassword, String cookie) throws JsonProcessingException {
        HttpResponse httpResponse = doHttpResponse("/api/v1/login", "POST", usernameAndPassword, cookie);

        int statusCode = httpResponse.getCode();
        Response<User> response = objectMapper.readValue(httpResponse.getBody(), new TypeReference<Response<User>>() {
        });
        Assertions.assertEquals(OK.value(), statusCode);
        if(response.getMessage().equals("登录成功")) {
            Map<String, List<String>> headers = httpResponse.headers;
            List<String> setCookie = headers.get("Set-Cookie");
            Assertions.assertNotNull(setCookie);
            String sessionid = getSessionIdFromSetCookie(setCookie.stream()
                    .filter(c -> c.contains("JSESSIONID")).findFirst().get());
            return new UserLoginResponse(sessionid,response);
        }
        return new UserLoginResponse(null,response);
    }
    public HttpResponse doHttpResponse(String apiName, String method, Object requestBody, String cookie) throws JsonProcessingException {
        HttpRequest request = createRequest(getUrl(apiName), method);
        if (cookie != null) {
            request.header("Cookie", cookie);
        }
        request.contentType(MediaType.APPLICATION_JSON_VALUE)
                .accept(MediaType.APPLICATION_JSON_VALUE);
        if (requestBody != null) {
            request.send(objectMapper.writeValueAsString(requestBody));
        }

        return new HttpResponse(request.code(), request.body(), request.headers());
    }
    public boolean isLogin(String cookie) throws JsonProcessingException {
        HttpResponse httpResponse = doHttpResponse("/api/v1/status", "GET", null, cookie);

        int statusCode = httpResponse.getCode();
        Response<User> response = objectMapper.readValue(httpResponse.getBody(), new TypeReference<Response<User>>() {
        });

        Assertions.assertEquals(OK.value(), statusCode);
        Assertions.assertEquals(OK.value(), response.getStatusCode());
        return response.getDate() != null;
    }
    public List<CostType> getAllCostTypeByBillType(String billType, String cookie) throws UnsupportedEncodingException, JsonProcessingException {
        HttpResponse httpResponse = doHttpResponse("/api/v2/"+ URLEncoder.encode(billType, "UTF-8"), "GET", null, cookie);

        int statusCode = httpResponse.getCode();
        Response<List<CostType>> response = objectMapper.readValue(httpResponse.getBody(), new TypeReference<Response<List<CostType>>>() {
        });
        Assertions.assertEquals(OK.value(), statusCode);
        Assertions.assertEquals(OK.value(), response.getStatusCode());
        Assertions.assertEquals("获取成功", response.getMessage());

        response.getDate().stream().filter(costType -> costType.getSource().equals("默认")).forEach(costType -> {
            Assertions.assertEquals(billType,costType.getBillType());
            Assertions.assertTrue(initialCostTypeModel.stream()
                    .map(CostType::getBillCostType)
                    .collect(Collectors.toList())
                    .contains(costType.getBillCostType())
            );
        });
        return response.getDate();
    }
    private HttpRequest createRequest(String url, String method) {
        if ("PATCH".equalsIgnoreCase(method)) {
            // workaround for https://bugs.openjdk.java.net/browse/JDK-8207840
            HttpRequest request = new HttpRequest(url, "POST");
            request.header("X-HTTP-Method-Override", "PATCH");
            return request;
        } else {
            return new HttpRequest(url, method);
        }
    }
    public String getSessionIdFromSetCookie(String setCookie) {
        int semiColonIndex = setCookie.indexOf(";");
        return setCookie.substring(0, semiColonIndex);

    }
    private String getUrl(String apiName) {
//        String encode = URLEncoder.encode(apiName);
        return "http://localhost:" + environment.getProperty("local.server.port") + apiName;
    }

    public static class HttpResponse {
        int code;
        String body;
        Map<String, List<String>> headers;

        HttpResponse(int code, String body, Map<String, List<String>> headers) {
            this.code = code;
            this.body = body;
            this.headers = headers;
        }

        public int getCode() {
            return code;
        }

        public void setCode(int code) {
            this.code = code;
        }

        public String getBody() {
            return body;
        }

        public void setBody(String body) {
            this.body = body;
        }

        public Map<String, List<String>> getHeaders() {
            return headers;
        }

        public void setHeaders(Map<String, List<String>> headers) {
            this.headers = headers;
        }
    }
    public static class UserLoginResponse {
        String cookie;
        Response<User> response;

        public UserLoginResponse(String cookie, Response<User> response) {
            this.cookie = cookie;
            this.response = response;
        }
    }
}
