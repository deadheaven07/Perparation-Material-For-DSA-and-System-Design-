package com.prep.lld.elevator;

import java.util.Collections;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.locks.ReentrantLock;

public class ElevatorCar {
    private final int carId;
    private final int minFloor;
    private final int maxFloor;
    private int currentFloor;
    private Direction direction;
    private ElevatorState state;

    private final TreeSet<Integer> upStops = new TreeSet<>();
    private final TreeSet<Integer> downStops = new TreeSet<>();
    private final ReentrantLock lock = new ReentrantLock();

    public ElevatorCar(int carId, int initialFloor, int minFloor, int maxFloor) {
        if (minFloor >= maxFloor) {
            throw new IllegalArgumentException("minFloor must be less than maxFloor");
        }
        if (initialFloor < minFloor || initialFloor > maxFloor) {
            throw new IllegalArgumentException("initialFloor out of range [" + minFloor + ", " + maxFloor + "]");
        }
        this.carId = carId;
        this.currentFloor = initialFloor;
        this.minFloor = minFloor;
        this.maxFloor = maxFloor;
        this.direction = Direction.IDLE;
        this.state = ElevatorState.STOPPED;
    }

    public int getCarId() {
        return carId;
    }

    public int getCurrentFloor() {
        lock.lock();
        try {
            return currentFloor;
        } finally {
            lock.unlock();
        }
    }

    public Direction getDirection() {
        lock.lock();
        try {
            return direction;
        } finally {
            lock.unlock();
        }
    }

    public ElevatorState getState() {
        lock.lock();
        try {
            return state;
        } finally {
            lock.unlock();
        }
    }

    public void addStop(int targetFloor) {
        if (targetFloor < minFloor || targetFloor > maxFloor) {
            throw new ElevatorException("Target floor " + targetFloor + " is out of bounds [" + minFloor + ", " + maxFloor + "]");
        }

        lock.lock();
        try {
            if (targetFloor == currentFloor && direction == Direction.IDLE) {
                state = ElevatorState.DOORS_OPEN;
                return;
            }

            if (targetFloor > currentFloor) {
                upStops.add(targetFloor);
            } else if (targetFloor < currentFloor) {
                downStops.add(targetFloor);
            } else {
                // targetFloor == currentFloor while moving; will open doors
                state = ElevatorState.DOORS_OPEN;
            }

            if (direction == Direction.IDLE) {
                if (targetFloor > currentFloor) {
                    direction = Direction.UP;
                    state = ElevatorState.MOVING_UP;
                } else if (targetFloor < currentFloor) {
                    direction = Direction.DOWN;
                    state = ElevatorState.MOVING_DOWN;
                }
            }
        } finally {
            lock.unlock();
        }
    }

    /**
     * Executes one discrete simulation step according to LOOK/SCAN.
     * Returns true if doors opened at a stop, false otherwise.
     */
    public boolean step() {
        lock.lock();
        try {
            if (state == ElevatorState.DOORS_OPEN) {
                // Close doors
                updateDirectionAndState();
                return false;
            }

            if (!hasPendingStops()) {
                direction = Direction.IDLE;
                state = ElevatorState.STOPPED;
                return false;
            }

            if (direction == Direction.UP) {
                Integer nextUp = upStops.ceiling(currentFloor);
                if (nextUp != null) {
                    if (nextUp == currentFloor) {
                        upStops.remove(currentFloor);
                        state = ElevatorState.DOORS_OPEN;
                        checkDirectionTransitionAfterStop(Direction.UP);
                        return true;
                    }
                    currentFloor++;
                    if (upStops.contains(currentFloor)) {
                        upStops.remove(currentFloor);
                        state = ElevatorState.DOORS_OPEN;
                        checkDirectionTransitionAfterStop(Direction.UP);
                        return true;
                    }
                } else {
                    // No more upward stops; reverse to DOWN (LOOK algorithm)
                    direction = Direction.DOWN;
                    state = ElevatorState.MOVING_DOWN;
                }
            } else if (direction == Direction.DOWN) {
                Integer nextDown = downStops.floor(currentFloor);
                if (nextDown != null) {
                    if (nextDown == currentFloor) {
                        downStops.remove(currentFloor);
                        state = ElevatorState.DOORS_OPEN;
                        checkDirectionTransitionAfterStop(Direction.DOWN);
                        return true;
                    }
                    currentFloor--;
                    if (downStops.contains(currentFloor)) {
                        downStops.remove(currentFloor);
                        state = ElevatorState.DOORS_OPEN;
                        checkDirectionTransitionAfterStop(Direction.DOWN);
                        return true;
                    }
                } else {
                    // No more downward stops; reverse to UP (LOOK algorithm)
                    direction = Direction.UP;
                    state = ElevatorState.MOVING_UP;
                }
            } else {
                updateDirectionAndState();
            }

            return false;
        } finally {
            lock.unlock();
        }
    }

    private void checkDirectionTransitionAfterStop(Direction currentDir) {
        if (currentDir == Direction.UP) {
            if (upStops.ceiling(currentFloor) == null) {
                if (!downStops.isEmpty()) {
                    direction = Direction.DOWN;
                } else {
                    direction = Direction.IDLE;
                }
            }
        } else {
            if (downStops.floor(currentFloor) == null) {
                if (!upStops.isEmpty()) {
                    direction = Direction.UP;
                } else {
                    direction = Direction.IDLE;
                }
            }
        }
    }

    private void updateDirectionAndState() {
        if (!hasPendingStops()) {
            direction = Direction.IDLE;
            state = ElevatorState.STOPPED;
            return;
        }

        if (direction == Direction.UP && upStops.ceiling(currentFloor) != null) {
            state = ElevatorState.MOVING_UP;
        } else if (direction == Direction.DOWN && downStops.floor(currentFloor) != null) {
            state = ElevatorState.MOVING_DOWN;
        } else if (!upStops.isEmpty()) {
            direction = Direction.UP;
            state = ElevatorState.MOVING_UP;
        } else if (!downStops.isEmpty()) {
            direction = Direction.DOWN;
            state = ElevatorState.MOVING_DOWN;
        } else {
            direction = Direction.IDLE;
            state = ElevatorState.STOPPED;
        }
    }

    public boolean hasPendingStops() {
        lock.lock();
        try {
            return !upStops.isEmpty() || !downStops.isEmpty();
        } finally {
            lock.unlock();
        }
    }

    public int calculateSuitabilityScore(int floor, Direction requestDir) {
        lock.lock();
        try {
            int distance = Math.abs(currentFloor - floor);

            if (direction == Direction.IDLE) {
                return distance;
            }

            // If car is moving UP and floor is above currentFloor, moving towards it
            if (direction == Direction.UP && requestDir == Direction.UP && floor >= currentFloor) {
                return distance;
            }

            // If car is moving DOWN and floor is below currentFloor, moving towards it
            if (direction == Direction.DOWN && requestDir == Direction.DOWN && floor <= currentFloor) {
                return distance;
            }

            // If car is moving in opposite direction or away: add heavy round-trip penalty
            int totalFloors = maxFloor - minFloor;
            return totalFloors * 2 + distance;
        } finally {
            lock.unlock();
        }
    }

    public Set<Integer> getUpStops() {
        lock.lock();
        try {
            return Collections.unmodifiableSet(new TreeSet<>(upStops));
        } finally {
            lock.unlock();
        }
    }

    public Set<Integer> getDownStops() {
        lock.lock();
        try {
            return Collections.unmodifiableSet(new TreeSet<>(downStops));
        } finally {
            lock.unlock();
        }
    }
}
