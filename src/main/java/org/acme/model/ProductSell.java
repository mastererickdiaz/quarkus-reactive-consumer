package org.acme.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ProductSell {

    public Long id;
    public String name;
    public String description;

    public ProductSell(Long id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }

    @Override
    public String toString() {
        return "ProductSell{" +
                "id=" + id +
                ", name=" + name +
                ", description=" + description +
                '}';
    }
}
