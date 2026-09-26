package com.prep.lld.parkinglot;

import java.util.List;
import java.util.Optional;

public interface SpotAllocationStrategy {
    Optional<ParkingSpot> allocateSpot(List<ParkingFloor> floors, Vehicle vehicle);
}
