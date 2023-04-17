import java.util.*;

/**
 * ProductMap
 */
public class ProductMap{

    private HashMap<String, Product> products;
    public ProductMap(){
        products = new HashMap<>();
    }
    public void addProduct(Product prod) {
        String key = prod.getName().toLowerCase().trim();
        products.put(key, prod);
    }

    public Product findProduct(String name){
        return (Product)products.get(name);
    }

    public HashMap<String, Product> getProducts() {
        
        return products;
    }
    
}