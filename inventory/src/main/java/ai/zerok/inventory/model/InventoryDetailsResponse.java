package ai.zerok.inventory.model;

import jakarta.persistence.Column;
import jakarta.persistence.Lob;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Data
public class InventoryDetailsResponse {

    private Long skuId;
    private Long productId;

    private List<String> categories;

    private String name;

    private int price;

    private String image;

    private String description;
    private String brand;

    private  String sku;

    private Integer currentInventory;
}
