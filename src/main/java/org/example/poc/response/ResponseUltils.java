package org.example.poc.response;
import org.springframework.http.ResponseEntity;

public class ResponseUltils {
    public static <T> ResponseEntity<ApiResponse<T>> success(T data, String message, String code){
        return ResponseEntity.ok(new ApiResponse<>(code, message, data));
    }
}
