package de.melnichuk.poc.cars.http;

import de.melnichuk.poc.cars.model.Car;
import de.melnichuk.poc.cars.model.Location;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

@RestController
public class LocationsController {

    private static final Logger LOGGER = LoggerFactory.getLogger(LocationsController.class);

    @Autowired
    private RestTemplate restTemplate;

    @GetMapping("/locations")
    public Collection<Location> locations() {
        LOGGER.info("All locations requested");
        return getTestLocations();
    }

    @GetMapping("/locations/{name}")
    public Collection<Car> location(@PathVariable("name") String name) {
        LOGGER.info("Locations for {} requested.", name);
        final URI uri = UriComponentsBuilder.fromHttpUrl("http://cars/").pathSegment("cars")
                .queryParam("location", name)
                .build().toUri();

        return restTemplate.exchange(uri, HttpMethod.GET, null, new ParameterizedTypeReference<Collection<Car>>() {
        }).getBody();
    }

    private List<Location> getTestLocations() {
        return Arrays.asList(
                new Location("Solingen"),
                new Location("Leverkusen"),
                new Location("Erkrath"),
                new Location("Leichlingen"),
                new Location("Langenfeld")
        );
    }

}
