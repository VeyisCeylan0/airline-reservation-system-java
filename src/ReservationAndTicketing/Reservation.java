package ReservationAndTicketing;

import FlightManagement.Flight;
import FlightManagement.Seat;

import java.io.Serializable;

public class Reservation implements Serializable {

    private String reservationCode;
    private Flight flight;
    private Passenger passenger;
    private Seat seat;
    private String dateOfReservation;
    private String ownerUsername;

    public Reservation(String reservationCode, Flight flight, Passenger passenger, Seat seat, String dateOfReservation, String ownerUsername) {
        this.reservationCode = reservationCode;
        this.flight = flight;
        this.passenger = passenger;
        this.seat = seat;
        this.dateOfReservation = dateOfReservation;
        this.ownerUsername=ownerUsername;
    }

    public String getOwnerUsername(){
        return ownerUsername;
    }

    public String getReservationCode() {
        return reservationCode;
    }

    public void setReservationCode(String reservationCode) {
        this.reservationCode = reservationCode;
    }

    public Flight getFlight() {
        return flight;
    }

    public void setFlight(Flight flight) {
        this.flight = flight;
    }

    public Passenger getPassenger() {
        return passenger;
    }

    public void setPassenger(Passenger passenger) {
        this.passenger = passenger;
    }

    public Seat getSeat() {
        return seat;
    }

    public void setSeat(Seat seat) {
        this.seat = seat;
    }

    public String getDateOfReservation() {
        return dateOfReservation;
    }

    public void setDateOfReservation(String dateOfReservation) {
        this.dateOfReservation = dateOfReservation;
    }

    // Reservation.java dosyasının içi:

    @Override
    public String toString() {
        // Çıktı Örneği:
        // R-ABCD123 | Flight: 101 (IST->ANK) | Seat: 1A | Passenger: Ahmet Yilmaz

        return reservationCode +
                " | Flight: " + flight.getFlightNum() +
                " (" + flight.getDeparturePlace() + "->" + flight.getArrivalPlace() + ")" +
                " | Seat: " + seat.getSeatNum() +
                " | Pass: " + passenger.getName() + " " + passenger.getSurname();
    }
}
