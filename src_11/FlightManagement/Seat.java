package FlightManagement;

import java.io.Serializable;

public class Seat implements Serializable {

    private String seatNum;
    private int price;
    private boolean reserveStatus;
    public enum SeatClass{
        ECONOMY(1, 23), BUSINESS(2, 32);

        private int maxBaggage=3;
        private int freeBaggage;
        private double allowedWeightPerBag;

        SeatClass(int freeBaggage, double weight){
            this.freeBaggage=freeBaggage;
            this.allowedWeightPerBag=weight;
        }

        public int getFreeBaggage() {
            return freeBaggage;
        }

        public double getAllowedWeightPerBag() {
            return allowedWeightPerBag;
        }

        public int getMaxBaggage() {
            return maxBaggage;
        }

    }

    private SeatClass seatClass;

    public Seat(String seatNum, SeatClass seatClass, int price, boolean reserveStatus) {
        this.seatNum = seatNum;
        this.seatClass=seatClass;
        this.price = price;
        this.reserveStatus = reserveStatus;
    }

    public String getSeatNum() {
        return seatNum;
    }

    public void setSeatNum(String seatNum) {
        this.seatNum = seatNum;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public boolean isReserveStatus() {
        return reserveStatus;
    }

    public void setReserveStatus(boolean reserveStatus) {
        this.reserveStatus = reserveStatus;
    }

    public SeatClass getSeatClass() {
        return seatClass;
    }

    public void setSeatClass(SeatClass seatClass) {
        this.seatClass = seatClass;
    }


}
