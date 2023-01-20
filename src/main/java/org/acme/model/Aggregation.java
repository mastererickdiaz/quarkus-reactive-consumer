package org.acme.model;

import java.util.ArrayList;
import java.util.List;

import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
public class Aggregation {

    public Long cartId;

    public ProductSell product;

    public int cartTotal;

    public List<ItemCart> cartItems = new ArrayList<ItemCart>();

    public Aggregation() {
    }



    public Aggregation updateFrom(ItemCart itemCart) {
        cartId = 1L;

        // product = itemCart.getProduct();

        return this;
    }

    @Override
    public String toString() {
        return "Aggregation {" +
                "cartId=" + cartId +
                ", product=" + product +
                ", cartTotal=" + cartTotal +
                '}';
    }

}