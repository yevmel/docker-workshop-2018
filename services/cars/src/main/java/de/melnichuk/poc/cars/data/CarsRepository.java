package de.melnichuk.poc.cars.data;

import de.melnichuk.poc.cars.model.Car;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;


public interface CarsRepository extends PagingAndSortingRepository<Car, Long> {
    Page<Car> findByLocation(@Param("location") String location, Pageable p);

    Page<Car> findByUpdatingIsFalseAndFirmwareVersionLessThan(@Param("firmwareVersion") long firmwareVersion, Pageable p);

    @Modifying
    @Transactional
    @Query("update Car c set c.firmwareVersion = :firmwareVersion where c.id = :id and c.firmwareVersion < :firmwareVersion")
    int setFirmwareVersion(@Param("id") long id, @Param("firmwareVersion") long firmwareVersion);

    @Modifying
    @Transactional
    @Query("update Car c set c.updating = :updating where c.id = :id")
    int setUpdating(@Param("id") long id,  @Param("updating") boolean updating);
}
