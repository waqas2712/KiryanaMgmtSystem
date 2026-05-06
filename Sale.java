import java.time.LocalDateTime;
 
public class Sale {
 
    public static final String CASH   = "CASH";
    public static final String CREDIT = "CREDIT";
 
    private int           saleId;
    private String        customerName;   
    private double        amount;         
    private LocalDateTime saleDate;
    private String        status;         
 
 
    public Sale() {
    }
 
    
    public Sale(String customerName, double amount,
                LocalDateTime saleDate, String status) {
        this.customerName = customerName;
        this.amount       = amount;
        this.saleDate     = saleDate;
        this.status       = status;
    }
 
    
    public Sale(int saleId, String customerName, double amount,
                LocalDateTime saleDate, String status) {
        this.saleId       = saleId;
        this.customerName = customerName;
        this.amount       = amount;
        this.saleDate     = saleDate;
        this.status       = status;
    }
 
    // HELPER METHODS
    
    public boolean isCreditSale() {
        return CREDIT.equals(this.status);
    }
 
    
    // GETTERS
 
    public int           getSaleId()       { return saleId; }
    public String        getCustomerName() { return customerName; }
    public double        getAmount()       { return amount; }
    public LocalDateTime getSaleDate()     { return saleDate; }
    public String        getStatus()       { return status; }
 
    
 
    public void setSaleId(int saleId)             { this.saleId = saleId; }
    public void setCustomerName(String name)      { this.customerName = name; }
    public void setAmount(double amount)          { this.amount = amount; }
    public void setSaleDate(LocalDateTime date)   { this.saleDate = date; }
    public void setStatus(String status)          { this.status = status; }
 
    
 
    @Override
    public String toString() {
        return "Sale #" + saleId + " | Rs." + amount
               + " | " + status + " | " + saleDate;
    }
}