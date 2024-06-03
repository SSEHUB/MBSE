package gui.windows;

import java.awt.Graphics;
import java.awt.Image;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JWindow;

/**
 * A startup screen.
 * 
 * @author SSE
 */
public class StartUpScreen extends JWindow {

    private static final long serialVersionUID = -464690585875344574L;
    private Image splashImage;

    /**
     * Creates the startup splash window.
     */
    public StartUpScreen() {
        setSize(400, 400);
        setLocationRelativeTo(null);
        setVisible(true);

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            dispose();
        }
        dispose();
    }

    @Override
    public void paint(Graphics g) {
        try {
            splashImage = ImageIO.read(getClass().getResource("../../elevator1.gif"));
            g.drawImage(splashImage, 0, 0, this);
        } catch (IOException e) {
        }
    }

}
