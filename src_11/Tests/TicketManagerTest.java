package Tests;

import FlightManagement.*;
import ReservationAndTicketing.Baggage;
import ReservationAndTicketing.Passenger;
import ReservationAndTicketing.Reservation;
import ReservationAndTicketing.Ticket;
import ServicesAndManagers.CalculatePrice;
import ServicesAndManagers.SeatManager;
import ServicesAndManagers.TicketManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class TicketManagerTest {

    @BeforeEach
    void clearTickets() {
        TicketManager.getAllTickets().clear();
    }

    @Test
    void generateTicket_shouldCreateTicketAndFilterByOwner() {

        Route route = new Route("ANK", "IST", 100, 3);
        PlaneConfig pc = new PlaneConfig("B737", 30, 6, 5);
        Plane plane = new Plane("P001", pc);
        Flight flight = new Flight(route, plane, 101, "09.01.2026", "14:30", 60);

        Seat seat = SeatManager.findSeatByNumber(flight.getSeatStatus(), "1A");
        assertNotNull(seat);

        Passenger passenger = new Passenger("P1", "Ali", "Veli", "0555");
        String owner = "berat";

        Reservation reservation = new Reservation(
                "R-0001",
                flight,
                passenger,
                seat,
                "NOW",
                owner
        );

        List<Baggage> baggages = List.of(new Baggage(30.0));
        double expectedPrice = CalculatePrice.totalTicketPrice(
                seat.getSeatClass(),
                baggages,
                route
        );

        Ticket ticket = TicketManager.generateTicket(reservation, baggages, route);

        assertNotNull(ticket);
        assertEquals((int) expectedPrice, ticket.getPrice());
        assertEquals(owner, ticket.getReservation().getOwnerUsername());

        List<Ticket> ownerTickets = TicketManager.getTicketsByOwner(owner);
        assertEquals(1, ownerTickets.size());

        List<Ticket> otherTickets = TicketManager.getTicketsByOwner("someoneElse");
        assertTrue(otherTickets.isEmpty());
    }
}
