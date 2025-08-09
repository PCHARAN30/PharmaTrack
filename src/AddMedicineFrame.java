import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;

public class AddMedicineFrame extends JFrame {
    private MedicineDAO dao = new MedicineDAO();

    public AddMedicineFrame() {
        setTitle("Add Medicine");
        setSize(400, 300);
        setLocationRelativeTo(null);
        setLayout(new GridLayout(7, 2, 5, 5));

        JTextField nameField = new JTextField();
        JTextField batchField = new JTextField();
        JTextField qtyField = new JTextField();
        JTextField priceField = new JTextField();
        JTextField expiryField = new JTextField("YYYY-MM-DD");
        JTextField supplierField = new JTextField();
        JButton saveBtn = new JButton("Save");

        add(new JLabel("Name:"));
        add(nameField);
        add(new JLabel("Batch No:"));
        add(batchField);
        add(new JLabel("Quantity:"));
        add(qtyField);
        add(new JLabel("Price:"));
        add(priceField);
        add(new JLabel("Expiry Date:"));
        add(expiryField);
        add(new JLabel("Supplier:"));
        add(supplierField);
        add(new JLabel());
        add(saveBtn);

        saveBtn.addActionListener(e -> {
            try {
                String name = nameField.getText();
                String batch = batchField.getText();
                int qty = Integer.parseInt(qtyField.getText());
                double price = Double.parseDouble(priceField.getText());
                LocalDate expiry = LocalDate.parse(expiryField.getText());
                String supplier = supplierField.getText();

                Medicine m = new Medicine(name, batch, qty, price, expiry, supplier);
                dao.addMedicine(m);
                JOptionPane.showMessageDialog(this, "Medicine added successfully!");
                dispose();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        setVisible(true);
    }
}
