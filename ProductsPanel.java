import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductsPanel extends JPanel {
    private MainFrame parent;
    private String userEmail;
    private JPanel cardsContainer;
    private List<ProductItem> products = new ArrayList<>();
    private List<ProductItem> cart = new ArrayList<>();
    private JLabel cartLabel, totalLabel;

    public ProductsPanel(MainFrame parent) {
        this.parent = parent;
        setLayout(new BorderLayout());
        setBackground(new Color(250, 250, 250));

        // Header
        JPanel header = new JPanel(new BorderLayout());
        header.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
        header.setBackground(Color.WHITE);

        JLabel title = new JLabel("🛍️ Naveen Store — Products");
        title.setFont(new Font("Arial", Font.BOLD, 20));
        header.add(title, BorderLayout.WEST);

        JButton logoutBtn = new JButton("Logout");
        logoutBtn.addActionListener(e -> parent.showAuth());
        header.add(logoutBtn, BorderLayout.EAST);
        add(header, BorderLayout.NORTH);

        // Product cards
        cardsContainer = new JPanel(new WrapLayout(FlowLayout.LEFT, 15, 15));
        cardsContainer.setBackground(new Color(245, 245, 245));
        JScrollPane sp = new JScrollPane(cardsContainer);
        sp.getVerticalScrollBar().setUnitIncrement(12);
        add(sp, BorderLayout.CENTER);

        // Cart summary panel
        JPanel cartPanel = new JPanel(new GridLayout(3,1,5,5));
        cartPanel.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
        cartPanel.setBackground(new Color(255, 255, 255));

        cartLabel = new JLabel("🛒 Cart: 0 items");
        cartLabel.setFont(new Font("Arial", Font.PLAIN, 14));

        totalLabel = new JLabel("Total: ₹0.00");
        totalLabel.setFont(new Font("Arial", Font.BOLD, 16));
        totalLabel.setForeground(new Color(0,120,215));

        JButton buyAll = new JButton("Buy All Items");
        buyAll.setBackground(new Color(0,120,215));
        buyAll.setForeground(Color.WHITE);
        buyAll.addActionListener(e -> doBuyAll());

        cartPanel.add(cartLabel);
        cartPanel.add(totalLabel);
        cartPanel.add(buyAll);

        add(cartPanel, BorderLayout.SOUTH);
    }

    public void setUser(String email) {
        this.userEmail = email;
    }

    public void reloadProducts() {
        cardsContainer.removeAll();
        products.clear();
        try (Connection con = DBHelper.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery("SELECT * FROM products")) {
            while (rs.next()) {
                ProductItem p = new ProductItem(rs.getInt("id"), rs.getString("name"), rs.getDouble("price"));
                products.add(p);
                cardsContainer.add(createCard(p));
            }
        } catch (SQLException ex) {
            cardsContainer.add(new JLabel("❌ Error loading products: " + ex.getMessage()));
        }
        revalidate();
        repaint();
    }

    private JPanel createCard(ProductItem p) {
        JPanel card = new JPanel(new BorderLayout(5,5));
        card.setPreferredSize(new Dimension(220, 150));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createLineBorder(new Color(200,200,200)));

        JLabel name = new JLabel(p.name, SwingConstants.CENTER);
        name.setFont(new Font("Arial", Font.BOLD, 15));
        JLabel price = new JLabel("₹" + p.price, SwingConstants.CENTER);
        price.setForeground(new Color(0,120,215));

        JButton addCart = new JButton("Add to Cart");
        addCart.addActionListener(e -> addToCart(p));

        JButton buyNow = new JButton("Buy Now");
        buyNow.setBackground(new Color(0,120,215));
        buyNow.setForeground(Color.WHITE);
        buyNow.addActionListener(e -> buyNow(p));

        JPanel btnPanel = new JPanel(new GridLayout(1,2,5,5));
        btnPanel.add(addCart);
        btnPanel.add(buyNow);

        card.add(name, BorderLayout.NORTH);
        card.add(price, BorderLayout.CENTER);
        card.add(btnPanel, BorderLayout.SOUTH);

        return card;
    }

    private void addToCart(ProductItem p) {
        cart.add(p);
        updateCartInfo();
    }

    private void updateCartInfo() {
        cartLabel.setText("🛒 Cart: " + cart.size() + " items");
        double total = cart.stream().mapToDouble(pr -> pr.price).sum();
        totalLabel.setText("Total: ₹" + String.format("%.2f", total));
    }

    private void buyNow(ProductItem p) {
        boolean ok = OrderDAO.placeOrder(userEmail, p.name, p.price);
        if (ok)
            JOptionPane.showMessageDialog(this, "✅ Bought " + p.name + " for ₹" + p.price);
        else
            JOptionPane.showMessageDialog(this, "❌ Purchase failed");
    }

    private void doBuyAll() {
        if (cart.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Your cart is empty!");
            return;
        }
        double total = cart.stream().mapToDouble(p -> p.price).sum();
        for (ProductItem p : cart) {
            OrderDAO.placeOrder(userEmail, p.name, p.price);
        }
        JOptionPane.showMessageDialog(this, "🎉 All items purchased successfully!\nTotal: ₹" + total);
        cart.clear();
        updateCartInfo();
    }

    static class ProductItem {
        int id;
        String name;
        double price;
        ProductItem(int id, String name, double price) {
            this.id = id;
            this.name = name;
            this.price = price;
        }
    }
}
