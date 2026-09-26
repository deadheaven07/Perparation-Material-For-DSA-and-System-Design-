package com.prep.lld.parkinglot;

import java.time.Instant;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

public class ParkingTicket {
    private final String ticketId;
    private final Vehicle vehicle;
    private final ParkingSpot spot;
    private final Instant entryTime;
    private final AtomicReference<Instant> exitTime = new AtomicReference<>(null);
    private volatile double fee;

    public ParkingTicket(String ticketId, Vehicle vehicle, ParkingSpot spot, Instant entryTime) {
        this.ticketId = Objects.requireNonNull(ticketId, "ticketId cannot be null");
        this.vehicle = Objects.requireNonNull(vehicle, "vehicle cannot be null");
        this.spot = Objects.requireNonNull(spot, "spot cannot be null");
        this.entryTime = Objects.requireNonNull(entryTime, "entryTime cannot be null");
        this.fee = 0.0;
    }

    public String getTicketId() {
        return ticketId;
    }

    public Vehicle getVehicle() {
        return vehicle;
    }

    public ParkingSpot getSpot() {
        return spot;
    }

    public Instant getEntryTime() {
        return entryTime;
    }

    public Instant getExitTime() {
        return exitTime.get();
    }

    public void setExitTime(Instant exitTime) {
        this.exitTime.set(Objects.requireNonNull(exitTime, "exitTime cannot be null"));
    }

    public double getFee() {
        return fee;
    }

    public void setFee(double fee) {
        this.fee = fee;
    }

    public boolean isClosed() {
        return exitTime.get() != null;
    }
}
