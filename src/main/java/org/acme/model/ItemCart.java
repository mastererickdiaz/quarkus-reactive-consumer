package org.acme.model;

import java.math.BigDecimal;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ItemCart {

    public Long id;
    public ProductSell productSell;
    public Integer quantity;
    public BigDecimal price;

    @Override
    public String toString() {
        return "ItemCart{" +
                "id=" + id +
                ", productSell=" + productSell +
                ", quantity=" + quantity +
                ", price=" + price +
                '}';
    }

}
