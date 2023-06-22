package example.cafekiosk.spring.domain.orderproduct;

import example.cafekiosk.spring.domain.BaseEntity;
import example.cafekiosk.spring.domain.order.Order;
import example.cafekiosk.spring.domain.product.Product;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderProduct extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // TODO : https://www.baeldung.com/hibernate-unsaved-transient-instance-error 왜 cascade 를 붙어야되는지 ? 필수적으로 연구 해봐야된다
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Order order ;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Product product;

    public OrderProduct(Order order, Product product) {
        this.order = order;
        this.product = product;
    }


}
