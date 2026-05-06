public class SaleItem {

    private int     saleId;
    private int     productId;
    private Product product;       
    private double  qtySold;
    private double  subTotal;

    

    public SaleItem() {
    }


    public SaleItem(int saleId, Product product, double qtySold, double subTotal) {
        this.saleId    = saleId;
        this.product   = product;
        this.productId = product.getProductId();
        this.qtySold   = qtySold;
        this.subTotal  = subTotal;
    }
    public SaleItem(int saleId, int productId, double qtySold, double subTotal) {
        this.saleId    = saleId;
        this.productId = productId;
        this.qtySold   = qtySold;
        this.subTotal  = subTotal;
    }

    public int     getSaleId()    { return saleId; }
    public int     getProductId() { return productId; }
    public Product getProduct()   { return product; }
    public double  getQtySold()   { return qtySold; }
    public double  getSubTotal()  { return subTotal; }

    public void setSaleId(int saleId)       { this.saleId = saleId; }
    public void setProductId(int id)        { this.productId = id; }
    public void setProduct(Product product) {
        this.product   = product;
        this.productId = product.getProductId();
    }
    public void setQtySold(double qtySold)  { this.qtySold = qtySold; }
    public void setSubTotal(double sub)     { this.subTotal = sub; }


    @Override
    public String toString() {
        String name = (product != null) ? product.getProductName() : "Product #" + productId;
        return name + " x" + qtySold + " = Rs." + subTotal;
    }
}