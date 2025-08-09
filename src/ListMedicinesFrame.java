import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;

public class ListMedicinesFrame extends JFrame {
    private MedicineDAO dao = new MedicineDAO();
    private DefaultTableModel model;
    private JTable table;

    public ListMedicinesFrame() {
        setTitle("Manage Medicines");
        setSize(800, 400);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        String[] columns = {"ID", "Name", "Batch No", "Quantity", "Price", "Expiry Date", "Supplier"};
        model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column != 0; // ID column not editable
            }
        };

        table = new JTable(model);
        refreshTable();

        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

        JPanel btnPanel = new JPanel();
        JButton addBtn = new JButton("Add");
        JButton updateBtn = new JButton("Update");
        JButton deleteBtn = new JButton("Delete");
        JButton refreshBtn = new JButton("Refresh");

        btnPanel.add(addBtn);
        btnPanel.add(updateBtn);
        btnPanel.add(deleteBtn);
        btnPanel.add(refreshBtn);

        add(btnPanel, BorderLayout.SOUTH);

        // Button actions
        addBtn.addActionListener(e -> addMedicine());
        updateBtn.addActionListener(e -> updateSelectedMedicine());
        deleteBtn.addActionListener(e -> deleteSelectedMedicine());
        refreshBtn.addActionListener(e -> refreshTable());

        setVisible(true);
    }

    private void refreshTable() {
        try {
            model.setRowCount(0);
            List<Medicine> medicines = dao.getAllMedicines();
            for (Medicine m : medicines) {
                model.addRow(new Object[]{
                        m.getId(), m.getName(), m.getBatchNo(),
                        m.getQuantity(), m.getPrice(), m.getExpiryDate(), m.getSupplier()
                });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error fetching medicines: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void addMedicine() {
        JTextField nameField = new JTextField();
        JTextField batchField = new JTextField();
        JTextField qtyField = new JTextField();
        JTextField priceField = new JTextField();
        JTextField expiryField = new JTextField("YYYY-MM-DD");
        JTextField supplierField = new JTextField();

        Object[] fields = {
                "Name:", nameField,
                "Batch No:", batchField,
                "Quantity:", qtyField,
                "Price:", priceField,
                "Expiry Date:", expiryField,
                "Supplier:", supplierField
        };

        int option = JOptionPane.showConfirmDialog(this, fields, "Add Medicine", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            try {
                Medicine m = new Medicine(
                        nameField.getText(),
                        batchField.getText(),
                        Integer.parseInt(qtyField.getText()),
                        Double.parseDouble(priceField.getText()),
                        LocalDate.parse(expiryField.getText()),
                        supplierField.getText()
                );
                dao.addMedicine(m);
                refreshTable();
                JOptionPane.showMessageDialog(this, "Medicine added successfully!");
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error adding medicine: " + e.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void updateSelectedMedicine() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select a row to update.");
            return;
        }

        try {
            int id = (int) model.getValueAt(row, 0);
            Medicine m = new Medicine();
            m.setId(id);
            m.setName((String) model.getValueAt(row, 1));
            m.setBatchNo((String) model.getValueAt(row, 2));
            m.setQuantity(Integer.parseInt(model.getValueAt(row, 3).toString()));
            m.setPrice(Double.parseDouble(model.getValueAt(row, 4).toString()));
            m.setExpiryDate(LocalDate.parse(model.getValueAt(row, 5).toString()));
            m.setSupplier((String) model.getValueAt(row, 6));

            dao.updateMedicine(m);
            JOptionPane.showMessageDialog(this, "Medicine updated successfully!");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error updating medicine: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteSelectedMedicine() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select a row to delete.");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this medicine?",
                "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;

        try {
            int id = (int) model.getValueAt(row, 0);
            dao.deleteMedicine(id);
            refreshTable();
            JOptionPane.showMessageDialog(this, "Medicine deleted successfully!");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error deleting medicine: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
