package com.prep.lld.parkinglot;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class NearestSpotAllocationStrategy implements SpotAllocationStrategy {

    @Override
    public Optional<ParkingSpot> allocateSpot(List<ParkingFloor> floors, Vehicle vehicle) {
        // Sort floors by floorNumber ascending (nearest floor to entrance)
        List<ParkingFloor> sortedFloors = floors.stream()
                .sorted(Comparator.comparingInt(ParkingFloor::getFloorNumber))
                .toList();

        for (ParkingFloor floor : sortedFloors) {
            // Find first available spot that fits best
            // Preferred match: exact type first, then larger
            List<ParkingSpot> candidateSpots = floor.getSpots().stream()
                    .filter(spot -> spot.getSpotType().canFit(vehicle.type()) && !spot.isOccupied())
                    .sorted(Comparator.comparingInt(s -> spotPriority(s.getSpotType(), vehicle.type())))
                    .toList();

            for (ParkingSpot spot : candidateSpots) {
                if (spot.tryPark(vehicle)) {
                    return Optional.of(spot);
                }
            }
        }
        return Optional.empty();
    }

    private int spotPriority(ParkingSpotType spotType, VehicleType vehicleType) {
        // Prefer tightest fit first:
        // For MOTORCYCLE: MOTORCYCLE (1) < COMPACT (2) < LARGE (3)
        // For CAR: COMPACT (1) < LARGE (2)
        // For TRUCK: LARGE (1)
        if (vehicleType == VehicleType.MOTORCYCLE) {
            return switch (spotType) {
                case MOTORCYCLE -> 1;
                case COMPACT -> 2;
                case LARGE -> 3;
            };
        } else if (vehicleType == VehicleType.CAR) {
            return switch (spotType) {
                case COMPACT -> 1;
                case LARGE -> 2;
                default -> 99;
            };
        } else {
            return spotType == ParkingSpotType.LARGE ? 1 : 99;
        }
    }
}
