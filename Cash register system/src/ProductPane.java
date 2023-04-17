import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

/**
 * ProductPane
 */

public class ProductPane extends JPanel {
    private JLabel name, price1, price2;
    private Button addToCart;
    private Product product;

    public ProductPane(Product product, Runnable act){
        this.product = product;
        name = new JLabel(product.getName());
        price1 = new JLabel("Price:");
        price2 = new JLabel(String.valueOf(product.getPrice()));
        addToCart = new Button(new JButton("Add"), act);

        setLayout(new BorderLayout());

        add(name, BorderLayout.NORTH);
        add(price1, BorderLayout.CENTER);
        add(price2, BorderLayout.CENTER);
        add(addToCart, BorderLayout.SOUTH);
    }

}