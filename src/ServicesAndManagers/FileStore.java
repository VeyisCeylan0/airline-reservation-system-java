package ServicesAndManagers;

import FlightManagement.*;
import ReservationAndTicketing.Passenger;
import ReservationAndTicketing.Reservation;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FileStore {

    private final Path baseDir;

    public FileStore(String folderName) {
        baseDir = Path.of(folderName);
        try { Files.createDirectories(baseDir); }
        catch (IOException e) { throw new RuntimeException(e); }
    }

    // flightNum|dep|arr|distance|basePrice|planeID|planeModel|rows|cols|businessRows|date|hour|duration
    public void saveFlights(List<Flight> flights, String fileName) {
        Path file = baseDir.resolve(fileName);

        try (BufferedWriter bw = Files.newBufferedWriter(file)) {
            for (Flight f : flights) {
                Route r = f.getRoute();
                Plane p = f.getPlane();
                PlaneConfig pc = p.getPlaneConfig();

                String line = f.getFlightNum() + "|" +
                        r.getDeparturePlace() + "|" + r.getArrivalPlace() + "|" +
                        r.getDistance() + "|" + r.getBasePrice() + "|" +
                        p.getPlaneID() + "|" + pc.getPlaneModel() + "|" +
                        pc.getRows() + "|" + pc.getColumns() + "|" + pc.getBusinessRows() + "|" +
                        f.getDate() + "|" + f.getHour() + "|" + f.getDuration();

                bw.write(line);
                bw.newLine();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Flight> loadFlights(String fileName) {
        Path file = baseDir.resolve(fileName);
        List<Flight> flights = new ArrayList<>();
        if (!Files.exists(file)) return flights;

        try (BufferedReader br = Files.newBufferedReader(file)) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] p = line.split("\\|");
                if (p.length < 13) continue;

                int flightNum = Integer.parseInt(p[0].trim());
                String dep = p[1].trim();       // " IST " -> "IST" olur
                String arr = p[2].trim();       // " ANK " -> "ANK" olur
                int distance = Integer.parseInt(p[3].trim());
                int basePrice = Integer.parseInt(p[4].trim());

                String planeID = p[5].trim();
                String planeModel = p[6].trim();
                int rows = Integer.parseInt(p[7].trim());
                int cols = Integer.parseInt(p[8].trim());
                int businessRows = Integer.parseInt(p[9].trim());

                String date = p[10].trim();
                String hour = p[11].trim();
                int duration = Integer.parseInt(p[12].trim());

                Route route = new Route(arr, dep, distance, basePrice);
                PlaneConfig config = new PlaneConfig(planeModel, rows, cols, businessRows);
                Plane plane = new Plane(planeID, config);

                Flight flight = new Flight(route, plane, flightNum, date, hour, duration);
                flights.add(flight);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (NumberFormatException e) {
            System.err.println("Dosya format hatası (Sayısal değer bozuk): " + e.getMessage());
        }

        return flights;
    }

    // reservationCode|flightNum|passengerID|name|surname|contact|seatNum|date|ownerUsername
    public void saveReservations(List<Reservation> reservations, String fileName) {
        Path file = baseDir.resolve(fileName);

        try (BufferedWriter bw = Files.newBufferedWriter(file)) {
            for (Reservation r : reservations) {
                String line =
                        r.getReservationCode() + "|" +
                                r.getFlight().getFlightNum() + "|" +
                                r.getPassenger().getPassengerID() + "|" +
                                r.getPassenger().getName() + "|" +
                                r.getPassenger().getSurname() + "|" +
                                r.getPassenger().getContactInfo() + "|" +
                                r.getSeat().getSeatNum() + "|" +
                                r.getDateOfReservation() + "|" +
                                r.getOwnerUsername();

                bw.write(line);
                bw.newLine();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Reservation> loadReservations(String fileName, List<Flight> flights) {
        Path file = baseDir.resolve(fileName);
        List<Reservation> loaded = new ArrayList<>();
        if (!Files.exists(file)) return loaded;

        Map<Integer, Flight> byNum = new HashMap<>();
        for (Flight f : flights) byNum.put(f.getFlightNum(), f);

        try (BufferedReader br = Files.newBufferedReader(file)) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] p = line.split("\\|");
                if (p.length < 9) continue;

                String code = p[0];
                int flightNum = Integer.parseInt(p[1]);
                String pid = p[2];
                String name = p[3];
                String surname = p[4];
                String contact = p[5];
                String seatNum = p[6];
                String date = p[7];
                String owner = p[8];

                Flight flight = byNum.get(flightNum);
                if (flight == null) continue;

                Seat seat = SeatManager.findSeatByNumber(flight.getSeatStatus(), seatNum);
                if (seat == null) continue;

                seat.setReserveStatus(true);

                Passenger passenger = new Passenger(pid, name, surname, contact);
                Reservation r = new Reservation(code, flight, passenger, seat, date, owner);
                loaded.add(r);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return loaded;
    }
}
