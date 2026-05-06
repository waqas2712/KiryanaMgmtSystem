import java.time.LocalDateTime;
import java.util.ArrayList;


public class SaleService {

    // Composition — five DAOs working together
    private SaleDAO             saleDAO;
    private SaleItemDAO         saleItemDAO;
    private InventoryDAO        inventoryDAO;
    private DebtDAO             debtDAO;
    private DebtTransactionDAO  debtTransactionDAO;

    
    public SaleService() {
        this.saleDAO            = new SaleDAO();
        this.saleItemDAO        = new SaleItemDAO();
        this.inventoryDAO       = new InventoryDAO();
        this.debtDAO            = new DebtDAO();
        this.debtTransactionDAO = new DebtTransactionDAO();
    }

    
    public boolean processCashSale(Receipt receipt) {

        // Guard: do not process an empty receipt
        if (receipt == null || receipt.isEmpty()) {
            System.out.println("Cannot process empty receipt.");
            return false;
        }

        // --------------------------------------------------
        // STEP 1: Build and save the Sale header
        // --------------------------------------------------
        Sale sale = new Sale(
            null,                       // no customer name for cash sales
            receipt.getGrandTotal(),
            LocalDateTime.now(),
            Sale.CASH                   // status = "CASH"
        );

        int saleId = saleDAO.saveSale(sale);

        if (saleId == -1) {
            System.out.println("Cash sale failed: could not save sale record.");
            return false;
        }

        
        return saveItemsAndDeductStock(saleId, receipt.getItems());
    }

    
    public boolean processCreditSale(Receipt receipt, String customerName) {

        // Guard: credit sales must have a customer name
        if (receipt == null || receipt.isEmpty()) {
            System.out.println("Cannot process empty receipt.");
            return false;
        }
        if (customerName == null || customerName.trim().isEmpty()) {
            System.out.println("Customer name is required for credit sales.");
            return false;
        }

        // --------------------------------------------------
        // STEP 1: Build and save the Sale header
        // --------------------------------------------------
        Sale sale = new Sale(
            customerName.trim(),
            receipt.getGrandTotal(),
            LocalDateTime.now(),
            Sale.CREDIT                 // status = "CREDIT"
        );

        int saleId = saleDAO.saveSale(sale);

        if (saleId == -1) {
            System.out.println("Credit sale failed: could not save sale record.");
            return false;
        }

        
        boolean itemsSaved = saveItemsAndDeductStock(saleId, receipt.getItems());

        if (!itemsSaved) {
            System.out.println("Credit sale failed: could not save sale items.");
            return false;
        }

        
        Debt debt = new Debt(
            saleId,
            customerName.trim(),
            receipt.getGrandTotal(),
            LocalDateTime.now()
            // Status defaults to UNPAID, remaining = amount (see Debt constructor)
        );

        int debtId = debtDAO.createDebt(debt);

        if (debtId == -1) {
            System.out.println("Credit sale failed: could not create debt record.");
            return false;
        }

        DebtTransaction openingEntry = new DebtTransaction(
            debtId,
            receipt.getGrandTotal(),
            DebtTransaction.CREDIT,     // type = "CREDIT" (debt given)
            LocalDateTime.now()
        );

        boolean transactionSaved = debtTransactionDAO.saveTransaction(openingEntry);

        if (!transactionSaved) {
            System.out.println("Warning: Debt created but transaction log failed.");
            // We return true here because the core sale and debt were saved.
            // The transaction log is supplementary.
        }

        return true; // all 5 steps completed
    }

    
    private boolean saveItemsAndDeductStock(int saleId, ArrayList<SaleItem> items) {
        for (SaleItem item : items) {

            // Link this item to the sale we just created
            item.setSaleId(saleId);

            // Save the item to item_per_sale table
            boolean itemSaved = saleItemDAO.saveSaleItem(item);
            if (!itemSaved) {
                System.out.println("Failed to save item: " + item.getProductId());
                return false;
            }

            // Deduct stock from inventory
            boolean stockDeducted = inventoryDAO.deductStock(
                item.getProductId(),
                item.getQtySold()
            );
            if (!stockDeducted) {
                System.out.println("Failed to deduct stock for product: " + item.getProductId());
                return false;
            }
        }
        return true;
    }

    
    public ArrayList<Sale> getRecentSales() {
        return saleDAO.getRecentSales();
    }

    
    public ArrayList<Sale> getAllSales() {
        return saleDAO.getAllSales();
    }

   
    public double calculateQtyFromAmount(double amountInRupees, double pricePerUnit) {
        if (pricePerUnit <= 0) return 0;
        double qty = amountInRupees / pricePerUnit;
        // Round to 3 decimal places for clean display
        return Math.round(qty * 1000.0) / 1000.0;
    }

   
    public double calculateSubTotal(double qty, double pricePerUnit) {
        return Math.round(qty * pricePerUnit * 100.0) / 100.0;
    }
}