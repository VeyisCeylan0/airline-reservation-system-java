package ReservationAndTicketing;

import java.io.Serializable;

public class Baggage implements Serializable {

    private double weight;

    //private String baggageId;
    //belki sonraId eklerim.

    public Baggage(double weight){
        this.weight=weight;
    }

    public double getWeight(){
        return weight;
    }

}
