package Tests;

import FlightManagement.Route;
import FlightManagement.Seat;
import ReservationAndTicketing.Baggage;
import ServicesAndManagers.CalculatePrice;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class CalculatePriceTest {

    @Test
    void economyFlightPrice_shouldBeBasePriceTimesDistance() {
        Route route = new Route("ANK", "IST", 450, 2);
        double expected = 2 * 450;

        double actual = CalculatePrice.flightPrice(Seat.SeatClass.ECONOMY, route);

        assertEquals(expected, actual, 1e-9);
    }

    @Test
    void businessFlightPrice_shouldBeDoubleOfEconomy() {
        Route route = new Route("ANK", "IST", 450, 2);

        double economy = CalculatePrice.flightPrice(Seat.SeatClass.ECONOMY, route);
        double business = CalculatePrice.flightPrice(Seat.SeatClass.BUSINESS, route);

        assertEquals(economy * 2, business, 1e-9);
    }

    @Test
    void baggageFee_nullList_shouldReturnZero() {
        double fee = CalculatePrice.baggageFee(Seat.SeatClass.ECONOMY, null);
        assertEquals(0.0, fee, 1e-9);
    }

    @Test
    void baggageFee_freeBaggageWithinLimit_shouldBeZero() {
        List<Baggage> baggages = List.of(new Baggage(23.0));

        double fee = CalculatePrice.baggageFee(Seat.SeatClass.ECONOMY, baggages);

        assertEquals(0.0, fee, 1e-9);
    }

    @Test
    void baggageFee_overweightBaggage_shouldChargePerKg() {
        List<Baggage> baggages = List.of(new Baggage(30.0));

        double fee = CalculatePrice.baggageFee(Seat.SeatClass.ECONOMY, baggages);

        assertEquals((30.0 - 23.0) * 5.0, fee, 1e-9);
    }

    @Test
    void baggageFee_extraBag_shouldAddExtraBaggageFee() {
        List<Baggage> baggages = List.of(
                new Baggage(20.0),
                new Baggage(10.0)
        );

        double fee = CalculatePrice.baggageFee(Seat.SeatClass.ECONOMY, baggages);

        double expected = 50.0 + 10.0 * 5.0;
        assertEquals(expected, fee, 1e-9);
    }

    @Test
    void businessTwoFreeBagsWithinLimit_shouldBeZero() {
        List<Baggage> baggages = List.of(
                new Baggage(32.0),
                new Baggage(25.0)
        );

        double fee = CalculatePrice.baggageFee(Seat.SeatClass.BUSINESS, baggages);

        assertEquals(0.0, fee, 1e-9);
    }

    @Test
    void totalTicketPrice_shouldBeFlightPlusBaggage() {
        Route route = new Route("ANK", "IST", 100, 3);
        List<Baggage> baggages = new ArrayList<>();
        baggages.add(new Baggage(30.0));

        double total = CalculatePrice.totalTicketPrice(
                Seat.SeatClass.ECONOMY,
                baggages,
                route
        );

        assertEquals(300.0 + 35.0, total, 1e-9);
    }
}
