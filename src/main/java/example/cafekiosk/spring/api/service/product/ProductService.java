package example.cafekiosk.spring.api.service.product;

import example.cafekiosk.spring.api.controller.product.request.ProductCreateRequest;
import example.cafekiosk.spring.api.service.product.request.ProductCreateServiceRequest;
import example.cafekiosk.spring.api.service.product.response.ProductResponse;
import example.cafekiosk.spring.domain.product.Product;
import example.cafekiosk.spring.domain.product.ProductRepository;
import example.cafekiosk.spring.domain.product.ProductSellingStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductService {

    private final ProductRepository productRepository;

    @Transactional
    public ProductResponse createProduct(ProductCreateServiceRequest request) {
        String nextProductNumber = createNextProductNumber();

        Product product = request.toEntity(nextProductNumber);
        Product saveProduct = productRepository.save(product);

        return ProductResponse.of(saveProduct);
    }

    private String createNextProductNumber() {
        // productNumber
        // 001 , 002,
        // DB에서 마지막 저장된 Product 의 상품 번호를 읽어와서 +1
        String latestProductNumber =  productRepository.findLatestProductNumber();

        if(latestProductNumber == null) {
           return "001";
        }

        int latestProductNumberInt = Integer.parseInt(latestProductNumber);
        int nextProductNumberInt = latestProductNumberInt + 1;

        return String.format("%03d", nextProductNumberInt);
    }


    public List<ProductResponse> getSellingProducts() {

        List<Product> allBySellingTypeIn = productRepository.findAllBySellingStatusIn(ProductSellingStatus.forDisplay());

        return allBySellingTypeIn.stream()
                .map(ProductResponse::of)
                .collect(Collectors.toList());
    }


}
