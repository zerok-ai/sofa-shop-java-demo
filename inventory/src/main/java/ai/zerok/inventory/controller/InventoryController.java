package ai.zerok.inventory.controller;

import ai.zerok.inventory.model.InventoryDetailsResponse;
import ai.zerok.inventory.model.InventoryRequest;
import ai.zerok.inventory.model.InventoryRequestArr;
import ai.zerok.inventory.service.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    public List<InventoryDetailsResponse> getAllInventory(@RequestParam(value = "rndon", required = false) boolean rndOn,
            @RequestParam(value = "rndlimit", required = false, defaultValue = "0") int rndLimit){
        List<InventoryDetailsResponse> response = inventoryService.getAll();
        if(rndOn) {
            generateNumbers(rndLimit);
            System.out.println("random numbers generated " + rndLimit);
        }

        return response;
    }

    @PutMapping
    @ResponseStatus(HttpStatus.OK)
    public void updateQuantity(@RequestBody InventoryRequest inventoryRequest){
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

    @GetMapping("/err503")
    @ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
    public void Error500Inventory(){

    }

    @GetMapping("/err422")
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    public void Error5422Inventory(){

    }

    @GetMapping("/exception")
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    public void Exception(){
        throw new RuntimeException("Hello");
    }

    private void generateNumbers(int n){
        if(n <= 0){
            n = 100;
        }
        for(int i = 0;i < n; i++){
            Math.random();
        }
    }
}
