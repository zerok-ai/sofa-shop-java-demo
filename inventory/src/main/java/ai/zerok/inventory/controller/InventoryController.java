package ai.zerok.inventory.controller;

import ai.zerok.inventory.model.InventoryDetailsResponse;
import ai.zerok.inventory.model.InventoryRequest;
import ai.zerok.inventory.model.InventoryRequestArr;
import ai.zerok.inventory.service.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/inventory")
public class InventoryController {

    private final InventoryService inventoryService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public boolean isInStock(@RequestParam("skuCode") String skuCode, @RequestParam("quantity") String quantity){
        return inventoryService.isInStock(skuCode, quantity);
    }

    @GetMapping("/all")
    @ResponseStatus(HttpStatus.OK)
    public List<InventoryDetailsResponse> getAllInventory(){
        return inventoryService.getAll();
    }

    @PutMapping
    @ResponseStatus(HttpStatus.OK)
    public void updateQuantity(@RequestHeader Map<String, String> allHeaders, @RequestBody InventoryRequest inventoryRequest){
        inventoryService.updateQuantity(inventoryRequest);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    public void createInventory(@RequestBody InventoryRequest inventoryRequest){
        inventoryService.createInventory(inventoryRequest);
    }

    @PostMapping("/all")
    @ResponseStatus(HttpStatus.OK)
    public void createBulkInventory(@RequestBody InventoryRequestArr inventoryRequestArr){
        for (InventoryRequest i: inventoryRequestArr.getInventoryRequestList()) {
            inventoryService.createInventory(i);
        }
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.OK)
    public void DeleteInventory(@RequestParam("id") String inventoryId){
        inventoryService.deleteInventory(inventoryId);
    }
}
