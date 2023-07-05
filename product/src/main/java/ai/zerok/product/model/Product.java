package ai.zerok.product.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "products")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

//    @Column(name="categories",columnDefinition="varchar(255)[]")
    private List<String> categories;

    private String name;

    private int price;

    private String image;

    @Column(name="description",columnDefinition="TEXT")
    private String description;
    private String brand;

    private  String sku;

}
