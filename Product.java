public class Product {
	private int productId;
	private String productName;
	private String unitType;
	private Double pricePerUnit;
	private Double costPerUnit;

	public Product (){
	}

	public Product (String productName, String unitType, double pricePerUnit, double costPerUnit){
			this.productName = productName;
			this.unitType = unitType;
			this.pricePerUnit = pricePerUnit;
			this.costPerUnit = costPerUnit;
	}

	public Product(int productId, String productName, String unitType,
                   double pricePerUnit, double costPerUnit) {
        this.productId    = productId;
        this.productName  = productName;
        this.unitType     = unitType;
        this.pricePerUnit = pricePerUnit;
        this.costPerUnit  = costPerUnit;
    }
    public int    getProductId()   { return productId; }
    public String getProductName() { return productName; }
    public String getUnitType()    { return unitType; }
    public double getPricePerUnit(){ return pricePerUnit; }
    public double getCostPerUnit() { return costPerUnit; } 
    public void setProductId(int productId)       { this.productId = productId; }
    public void setProductName(String productName){ this.productName = productName; }
    public void setUnitType(String unitType)      { this.unitType = unitType; }
    public void setPricePerUnit(double price)     { this.pricePerUnit = price; }
    public void setCostPerUnit(double cost)       { this.costPerUnit = cost; }	
    @Override
    public String toString() {
        return productName + " (" + unitType + ") - Rs." + pricePerUnit;
    }
}
  



