package example.cafekiosk.spring.domain.product;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class ProductTypeTest {

    @DisplayName("상품 타입이 재고 관련 타입인지를 체크한다.")
    @Test
    void containsStockType() {
        //given
        ProductType handmade = ProductType.HANDMADE;
        //when
        boolean result = ProductType.containsStockType(handmade);
        //then
         assertThat(result).isFalse();
    }

    @DisplayName("상품 타입이 재고 관련 타입인지를 체크한다.")
    @Test
    void containsStockTyp2() {
        //given
        ProductType handmade = ProductType.BAKERY;
        //when
        boolean result = ProductType.containsStockType(handmade);
        //then
        assertThat(result).isTrue();
    }

}