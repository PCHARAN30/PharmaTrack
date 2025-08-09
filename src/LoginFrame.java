import javax.swing.*;
import java.awt.*;

public class LoginFrame extends JFrame {
    public LoginFrame() {
        setTitle("PharmaTrack - Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 300);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        // ==== Welcome Heading ====
        JPanel welcomePanel = new JPanel(new GridLayout(2, 1));
        JLabel titleLabel = new JLabel("Welcome to PharmaTrack", SwingConstants.CENTER);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 20));
        titleLabel.setForeground(new Color(0, 102, 204)); // Blue text

        JLabel taglineLabel = new JLabel(
                "Smart Medicine Management & Expiry Alerts",
                SwingConstants.CENTER
        );
        taglineLabel.setFont(new Font("SansSerif", Font.ITALIC, 12));
        taglineLabel.setForeground(Color.DARK_GRAY);

        welcomePanel.add(titleLabel);
        welcomePanel.add(taglineLabel);

        // ==== Login Form ====
        JPanel formPanel = new JPanel(new GridLayout(3, 2, 8, 8));
        JLabel userLabel = new JLabel("Username:");
        JTextField userField = new JTextField();
        JLabel passLabel = new JLabel("Password:");
        JPasswordField passField = new JPasswordField();
        JButton loginBtn = new JButton("Login");

        formPanel.add(userLabel);
        formPanel.add(userField);
        formPanel.add(passLabel);
        formPanel.add(passField);
        formPanel.add(new JLabel(""));
        formPanel.add(loginBtn);

        // ==== Padding ====
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        centerPanel.add(formPanel, BorderLayout.CENTER);

        // ==== Add panels to frame ====
        add(welcomePanel, BorderLayout.NORTH);
        add(centerPanel, BorderLayout.CENTER);

        // ==== Login Button Action ====
        loginBtn.addActionListener(e -> {
            String username = userField.getText().trim();
            String password = new String(passField.getPassword());

            if (username.equals("admin") && password.equals("admin123")) {
                dispose();
                new DashboardFrame();
            } else {
                JOptionPane.showMessageDialog(this, "Invalid credentials!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        setVisible(true);
    }
}
