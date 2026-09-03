package com.Arjun.MyMarket.inventory.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.UUID;

//ignore unknown properties in JSON formation
@JsonIgnoreProperties(ignoreUnknown = true)
public record ProductSnapshot(
        UUID id,
        String title,
        String shortDesc,
        String longDesc,
        Double price,
        Integer discount,
        Boolean live
) {

}
