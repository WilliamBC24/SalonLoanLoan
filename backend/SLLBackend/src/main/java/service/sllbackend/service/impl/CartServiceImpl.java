package service.sllbackend.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import service.sllbackend.entity.Cart;
import service.sllbackend.entity.Product;
import service.sllbackend.entity.UserAccount;
import service.sllbackend.repository.CartRepo;
import service.sllbackend.repository.ProductRepo;
import service.sllbackend.repository.UserAccountRepo;
import service.sllbackend.service.CartService;
import service.sllbackend.service.InventoryService;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {
    private final CartRepo cartRepo;
    private final UserAccountRepo userAccountRepo;
    private final ProductRepo productRepo;
    private final InventoryService inventoryService;

    @Override
    @Transactional(readOnly = true)
    public List<Cart> getCartByUser(String username) {
        UserAccount userAccount = userAccountRepo.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return cartRepo.findByUserAccount(userAccount);
    }

    @Override
    public int getCartCount(String username) {
        return userAccountRepo.findByUsername(username)
                .map(cartRepo::countCartByUserAccount)
                .orElse(0);
    }


    @Override
    @Transactional
    public void addProductToCart(String username, Integer productId, Integer amount) {
        UserAccount userAccount = userAccountRepo.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Product product = productRepo.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        
        // Check if product has stock
        Integer availableStock = inventoryService.getAvailableStock(productId);
        if (availableStock <= 0) {
            throw new RuntimeException("Product is out of stock");
        }
        
        Optional<Cart> existingCart = cartRepo.findByUserAccountAndProduct_Id(userAccount, productId);
        
        if (existingCart.isPresent()) {
            Cart cart = existingCart.get();
            int newAmount = cart.getAmount() + amount;
            
            // Check if new amount exceeds available stock
            if (newAmount > availableStock) {
                newAmount = availableStock; // Cap to available stock
            }
            
            cart.setAmount(newAmount);
            cartRepo.save(cart);
        } else {
            // Check if amount exceeds available stock
            if (amount > availableStock) {
                amount = availableStock; // Cap to available stock
            }
            
            Cart cart = Cart.builder()
                    .userAccount(userAccount)
                    .product(product)
                    .amount(amount)
                    .build();
            cartRepo.save(cart);
        }
    }

    @Override
    @Transactional
    public void removeProductFromCart(String username, Integer productId) {
        UserAccount userAccount = userAccountRepo.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Cart cart = cartRepo.findByUserAccountAndProduct_Id(userAccount, productId)
                .orElseThrow(() -> new RuntimeException("Cart item not found"));
        cartRepo.delete(cart);
    }

    @Override
    @Transactional
    public void adjustProductAmount(String username, Integer productId, Integer amount) {
        if (amount <= 0) {
            removeProductFromCart(username, productId);
            return;
        }
        
        // Check stock availability
        Integer availableStock = inventoryService.getAvailableStock(productId);
        if (amount > availableStock) {
            amount = availableStock; // Cap to available stock
        }
        
        UserAccount userAccount = userAccountRepo.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Cart cart = cartRepo.findByUserAccountAndProduct_Id(userAccount, productId)
                .orElseThrow(() -> new RuntimeException("Cart item not found"));
        cart.setAmount(amount);
        cartRepo.save(cart);
    }
}
