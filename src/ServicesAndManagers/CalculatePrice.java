package ServicesAndManagers;

import FlightManagement.Route;
import FlightManagement.Seat;
import ReservationAndTicketing.Baggage;

import java.util.List;

public class CalculatePrice {

    private static final double extraBaggageFee=50; //euros
    private static final double extraWeightPerKG=5;

    public static double flightPrice(Seat.SeatClass seatClass, Route route){
        double flightPrice=0.0;
        //Route, SeatClass
        // route.basePrice, distance, Economy or Business
        flightPrice+=route.getBasePrice()*route.getDistance();

        if(seatClass== Seat.SeatClass.BUSINESS){
            flightPrice*=2;
        }

        return flightPrice;
    }

    public static double baggageFee(Seat.SeatClass type, List<Baggage> baggageList){
        //SeatClass a ve ağırlık, bagaj sayısı vs. göre bagaş price ı verir.
        double baggageFee=0.0;

        if(baggageList==null){
            return 0.0;
        }

        for(int i=0; i<baggageList.size(); i++){
            Baggage baggage = baggageList.get(i);
            double weight=baggage.getWeight();

            if(i< type.getFreeBaggage()){
                if(weight<=type.getAllowedWeightPerBag()){
                    baggageFee+=0;
                }
                else{
                    baggageFee+= (weight-type.getAllowedWeightPerBag())*extraWeightPerKG;
                }
            }
            else{
                baggageFee+=extraBaggageFee;
                baggageFee+= weight*extraWeightPerKG;
            }
        }
        return baggageFee;
    }

    public static double totalTicketPrice(Seat.SeatClass seatClass, List<Baggage> baggageList, Route route){
        double flightPrice = flightPrice(seatClass, route);
        double baggageFee = baggageFee(seatClass, baggageList);

        return flightPrice + baggageFee;
    }

}
