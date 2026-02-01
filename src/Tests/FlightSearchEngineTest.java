package Tests;

import FlightManagement.Flight;
import FlightManagement.Plane;
import FlightManagement.PlaneConfig;
import FlightManagement.Route;
import ServicesAndManagers.FlightManager;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class FlightSearchEngineTest {

    @Test
    void filterByDepartureArrival_shouldReturnCorrectFlights() {
        FlightManager fm = new FlightManager();

        PlaneConfig pc = new PlaneConfig("B737", 30, 6, 5);
        Plane p = new Plane("P1", pc);

        fm.addFlight(new Flight(new Route("ANK","IST",450,2), p, 101, "09.01.2026", "14:30", 60));
        fm.addFlight(new Flight(new Route("IZM","IST",350,2), p, 102, "09.01.2026", "16:30", 60));

        List<Flight> found = fm.searchFlights("IST","ANK");
        assertEquals(1, found.size());
        assertEquals(101, found.get(0).getFlightNum());
    }

    @Test
    void eliminateFlightsWhoseDepartureTimeHasPassed_shouldRemovePastFlights() {
        FlightManager fm = new FlightManager();

        PlaneConfig pc = new PlaneConfig("B737", 30, 6, 5);
        Plane p = new Plane("P1", pc);

        // biri geçmiş, biri gelecek
        fm.addFlight(new Flight(new Route("ANK","IST",450,2), p, 101, "08.01.2026", "10:00", 60));
        fm.addFlight(new Flight(new Route("ANK","IST",450,2), p, 102, "09.01.2026", "14:30", 60));

        // "şu an" 08.01.2026 12:00
        List<Flight> upcoming = fm.searchUpcomingFlights("IST","ANK", "08.01.2026", "12:00");

        assertEquals(1, upcoming.size());
        assertEquals(102, upcoming.get(0).getFlightNum());
    }
}
