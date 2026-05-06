import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

public class DebtPanel extends JPanel {

    DebtService debtService;

    JTable            debtTable;
    DefaultTableModel debtModel;

    JTable            historyTable;
    DefaultTableModel historyModel;

    JTextField searchField;

    JTextField payAmountField;
    JLabel     selectedCustomerLabel;
    JLabel     remainingAmountLabel;

    int selectedDebtId = -1;

    public DebtPanel() {

        debtService = new DebtService();

        setLayout(new BorderLayout(5, 5));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        add(buildSearchBar(),    BorderLayout.NORTH);
        add(buildCenterPanel(),  BorderLayout.CENTER);
        add(buildPaymentPanel(), BorderLayout.EAST);

        loadDebtTable(); // fill on startup
    }

    
    private JPanel buildSearchBar() {

        JPanel bar = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 5));

        bar.add(new JLabel("Search by Customer Name:"));

        searchField = new JTextField(18);
        bar.add(searchField);

        JButton searchBtn  = new JButton("Search");
        JButton showAllBtn = new JButton("Show All");

        searchBtn.setBackground(new Color(33, 150, 243));
        searchBtn.setForeground(Color.WHITE);

        bar.add(searchBtn);
        bar.add(showAllBtn);

        searchBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                handleSearch();
            }
        });

        showAllBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                loadDebtTable();
            }
        });

        return bar;
    }

    
    private JPanel buildCenterPanel() {

        JPanel center = new JPanel(new BorderLayout(5, 5));

        String[] debtColumns = {
            "Debt ID", "Customer", "Total (Rs.)", "Remaining (Rs.)", "Date", "Status"
        };

        debtModel = new DefaultTableModel(debtColumns, 0) {
            public boolean isCellEditable(int row, int col) { return false; }
        };

        debtTable = new JTable(debtModel);
        debtTable.setFont(new Font("Arial", Font.PLAIN, 12));
        debtTable.setRowHeight(24);
        debtTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));

        debtTable.getColumnModel().getColumn(0).setMinWidth(0);
        debtTable.getColumnModel().getColumn(0).setMaxWidth(0);

        JScrollPane topScroll = new JScrollPane(debtTable);
        topScroll.setBorder(BorderFactory.createTitledBorder("All Debts (Khata)"));
        topScroll.setPreferredSize(new Dimension(0, 250));

        center.add(topScroll, BorderLayout.NORTH);

        String[] historyColumns = {"Trans ID", "Type", "Amount (Rs.)", "Date"};

        historyModel = new DefaultTableModel(historyColumns, 0) {
            public boolean isCellEditable(int row, int col) { return false; }
        };

        historyTable = new JTable(historyModel);
        historyTable.setFont(new Font("Arial", Font.PLAIN, 12));
        historyTable.setRowHeight(24);
        historyTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));

        JScrollPane bottomScroll = new JScrollPane(historyTable);
        bottomScroll.setBorder(BorderFactory.createTitledBorder("Payment History (click a debt above)"));

        center.add(bottomScroll, BorderLayout.CENTER);

        
        debtTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int row = debtTable.getSelectedRow();
                if (row >= 0) {
                    selectedDebtId = (int) debtModel.getValueAt(row, 0);
                    String customerName = (String) debtModel.getValueAt(row, 1);
                    String remaining    = String.valueOf(debtModel.getValueAt(row, 3));
                    String status       = (String) debtModel.getValueAt(row, 5);

                    selectedCustomerLabel.setText("Customer: " + customerName);
                    remainingAmountLabel.setText("Owes: Rs. " + remaining + " [" + status + "]");

                    loadHistoryTable(selectedDebtId);
                }
            }
        });

        return center;
    }

    
    private JPanel buildPaymentPanel() {

        JPanel panel = new JPanel(new GridLayout(9, 1, 5, 5));
        panel.setBorder(BorderFactory.createTitledBorder("Record Payment"));
        panel.setPreferredSize(new Dimension(210, 0));

        selectedCustomerLabel = new JLabel("No debt selected");
        selectedCustomerLabel.setFont(new Font("Arial", Font.BOLD, 12));

        remainingAmountLabel = new JLabel("Remaining: --");
        remainingAmountLabel.setForeground(new Color(180, 0, 0));

        JLabel amountLabel = new JLabel("Payment Amount (Rs.):");
        payAmountField = new JTextField();

        JButton payBtn     = new JButton("Record Payment");
        JButton refreshBtn = new JButton("Refresh Table");

        payBtn.setBackground(new Color(76, 175, 80));
        payBtn.setForeground(Color.WHITE);

        panel.add(selectedCustomerLabel);
        panel.add(remainingAmountLabel);
        panel.add(new JLabel("")); // spacer
        panel.add(amountLabel);
        panel.add(payAmountField);
        panel.add(payBtn);
        panel.add(new JLabel("")); // spacer
        panel.add(refreshBtn);
        panel.add(new JLabel("")); // spacer

        
        payBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                handleRecordPayment();
            }
        });

        refreshBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                loadDebtTable();
            }
        });

        return panel;
    }

   
    private void loadDebtTable() {

        debtModel.setRowCount(0);
        historyModel.setRowCount(0);
        selectedDebtId = -1;
        selectedCustomerLabel.setText("No debt selected");
        remainingAmountLabel.setText("Remaining: --");

        ArrayList<Debt> debts = debtService.getAllDebts();

        for (Debt d : debts) {
            debtModel.addRow(new Object[]{
                d.getDebtId(),
                d.getCustomerName(),
                d.getAmount(),
                d.getRemaining(),
                d.getDebtDate() != null ? d.getDebtDate().toLocalDate().toString() : "-",
                d.getStatus()
            });
        }
    }

    
    private void loadHistoryTable(int debtId) {

        historyModel.setRowCount(0);

        ArrayList<DebtTransaction> transactions = debtService.getPaymentHistory(debtId);

        if (transactions.isEmpty()) {
            historyModel.addRow(new Object[]{"--", "--", "--", "No history found"});
            return;
        }

        for (DebtTransaction dt : transactions) {
            historyModel.addRow(new Object[]{
                dt.getTransId(),
                dt.getType(),
                dt.getAmount(),
                dt.getTransDate() != null ? dt.getTransDate().toString() : "-"
            });
        }
    }

    
    private void handleSearch() {

        String keyword = searchField.getText().trim();
        ArrayList<Debt> results = debtService.searchDebts(keyword);

        debtModel.setRowCount(0);
        historyModel.setRowCount(0);

        if (results.isEmpty()) {
            JOptionPane.showMessageDialog(null, "No debts found for: " + keyword);
            return;
        }

        for (Debt d : results) {
            debtModel.addRow(new Object[]{
                d.getDebtId(),
                d.getCustomerName(),
                d.getAmount(),
                d.getRemaining(),
                d.getDebtDate() != null ? d.getDebtDate().toLocalDate().toString() : "-",
                d.getStatus()
            });
        }
    }

    
    private void handleRecordPayment() {

        if (selectedDebtId == -1) {
            JOptionPane.showMessageDialog(null, "Please select a customer debt first.");
            return;
        }

        String amountText = payAmountField.getText().trim();
        if (amountText.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Please enter the payment amount.");
            return;
        }

        double amount;
        try {
            amount = Double.parseDouble(amountText);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(null, "Please enter a valid number.");
            return;
        }

        if (amount <= 0) {
            JOptionPane.showMessageDialog(null, "Amount must be greater than zero.");
            return;
        }

        boolean success = debtService.recordPayment(selectedDebtId, amount);

        if (success) {
            JOptionPane.showMessageDialog(null,
                "Payment of Rs. " + amount + " recorded successfully!"
            );
            payAmountField.setText("");
            loadDebtTable(); // refresh the full table

        } else {
            JOptionPane.showMessageDialog(null,
                "Payment failed. Amount may exceed remaining balance."
            );
        }
    }
}