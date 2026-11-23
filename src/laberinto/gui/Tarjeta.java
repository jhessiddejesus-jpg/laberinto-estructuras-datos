package laberinto.gui;

import java.awt.BorderLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

// Contenedor con borde redondeado
public class Tarjeta extends JPanel {
    private static final long serialVersionUID = 1L;

    public Tarjeta() {
        super(new BorderLayout(0, Tema.ESPACIO));
        setOpaque(false);
        setBorder(BorderFactory.createEmptyBorder(14, 16, 14, 16));
    }

    public static Tarjeta conTitulo(String titulo, String ayuda, JComponent contenido) {
        Tarjeta tarjeta = new Tarjeta();
        JPanel encabezado = new JPanel();
        encabezado.setOpaque(false);
        encabezado.setLayout(new BoxLayout(encabezado, BoxLayout.Y_AXIS));
        JLabel etiquetaTitulo = new JLabel(titulo);
        etiquetaTitulo.setFont(Tema.FUENTE_SUBTITULO);
        etiquetaTitulo.setForeground(Tema.TEXTO);
        encabezado.add(etiquetaTitulo);
        if (ayuda != null && !ayuda.isEmpty()) {
            JLabel etiquetaAyuda = new JLabel(ayuda);
            etiquetaAyuda.setFont(Tema.FUENTE_PEQUENA);
            etiquetaAyuda.setForeground(Tema.TEXTO_SUAVE);
            encabezado.add(etiquetaAyuda);
        }
        tarjeta.add(encabezado, BorderLayout.NORTH);
        tarjeta.add(contenido, BorderLayout.CENTER);
        return tarjeta;
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        Tema.suavizar(g2);
        g2.setColor(Tema.SUPERFICIE);
        g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, Tema.RADIO + 4, Tema.RADIO + 4);
        g2.setColor(Tema.BORDE);
        g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, Tema.RADIO + 4, Tema.RADIO + 4);
        g2.dispose();
        super.paintComponent(g);
    }
}
