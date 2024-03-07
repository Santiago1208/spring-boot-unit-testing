package co.edu.santiago.springboot.springboot_test.app.models.dto;

import java.math.BigDecimal;

public record TransferDTO(
        Long sourceAccountId,
        Long targetAccountId,
        BigDecimal amount,
        Long bankId
) {
}
