package ai.zerok.sofademo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductRequest implements Serializable {

    private List<String> categories;

    private String name;

    private int price;

    private String image;

    private String description;

    private String brand;

    private  String sku;
}
