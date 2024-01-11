package ai.zerok.inventory.controller;

import ai.zerok.inventory.model.InventoryDetailsResponse;
import ai.zerok.inventory.model.InventoryRequest;
import ai.zerok.inventory.model.InventoryRequestArr;
import ai.zerok.inventory.service.InventoryService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.util.JSONPObject;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/inventory")
public class InventoryController {

    private final InventoryService inventoryService;
    private final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private final String JSONARRAY_STRING = "[{\"header\":{\"title\":\"Random JSON Data\",\"date\":\"2023-07-25\",\"author\":\"ChatGPT\"},\"content\":{\"section1\":{\"heading\":\"Introduction\",\"text\":\"This is a randomly generated JSON file with a size of approximately 5KB. It's intended for demonstration and testing purposes only.\"},\"section2\":{\"heading\":\"Data\",\"items\":[{\"id\":1,\"name\":\"Item 1\",\"description\":\"This is the first item in the data list.\",\"quantity\":10,\"price\":19.99},{\"id\":2,\"name\":\"Item 2\",\"description\":\"This is the second item in the data list.\",\"quantity\":5,\"price\":24.99},{\"id\":3,\"name\":\"Item 3\",\"description\":\"This is the third item in the data list.\",\"quantity\":3,\"price\":34.99}]},\"section3\":{\"heading\":\"Users\",\"users\":[{\"id\":101,\"name\":\"John Doe\",\"email\":\"john@example.com\",\"age\":30},{\"id\":102,\"name\":\"Jane Smith\",\"email\":\"jane@example.com\",\"age\":25},{\"id\":103,\"name\":\"Mike Johnson\",\"email\":\"mike@example.com\",\"age\":40},{\"id\":101,\"name\":\"John Doe\",\"email\":\"john@example.com\",\"age\":30},{\"id\":102,\"name\":\"Jane Smith\",\"email\":\"jane@example.com\",\"age\":25},{\"id\":103,\"name\":\"Mike Johnson\",\"email\":\"mike@example.com\",\"age\":40}]}}},{\"header\":{\"title\":\"Random JSON Data\",\"date\":\"2023-07-25\",\"author\":\"ChatGPT\"},\"content\":{\"section1\":{\"heading\":\"Introduction\",\"text\":\"This is a randomly generated JSON file with a size of approximately 5KB. It's intended for demonstration and testing purposes only.\"},\"section2\":{\"heading\":\"Data\",\"items\":[{\"id\":1,\"name\":\"Item 1\",\"description\":\"This is the first item in the data list.\",\"quantity\":10,\"price\":19.99},{\"id\":2,\"name\":\"Item 2\",\"description\":\"This is the second item in the data list.\",\"quantity\":5,\"price\":24.99},{\"id\":3,\"name\":\"Item 3\",\"description\":\"This is the third item in the data list.\",\"quantity\":3,\"price\":34.99}]},\"section3\":{\"heading\":\"Users\",\"users\":[{\"id\":101,\"name\":\"John Doe\",\"email\":\"john@example.com\",\"age\":30},{\"id\":102,\"name\":\"Jane Smith\",\"email\":\"jane@example.com\",\"age\":25},{\"id\":103,\"name\":\"Mike Johnson\",\"email\":\"mike@example.com\",\"age\":40},{\"id\":101,\"name\":\"John Doe\",\"email\":\"john@example.com\",\"age\":30},{\"id\":102,\"name\":\"Jane Smith\",\"email\":\"jane@example.com\",\"age\":25},{\"id\":103,\"name\":\"Mike Johnson\",\"email\":\"mike@example.com\",\"age\":40}]}}},{\"header\":{\"title\":\"Random JSON Data\",\"date\":\"2023-07-25\",\"author\":\"ChatGPT\"},\"content\":{\"section1\":{\"heading\":\"Introduction\",\"text\":\"This is a randomly generated JSON file with a size of approximately 5KB. It's intended for demonstration and testing purposes only.\"},\"section2\":{\"heading\":\"Data\",\"items\":[{\"id\":1,\"name\":\"Item 1\",\"description\":\"This is the first item in the data list.\",\"quantity\":10,\"price\":19.99},{\"id\":2,\"name\":\"Item 2\",\"description\":\"This is the second item in the data list.\",\"quantity\":5,\"price\":24.99},{\"id\":3,\"name\":\"Item 3\",\"description\":\"This is the third item in the data list.\",\"quantity\":3,\"price\":34.99}]},\"section3\":{\"heading\":\"Users\",\"users\":[{\"id\":101,\"name\":\"John Doe\",\"email\":\"john@example.com\",\"age\":30},{\"id\":102,\"name\":\"Jane Smith\",\"email\":\"jane@example.com\",\"age\":25},{\"id\":103,\"name\":\"Mike Johnson\",\"email\":\"mike@example.com\",\"age\":40},{\"id\":101,\"name\":\"John Doe\",\"email\":\"john@example.com\",\"age\":30},{\"id\":102,\"name\":\"Jane Smith\",\"email\":\"jane@example.com\",\"age\":25},{\"id\":103,\"name\":\"Mike Johnson\",\"email\":\"mike@example.com\",\"age\":40}]}}},{\"header\":{\"title\":\"Random JSON Data\",\"date\":\"2023-07-25\",\"author\":\"ChatGPT\"},\"content\":{\"section1\":{\"heading\":\"Introduction\",\"text\":\"This is a randomly generated JSON file with a size of approximately 5KB. It's intended for demonstration and testing purposes only.\"},\"section2\":{\"heading\":\"Data\",\"items\":[{\"id\":1,\"name\":\"Item 1\",\"description\":\"This is the first item in the data list.\",\"quantity\":10,\"price\":19.99},{\"id\":2,\"name\":\"Item 2\",\"description\":\"This is the second item in the data list.\",\"quantity\":5,\"price\":24.99},{\"id\":3,\"name\":\"Item 3\",\"description\":\"This is the third item in the data list.\",\"quantity\":3,\"price\":34.99}]},\"section3\":{\"heading\":\"Users\",\"users\":[{\"id\":101,\"name\":\"John Doe\",\"email\":\"john@example.com\",\"age\":30},{\"id\":102,\"name\":\"Jane Smith\",\"email\":\"jane@example.com\",\"age\":25},{\"id\":103,\"name\":\"Mike Johnson\",\"email\":\"mike@example.com\",\"age\":40},{\"id\":101,\"name\":\"John Doe\",\"email\":\"john@example.com\",\"age\":30},{\"id\":102,\"name\":\"Jane Smith\",\"email\":\"jane@example.com\",\"age\":25},{\"id\":103,\"name\":\"Mike Johnson\",\"email\":\"mike@example.com\",\"age\":40}]}}},{\"header\":{\"title\":\"Random JSON Data\",\"date\":\"2023-07-25\",\"author\":\"ChatGPT\"},\"content\":{\"section1\":{\"heading\":\"Introduction\",\"text\":\"This is a randomly generated JSON file with a size of approximately 5KB. It's intended for demonstration and testing purposes only.\"},\"section2\":{\"heading\":\"Data\",\"items\":[{\"id\":1,\"name\":\"Item 1\",\"description\":\"This is the first item in the data list.\",\"quantity\":10,\"price\":19.99},{\"id\":2,\"name\":\"Item 2\",\"description\":\"This is the second item in the data list.\",\"quantity\":5,\"price\":24.99},{\"id\":3,\"name\":\"Item 3\",\"description\":\"This is the third item in the data list.\",\"quantity\":3,\"price\":34.99}]},\"section3\":{\"heading\":\"Users\",\"users\":[{\"id\":101,\"name\":\"John Doe\",\"email\":\"john@example.com\",\"age\":30},{\"id\":102,\"name\":\"Jane Smith\",\"email\":\"jane@example.com\",\"age\":25},{\"id\":103,\"name\":\"Mike Johnson\",\"email\":\"mike@example.com\",\"age\":40},{\"id\":101,\"name\":\"John Doe\",\"email\":\"john@example.com\",\"age\":30},{\"id\":102,\"name\":\"Jane Smith\",\"email\":\"jane@example.com\",\"age\":25},{\"id\":103,\"name\":\"Mike Johnson\",\"email\":\"mike@example.com\",\"age\":40}]}}}]";

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public boolean isInStock(@RequestParam("skuCode") String skuCode, @RequestParam("quantity") String quantity){
        return inventoryService.isInStock(skuCode, quantity);
    }

    @GetMapping("/status/{statusCode}")
    public void getAllInventory(@PathVariable int statusCode){
        // Validate the statusCode (optional)
        if (statusCode >= 100 && statusCode <= 599) {
            // Set the HTTP status based on the path variable
            System.out.println("/status API with status=" + statusCode);
            throw new ResponseStatusException(HttpStatus.valueOf(statusCode), "Custom status code");
        } else {
            // Handle invalid status code
            System.out.println("/status API without status, throwing bad request (400)");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid status code");
        }
    }

    @GetMapping("/all")
    @ResponseStatus(HttpStatus.OK)
    public List<InventoryDetailsResponse> getAllInventory(@RequestParam(value = "rndon", required = false) boolean rndOn,
                                                          @RequestParam(value = "rndmemon", required = false) boolean rndMemOn,
            @RequestParam(value = "rndlimit", required = false, defaultValue = "0") int rndLimit){
        long startTime = System.currentTimeMillis();
        List<InventoryDetailsResponse> response = inventoryService.getAll();
        if(rndOn) {
            generateNumbers(rndLimit);
//            System.out.println("random numbers generated " + rndLimit);
        }

        if(rndMemOn) {
            try {
                List<JsonNode> listJsonNodes = loadJson(rndLimit);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
//            System.out.println("random jsons generated " + rndLimit);
        }

        long currentTime = System.currentTimeMillis();
        long diff = currentTime - startTime;
        System.out.println("Time taken [INVENTORY /all] " + diff);
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

    private List<JsonNode> loadJson(int n) throws JsonProcessingException {
        List<JsonNode> listJsonNodes = new ArrayList<>();
        for(int i = 0;i < n; i++){
            listJsonNodes.add(parseJson());
        }

        return listJsonNodes;
    }

    private JsonNode parseJson() throws JsonProcessingException {
        JsonNode jsonNode = OBJECT_MAPPER.readTree(JSONARRAY_STRING);
        return jsonNode;
    }

//    public void loadFileFromResources() {
//        // Specify the path to the file inside the resources folder
//        String filePath = "hello.json";
//
//        // Load the resource using ClassPathResource
//        ClassPathResource resource = new ClassPathResource(filePath);
//
//        // Perform operations on the resource, e.g., read the contents of the file
//        try (InputStream inputStream = resource.getInputStream();
//             BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
//            String line;
//            while ((line = reader.readLine()) != null) {
//                // Process each line of the file
//                System.out.println(line);
//            }
//        }
//    }
}
