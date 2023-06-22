package example.cafekiosk.spring.api.service.product.request;

import example.cafekiosk.spring.domain.product.Product;
import example.cafekiosk.spring.domain.product.ProductSellingStatus;
import example.cafekiosk.spring.domain.product.ProductType;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.util.List;


@Getter
@NoArgsConstructor
public class ProductCreateServiceRequest {


    private ProductType type;
    private ProductSellingStatus sellingStatus;
    private String name;
    private int price;

    public Product toEntity(String nextProductNumber) {
        return Product.builder()
                .type(type)
                .productNumber(nextProductNumber)
                .sellingStatus(sellingStatus)
                .price(price)
                .name(name)
                .build();
    }

    @Builder
    public ProductCreateServiceRequest(ProductType type, ProductSellingStatus sellingStatus, String name, int price) {
        this.type = type;
        this.sellingStatus = sellingStatus;
        this.name = name;
        this.price = price;
    }
}
