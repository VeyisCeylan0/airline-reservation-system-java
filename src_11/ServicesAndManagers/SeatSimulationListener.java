package ServicesAndManagers;

public interface SeatSimulationListener {
    void onSimulationUpdate(boolean[][] occupiedMap, int occupiedCount, int emptyCount);
    void onSimulationFinished(boolean[][] occupiedMap, int occupiedCount, int emptyCount);
}
