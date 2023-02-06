import appDetails from "../utils/appDetails";
import mapping from "../utils/mapping";
import axios from "axios";

export async function getProducts() {
    return new Promise(async (resolve, reject) => {
        try {
            const response = await axios.get(appDetails.http_scheme+appDetails.hostname+'/api/inventory/all');            
            resolve(response.data);
        } catch (error) {
            console.log(error);            
        }
    });
}

function getSKU(itemName) {
    return mapping[itemName];
}

export async function placeOrder(cart) {
    let orderItem = {};
    orderItem.orderLineItemDtoList = cart.map((cartItem, index) => {
        return {
            "id": index,
            "skuCode": getSKU(cartItem.name),
            "price": cartItem.price,
            "quantity": cartItem.quantity
        };
    })
    console.log(orderItem);
    return new Promise(async (resolve, reject) => {
        try {
            const hostname = process.env.EXTERNAL_HOSTNAME || appDetails.hostname;
            const response = await axios.post(appDetails.http_scheme+hostname+'/api/order', orderItem);
            resolve(response.data);     
        } catch (error) {
            console.log(error);
        }
    })
}