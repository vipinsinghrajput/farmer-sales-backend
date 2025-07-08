package com.farmerapp.response;
import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
public class CartResponse {
    private Long cartId;
    private Long consumerId;
    private List<CartItemResponse> items;
}
