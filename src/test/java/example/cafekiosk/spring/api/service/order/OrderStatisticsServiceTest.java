package example.cafekiosk.spring.api.service.order;

import example.cafekiosk.spring.client.mail.MailSendClient;
import example.cafekiosk.spring.domain.history.mail.MailSendHistory;
import example.cafekiosk.spring.domain.history.mail.MailSendHistoryRepository;
import example.cafekiosk.spring.domain.order.Order;
import example.cafekiosk.spring.domain.order.OrderRepository;
import example.cafekiosk.spring.domain.order.OrderStatus;
import example.cafekiosk.spring.domain.orderproduct.OrderProduct;
import example.cafekiosk.spring.domain.orderproduct.OrderProductRepository;
import example.cafekiosk.spring.domain.product.Product;
import example.cafekiosk.spring.domain.product.ProductRepository;
import example.cafekiosk.spring.domain.product.ProductType;
import org.assertj.core.api.Assertions;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static example.cafekiosk.spring.domain.product.ProductSellingStatus.SELLING;
import static example.cafekiosk.spring.domain.product.ProductType.*;
import static org.assertj.core.api.Assertions.anyOf;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
@Transactional
class OrderStatisticsServiceTest {

    @Autowired
    private OrderStatisticsService orderStatisticsService;

    @Autowired
    private OrderRepository orderRepository;


    @Autowired
    private OrderProductRepository orderProductRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private MailSendHistoryRepository mailSendHistoryRepository;

    @MockBean
    private MailSendClient mailSendClient;

    @AfterEach
    void tearDown() {
        orderProductRepository.deleteAllInBatch();
        orderRepository.deleteAllInBatch();
        productRepository.deleteAllInBatch();
        mailSendHistoryRepository.deleteAllInBatch();
    }

    @Test
    @DisplayName("결제완료 주문들의 조회하여 매출 통계 메일을 전송한다.")
    void sendOrderStatisticsMail() throws Exception {

        LocalDateTime of = LocalDateTime.of(2023, 3, 5, 0, 0);
        //given
        Product product1 = createProduct(HANDMADE, "001", 1000);
        Product product2 = createProduct(HANDMADE, "002", 2000);
        Product product3 = createProduct(HANDMADE, "003", 3000);
        List<Product> products = List.of(product1, product2, product3);

        Order order1 = createPaymentCompletedOrder(LocalDateTime.of(2023,3,4,23,59, 59), products);
        Order order2 = createPaymentCompletedOrder(of, products);
        Order order3 = createPaymentCompletedOrder(LocalDateTime.of(2023,3,5,23,59, 59), products);
        Order order4 = createPaymentCompletedOrder(LocalDateTime.of(2023,3,6,0,0), products);

        // stubbing 행위를 목 객체 원하는걸 리턴해준다.
        when(mailSendClient.sendEmail(any(String.class), any(String.class), any(String.class), any(String.class)))
                .thenReturn(true);

        //when
        boolean result = orderStatisticsService.sendOrderStatisticsMail(LocalDate.of(2023, 3, 5), "test@test.com");

        //then
        assertThat(result).isTrue();

        List<MailSendHistory> all = mailSendHistoryRepository.findAll();
        assertThat(all).hasSize(1)
                .extracting("content")
                .contains("총 매출 합계는 12000원입니다.");
    }

    private Order createPaymentCompletedOrder(LocalDateTime of, List<Product> products) {
        Order order = Order.builder()
                .products(products)
                .orderStatus(OrderStatus.COMPLETED)
                .registeredDateTime(of)
                .build();

       return orderRepository.save(order);
    }


    private Product createProduct(ProductType type, String ProductNumber, int price) {
        return Product.builder()
                .productNumber(ProductNumber)
                .type(type)
                .sellingStatus(SELLING)
                .name("메뉴 이름")
                .price(price)
                .build();
    }


}