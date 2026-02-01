package FlightManagement;
import java.io.Serializable;

public class PlaneConfig implements Serializable{

    private String planeModel;
    private int rows;
    private int columns;
    private int businessRows;

    public PlaneConfig(String planeModel, int rows, int columns, int businessRows) {
        this.planeModel = planeModel;
        this.rows = rows;
        this.columns = columns;
        this.businessRows = businessRows;
    }

    public String getPlaneModel() {
        return planeModel;
    }

    public void setPlaneModel(String planeModel) {
        this.planeModel = planeModel;
    }

    public int getCapacity() {
        return this.rows * this.columns;
    }

    public int getRows() {
        return rows;
    }

    public void setRows(int rows) {
        this.rows = rows;
    }

    public int getColumns() {
        return columns;
    }

    public void setColumns(int columns) {
        this.columns = columns;
    }

    public int getBusinessRows() {
        return businessRows;
    }

    public void setBusinessRows(int businessRows) {
        this.businessRows = businessRows;
    }
}
