package com.furniture.miley.sales.controller;

import com.furniture.miley.commons.constants.ResponseMessage;
import com.furniture.miley.commons.dto.SuccessResponseDTO;
import com.furniture.miley.exception.customexception.ResourceNotFoundException;
import com.furniture.miley.sales.dto.cart.AddItemDTO;
import com.furniture.miley.sales.dto.cart.CartDTO;
import com.furniture.miley.sales.dto.cart.RemoveItemDTO;
import com.furniture.miley.sales.dto.cart.UpdateShippingCostDTO;
import com.furniture.miley.sales.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.util.Pair;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {
    private final CartService cartService;

    @GetMapping("/fromUser")
    public ResponseEntity<SuccessResponseDTO<CartDTO>> addToCart(
            @RequestParam(name = "user") String userId
    ) throws ResourceNotFoundException {
        return ResponseEntity.ok(
                new SuccessResponseDTO<>(
                        ResponseMessage.PRODUCT_ADDED_TO_CART,
                        HttpStatus.OK.name(),
                        cartService.getCartFromUser( userId )
                )
        );
    }

    @PostMapping("/add")
    public ResponseEntity<SuccessResponseDTO<CartDTO>> addToCart(
            @RequestBody AddItemDTO addItemDTO
            ) throws ResourceNotFoundException {
        Pair<CartDTO, String> result = cartService.addItemToCart( addItemDTO );
        return  ResponseEntity.ok(
                new SuccessResponseDTO<>(
                        result.getSecond(),
                        HttpStatus.OK.name(),
                        result.getFirst()
                )
        );
    }

    @PostMapping("/remove")
    public ResponseEntity<SuccessResponseDTO<CartDTO>> removeToCart(
            @RequestBody RemoveItemDTO removeItemDTO
    ) throws ResourceNotFoundException {
        Pair<CartDTO, String> result = cartService.removeItemToCart( removeItemDTO );
        return ResponseEntity.ok(
                new SuccessResponseDTO<>(
                        result.getSecond(),
                        HttpStatus.OK.name(),
                        result.getFirst()
                )
        );
    }

    @PostMapping("/clear")
    public ResponseEntity<SuccessResponseDTO<CartDTO>> clearCart(
            @RequestParam("cart") String cart
    ) throws ResourceNotFoundException {
        return ResponseEntity.ok(
                new SuccessResponseDTO<>(
                        ResponseMessage.CART_CLEARED,
                        HttpStatus.OK.name(),
                        cartService.clearCart( cart )
                )
        );
    }

    @PutMapping("/shipping")
    public ResponseEntity<SuccessResponseDTO<CartDTO>> updateShippingCost(
            @RequestBody UpdateShippingCostDTO updateShippingCostDTO
    ) throws ResourceNotFoundException {
        return ResponseEntity.ok(
                new SuccessResponseDTO<>(
                        ResponseMessage.SHIPPING_COST_UPDATED,
                        HttpStatus.OK.name(),
                        cartService.updateShippingCost( updateShippingCostDTO )
                )
        );
    }
}
