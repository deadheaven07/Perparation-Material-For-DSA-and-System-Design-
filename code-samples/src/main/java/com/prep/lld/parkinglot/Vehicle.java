package com.prep.lld.parkinglot;

import java.util.Objects;

public record Vehicle(String licensePlate, VehicleType type) {
    public Vehicle {
        Objects.requireNonNull(licensePlate, "licensePlate cannot be null");
        Objects.requireNonNull(type, "VehicleType cannot be null");
    }
}
