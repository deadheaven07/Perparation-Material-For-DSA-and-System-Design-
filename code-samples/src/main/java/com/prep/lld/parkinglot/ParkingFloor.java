package com.prep.lld.parkinglot;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;

public class ParkingFloor {
    private final int floorNumber;
    private final List<ParkingSpot> spots = new CopyOnWriteArrayList<>();

    public ParkingFloor(int floorNumber) {
        this.floorNumber = floorNumber;
    }

    public int getFloorNumber() {
        return floorNumber;
    }

    public void addSpot(ParkingSpot spot) {
        Objects.requireNonNull(spot, "ParkingSpot cannot be null");
        if (spot.getFloorNumber() != this.floorNumber) {
            throw new IllegalArgumentException("Spot floor number " + spot.getFloorNumber() + " does not match floor " + this.floorNumber);
        }
        spots.add(spot);
    }

    public List<ParkingSpot> getSpots() {
        return Collections.unmodifiableList(spots);
    }

    public long getAvailableCount(ParkingSpotType type) {
        return spots.stream()
                .filter(s -> s.getSpotType() == type && !s.isOccupied())
                .count();
    }

    public long getTotalAvailableCount() {
        return spots.stream()
                .filter(s -> !s.isOccupied())
                .count();
    }
}
