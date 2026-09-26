package com.prep.lld.elevator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Elevator Controller LOOK/SCAN LLD Test Suite")
class ElevatorControllerTest {

    private ElevatorController controller;

    @BeforeEach
    void setUp() {
        // 2 elevator cars, floors 1 through 10
        controller = new ElevatorController(2, 1, 10);
    }

    @Test
    @DisplayName("Should serve floors in LOOK/SCAN sweep order without oscillating")
    void testLookScanServicingOrder() {
        ElevatorCar car = new ElevatorCar(99, 2, 1, 10);

        // Add upward requests: 5, 4
        car.addStop(5);
        car.addStop(4);

        // Add downward request: 1
        car.addStop(1);

        List<Integer> visitedStops = new ArrayList<>();

        // Advance simulation until all stops served
        for (int step = 0; step < 30 && (car.hasPendingStops() || car.getState() == ElevatorState.DOORS_OPEN); step++) {
            boolean stopped = car.step();
            if (stopped) {
                visitedStops.add(car.getCurrentFloor());
            }
        }

        // Must visit in ascending order (4, 5) then reverse down to 1
        assertEquals(List.of(4, 5, 1), visitedStops);
        assertEquals(Direction.IDLE, car.getDirection());
    }

    @Test
    @DisplayName("Should dispatch closest elevator to external hall call")
    void testMultipleCarDispatch() {
        ElevatorCar car1 = controller.getCar(1);
        ElevatorCar car2 = controller.getCar(2);

        // Move car 2 to floor 8
        car2.addStop(8);
        for (int i = 0; i < 15 && (car2.hasPendingStops() || car2.getState() == ElevatorState.DOORS_OPEN); i++) {
            car2.step();
        }
        assertEquals(8, car2.getCurrentFloor());
        assertEquals(Direction.IDLE, car2.getDirection());

        // Car 1 is at floor 1, Car 2 is at floor 8
        // External call at floor 7 requesting DOWN should assign Car 2 (distance 1 vs 6)
        ElevatorCar assignedCar = controller.requestElevator(7, Direction.DOWN);
        assertEquals(car2.getCarId(), assignedCar.getCarId());
    }

    @Test
    @DisplayName("Should reject out of bounds floor requests")
    void testOutOfBoundsFloors() {
        assertThrows(ElevatorException.class, () -> controller.requestElevator(15, Direction.UP));
        assertThrows(ElevatorException.class, () -> controller.requestFloor(1, 0));
    }

    @Test
    @DisplayName("Should handle concurrent internal and external floor requests cleanly")
    void testConcurrentRequests() throws InterruptedException {
        int threadCount = 16;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch finishLatch = new CountDownLatch(threadCount);

        for (int i = 0; i < threadCount; i++) {
            final int id = i;
            executor.submit(() -> {
                try {
                    startLatch.await();
                    int targetFloor = 2 + (id % 8); // floors 2 to 9
                    if (id % 2 == 0) {
                        controller.requestElevator(targetFloor, Direction.UP);
                    } else {
                        controller.requestFloor(1, targetFloor);
                    }
                } catch (Exception e) {
                    fail("Concurrent request failed: " + e.getMessage());
                } finally {
                    finishLatch.countDown();
                }
            });
        }

        startLatch.countDown();
        boolean completed = finishLatch.await(5, TimeUnit.SECONDS);
        executor.shutdown();

        assertTrue(completed, "Concurrent requests timed out");

        // Verify cars have pending stops registered
        boolean hasPending = controller.getCars().stream().anyMatch(ElevatorCar::hasPendingStops);
        assertTrue(hasPending, "Cars should have received pending stops");

        // Step simulation until all stops served
        for (int i = 0; i < 50; i++) {
            controller.stepAll();
        }

        boolean allEmpty = controller.getCars().stream().noneMatch(ElevatorCar::hasPendingStops);
        assertTrue(allEmpty, "All stops should be cleared after stepping through simulation");
    }
}
