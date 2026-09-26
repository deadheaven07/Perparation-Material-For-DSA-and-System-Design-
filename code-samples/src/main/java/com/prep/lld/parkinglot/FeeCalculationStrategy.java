package com.prep.lld.parkinglot;

import java.time.Instant;

public interface FeeCalculationStrategy {
    double calculateFee(ParkingTicket ticket, Instant exitTime);
}
