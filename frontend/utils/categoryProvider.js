import { fetchInventory } from "./inventoryProvider"

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

export default fetchCategories
