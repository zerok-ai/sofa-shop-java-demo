import { fetchInventory } from './inventoryProvider'
import { inventoryByCategory } from './inventoryByCategory'
import { getProducts } from '../services/dataservice'

async function inventoryForCategory (category) {
  // const inventory = await getProducts()
  const inventory = await fetchInventory()
  const byCategory = inventoryByCategory(inventory.data)
  console.log({ byCategory })
  return byCategory[category].items
}

export default inventoryForCategory