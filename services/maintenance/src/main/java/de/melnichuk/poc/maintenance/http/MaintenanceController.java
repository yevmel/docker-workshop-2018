package de.melnichuk.poc.maintenance.http;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicLong;

@RestController
public class MaintenanceController {
    private static final Logger LOGGER = LoggerFactory.getLogger(MaintenanceController.class);

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private DiscoveryClient discoveryClient;

    private Metrics metrics;

    private CompletableFuture<Void> currentUpdateFuture;

    @GetMapping("/update")
    public ResponseEntity<String> updateAllCars(@RequestParam("firmwareVersion") long firmwareVersion) {
        LOGGER.debug("received update request for all cars to firmwareVersion={}.", firmwareVersion);

        if (isCurrentlyUpdating()) {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).build();
        } else {
            currentUpdateFuture = CompletableFuture.runAsync(() -> doUpdateAllCars(firmwareVersion));
            return ResponseEntity.accepted().build();
        }
    }

    @GetMapping(path = "/metrics", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<Metrics> metrics() {
        return ResponseEntity.ok(metrics);
    }

    private boolean isCurrentlyUpdating() {
        return currentUpdateFuture != null && !currentUpdateFuture.isDone();
    }

    private void doUpdateAllCars(@RequestParam("firmwareVersion") long firmwareVersion) {
        metrics = new Metrics();

        while (true) {
            final List<ServiceInstance> serviceInstances = discoveryClient.getInstances("cars");
            final Collection<Car> cars = retrieveCarsWithOutdatedFirmware(firmwareVersion, serviceInstances);
            if (cars.isEmpty()) {
                break;
            }

            for (final Car car : cars) {
                while (true) {
                    try {
                        updateSingleCar(car, firmwareVersion, serviceInstances);
                        break;
                    } catch (NoAvailableServiceInstanceException e) {
                        LOGGER.warn("failed to update car with id={}, because no instance of car service was available.");

                        try {
                            Thread.sleep(3_000);
                        } catch (InterruptedException e1) {
                            metrics.done();
                            return;
                        }
                    }
                }
            }
        }

        metrics.done();
    }

    /**
     * triggers update routine for a single car.
     *
     * @param car
     * @param firmwareVersion  new firmware version
     * @param serviceInstances list of car service instances
     * @throws NoAvailableServiceInstanceException
     */
    private void updateSingleCar(final Car car, @RequestParam("firmwareVersion") final long firmwareVersion, final List<ServiceInstance> serviceInstances) {
        final String id = Long.toString(car.getId());
        LOGGER.info("using one of {} car service instances to update car with id={}.", serviceInstances.size(), id);

        for (ServiceInstance serviceInstance : serviceInstances) {
            final URI uri = constructUpdateUri(id, firmwareVersion, serviceInstance);
            final RequestEntity request = RequestEntity.get(uri).build();
            try {
                final ResponseEntity<Void> response = restTemplate.exchange(request, Void.class);
                if (response.getStatusCode().is2xxSuccessful()) {
                    LOGGER.info("successfully started update of car with id={}.", id);
                    metrics.inc();

                    // success!
                    return;
                }
            } catch (HttpClientErrorException e) {
                if (e.getRawStatusCode() == 429) {
                    LOGGER.info("updating car with id={} failed. instance of car service is too busy.", id);
                } else {
                    LOGGER.error("updating car with id={} failed for whatever reason. should not be ignored in a real application.", id);
                }
            }
        }

        throw new NoAvailableServiceInstanceException();
    }


    private URI constructUpdateUri(String id, long firmwareVersion, ServiceInstance serviceInstance) {
        return UriComponentsBuilder
                .fromUri(serviceInstance.getUri())
                .pathSegment("cars", id, "update")
                .queryParam("firmwareVersion", firmwareVersion)
                .build().toUri();
    }

    private Collection<Car> retrieveCarsWithOutdatedFirmware(long version, final List<ServiceInstance> serviceInstances) {
        final URI baseUri = serviceInstances.get(0).getUri();
        final URI uri = UriComponentsBuilder
                .fromUri(baseUri)
                .pathSegment("cars")
                .queryParam("maxFirmwareVersion", version)
                .build().toUri();

        final RequestEntity<Void> request = RequestEntity.get(uri).accept(MediaType.APPLICATION_JSON_UTF8).build();
        final ResponseEntity<Collection<Car>> response = restTemplate.exchange(request, new CarsCollection());
        final Collection<Car> cars = response.getBody();

        LOGGER.info("retrieved {} cars with outdated firmware.", cars.size());
        return cars;
    }

    private class NoAvailableServiceInstanceException extends RuntimeException {
    }

    private class CarsCollection extends ParameterizedTypeReference<Collection<Car>> {
    }

    private final class Metrics {
        private final AtomicLong counter = new AtomicLong();

        private final long start = System.currentTimeMillis();
        private Long end;

        void inc() {
            counter.incrementAndGet();
        }

        void done() {
            end = System.currentTimeMillis();
        }

        public long getCount() {
            return counter.get();
        }

        public String getState() {
            return currentUpdateFuture.isDone() ? "DONE" : "RUNNING";
        }

        public long getDurationInSeconds() {
            long duration;

            if (end == null) {
                duration = System.currentTimeMillis() - start;
            } else {
                duration = end - start;
            }

            return duration / 1000;
        }
    }

    private static final class Car {
        private long id;
        private long firmwareVersion;

        public long getId() {
            return id;
        }

        public void setId(long id) {
            this.id = id;
        }

        public long getFirmwareVersion() {
            return firmwareVersion;
        }

        public void setFirmwareVersion(long firmwareVersion) {
            this.firmwareVersion = firmwareVersion;
        }
    }

}
