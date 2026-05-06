import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;



public class DashboardFrame extends JFrame {

    User currentUser;

    
    InventoryService inventoryService;
    SaleService      saleService;
    DebtService      debtService;

    
    JTextArea lowStockArea;
    JTextArea recentSalesArea;
    JTextArea pendingDebtsArea;

    public DashboardFrame(User user) {

        currentUser      = user;
        inventoryService = new InventoryService();
        saleService      = new SaleService();
        debtService      = new DebtService();

       
        setTitle("Kiryana Shop - Welcome " + user.getUsername());
        setSize(950, 620);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(new Color(30, 30, 30));

        JLabel appName = new JLabel("  Kiryana Management System");
        appName.setForeground(Color.WHITE);
        appName.setFont(new Font("Arial", Font.BOLD, 15));

        JButton logoutBtn = new JButton("Logout");
        logoutBtn.setBackground(new Color(200, 50, 50));
        logoutBtn.setForeground(Color.WHITE);

        topBar.add(appName,   BorderLayout.WEST);
        topBar.add(logoutBtn, BorderLayout.EAST);
        add(topBar, BorderLayout.NORTH);

        
        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(new Font("Arial", Font.PLAIN, 13));

        
        tabs.addTab("Home", buildHomeTab());

        
        tabs.addTab("Sell (POS)",          new SellPanel(this));
        tabs.addTab("Restock / Inventory", new RestockPanel());
        tabs.addTab("Debt / Khata",        new DebtPanel());

        add(tabs, BorderLayout.CENTER);

        
        loadSummary();

        
        logoutBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {

                int choice = JOptionPane.showConfirmDialog(
                    null,
                    "Are you sure you want to logout?",
                    "Logout",
                    JOptionPane.YES_NO_OPTION
                );

                if (choice == JOptionPane.YES_OPTION) {
                    
                    LoginFrame login = new LoginFrame();
                    login.setVisible(true);
                    dispose();
                }
            }
        });
    }

    
    private JPanel buildHomeTab() {

        JPanel homePanel = new JPanel(new BorderLayout());

        JLabel welcomeLabel = new JLabel(
            "Welcome, " + currentUser.getUsername() + "!  Here is your shop summary:",
            SwingConstants.CENTER
        );
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 14));
        homePanel.add(welcomeLabel, BorderLayout.NORTH);

        // 3 columns for the 3 summary sections
        JPanel summaryPanel = new JPanel(new GridLayout(1, 3, 10, 0));
        summaryPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        lowStockArea    = makeSummaryArea();
        recentSalesArea = makeSummaryArea();
        pendingDebtsArea = makeSummaryArea();

        JScrollPane scroll1 = new JScrollPane(lowStockArea);
        JScrollPane scroll2 = new JScrollPane(recentSalesArea);
        JScrollPane scroll3 = new JScrollPane(pendingDebtsArea);

        scroll1.setBorder(BorderFactory.createTitledBorder("Low Stock Alert"));
        scroll2.setBorder(BorderFactory.createTitledBorder("Recent Sales"));
        scroll3.setBorder(BorderFactory.createTitledBorder("Pending Debts"));

        summaryPanel.add(scroll1);
        summaryPanel.add(scroll2);
        summaryPanel.add(scroll3);

        homePanel.add(summaryPanel, BorderLayout.CENTER);

        JButton refreshBtn = new JButton("Refresh Summary");
        JPanel  btnPanel   = new JPanel(new FlowLayout(FlowLayout.CENTER));
        btnPanel.add(refreshBtn);
        homePanel.add(btnPanel, BorderLayout.SOUTH);

        refreshBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                loadSummary();
            }
        });

        return homePanel;
    }

    private JTextArea makeSummaryArea() {
        JTextArea area = new JTextArea();
        area.setEditable(false);
        area.setFont(new Font("Monospaced", Font.PLAIN, 12));
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        return area;
    }

    
    public void loadSummary() {

        ArrayList<Inventory> lowStock = inventoryService.getLowStockItems();
        if (lowStock.isEmpty()) {
            lowStockArea.setText("All stock levels are OK.");
        } else {
            StringBuilder sb = new StringBuilder();
            for (Inventory inv : lowStock) {
                String name = "";
                if (inv.getProduct() != null) {
                    name = inv.getProduct().getProductName();
                }
                sb.append(name);
                sb.append(" : ");
                sb.append(inv.getCurrentQty());
                sb.append(" (min ");
                sb.append(inv.getLowStockThreshold());
                sb.append(")\n");
            }
            lowStockArea.setText(sb.toString());
        }

        ArrayList<Sale> sales = saleService.getRecentSales();
        if (sales.isEmpty()) {
            recentSalesArea.setText("No sales yet.");
        } else {
            StringBuilder sb = new StringBuilder();
            for (Sale s : sales) {
                String customer = s.getCustomerName();
                if (customer == null || customer.isEmpty()) {
                    customer = "Cash";
                }
                sb.append(customer);
                sb.append(" - Rs.");
                sb.append(s.getAmount());
                sb.append(" [");
                sb.append(s.getStatus());
                sb.append("]\n");
            }
            recentSalesArea.setText(sb.toString());
        }

        ArrayList<Debt> debts = debtService.getPendingDebts();
        if (debts.isEmpty()) {
            pendingDebtsArea.setText("No pending debts.");
        } else {
            StringBuilder sb = new StringBuilder();
            for (Debt d : debts) {
                sb.append(d.getCustomerName());
                sb.append(" owes Rs.");
                sb.append(d.getRemaining());
                sb.append("\n");
            }
            pendingDebtsArea.setText(sb.toString());
        }
    }
}