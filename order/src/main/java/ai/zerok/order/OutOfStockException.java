package ai.zerok.order;

public class OutOfStockException extends RuntimeException{

    public OutOfStockException() {
        super("Product is out of stock!");
    }
}
