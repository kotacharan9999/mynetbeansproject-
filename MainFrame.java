import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {
    CardLayout cards = new CardLayout();
    JPanel root = new JPanel(cards);

    AuthPanel authPanel;
    ProductsPanel productsPanel;

    public MainFrame() {
        setTitle("NaveenEStore");
        setSize(920, 640);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        authPanel = new AuthPanel(this);
        productsPanel = new ProductsPanel(this);

        root.add(authPanel, "AUTH");
        root.add(productsPanel, "PRODUCTS");

        add(root, BorderLayout.CENTER);

        // show auth first
        showAuth();
    }

    public void showAuth() {
        cards.show(root, "AUTH");
    }

    public void showProducts(String userEmail) {
        productsPanel.setUser(userEmail);
        productsPanel.reloadProducts();
        cards.show(root, "PRODUCTS");
    }
}
