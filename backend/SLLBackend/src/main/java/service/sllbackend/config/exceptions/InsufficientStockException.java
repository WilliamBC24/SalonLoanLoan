package service.sllbackend.config.exceptions;

public class InsufficientStockException extends RuntimeException {
    private final Integer productId;
    private final Integer availableStock;
    private final Integer requestedAmount;

    public InsufficientStockException(Integer productId, Integer availableStock, Integer requestedAmount) {
        super(String.format("Insufficient stock for product ID %d. Available: %d, Requested: %d",
                productId, availableStock, requestedAmount));
        this.productId = productId;
        this.availableStock = availableStock;
        this.requestedAmount = requestedAmount;
    }

    public Integer getProductId() {
        return productId;
    }

    public Integer getAvailableStock() {
        return availableStock;
    }

    public Integer getRequestedAmount() {
        return requestedAmount;
    }
}
