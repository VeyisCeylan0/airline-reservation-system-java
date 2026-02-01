package FlightManagement;

import ServicesAndManagers.SeatManager;

import java.io.Serializable;

public class Flight implements Serializable {

    private int flightNum;
    private Route route; // route info
    private Plane plane; // bu uçuşu hangi uçakla yapıcaz,
    // -Bu uçakta kaç koltuk var , hangileri economy business vs.-
    private Seat[][] seatStatus; // Bir uçak birden fazla uçuş yapabilir,
    // hangi uçuşta hangi koltuklar dolu vs.

    // Plane deki seatMatrix şablon, flighttaki seatStatus güncel olan
    // Yani SeatMatrix de uçağın temel bilgileri olacak hangileri economy, business
    // kaç koltuk var vs.  (reserveStatus false)

    //Flight atandığında , planedeki şablonu kullanıcaz, Bu kopyalama işlemi seatManager da olacak
    private String date;
    private String hour;
    private int duration;

    public Flight(Route route, Plane plane, int flightNUm, String date, String hour, int duration) {
        this.route=route;
        this.plane=plane;
        this.seatStatus= SeatManager.createSeatsForFlight(plane, route);
        this.flightNum = flightNUm;
        this.date = date;
        this.hour = hour;
        this.duration = duration;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public String getHour() {
        return hour;
    }

    public void setHour(String hour) {
        this.hour = hour;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getArrivalPlace() {
        return this.route.getArrivalPlace();
    }


    public String getDeparturePlace() {
        return this.route.getDeparturePlace();
    }

    public int getFlightNum() {
        return flightNum;
    }

    public void setFlightNUm(int flightNUm) {
        this.flightNum = flightNUm;
    }

    public Route getRoute() {
        return route;
    }

    public Plane getPlane() {
        return plane;
    }

    public Seat[][] getSeatStatus() {
        return seatStatus;
    }

    @Override
    public String toString() {
        // İstenen Format: IST -> ANK | Boeing-737 | 450 TL
        // Karışıklık olmasın diye Saat bilgisini de ekledim, istersen çıkarabilirsin.
        return getDeparturePlace() + " -> " + getArrivalPlace() +
                " | " + getPlane().getPlaneModel() +
                " | " + date + " " + hour +
                " | " + route.getBasePrice()*route.getDistance() + " TL";
    }
}
