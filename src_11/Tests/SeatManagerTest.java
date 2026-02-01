package Tests;

import FlightManagement.*;
import ServicesAndManagers.ReservationOperationException;
import ServicesAndManagers.SeatManager;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class SeatManagerTest {

    @Test
    void emptySeatsCount_shouldDecreaseAfterReservation() {
        PlaneConfig pc = new PlaneConfig("B737", 30, 6, 5);
        Plane plane = new Plane("P1", pc);

        Flight flight = new Flight(new Route("ANK","IST",450,2), plane, 101, "09.01.2026", "14:30", 60);

        int before = SeatManager.emptyCount(flight.getSeatStatus());

        // 1A'yı rezerve et
        Seat s = SeatManager.requireSeatByNumber(flight.getSeatStatus(), "1A");
        s.setReserveStatus(true);

        int after = SeatManager.emptyCount(flight.getSeatStatus());

        assertEquals(before - 1, after);
    }

    @Test
    void bookingNonExistentSeat_shouldThrowException() {
        PlaneConfig pc = new PlaneConfig("B737", 30, 6, 5);
        Plane plane = new Plane("P1", pc);

        Flight flight = new Flight(new Route("ANK","IST",450,2), plane, 101, "09.01.2026", "14:30", 60);

        assertThrows(ReservationOperationException.class, () -> {
            SeatManager.requireSeatByNumber(flight.getSeatStatus(), "99Z");
        });
    }
}
