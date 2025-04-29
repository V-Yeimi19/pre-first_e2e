package org.e2e.labe2e01.passenger.application;

import lombok.RequiredArgsConstructor;
import org.e2e.labe2e01.coordinate.domain.Coordinate;
import org.e2e.labe2e01.coordinate.infrastructure.CoordinateRepository;
import org.e2e.labe2e01.passenger.domain.Passenger;
import org.e2e.labe2e01.passenger.infrastructure.PassengerRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;


@RestController
@RequestMapping("/passenger")
@RequiredArgsConstructor
public class PassengerController {
    private final PassengerRepository passengerRepository;
    private final CoordinateRepository coordinateRepository;

    @GetMapping ("/{id}")
    public ResponseEntity<Passenger> getPassengerById(@PathVariable Long id) {
        return ResponseEntity.ok(passengerRepository.findById(id).orElse(null));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePassengerById(@PathVariable Long id) {
        passengerRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Passenger> updatePassenger(
            @PathVariable Long id,
            @RequestParam String description,
            @RequestBody Coordinate newCoordinate) {

        Passenger passenger = passengerRepository.findById(id).orElse(null);
        passenger.setDescription(description);
        passenger.setCoordinate(newCoordinate);
        return ResponseEntity.ok(passengerRepository.save(passenger));
    }


    @GetMapping("/{id}/places")
    public ResponseEntity<List<Coordinate>> getPassengerPlaces(@PathVariable Long id) {
        return ResponseEntity.ok(passengerRepository.findById(id)
                .map(Passenger::getPlacesList)
                .orElse(null));
    }

    @DeleteMapping("/{id}/places/{coordinateId}")
    public ResponseEntity<Void> deletePassengerPlace(
            @PathVariable Long id,
            @PathVariable Long coordinateId) {

        Passenger passenger = passengerRepository.findById(id).orElse(null);
        passenger.getPlacesList().removeIf(c -> c.getId().equals(coordinateId));
        passengerRepository.save(passenger);
        return ResponseEntity.noContent().build();
    }
}

