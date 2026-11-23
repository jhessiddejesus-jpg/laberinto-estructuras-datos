package laberinto;

import java.io.FileOutputStream;
import java.io.FileDescriptor;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import javax.swing.SwingUtilities;

import laberinto.consola.CorridaVerificacion;
import laberinto.gui.VentanaPrincipal;

// Punto de entrada del programa
public final class Main {
    private Main() {
    }

    public static void main(String[] argumentos) {
        if (argumentos.length > 0 && argumentos[0].equals("--consola")) {
            PrintStream salida = new PrintStream(new FileOutputStream(FileDescriptor.out), true, StandardCharsets.UTF_8);
            int fallas = new CorridaVerificacion(salida).ejecutar();
            System.exit(fallas == 0 ? 0 : 1);
        }
        SwingUtilities.invokeLater(() -> new VentanaPrincipal().setVisible(true));
    }
}
