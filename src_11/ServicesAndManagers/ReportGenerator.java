package ServicesAndManagers;

import FlightManagement.Flight;
import FlightManagement.Seat;

import java.util.List;

public class ReportGenerator implements Runnable {

    private final List<Flight> flights;
    private final ReportListener listener;

    public ReportGenerator(List<Flight> flights, ReportListener listener) {
        this.flights = flights;
        this.listener = listener;
    }

    @Override
    public void run() {
        if (listener != null) listener.onReportStatus("Preparing report...");

        StringBuilder sb = new StringBuilder();
        sb.append("=== OCCUPANCY REPORT ===\n");

        for (Flight f : flights) {
            Seat[][] status = f.getSeatStatus();
            int occ = SeatManager.occupiedCount(status);
            int total = status.length * status[0].length;

            double pct = (total == 0) ? 0 : (occ * 100.0 / total);

            sb.append("Flight#").append(f.getFlightNum())
                    .append(" | ").append(f.getDeparturePlace()).append(" -> ").append(f.getArrivalPlace())
                    .append(" | ").append(String.format("%.2f", pct)).append("%\n");
        }

        if (listener != null) listener.onReportReady(sb.toString());
    }
}
