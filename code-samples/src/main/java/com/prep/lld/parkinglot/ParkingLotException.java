package com.prep.lld.parkinglot;

public class ParkingLotException extends RuntimeException {
    public ParkingLotException(String message) {
        super(message);
    }

    public ParkingLotException(String message, Throwable cause) {
        super(message, cause);
    }
}
