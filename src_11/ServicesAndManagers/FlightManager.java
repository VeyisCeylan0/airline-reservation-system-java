package ServicesAndManagers;

import FlightManagement.Flight;

import java.util.ArrayList;
import java.util.List;

public class FlightManager {

    private final List<Flight> flights = new ArrayList<>();

    public void addFlight(Flight f) {
        flights.add(f);
    }

    public boolean deleteFlight(int flightNum) {
        return flights.removeIf(f -> f.getFlightNum() == flightNum);
    }

    public Flight getFlightByNum(int flightNum) {
        for (Flight f : flights) {
            if (f.getFlightNum() == flightNum){
                return f;
            }
        }
        return null;
    }

    public List<Flight> getAllFlights(){
        return new ArrayList<>(flights);
    }

    public List<Flight> searchFlights(String departure, String arrival) {
        List<Flight> found = new ArrayList<>();
        for (Flight f : flights) {
            if (f.getDeparturePlace().equalsIgnoreCase(departure)
                    && f.getArrivalPlace().equalsIgnoreCase(arrival)) {
                found.add(f);
            }
        }
        return found;
    }

    public List<Flight> searchUpcomingFlights(String departure, String arrival, String nowDate, String nowHour) {
        List<Flight> found = new ArrayList<>();
        for (Flight f : flights) {
            if (f.getDeparturePlace().equalsIgnoreCase(departure)
                    && f.getArrivalPlace().equalsIgnoreCase(arrival)) {

                String dt = f.getDate() + " " + f.getHour();
                String now = nowDate + " " + nowHour;

                if (dt.compareTo(now) >= 0) {
                    found.add(f);
                }
            }
        }
        return found;
    }
}
