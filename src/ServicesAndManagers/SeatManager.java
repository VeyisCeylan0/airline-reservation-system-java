package ServicesAndManagers;

import FlightManagement.Plane;
import FlightManagement.PlaneConfig;
import FlightManagement.Route;
import FlightManagement.Seat;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SeatManager {

    private static final Map<String, PlaneConfig> planeModels = new HashMap<>();
    private static final Path CONFIG_PATH = Path.of("data", "plane_models.txt");

    static {
        loadModels();
        // Eğer hiç model yoksa varsayılan bir tane ekle (Test kolaylığı için)
        if (planeModels.isEmpty()) {
            defineModel(new PlaneConfig("Boeing-737", 30, 6, 5));
        }
    }

    public static void defineModel(PlaneConfig config) {
        planeModels.put(config.getPlaneModel(), config);
        saveModels();
    }

    public static PlaneConfig getModel(String modelName) {
        return planeModels.get(modelName);
    }

    public static List<String> getModelNames() {
        return new ArrayList<>(planeModels.keySet());
    }

    private static void saveModels() {
        try {
            if (Files.notExists(CONFIG_PATH.getParent())) {
                Files.createDirectories(CONFIG_PATH.getParent());
            }
            try (BufferedWriter bw = Files.newBufferedWriter(CONFIG_PATH)) {
                for (PlaneConfig pc : planeModels.values()) {
                    String line = pc.getPlaneModel() + "|" +
                            pc.getRows() + "|" +
                            pc.getColumns() + "|" +
                            pc.getBusinessRows();
                    bw.write(line);
                    bw.newLine();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Dosyadan Okuma (Private)
    private static void loadModels() {
        if (!Files.exists(CONFIG_PATH)) return;

        try (BufferedReader br = Files.newBufferedReader(CONFIG_PATH)) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] p = line.split("\\|");
                if (p.length < 4) continue;
                String name = p[0];
                int r = Integer.parseInt(p[1]);
                int c = Integer.parseInt(p[2]);
                int b = Integer.parseInt(p[3]);
                planeModels.put(name, new PlaneConfig(name, r, c, b));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // PlaneConfig'e göre uçağın sabit şablon seatMatrix'ini üretir
    // (reserveStatus=false, price=0; class dağılımı businessRows'e göre)
    public static Seat[][] createPanelLayout(PlaneConfig config) {
        int rows = config.getRows();
        int cols = config.getColumns();
        int businessRows = config.getBusinessRows();

        Seat[][] matrix = new Seat[rows][cols];

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                String seatNum = seatNumber(r, c);
                Seat.SeatClass seatClass = (r < businessRows) ? Seat.SeatClass.BUSINESS : Seat.SeatClass.ECONOMY;
                matrix[r][c] = new Seat(seatNum, seatClass, 0, false);
            }
        }
        return matrix;
    }

    // Flight'e özel seatStatus üretir (plane seatMatrix'ten kopya + route'a göre fiyat)
    public static Seat[][] createSeatsForFlight(Plane plane, Route route) {
        Seat[][] template = plane.getSeatMatrix();
        int rows = template.length;
        int cols = template[0].length;

        Seat[][] status = new Seat[rows][cols];

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                Seat t = template[r][c];
                int price = (int) Math.round(CalculatePrice.flightPrice(t.getSeatClass(), route));
                status[r][c] = new Seat(t.getSeatNum(), t.getSeatClass(), price, false);
            }
        }
        return status;
    }

    // GUI için (Seat[][] -> boolean[][])
    public static boolean[][] buildOccupiedMap(Seat[][] seatStatus) {
        int rows = seatStatus.length;
        int cols = seatStatus[0].length;
        boolean[][] map = new boolean[rows][cols];

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                map[r][c] = seatStatus[r][c].isReserveStatus();
            }
        }
        return map;
    }

    public static int occupiedCount(Seat[][] seatStatus) {
        int count = 0;
        for (Seat[] row : seatStatus) {
            for (Seat s : row) {
                if (s.isReserveStatus()) count++;
            }
        }
        return count;
    }

    public static int emptyCount(Seat[][] seatStatus) {
        int total = seatStatus.length * seatStatus[0].length;
        return total - occupiedCount(seatStatus);
    }

    public static Seat findSeatByNumber(Seat[][] seatStatus, String seatNum) {
        for (Seat[] row : seatStatus) {
            for (Seat s : row) {
                if (s.getSeatNum().equalsIgnoreCase(seatNum)) return s;
            }
        }
        return null;
    }

    private static String seatNumber(int r, int c) {
        // 0->1, 0->A, 1->B...
        char letter = (char) ('A' + c);
        return (r + 1) + String.valueOf(letter);
    }

    public static Seat requireSeatByNumber(Seat[][] seatStatus, String seatNum) {
        Seat s = findSeatByNumber(seatStatus, seatNum);
        if (s == null) throw new ReservationOperationException("Seat not found: " + seatNum);
        return s;
    }
}
