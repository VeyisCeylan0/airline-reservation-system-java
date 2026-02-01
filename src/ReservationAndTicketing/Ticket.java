package ReservationAndTicketing;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Ticket implements Serializable {

    private String ticketID;
    private Reservation reservation;
    private int price;
    private List<Baggage> baggageList;

    public Ticket(String ticketID, Reservation reservation, int price, List<Baggage> baggageList) {
        this.ticketID = ticketID;
        this.reservation = reservation;
        this.price = price;
        this.baggageList = (baggageList != null) ? baggageList : new ArrayList<>();
    }

    public void addBaggage(Baggage baggage){
        baggageList.add(baggage);
    }


    public String getTicketID() {
        return ticketID;
    }

    public void setTicketID(String ticketID) {
        this.ticketID = ticketID;
    }

    public Reservation getReservation() {
        return reservation;
    }

    public void setReservation(Reservation reservation) {
        this.reservation = reservation;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public List<Baggage> getBaggageList(){
        return baggageList;
    }

}
