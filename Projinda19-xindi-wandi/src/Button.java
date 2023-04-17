import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;

public class Button extends JPanel{
    private JButton button;
    private Runnable act;
    public Button(JButton b, Runnable f){
        super();
        this.button = b;
        this.act = f;
        this.add(button);
        button.addActionListener(new AL());
    }

    public Button(){
        super();
    }

    public void setJButton(JButton b){
        this.button = b;
    }

    public JButton getJButton(){
        return this.button;
    }

    public void setAct(Runnable f){
        this.act = f;
    }    


    private class AL implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            act.run();
        }
    }
}
