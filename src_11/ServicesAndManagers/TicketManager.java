package ServicesAndManagers;

import FlightManagement.Route;
import FlightManagement.Seat;
import ReservationAndTicketing.Baggage;
import ReservationAndTicketing.Reservation;
import ReservationAndTicketing.Ticket;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class TicketManager {

    private static final List<Ticket> tickets = new ArrayList<>();
    private static final Path FILE_PATH = Path.of("data", "tickets.txt");

    /**
     * Yeni bilet üretir ve listeye ekler.
     * (Otomatik kayıt istenirse saveTickets() buradan çağrılabilir)
     */
    public static Ticket generateTicket(Reservation reservation, List<Baggage> baggageList, Route route) {
        if (reservation == null || route == null) {
            throw new IllegalArgumentException("Reservation or Route cannot be null");
        }

        Seat seat = reservation.getSeat();
        double finalPrice = CalculatePrice.totalTicketPrice(seat.getSeatClass(), baggageList, route);

        String ticketID = "TICKET-" + (10000 + new Random().nextInt(90000));

        Ticket newTicket = new Ticket(ticketID, reservation, (int) finalPrice, baggageList);
        tickets.add(newTicket);

        return newTicket;
    }

    public static List<Ticket> getAllTickets() {
        return new ArrayList<>(tickets);
    }

    public static List<Ticket> getTicketsByOwner(String username) {
        List<Ticket> myTickets = new ArrayList<>();
        for (Ticket t : tickets) {
            if (t.getReservation().getOwnerUsername().equals(username)) {
                myTickets.add(t);
            }
        }
        return myTickets;
    }

    /**
     * Biletleri data/tickets.txt dosyasına yazar.
     * Format: TicketID|ReservationCode|Price|BaggageWeights
     */
    public static void saveTickets() {
        try {
            if (Files.notExists(FILE_PATH.getParent())) {
                Files.createDirectories(FILE_PATH.getParent());
            }

            try (BufferedWriter bw = Files.newBufferedWriter(FILE_PATH)) {
                for (Ticket t : tickets) {
                    StringBuilder bagStr = new StringBuilder();
                    for (Baggage b : t.getBaggageList()) {
                        if (bagStr.length() > 0) bagStr.append(",");
                        bagStr.append(b.getWeight());
                    }

                    String line = t.getTicketID() + "|" +
                            t.getReservation().getReservationCode() + "|" +
                            t.getPrice() + "|" +
                            bagStr.toString();

                    bw.write(line);
                    bw.newLine();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Dosyadan biletleri okur.
     * DİKKAT: Biletin oluşması için ilgili Rezervasyonun bulunması gerekir.
     * Bu yüzden parametre olarak tüm rezervasyon listesini ister.
     */
    public static void loadTickets(List<Reservation> allReservations) {
        tickets.clear(); // Listeyi temizle
        if (!Files.exists(FILE_PATH)) return;

        try (BufferedReader br = Files.newBufferedReader(FILE_PATH)) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] p = line.split("\\|");
                if (p.length < 3) continue;

                String ticketID = p[0];
                String resCode = p[1];
                int price = Integer.parseInt(p[2]);
                String bagData = (p.length > 3) ? p[3] : "";

                Reservation foundRes = null;
                for (Reservation r : allReservations) {
                    if (r.getReservationCode().equals(resCode)) {
                        foundRes = r;
                        break;
                    }
                }

                if (foundRes == null) continue;

                List<Baggage> baggageList = new ArrayList<>();
                if (!bagData.isEmpty()) {
                    String[] weights = bagData.split(",");
                    for (String w : weights) {
                        try {
                            baggageList.add(new Baggage(Double.parseDouble(w)));
                        } catch (NumberFormatException ignored) { }
                    }
                }

                Ticket t = new Ticket(ticketID, foundRes, price, baggageList);
                tickets.add(t);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void deleteTicketsByReservationCode(String resCode) {
        boolean removed = tickets.removeIf(t -> t.getReservation().getReservationCode().equals(resCode));

        if (removed) {
            saveTickets(); // Dosyayı da güncelle
            System.out.println("Reservation " + resCode + " ve bileti silindi.");
        }
    }
}