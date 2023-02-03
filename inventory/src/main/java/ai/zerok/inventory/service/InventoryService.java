package ai.zerok.inventory.service;

import ai.zerok.inventory.model.*;
import ai.zerok.inventory.repository.InventoryRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

import static java.util.stream.Collectors.joining;

@Service
@RequiredArgsConstructor
public class InventoryService {
    private final InventoryRepository inventoryRepository;

    @Value("${product.host}")
    private String productHost;

    @Transactional(readOnly = true)
    public boolean isInStock(String skuCode, String quantity) {
        int qty = 0;
        if (quantity != null && !quantity.trim().equals("")) {
            qty = Integer.parseInt(quantity);
        }
        List<Inventory> inventory = inventoryRepository.findBySkuCode(skuCode).orElse(null);
        return inventory != null && inventory.get(0).getCurrentInventory() >= qty;
    }


    public List<InventoryDetailsResponse> getAll() {
        List<Inventory> inventoryList = inventoryRepository.findAll();
        Map<String, Inventory> skuToQuantityMap = new HashMap<>();
        for (Inventory inventory: inventoryList) {
            skuToQuantityMap.put(inventory.getSkuCode(), inventory);
        }
        ProductList productList = getProductDetailsForSkuCodes();

        List<InventoryDetailsResponse> responseList = new ArrayList<>();

        for (Product product: productList.getProductList()) {
            InventoryDetailsResponse response = new InventoryDetailsResponse();

            response.setProductId(product.getId());
            response.setSkuId(skuToQuantityMap.get(product.getSku()).getId());
            response.setCurrentInventory(skuToQuantityMap.get(product.getSku()).getCurrentInventory());
            response.setBrand(product.getBrand());
            response.setImage(product.getImage());
            response.setName(product.getName());
            response.setCategories(product.getCategories());
            response.setPrice(product.getPrice());
            response.setSku(product.getSku());
            response.setDescription(product.getDescription());

            responseList.add(response);
        }

        return responseList;
    }


    private String encodeValue(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }


    private String getUrl(String host, String endPoint) {
        return "http://" + host + endPoint;
    }

    private ProductList getProductDetailsForSkuCodes() {

        String getAllProductEndpoint = "/api/product";
        String url = getUrl(productHost, getAllProductEndpoint);

        Map<String, String> requestParams = new HashMap<>();
//        requestParams.put("skuCodes", skuCodes);

        String encodedURL = requestParams.keySet().stream()
                .map(key -> key + "=" + encodeValue(requestParams.get(key)))
                .collect(joining("&", url, ""));

        HttpClient client = new HttpClient();

        GetMethod method = new GetMethod(encodedURL);
        ProductList productRequestList = new ProductList();

        try {
            int statusCode = client.executeMethod(method);

            if (statusCode != HttpStatus.SC_OK) {
                System.err.println("Method failed: " + method.getStatusLine());
            }
            byte[] responseBody = method.getResponseBody();

            if(responseBody == null) {
                throw new IllegalArgumentException("error in fetching product details");
            }
            String response = new String(responseBody, StandardCharsets.UTF_8);

            ObjectMapper objectMapper = new ObjectMapper();
            Product[] productRequestArr = objectMapper.readValue(response, Product[].class);
            productRequestList.setProductList(Arrays.asList(productRequestArr));


        } catch (HttpException e) {
            System.err.println("Fatal protocol violation: " + e.getMessage());
            e.printStackTrace();
        } catch (IOException e) {
            System.err.println("Fatal transport error: " + e.getMessage());
            e.printStackTrace();
        } finally {
            method.releaseConnection();
        }

        return productRequestList;

    }

    public void updateQuantity(InventoryRequest request) {
        if (request.getCurrentInventory() == 0) {
            throw new IllegalArgumentException("quantity should be more than 0");
        }

        List<Inventory> inventory = inventoryRepository.findBySkuCode(request.getSku()).orElse(null);

        if (inventory != null) {
            Inventory inv = inventory.get(0);
            inv.setCurrentInventory(inv.getCurrentInventory() + request.getCurrentInventory());
            inventoryRepository.save(inv);
        } else {
            throw new IllegalArgumentException("inventory does not exist");
        }
    }


    public void createInventory(InventoryRequest request) {
        if (request.getCurrentInventory() == 0 || request.getSku() == null || request.getSku().trim().equals("")) {
            throw new IllegalArgumentException("quantity should be more than 0");
        }
        Inventory inventory = new Inventory();
        inventory.setSkuCode(request.getSku());
        inventory.setCurrentInventory(request.getCurrentInventory());

        inventoryRepository.save(inventory);
    }

    public void deleteInventory(String inventoryId) {
        Long id = Long.parseLong(inventoryId);
        if (inventoryRepository.existsById(id)) {
            inventoryRepository.deleteById(id);
        } else {
            throw new IllegalArgumentException("inventory id does not exist");
        }
    }
}
