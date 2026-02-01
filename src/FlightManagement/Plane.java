package FlightManagement;

import ServicesAndManagers.SeatManager;

import java.io.Serializable;

public class Plane implements Serializable {

    private String planeID;
    private PlaneConfig planeConfig;
    private Seat[][] seatMatrix;

    public Plane(String planeID, PlaneConfig planeConfig) {
        this.planeID = planeID;
        this.planeConfig=planeConfig;
        this.seatMatrix= SeatManager.createPanelLayout(planeConfig);
    }

    public String getPlaneID() {
        return planeID;
    }

    public void setPlaneID(String planeID) {
        this.planeID = planeID;
    }

    public String getPlaneModel() {
        return planeConfig.getPlaneModel();
    }

    public int getCapacity() {
        return planeConfig.getCapacity();
    }

    public Seat[][] getSeatMatrix() {
        return seatMatrix;
    }

    public void setSeatMatrix(Seat[][] seatMatrix) {
        this.seatMatrix = seatMatrix;
    }

    public PlaneConfig getPlaneConfig(){
        return planeConfig;
    }
}
