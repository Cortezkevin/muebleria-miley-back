package com.furniture.miley.sales.service;

import com.furniture.miley.catalog.model.Product;
import com.furniture.miley.catalog.repository.ProductRepository;
import com.furniture.miley.exception.customexception.ResourceNotFoundException;
import com.furniture.miley.sales.dto.cart.AddItemDTO;
import com.furniture.miley.sales.dto.cart.CartDTO;
import com.furniture.miley.sales.dto.cart.RemoveItemDTO;
import com.furniture.miley.sales.dto.cart.UpdateShippingCostDTO;
import com.furniture.miley.sales.model.cart.Cart;
import com.furniture.miley.sales.model.cart.CartItem;
import com.furniture.miley.sales.repository.CartItemRepository;
import com.furniture.miley.sales.repository.CartRepository;
import com.furniture.miley.security.model.MainUser;
import com.furniture.miley.security.model.User;
import com.furniture.miley.security.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.util.Pair;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    public CartDTO getCartFromUser(String userId) throws ResourceNotFoundException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        MainUser mainUser = (MainUser) authentication.getPrincipal();

        User userFounded = userRepository.findById( userId ).orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));;
        User userSession = userRepository.findByEmail( mainUser.getEmail() ).orElseThrow(() -> new ResourceNotFoundException("No tiene autorizacion para ver este carrito"));

        if( !(userFounded.getEmail().equals(userSession.getEmail()))){
            throw new RuntimeException("Este carrito no le pertenece");
        }

        Cart cart = cartRepository.findByUser( userSession ).orElseThrow(() -> new ResourceNotFoundException("Carrito no encontrado"));

        return CartDTO.fromEntity( cart );
    }

    public Pair<CartDTO, String> addItemToCart(AddItemDTO addItemDTO) throws ResourceNotFoundException {
        String message = "";
        Cart cart = cartRepository.findById(addItemDTO.cart_id()).orElseThrow(() -> new ResourceNotFoundException("Carrito no encontrado"));
        Product product = productRepository.findById(addItemDTO.product_id()).orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado"));

        if( product.getStock() < addItemDTO.amount() ){
            throw new RuntimeException("La cantidad del producto a agregar excede las existencias");
        }

        List<CartItem> cartItemsInCart = cartItemRepository.findByCart( cart );/**/
        CartItem cartItemInCart = cartItemsInCart.stream().filter(i -> i.getProduct().getId().equals( product.getId() )).findFirst().orElse(null);
        if( cartItemInCart == null ){
            message = "Producto agregado al carrito";
            CartItem newCartItem = CartItem.builder()
                    .cart( cart )
                    .amount( addItemDTO.amount() )
                    .product( product )
                    .total( product.getPrice().multiply(BigDecimal.valueOf(addItemDTO.amount())).setScale(2, RoundingMode.HALF_UP))
                    .build();

            cartItemRepository.save( newCartItem );
        }else {
            if( (product.getStock() == cartItemInCart.getAmount()) || (product.getStock() < (cartItemInCart.getAmount() + addItemDTO.amount())) ){
                throw new RuntimeException("La cantidad del producto a agregar excede las existencias");
            }
            message = "Cantidad aumentada";
            int newAmount = cartItemInCart.getAmount() + addItemDTO.amount();
            cartItemInCart.setAmount(newAmount);
            cartItemInCart.setTotal(cartItemInCart.getProduct().getPrice().multiply(BigDecimal.valueOf(newAmount)).setScale(2, RoundingMode.HALF_UP));
            cartItemRepository.save(cartItemInCart);
        }

        Cart cartToUpdate = cartRepository.findById(cart.getId() ).orElseThrow(() -> new ResourceNotFoundException("Carrito no encontrado"));
        cartToUpdate.calculateTotals();
        Cart cartUpdated = cartRepository.save(cartToUpdate);

        return Pair.of(CartDTO.fromEntity( cartUpdated ), message);
    }
    public Pair<CartDTO, String> removeItemToCart(RemoveItemDTO removeItemDTO) throws ResourceNotFoundException {
        Cart cart = cartRepository.findById(removeItemDTO.cart_id()).orElseThrow(() -> new ResourceNotFoundException("Carrito no encontrado"));
        CartItem cartItem = cartItemRepository.findById(removeItemDTO.item_id()).orElseThrow(() -> new ResourceNotFoundException("Item no encontrado"));
        String message = "";

        if(removeItemDTO.removeAll()){
            cartItemRepository.deleteById(removeItemDTO.item_id());
            message = "Item removido del carrito";
        }else {
            int newAmount = cartItem.getAmount() - removeItemDTO.amount();
            if( newAmount <= 0){
                cartItemRepository.deleteById(removeItemDTO.item_id());
                message = "Item removido del carrito";
            }else {
                cartItem.setAmount(newAmount);
                cartItem.setTotal(cartItem.getProduct().getPrice().multiply(BigDecimal.valueOf(newAmount)).setScale(2, RoundingMode.HALF_UP));
                cartItemRepository.save( cartItem  );
                message = "Cantidad reducida";
            }
        }

        Cart cartToUpdate = cartRepository.findById(cart.getId() ).orElseThrow(() -> new ResourceNotFoundException("Carrito no encontrado"));
        cartToUpdate.calculateTotals();
        Cart cartUpdated = cartRepository.save( cartToUpdate );

        return Pair.of(CartDTO.fromEntity( cartUpdated ), message);
    }

    public CartDTO updateShippingCost(UpdateShippingCostDTO updateShippingCostDTO) throws ResourceNotFoundException {
        Cart cart = cartRepository.findById(updateShippingCostDTO.cartId()).orElseThrow(() -> new ResourceNotFoundException("Carrito no encontrado"));
        cart.setShippingCost( updateShippingCostDTO.shippingCost().setScale(2, RoundingMode.HALF_UP) );
        cart.setDistance(updateShippingCostDTO.distance());
        cart.calculateTotals();
        System.out.println(cart.getTax());

        Cart cartUpdated = cartRepository.save( cart );
        return CartDTO.fromEntity( cartUpdated );
    }

    public CartDTO clearCart(String cartId) throws ResourceNotFoundException {
        Cart cart = cartRepository.findById(cartId).orElseThrow(() -> new ResourceNotFoundException("Carrito no encontrado"));
        cart.setTotal(cart.getShippingCost());
        cart.setTax(BigDecimal.ZERO);
        cart.setDiscount(BigDecimal.ZERO);
        cart.setSubtotal(BigDecimal.ZERO);

        cartRepository.save( cart );

        cart.getCartItems().forEach(cartItemRepository::delete);

        Cart cartUpdated = cartRepository.findById( cart.getId() ).get();
        cartUpdated.setCartItems(new ArrayList<>());
        return CartDTO.fromEntity(cartUpdated);
    }

    public CartDTO clearCartByUser(String userId) throws ResourceNotFoundException {
        User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));
        Cart cart = cartRepository.findByUser(user).orElseThrow(() -> new ResourceNotFoundException("Carrito no encontrado"));
        cart.setTotal(cart.getShippingCost().setScale(2, RoundingMode.HALF_UP));
        cart.setTax(BigDecimal.ZERO);
        cart.setDiscount(BigDecimal.ZERO);
        cart.setSubtotal(BigDecimal.ZERO);

        cartRepository.save( cart );

        cart.getCartItems().forEach(cartItemRepository::delete);

        Cart cartUpdated = cartRepository.findById( cart.getId() ).get();
        cartUpdated.setCartItems(new ArrayList<>());
        return  CartDTO.fromEntity(cartUpdated);
    }

}
