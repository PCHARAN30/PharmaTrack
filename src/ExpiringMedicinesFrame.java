import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class ExpiringMedicinesFrame extends JFrame {
    private MedicineDAO dao = new MedicineDAO();

    public ExpiringMedicinesFrame(int daysAhead) {
        setTitle("Expiring Medicines (Next " + daysAhead + " Days)");
        setSize(600, 400);
        setLocationRelativeTo(null);

        String[] columns = {"ID", "Name", "Batch No", "Quantity", "Price", "Expiry Date", "Supplier"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);

        try {
            List<Medicine> medicines = dao.getExpiringWithinDays(daysAhead);
            for (Medicine m : medicines) {
                model.addRow(new Object[]{
                        m.getId(), m.getName(), m.getBatchNo(),
                        m.getQuantity(), m.getPrice(), m.getExpiryDate(), m.getSupplier()
                });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error fetching expiring medicines: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }

        JTable table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

        setVisible(true);
    }
}
