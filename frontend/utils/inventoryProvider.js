import axios from "axios"
import inventory from "./inventory"
import raxios from "./raxios"
import { LIST_INVENTORY_ENDPOINT } from "./endpoints"
import { STATIC_INVENTORY } from "./staticInventory"

/*
Inventory items should adhere to the following schema:
type Product {
  id: ID!
  categories: [String]!
  price: Float!
  name: String!
  image: String!
  description: String!
  currentInventory: Int!
  brand: String
  sku: ID
}
*/

async function fetchInventory() {
  // const inventory = API.get(apiUrl)
  try {
    return await raxios.get(LIST_INVENTORY_ENDPOINT)
  } catch (err) {
    return await axios.get("http://localhost:3000/data/inventory.json")
  }
}


export { fetchInventory, inventory as staticInventory }
