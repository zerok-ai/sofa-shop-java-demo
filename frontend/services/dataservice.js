import appDetails from "../utils/appDetails";
import mapping from "../utils/mapping";
import axios from "axios";
import raxios from "../utils/raxios"
import { LIST_INVENTORY_ENDPOINT, ORDER_ENDPOINT } from "../utils/endpoints"

export async function getProducts() {
  return new Promise(async (resolve, reject) => {
    try {
      const response = await raxios.get(LIST_INVENTORY_ENDPOINT)
      resolve(response.data)
    } catch (error) {
      console.log(error)
    }
  })
}

function getSKU(itemName) {
  return mapping[itemName]
}

export async function placeOrder(cart) {
  let orderItem = {}
  orderItem.orderLineItemDtoList = cart.map((cartItem, index) => {
    return {
      id: index,
      skuCode: getSKU(cartItem.name),
      price: cartItem.price,
      quantity: cartItem.quantity,
    }
  })
  console.log(orderItem)
  return new Promise(async (resolve, reject) => {
    try {
      const response = await raxios.post(ORDER_ENDPOINT, orderItem)
      resolve(response.data)
    } catch (error) {
      console.log(error)
      reject(error)
    }
  })
}