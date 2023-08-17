import { fetchInventory, fetchStaticInventory } from "./inventoryProvider"
import { STATIC_INVENTORY } from "./staticInventory"

async function fetchCategories() {
  const rdata = await fetchInventory()
  const inventory = rdata.data
  const categories = inventory.reduce((acc, next) => {
    next.categories.map((category) => {
      if (acc.includes(category)) return
      acc.push(category)
    })
    return acc
  }, [])
  return Promise.resolve(categories)
}

export async function fetchStaticCategories() {
  const categories = STATIC_INVENTORY.reduce((acc, next) => {
    next.categories.map((category) => {
      if (acc.includes(category)) return
      acc.push(category)
    })
    return acc
  }, [])
  return Promise.resolve(categories)
}

export default fetchCategories
