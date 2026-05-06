import java.time.LocalDate;
public class Inventory {
	private int stockId;
	private int productId;
	private Product product;
	private double currentQty;
	private double lowStockThreshold;
	private LocalDate lastRestockDate;


      public Inventory() {
    }
 
    
    public Inventory(int productId, double currentQty, double lowStockThreshold) {
        this.productId          = productId;
        this.currentQty         = currentQty;
        this.lowStockThreshold  = lowStockThreshold;
        this.lastRestockDate    = LocalDate.now();
    }
 

    public Inventory(int stockId, int productId, double currentQty,
                     double lowStockThreshold, LocalDate lastRestockDate) {
        this.stockId            = stockId;
        this.productId          = productId;
        this.currentQty         = currentQty;
        this.lowStockThreshold  = lowStockThreshold;
        this.lastRestockDate    = lastRestockDate;
    }


 public boolean isLowStock() {
        return currentQty < lowStockThreshold;
    }
    public int       getStockId()           { return stockId; }
    public int       getProductId()         { return productId; }
    public Product   getProduct()           { return product; }
    public double    getCurrentQty()        { return currentQty; }
    public double    getLowStockThreshold() { return lowStockThreshold; }
    public LocalDate getLastRestockDate()   { return lastRestockDate; }


    public void setStockId(int stockId)                  { this.stockId = stockId; }
    public void setProductId(int productId)              { this.productId = productId; }
    public void setProduct(Product product)              { this.product = product; }
    public void setCurrentQty(double currentQty)         { this.currentQty = currentQty; }
    public void setLowStockThreshold(double threshold)   { this.lowStockThreshold = threshold; }
    public void setLastRestockDate(LocalDate date)       { this.lastRestockDate = date; }
     @Override
    public String toString() {
        String name = (product != null) ? product.getProductName() : "Product #" + productId;
        return name + " | Qty: " + currentQty + " | Threshold: " + lowStockThreshold;
    }
}