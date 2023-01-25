package ai.zerok.sofademo.controller;

import ai.zerok.sofademo.dto.ProductRequest;
import ai.zerok.sofademo.dto.ProductRequestArr;
import ai.zerok.sofademo.model.Product;
import ai.zerok.sofademo.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/product")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void createProduct(@RequestBody ProductRequest productRequest) {
        productService.createProduct(productRequest);
    }

    @PostMapping("/all")
    @ResponseStatus(HttpStatus.CREATED)
    public void createProduct(@RequestBody ProductRequestArr productRequests) {
        for(ProductRequest p: productRequests.getProductRequestList()){
            productService.createProduct(p);
        }
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<Product> getAllProducts() {
        return productService.getAllProducts();
    }

    @GetMapping("/price")
    @ResponseStatus(HttpStatus.OK)
    public boolean isPriceCorrect(@RequestParam("skuCode") String skuCode, @RequestParam("price") String price){
        return productService.checkPrice(skuCode, price);

    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void deleteProduct(@RequestParam("id") Long productId) {
        productService.deleteProduct(productId);
    }
}
