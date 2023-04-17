import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;

public class ProductGUI {
    private final static JLabel EMPTYLABEL = new JLabel(" ");
    private JFrame cashSystem;
    private JPanel p_back, p_top, productList, p_bot, p_bot1, searchList, checkoutPanel;
    private JTextField tf_search;
    private JPanel b_search, b_checkout, b_end;
    private JLabel l1_price, l2_price;
    private JMenuBar mb;
    
    private JMenu menu;
    private JMenuItem item_save, item_exit;

    private ProductMap products;
    private Customer customer;

    private CartPanel cartPanel;

    public ProductGUI(ProductMap proMap) {
        customer = new Customer();
        products = proMap;
        cashSystem = new JFrame("Quick checkout system");
        createComponents();
        addComponents();
        cashSystem.setResizable(false);
        cashSystem.setContentPane(p_back);
        cashSystem.pack();
        cashSystem.setVisible(true);
        cashSystem.setDefaultCloseOperation(3);
    }


    private void createComponents() {

        tf_search = new JTextField(20);
        b_search = new Button(new JButton("Search for product"), () -> search());
        
        l1_price = new JLabel("Price:");
        l2_price = new JLabel("0");
        b_end = new Button(new JButton("Return"), () -> previous());
        b_checkout = new Button(new JButton("Checkout"), () -> checkout());

        mb = new JMenuBar();
        menu = new JMenu("File");
        item_save = new JMenuItem("Save");
        item_exit = new JMenuItem("Exit");

        
        p_bot = new JPanel();
        p_bot1 = new JPanel();
        p_back = new JPanel();
        p_top = new JPanel();
        cartPanel = new CartPanel();
        searchList = new JPanel();
    }

    private void addComponents(){
        p_bot1.setLayout(new GridLayout(1, 4));
        p_bot1.add(l1_price);
        p_bot1.add(l2_price);
        p_bot1.add(EMPTYLABEL);
        p_bot1.add(b_checkout);
        p_bot.setLayout(new BorderLayout());
        p_bot.add(cartPanel, BorderLayout.NORTH);
        p_bot.add(p_bot1, BorderLayout.SOUTH);
        
        p_top.setLayout(new GridLayout(1, 4));
        p_top.add(b_end);
        p_top.add(EMPTYLABEL);
        p_top.add(tf_search);
        p_top.add(b_search);

        productList = createProductList();
        p_back.setLayout(new BorderLayout());
        p_back.add(p_top, BorderLayout.NORTH);
        p_back.add(productList, BorderLayout.CENTER);
        p_back.add(p_bot, BorderLayout.SOUTH);

        menu.add(item_save);
        menu.add(item_exit);
        mb.add(menu);
        cashSystem.setJMenuBar(mb);
        b_end.setVisible(false);

    }

    private JPanel createProductList() {
        productList = new JPanel();
        productList.setLayout(new BorderLayout());

        // Adding products to the ProductPages
        CardLayout cl = new CardLayout();
        JPanel p_back_card = new JPanel(cl);
        
        ArrayList<JPanel> productPages = new ArrayList<>();
        HashMap<String, Product> map = products.getProducts();
        ArrayList<ProductPane> panes = new ArrayList<>();

        // Create Product Panes
        for (String key : map.keySet()) {
            Product temp = map.get(key);
            ProductPane pPane = new ProductPane(temp, () -> addToCart(temp));
            panes.add(pPane);
        }

        for (int i = 0; i < panes.size(); i += 4) {
            JPanel card = new JPanel();
            card.setLayout(new GridLayout(2, 2));

            for (int j = i; j < i + 4; j++) {
                if (j >= panes.size()) {
                    card.add(new JPanel());
                }
                else{
                    card.add(panes.get(j));
                }
            }
            productPages.add(card);
        }

        for (int i = 0; i < productPages.size(); i++) {
            p_back_card.add(productPages.get(i), "Page: " + i + 1);
        }

        //Buttons to turn pages.
        JPanel prevCard = new Button(new JButton("Previous"), () -> cl.previous(p_back_card));
        JPanel nextCard = new Button(new JButton("Next"), () -> cl.next(p_back_card));
        JPanel shiftButtons = new JPanel(new GridLayout());
        shiftButtons.add(prevCard);
        shiftButtons.add(nextCard);
        productList.add(p_back_card, BorderLayout.NORTH);
        productList.add(shiftButtons, BorderLayout.SOUTH);

        return productList;
    }

    public void addToCart(Product prod) {
        customer.addCart(prod, 1);
        setPriceLabel();
        cartPanel.updateGUI();
        cashSystem.pack();
    }

    public void removeFromCart(Product prod) {
        customer.removeCart(prod, 1);
        setPriceLabel();
        cartPanel.updateGUI();
        cashSystem.pack();
    }

    public void setPriceLabel() {
        l2_price.setText(String.valueOf(customer.getTotalPrice()));
    }

    public void search(){
        String searchString = tf_search.getText().toLowerCase().trim();
        Product temp = products.findProduct(searchString);
        if(searchString.equals("")){

        }else if(temp == null){
            JOptionPane.showMessageDialog(null, "That product does not exist!");
        }else{
            p_back.remove(productList);
            searchList.removeAll();
            tf_search.setText("");
            ProductPane pPane = new ProductPane(temp, () -> addToCart(temp));
            searchList.add(pPane);
            searchList.setVisible(true);
            p_back.add(searchList, BorderLayout.CENTER);
            b_end.setVisible(true);
        }   
    }

    private void checkout(){

        createCheckout();
        
        p_back.setVisible(false);
        checkoutPanel.setVisible(true);
        cashSystem.setContentPane(checkoutPanel);
    
    }
    
    private void createCheckout(){
        checkoutPanel = new JPanel();
        checkoutPanel.setLayout(new BorderLayout());
    
        JPanel bot = new JPanel(new GridLayout(1, 4));
        JPanel b_cancel = new Button(new JButton("Cancel"), () -> cancelCheckOut());
        JPanel b_confirm = new Button(new JButton("Confirm"), () -> purchaseFinish());
        bot.add(b_cancel);
        bot.add(b_confirm);
        
        JPanel l_price = new JPanel(new GridLayout(1, 4));
        l_price.add(new JLabel("Price to pay: "));
        l_price.add(new JLabel(String.valueOf(customer.getTotalPrice())));

        JPanel bot1 = new JPanel(new GridLayout(0,1));
        bot1.add(l_price);
        bot1.add(bot);
        checkoutPanel.add(cartPanel.cartList(), BorderLayout.NORTH);

        checkoutPanel.add(bot1, BorderLayout.SOUTH);
        cashSystem.pack();
    }

    private void cancelCheckOut(){
        p_back.setVisible(true);
        checkoutPanel.setVisible(false);
        cashSystem.setContentPane(p_back);
    }
    private void purchaseFinish(){
        JPanel finishPanel = new JPanel(new BorderLayout());
        finishPanel.add(new JLabel("Purchase finished."), BorderLayout.NORTH);
        finishPanel.add(new Button(new JButton("New customer"), () -> {
            cashSystem.dispose();
            new ProductGUI(products);
        }), BorderLayout.SOUTH);
        
        checkoutPanel.setVisible(false);
        finishPanel.setVisible(true);
        cashSystem.setContentPane(finishPanel);
    }

    public void previous(){
        p_back.remove(searchList);
        p_back.add(productList);
        b_end.setVisible(false);
        cashSystem.pack();
    }


    private class CartPanel extends JPanel{

        final GridLayout gl = new GridLayout(1, 4);

        JPanel list;
        JPanel head;
        public CartPanel(){
            super();

            list = new JPanel(new GridLayout(0, 1));
            head = new JPanel(new GridLayout(1, 3));
            head.add(new JLabel("Name"));
            
            head.add(new JLabel("Amount"));
            head.add(new JLabel("Price"));

            this.setLayout(new BorderLayout());
            this.add(list, BorderLayout.SOUTH);
            this.add(head, BorderLayout.NORTH);
            updateGUI();
        }

        public JPanel cartList(){
            JPanel cartList = new JPanel(new BorderLayout());
            JPanel list = new JPanel(new GridLayout(0, 1));
            JPanel head = new JPanel(new GridLayout(1, 3));
            head.add(new JLabel("Name"));
            
            head.add(new JLabel("Amount"));
            head.add(new JLabel("Price"));

            cartList.setLayout(new BorderLayout());
            cartList.add(list, BorderLayout.SOUTH);
            cartList.add(head, BorderLayout.NORTH);
            
            for(Product p : customer.getCart().keySet()){
                if(customer.getCart().get(p) > 0){

                    JPanel cartProduct = new JPanel(new GridLayout(1, 3));
                    JLabel pName = new JLabel(p.getName());
                    JLabel pAmount = new JLabel(String.valueOf(customer.getCart().get(p)));
                    JLabel pTotPrice = new JLabel(String.valueOf(customer.getCart().get(p) * p.getPrice()));
                    cartProduct.setLayout(gl);
                    cartProduct.add(pName);
                    cartProduct.add(pAmount);
                    cartProduct.add(pTotPrice);

                    list.add(cartProduct);
                }
                
            }
            return cartList;
        }

        public void updateGUI(){
            list.removeAll();

            for(Product p : customer.getCart().keySet()){
                if(customer.getCart().get(p) > 0){

                    JPanel cartProduct = new JPanel();
                    JLabel pName = new JLabel(p.getName());
                    JLabel pAmount = new JLabel(String.valueOf(customer.getCart().get(p)));
                    JLabel pTotPrice = new JLabel(String.valueOf(customer.getCart().get(p) * p.getPrice()));
                    Button removeB = new Button(new JButton("Remove"), () -> removeFromCart(p));
                
        
                    cartProduct.setLayout(gl);
                    cartProduct.add(pName);
                    cartProduct.add(pAmount);
                    cartProduct.add(pTotPrice);
                    cartProduct.add(removeB);
        
                    list.add(cartProduct);
                }
                
            }
        }
    }
}