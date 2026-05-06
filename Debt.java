
import java.time.LocalDateTime;


public class Debt {

    // Status constants — use these instead of raw strings
    public static final String UNPAID          = "UNPAID";
    public static final String PARTIALLY_PAID  = "PARTIALLY_PAID";
    public static final String PAID            = "PAID";

    private int           debtId;
    private int           saleId;
    private String        customerName;
    private double        amount;       // original debt amount
    private double        remaining;    // amount still owed
    private LocalDateTime debtDate;
    private String        status;
    public Debt() {
    }
    public Debt(int saleId, String customerName, double amount, LocalDateTime debtDate) {
        this.saleId       = saleId;
        this.customerName = customerName;
        this.amount       = amount;
        this.remaining    = amount;      // nothing paid yet
        this.debtDate     = debtDate;
        this.status       = UNPAID;      // always starts as UNPAID
    }
    public Debt(int debtId, int saleId, String customerName,
                double amount, double remaining,
                LocalDateTime debtDate, String status) {
        this.debtId       = debtId;
        this.saleId       = saleId;
        this.customerName = customerName;
        this.amount       = amount;
        this.remaining    = remaining;
        this.debtDate     = debtDate;
        this.status       = status;
    }
    public boolean isFullyPaid() {
        return remaining <= 0;
    }
    public boolean isUnpaid() {
        return UNPAID.equals(this.status);
    }
    public void applyPayment(double paymentAmount) {
        this.remaining -= paymentAmount;

        if (this.remaining <= 0) {
            this.remaining = 0;
            this.status    = PAID;
        } else {
            this.status = PARTIALLY_PAID;
        }
    }


    public int           getDebtId()       { return debtId; }
    public int           getSaleId()       { return saleId; }
    public String        getCustomerName() { return customerName; }
    public double        getAmount()       { return amount; }
    public double        getRemaining()    { return remaining; }
    public LocalDateTime getDebtDate()     { return debtDate; }
    public String        getStatus()       { return status; }

    // SETTERS

    public void setDebtId(int debtId)             { this.debtId = debtId; }
    public void setSaleId(int saleId)             { this.saleId = saleId; }
    public void setCustomerName(String name)      { this.customerName = name; }
    public void setAmount(double amount)          { this.amount = amount; }
    public void setRemaining(double remaining)    { this.remaining = remaining; }
    public void setDebtDate(LocalDateTime date)   { this.debtDate = date; }
    public void setStatus(String status)          { this.status = status; }

    @Override
    public String toString() {
        return "Debt #" + debtId + " | " + customerName
               + " | Total: Rs." + amount
               + " | Remaining: Rs." + remaining
               + " | " + status;
    }
}