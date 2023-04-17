import java.util.*;

public class Product{
    private String name;
	private String details;
    private double price;

    public Product(String name, double price, String details) {
        this.name = name;
        this.details = details;
        this.price = price;
    }

    public void setDetails(String info) {
        this.details = info;
    }

    public void setPrice(double price){
        if(Double.isNaN(price)){
            throw new IllegalArgumentException("The price of a product has to be a number.");
        }
        if(price <= 0){
            throw new IllegalArgumentException("The price of a product has to be larger than 0.");
        }
        this.price = price;
    }

    public String getName(){
        return name;
    }

    public double getPrice(){
        return price;
    }

    public String getDetails(){
        return details;
    }
    
    public String toString(){
        return "Name: " + name + "\nPrice: " + price + "\nDetails: "+ details;
    }
	
}