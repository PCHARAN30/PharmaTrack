import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.FileWriter;
import java.time.LocalDate;
import java.util.List;

public class DashboardFrame extends JFrame {
    private MedicineDAO dao = new MedicineDAO();
    private DefaultTableModel model;
    private JTable table;
    private JTextField searchField;
    private JComboBox<String> supplierFilter;
    private JLabel statusLabel;

    public DashboardFrame() {
        setTitle("PharmaTrack - Dashboard");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(950, 550);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // ==== Table ====
        String[] columns = {"ID", "Name", "Batch No", "Quantity", "Price", "Expiry Date", "Supplier"};
        model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column != 0; // ID column not editable
            }
        };

        table = new JTable(model) {
            @Override
            public Component prepareRenderer(javax.swing.table.TableCellRenderer renderer, int row, int column) {
                Component c = super.prepareRenderer(renderer, row, column);

                if (isRowSelected(row)) {
                    c.setBackground(new Color(184, 207, 229));
                    return c;
                }

                try {
                    Object val = getValueAt(row, 5);
                    if (val != null) {
                        LocalDate expiry = LocalDate.parse(val.toString());
                        LocalDate today = LocalDate.now();

                        if (expiry.isBefore(today)) {
                            c.setBackground(new Color(255, 102, 102)); // Light Red
                        } else if (!expiry.isBefore(today) && expiry.isBefore(today.plusDays(7))) {
                            c.setBackground(new Color(255, 255, 153)); // Light Yellow
                        } else {
                            c.setBackground(Color.WHITE);
                        }
                    } else {
                        c.setBackground(Color.WHITE);
                    }
                } catch (Exception e) {
                    c.setBackground(Color.WHITE);
                }
                return c;
            }
        };

        add(new JScrollPane(table), BorderLayout.CENTER);

        // ==== Top Buttons ====
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton addBtn = new JButton("Add");
        JButton updateBtn = new JButton("Update");
        JButton deleteBtn = new JButton("Delete");
        JButton exportBtn = new JButton("Export Expiring (CSV)");
        JButton refreshBtn = new JButton("Refresh");
        JButton exitBtn = new JButton("Exit");

        btnPanel.add(addBtn);
        btnPanel.add(updateBtn);
        btnPanel.add(deleteBtn);
        btnPanel.add(exportBtn);
        btnPanel.add(refreshBtn);
        btnPanel.add(exitBtn);

        add(btnPanel, BorderLayout.NORTH);

        // ==== Search + Filter ====
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel searchLabel = new JLabel("Search:");
        searchField = new JTextField(20);

        JLabel filterLabel = new JLabel("Filter by Supplier:");
        supplierFilter = new JComboBox<>();
        supplierFilter.addItem("All");

        try {
            List<Medicine> allMeds = dao.getAllMedicines();
            allMeds.stream()
                    .map(Medicine::getSupplier)
                    .distinct()
                    .filter(s -> s != null && !s.isBlank())
                    .forEach(supplierFilter::addItem);
        } catch (Exception e) {
            e.printStackTrace();
        }

        searchPanel.add(searchLabel);
        searchPanel.add(searchField);
        searchPanel.add(filterLabel);
        searchPanel.add(supplierFilter);

        // ==== Status Bar ====
        statusLabel = new JLabel("Loading status...");
        statusLabel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.add(searchPanel, BorderLayout.NORTH);
        bottomPanel.add(statusLabel, BorderLayout.SOUTH);

        add(bottomPanel, BorderLayout.SOUTH);

        // ==== Button Actions ====
        addBtn.addActionListener(e -> addMedicine());
        updateBtn.addActionListener(e -> updateSelectedMedicine());
        deleteBtn.addActionListener(e -> deleteSelectedMedicine());
        exportBtn.addActionListener(e -> exportExpiringMedicinesCSV());
        refreshBtn.addActionListener(e -> refreshTable());
        exitBtn.addActionListener(e -> {
            dispose();
            System.exit(0);
        });

        // ==== Search + Filter Actions ====
        searchField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { filterTable(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { filterTable(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { filterTable(); }
        });

        supplierFilter.addActionListener(e -> filterTable());

        // ==== Expiry Alert ====
        checkExpiryOnStartup();

        // ==== Refresh AFTER statusLabel exists ====
        refreshTable();

        setVisible(true);
    }

    // Refresh table from DB
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
            updateStatusBar();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error fetching medicines: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Add medicine popup
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
                        nameField.getText().trim(),
                        batchField.getText().trim(),
                        Integer.parseInt(qtyField.getText().trim()),
                        Double.parseDouble(priceField.getText().trim()),
                        LocalDate.parse(expiryField.getText().trim()),
                        supplierField.getText().trim()
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

    // Update selected row
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
            Object expiryObj = model.getValueAt(row, 5);
            if (expiryObj != null && !expiryObj.toString().isBlank()) {
                m.setExpiryDate(LocalDate.parse(expiryObj.toString()));
            } else {
                m.setExpiryDate(null);
            }
            m.setSupplier((String) model.getValueAt(row, 6));

            dao.updateMedicine(m);
            refreshTable();
            JOptionPane.showMessageDialog(this, "Medicine updated successfully!");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error updating medicine: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Delete selected row
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

    // Export expiring medicines to CSV
    private void exportExpiringMedicinesCSV() {
        try {
            String daysStr = JOptionPane.showInputDialog(this, "Enter days ahead to export:", "7");
            int daysAhead = Integer.parseInt(daysStr);

            List<Medicine> expiring = dao.getExpiringWithinDays(daysAhead);
            if (expiring.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No medicines expiring within " + daysAhead + " days.",
                        "Export Result", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Save Expiring Medicines CSV");
            fileChooser.setSelectedFile(new java.io.File("expiring_medicines_" + LocalDate.now() + ".csv"));

            int userSelection = fileChooser.showSaveDialog(this);
            if (userSelection != JFileChooser.APPROVE_OPTION) return;

            java.io.File fileToSave = fileChooser.getSelectedFile();

            try (FileWriter writer = new FileWriter(fileToSave)) {
                writer.write("ID,Name,Batch No,Quantity,Price,Expiry Date,Supplier\n");
                for (Medicine m : expiring) {
                    writer.write(String.format("%d,%s,%s,%d,%.2f,%s,%s\n",
                            m.getId(), escapeCsv(m.getName()), escapeCsv(m.getBatchNo()),
                            m.getQuantity(), m.getPrice(),
                            m.getExpiryDate() == null ? "" : m.getExpiryDate().toString(),
                            escapeCsv(m.getSupplier())));
                }
            }

            JOptionPane.showMessageDialog(this, "CSV exported successfully:\n" + fileToSave.getAbsolutePath(),
                    "Export Successful", JOptionPane.INFORMATION_MESSAGE);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Invalid number entered!", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error exporting CSV: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private String escapeCsv(String s) {
        if (s == null) return "";
        if (s.contains(",") || s.contains("\"") || s.contains("\n")) {
            return "\"" + s.replace("\"", "\"\"") + "\"";
        } else {
            return s;
        }
    }

    // Expiry alert when dashboard opens
    private void checkExpiryOnStartup() {
        try {
            int daysAhead = 7;
            List<Medicine> expiring = dao.getExpiringWithinDays(daysAhead);

            if (!expiring.isEmpty()) {
                StringBuilder msg = new StringBuilder("⚠ Medicines expiring within " + daysAhead + " days:\n\n");
                for (Medicine m : expiring) {
                    msg.append(String.format("%s (Expiry: %s, Qty: %d)\n",
                            m.getName(), m.getExpiryDate(), m.getQuantity()));
                }
                JOptionPane.showMessageDialog(this, msg.toString(), "Expiry Alert", JOptionPane.WARNING_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error checking expiry: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Filter by search + supplier
    private void filterTable() {
        String searchText = searchField.getText().trim().toLowerCase();
        String selectedSupplier = (String) supplierFilter.getSelectedItem();

        model.setRowCount(0);
        try {
            List<Medicine> medicines = dao.getAllMedicines();
            for (Medicine m : medicines) {
                boolean matchesSearch = m.getName() != null && m.getName().toLowerCase().contains(searchText);
                boolean matchesSupplier = selectedSupplier == null || selectedSupplier.equals("All") ||
                        (m.getSupplier() != null && m.getSupplier().equals(selectedSupplier));

                if (matchesSearch && matchesSupplier) {
                    model.addRow(new Object[]{
                            m.getId(), m.getName(), m.getBatchNo(),
                            m.getQuantity(), m.getPrice(), m.getExpiryDate(), m.getSupplier()
                    });
                }
            }
            updateStatusBar();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // update status bar counts
    private void updateStatusBar() {
        try {
            List<Medicine> medicines = dao.getAllMedicines();
            int total = medicines.size();
            int expiringSoon = 0;
            int expired = 0;

            LocalDate today = LocalDate.now();

            for (Medicine m : medicines) {
                if (m.getExpiryDate() == null) continue;
                if (m.getExpiryDate().isBefore(today)) {
                    expired++;
                } else if (!m.getExpiryDate().isBefore(today) && m.getExpiryDate().isBefore(today.plusDays(7))) {
                    expiringSoon++;
                }
            }

            statusLabel.setText(
                    String.format("Total Medicines: %d | Expiring Soon: %d | Expired: %d",
                            total, expiringSoon, expired)
            );
        } catch (Exception e) {
            statusLabel.setText("Error fetching status");
        }
    }
}
