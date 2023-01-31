import appDetails from "../utils/appDetails";
import axios from "axios";

export async function getProducts() {
    return new Promise(async (resolve, reject) => {
        const response = await axios.get(appDetails.http_scheme+appDetails.hostname+'/api/inventory/all');
        resolve(response.data);
    });
}

export async function placeOrder(cart) {
    let orderItem = {};
    orderItem.orderLineItemDtoList = cart.map((cartItem, index) => {
        return {
            "id": index,
            "skuCode": cartItem.sku,
            "price": cartItem.price,
            "quantity": cartItem.quantity
        };
    })
    console.log(orderItem);
    return new Promise(async (resolve, reject) => {
        const response = await axios.post(appDetails.http_scheme+appDetails.hostname+'/api/order', orderItem);
        resolve(response.data);
    })
}