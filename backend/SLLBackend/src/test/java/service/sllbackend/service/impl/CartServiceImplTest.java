package service.sllbackend.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import service.sllbackend.entity.Cart;
import service.sllbackend.entity.Product;
import service.sllbackend.entity.UserAccount;
import service.sllbackend.repository.CartRepo;
import service.sllbackend.repository.ProductRepo;
import service.sllbackend.repository.UserAccountRepo;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CartServiceImplTest {

    @Mock
    private CartRepo cartRepo;

    @Mock
    private UserAccountRepo userAccountRepo;

    @Mock
    private ProductRepo productRepo;

    @InjectMocks
    private CartServiceImpl cartService;

    private UserAccount mockUser;
    private Product mockProduct;
    private Cart mockCart;

    @BeforeEach
    void setUp() {
        mockUser = new UserAccount();
        mockUser.setId(1);
        mockUser.setUsername("testUser");

        mockProduct = new Product();
        mockProduct.setId(1);

        mockCart = Cart.builder()
                .userAccount(mockUser)
                .product(mockProduct)
                .amount(2)
                .build();
    }

    // ---- 1. getCartByUser ----
    @Test
    void testGetCartByUser_Success() {
        when(userAccountRepo.findByUsername("testUser")).thenReturn(Optional.of(mockUser));
        when(cartRepo.findByUserAccount(mockUser)).thenReturn(List.of(mockCart));

        List<Cart> result = cartService.getCartByUser("testUser");

        assertEquals(1, result.size());
        assertEquals(mockCart, result.get(0));
        verify(userAccountRepo).findByUsername("testUser");
        verify(cartRepo).findByUserAccount(mockUser);
    }

    @Test
    void testGetCartByUser_UserNotFound() {
        when(userAccountRepo.findByUsername("unknown")).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> cartService.getCartByUser("unknown"));
    }

    // ---- 2. addProductToCart ----
    @Test
    void testAddProductToCart_AddNewItem() {
        when(userAccountRepo.findByUsername("testUser")).thenReturn(Optional.of(mockUser));
        when(productRepo.findById(1)).thenReturn(Optional.of(mockProduct));
        when(cartRepo.findByUserAccountAndProduct_Id(mockUser, 1)).thenReturn(Optional.empty());

        cartService.addProductToCart("testUser", 1, 3);

        verify(cartRepo, times(1)).save(any(Cart.class));
    }

    @Test
    void testAddProductToCart_UpdateExistingItem() {
        when(userAccountRepo.findByUsername("testUser")).thenReturn(Optional.of(mockUser));
        when(productRepo.findById(1)).thenReturn(Optional.of(mockProduct));
        when(cartRepo.findByUserAccountAndProduct_Id(mockUser, 1)).thenReturn(Optional.of(mockCart));

        cartService.addProductToCart("testUser", 1, 2);

        assertEquals(4, mockCart.getAmount());
        verify(cartRepo).save(mockCart);
    }

    // ---- 3. removeProductFromCart ----
    @Test
    void testRemoveProductFromCart_Success() {
        when(userAccountRepo.findByUsername("testUser")).thenReturn(Optional.of(mockUser));
        when(cartRepo.findByUserAccountAndProduct_Id(mockUser, 1)).thenReturn(Optional.of(mockCart));

        cartService.removeProductFromCart("testUser", 1);

        verify(cartRepo).delete(mockCart);
    }

    @Test
    void testRemoveProductFromCart_NotFound() {
        when(userAccountRepo.findByUsername("testUser")).thenReturn(Optional.of(mockUser));
        when(cartRepo.findByUserAccountAndProduct_Id(mockUser, 1)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> cartService.removeProductFromCart("testUser", 1));
    }

    // ---- 4. adjustProductAmount ----
    @Test
    void testAdjustProductAmount_UpdateAmount() {
        when(userAccountRepo.findByUsername("testUser")).thenReturn(Optional.of(mockUser));
        when(cartRepo.findByUserAccountAndProduct_Id(mockUser, 1)).thenReturn(Optional.of(mockCart));

        cartService.adjustProductAmount("testUser", 1, 5);

        assertEquals(5, mockCart.getAmount());
        verify(cartRepo).save(mockCart);
    }

    @Test
    void testAdjustProductAmount_RemoveIfZero() {
        CartServiceImpl spyService = Mockito.spy(cartService);
        doNothing().when(spyService).removeProductFromCart(anyString(), anyInt());

        spyService.adjustProductAmount("testUser", 1, 0);

        verify(spyService).removeProductFromCart("testUser", 1);
    }

    @Test
    void testAdjustProductAmount_CartItemNotFound() {
        when(userAccountRepo.findByUsername("testUser")).thenReturn(Optional.of(mockUser));
        when(cartRepo.findByUserAccountAndProduct_Id(mockUser, 1)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> cartService.adjustProductAmount("testUser", 1, 3));
    }
}
