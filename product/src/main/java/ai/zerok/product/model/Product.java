package ai.zerok.sofademo.model;

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

    private List<String> categories;

    private String name;

    private int price;

    private String image;

    @Lob
    @Column(name="description",columnDefinition="LONGTEXT")
    private String description;
    private String brand;

    private  String sku;

}
