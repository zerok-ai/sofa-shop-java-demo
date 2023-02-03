package ai.zerok.sofademo.service;

import ai.zerok.sofademo.dto.ProductRequest;
import ai.zerok.sofademo.dto.ProductResponse;
import ai.zerok.sofademo.model.Product;
import ai.zerok.sofademo.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    public void createProduct(ProductRequest productRequest) {
        Product product = new Product();

        product.setName(productRequest.getName());
        product.setDescription(productRequest.getDescription());
        product.setPrice(productRequest.getPrice());
        product.setImage(productRequest.getImage());
        product.setCategories(productRequest.getCategories());
        product.setSku(productRequest.getSku());
        product.setBrand(productRequest.getBrand());
        product.setCategories(productRequest.getCategories());

        productRepository.save(product);

    }

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public void deleteProduct(Long id) {
        if (productRepository.existsById(id)) {
            productRepository.deleteById(id);
        } else {
            throw new IllegalArgumentException("Product id not found");
        }
    }

    public boolean checkPrice(String skuCode, String price) {
        Product product = productRepository.findBySku(skuCode).orElse(null);
        if (product == null) {
            return false;
        } else if(product.getPrice() == Integer.parseInt(price)) {
            return true;
        }
        return false;
    }
}
