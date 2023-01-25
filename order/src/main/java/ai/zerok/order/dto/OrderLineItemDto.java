package ai.zerok.order.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderLineItemDto {

    private Long id;
    private String skuCode;
    private Integer price;
    private Integer quantity;
}
