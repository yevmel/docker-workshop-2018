package de.melnichuk.poc.cars.http;

import de.melnichuk.poc.cars.data.CarsRepository;
import de.melnichuk.poc.cars.model.Car;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@RestController
public class CarsController {
    private static final Logger LOGGER = LoggerFactory.getLogger(CarsController.class);

    @Autowired
    private CarsRepository cars;

    private final ThreadPoolExecutor executorService =
            new ThreadPoolExecutor(2, 2, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>());

    @GetMapping(path = "/cars/{id}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<Car> car(@PathVariable("id") final long id) {
        LOGGER.info("received request for car with id={}.", id);

        final Optional<Car> result = cars.findById(id);
        if (result.isPresent()) {
            return ResponseEntity.ok().body(result.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping(path = "/cars", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<Collection<Car>> cars() {
        LOGGER.info("received request for all cars.");

        final Page<Car> result = this.cars.findAll(PageRequest.of(0, 20));
        final List<Car> cars = result.getContent();
        return ResponseEntity.ok().body(cars);
    }

    @GetMapping(path = "/cars", produces = MediaType.APPLICATION_JSON_UTF8_VALUE, params = "location")
    public ResponseEntity<Collection<Car>> cars(@RequestParam("location") final String location) {
        LOGGER.info("received request for all cars with location={}.", location);

        final Page<Car> result = this.cars.findByLocation(location, PageRequest.of(0, 20));
        final List<Car> cars = result.getContent();
        return ResponseEntity.ok().body(cars);
    }

    @GetMapping(path = "/cars", produces = MediaType.APPLICATION_JSON_UTF8_VALUE, params = "maxFirmwareVersion")
    public ResponseEntity<Collection<Car>> cars(@RequestParam("maxFirmwareVersion") final long maxFirmwareVersion, @RequestParam(name = "count", defaultValue = "5") int count) {
        LOGGER.info("received request for all cars with firmware version lower then {}.");

        final Page<Car> result = this.cars.findByUpdatingIsFalseAndFirmwareVersionLessThan(maxFirmwareVersion, PageRequest.of(0, count));
        final List<Car> cars = result.getContent();
        return ResponseEntity.ok().body(cars);
    }

    @GetMapping("/cars/{id}/update")
    public ResponseEntity<Void> update(@PathVariable("id") final long id, @RequestParam("firmwareVersion") final long firmwareVersion) {
        LOGGER.info("received update request for car with id={}.", id);

        if (executorService.getMaximumPoolSize() - executorService.getActiveCount() < 1) {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).build();
        } else {
            executorService.execute(() -> {
                try {
                    cars.setUpdating(id,true);

                    LOGGER.info("preparing firmware update for car with id={}.", id);
                    Thread.sleep(3_000);

                    LOGGER.info("uploading new firmware to car with id={}.", id);
                    Thread.sleep(2_000);

                    LOGGER.info("car with id={} updated.", id);

                    cars.setFirmwareVersion(id, firmwareVersion);
                } catch (InterruptedException e) {
                    LOGGER.warn("interrupted.", e);
                } finally {
                    cars.setUpdating(id,false);
                }
            });

            return ResponseEntity.accepted().build();
        }

    }
}
