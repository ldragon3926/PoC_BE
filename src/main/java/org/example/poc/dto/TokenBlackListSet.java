package org.example.poc.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.example.poc.entity.TokenBlackList;

import java.time.LocalDate;

@Getter
@Setter
public class TokenBlackListSet {
    private Integer id;
    @NotBlank(message = "Không được để token trống")
    private String token;
    @NotNull(message = "Không được để ngày hết hạn trống")
    private LocalDate expiryDate;

    public TokenBlackList dto(TokenBlackList tokenBlackList) {
        tokenBlackList.setId(this.getId());
        tokenBlackList.setToken(this.getToken());
        tokenBlackList.setExpiryDate(this.getExpiryDate());
        return tokenBlackList;
    }
}
