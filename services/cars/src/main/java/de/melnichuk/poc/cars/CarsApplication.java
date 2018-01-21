package de.melnichuk.poc.cars;

import de.melnichuk.poc.cars.data.CarsRepository;
import de.melnichuk.poc.cars.model.Car;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.List;

@SpringBootApplication
@EnableEurekaClient
public class CarsApplication {
    public static void main(String... args) {
        SpringApplication.run(CarsApplication.class, args);
    }

    @Autowired
    private CarsRepository cars;

    @PostConstruct
    public void setupTestData() {
        final List<String> locations = Arrays.asList("Solingen", "Leverkusen", "Erkrath", "Leichlingen", "Langenfeld");

        for (int i = 0; i < 15; i++) {
            final String location = locations.get(i % locations.size());
            final Car car = new Car("SG CC " + i, location, 1);

            cars.save(car);
        }
    }

}
