package com.prep.lld.parkinglot;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Parking Lot LLD Test Suite")
class ParkingLotTest {

    private ParkingLotSystem parkingLot;
    private ParkingFloor floor1;
    private ParkingFloor floor2;

    @BeforeEach
    void setUp() {
        parkingLot = new ParkingLotSystem(
                "PL-DOWNTOWN-01",
                new NearestSpotAllocationStrategy(),
                new HourlyFeeCalculationStrategy(10.0, 20.0, 35.0)
        );

        floor1 = new ParkingFloor(1);
        floor1.addSpot(new ParkingSpot("F1-M1", 1, ParkingSpotType.MOTORCYCLE));
        floor1.addSpot(new ParkingSpot("F1-C1", 1, ParkingSpotType.COMPACT));
        floor1.addSpot(new ParkingSpot("F1-C2", 1, ParkingSpotType.COMPACT));
        floor1.addSpot(new ParkingSpot("F1-L1", 1, ParkingSpotType.LARGE));

        floor2 = new ParkingFloor(2);
        floor2.addSpot(new ParkingSpot("F2-M1", 2, ParkingSpotType.MOTORCYCLE));
        floor2.addSpot(new ParkingSpot("F2-C1", 2, ParkingSpotType.COMPACT));
        floor2.addSpot(new ParkingSpot("F2-L1", 2, ParkingSpotType.LARGE));

        parkingLot.addFloor(floor1);
        parkingLot.addFloor(floor2);
    }

    @Test
    @DisplayName("Should allocate nearest matching spots according to vehicle size")
    void testBasicAllocation() {
        Vehicle bike = new Vehicle("MOTO-101", VehicleType.MOTORCYCLE);
        Vehicle car1 = new Vehicle("CAR-201", VehicleType.CAR);
        Vehicle car2 = new Vehicle("CAR-202", VehicleType.CAR);
        Vehicle truck = new Vehicle("TRUCK-301", VehicleType.TRUCK);

        // Bike should get Floor 1 motorcycle spot
        ParkingTicket bikeTicket = parkingLot.parkVehicle(bike);
        assertEquals("F1-M1", bikeTicket.getSpot().getSpotId());

        // Car 1 should get Floor 1 compact spot
        ParkingTicket carTicket1 = parkingLot.parkVehicle(car1);
        assertEquals("F1-C1", carTicket1.getSpot().getSpotId());

        // Car 2 should get Floor 1 compact spot
        ParkingTicket carTicket2 = parkingLot.parkVehicle(car2);
        assertEquals("F1-C2", carTicket2.getSpot().getSpotId());

        // Truck should get Floor 1 large spot
        ParkingTicket truckTicket = parkingLot.parkVehicle(truck);
        assertEquals("F1-L1", truckTicket.getSpot().getSpotId());

        // Next car should spill over to Floor 2 compact spot
        Vehicle car3 = new Vehicle("CAR-203", VehicleType.CAR);
        ParkingTicket carTicket3 = parkingLot.parkVehicle(car3);
        assertEquals("F2-C1", carTicket3.getSpot().getSpotId());
    }

    @Test
    @DisplayName("Should successfully unpark, calculate fee, and recycle the spot")
    void testUnparkAndRecycle() {
        Vehicle car = new Vehicle("CAR-999", VehicleType.CAR);
        ParkingTicket ticket = parkingLot.parkVehicle(car);
        ParkingSpot spot = ticket.getSpot();

        assertTrue(spot.isOccupied());
        assertEquals(1, parkingLot.getFloors().get(0).getAvailableCount(ParkingSpotType.COMPACT));

        ParkingTicket closedTicket = parkingLot.unparkVehicle(ticket.getTicketId());
        assertTrue(closedTicket.isClosed());
        assertTrue(closedTicket.getFee() >= 20.0);
        assertFalse(spot.isOccupied());

        // Spot can now be parked again
        Vehicle car2 = new Vehicle("CAR-888", VehicleType.CAR);
        ParkingTicket ticket2 = parkingLot.parkVehicle(car2);
        assertEquals(spot.getSpotId(), ticket2.getSpot().getSpotId());
    }

    @Test
    @DisplayName("Should reject duplicate vehicle parking")
    void testDuplicateVehicleRejection() {
        Vehicle car = new Vehicle("DUP-123", VehicleType.CAR);
        parkingLot.parkVehicle(car);

        assertThrows(ParkingLotException.class, () -> parkingLot.parkVehicle(car));
    }

    @Test
    @DisplayName("Should reject unparking unknown ticket")
    void testUnknownTicketRejection() {
        assertThrows(ParkingLotException.class, () -> parkingLot.unparkVehicle("TKT-INVALID"));
    }

    @Test
    @DisplayName("Should handle high concurrency race condition with exact spot capacity guarantees")
    void testConcurrentSpotAllocation() throws InterruptedException {
        // Total compact spots: 2 on Floor 1, 1 on Floor 2 = 3 compact spots
        // Plus 1 Large on Floor 1, 1 Large on Floor 2 = 2 large spots (can fit cars)
        // Total car-capable spots = 5.
        // We will launch 25 concurrent threads attempting to park 25 distinct cars.

        int threadCount = 25;
        int expectedSuccessCapacity = 5;

        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch finishLatch = new CountDownLatch(threadCount);

        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger rejectedCount = new AtomicInteger(0);
        List<ParkingTicket> tickets = Collections.synchronizedList(new ArrayList<>());

        for (int i = 0; i < threadCount; i++) {
            final int id = i;
            executor.submit(() -> {
                try {
                    startLatch.await();
                    Vehicle vehicle = new Vehicle("RACE-CAR-" + id, VehicleType.CAR);
                    ParkingTicket ticket = parkingLot.parkVehicle(vehicle);
                    tickets.add(ticket);
                    successCount.incrementAndGet();
                } catch (ParkingLotException e) {
                    rejectedCount.incrementAndGet();
                } catch (Exception e) {
                    fail("Unexpected exception: " + e.getMessage());
                } finally {
                    finishLatch.countDown();
                }
            });
        }

        startLatch.countDown();
        boolean completed = finishLatch.await(10, TimeUnit.SECONDS);
        executor.shutdown();

        assertTrue(completed, "Concurrent test timed out");
        assertEquals(expectedSuccessCapacity, successCount.get(), "Exactly available spots should be claimed");
        assertEquals(threadCount - expectedSuccessCapacity, rejectedCount.get(), "Remaining threads should be rejected");
        assertEquals(0, parkingLot.getAvailableSpots(ParkingSpotType.COMPACT));
        assertEquals(0, parkingLot.getAvailableSpots(ParkingSpotType.LARGE));
    }
}
