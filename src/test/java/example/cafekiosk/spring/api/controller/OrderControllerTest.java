package example.cafekiosk.spring.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import example.cafekiosk.spring.api.controller.order.OrderController;
import example.cafekiosk.spring.api.service.order.OrderService;
import example.cafekiosk.spring.api.controller.order.request.OrderCreatedRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(controllers = OrderController.class)
class OrderControllerTest {

    @MockBean
    private OrderService orderService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;



    @Test
    @DisplayName("신규 주문을 등록한다.")
    void  createProduct() throws Exception {
        //given
        List<String> stringList = List.of("001");

        OrderCreatedRequest orderCreatedRequest = OrderCreatedRequest.builder()
                .productNumbers(stringList)
                .build();

        //when
        //then
        mockMvc.perform(post("/api/v1/orders/new")
                        .content(objectMapper.writeValueAsString(orderCreatedRequest))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("신규 주문을 등록할 때 상품 번호는 1개 이상이어야 한다.")
    void createBadProduct() throws Exception {
        //given

        OrderCreatedRequest orderCreatedRequest = OrderCreatedRequest.builder()
                .productNumbers(List.of())
                .build();

        //when
        //then
        mockMvc.perform(post("/api/v1/orders/new")
                        .content(objectMapper.writeValueAsString(orderCreatedRequest))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("400"))
                .andExpect(jsonPath("$.status").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.message").value("상품 번호 리스트는 필수입니다."))
                .andExpect(jsonPath("$.data").isEmpty()
                );
    }
}