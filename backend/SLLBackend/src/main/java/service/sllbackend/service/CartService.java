package service.sllbackend.service;

import service.sllbackend.entity.Cart;

import java.util.List;

public interface CartService {
    List<Cart> getCartByUser(String username);
    void addProductToCart(String username, Integer productId, Integer amount);
    void removeProductFromCart(String username, Integer productId);
    void adjustProductAmount(String username, Integer productId, Integer amount);
}
