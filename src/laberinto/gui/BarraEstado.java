package laberinto.gui;

import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;

// Mensajes de estado inferiores
public final class BarraEstado extends JPanel {
    private static final long serialVersionUID = 1L;

    private final JLabel mensaje = new JLabel(" ");

    public BarraEstado() {
        super(new BorderLayout());
        mensaje.setFont(Tema.FUENTE_TEXTO);
        mensaje.setBorder(BorderFactory.createEmptyBorder(9, 16, 9, 16));
        add(mensaje, BorderLayout.CENTER);
        informar("Listo.");
    }

    public void informar(String texto) {
        pintar("Info", texto, Tema.PRIMARIO_SUAVE, Tema.TEXTO);
    }

    public void exito(String texto) {
        pintar("Listo", texto, Tema.EXITO_SUAVE, Tema.EXITO);
    }

    public void aviso(String texto) {
        pintar("Atención", texto, Tema.AVISO_SUAVE, Tema.AVISO);
    }

    public void error(String texto) {
        pintar("Error", texto, Tema.PELIGRO_SUAVE, Tema.PELIGRO);
    }

    private void pintar(String tipo, String texto, Color fondo, Color frente) {
        setBackground(fondo);
        mensaje.setForeground(frente);
        mensaje.setText("<html><b>" + tipo + ":</b> " + texto + "</html>");
    }
}
