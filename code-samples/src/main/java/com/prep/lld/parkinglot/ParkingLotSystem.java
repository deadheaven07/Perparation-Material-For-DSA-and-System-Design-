package com.prep.lld.parkinglot;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class ParkingLotSystem {
    private final String lotId;
    private final List<ParkingFloor> floors = new CopyOnWriteArrayList<>();
    private final Map<String, ParkingTicket> activeTickets = new ConcurrentHashMap<>();
    private final SpotAllocationStrategy allocationStrategy;
    private final FeeCalculationStrategy feeStrategy;

    public ParkingLotSystem(String lotId, SpotAllocationStrategy allocationStrategy, FeeCalculationStrategy feeStrategy) {
        this.lotId = Objects.requireNonNull(lotId, "lotId cannot be null");
        this.allocationStrategy = Objects.requireNonNull(allocationStrategy, "allocationStrategy cannot be null");
        this.feeStrategy = Objects.requireNonNull(feeStrategy, "feeStrategy cannot be null");
    }

    public String getLotId() {
        return lotId;
    }

    public void addFloor(ParkingFloor floor) {
        Objects.requireNonNull(floor, "ParkingFloor cannot be null");
        floors.add(floor);
    }

    public List<ParkingFloor> getFloors() {
        return Collections.unmodifiableList(floors);
    }

    public ParkingTicket parkVehicle(Vehicle vehicle) {
        Objects.requireNonNull(vehicle, "vehicle cannot be null");

        // Check if vehicle is already parked
        boolean alreadyParked = activeTickets.values().stream()
                .anyMatch(t -> t.getVehicle().licensePlate().equalsIgnoreCase(vehicle.licensePlate()));
        if (alreadyParked) {
            throw new ParkingLotException("Vehicle with license plate " + vehicle.licensePlate() + " is already parked.");
        }

        Optional<ParkingSpot> allocatedSpot = allocationStrategy.allocateSpot(floors, vehicle);
        if (allocatedSpot.isEmpty()) {
            throw new ParkingLotException("Parking Lot is full for vehicle type: " + vehicle.type());
        }

        String ticketId = "TKT-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        ParkingTicket ticket = new ParkingTicket(ticketId, vehicle, allocatedSpot.get(), Instant.now());
        activeTickets.put(ticketId, ticket);
        return ticket;
    }

    public ParkingTicket unparkVehicle(String ticketId) {
        Objects.requireNonNull(ticketId, "ticketId cannot be null");
        ParkingTicket ticket = activeTickets.remove(ticketId);
        if (ticket == null) {
            throw new ParkingLotException("Active ticket not found: " + ticketId);
        }

        Instant exitTime = Instant.now();
        ticket.setExitTime(exitTime);
        double fee = feeStrategy.calculateFee(ticket, exitTime);
        ticket.setFee(fee);

        ticket.getSpot().unpark();
        return ticket;
    }

    public Optional<ParkingTicket> getTicket(String ticketId) {
        return Optional.ofNullable(activeTickets.get(ticketId));
    }

    public long getAvailableSpots(ParkingSpotType type) {
        return floors.stream()
                .mapToLong(f -> f.getAvailableCount(type))
                .sum();
    }

    public long getTotalAvailableSpots() {
        return floors.stream()
                .mapToLong(ParkingFloor::getTotalAvailableCount)
                .sum();
    }
}
