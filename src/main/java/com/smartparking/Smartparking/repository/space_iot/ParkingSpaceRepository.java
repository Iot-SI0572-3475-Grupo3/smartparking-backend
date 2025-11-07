package com.smartparking.Smartparking.repository.space_iot;

import com.smartparking.Smartparking.entity.space_iot.ParkingSpace;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ParkingSpaceRepository extends JpaRepository<ParkingSpace, String> {

    @Query("SELECT ps FROM ParkingSpace ps WHERE ps.status = 'available'")
    List<ParkingSpace> findAvailableSpaces();

    List<ParkingSpace> findAll();

    @Query("SELECT ps FROM ParkingSpace ps ORDER BY ps.code ASC")
    List<ParkingSpace> findAllOrderedByCode();

    List<ParkingSpace> findByStatus(ParkingSpace.SpaceStatus status);

    boolean existsByCode(String code);
}