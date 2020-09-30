package com.douglasog87.model;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class Client {
    String cnpj, name, businessArea;
}
