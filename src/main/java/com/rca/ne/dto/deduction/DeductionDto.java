package com.rca.ne.dto.deduction;

import com.rca.ne.model.Deduction;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeductionDto {
    private Long id;
    private String code;
    
    @NotBlank(message = "Deduction name is required")
    private String deductionName;
    
    @NotNull(message = "Percentage is required")
    @DecimalMin(value = "0.0", inclusive = true, message = "Percentage must be at least 0")
    @DecimalMax(value = "100.0", inclusive = true, message = "Percentage must be at most 100")
    private BigDecimal percentage;
    
    private boolean active;

    // Constructor to convert from Entity to DTO
    public DeductionDto(Deduction deduction) {
        this.id = deduction.getId();
        this.code = deduction.getCode();
        this.deductionName = deduction.getDeductionName();
        this.percentage = deduction.getPercentage();
        this.active = deduction.isActive();
    }
}