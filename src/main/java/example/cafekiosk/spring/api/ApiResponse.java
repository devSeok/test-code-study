package example.cafekiosk.spring.api;

import lombok.Getter;
import org.springframework.http.HttpStatus;




@Getter
public class ApiResponse<T> {

    private int code;
    private HttpStatus status;
    private String message;
    private T data;

    public ApiResponse( HttpStatus httpStatus, String message, T data) {
        this.code = httpStatus.value();
        this.status = httpStatus;
        this.message = message;
        this.data = data;
    }

    public static <T> ApiResponse<T> of(HttpStatus httpStatus, String message, T data) {
        return new ApiResponse<>(httpStatus, message, data);
    }

    public static <T> ApiResponse<T> of(HttpStatus httpStatus, T data) {
        return ApiResponse.of(httpStatus, httpStatus.name(), data);
    }

    public static <T> ApiResponse<T> of(T data) {
        return ApiResponse.of(HttpStatus.OK, HttpStatus.OK.name(), data);
    }
}
