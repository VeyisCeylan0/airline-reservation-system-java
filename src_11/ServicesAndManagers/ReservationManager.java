package ServicesAndManagers;

import FlightManagement.Flight;
import FlightManagement.Seat;
import ReservationAndTicketing.Passenger;
import ReservationAndTicketing.Reservation;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class ReservationManager {

    private final List<Reservation> reservations = Collections.synchronizedList(new ArrayList<>());
    private final Object lock = new Object();


    //liste kopyalanırken bir thread in listeye girmesini engelledi
    public List<Reservation> getAllReservations() {
        synchronized (reservations) {
            return new ArrayList<>(reservations);
        }
    }

    public Reservation makeReservation(Flight flight, Passenger passenger, Seat seat, String dateOfReservation, String ownerUsername, boolean synchronizedMode) {

        if (flight == null || passenger == null || seat == null) {
            throw new IllegalArgumentException("flight/passenger/seat null olamaz.");
        }

        if (synchronizedMode) {
            synchronized (lock) {
                return makeReservationSafe(flight, passenger, seat, dateOfReservation, ownerUsername);
            }
        } else {
            return makeReservationUnsafe(flight, passenger, seat, dateOfReservation, ownerUsername);
        }
    }

    private Reservation makeReservationSafe(Flight flight, Passenger passenger, Seat seat, String dateOfReservation, String ownerUsername) {
        if (seat.isReserveStatus()) {
            throw new ReservationOperationException("Seat already reserved: " + seat.getSeatNum());
        }
        seat.setReserveStatus(true);

        String code = "R-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        Reservation r = new Reservation(code, flight, passenger, seat, dateOfReservation, ownerUsername);
        reservations.add(r);
        return r;
    }

    // NOT SYNCHRONIZED
    private Reservation makeReservationUnsafe(Flight flight, Passenger passenger, Seat seat, String dateOfReservation, String ownerUsername) {

        boolean seenEmpty = !seat.isReserveStatus();

        try { Thread.sleep(2); } catch (InterruptedException ignored) {}

        if (seenEmpty) {
            seat.setReserveStatus(true);
        }

        String code = "R-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        Reservation r = new Reservation(code, flight, passenger, seat, dateOfReservation, ownerUsername);
        reservations.add(r);
        return r;
    }

    public boolean cancelReservation(String reservationCode) {
        synchronized (reservations) {
            Iterator<Reservation> it = reservations.iterator();
            while (it.hasNext()) {
                Reservation r = it.next();
                if (r.getReservationCode().equals(reservationCode)) {
                    // koltuğu boşalt
                    r.getSeat().setReserveStatus(false);
                    it.remove();
                    TicketManager.deleteTicketsByReservationCode(reservationCode);
                    return true;
                }
            }
        }
        return false;
    }

    // ====== 90 Passenger Simulation ======
    public void runSeatSimulation(Flight flight, int passengerCount, boolean synchronizedMode, SeatSimulationListener listener) {
        reservations.clear(); // simülasyon için temiz başla

        Seat[][] seatStatus = flight.getSeatStatus();

        // önce tüm koltukları boşalt
        for (Seat[] row : seatStatus) {
            for (Seat s : row) s.setReserveStatus(false);
        }

        List<Thread> threads = new ArrayList<>();

        for (int i = 0; i < passengerCount; i++) {
            final int idx = i;

            Thread t = new Thread(() -> {
                Passenger p = new Passenger("SIM-" + idx, "Name" + idx, "Surname" + idx, "contact" + idx);

                boolean booked = false;
                int attempts = 0;

                // Bilet alana kadar
                while (!booked && attempts<seatStatus.length) {
                    int r = ThreadLocalRandom.current().nextInt(seatStatus.length);
                    int c = ThreadLocalRandom.current().nextInt(seatStatus[0].length);
                    Seat chosen = seatStatus[r][c];

                    try {
                        String simDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"));
                        makeReservation(flight, p, chosen, simDate, "SIM", synchronizedMode);
                        booked = true;
                    } catch (Exception ignored) {
                        //dolu koltuk vs
                    }
                    attempts++;
                }

                if (listener != null) {
                    boolean[][] map = SeatManager.buildOccupiedMap(seatStatus);
                    int occ = SeatManager.occupiedCount(seatStatus);
                    int emp = SeatManager.emptyCount(seatStatus);
                    listener.onSimulationUpdate(map, occ, emp);
                }

            }, "Passenger-" + i);

            threads.add(t);
            t.start();
        }

        // join
        for (Thread t : threads) {
            try { t.join(); } catch (InterruptedException ignored) {}
        }

        if (listener != null) {
            boolean[][] map = SeatManager.buildOccupiedMap(seatStatus);
            int occ = SeatManager.occupiedCount(seatStatus);
            int emp = SeatManager.emptyCount(seatStatus);
            listener.onSimulationFinished(map, occ, emp);
        }
    }

    public void loadReservations(List<Reservation> list) {
        synchronized (reservations) {
            reservations.clear();
            reservations.addAll(list);
        }
    }
}

