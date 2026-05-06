
import java.time.LocalDateTime;

public class DebtTransaction {
    public static final String CREDIT  = "CREDIT";   
    public static final String PAYMENT = "PAYMENT";  

    private int           transId;
    private int           debtId;
    private double        amount;
    private String        type;        
    private LocalDateTime transDate;

    public DebtTransaction() {
    }
    public DebtTransaction(int debtId, double amount, String type, LocalDateTime transDate) {
        this.debtId    = debtId;
        this.amount    = amount;
        this.type      = type;
        this.transDate = transDate;
    }

    public DebtTransaction(int transId, int debtId, double amount,
                           String type, LocalDateTime transDate) {
        this.transId   = transId;
        this.debtId    = debtId;
        this.amount    = amount;
        this.type      = type;
        this.transDate = transDate;
    }

    public boolean isPayment() {
        return PAYMENT.equals(this.type);
    }
    public int           getTransId()   { return transId; }
    public int           getDebtId()    { return debtId; }
    public double        getAmount()    { return amount; }
    public String        getType()      { return type; }
    public LocalDateTime getTransDate() { return transDate; }

    public void setTransId(int transId)           { this.transId = transId; }
    public void setDebtId(int debtId)             { this.debtId = debtId; }
    public void setAmount(double amount)          { this.amount = amount; }
    public void setType(String type)              { this.type = type; }
    public void setTransDate(LocalDateTime date)  { this.transDate = date; }

    @Override
    public String toString() {
        return type + " | Rs." + amount + " | " + transDate;
    }
}