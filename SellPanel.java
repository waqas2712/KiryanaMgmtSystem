

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;



public class SellPanel extends JPanel {

    DashboardFrame dashboard;

    SaleService      saleService;
    InventoryService inventoryService;

    Receipt receipt;

    JTextField         searchField;
    JComboBox<Product> productDropdown;
    ArrayList<Product> searchResults;

    JRadioButton radioByPiece;
    JRadioButton radioByWeight;
    JRadioButton radioByAmount;

    JTextField inputField;
    JLabel     inputLabel; 

    JTable            receiptTable;
    DefaultTableModel tableModel;

    JLabel totalLabel;

    public SellPanel(DashboardFrame dashboard) {

        this.dashboard        = dashboard;
        this.saleService      = new SaleService();
        this.inventoryService = new InventoryService();
        this.receipt          = new Receipt();
        this.searchResults    = new ArrayList<>();

        setLayout(new BorderLayout(5, 5));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        add(buildLeftPanel(),  BorderLayout.WEST);
        add(buildRightPanel(), BorderLayout.CENTER);
    }

    
    private JPanel buildLeftPanel() {

        JPanel left = new JPanel(new GridLayout(10, 1, 5, 5));
        left.setBorder(BorderFactory.createTitledBorder("Add Item"));
        left.setPreferredSize(new Dimension(270, 0));

        left.add(new JLabel("Search Product:"));
        searchField = new JTextField();
        left.add(searchField);

        JButton searchBtn = new JButton("Search");
        searchBtn.setBackground(new Color(70, 130, 180));
        searchBtn.setForeground(Color.WHITE);
        left.add(searchBtn);

        left.add(new JLabel("Select Product:"));
        productDropdown = new JComboBox<>();
        left.add(productDropdown);

        left.add(new JLabel("Sell Mode:"));
        JPanel radioPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        radioByPiece  = new JRadioButton("Pieces", true); // selected by default
        radioByWeight = new JRadioButton("Weight");
        radioByAmount = new JRadioButton("By Rs.");

        ButtonGroup group = new ButtonGroup();
        group.add(radioByPiece);
        group.add(radioByWeight);
        group.add(radioByAmount);

        radioPanel.add(radioByPiece);
        radioPanel.add(radioByWeight);
        radioPanel.add(radioByAmount);
        left.add(radioPanel);

        inputLabel = new JLabel("Enter Quantity:");
        left.add(inputLabel);
        inputField = new JTextField();
        left.add(inputField);

        JButton addBtn = new JButton("+ Add to Receipt");
        addBtn.setBackground(new Color(76, 175, 80));
        addBtn.setForeground(Color.WHITE);
        addBtn.setFont(new Font("Arial", Font.BOLD, 13));
        left.add(addBtn);

        searchBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                handleSearch();
            }
        });

        radioByPiece.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                inputLabel.setText("Enter Quantity:");
            }
        });
        radioByWeight.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                inputLabel.setText("Enter Weight (kg/L):");
            }
        });
        radioByAmount.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                inputLabel.setText("Enter Amount (Rs.):");
            }
        });

        // Add to receipt button click
        addBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                handleAddToReceipt();
            }
        });

        return left;
    }

   
    private JPanel buildRightPanel() {

        JPanel right = new JPanel(new BorderLayout(5, 5));
        right.setBorder(BorderFactory.createTitledBorder("Receipt"));

        String[] columns = {"Product", "Quantity", "Subtotal (Rs.)"};

        tableModel = new DefaultTableModel(columns, 0) {
            // This stops the user from editing cells directly in the table
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };

        receiptTable = new JTable(tableModel);
        receiptTable.setFont(new Font("Arial", Font.PLAIN, 13));
        receiptTable.setRowHeight(25);
        receiptTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 13));

        JScrollPane tableScroll = new JScrollPane(receiptTable);
        right.add(tableScroll, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new BorderLayout());

        totalLabel = new JLabel("Grand Total: Rs. 0.00");
        totalLabel.setFont(new Font("Arial", Font.BOLD, 15));
        bottomPanel.add(totalLabel, BorderLayout.WEST);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        JButton removeBtn = new JButton("Remove Selected");
        JButton clearBtn  = new JButton("Clear All");
        JButton cashBtn   = new JButton("Cash Sale");
        JButton creditBtn = new JButton("Credit (Khata)");

        cashBtn.setBackground(new Color(33, 150, 243));
        cashBtn.setForeground(Color.WHITE);
        creditBtn.setBackground(new Color(255, 152, 0));
        creditBtn.setForeground(Color.WHITE);

        btnPanel.add(removeBtn);
        btnPanel.add(clearBtn);
        btnPanel.add(cashBtn);
        btnPanel.add(creditBtn);

        bottomPanel.add(btnPanel, BorderLayout.EAST);
        right.add(bottomPanel, BorderLayout.SOUTH);


        removeBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                handleRemoveItem();
            }
        });

        clearBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                handleClearAll();
            }
        });

        cashBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                handleCheckout(false); // false = cash sale
            }
        });

        creditBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                handleCheckout(true); // true = credit sale
            }
        });

        return right;
    }

    
    private void handleSearch() {

        String keyword = searchField.getText().trim();
        searchResults  = inventoryService.searchProducts(keyword);

        productDropdown.removeAllItems(); // clear old dropdown items

        if (searchResults.isEmpty()) {
            JOptionPane.showMessageDialog(null, "No products found.");
        } else {
            for (Product p : searchResults) {
                productDropdown.addItem(p); // Product.toString() shows in dropdown
            }
        }
    }

   
    private void handleAddToReceipt() {

        Product selected = (Product) productDropdown.getSelectedItem();

        if (selected == null) {
            JOptionPane.showMessageDialog(null, "Please search and select a product first.");
            return;
        }

        // Step 2: parse the input value
        String inputText = inputField.getText().trim();
        if (inputText.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Please enter a quantity or amount.");
            return;
        }

        double inputValue;
        try {
            inputValue = Double.parseDouble(inputText);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(null, "Please enter a valid number.");
            return;
        }

        if (inputValue <= 0) {
            JOptionPane.showMessageDialog(null, "Value must be greater than zero.");
            return;
        }

        double qty;
        double subtotal;
        double price = selected.getPricePerUnit();

        if (radioByAmount.isSelected()) {
            qty     = saleService.calculateQtyFromAmount(inputValue, price);
            subtotal = inputValue; 
        } else {
            qty      = inputValue;
            subtotal = saleService.calculateSubTotal(qty, price);
        }

        Inventory inv = inventoryService.getInventoryForProduct(selected.getProductId());

        if (inv == null || inv.getCurrentQty() < qty) {
            double available = 0;
            if (inv != null) available = inv.getCurrentQty();
            JOptionPane.showMessageDialog(null,
                "Not enough stock! Available: " + available + " " + selected.getUnitType()
            );
            return;
        }

        SaleItem item = new SaleItem(0, selected, qty, subtotal);
        receipt.addItem(item);

        // Step 6: add a row to the JTable
        tableModel.addRow(new Object[]{
            selected.getProductName(),
            qty + " " + selected.getUnitType(),
            subtotal
        });

        totalLabel.setText("Grand Total: Rs. " + receipt.getGrandTotal());

        inputField.setText("");
    }

    private void handleRemoveItem() {

        int selectedRow = receiptTable.getSelectedRow();

        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(null, "Please click a row to select it first.");
            return;
        }

        receipt.removeItem(selectedRow);
        tableModel.removeRow(selectedRow);
        totalLabel.setText("Grand Total: Rs. " + receipt.getGrandTotal());
    }

    private void handleClearAll() {
        receipt.clear();
        tableModel.setRowCount(0); 
        totalLabel.setText("Grand Total: Rs. 0.00");
    }
    private void handleCheckout(boolean isCredit) {

        if (receipt.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Receipt is empty. Add items first.");
            return;
        }

        boolean success;

        if (isCredit) {
            String customerName = JOptionPane.showInputDialog(
                null,
                "Enter customer name for khata:",
                "Credit Sale",
                JOptionPane.QUESTION_MESSAGE
            );

            if (customerName == null || customerName.trim().isEmpty()) {
                JOptionPane.showMessageDialog(null, "Customer name is required for credit sale.");
                return;
            }

            success = saleService.processCreditSale(receipt, customerName);

        } else {
            success = saleService.processCashSale(receipt);
        }

        if (success) {
            String type = isCredit ? "Credit" : "Cash";
            JOptionPane.showMessageDialog(null,
                type + " sale complete!\nTotal: Rs. " + receipt.getGrandTotal()
            );

            receipt.clear();
            tableModel.setRowCount(0);
            totalLabel.setText("Grand Total: Rs. 0.00");

            dashboard.loadSummary();

        } else {
            JOptionPane.showMessageDialog(null, "Sale failed! Please check stock and try again.");
        }
    }
}