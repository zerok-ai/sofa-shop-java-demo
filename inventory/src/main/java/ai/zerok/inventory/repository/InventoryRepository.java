package ai.zerok.inventory.repository;

import ai.zerok.inventory.model.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface InventoryRepository extends JpaRepository<Inventory, Long> {
    Optional<List<Inventory>> findBySkuCode(String skuCode);

}
