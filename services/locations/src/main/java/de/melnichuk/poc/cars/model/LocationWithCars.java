package de.melnichuk.poc.cars.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class LocationWithCars {
    private final String name;
    private final List<Car> cars;

    @JsonCreator
    public LocationWithCars(@JsonProperty("name") String name, @JsonProperty("cars") List<Car> cars) {
        this.name = name;
        this.cars = cars;
    }

    public List<Car> getCars() {
        return cars;
    }

    public String getName() {
        return name;
    }
}
