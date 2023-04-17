import java.util.HashMap;
import java.util.Map;
/**
 * Customer
 */
public class Customer {
    private Map<Product, Double> cart;
	private double totalPrice;    

    public Customer(){
        cart = new HashMap<>();
        totalPrice = 0;
    }

    public void addCart(Product product, double amount){
		if(cart.containsKey(product)){
			cart.put(product, cart.get(product)+amount);
		}
		else{
			cart.put(product, amount);
		}
		
		addTotalPrice(product.getPrice() * amount);
	}
	public void removeCart(Product product, double amount){
		if(cart.containsKey(product)){
			
			if(cart.get(product) <= amount){
				amount = cart.get(product);
				cart.remove(product);
			}
			else{
				cart.put(product, cart.get(product) - amount);
			}
			
			addTotalPrice(-product.getPrice() * amount);
			
		}

	}

    private void addTotalPrice(double price){
		totalPrice += price;
	}
	
	public Map<Product, Double> getCart(){
		return cart;
	}
	public double getTotalPrice(){
		return totalPrice;
	}
}