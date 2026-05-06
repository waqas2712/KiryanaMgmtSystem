
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;



public class RestockPanel extends JPanel {

    InventoryService inventoryService;

    // Form fields (left side)
    JTextField  nameField;
    JTextField  priceField;
    JTextField  costField;
    JTextField  startQtyField;
    JTextField  thresholdField;
    JTextField  restockQtyField;
    JComboBox<String> unitDropdown;

    
    int selectedProductId = -1;

    JTable            table;
    DefaultTableModel tableModel;

    public RestockPanel() {

        inventoryService = new InventoryService();

        setLayout(new BorderLayout(5, 5));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        add(buildFormPanel(),   BorderLayout.WEST);
        add(buildTablePanel(),  BorderLayout.CENTER);
        add(buildButtonPanel(), BorderLayout.SOUTH);

        loadTable(); // fill table with data on startup
    }

    
    private JPanel buildFormPanel() {

        JPanel form = new JPanel(new GridLayout(12, 2, 5, 5));
        form.setBorder(BorderFactory.createTitledBorder("Product Form"));
        form.setPreferredSize(new Dimension(300, 0));

        nameField      = new JTextField();
        priceField     = new JTextField();
        costField      = new JTextField();
        startQtyField  = new JTextField();
        thresholdField = new JTextField();
        restockQtyField = new JTextField();
        unitDropdown   = new JComboBox<>(new String[]{"KG", "PIECE", "LITER", "GRAM", "DOZEN"});

        form.add(new JLabel("Product Name:"));
        form.add(nameField);

        form.add(new JLabel("Unit Type:"));
        form.add(unitDropdown);

        form.add(new JLabel("Price / Unit (Rs.):"));
        form.add(priceField);

        form.add(new JLabel("Cost / Unit (Rs.):"));
        form.add(costField);

        form.add(new JLabel("--- New Product Only ---"));
        form.add(new JLabel(""));

        form.add(new JLabel("Starting Qty:"));
        form.add(startQtyField);

        form.add(new JLabel("Low Stock Alert:"));
        form.add(thresholdField);

        // Buttons for add and update
        JButton addBtn    = new JButton("Add New Product");
        JButton updateBtn = new JButton("Update Product");

        addBtn.setBackground(new Color(76, 175, 80));
        addBtn.setForeground(Color.WHITE);
        updateBtn.setBackground(new Color(33, 150, 243));
        updateBtn.setForeground(Color.WHITE);

        form.add(addBtn);
        form.add(updateBtn);

        form.add(new JLabel("--- Restock Only ---"));
        form.add(new JLabel(""));

        form.add(new JLabel("Add Qty to Stock:"));
        form.add(restockQtyField);

        JButton restockBtn = new JButton("Add Stock");
        restockBtn.setBackground(new Color(255, 152, 0));
        restockBtn.setForeground(Color.WHITE);
        form.add(restockBtn);
        form.add(new JLabel(""));

    
        addBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                handleAddProduct();
            }
        });

        updateBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                handleUpdateProduct();
            }
        });

        restockBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                handleRestock();
            }
        });

        return form;
    }

    
    private JPanel buildTablePanel() {

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Inventory"));

        String[] columns = {
            "ID", "Product Name", "Unit", "Price", "Cost", "Stock Qty", "Low Stock Alert"
        };

        tableModel = new DefaultTableModel(columns, 0) {
            public boolean isCellEditable(int row, int col) {
                return false; // not editable directly in the table
            }
        };

        table = new JTable(tableModel);
        table.setFont(new Font("Arial", Font.PLAIN, 12));
        table.setRowHeight(24);
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));

        // Hide the ID column (column 0) - it is there but invisible
        table.getColumnModel().getColumn(0).setMinWidth(0);
        table.getColumnModel().getColumn(0).setMaxWidth(0);

        panel.add(new JScrollPane(table), BorderLayout.CENTER);


        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int row = table.getSelectedRow();
                if (row >= 0) {
                    // Read product data from the selected row
                    selectedProductId = (int) tableModel.getValueAt(row, 0);
                    nameField.setText((String) tableModel.getValueAt(row, 1));
                    unitDropdown.setSelectedItem(tableModel.getValueAt(row, 2));
                    priceField.setText(String.valueOf(tableModel.getValueAt(row, 3)));
                    costField.setText(String.valueOf(tableModel.getValueAt(row, 4)));
                    thresholdField.setText(String.valueOf(tableModel.getValueAt(row, 6)));
                }
            }
        });

        return panel;
    }

    
    private JPanel buildButtonPanel() {

        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        JButton deleteBtn  = new JButton("Delete Selected");
        JButton refreshBtn = new JButton("Refresh Table");

        deleteBtn.setBackground(new Color(211, 47, 47));
        deleteBtn.setForeground(Color.WHITE);

        panel.add(refreshBtn);
        panel.add(deleteBtn);

        deleteBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                handleDelete();
            }
        });

        refreshBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                loadTable();
            }
        });

        return panel;
    }

    // -----------------------------------------------
    // LOAD TABLE - fills the JTable from the database
    // -----------------------------------------------
    private void loadTable() {

        tableModel.setRowCount(0); // clear existing rows

        ArrayList<Inventory> list = inventoryService.getAllInventory();

        for (Inventory inv : list) {
            Product p = inv.getProduct();
            if (p == null) continue;

            tableModel.addRow(new Object[]{
                p.getProductId(),
                p.getProductName(),
                p.getUnitType(),
                p.getPricePerUnit(),
                p.getCostPerUnit(),
                inv.getCurrentQty(),
                inv.getLowStockThreshold()
            });
        }
    }



    private void handleAddProduct() {

        String name  = nameField.getText().trim();
        String unit  = (String) unitDropdown.getSelectedItem();
        String price = priceField.getText().trim();
        String cost  = costField.getText().trim();
        String qty   = startQtyField.getText().trim();
        String thr   = thresholdField.getText().trim();

        if (name.isEmpty() || price.isEmpty() || cost.isEmpty()
                || qty.isEmpty() || thr.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Please fill in all fields.");
            return;
        }

        try {
            double priceVal = Double.parseDouble(price);
            double costVal  = Double.parseDouble(cost);
            double qtyVal   = Double.parseDouble(qty);
            double thrVal   = Double.parseDouble(thr);

            Product product = new Product(name, unit, priceVal, costVal);
            boolean success = inventoryService.addNewProduct(product, qtyVal, thrVal);

            if (success) {
                JOptionPane.showMessageDialog(null, "Product added successfully!");
                clearForm();
                loadTable();
            } else {
                JOptionPane.showMessageDialog(null, "Failed. Product name may already exist.");
            }

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(null, "Price, cost, qty, and threshold must be numbers.");
        }
    }

    private void handleUpdateProduct() {

        if (selectedProductId == -1) {
            JOptionPane.showMessageDialog(null, "Please select a product from the table first.");
            return;
        }

        String name  = nameField.getText().trim();
        String unit  = (String) unitDropdown.getSelectedItem();
        String price = priceField.getText().trim();
        String cost  = costField.getText().trim();

        if (name.isEmpty() || price.isEmpty() || cost.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Name, price, and cost cannot be empty.");
            return;
        }

        try {
            double priceVal = Double.parseDouble(price);
            double costVal  = Double.parseDouble(cost);

            Product updated = new Product(selectedProductId, name, unit, priceVal, costVal);
            boolean success = inventoryService.updateProduct(updated);

            if (success) {
                JOptionPane.showMessageDialog(null, "Product updated!");
                loadTable();
            } else {
                JOptionPane.showMessageDialog(null, "Update failed.");
            }

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(null, "Price and cost must be numbers.");
        }
    }

    private void handleRestock() {

        if (selectedProductId == -1) {
            JOptionPane.showMessageDialog(null, "Please select a product from the table first.");
            return;
        }

        String qtyText = restockQtyField.getText().trim();
        if (qtyText.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Enter quantity to add.");
            return;
        }

        try {
            double qty     = Double.parseDouble(qtyText);
            boolean success = inventoryService.restockProduct(selectedProductId, qty);

            if (success) {
                JOptionPane.showMessageDialog(null, "Stock updated!");
                restockQtyField.setText("");
                loadTable();
            } else {
                JOptionPane.showMessageDialog(null, "Restock failed.");
            }

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(null, "Please enter a valid number.");
        }
    }

    private void handleDelete() {

        if (selectedProductId == -1) {
            JOptionPane.showMessageDialog(null, "Please select a product from the table first.");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
            null,
            "Delete this product? This cannot be undone.",
            "Confirm Delete",
            JOptionPane.YES_NO_OPTION
        );

        if (confirm == JOptionPane.YES_OPTION) {
            boolean success = inventoryService.deleteProduct(selectedProductId);
            if (success) {
                JOptionPane.showMessageDialog(null, "Product deleted.");
                clearForm();
                selectedProductId = -1;
                loadTable();
            } else {
                JOptionPane.showMessageDialog(null, "Delete failed.");
            }
        }
    }

    private void clearForm() {
        nameField.setText("");
        priceField.setText("");
        costField.setText("");
        startQtyField.setText("");
        thresholdField.setText("");
        restockQtyField.setText("");
        unitDropdown.setSelectedIndex(0);
        selectedProductId = -1;
    }
}