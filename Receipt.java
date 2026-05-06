
import java.util.ArrayList;


public class Receipt {

    private ArrayList<SaleItem> items;


    public Receipt() {
        items = new ArrayList<>();
    }


    public void addItem(SaleItem item) {
        items.add(item);
    }
    public void removeItem(int index) {
        if (index >= 0 && index < items.size()) {
            items.remove(index);
        }
    }

    public void clear() {
        items.clear();
    }

    public double getGrandTotal() {
        double total = 0;
        for (SaleItem item : items) {
            total += item.getSubTotal();
        }
        return total;
    }

    public int getItemCount() {
        return items.size();
    }
    public boolean isEmpty() {
        return items.isEmpty();
    }
    public ArrayList<SaleItem> getItems() {
        return items;
    }


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("===== RECEIPT =====\n");
        for (SaleItem item : items) {
            sb.append(item.toString()).append("\n");
        }
        sb.append("TOTAL: Rs.").append(getGrandTotal());
        return sb.toString();
    }
}