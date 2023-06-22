package example.cafekiosk.spring.domain.product;

import org.assertj.core.api.Assertions;
import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static example.cafekiosk.spring.domain.product.ProductSellingStatus.*;
import static example.cafekiosk.spring.domain.product.ProductType.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.junit.jupiter.api.Assertions.*;


//@SpringBootTest
@DataJpaTest
@ActiveProfiles("test")
class ProductRepositoryTest {

    @Autowired
    private ProductRepository productRepository;


    @DisplayName("원하는 판매상태를 가진 상품들을 조회한다.")
    @Test
    void findAllBySellingStatusIn() {
    // given
        Product product1 = Product.builder()
                .productNumber("001")
                .type(HANDMADE)
                .sellingStatus(SELLING)
                .name("아메리카노")
                .price(4000)
                .build();
        Product product2 = Product.builder()
                .productNumber("001")
                .type(HANDMADE)
                .sellingStatus(HOLD)
                .name("카페라떼")
                .price(4500)
                .build();

        Product product3 = Product.builder()
                .productNumber("001")
                .type(HANDMADE)
                .sellingStatus(STOP_SELLING)
                .name("팥빙수")
                .price(7000)
                .build();
        productRepository.saveAll(List.of(product1, product2, product3));
        //when
        List<Product> allBySellingStatusIn = productRepository.findAllBySellingStatusIn(List.of(SELLING, HOLD));
        //then

        assertThat(allBySellingStatusIn).hasSize(2)
                .extracting("productNumber", "name", "sellingStatus")
                .containsExactlyInAnyOrder(
                        tuple("001", "아메리카노", SELLING),
                        tuple("001", "카페라떼", HOLD)
                );
    }

    @DisplayName("상품 번호를 이용해서 내가 원하는 상품을 조회 한다.")
    @Test
    void findAllByProductNumberIn() {
        // given
        Product product1 = Product.builder()
                .productNumber("001")
                .type(HANDMADE)
                .sellingStatus(SELLING)
                .name("아메리카노")
                .price(4000)
                .build();
        Product product2 = Product.builder()
                .productNumber("002")
                .type(HANDMADE)
                .sellingStatus(HOLD)
                .name("카페라떼")
                .price(4500)
                .build();

        Product product3 = Product.builder()
                .productNumber("003")
                .type(HANDMADE)
                .sellingStatus(STOP_SELLING)
                .name("팥빙수")
                .price(7000)
                .build();
        productRepository.saveAll(List.of(product1, product2, product3));

        //when
        List<Product> allByProductNumberIn = productRepository.findAllByProductNumberIn(List.of("001", "002"));

        //then
        assertThat(allByProductNumberIn)
                .hasSize(2)
                .extracting("productNumber", "name" , "price")
                .containsExactlyInAnyOrder(
                        tuple("001", "아메리카노", 4000),
                        tuple("002", "카페라떼", 4500)
                );

    }

    @DisplayName("가장 마지막으로 저장한 상품번호를 가져온다.")
    @Test
    void findLatestProduct() {
        // given
        Product product1 = Product.builder()
                .productNumber("001")
                .type(HANDMADE)
                .sellingStatus(SELLING)
                .name("아메리카노")
                .price(4000)
                .build();
        Product product2 = Product.builder()
                .productNumber("002")
                .type(HANDMADE)
                .sellingStatus(HOLD)
                .name("카페라떼")
                .price(4500)
                .build();

        Product product3 = Product.builder()
                .productNumber("003")
                .type(HANDMADE)
                .sellingStatus(STOP_SELLING)
                .name("팥빙수")
                .price(7000)
                .build();
        productRepository.saveAll(List.of(product1, product2, product3));

        //when
        String latestProductNumber = productRepository.findLatestProductNumber();

        //then
        assertThat(latestProductNumber).isEqualTo("003");
    }

    @DisplayName("가장 마지막으로 저장한 상품의 상품번호를 읽어올때, 상품이 하나도 없는 경우 null을 반환한다.")
    @Test
    void findLatestProductNumberWhenProductIsEmpty() {
        //when
        String latestProductNumber =  productRepository.findLatestProductNumber();


        assertThat(latestProductNumber).isNull();
    //then
    }

}