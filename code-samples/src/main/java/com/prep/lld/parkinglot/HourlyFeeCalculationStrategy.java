package com.prep.lld.parkinglot;

import java.time.Duration;
import java.time.Instant;
import java.util.EnumMap;
import java.util.Map;

public class HourlyFeeCalculationStrategy implements FeeCalculationStrategy {
    private final Map<VehicleType, Double> hourlyRates = new EnumMap<>(VehicleType.class);

    public HourlyFeeCalculationStrategy() {
        hourlyRates.put(VehicleType.MOTORCYCLE, 10.0);
        hourlyRates.put(VehicleType.CAR, 20.0);
        hourlyRates.put(VehicleType.TRUCK, 35.0);
    }

    public HourlyFeeCalculationStrategy(double motorcycleRate, double carRate, double truckRate) {
        hourlyRates.put(VehicleType.MOTORCYCLE, motorcycleRate);
        hourlyRates.put(VehicleType.CAR, carRate);
        hourlyRates.put(VehicleType.TRUCK, truckRate);
    }

    @Override
    public double calculateFee(ParkingTicket ticket, Instant exitTime) {
        Duration duration = Duration.between(ticket.getEntryTime(), exitTime);
        long minutes = Math.max(0, duration.toMinutes());
        // Ceiling hours, minimum 1 hour
        long hours = Math.max(1, (minutes + 59) / 60);
        double rate = hourlyRates.getOrDefault(ticket.getVehicle().type(), 20.0);
        return hours * rate;
    }
}
