package example.cafekiosk.spring.api.service.order;

import example.cafekiosk.spring.api.service.mail.MailService;
import example.cafekiosk.spring.domain.order.Order;
import example.cafekiosk.spring.domain.order.OrderRepository;
import example.cafekiosk.spring.domain.order.OrderStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderStatisticsService {

    private final OrderRepository orderRepository;
    private final MailService mailService;

    public boolean sendOrderStatisticsMail(LocalDate orderDate, String email) {
        // 해당 일자에 결제왼료된 주문들을 가져와서
        List<Order> orders = orderRepository.findOrdersBy(
                orderDate.atStartOfDay(),
                orderDate.plusDays(1).atStartOfDay(),
                OrderStatus.COMPLETED
        );
        // 총 매출 합계를 계산하고
        int totalAmount = orders.stream()
                .mapToInt(Order::getTotalPrice)
                .sum();

       boolean result = mailService.sendMail(
                "test@naver.com",
                email,
                String.format("[매출통계] %s", orderDate),
                String.format("총 매출 합계는 %s원입니다.", totalAmount)
        );
        // 메일 전
        if(!result) {
            throw new IllegalArgumentException("매출 통계 메일 전송에 실패했습니다.");
        }

        return true;
    }
}
