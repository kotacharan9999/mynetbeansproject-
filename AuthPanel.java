import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class AuthPanel extends JPanel {
    private MainFrame parent;
    private JTextField regName, regEmail;
    private JPasswordField regPass;
    private JTextField loginEmail;
    private JPasswordField loginPass;

    public AuthPanel(MainFrame parent) {
        this.parent = parent;
        setLayout(new BorderLayout());
        setBackground(new Color(245, 247, 250));

        // left decorative panel
        JPanel left = new JPanel();
        left.setBackground(new Color(30, 144, 255));
        left.setPreferredSize(new Dimension(320, 0));
        left.setLayout(new GridBagLayout());
        JLabel brand = new JLabel("<html><h1 style='color:white'>NaveenShop</h1><p style='color:white'>Mini E-Commerce</p></html>");
        brand.setForeground(Color.WHITE);
        left.add(brand);

        // right card (auth)
        JPanel right = new JPanel();
        right.setBackground(Color.WHITE);
        right.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel regTitle = new JLabel("Register");
        regTitle.setFont(regTitle.getFont().deriveFont(Font.BOLD, 18f));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        right.add(regTitle, gbc);

        gbc.gridwidth = 1;
        gbc.gridy++;
        right.add(new JLabel("Name:"), gbc);
        regName = new JTextField(18); gbc.gridx = 1; right.add(regName, gbc);

        gbc.gridx = 0; gbc.gridy++;
        right.add(new JLabel("Email:"), gbc);
        regEmail = new JTextField(18); gbc.gridx = 1; right.add(regEmail, gbc);

        gbc.gridx = 0; gbc.gridy++;
        right.add(new JLabel("Password:"), gbc);
        regPass = new JPasswordField(18); gbc.gridx = 1; right.add(regPass, gbc);

        gbc.gridx = 0; gbc.gridy++;
        JButton btnRegister = new JButton("Create Account");
        gbc.gridwidth = 2;
        right.add(btnRegister, gbc);

        // separator
        gbc.gridy++; right.add(new JSeparator(), gbc);

        // login
        gbc.gridy++; gbc.gridwidth = 2;
        JLabel loginTitle = new JLabel("Login");
        loginTitle.setFont(loginTitle.getFont().deriveFont(Font.BOLD, 18f));
        right.add(loginTitle, gbc);

        gbc.gridwidth = 1;
        gbc.gridy++; gbc.gridx = 0;
        right.add(new JLabel("Email:"), gbc);
        loginEmail = new JTextField(18); gbc.gridx = 1; right.add(loginEmail, gbc);

        gbc.gridx = 0; gbc.gridy++;
        right.add(new JLabel("Password:"), gbc);
        loginPass = new JPasswordField(18); gbc.gridx = 1; right.add(loginPass, gbc);

        gbc.gridx = 0; gbc.gridy++;
        JButton btnLogin = new JButton("Login");
        gbc.gridwidth = 2;
        right.add(btnLogin, gbc);

        // action listeners
        btnRegister.addActionListener(e -> doRegister());
        btnLogin.addActionListener(e -> doLogin());

        add(left, BorderLayout.WEST);
        add(right, BorderLayout.CENTER);
    }

    private void doRegister() {
        String name = regName.getText().trim();
        String email = regEmail.getText().trim();
        String pass = new String(regPass.getPassword()).trim();
        if (name.isEmpty() || email.isEmpty() || pass.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Fill all fields");
            return;
        }
        try (Connection con = DBHelper.getConnection();
             PreparedStatement ps = con.prepareStatement("INSERT INTO users(name,email,password) VALUES(?,?,?)")) {
            ps.setString(1, name); ps.setString(2, email); ps.setString(3, pass);
            ps.executeUpdate();
            JOptionPane.showMessageDialog(this, "Registered. Please login.");
            regName.setText(""); regEmail.setText(""); regPass.setText("");
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Registration failed: " + ex.getMessage());
        }
    }

    private void doLogin() {
        String email = loginEmail.getText().trim();
        String pass = new String(loginPass.getPassword()).trim();
        if (email.isEmpty() || pass.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Enter email and password");
            return;
        }
        try (Connection con = DBHelper.getConnection();
             PreparedStatement ps = con.prepareStatement("SELECT * FROM users WHERE email=? AND password=?")) {
            ps.setString(1, email); ps.setString(2, pass);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    JOptionPane.showMessageDialog(this, "Welcome, " + rs.getString("name"));
                    parent.showProducts(email);
                    // clear login fields
                    loginEmail.setText(""); loginPass.setText("");
                } else {
                    JOptionPane.showMessageDialog(this, "Invalid credentials");
                }
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Login error: " + ex.getMessage());
        }
    }
}
