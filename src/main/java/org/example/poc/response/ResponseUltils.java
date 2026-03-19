package org.example.poc.response;
import org.springframework.http.ResponseEntity;

public class ResponseUltils {
    public static <T> ResponseEntity<ApiResponse<T>> success(T data, String message, String code){
        return ResponseEntity.ok(new ApiResponse<>(code, message, data));
    }
    public static <T> ResponseEntity<ApiResponse<T>> error( String code, String message){
        return ResponseEntity.badRequest().body(new ApiResponse<>(code, message, null));
    }
}
