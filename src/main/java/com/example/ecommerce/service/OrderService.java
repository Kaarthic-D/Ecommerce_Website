package com.example.ecommerce.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.ecommerce.dto.OrderItemResponse;
import com.example.ecommerce.dto.OrderResponse;
import com.example.ecommerce.entity.Cart;
import com.example.ecommerce.entity.CartItem;
import com.example.ecommerce.entity.Order;
import com.example.ecommerce.entity.OrderItem;
import com.example.ecommerce.entity.OrderStatus;
import com.example.ecommerce.entity.Product;
import com.example.ecommerce.entity.User;
import com.example.ecommerce.exception.ResourceNotFoundException;
import com.example.ecommerce.repository.CartRepository;
import com.example.ecommerce.repository.OrderRepository;
import com.example.ecommerce.repository.UserRepository;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;
    private final UserRepository userRepository;

    public OrderService(
            OrderRepository orderRepository,
            CartRepository cartRepository,
            UserRepository userRepository) {

        this.orderRepository = orderRepository;
        this.cartRepository = cartRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public OrderResponse placeOrder(Long userId) {

        // 1. Find user
        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                    new ResourceNotFoundException(
                        "User not found"));

        // 2. Find cart
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() ->
                    new ResourceNotFoundException(
                        "Cart not found"));

        // 3. Check cart is not empty
        if (cart.getItems().isEmpty()) {
            throw new RuntimeException(
                    "Cannot place order with empty cart");
        }

        // 4. Check stock
        for (CartItem cartItem : cart.getItems()) {

            Product product = cartItem.getProduct();

            if (product.getStock() < cartItem.getQuantity()) {
                throw new RuntimeException(
                        "Insufficient stock for product: "
                        + product.getName());
            }
        }

        // 5. Calculate total
        double totalAmount = 0;

        for (CartItem cartItem : cart.getItems()) {

            Product product = cartItem.getProduct();

            double subtotal =
                    product.getPrice() * cartItem.getQuantity();

            totalAmount += subtotal;
        }

        // 6. Create order
        Order order = new Order(
                user,
                totalAmount,
                OrderStatus.CONFIRMED);

        // 7. Create order items
        for (CartItem cartItem : cart.getItems()) {

            Product product = cartItem.getProduct();

            int quantity = cartItem.getQuantity();

            double price = product.getPrice();

            double subtotal = price * quantity;

            OrderItem orderItem = new OrderItem(
                    order,
                    product,
                    quantity,
                    price,
                    subtotal);

            order.addItem(orderItem);

            // 8. Reduce stock
            product.setStock(
                    product.getStock() - quantity);
        }

        // 9. Save order
        Order savedOrder = orderRepository.save(order);

        // 10. Clear cart
        cart.getItems().clear();

        // 11. Return response
        return convertToResponse(savedOrder);
    }

    public List<OrderResponse> getUserOrders(Long userId) {

        List<Order> orders =
                orderRepository.findByUserId(userId);

        return orders.stream()
                .map(this::convertToResponse)
                .toList();
    }

    public OrderResponse getOrderById(
            Long userId,
            Long orderId) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() ->
                    new ResourceNotFoundException(
                        "Order not found"));

        if (!order.getUser().getId().equals(userId)) {
            throw new RuntimeException(
                    "Order does not belong to this user");
        }

        return convertToResponse(order);
    }

    private OrderResponse convertToResponse(Order order) {

        List<OrderItemResponse> items =
                order.getItems()
                        .stream()
                        .map(item ->
                            new OrderItemResponse(
                                item.getProduct().getId(),
                                item.getProduct().getName(),
                                item.getQuantity(),
                                item.getPrice(),
                                item.getSubtotal()))
                        .toList();

        return new OrderResponse(
                order.getId(),
                order.getTotalAmount(),
                order.getStatus(),
                order.getCreatedAt(),
                items);
    }
}