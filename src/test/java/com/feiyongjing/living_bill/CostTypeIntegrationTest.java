package com.feiyongjing.living_bill;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.feiyongjing.living_bill.controller.AuthController;
import com.feiyongjing.living_bill.controller.CostTypeController;
import com.feiyongjing.living_bill.enity.CostType;
import com.feiyongjing.living_bill.enity.Response;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.OK;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = LivingBillApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = {"spring.config.location=classpath:test-application.yml"})
public class CostTypeIntegrationTest extends AbstractIntegrationTest {
    @Test
    public void getShownCostTypeByBillTypeTest() throws JsonProcessingException, UnsupportedEncodingException {
        AuthController.UsernameAndPassword usernameAndPassword = new AuthController.UsernameAndPassword("13966666661", "111111");

        String cookie = loginAndGetCookie(usernameAndPassword).cookie;
        HttpResponse httpResponse = doHttpResponse("/api/v2/bill_type?billType=" + URLEncoder.encode("收入", "UTF-8"), "GET", null, cookie);

        int statusCode = httpResponse.getCode();
        Response<List<CostType>> response = objectMapper.readValue(httpResponse.getBody(), new TypeReference<Response<List<CostType>>>() {
        });
        Assertions.assertEquals(OK.value(), statusCode);
        Assertions.assertEquals(OK.value(), response.getStatusCode());
        Assertions.assertEquals("获取成功", response.getMessage());

        Assertions.assertEquals("收入"
                , response.getDate().get(4).getBillType());
        Assertions.assertEquals("收入方式1", response.getDate().get(4).getBillCostType());
        Assertions.assertEquals("展示", response.getDate().get(4).getStatusDisplay());
        Assertions.assertEquals("自定义", response.getDate().get(4).getSource());

        Assertions.assertEquals("收入"
                , response.getDate().get(5).getBillType());
        Assertions.assertEquals("收入方式2", response.getDate().get(5).getBillCostType());
        Assertions.assertEquals("展示", response.getDate().get(5).getStatusDisplay());
        Assertions.assertEquals("自定义", response.getDate().get(5).getSource());
    }

    @Test
    public void getAllCostTypeByBillTypeTest() throws JsonProcessingException, UnsupportedEncodingException {
        AuthController.UsernameAndPassword usernameAndPassword = new AuthController.UsernameAndPassword("13966666661", "111111");

        String cookie = loginAndGetCookie(usernameAndPassword).cookie;
        List<CostType> costTypes = getAllCostTypeByBillType("收入", cookie)
                .stream().filter(costType -> costType.getSource().equals("自定义")).collect(Collectors.toList());

        Assertions.assertEquals("收入"
                , costTypes.get(0).getBillType());
        Assertions.assertEquals("收入方式1", costTypes.get(0).getBillCostType());
        Assertions.assertEquals("展示", costTypes.get(0).getStatusDisplay());
        Assertions.assertEquals("自定义", costTypes.get(0).getSource());

        Assertions.assertEquals("收入"
                , costTypes.get(1).getBillType());
        Assertions.assertEquals("收入方式2", costTypes.get(1).getBillCostType());
        Assertions.assertEquals("展示", costTypes.get(1).getStatusDisplay());
        Assertions.assertEquals("自定义", costTypes.get(1).getSource());

        costTypes = getAllCostTypeByBillType("支出", cookie)
                .stream().filter(costType -> costType.getSource().equals("自定义")).collect(Collectors.toList());

        Assertions.assertEquals("支出"
                , costTypes.get(0).getBillType());
        Assertions.assertEquals("支出方式1", costTypes.get(0).getBillCostType());
        Assertions.assertEquals("展示", costTypes.get(0).getStatusDisplay());
        Assertions.assertEquals("自定义", costTypes.get(0).getSource());
    }

    @Test
    public void createBillCostTypeTest() throws JsonProcessingException {
        AuthController.UsernameAndPassword usernameAndPassword = new AuthController.UsernameAndPassword("13966666661", "111111");

        String cookie = loginAndGetCookie(usernameAndPassword).cookie;
        CostType costType = new CostType(1L, "支出", "支出方式2", null, null);
        HttpResponse httpResponse = doHttpResponse("/api/v2/bill_type", "POST", costType, cookie);

        int statusCode = httpResponse.getCode();
        Response<CostType> response = objectMapper.readValue(httpResponse.getBody(), new TypeReference<Response<CostType>>() {
        });
        Assertions.assertEquals(OK.value(), statusCode);
        Assertions.assertEquals(OK.value(), response.getStatusCode());
        Assertions.assertEquals("添加成功", response.getMessage());
        Assertions.assertEquals(1L, response.getDate().getUserId());
        Assertions.assertEquals("支出", response.getDate().getBillType());
        Assertions.assertEquals("支出方式2", response.getDate().getBillCostType());
        Assertions.assertEquals("展示", response.getDate().getStatusDisplay());
        Assertions.assertEquals("自定义", response.getDate().getSource());

        httpResponse = doHttpResponse("/api/v2/bill_type", "POST", costType, cookie);

        statusCode = httpResponse.getCode();
        response = objectMapper.readValue(httpResponse.getBody(), new TypeReference<Response<CostType>>() {
        });
        Assertions.assertEquals(OK.value(), statusCode);
        Assertions.assertEquals(BAD_REQUEST.value(), response.getStatusCode());
        Assertions.assertEquals("添加类型重复", response.getMessage());
        Assertions.assertNull(response.getDate());
    }

    @Test
    public void updateBillCostTypeTest() throws JsonProcessingException {
        AuthController.UsernameAndPassword usernameAndPassword = new AuthController.UsernameAndPassword("13966666662", "222222");

        String cookie = loginAndGetCookie(usernameAndPassword).cookie;
        CostTypeController.StatusDisplayAndSource costType = new CostTypeController.StatusDisplayAndSource("展示", "自定义");
        HttpResponse httpResponse = doHttpResponse("/api/v2/42", "POST", costType, cookie);

        int statusCode = httpResponse.getCode();
        Response<CostType> response = objectMapper.readValue(httpResponse.getBody(), new TypeReference<Response<CostType>>() {
        });
        Assertions.assertEquals(OK.value(), statusCode);
        Assertions.assertEquals(OK.value(), response.getStatusCode());
        Assertions.assertEquals("更新成功", response.getMessage());
        Assertions.assertEquals(2L, response.getDate().getUserId());
        Assertions.assertEquals("支出", response.getDate().getBillType());
        Assertions.assertEquals("支出方式1", response.getDate().getBillCostType());
        Assertions.assertEquals("展示", response.getDate().getStatusDisplay());
        Assertions.assertEquals("自定义", response.getDate().getSource());

        costType.setStatusDisplay("不展示");
        costType.setSource("默认");
        usernameAndPassword.setUsername("13966666661");
        usernameAndPassword.setPassword("111111");
        cookie = loginAndGetCookie(usernameAndPassword).cookie;
        httpResponse = doHttpResponse("/api/v2/1", "POST", costType, cookie);

        statusCode = httpResponse.getCode();
        response = objectMapper.readValue(httpResponse.getBody(), new TypeReference<Response<CostType>>() {
        });
        Assertions.assertEquals(OK.value(), statusCode);
        Assertions.assertEquals(OK.value(), response.getStatusCode());
        Assertions.assertEquals("更新成功", response.getMessage());
        Assertions.assertEquals(1L, response.getDate().getUserId());
        Assertions.assertEquals("收入", response.getDate().getBillType());
        Assertions.assertEquals("工资", response.getDate().getBillCostType());
        Assertions.assertEquals("不展示", response.getDate().getStatusDisplay());
        Assertions.assertEquals("默认", response.getDate().getSource());

    }
}
