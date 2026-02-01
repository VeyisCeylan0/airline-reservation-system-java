package GUI;

import FlightManagement.*;
import ReservationAndTicketing.Passenger;
import ReservationAndTicketing.Reservation;
import ServicesAndManagers.*;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class MainFrame extends JFrame implements SeatSimulationListener, ReportListener {

    private final FileStore fileStore = new FileStore("data");

    // Managers
    private final FlightManager flightManager = new FlightManager();
    private final ReservationManager reservationManager = new ReservationManager();

    // Active flight
    private Flight activeFlight;

    // GUI Components
    private final JTextField txtDeparture = new JTextField("IST", 5);
    private final JTextField txtArrival = new JTextField("ANK", 5);
    private final JButton btnSearch = new JButton("Search Flights");

    private final JComboBox<Flight> cmbFlights = new JComboBox<>();
    private final JButton btnSelectFlight = new JButton("Select Flight");

    private final JCheckBox chkSynchronized = new JCheckBox("Run synchronized (correct result)", true);
    private final JButton btnSimulate = new JButton("Simulate Seat Reservation (90 passengers)");
    private final JButton btnReport = new JButton("Prepare Report");
    private final JLabel lblCounts = new JLabel("Occupied: 0 | Empty: 0");
    private final JLabel lblSelectedPrice = new JLabel("Price: -");

    private final JButton btnSave = new JButton("Save Data");
    private final JButton btnAdmin = new JButton("Admin/Staff");
    private final JButton btnReservations = new JButton("Reservations");
    private final JButton btnTickets = new JButton("My Tickets");

    // Booking
    private final JTextField txtName = new JTextField(8);
    private final JTextField txtSurname = new JTextField(8);
    private final JTextField txtContact = new JTextField(10);
    private final JButton btnBookSelected = new JButton("Book Selected Seat");

    private final SeatPanel seatPanel = new SeatPanel();

    private final JTextArea txtOutput = new JTextArea();
    private JScrollPane outScroll;

    public MainFrame() {
        setTitle("Airline Reservation & Management System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        BackgroundPanel bgPanel = new BackgroundPanel("airplane.jpg");

        bgPanel.setLayout(new BorderLayout(8, 8));

        setContentPane(bgPanel);

        Route route = new Route("ANK", "IST", 450, 1);
        PlaneConfig pc = new PlaneConfig("Boeing-737", 30, 6, 5);
        Plane plane = new Plane("P001", pc);
        activeFlight = new Flight(route, plane, 101, "09.01.2026", "14:30", 60);
        flightManager.addFlight(activeFlight);

        // ---- TOP: search + select flight ----
        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        top.setOpaque(false);
        top.add(new JLabel("Departure:"));
        top.add(txtDeparture);
        top.add(new JLabel("Arrival:"));
        top.add(txtArrival);
        top.add(btnSearch);

        top.add(new JLabel(" | Found:"));
        cmbFlights.setPreferredSize(new Dimension(360, 26));
        top.add(cmbFlights);
        top.add(btnSelectFlight);

        add(top, BorderLayout.NORTH);

        // ---- Controls: 2 row ----
        JPanel controlsContainer = new JPanel();
        controlsContainer.setLayout(new BoxLayout(controlsContainer, BoxLayout.Y_AXIS));
        controlsContainer.setOpaque(false);

        JPanel row1 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        row1.setOpaque(false); // <-- EKLE
        row1.add(chkSynchronized);
        row1.add(btnSimulate);
        row1.add(btnReport);
        row1.add(lblCounts);

        row1.add(Box.createHorizontalStrut(15));

        // Fiyat etiketini belirgin yapalım
        lblSelectedPrice.setForeground(new Color(0, 100, 0));
        lblSelectedPrice.setFont(new Font("SansSerif", Font.BOLD, 14));
        row1.add(lblSelectedPrice);

        row1.add(Box.createHorizontalStrut(15));


        row1.add(btnSave);

        JPanel row2 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        row2.setOpaque(false);
        row2.add(new JLabel("Name:"));
        row2.add(txtName);
        row2.add(new JLabel("Surname:"));
        row2.add(txtSurname);
        row2.add(new JLabel("Contact:"));
        row2.add(txtContact);
        row2.add(btnBookSelected);
        row2.add(btnReservations);
        row2.add(btnTickets);
        row2.add(btnAdmin);

        controlsContainer.add(row1);
        controlsContainer.add(row2);

        // ---- CENTER seat map ----
        JPanel center = new JPanel(new BorderLayout(6, 6));
        center.add(controlsContainer, BorderLayout.NORTH);
        center.setOpaque(false);

        JScrollPane seatScroll = new JScrollPane(
                seatPanel,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED
        );
        seatScroll.setOpaque(false);
        seatScroll.getViewport().setOpaque(false);
        seatScroll.setBorder(null);
        center.add(seatScroll, BorderLayout.CENTER);

        add(center, BorderLayout.CENTER);

        // ---- RIGHT output----
        txtOutput.setEditable(false);
        txtOutput.setLineWrap(true);
        txtOutput.setWrapStyleWord(true);
        txtOutput.setOpaque(false);
        txtOutput.setForeground(Color.BLACK);
        txtOutput.setFont(new Font("Monospaced", Font.BOLD, 12));

        outScroll = new JScrollPane(txtOutput);
        outScroll.setPreferredSize(new Dimension(380, 0));

        outScroll.setOpaque(false);
        outScroll.getViewport().setOpaque(false);
        outScroll.setBorder(null);
        outScroll.setVisible(false);

        add(outScroll, BorderLayout.EAST);

        // ---- Initial seat map ----
        refreshSeatView();

        // ---- Actions ----
        btnSearch.addActionListener(e -> doSearch());
        btnSelectFlight.addActionListener(e -> doSelectFlight());

        btnSimulate.addActionListener(e -> doSimulation());
        btnReport.addActionListener(e -> doReport());
        btnBookSelected.addActionListener(e -> bookSelectedSeat());

        btnSave.addActionListener(e -> doSave());

        btnReservations.addActionListener(e -> new ReservationManagementFrame(reservationManager, activeFlight));
        btnTickets.addActionListener(e -> new TicketFrame());


        btnAdmin.addActionListener(e -> {
            if (!UserManager.isStaff()) {
                JOptionPane.showMessageDialog(this, "Admin ekranı sadece STAFF içindir.");
                return;
            }

            new AdminFrame(flightManager, () -> {
                refillComboFromSearch(txtDeparture.getText().trim(), txtArrival.getText().trim());
            });
        });
        seatPanel.setSelectionListener(seat -> {
            if (seat.isReserveStatus()) {
                lblSelectedPrice.setText("Price: (Occupied)");
                lblSelectedPrice.setForeground(Color.RED);
            } else {
                // seat.getPrice() metodu, bagajsız saf bilet fiyatını döndürür.
                lblSelectedPrice.setText("Price: " + seat.getPrice() + " TL");
                // Eğer Business ise Mor, Economy ise Yeşil yapalım (Görsel güzellik için)
                if (seat.getSeatClass() == Seat.SeatClass.BUSINESS) {
                    lblSelectedPrice.setForeground(new Color(138, 43, 226)); // Mor
                } else {
                    lblSelectedPrice.setForeground(new Color(0, 100, 0)); // Yeşil
                }
            }
        });
        doLoad();
        setVisible(true);

    }

    private void logToOutput(String text) {
        if (outScroll != null && !outScroll.isVisible()) {
            outScroll.setVisible(true);
            revalidate();
            repaint();
        }

        // Yazıyı ekle
        txtOutput.append(text);
    }

    private void doSearch() {
        String dep = txtDeparture.getText().trim();
        String arr = txtArrival.getText().trim();
        refillComboFromSearch(dep, arr);
    }

    private void refillComboFromSearch(String dep, String arr) {
        List<Flight> found = flightManager.searchFlights(dep, arr);

        cmbFlights.removeAllItems();
        for (Flight f : found) cmbFlights.addItem(f);

        logToOutput("SEARCH RESULTS:\n");
        if (found.isEmpty()) {
            logToOutput("No flights found for " + dep + " -> " + arr + "\n\n");
        } else {
            for (Flight f : found) logToOutput(" - " + f + "\n");
            logToOutput("\n");
        }
    }

    private void doSelectFlight() {
        Flight f = (Flight) cmbFlights.getSelectedItem();
        if (f == null) {
            JOptionPane.showMessageDialog(this, "Önce search yapıp listeden flight seç.");
            return;
        }
        activeFlight = f;
        logToOutput("ACTIVE FLIGHT SET: " + activeFlight + "\n\n");
        refreshSeatView();
    }

    private void refreshSeatView() {
        if (activeFlight == null) return;

        seatPanel.setSeatMatrix(activeFlight.getSeatStatus());
        lblSelectedPrice.setText("Price: -");
        lblSelectedPrice.setForeground(Color.BLACK);

        int occ = SeatManager.occupiedCount(activeFlight.getSeatStatus());
        int emp = SeatManager.emptyCount(activeFlight.getSeatStatus());
        lblCounts.setText("Occupied: " + occ + " | Empty: " + emp);
    }

    private void doSimulation() {
        boolean sync = chkSynchronized.isSelected();
        logToOutput("Simulation started. Mode: " + (sync ? "SYNCHRONIZED" : "NOT SYNCHRONIZED") + "\n");
        reservationManager.runSeatSimulation(activeFlight, 90, sync, this);
    }

    private void doReport() {
        Thread reportThread = new Thread(
                new ReportGenerator(flightManager.getAllFlights(), this),
                "ReportGenerator"
        );
        reportThread.start();
    }

    private void bookSelectedSeat() {
        String seatNum = seatPanel.getSelectedSeatNum();
        if (seatNum == null) {
            JOptionPane.showMessageDialog(this, "Önce seat map'ten koltuk seç.");
            return;
        }

        String name = txtName.getText().trim();
        String surname = txtSurname.getText().trim();
        String contact = txtContact.getText().trim();

        if (name.isEmpty() || surname.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Name ve Surname gir.");
            return;
        }

        Seat seat = SeatManager.findSeatByNumber(activeFlight.getSeatStatus(), seatNum);
        if (seat == null) {
            JOptionPane.showMessageDialog(this, "Seat bulunamadı: " + seatNum);
            return;
        }

        try {
            String owner = UserManager.getActiveUser() == null ? "GUEST" : UserManager.getActiveUser().getUsername();

            Passenger p = new Passenger(
                    "CUST-" + System.currentTimeMillis(),
                    name,
                    surname,
                    contact
            );

            String realDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"));
            reservationManager.makeReservation(activeFlight, p, seat, realDate, owner, true);

            logToOutput("BOOKED: " + seatNum + " for " + name + " " + surname + "\n\n");
            refreshSeatView();
            seatPanel.clearSelection();

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Booking failed: " + ex.getMessage());
        }
    }

    private void doSave() {
        fileStore.saveFlights(flightManager.getAllFlights(), "flights.txt");
        fileStore.saveReservations(reservationManager.getAllReservations(), "reservations.txt");
        TicketManager.saveTickets();
        logToOutput("Data saved to /data folder.\n\n");
    }

    private void doLoad() {
        // 1. UÇUŞLARI YÜKLE
        List<Flight> loadedFlights = fileStore.loadFlights("flights.txt");
        // Eğer manager'da yoksa ekle
        for (Flight f : loadedFlights) {
            if (flightManager.getFlightByNum(f.getFlightNum()) == null) {
                flightManager.addFlight(f);
            }
        }

        List<Reservation> loadedRes = fileStore.loadReservations("reservations.txt", flightManager.getAllFlights());

        reservationManager.loadReservations(loadedRes);

        TicketManager.loadTickets(reservationManager.getAllReservations());

        logToOutput("Loaded flights: " + loadedFlights.size() + "\n");
        logToOutput("Loaded reservations: " + reservationManager.getAllReservations().size() + "\n");
        logToOutput("Loaded tickets: " + TicketManager.getAllTickets().size() + "\n\n");

        refreshSeatView();
    }

    class BackgroundPanel extends JPanel {
        private Image backgroundImage;

        public BackgroundPanel(String fileName) {
            try {
                // Resmi dosya yolundan oku
                backgroundImage = javax.imageio.ImageIO.read(new java.io.File(fileName));
            } catch (Exception e) {
                System.err.println("Arka plan resmi bulunamadı: " + fileName);
            }
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (backgroundImage != null) {
                g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
            }
        }
    }

    @Override
    public void onSimulationUpdate(boolean[][] occupiedMap, int occupiedCount, int emptyCount) {
        seatPanel.setSeatMatrix(activeFlight.getSeatStatus());
        lblCounts.setText("Occupied: " + occupiedCount + " | Empty: " + emptyCount);
    }

    @Override
    public void onSimulationFinished(boolean[][] occupiedMap, int occupiedCount, int emptyCount) {
        seatPanel.setSeatMatrix(activeFlight.getSeatStatus());
        lblCounts.setText("Occupied: " + occupiedCount + " | Empty: " + emptyCount);
        logToOutput("Simulation finished. Occupied=" + occupiedCount + " Empty=" + emptyCount + "\n\n");
    }

    // ---- ReportListener ----
    @Override
    public void onReportStatus(String message) {
        logToOutput(message + "\n");
    }

    @Override
    public void onReportReady(String reportText) {
        logToOutput("\n" + reportText + "\n");
    }
}