package de.melnichuk.poc.cars.model;


import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class Car {

    @Id
    @GeneratedValue
    private Long id;

    private Long firmwareVersion;

    private String location;

    private String licensePlate;

    private boolean updating;

    public Car() {}

    public Car(final String licensePlate, final String location, final long firmwareVersion) {
        this.firmwareVersion = firmwareVersion;
        this.licensePlate = licensePlate;
        this.location = location;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLicensePlate() {
        return licensePlate;
    }

    public void setLicensePlate(String licensePlate) {
        this.licensePlate = licensePlate;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Long getFirmwareVersion() {
        return firmwareVersion;
    }

    public void setFirmwareVersion(Long firmwareVersion) {
        this.firmwareVersion = firmwareVersion;
    }

    public boolean isUpdating() {
        return updating;
    }

    public void setUpdating(boolean updating) {
        this.updating = updating;
    }
}
