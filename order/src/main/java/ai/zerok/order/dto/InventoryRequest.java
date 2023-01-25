package ai.zerok.order.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InventoryRequest {
    Long id;

    private String sku;

    private int currentInventory;

    private int price;
}
