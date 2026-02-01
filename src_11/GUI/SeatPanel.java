package GUI;

import FlightManagement.Seat;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.function.Consumer;

public class SeatPanel extends JPanel {

    private Seat[][] seatMatrix;

    private final int cellW = 26;
    private final int cellH = 20;
    private final int gap = 4;
    private final int aisleGap = 20;

    private final int startX = 40;
    private final int startY = 50;

    //BUsiness için
    private final Color PURPLE_COLOR = new Color(138, 43, 226);

    private int selectedRow = -1;
    private int selectedCol = -1;

    private Consumer<Seat> selectionListener;

    public SeatPanel() {
        setOpaque(false);
        // Başlangıçta boş bir matris olmasın diye null check yapacağız
        setPreferredSize(new Dimension(400, 400));

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                // Eğer matrix henüz yüklenmediyse işlem yapma
                if (seatMatrix == null) return;

                int[] rc = findSeatByPoint(e.getX(), e.getY());

                //boşkuğa tıklayonca hata veriyodu, bunu düzelttik
                if (rc != null && rc[0] >= 0 && rc[1] >= 0 &&
                        rc[0] < seatMatrix.length && rc[1] < seatMatrix[0].length) {

                    selectedRow = rc[0];
                    selectedCol = rc[1];
                    repaint();

                    if (selectionListener != null) {
                        Seat selectedSeat = seatMatrix[selectedRow][selectedCol];
                        selectionListener.accept(selectedSeat);
                    }
                }
            }
        });
    }

    public void setSelectionListener(Consumer<Seat> listener) {
        this.selectionListener = listener;
    }

    public void setSeatMatrix(Seat[][] seatMatrix) {
        if (seatMatrix != null) {
            this.seatMatrix = seatMatrix;
            selectedRow = -1;
            selectedCol = -1;
            updatePanelSize();
            revalidate();
            repaint();
        }
    }

    private void updatePanelSize() {
        if (seatMatrix == null) return;
        int rows = seatMatrix.length;
        int cols = (rows > 0) ? seatMatrix[0].length : 0;

        int totalH = startY + (rows * (cellH + gap)) + 90;
        int totalW = startX + (cols * (cellW + gap)) + aisleGap + 90;

        setPreferredSize(new Dimension(totalW, totalH));
    }

    public String getSelectedSeatNum() {
        if (selectedRow < 0 || selectedCol < 0 || seatMatrix == null) return null;
        return seatMatrix[selectedRow][selectedCol].getSeatNum();
    }

    public void clearSelection() {
        selectedRow = -1;
        selectedCol = -1;
        repaint();
    }

    private int[] findSeatByPoint(int x, int y) {
        if (seatMatrix == null) return null;
        for (int r = 0; r < seatMatrix.length; r++) {
            for (int c = 0; c < seatMatrix[r].length; c++) {
                Rectangle rect = seatRect(r, c);
                if (rect.contains(x, y)) return new int[]{r, c};
            }
        }
        return null;
    }

    private Rectangle seatRect(int r, int c) {
        if (seatMatrix == null || seatMatrix.length == 0) return new Rectangle(0,0,0,0);
        int totalCols = seatMatrix[0].length;
        int mid = totalCols / 2;

        int x = startX + c * (cellW + gap);
        if (c >= mid) x += aisleGap;

        int y = startY + r * (cellH + gap);
        return new Rectangle(x, y, cellW, cellH);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Matris boşsa çizim yapma
        if (seatMatrix == null || seatMatrix.length == 0) return;

        int totalCols = seatMatrix[0].length;
        int mid = totalCols / 2;

        // --- Başlık ---
        g.setColor(Color.BLACK);
        g.setFont(new Font("SansSerif", Font.BOLD, 16));
        g.drawString("Front of Plane", startX + 60, 20);

        g.setFont(new Font("SansSerif", Font.BOLD, 14));
        g.setColor(Color.BLACK);

        // --- SÜTUN HARFLERİ---
        for (int c = 0; c < totalCols; c++) {
            String letter = String.valueOf((char)('A' + c));
            int xPos = startX + c * (cellW + gap) + (cellW / 2) - 4;
            if (c >= mid) xPos += aisleGap;
            g.drawString(letter, xPos, startY - 10);
        }

        // --- SATIR VE KOLTUK---
        for (int r = 0; r < seatMatrix.length; r++) {

            String rowNum = String.valueOf(r + 1);

            int xOffset = (r < 9) ? 10 : 5;
            int yPos = startY + r * (cellH + gap) + (cellH / 2) + 5;

            g.drawString(rowNum, xOffset, yPos);

            for (int c = 0; c < seatMatrix[r].length; c++) {
                Rectangle rect = seatRect(r, c);
                Seat seat = seatMatrix[r][c];

                if (seat.isReserveStatus()) g.setColor(Color.RED);
                else if (seat.getSeatClass() == Seat.SeatClass.BUSINESS) g.setColor(PURPLE_COLOR);
                else g.setColor(Color.GREEN);

                g.fillRect(rect.x, rect.y, rect.width, rect.height);

                g.setColor(Color.BLACK);
                g.drawRect(rect.x, rect.y, rect.width, rect.height);

                if (r == selectedRow && c == selectedCol) {
                    g.setColor(Color.BLUE);
                    Graphics2D g2 = (Graphics2D) g;
                    Stroke oldStroke = g2.getStroke();
                    g2.setStroke(new BasicStroke(3));
                    g2.drawRect(rect.x, rect.y, rect.width, rect.height);
                    g2.setStroke(oldStroke);
                }
            }
        }

        g.setFont(new Font("SansSerif", Font.BOLD, 12));

        int legendY = startY + seatMatrix.length * (cellH + gap) + 20;

        g.setColor(Color.GREEN); g.fillRect(startX, legendY, 12, 12);
        g.setColor(Color.BLACK); g.drawString("Eco", startX + 15, legendY + 11);

        g.setColor(PURPLE_COLOR); g.fillRect(startX + 50, legendY, 12, 12);
        g.setColor(Color.BLACK); g.drawString("Biz", startX + 65, legendY + 11);

        g.setColor(Color.RED); g.fillRect(startX + 100, legendY, 12, 12);
        g.setColor(Color.BLACK); g.drawString("Full", startX + 115, legendY + 11);

        g.setColor(Color.BLUE);
        String selectedTxt = "Sel: " + (getSelectedSeatNum() == null ? "-" : getSelectedSeatNum());
        g.drawString(selectedTxt, startX + 160, legendY + 11);
    }
}