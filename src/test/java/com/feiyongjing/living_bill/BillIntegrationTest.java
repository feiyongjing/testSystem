package com.feiyongjing.living_bill;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.feiyongjing.living_bill.controller.AuthController;
import com.feiyongjing.living_bill.controller.BillController;
import com.feiyongjing.living_bill.enity.Bill;
import com.feiyongjing.living_bill.enity.PageResponse;
import com.feiyongjing.living_bill.enity.Response;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static org.springframework.http.HttpStatus.OK;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = LivingBillApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = {"spring.config.location=classpath:test-application.yml"})
public class BillIntegrationTest extends AbstractIntegrationTest{
    @Test
    public void createBillTest() throws JsonProcessingException {
        AuthController.UsernameAndPassword usernameAndPassword = new AuthController.UsernameAndPassword("13966666661", "111111");

        String cookie = loginAndGetCookie(usernameAndPassword).cookie;
        BillController.BillMessage billMessage=new BillController.BillMessage(1L,new BigDecimal("88.88"),"支出","餐饮","2020-2-10",null);
        HttpResponse httpResponse = doHttpResponse("/api/v3/bill" , "POST", billMessage, cookie);

        int statusCode = httpResponse.getCode();
        Response<Bill> response = objectMapper.readValue(httpResponse.getBody(), new TypeReference<Response<Bill>>() {
        });
        Assertions.assertEquals(OK.value(), statusCode);
        Assertions.assertEquals(OK.value(), response.getStatusCode());
        Assertions.assertEquals("账单创建成功", response.getMessage());
        Assertions.assertEquals(billMessage.getUserId(),response.getDate().getUserId());
        Assertions.assertEquals(billMessage.getBillType(),response.getDate().getBillType());
        Assertions.assertEquals(billMessage.getBillCostType(),response.getDate().getBillCostType());
        Assertions.assertEquals(timing(billMessage.getBillTime()),timing(response.getDate().getBillTime()));
        Assertions.assertEquals(billMessage.getBillAmount(),response.getDate().getBillAmount());
        if(billMessage.getRemark()==null){
            Assertions.assertEquals(billMessage.getBillCostType(),response.getDate().getRemark());
        }else {
            Assertions.assertEquals(billMessage.getRemark(),response.getDate().getRemark());
        }
    }
    private long timing(String str){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date parse = null;
        try {
            parse = sdf.parse(str);
        } catch (ParseException e) {
            throw new RuntimeException();
        }
        return parse.getTime();
    }
    @Test
    public void getMonthBillTest() throws JsonProcessingException {
        AuthController.UsernameAndPassword usernameAndPassword = new AuthController.UsernameAndPassword("13966666661", "111111");

        String cookie = loginAndGetCookie(usernameAndPassword).cookie;
       HttpResponse httpResponse = doHttpResponse("/api/v3/month_bill?userId=1&pageNum=2&pageSize=3&yearMonth=2020-10" , "GET", null, cookie);

        int statusCode = httpResponse.getCode();
        Response<PageResponse<Bill>> response = objectMapper.readValue(httpResponse.getBody(), new TypeReference<Response<PageResponse<Bill>>>() {
        });
        Bill bill = response.getDate().getData().get(0);
        Assertions.assertEquals(OK.value(), statusCode);
        Assertions.assertEquals(OK.value(), response.getStatusCode());
        Assertions.assertEquals("账单查询成功", response.getMessage());
        Assertions.assertEquals(2, response.getDate().getPageNum());
        Assertions.assertEquals(3, response.getDate().getPageSize());
        Assertions.assertEquals(2, response.getDate().getTotalPage());
        Assertions.assertEquals(1L, bill.getUserId());
        Assertions.assertEquals("2020-10-22", bill.getBillTime());
        Assertions.assertEquals("支出", bill.getBillType());
        Assertions.assertEquals("支出方式1", bill.getBillCostType());
        Assertions.assertEquals(new BigDecimal("4000.00"), bill.getBillAmount());
        Assertions.assertEquals("备注4", bill.getRemark());

    }
}
