import javax.swing.SwingUtilities;

public class MainApp {
    public static void main(String[] args) {
        // Ensure driver is loaded
        try { Class.forName("com.mysql.cj.jdbc.Driver"); } catch (Exception ignored) {}
        SwingUtilities.invokeLater(() -> {
            MainFrame f = new MainFrame();
            f.setVisible(true);
        });
    }
}
