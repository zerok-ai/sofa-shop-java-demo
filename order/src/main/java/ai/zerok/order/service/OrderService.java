package ai.zerok.order.service;


import ai.zerok.order.OutOfStockException;
import ai.zerok.order.dto.InventoryRequest;
import ai.zerok.order.dto.OrderLineItemDto;
import ai.zerok.order.dto.OrderRequest;
import ai.zerok.order.model.Order;
import ai.zerok.order.model.OrderLineItem;
import ai.zerok.order.repository.OrderRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PutMethod;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static java.util.stream.Collectors.joining;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderService {

    private final OrderRepository orderRepository;

    private final String productEndpoint = "/api/product";
    private final String productPriceEndpoint = "/api/product/price";
    private final String inventoryEndpoint = "/api/inventory";

    @Value("${inventory.host}")
    private String inventoryHost;

    @Value("${product.host}")
    private String productHost;


    private String getUrl(String host, String endPoint) {
        return "http://" + host + endPoint;
    }

    public String placeOrder(OrderRequest orderRequest) {
        Order order = new Order();
        order.setOrderNumber(UUID.randomUUID().toString());

        List<OrderLineItem> orderLineItems = orderRequest.getOrderLineItemDtoList()
                .stream()
                .map(this::mapToDto)
                .toList();
        order.setOrderLineItemList(orderLineItems);

        order.setOrderLineItemList(orderLineItems);

        checkStockInInventory(order);
        checkProduct(order);
        order.setStatus("PLACED");
        Order savedOrder = orderRepository.save(order);
        try {
            updateInventory(order.getOrderLineItemList(), false);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        return savedOrder.getOrderNumber();
    }

    public String cancelOrder(String orderId) {

        Order order = orderRepository.findById(Long.parseLong(orderId)).orElse(null);
        if (order != null) {
            if (order.getStatus().equals("CANCELLED")) {
                throw new IllegalArgumentException(String.format("Order: %s already deleted", orderId));
            }
            order.setStatus("CANCELLED");
            orderRepository.save(order);

            try {
                updateInventory(order.getOrderLineItemList(), true);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }

//            List<Boolean> booleans = order.getOrderLineItemList().stream().map(orderLineItem ->
//                    webClient.put()
//                            .uri(url)
//                            .body(BodyInserters.fromValue(getBodyMap(orderLineItem, true)))
//                            .retrieve()
//                            .bodyToMono(Boolean.class)
//                            .block()
//            ).toList();

            return String.format("Order: %s cancelled successfully", orderId);
        }
        throw new IllegalArgumentException(String.format("Order: %s not found", orderId));
    }

    private void updateInventory(List<OrderLineItem> orderLineItemList, boolean add) throws JsonProcessingException {
        for (OrderLineItem orderLineItem: orderLineItemList) {
//            String url = "http://inventory.default.svc.cluster.local/api/inventory";
//            String url = "http://localhost:8081/api/inventory";
            String url = getUrl(inventoryHost, inventoryEndpoint);


            HttpClient client = new HttpClient();

            PutMethod method = new PutMethod(url);
            int qtyToUpdate = add? orderLineItem.getQuantity(): -orderLineItem.getQuantity();

            InventoryRequest request = new InventoryRequest(orderLineItem.getId(), orderLineItem.getSkuCode(), qtyToUpdate, orderLineItem.getPrice());

            String s = new ObjectMapper().writeValueAsString(request);
            method.setRequestBody(s);
            method.addRequestHeader("Content-Type", "application/json; charset=utf8");


            try {
                // Execute the method.
                int statusCode = client.executeMethod(method);

                if (statusCode != HttpStatus.SC_OK) {
                    System.err.println("Method failed: " + method.getStatusLine());
                }

            } catch (HttpException e) {
                System.err.println("Fatal protocol violation: " + e.getMessage());
                e.printStackTrace();
            } catch (IOException e) {
                System.err.println("Fatal transport error: " + e.getMessage());
                e.printStackTrace();
            } finally {
                method.releaseConnection();
            }
        }
    }

    private void checkStockInInventory(Order order) {

        for (OrderLineItem orderLineItem : order.getOrderLineItemList()) {
            Map<String, String> requestParams = new HashMap<>();
            requestParams.put("skuCode", orderLineItem.getSkuCode());
            requestParams.put("quantity", orderLineItem.getQuantity().toString());
//            String url = "http://inventory.default.svc.cluster.local/api/inventory?";
//            String url = "http://localhost:8081/api/inventory?";
            String url = getUrl(inventoryHost, inventoryEndpoint);
            url+="?";


            String encodedURL = requestParams.keySet().stream()
                    .map(key -> key + "=" + encodeValue(requestParams.get(key)))
                    .collect(joining("&", url, ""));
            System.err.println(encodedURL);
            System.err.println("---------------------------------------------------------------");

            HttpClient client = new HttpClient();

            GetMethod method = new GetMethod(encodedURL);

            try {
                int statusCode = client.executeMethod(method);

                if (statusCode != HttpStatus.SC_OK) {
                    System.err.println("Method failed: " + method.getStatusLine());
                }
                byte[] responseBody = method.getResponseBody();
                String resp = new String(responseBody, StandardCharsets.UTF_8);
                System.err.println(resp);
                boolean response = Boolean.parseBoolean(resp);
                if(!response) {
                    throw new IllegalArgumentException("item not in stock");
//                    throw new OutOfStockException();
                }

            } catch (HttpException e) {
                System.err.println("Fatal protocol violation: " + e.getMessage());
                e.printStackTrace();
            } catch (IOException e) {
                System.err.println("Fatal transport error: " + e.getMessage());
                e.printStackTrace();
            } finally {
                method.releaseConnection();
            }
        }
    }

    private void checkProduct(Order order) {

        for (OrderLineItem orderLineItem : order.getOrderLineItemList()) {
            Map<String, String> requestParams = new HashMap<>();
            requestParams.put("skuCode", orderLineItem.getSkuCode());
            requestParams.put("price", orderLineItem.getPrice().toString());
//            String url = "http://product.default.svc.cluster.local/api/product/price?";
//            String url = "http://localhost:8080/api/product/price?";
            String url = getUrl(productHost, productPriceEndpoint);
            url += "?";



            String encodedURL = requestParams.keySet().stream()
                    .map(key -> key + "=" + encodeValue(requestParams.get(key)))
                    .collect(joining("&", url, ""));
            System.err.println(encodedURL);

            HttpClient client = new HttpClient();

            GetMethod method = new GetMethod(encodedURL);

            try {
                int statusCode = client.executeMethod(method);

                if (statusCode != HttpStatus.SC_OK) {
                    System.err.println("Method failed: " + method.getStatusLine());
                }
                byte[] responseBody = method.getResponseBody();

                System.err.println(encodedURL);
                String res = new String(responseBody, StandardCharsets.UTF_8);
                boolean response = Boolean.parseBoolean(res);
                if(!response) {
                    throw new IllegalArgumentException("item price do not match");
                }

            } catch (HttpException e) {
                System.err.println("Fatal protocol violation: " + e.getMessage());
                e.printStackTrace();
            } catch (IOException e) {
                System.err.println("Fatal transport error: " + e.getMessage());
                e.printStackTrace();
            } finally {
                method.releaseConnection();
            }
        }
    }

    private String encodeValue(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }


    private OrderLineItem mapToDto(OrderLineItemDto orderLineItemDto) {
        OrderLineItem orderLineItem = new OrderLineItem();
        orderLineItem.setPrice(orderLineItemDto.getPrice());
        orderLineItem.setQuantity(orderLineItemDto.getQuantity());
        orderLineItem.setSkuCode(orderLineItemDto.getSkuCode());
        return orderLineItem;
    }

    public List<Order> getAllOrderID() {
        return orderRepository.findAll();
    }


    private Map<String, String> getBodyMap(OrderLineItem orderLineItem, boolean addQuantity) {
        Map<String, String> bodyMap = new HashMap();
        Integer qty = addQuantity ? orderLineItem.getQuantity() : -orderLineItem.getQuantity();
        bodyMap.put("skuCode", orderLineItem.getSkuCode());
        bodyMap.put("quantity", qty.toString());
        return bodyMap;

    }

    public void updateOrder(String orderId, OrderRequest orderRequest) {
        Long id = Long.parseLong(orderId);
        if (orderRepository.existsById(id)) {
            Order order = orderRepository.findById(id).orElse(null);
            if (order != null) {
                List<OrderLineItem> orderLineItems = orderRequest.getOrderLineItemDtoList()
                        .stream()
                        .map(this::mapToDto)
                        .toList();
                order.setOrderLineItemList(orderLineItems);
                orderRepository.save(order);
            }
        } else {
            throw new IllegalArgumentException("Invalid order id");
        }
    }
}
