package ai.zerok.order.controller;

import ai.zerok.order.model.Order;
import ai.zerok.order.dto.OrderRequest;
import ai.zerok.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/order")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public String placeOrder(@RequestBody OrderRequest orderRequest) {
        String id = orderService.placeOrder(orderRequest);
        return "Order Placed Successfully, Order id:"+id;
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.OK)
    public String cancelOrder(@RequestParam("id") String orderId) {
        return orderService.cancelOrder(orderId);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<Order> getOrder() {
        return orderService.getAllOrderID();
    }


    @PutMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateOrder(@RequestParam("id") String orderId, @RequestBody OrderRequest orderRequest) {
        orderService.updateOrder(orderId, orderRequest);
    }
}
