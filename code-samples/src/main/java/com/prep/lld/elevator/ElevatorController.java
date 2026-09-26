package com.prep.lld.elevator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;

public class ElevatorController {
    private final List<ElevatorCar> cars = new CopyOnWriteArrayList<>();
    private final int minFloor;
    private final int maxFloor;

    public ElevatorController(int numCars, int minFloor, int maxFloor) {
        if (numCars <= 0) {
            throw new IllegalArgumentException("numCars must be positive");
        }
        if (minFloor >= maxFloor) {
            throw new IllegalArgumentException("minFloor must be less than maxFloor");
        }
        this.minFloor = minFloor;
        this.maxFloor = maxFloor;

        for (int i = 1; i <= numCars; i++) {
            cars.add(new ElevatorCar(i, minFloor, minFloor, maxFloor));
        }
    }

    public List<ElevatorCar> getCars() {
        return Collections.unmodifiableList(cars);
    }

    public ElevatorCar getCar(int carId) {
        return cars.stream()
                .filter(c -> c.getCarId() == carId)
                .findFirst()
                .orElseThrow(() -> new ElevatorException("Car ID " + carId + " not found"));
    }

    /**
     * External hall call from a floor wanting to travel UP or DOWN.
     * Selects best elevator car based on proximity and current trajectory.
     */
    public ElevatorCar requestElevator(int sourceFloor, Direction direction) {
        if (sourceFloor < minFloor || sourceFloor > maxFloor) {
            throw new ElevatorException("Source floor " + sourceFloor + " is out of bounds [" + minFloor + ", " + maxFloor + "]");
        }
        if (direction == Direction.IDLE) {
            throw new IllegalArgumentException("Direction cannot be IDLE for a hall call");
        }

        ElevatorCar bestCar = cars.stream()
                .min(Comparator.comparingInt(c -> c.calculateSuitabilityScore(sourceFloor, direction)))
                .orElseThrow(() -> new ElevatorException("No elevator cars available"));

        bestCar.addStop(sourceFloor);
        return bestCar;
    }

    /**
     * Internal car button pressed by passenger inside an elevator car.
     */
    public void requestFloor(int carId, int targetFloor) {
        ElevatorCar car = getCar(carId);
        car.addStop(targetFloor);
    }

    /**
     * Advances all cars by one simulation step.
     */
    public void stepAll() {
        for (ElevatorCar car : cars) {
            car.step();
        }
    }
}
