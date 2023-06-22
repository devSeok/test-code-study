package example.cafekiosk.spring.docs.product;

import example.cafekiosk.spring.api.controller.product.ProductController;
import example.cafekiosk.spring.api.controller.product.request.ProductCreateRequest;
import example.cafekiosk.spring.api.service.product.ProductService;
import example.cafekiosk.spring.docs.RestDocsSupport;
import example.cafekiosk.spring.domain.product.ProductSellingStatus;
import example.cafekiosk.spring.domain.product.ProductType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.restdocs.payload.PayloadDocumentation;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ProductControllerDocsTest extends RestDocsSupport {

    private final ProductService productService = Mockito.mock(ProductService.class);
    @Override
    protected Object initController() {
        return new ProductController(productService);
    }



    @Test
    @DisplayName("신규 상품을 등록하는 API")
    void createProduct() throws Exception {
        //given
        ProductCreateRequest request = ProductCreateRequest.builder()
                .type(ProductType.HANDMADE)
                .sellingStatus(ProductSellingStatus.SELLING)
                .name("아메리카노")
                .price(4000)
                .build();

        //when
        //then
        mockMvc.perform(post("/api/v1/products/new")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("product-create",
                        requestFields(
                                fieldWithPath("type").type(JsonFieldType.STRING)
                                        .description("상품의 타입"),
                                fieldWithPath("sellingStatus").type(JsonFieldType.STRING)
                                        .description("상품의 판매상태"),
                                fieldWithPath("name").type(JsonFieldType.STRING)
                                        .description("상품의 이름"),
                                fieldWithPath("price").type(JsonFieldType.NUMBER)
                                        .description("상품 가격")
                        ),
                        responseFields(
                                fieldWithPath("code").type(JsonFieldType.STRING)
                                        .description("코드"),
                                fieldWithPath("status").type(JsonFieldType.STRING)
                                        .description("상태"),
                                fieldWithPath("message").type(JsonFieldType.STRING)
                                        .description("메시지"),
                                fieldWithPath("data").type(JsonFieldType.STRING)
                                        .description("응답 데이터"),
                                fieldWithPath("data.id").type(JsonFieldType.STRING)
                                        .description("상품 ID"),
                                fieldWithPath("data.productNumber").type(JsonFieldType.STRING)
                                        .description("상품 번호"),
                                fieldWithPath("data").type(JsonFieldType.STRING)
                                        .description("코드"),
                                fieldWithPath("data").type(JsonFieldType.STRING)
                                        .description("응답 데이터"),
                                fieldWithPath("data").type(JsonFieldType.STRING)
                                        .description("코드")
                        )
                        ))
                ;
    }
}
