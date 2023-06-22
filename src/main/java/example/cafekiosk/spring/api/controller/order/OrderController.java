package example.cafekiosk.spring.api.controller.order;


import example.cafekiosk.spring.api.ApiResponse;
import example.cafekiosk.spring.api.service.order.OrderService;
import example.cafekiosk.spring.api.controller.order.request.OrderCreatedRequest;
import example.cafekiosk.spring.api.service.order.response.OrderResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.time.LocalDateTime;

@RestController
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping("/api/v1/orders/new")
    public ApiResponse<OrderResponse> createOrder(@Valid @RequestBody OrderCreatedRequest request) {

        LocalDateTime registeredDateTime = LocalDateTime.now();

       return ApiResponse.of(orderService.createOrder(request.toServiceRequest(), registeredDateTime));
    }

}
