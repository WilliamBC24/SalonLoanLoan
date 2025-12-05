package service.sllbackend.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import service.sllbackend.config.exceptions.InsufficientStockException;
import service.sllbackend.entity.Cart;
import service.sllbackend.entity.Product;
import service.sllbackend.entity.UserAccount;
import service.sllbackend.enumerator.AccountStatus;
import service.sllbackend.enumerator.Gender;
import service.sllbackend.repository.CartRepo;
import service.sllbackend.repository.ProductRepo;
import service.sllbackend.repository.UserAccountRepo;
import service.sllbackend.service.impl.CartServiceImpl;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CartServiceTest {

    @Mock
    private CartRepo cartRepo;

    @Mock
    private UserAccountRepo userAccountRepo;

    @Mock
    private ProductRepo productRepo;

    @Mock
    private InventoryService inventoryService;

    @InjectMocks
    private CartServiceImpl cartService;

    private UserAccount testUser;
    private Product testProduct;

    @BeforeEach
    void setUp() {
        testUser = UserAccount.builder()
                .id(1)
                .username("testuser")
                .password("password")
                .gender(Gender.MALE)
                .birthDate(LocalDate.of(2000, 1, 1))
                .phoneNumber("0999999999")
                .accountStatus(AccountStatus.ACTIVE)
                .build();

        testProduct = Product.builder()
                .id(1)
                .productName("Test Product")
                .currentPrice(10000)
                .productDescription("Test Description")
                .activeStatus(true)
                .build();
    }

    @Test
    void addProductToCart_ShouldThrowException_WhenProductOutOfStock() {
        when(userAccountRepo.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(productRepo.findById(1)).thenReturn(Optional.of(testProduct));
        when(inventoryService.getAvailableStock(1)).thenReturn(0);

        InsufficientStockException exception = assertThrows(
                InsufficientStockException.class,
                () -> cartService.addProductToCart("testuser", 1, 1)
        );

        assertEquals(1, exception.getProductId());
        assertEquals(0, exception.getAvailableStock());
        assertEquals(1, exception.getRequestedAmount());
    }

    @Test
    void addProductToCart_ShouldThrowException_WhenRequestedAmountExceedsStock() {
        when(userAccountRepo.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(productRepo.findById(1)).thenReturn(Optional.of(testProduct));
        when(inventoryService.getAvailableStock(1)).thenReturn(5);
        when(cartRepo.findByUserAccountAndProduct_Id(testUser, 1)).thenReturn(Optional.empty());

        InsufficientStockException exception = assertThrows(
                InsufficientStockException.class,
                () -> cartService.addProductToCart("testuser", 1, 10)
        );

        assertEquals(1, exception.getProductId());
        assertEquals(5, exception.getAvailableStock());
        assertEquals(10, exception.getRequestedAmount());
    }

    @Test
    void addProductToCart_ShouldThrowException_WhenNewAmountExceedsStock() {
        Cart existingCart = Cart.builder()
                .userAccount(testUser)
                .product(testProduct)
                .amount(3)
                .build();

        when(userAccountRepo.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(productRepo.findById(1)).thenReturn(Optional.of(testProduct));
        when(inventoryService.getAvailableStock(1)).thenReturn(5);
        when(cartRepo.findByUserAccountAndProduct_Id(testUser, 1)).thenReturn(Optional.of(existingCart));

        InsufficientStockException exception = assertThrows(
                InsufficientStockException.class,
                () -> cartService.addProductToCart("testuser", 1, 5)
        );

        assertEquals(1, exception.getProductId());
        assertEquals(5, exception.getAvailableStock());
        assertEquals(8, exception.getRequestedAmount()); // 3 + 5 = 8
    }

    @Test
    void addProductToCart_ShouldSucceed_WhenStockIsAvailable() {
        when(userAccountRepo.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(productRepo.findById(1)).thenReturn(Optional.of(testProduct));
        when(inventoryService.getAvailableStock(1)).thenReturn(10);
        when(cartRepo.findByUserAccountAndProduct_Id(testUser, 1)).thenReturn(Optional.empty());
        when(cartRepo.save(any(Cart.class))).thenAnswer(invocation -> invocation.getArgument(0));

        assertDoesNotThrow(() -> cartService.addProductToCart("testuser", 1, 5));

        verify(cartRepo, times(1)).save(any(Cart.class));
    }

    @Test
    void adjustProductAmount_ShouldThrowException_WhenAmountExceedsStock() {
        when(inventoryService.getAvailableStock(1)).thenReturn(5);

        InsufficientStockException exception = assertThrows(
                InsufficientStockException.class,
                () -> cartService.adjustProductAmount("testuser", 1, 10)
        );

        assertEquals(1, exception.getProductId());
        assertEquals(5, exception.getAvailableStock());
        assertEquals(10, exception.getRequestedAmount());
    }

    @Test
    void adjustProductAmount_ShouldSucceed_WhenStockIsAvailable() {
        Cart existingCart = Cart.builder()
                .userAccount(testUser)
                .product(testProduct)
                .amount(3)
                .build();

        when(inventoryService.getAvailableStock(1)).thenReturn(10);
        when(userAccountRepo.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(cartRepo.findByUserAccountAndProduct_Id(testUser, 1)).thenReturn(Optional.of(existingCart));
        when(cartRepo.save(any(Cart.class))).thenAnswer(invocation -> invocation.getArgument(0));

        assertDoesNotThrow(() -> cartService.adjustProductAmount("testuser", 1, 8));

        verify(cartRepo, times(1)).save(any(Cart.class));
    }

    @Test
    void adjustProductAmount_ShouldRemoveFromCart_WhenAmountIsZero() {
        Cart existingCart = Cart.builder()
                .userAccount(testUser)
                .product(testProduct)
                .amount(3)
                .build();

        when(userAccountRepo.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(cartRepo.findByUserAccountAndProduct_Id(testUser, 1)).thenReturn(Optional.of(existingCart));

        assertDoesNotThrow(() -> cartService.adjustProductAmount("testuser", 1, 0));

        verify(cartRepo, times(1)).delete(existingCart);
    }
}
