package example.cafekiosk.spring.api.service.order;

import example.cafekiosk.spring.api.controller.order.request.OrderCreatedRequest;
import example.cafekiosk.spring.api.service.order.request.OrderCreateServiceRequest;
import example.cafekiosk.spring.api.service.order.response.OrderResponse;
import example.cafekiosk.spring.domain.order.Order;
import example.cafekiosk.spring.domain.order.OrderRepository;
import example.cafekiosk.spring.domain.product.Product;
import example.cafekiosk.spring.domain.product.ProductRepository;
import example.cafekiosk.spring.domain.product.ProductType;
import example.cafekiosk.spring.domain.stock.Stock;
import example.cafekiosk.spring.domain.stock.StockRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
@Transactional
public class OrderService {

    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final StockRepository stockRepository;


    public OrderResponse createOrder(OrderCreateServiceRequest request, LocalDateTime registeredDateTime) {
        List<String> productNumbers = request.getProductNumbers();
        List<Product> duplicateProducts = findProductsBy(productNumbers);
        deductStockQuantities(duplicateProducts);

        Order order = Order.create(duplicateProducts, registeredDateTime);
        Order saveOrder = orderRepository.save(order);
        return OrderResponse.of(saveOrder);
    }

    private void deductStockQuantities(List<Product> duplicateProducts) {
        //재고 차감 체크가 필요한 상품들 filter
        // 재고와 관련 있는 상품 타입은 병 음료 , 베이커리이다.
        List<String> stockProductNumbers = extractStockProductNumbers(duplicateProducts);

        // 재고 엔티티 조회
        Map<String, Stock> stockMap = createStockMapBy(stockProductNumbers);
        Map<String, Long> productCountingMap = createCountingMapBy(stockProductNumbers);

        // 재고 차감 시도
        for (String stockProductNumber : new HashSet<>(stockProductNumbers)) {
            Stock stock = stockMap.get(stockProductNumber); // 재고 enetiy
            int quantity = productCountingMap.get(stockProductNumber).intValue(); // 상품 count

            if(stock.isQuantityLessThan(quantity)) {
                throw new IllegalArgumentException("재고가 부족한 상품이 있습니다");
            }

            stock.deductQuantity(quantity);
        }
    }

    private static Map<String, Long> createCountingMapBy(List<String> stockProductNumbers) {
        // 상품별 counting
        return stockProductNumbers.stream()
                .collect(Collectors.groupingBy(p -> p, Collectors.counting()));
    }

    private Map<String, Stock> createStockMapBy(List<String> stockProductNumbers) {
        List<Stock> stocks = stockRepository.findAllByProductNumberIn(stockProductNumbers);
        return stocks.stream()
                .collect(Collectors.toMap(Stock::getProductNumber, s -> s));
    }


    private static List<String> extractStockProductNumbers(List<Product> duplicateProducts) {
        return duplicateProducts.stream()
                .filter(product -> ProductType.containsStockType(product.getType()))
                .map(Product::getProductNumber)
                .collect(Collectors.toList());
    }

    private List<Product> findProductsBy(List<String> productNumbers) {

        // where in으로 검색 만약에 001 값이 두개 넣으면 한가지만 나옴 2개를 시켰는데
        List<Product> allByProductNumberIn = productRepository.findAllByProductNumberIn(productNumbers);

        // MAP형태로 변환해서 최초 주문이 들어온 값 기준으로 KEY와 매칭에서 최초 주문된 COUNT 기준으로 조회
        Map<String, Product> collect = allByProductNumberIn.stream()
                .collect(Collectors.toMap(Product::getProductNumber, p -> p));


        return productNumbers.stream()
                .map(collect::get)
                .collect(Collectors.toList());
    }
}
