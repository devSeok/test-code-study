package example.cafekiosk.spring.api.service.product;

import example.cafekiosk.spring.api.controller.product.request.ProductCreateRequest;
import example.cafekiosk.spring.api.service.product.response.ProductResponse;
import example.cafekiosk.spring.domain.product.Product;
import example.cafekiosk.spring.domain.product.ProductRepository;
import example.cafekiosk.spring.domain.product.ProductType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static example.cafekiosk.spring.domain.product.ProductSellingStatus.SELLING;
import static example.cafekiosk.spring.domain.product.ProductType.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.groups.Tuple.tuple;


@SpringBootTest
@ActiveProfiles("test")
class ProductServiceTest {

    @Autowired
    private ProductService productService;

    @Autowired
    private ProductRepository productRepository;

    @AfterEach
    void tearDown() {
        productRepository.deleteAllInBatch();
    }


    @DisplayName("신규 상품을 등록한다. 상풉번호는 가장 최근 상품의 상품번호에서 1 증가한 값이다.")
    @Test
    void createProduct() {
    //given
        Product product1 = createProduct(BOTTLE, "001", 1000);
        Product product2 = createProduct(BAKERY, "002", 3000);
        Product product3 = createProduct(HANDMADE, "003", 5000);
        productRepository.saveAll(List.of(product1, product2, product3));

        ProductCreateRequest request = ProductCreateRequest.builder()
                .type(HANDMADE)
                .sellingStatus(SELLING)
                .name("카푸치노")
                .price(5000)
                .build();
        //when
        ProductResponse product = productService.createProduct(request.toServiceRequest());

        //then
        assertThat(product)
                .extracting("productNumber", "type", "sellingStatus", "name", "price")
                .contains("004", HANDMADE, SELLING, "카푸치노", 5000);

        List<Product> products = productRepository.findAll();

        assertThat(products).hasSize(4)
                .extracting("productNumber", "type", "sellingStatus", "name", "price")
                .containsExactlyInAnyOrder(
                        tuple("001", BOTTLE, SELLING, "메뉴 이름", 1000),
                        tuple("002", BAKERY, SELLING, "메뉴 이름", 3000),
                        tuple("003", HANDMADE, SELLING, "메뉴 이름", 5000),
                        tuple("004", HANDMADE, SELLING, "카푸치노", 5000)
                );
    }

    @Test
    @DisplayName("상품이 하나도 없는 경우 신규 상품을 등록하면 상품번호는 001이다.")
    void createProductWhenProductsIsEmpty() throws Exception {
        //given
        ProductCreateRequest request = ProductCreateRequest.builder()
                .type(HANDMADE)
                .sellingStatus(SELLING)
                .name("카푸치노")
                .price(5000)
                .build();
        //when
        ProductResponse product = productService.createProduct(request.toServiceRequest());


        //then
        assertThat(product)
                .extracting("productNumber", "type", "sellingStatus", "name", "price")
                .contains("001", HANDMADE, SELLING, "카푸치노", 5000);


        List<Product> products = productRepository.findAll();

        assertThat(products).hasSize(1)
                .extracting("productNumber", "type", "sellingStatus", "name", "price")
                .containsExactlyInAnyOrder(
                        tuple("001", HANDMADE, SELLING, "카푸치노", 5000)
                );
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