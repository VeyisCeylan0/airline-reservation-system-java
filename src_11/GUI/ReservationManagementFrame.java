package GUI;

import FlightManagement.Flight;
import FlightManagement.Seat;
import ReservationAndTicketing.Baggage;
import ReservationAndTicketing.Reservation;
import ReservationAndTicketing.Ticket;
import ServicesAndManagers.*;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class ReservationManagementFrame extends JFrame {

    private final ReservationManager reservationManager;
    private final Flight flight;

    private final DefaultListModel<Reservation> model = new DefaultListModel<>();
    private final JList<Reservation> list = new JList<>(model);

    private final JButton btnRefresh = new JButton("Refresh");
    private final JButton btnCancel = new JButton("Cancel Selected");
    private final JButton btnTicket = new JButton("Generate Ticket");

    public ReservationManagementFrame(ReservationManager reservationManager, Flight flight) {
        this.reservationManager = reservationManager;
        this.flight = flight;

        setTitle("Reservations");
        setSize(640, 420);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(8, 8));

        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        add(new JScrollPane(list), BorderLayout.CENTER);

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottom.add(btnRefresh);
        bottom.add(btnCancel);
        bottom.add(btnTicket);
        add(bottom, BorderLayout.SOUTH);

        btnRefresh.addActionListener(e -> refresh());
        btnCancel.addActionListener(e -> cancelSelected());
        btnTicket.addActionListener(e -> ticketSelected());

        refresh();
        setVisible(true);
    }

    private void refresh() {
        model.clear();

        String owner = UserManager.getActiveUser() == null ? "GUEST" : UserManager.getActiveUser().getUsername();

        for (Reservation r : reservationManager.getAllReservations()) {
            // owner bazlı göster
            if (r.getOwnerUsername().equals(owner)) {
                model.addElement(r);
            }
        }
    }

    private void cancelSelected() {
        Reservation r = list.getSelectedValue();
        if (r == null) return;

        int res = JOptionPane.showConfirmDialog(this,
                "Cancel reservation: " + r.getReservationCode() + " ?", "Confirm",
                JOptionPane.OK_CANCEL_OPTION);

        if (res != JOptionPane.OK_OPTION) return;

        boolean ok = reservationManager.cancelReservation(r.getReservationCode());
        JOptionPane.showMessageDialog(this, ok ? "Cancelled." : "Not found.");
        refresh();
    }

    private void ticketSelected() {
        Reservation r = list.getSelectedValue();
        if (r == null) {
            JOptionPane.showMessageDialog(this, "Lütfen bir rezervasyon seçin.");
            return;
        }
        Seat.SeatClass sClass = r.getSeat().getSeatClass();

        int freeBagCount = sClass.getFreeBaggage();
        double freeWeightLimit = sClass.getAllowedWeightPerBag();

        int maxBagCount = sClass.getMaxBaggage();
        double hardLimit = 32.0;

        // --- KULLANICIYA GÖSTERİLECEK MESAJ ---
        String msg = "Baggage weights (kg).\n" +
                "Class: " + sClass.toString() + "\n" +
                "------------------------------------------------\n" +
                "Free Allowance : " + freeBagCount + " bag(s), max " + freeWeightLimit + " kg each.\n" +
                "Max Quantity   : " + maxBagCount + " bag(s) allowed in total.\n" +
                "Hard Limit     : " + hardLimit + " kg per bag (Cannot be exceeded).\n" +
                "------------------------------------------------\n" +
                "(Excess weight/bags will include extra fee automatically)\n" +
                "Example: 15, 25.5";

        String str = JOptionPane.showInputDialog(this, msg, "");

        List<Baggage> baggages = new ArrayList<>();
        if (str != null && !str.trim().isEmpty()) {
            try {
                String[] parts = str.split(",");

                if (parts.length > maxBagCount) {
                    JOptionPane.showMessageDialog(this,
                            "HATA: En fazla " + maxBagCount + " adet bagaj ekleyebilirsiniz.\n" +
                                    "(Siz " + parts.length + " adet girdiniz).");
                    return;
                }

                for (String s : parts) {
                    double w = Double.parseDouble(s.trim());

                    if (w > hardLimit) {
                        JOptionPane.showMessageDialog(this,
                                "HATA: Tek bir bagaj parçası " + hardLimit + " kg'dan ağır olamaz!\n" +
                                        "(" + w + " kg girdiniz). Lütfen bu bagajı ikiye bölerek girin.");
                        return;
                    }

                    if (w <= 0) {
                        JOptionPane.showMessageDialog(this, "Hatalı ağırlık: " + w);
                        return;
                    }

                    baggages.add(new Baggage(w));
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Hatalı giriş! Sadece sayı ve virgül kullanın.");
                return;
            }
        }

        Ticket t = TicketManager.generateTicket(r, baggages, r.getFlight().getRoute());

        JOptionPane.showMessageDialog(this,
                "Ticket Created Successfully!\n" +
                        "Ticket ID: " + t.getTicketID() + "\n" +
                        "Total Price: " + t.getPrice() + " TL");
    }
}
