package com.farmerapp.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class AddressResponse {
    private Long id;
    private String street;
    private String city;
    private String state;
    private String postalCode;
    private String Country;
}
