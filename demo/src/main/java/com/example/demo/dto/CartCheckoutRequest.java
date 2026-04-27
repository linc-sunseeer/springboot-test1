package com.example.demo.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class CartCheckoutRequest {

    private List<ReservationRequest> items = new ArrayList<>();
}
