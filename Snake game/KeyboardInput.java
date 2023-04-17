import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.function.Consumer;

public class KeyboardInput extends KeyAdapter {
	Consumer<Integer> f;

	public KeyboardInput(Consumer<Integer> f) {
		this.f = f;
	}

	public void keyPressed(KeyEvent e) {
		switch (e.getKeyCode()) {
			case KeyEvent.VK_RIGHT:
				f.accept(0);
				break;
			case KeyEvent.VK_UP:
				f.accept(1);
				break;

			case KeyEvent.VK_LEFT:
				f.accept(2);
				break;

			case KeyEvent.VK_DOWN:
				f.accept(3);
				break;
		}
	}

}
