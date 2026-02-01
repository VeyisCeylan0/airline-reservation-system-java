package GUI;

import FlightManagement.Flight;
import FlightManagement.Plane;
import FlightManagement.PlaneConfig;
import FlightManagement.Route;
import ServicesAndManagers.FlightManager;
import ServicesAndManagers.SeatManager;

import javax.swing.*;
import java.awt.*;

public class AdminFrame extends JFrame {

    private final FlightManager flightManager;
    private final Runnable onChange;

    // --- PANEL 1: CONFIG CREATION ---
    private final JTextField txtModelName = new JTextField(10);
    private final JTextField txtModelRows = new JTextField(3);
    private final JTextField txtModelCols = new JTextField(3);
    private final JTextField txtModelBiz = new JTextField(3);
    private final JButton btnSaveModel = new JButton("Save New Model");

    // --- PANEL 2: FLIGHT CREATION ---
    private final JComboBox<String> cmbPlaneModels = new JComboBox<>();

    private final JTextField txtFlightNum = new JTextField(8);
    private final JTextField txtDep = new JTextField(5);
    private final JTextField txtArr = new JTextField(5);
    private final JTextField txtDistance = new JTextField(5);
    private final JTextField txtBasePrice = new JTextField(5);
    private final JTextField txtPlaneID = new JTextField(6);

    private final JTextField txtDate = new JTextField(8);
    private final JTextField txtHour = new JTextField(5);
    private final JTextField txtDuration = new JTextField(4);

    private final JButton btnAddFlight = new JButton("Add Flight");
    private final JButton btnDeleteFlight = new JButton("Delete Flight (by Num)");

    public AdminFrame(FlightManager flightManager, Runnable onChange) {
        this.flightManager = flightManager;
        this.onChange = onChange;

        setTitle("Admin Panel");
        setSize(500, 650);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        // --- TAB 1 ---
        JPanel pnlModel = new JPanel(new GridLayout(0, 2, 5, 5));
        pnlModel.setBorder(BorderFactory.createTitledBorder("1. Define New Plane Model"));
        pnlModel.add(new JLabel("Model Name:")); pnlModel.add(txtModelName);
        pnlModel.add(new JLabel("Rows:")); pnlModel.add(txtModelRows);
        pnlModel.add(new JLabel("Cols:")); pnlModel.add(txtModelCols);
        pnlModel.add(new JLabel("Biz Rows:")); pnlModel.add(txtModelBiz);
        pnlModel.add(new JLabel("")); pnlModel.add(btnSaveModel);

        // --- TAB 2 ---
        JPanel pnlFlight = new JPanel(new GridLayout(0, 2, 5, 5));
        pnlFlight.setBorder(BorderFactory.createTitledBorder("2. Add Flight"));
        pnlFlight.add(new JLabel("Select Model:")); pnlFlight.add(cmbPlaneModels);
        pnlFlight.add(new JLabel("Plane Tail ID:")); pnlFlight.add(txtPlaneID);
        pnlFlight.add(new JLabel("Flight Num:")); pnlFlight.add(txtFlightNum);
        pnlFlight.add(new JLabel("Departure:")); pnlFlight.add(txtDep);
        pnlFlight.add(new JLabel("Arrival:")); pnlFlight.add(txtArr);
        pnlFlight.add(new JLabel("Distance:")); pnlFlight.add(txtDistance);
        pnlFlight.add(new JLabel("Base Price:")); pnlFlight.add(txtBasePrice);
        pnlFlight.add(new JLabel("Date:")); pnlFlight.add(txtDate);
        pnlFlight.add(new JLabel("Hour:")); pnlFlight.add(txtHour);
        pnlFlight.add(new JLabel("Duration:")); pnlFlight.add(txtDuration);

        JPanel pnlButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        pnlButtons.add(btnDeleteFlight);
        pnlButtons.add(btnAddFlight);

        JPanel container = new JPanel();
        container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
        container.add(pnlModel);
        container.add(Box.createVerticalStrut(20));
        container.add(pnlFlight);

        add(container, BorderLayout.CENTER);
        add(pnlButtons, BorderLayout.SOUTH);

        btnSaveModel.addActionListener(e -> doSaveModel());
        btnAddFlight.addActionListener(e -> doAddFlight());
        btnDeleteFlight.addActionListener(e -> doDeleteFlight());

        refreshModelCombo();
        setVisible(true);
    }

    private void refreshModelCombo() {
        cmbPlaneModels.removeAllItems();
        for (String name : SeatManager.getModelNames()) {
            cmbPlaneModels.addItem(name);
        }
    }

    private void doSaveModel() {
        try {
            String name = txtModelName.getText().trim();
            int r = Integer.parseInt(txtModelRows.getText().trim());
            int c = Integer.parseInt(txtModelCols.getText().trim());
            int b = Integer.parseInt(txtModelBiz.getText().trim());

            if(name.isEmpty()) throw new Exception("Name empty");

            PlaneConfig pc = new PlaneConfig(name, r, c, b);

            SeatManager.defineModel(pc);

            JOptionPane.showMessageDialog(this, "Model saved: " + name);
            refreshModelCombo();

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

    private void doAddFlight() {
        try {
            String selectedModelName = (String) cmbPlaneModels.getSelectedItem();
            if (selectedModelName == null) return;

            PlaneConfig selectedConfig = SeatManager.getModel(selectedModelName);

            int fNum = Integer.parseInt(txtFlightNum.getText().trim());
            String dep = txtDep.getText().trim();
            String arr = txtArr.getText().trim();
            int dist = Integer.parseInt(txtDistance.getText().trim());
            int base = Integer.parseInt(txtBasePrice.getText().trim());
            String pid = txtPlaneID.getText().trim();
            String date = txtDate.getText().trim();
            String hour = txtHour.getText().trim();
            int dur = Integer.parseInt(txtDuration.getText().trim());

            Route route = new Route(arr, dep, dist, base);
            Plane plane = new Plane(pid, selectedConfig);
            Flight flight = new Flight(route, plane, fNum, date, hour, dur);

            flightManager.addFlight(flight);
            JOptionPane.showMessageDialog(this, "Flight added.");
            if (onChange != null) onChange.run();

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

    private void doDeleteFlight() {
        try {
            int flightNum = Integer.parseInt(txtFlightNum.getText().trim());
            boolean ok = flightManager.deleteFlight(flightNum);
            JOptionPane.showMessageDialog(this, ok ? "Deleted." : "Not found.");
            if (ok && onChange != null) onChange.run();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }
}