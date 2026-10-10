package com.example.ecommerce.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.ecommerce.dto.CartItemResponse;
import com.example.ecommerce.dto.CartRequest;
import com.example.ecommerce.dto.CartResponse;
import com.example.ecommerce.entity.Cart;
import com.example.ecommerce.entity.CartItem;
import com.example.ecommerce.entity.Product;
import com.example.ecommerce.entity.User;
import com.example.ecommerce.exception.ResourceNotFoundException;
import com.example.ecommerce.repository.CartItemRepository;
import com.example.ecommerce.repository.CartRepository;
import com.example.ecommerce.repository.ProductRepository;
import com.example.ecommerce.repository.UserRepository;

@Service
public class CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    public CartService(
            CartRepository cartRepository,
            CartItemRepository cartItemRepository,
            ProductRepository productRepository,
            UserRepository userRepository) {

        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public CartResponse addToCart(
            Long userId,
            CartRequest request) {

        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                    new ResourceNotFoundException(
                        "User not found"
                    )
                );

        Product product =
                productRepository.findById(
                        request.getProductId()
                ).orElseThrow(() ->
                    new ResourceNotFoundException(
                        "Product not found"
                    )
                );

        if (product.getStock() < request.getQuantity()) {
            throw new RuntimeException(
                    "Insufficient stock"
            );
        }

        Cart cart =
                cartRepository.findByUserId(userId)
                .orElseGet(() ->
                    cartRepository.save(
                        new Cart(user)
                    )
                );

        CartItem cartItem =
                cartItemRepository
                    .findByCartIdAndProductId(
                        cart.getId(),
                        product.getId()
                    )
                    .orElse(null);

        if (cartItem != null) {

            int newQuantity =
                    cartItem.getQuantity()
                    + request.getQuantity();

            if (newQuantity > product.getStock()) {
                throw new RuntimeException(
                        "Insufficient stock"
                );
            }

            cartItem.setQuantity(newQuantity);

        } else {

            cartItem = new CartItem(
                    cart,
                    product,
                    request.getQuantity()
            );
        }

        cartItemRepository.save(cartItem);

        return getCart(userId);
    }

    public CartResponse getCart(Long userId) {

        Cart cart =
                cartRepository.findByUserId(userId)
                .orElseThrow(() ->
                    new ResourceNotFoundException(
                        "Cart not found"
                    )
                );

        List<CartItemResponse> items =
                cart.getItems()
                    .stream()
                    .map(item -> {

                        double subtotal =
                                item.getProduct().getPrice()
                                * item.getQuantity();

                        return new CartItemResponse(
                                item.getId(),
                                item.getProduct().getId(),
                                item.getProduct().getName(),
                                item.getProduct().getPrice(),
                                item.getQuantity(),
                                subtotal
                        );
                    })
                    .toList();

        double total =
                items.stream()
                     .mapToDouble(
                         CartItemResponse::getSubtotal
                     )
                     .sum();

        return new CartResponse(
                cart.getId(),
                items,
                total
        );
    }

    @Transactional
    public CartResponse updateQuantity(
            Long userId,
            Long itemId,
            int quantity) {

        Cart cart =
                cartRepository.findByUserId(userId)
                .orElseThrow(() ->
                    new ResourceNotFoundException(
                        "Cart not found"
                    )
                );

        CartItem item =
                cartItemRepository.findById(itemId)
                .orElseThrow(() ->
                    new ResourceNotFoundException(
                        "Cart item not found"
                    )
                );

        if (!item.getCart().getId()
                .equals(cart.getId())) {

            throw new RuntimeException(
                    "Cart item does not belong to this user"
            );
        }

        if (quantity <= 0) {
            throw new RuntimeException(
                    "Quantity must be greater than zero"
            );
        }

        if (quantity > item.getProduct().getStock()) {
            throw new RuntimeException(
                    "Insufficient stock"
            );
        }

        item.setQuantity(quantity);

        cartItemRepository.save(item);

        return getCart(userId);
    }

    @Transactional
    public void removeItem(
            Long userId,
            Long itemId) {

        Cart cart =
                cartRepository.findByUserId(userId)
                .orElseThrow(() ->
                    new ResourceNotFoundException(
                        "Cart not found"
                    )
                );

        CartItem item =
                cartItemRepository.findById(itemId)
                .orElseThrow(() ->
                    new ResourceNotFoundException(
                        "Cart item not found"
                    )
                );

        if (!item.getCart().getId()
                .equals(cart.getId())) {

            throw new RuntimeException(
                    "Cart item does not belong to this user"
            );
        }

        cartItemRepository.delete(item);
    }
}