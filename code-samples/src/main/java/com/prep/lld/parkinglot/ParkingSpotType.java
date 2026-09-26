package com.prep.lld.parkinglot;

public enum ParkingSpotType {
    MOTORCYCLE {
        @Override
        public boolean canFit(VehicleType vehicleType) {
            return vehicleType == VehicleType.MOTORCYCLE;
        }
    },
    COMPACT {
        @Override
        public boolean canFit(VehicleType vehicleType) {
            return vehicleType == VehicleType.MOTORCYCLE || vehicleType == VehicleType.CAR;
        }
    },
    LARGE {
        @Override
        public boolean canFit(VehicleType vehicleType) {
            return true; // Large spots fit Motorcycle, Car, and Truck
        }
    };

    public abstract boolean canFit(VehicleType vehicleType);
}
