package example.cafekiosk.spring.domain.order;

import example.cafekiosk.spring.domain.BaseEntity;
import example.cafekiosk.spring.domain.orderproduct.OrderProduct;
import example.cafekiosk.spring.domain.product.Product;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Getter
@Table(name = "orders")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Order extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus;

    private int totalPrice;

    private LocalDateTime registeredDateTime;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<OrderProduct> orderProducts = new ArrayList<>();



    @Builder
    public Order(OrderStatus orderStatus, LocalDateTime registeredDateTime, List<Product> products) {
        this.orderStatus = orderStatus;
        this.totalPrice = getTotalPrice(products);
        this.registeredDateTime = registeredDateTime;
        this.orderProducts = products.stream()
                .map(product -> new OrderProduct(this, product))
                .collect(Collectors.toList());
    }


    public static Order create(List<Product> products, LocalDateTime registeredDateTime) {
        return Order.builder()
                .orderStatus(OrderStatus.INIT)
                .products(products)
                .registeredDateTime(registeredDateTime)
                .build();
    }

    private int getTotalPrice(List<Product> products) {
        return products.stream()
                .mapToInt(Product::getPrice)
                .sum();
    }
}
