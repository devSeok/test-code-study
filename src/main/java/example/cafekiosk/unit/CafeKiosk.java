package example.cafekiosk.unit;

import example.cafekiosk.unit.beverage.Beverage;
import example.cafekiosk.unit.order.Order;
import lombok.Getter;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

/**
 *  받거나 삭제
 *  계산
 *
 */
@Getter
public class CafeKiosk {

    private static final LocalTime SHOP_OPEN_TIME = LocalTime.of(10,0);
    private static final LocalTime SHOP_CLOSE_TIME = LocalTime.of(22,0);

    private final List<Beverage> beverages = new ArrayList<>();

    public void add(Beverage beverage) {
        beverages.add(beverage);
    }

    public void add(Beverage beverage, int count) {

        if(count <= 0) {
            throw new IllegalArgumentException("음료는 한잔 이상 주문이 가능합니다");
        }


        for (int i = 0; i < count; i++) {
            beverages.add(beverage);
        }


        beverages.add(beverage);
    }

    public void remove(Beverage beverage) {
        beverages.remove(beverage);
    }

    public void clear() {
        beverages.clear();
    }

//    public int calculateTotalPrice() {
//        int totalPrice = 0;
//        for(Beverage beverage: beverages) {
//            totalPrice += beverage.getPrice();
//        }
//
//        return totalPrice;
//    }

    public Order createOrder() {
        LocalDateTime now = LocalDateTime.now();
        LocalTime localTime = now.toLocalTime();

        if(localTime.isBefore(SHOP_OPEN_TIME) || localTime.isAfter(SHOP_CLOSE_TIME)) {
            throw new IllegalArgumentException("주문 시간이 아닙니다. 관리자에게 문의주세요");
        }

        return new Order(now, beverages);
    }

    public Order createOrder(LocalDateTime localDateTime) {
        LocalTime localTime = localDateTime.toLocalTime();

        if(localTime.isBefore(SHOP_OPEN_TIME) || localTime.isAfter(SHOP_CLOSE_TIME)) {
            throw new IllegalArgumentException("주문 시간이 아닙니다. 관리자에게 문의주세요");
        }

        return new Order(localDateTime, beverages);
    }

    public int calculateTotalPrice() {
       return beverages.stream()
               .mapToInt(Beverage::getPrice)
               .sum();
    }
}
