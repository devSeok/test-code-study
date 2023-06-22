package example.cafekiosk.spring.domain.order;

import example.cafekiosk.spring.domain.product.Product;
import example.cafekiosk.spring.domain.product.ProductType;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;

import static example.cafekiosk.spring.domain.product.ProductSellingStatus.SELLING;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class OrderRepositoryTest {

    @Autowired
    private OrderRepository orderRepository;

    @Test
    @DisplayName("조회하고 싶은날짜를 조회하여 그날까지에 총 합을 조회한다.")
    void findOrdersBy() throws Exception {
        //given
        Product product1 = createProduct("001", 1000);
        Product product2 = createProduct("002", 2000);
        Product product3 = createProduct("003", 3000);
        LocalDateTime now = LocalDateTime.now();

        Order order = Order.create(List.of(product1, product2, product3), now);
        orderRepository.save(order);

        //when
        List<Order> ordersBy = orderRepository.findOrdersBy(now, now, OrderStatus.INIT);
        //then

        System.out.println(ordersBy);

    }

    private Product createProduct(String ProductNumber, int price) {
        return  Product.builder()
                .productNumber(ProductNumber)
                .type(ProductType.HANDMADE)
                .sellingStatus(SELLING)
                .name("메뉴 이름")
                .price(price)
                .build();
    }

}