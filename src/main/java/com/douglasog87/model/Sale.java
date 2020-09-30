package com.douglasog87.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@Builder
public class Sale {
    Long saleId;
    List<SaleItem> itens;
    String salesMan;
    Double total;

    public static List<SaleItem> parseItens(String[] splitItens) {
        List<String> itens = Arrays.asList(splitItens);
        List<SaleItem> result = itens.stream().map(s -> new SaleItem(s.split("-"))).collect(Collectors.toList());
        return result;
    }

    public void calculeTotal() {
        total = itens.parallelStream().mapToDouble(item -> item.getPrice() * item.getQuantity()).sum();
    }
}
