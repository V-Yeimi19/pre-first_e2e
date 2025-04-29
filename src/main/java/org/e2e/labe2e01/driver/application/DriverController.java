package org.e2e.labe2e01.driver.application;


import lombok.RequiredArgsConstructor;
import org.e2e.labe2e01.coordinate.domain.Coordinate;
import org.e2e.labe2e01.driver.domain.Driver;
import org.e2e.labe2e01.driver.infrastructure.DriverRepository;
import org.e2e.labe2e01.vehicle.domain.Vehicle;
import org.e2e.labe2e01.vehicle.infrastructure.VehicleRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/driver")
@RequiredArgsConstructor
public class DriverController {
    private final DriverRepository driverRepository;
    private final VehicleRepository vehicleRepository;

    @GetMapping("/{id}")
    public ResponseEntity<Driver> getDriver(@PathVariable Long id) {
        return driverRepository.findById(id).map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Driver> createDriver(@RequestBody Driver driver) {
        Driver savedDriver = driverRepository.save(driver);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedDriver);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDriver(@PathVariable Long id) {
        Driver driver = driverRepository.findById(id).orElse(null);
        if (driver != null) {
            driverRepository.delete(driver);
            return ResponseEntity.noContent().<Void>build();
        }
        return ResponseEntity.notFound().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Driver> updateDriver(@PathVariable Long id, @RequestBody Driver new_driver) {
        return driverRepository.findById(id).map(existingDriver -> {
            existingDriver.setFirstName(new_driver.getFirstName());
            existingDriver.setLastName(new_driver.getLastName());
            existingDriver.setEmail(new_driver.getEmail());
            existingDriver.setPhoneNumber(new_driver.getPhoneNumber());
            return ResponseEntity.ok(driverRepository.save(existingDriver));
        }).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PatchMapping("/{id}/location")
    public ResponseEntity<Driver> updateLocation(@PathVariable Long id, @RequestParam Double latitude, @RequestParam Double longitude) {

        Driver driver = driverRepository.findById(id).orElse(null);

        if (driver == null) {
            return ResponseEntity.notFound().build();
        }

        Coordinate coordinate = driver.getCoordinate();
        if (coordinate == null) {
            coordinate = new Coordinate();
            driver.setCoordinate(coordinate);
        }

        coordinate.setLatitude(latitude);
        coordinate.setLongitude(longitude);

        Driver updatedDriver = driverRepository.save(driver);
        return ResponseEntity.ok(updatedDriver);
    }


    @PatchMapping("/{id}/car")
    public ResponseEntity<Driver> updateCar(@PathVariable Long id, @RequestBody Vehicle new_vehicle) {
        return driverRepository.findById(id).map(driver -> {
            if (driver.getVehicle() != null) {
                vehicleRepository.delete(driver.getVehicle());
            }
            new_vehicle.setDriver(driver);
            Vehicle savedVehicle = vehicleRepository.save(new_vehicle);
            driver.setVehicle(savedVehicle);
            Driver updatedDriver = driverRepository.save(driver);
            return ResponseEntity.ok(updatedDriver);
        }).orElseGet(() -> ResponseEntity.notFound().build());
    }
}