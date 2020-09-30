package com.douglasog87.model;

import lombok.Value;

@Value
public class SaleItem {

    Long id;
    Integer quantity;
    Double price;

    public SaleItem(String[] split) {
        this.id = Long.parseLong(split[0].replace("[", ""));
        this.quantity = Integer.parseInt(split[1]);
        this.price = Double.parseDouble(split[2].replace("]", ""));
    }
}
