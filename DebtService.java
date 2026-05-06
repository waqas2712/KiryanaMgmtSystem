import java.time.LocalDateTime;
import java.util.ArrayList;


public class DebtService {

    private DebtDAO            debtDAO;
    private DebtTransactionDAO debtTransactionDAO;

    
    public DebtService() {
        this.debtDAO            = new DebtDAO();
        this.debtTransactionDAO = new DebtTransactionDAO();
    }

    public boolean recordPayment(int debtId, double paymentAmount) {

        if (paymentAmount <= 0) {
            System.out.println("Payment amount must be greater than zero.");
            return false;
        }

        
        Debt debt = debtDAO.getDebtById(debtId);

        if (debt == null) {
            System.out.println("Debt not found with ID: " + debtId);
            return false;
        }

        if (debt.isFullyPaid()) {
            System.out.println("This debt is already fully paid.");
            return false;
        }

        if (paymentAmount > debt.getRemaining()) {
            System.out.println("Payment amount (Rs." + paymentAmount
                + ") exceeds remaining balance (Rs." + debt.getRemaining() + ").");
            return false;
        }

        
        debt.applyPayment(paymentAmount);

       
        boolean debtUpdated = debtDAO.updateDebt(debt);

        if (!debtUpdated) {
            System.out.println("Failed to update debt record.");
            return false;
        }

        DebtTransaction payment = new DebtTransaction(
            debtId,
            paymentAmount,
            DebtTransaction.PAYMENT,    // type = "PAYMENT"
            LocalDateTime.now()
        );

        boolean transactionSaved = debtTransactionDAO.saveTransaction(payment);

        if (!transactionSaved) {
            System.out.println("Warning: Debt updated but transaction log failed.");
        }

        return true;
    }


    public ArrayList<Debt> getAllDebts() {
        return debtDAO.getAllDebts();
    }

    
    public ArrayList<Debt> getPendingDebts() {
        return debtDAO.getPendingDebts();
    }

    
    public ArrayList<Debt> searchDebts(String customerName) {
        if (customerName == null || customerName.trim().isEmpty()) {
            // If search is empty, return all debts
            return debtDAO.getAllDebts();
        }
        return debtDAO.searchByCustomer(customerName.trim());
    }

    
    public ArrayList<DebtTransaction> getPaymentHistory(int debtId) {
        return debtTransactionDAO.getTransactionsByDebt(debtId);
    }

    
    public Debt getDebtById(int debtId) {
        return debtDAO.getDebtById(debtId);
    }
}