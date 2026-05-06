
import java.util.ArrayList;


public class InventoryService {

    private ProductDAO   productDAO;
    private InventoryDAO inventoryDAO;

   
    public InventoryService() {
        this.productDAO   = new ProductDAO();
        this.inventoryDAO = new InventoryDAO();
    }

    
    public boolean addNewProduct(Product product, double startingQty, double threshold) {

        boolean productSaved = productDAO.addProduct(product);

        if (!productSaved) {
            System.out.println("Failed to save product. Inventory not created.");
            return false;
        }

        ArrayList<Product> found = productDAO.searchProduct(product.getProductName());
        if (found.isEmpty()) {
            System.out.println("Could not find newly added product.");
            return false;
        }

        Product savedProduct = found.get(0);

        Inventory inventory = new Inventory(
            savedProduct.getProductId(),
            startingQty,
            threshold
        );

        boolean inventoryCreated = inventoryDAO.createInventory(inventory);

        if (!inventoryCreated) {
            System.out.println("Product saved but inventory creation failed.");
            return false;
        }

        return true; 
    }

    
    public boolean updateProduct(Product product) {
        return productDAO.updateProduct(product);
    }

   
    public boolean deleteProduct(int productId) {
        return productDAO.deleteProduct(productId);
    }

    
    public ArrayList<Product> searchProducts(String keyword) {
        return productDAO.searchProduct(keyword);
    }

    
    public ArrayList<Product> getAllProducts() {
        return productDAO.getAllProduct();
    }

    
    public boolean restockProduct(int productId, double qtyToAdd) {
        if (qtyToAdd <= 0) {
            System.out.println("Restock quantity must be greater than zero.");
            return false;
        }
        return inventoryDAO.addStock(productId, qtyToAdd);
    }

    
    public ArrayList<Inventory> getAllInventory() {
        return inventoryDAO.getAllInventoryWithProduct();
    }

    
    public ArrayList<Inventory> getLowStockItems() {
        return inventoryDAO.getLowStockItems();
    }

   
    public Inventory getInventoryForProduct(int productId) {
        return inventoryDAO.getInventoryByProduct(productId);
    }

    
    public boolean updateLowStockThreshold(int productId, double threshold) {
        if (threshold < 0) {
            System.out.println("Threshold cannot be negative.");
            return false;
        }
        return inventoryDAO.updateThreshold(productId, threshold);
    }
}