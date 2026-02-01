package GUI;

import ReservationAndTicketing.Ticket;
import ServicesAndManagers.TicketManager;
import ServicesAndManagers.UserManager;

import javax.swing.*;
import java.awt.*;

public class TicketFrame extends JFrame {

    private final DefaultListModel<String> model = new DefaultListModel<>();
    private final JList<String> list = new JList<>(model);

    private final JButton btnRefresh = new JButton("Refresh");

    public TicketFrame() {
        setTitle("My Tickets");
        setSize(560, 360);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(8, 8));

        add(new JScrollPane(list), BorderLayout.CENTER);

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottom.add(btnRefresh);
        add(bottom, BorderLayout.SOUTH);

        btnRefresh.addActionListener(e -> refresh());
        refresh();

        setVisible(true);
    }

    private void refresh() {
        model.clear();
        String owner = UserManager.getActiveUser() == null ? "GUEST" : UserManager.getActiveUser().getUsername();

        for (Ticket t : TicketManager.getTicketsByOwner(owner)) {
            model.addElement(
                    t.getTicketID()
                            + " | Flight#" + t.getReservation().getFlight().getFlightNum()
                            + " | Seat " + t.getReservation().getSeat().getSeatNum()
                            + " | Price " + t.getPrice()
            );
        }
    }
}
