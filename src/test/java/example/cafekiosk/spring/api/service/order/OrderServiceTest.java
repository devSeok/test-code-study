package example.cafekiosk.spring.api.service.order;

import example.cafekiosk.spring.api.controller.order.request.OrderCreatedRequest;
import example.cafekiosk.spring.api.service.order.response.OrderResponse;
import example.cafekiosk.spring.domain.order.OrderRepository;
import example.cafekiosk.spring.domain.orderproduct.OrderProductRepository;
import example.cafekiosk.spring.domain.product.Product;
import example.cafekiosk.spring.domain.product.ProductRepository;
import example.cafekiosk.spring.domain.product.ProductType;
import example.cafekiosk.spring.domain.stock.Stock;
import example.cafekiosk.spring.domain.stock.StockRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;

import static example.cafekiosk.spring.domain.product.ProductSellingStatus.*;
import static example.cafekiosk.spring.domain.product.ProductType.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.groups.Tuple.*;

@SpringBootTest
@ActiveProfiles("test")
//@DataJpaTest
class OrderServiceTest {

    @Autowired
    private OrderService orderService;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private OrderProductRepository orderProductRepository;

    @Autowired
    private StockRepository stockRepository;

    @AfterEach
    void tearDown() {
        orderProductRepository.deleteAllInBatch();
        productRepository.deleteAllInBatch();
        orderRepository.deleteAllInBatch();
        stockRepository.deleteAllInBatch();
    }

    @DisplayName("주문번호 리스트를 받아 주문을 생성한다.")
    @Test
    void cratedOrder() {
        // given
        LocalDateTime now = LocalDateTime.now();
        Product product1 = createProduct(HANDMADE, "001", 1000);
        Product product2 = createProduct(HANDMADE, "002", 3000);
        Product product3 = createProduct(HANDMADE, "003", 5000);
        productRepository.saveAll(List.of(product1, product2, product3));
        OrderCreatedRequest build = OrderCreatedRequest.builder()
                .productNumbers(List.of("001", "002"))
                .build();

    //when
        OrderResponse order = orderService.createOrder(build.toServiceRequest(), now);

        //then

        assertThat(order.getId()).isNotNull();
        assertThat(order)
                .extracting("registeredDateTime", "totalPrice")
                .contains(now, 4000);

        assertThat(order.getProducts()).hasSize(2)
                .extracting("productNumber", "price")
                .containsExactlyInAnyOrder(
                        tuple("001", 1000),
                        tuple("002", 3000)
                );
    }


    @DisplayName("재고와 관련된 상품이 포함되어 있는 주문번호를 받아 주문을 생성한다.")
    @Test
    void cratedOrderWithStock() {
        // given
        LocalDateTime now = LocalDateTime.now();
        Product product1 = createProduct(BOTTLE, "001", 1000);
        Product product2 = createProduct(BAKERY, "002", 3000);
        Product product3 = createProduct(HANDMADE, "003", 5000);
        productRepository.saveAll(List.of(product1, product2, product3));


        Stock stock1 = Stock.create("001", 2);
        Stock stock2 = Stock.create("002", 2);

        stockRepository.saveAll(List.of(stock1, stock2));

        OrderCreatedRequest build = OrderCreatedRequest.builder()
                .productNumbers(List.of("001", "001", "002", "003"))
                .build();

        //when
        OrderResponse order = orderService.createOrder(build.toServiceRequest(), now);

        //then

        assertThat(order.getId()).isNotNull();
        assertThat(order)
                .extracting("registeredDateTime", "totalPrice")
                .contains(now, 10000);

        assertThat(order.getProducts()).hasSize(4)
                .extracting("productNumber", "price")
                .containsExactlyInAnyOrder(
                        tuple("001", 1000),
                        tuple("001", 1000),
                        tuple("002", 3000),
                        tuple("003", 5000)
                );

        List<Stock> all = stockRepository.findAll();

        assertThat(all).hasSize(2)
                .extracting("productNumber", "quantity")
                .containsExactlyInAnyOrder(
                        tuple("001", 0),
                        tuple("002", 1)
                );
    }


    @DisplayName("중복되는 상품번호 리스트로 주문을 생성할 수 있다.")
    @Test
    void createOrderWithDuplicateProductNumbers() {
        // given
        LocalDateTime now = LocalDateTime.now();
        Product product1 = createProduct(HANDMADE, "001", 1000);
        Product product2 = createProduct(HANDMADE, "002", 3000);
        Product product3 = createProduct(HANDMADE, "003", 5000);
        productRepository.saveAll(List.of(product1, product2, product3));
        OrderCreatedRequest build = OrderCreatedRequest.builder()
                .productNumbers(List.of("001", "001"))
                .build();

        //when
        OrderResponse order = orderService.createOrder(build.toServiceRequest(), now);

        //then

        assertThat(order.getId()).isNotNull();
        assertThat(order)
                .extracting("registeredDateTime", "totalPrice")
                .contains(now, 2000);

        assertThat(order.getProducts()).hasSize(2)
                .extracting("productNumber", "price")
                .containsExactlyInAnyOrder(
                        tuple("001", 1000),
                        tuple("001", 1000)
                );
    }


    @DisplayName("재고와 부족한 상품으로 주문을 생성하려는 경우 예외가 발생한다.")
    @Test
    void cratedOrderWithNoStock() {
        // given
        LocalDateTime now = LocalDateTime.now();
        Product product1 = createProduct(BOTTLE, "001", 1000);
        Product product2 = createProduct(BAKERY, "002", 3000);
        Product product3 = createProduct(HANDMADE, "003", 5000);
        productRepository.saveAll(List.of(product1, product2, product3));


        Stock stock1 = Stock.create("001", 1);
        Stock stock2 = Stock.create("002", 1);
        stock1.deductQuantity(1);
        stockRepository.saveAll(List.of(stock1, stock2));

        OrderCreatedRequest build = OrderCreatedRequest.builder()
                .productNumbers(List.of("001", "001", "002", "003"))
                .build();

        //when
        //then
        assertThatThrownBy(() -> orderService.createOrder(build.toServiceRequest(), now))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("재고가 부족한 상품이 있습니다");


    }



    private Product createProduct(ProductType type, String ProductNumber, int price) {
        return  Product.builder()
                .productNumber(ProductNumber)
                .type(type)
                .sellingStatus(SELLING)
                .name("메뉴 이름")
                .price(price)
                .build();
    }

}