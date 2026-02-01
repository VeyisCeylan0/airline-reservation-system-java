package FlightManagement;

import java.io.Serializable;

public class Route implements Serializable {
    private String arrivalPlace;
    private String departurePlace;
    private int distance; // km
    private int basePrice; // km başı ücret gibi hesapladım calculatePrice da

    public Route(String arrivalPlace, String departurePlace, int distance, int basePrice) {
        this.arrivalPlace = arrivalPlace;
        this.departurePlace = departurePlace;
        this.distance = distance;
        this.basePrice = basePrice;
    }

    public String getArrivalPlace() {
        return arrivalPlace;
    }

    public void setArrivalPlace(String arrivalPlace) {
        this.arrivalPlace = arrivalPlace;
    }

    public String getDeparturePlace() {
        return departurePlace;
    }

    public void setDeparturePlace(String departurePlace) {
        this.departurePlace = departurePlace;
    }

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    public int getBasePrice() {
        return basePrice;
    }

    public void setBasePrice(int basePrice) {
        this.basePrice = basePrice;
    }
}
