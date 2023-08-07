package ai.zerok.product.service;

import ai.zerok.product.dto.ProductRequest;
import ai.zerok.product.exception.CouldNotResolveException;
import ai.zerok.product.model.Product;
import ai.zerok.product.repository.ProductRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.net.ConnectException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.joining;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    @Value("${availability.host}")
    private String availabilityHost;

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

    private String getUrl(String host, String endPoint) {
        return "http://" + host + endPoint;
    }

    private String encodeValue(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }

    public boolean isAvailable(String skuCode, String zipCode) {
        String checkAvailabilityEndpoint = "/api/availability";
        String url = getUrl(availabilityHost, checkAvailabilityEndpoint);

        Map<String, String> requestParams = new HashMap<>();
        requestParams.put("skuCode", skuCode);
        requestParams.put("zipCode", zipCode);

        String encodedURL = requestParams.keySet().stream()
                .map(key -> key + "=" + encodeValue(requestParams.get(key)))
                .collect(joining("&", url, ""));

        HttpClient client = new HttpClient();

        GetMethod method = new GetMethod(encodedURL);

        try {
            int statusCode = client.executeMethod(method);

            if (statusCode != HttpStatus.SC_OK) {
                System.err.println("Method failed: " + method.getStatusLine());
            }
            InputStream in = method.getResponseBodyAsStream();

            int numRead = -1;
            byte[] buf = new byte[4 * 1024];

            String responseBody = "";

            while ((numRead = in.read(buf)) != -1) {
                String str = new String(buf, 0, numRead);
                responseBody = responseBody + str;
            }
            if(responseBody.equals("")) {
                throw new IllegalArgumentException("error in fetching product availability");
            }

            ObjectMapper objectMapper = new ObjectMapper();
            Product[] productRequestArr = objectMapper.readValue(responseBody, Product[].class);

            //TODO
            return true;

        } catch (HttpException e) {
            System.err.println("Fatal protocol violation: " + e.getMessage());
            e.printStackTrace();
        } catch (IOException e) {
            if(e instanceof ConnectException){
                throw new CouldNotResolveException();
            }
            System.err.println("Fatal transport error: " + e.getMessage());
            e.printStackTrace();
        } finally {
            method.releaseConnection();
        }

        return false;
    }

    public boolean checkPrice(String skuCode, String price) {
        System.out.println("DEBUG01 product checkPrice called skuCode " + skuCode);
        Product product = null;
        try {
            product = productRepository.findBySku(skuCode).orElse(null);
        }catch (Throwable t){
            t.printStackTrace();
        }
        if (product == null) {
            System.out.println("DEBUG01 product not present");
            return false;
        } else if(product.getPrice() == Integer.parseInt(price)) {
            System.out.println("DEBUG01 product sku " + product.getSku() + " price " + product.getPrice());
            return true;
        }
        System.out.println("DEBUG01 product price do not match");
        return false;
    }
}
