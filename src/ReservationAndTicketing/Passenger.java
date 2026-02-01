package ReservationAndTicketing;

import java.io.Serializable;

public class Passenger implements Serializable {

    private String passengerID;
    private String name;
    private String surname;
    private String contactInfo;

    public Passenger(String passengerID, String name, String surname, String contactInfo) {
        this.passengerID = passengerID;
        this.name = name;
        this.surname = surname;
        this.contactInfo = contactInfo;
    }

    public String getPassengerID() {
        return passengerID;
    }

    public void setPassengerID(String passengerID) {
        this.passengerID = passengerID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getContactInfo() {
        return contactInfo;
    }

    public void setContactInfo(String contactInfo) {
        this.contactInfo = contactInfo;
    }
}
