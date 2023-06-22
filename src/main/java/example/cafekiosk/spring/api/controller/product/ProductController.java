package example.cafekiosk.spring.api.controller.product;

import example.cafekiosk.spring.api.ApiResponse;
import example.cafekiosk.spring.api.service.product.ProductService;
import example.cafekiosk.spring.api.controller.product.request.ProductCreateRequest;
import example.cafekiosk.spring.api.service.product.response.ProductResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/products")
public class ProductController {

    private final ProductService productService;

    @PostMapping("/new")
    public ApiResponse<ProductResponse> createProduct(@Valid @RequestBody ProductCreateRequest request) {

        return ApiResponse.of(productService.createProduct(request.toServiceRequest()));
    }

    @GetMapping("/selling")
    public ApiResponse<List<ProductResponse>> getSellingProducts() {

        return ApiResponse.of(productService.getSellingProducts());
    }

}
