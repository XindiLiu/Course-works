import java.awt.Color;
import java.awt.GridBagLayout;
import javax.swing.*;

public class Block extends JPanel {
	int blocknr = 0;
	JLabel c;

	public Block() {
		this.blocknr = 0;
		setBackground(Color.white);
		c = new JLabel("", SwingConstants.CENTER);
		c.setForeground(Color.white);
		// setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		setLayout(new GridBagLayout());
		add(c);
	}

	public void update(int blocknr) {
		if (this.blocknr == blocknr) {
			return;
		}
		this.blocknr = blocknr;
		switch (blocknr) {
			case 0:
				this.setBackground(Color.white);
				c.setText("");
				break;
			case 'A':
				this.setBackground(Color.blue);
				c.setText("1");
				break;
			case 'a':
				this.setBackground(Color.blue);
				c.setText("");
				break;
			case 'B':
				this.setBackground(Color.red);
				c.setText("2");
				break;
			case 'b':
				this.setBackground(Color.red);
				c.setText("");
				break;
			case '#':
				this.setBackground(Color.black);
				c.setText("");
				break;
			case '+':
				this.setBackground(Color.lightGray);
				c.setText("+");
				break;
		}
		revalidate();
		repaint();
	}
}
