package atmos.app;
import javax.swing.*;

public class AppLauncher {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                // Exibe a janela através do método display() da classe AtmosAppGui
                new AtmosAppGui().display(); // Aqui chamamos display(), que invoca o setVisible(true) no JFrame
            }
        });
    }
}
