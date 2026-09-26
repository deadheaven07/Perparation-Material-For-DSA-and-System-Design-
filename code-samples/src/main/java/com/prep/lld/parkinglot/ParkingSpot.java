package com.prep.lld.parkinglot;

import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.locks.ReentrantLock;

public class ParkingSpot {
    private final String spotId;
    private final int floorNumber;
    private final ParkingSpotType spotType;
    private final ReentrantLock lock = new ReentrantLock();
    private Vehicle currentVehicle;

    public ParkingSpot(String spotId, int floorNumber, ParkingSpotType spotType) {
        this.spotId = Objects.requireNonNull(spotId, "spotId cannot be null");
        this.floorNumber = floorNumber;
        this.spotType = Objects.requireNonNull(spotType, "spotType cannot be null");
        this.currentVehicle = null;
    }

    public String getSpotId() {
        return spotId;
    }

    public int getFloorNumber() {
        return floorNumber;
    }

    public ParkingSpotType getSpotType() {
        return spotType;
    }

    public boolean isOccupied() {
        lock.lock();
        try {
            return currentVehicle != null;
        } finally {
            lock.unlock();
        }
    }

    public Optional<Vehicle> getCurrentVehicle() {
        lock.lock();
        try {
            return Optional.ofNullable(currentVehicle);
        } finally {
            lock.unlock();
        }
    }

    public boolean tryPark(Vehicle vehicle) {
        Objects.requireNonNull(vehicle, "vehicle cannot be null");
        if (!spotType.canFit(vehicle.type())) {
            return false;
        }

        if (lock.tryLock()) {
            try {
                if (currentVehicle == null) {
                    currentVehicle = vehicle;
                    return true;
                }
            } finally {
                lock.unlock();
            }
        }
        return false;
    }

    public Optional<Vehicle> unpark() {
        lock.lock();
        try {
            if (currentVehicle == null) {
                return Optional.empty();
            }
            Vehicle removed = currentVehicle;
            currentVehicle = null;
            return Optional.of(removed);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public String toString() {
        return "ParkingSpot{" +
                "spotId='" + spotId + '\'' +
                ", floorNumber=" + floorNumber +
                ", spotType=" + spotType +
                ", occupied=" + isOccupied() +
                '}';
    }
}
