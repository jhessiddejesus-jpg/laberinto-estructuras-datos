package laberinto.gui;

import java.awt.Color;
import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.Border;

// Fábrica de componentes comunes
public final class Componentes {
    private Componentes() {
    }

    public static JLabel etiqueta(String texto) {
        JLabel etiqueta = new JLabel(texto);
        etiqueta.setFont(Tema.FUENTE_TEXTO_FUERTE);
        etiqueta.setForeground(Tema.TEXTO);
        etiqueta.setAlignmentX(JComponent.LEFT_ALIGNMENT);
        return etiqueta;
    }

    public static JLabel ayuda(String texto) {
        JLabel ayuda = new JLabel("<html><body style='width:190px'>" + texto + "</body></html>");
        ayuda.setFont(Tema.FUENTE_PEQUENA);
        ayuda.setForeground(Tema.TEXTO_SUAVE);
        ayuda.setAlignmentX(JComponent.LEFT_ALIGNMENT);
        return ayuda;
    }

    public static JLabel seccion(String numero, String titulo) {
        JLabel seccion = new JLabel(numero + "  ·  " + titulo.toUpperCase());
        seccion.setFont(Tema.FUENTE_SECCION);
        seccion.setForeground(Tema.PRIMARIO);
        seccion.setAlignmentX(JComponent.LEFT_ALIGNMENT);
        seccion.setBorder(BorderFactory.createEmptyBorder(14, 0, 6, 0));
        return seccion;
    }

    public static JTextArea areaDeTexto(boolean monoespaciada) {
        JTextArea area = new JTextArea();
        area.setEditable(false);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setFont(monoespaciada ? Tema.FUENTE_MONO : Tema.FUENTE_TEXTO);
        area.setForeground(Tema.TEXTO);
        area.setBackground(Tema.SUPERFICIE_SUAVE);
        area.setBorder(BorderFactory.createEmptyBorder(10, 12, 10, 12));
        return area;
    }

    public static JScrollPane desplazable(JComponent contenido) {
        JScrollPane scroll = new JScrollPane(contenido);
        scroll.setBorder(BorderFactory.createLineBorder(Tema.BORDE_SUAVE));
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        scroll.getHorizontalScrollBar().setUnitIncrement(16);
        scroll.getViewport().setBackground(Tema.SUPERFICIE);
        return scroll;
    }

    public static Border bordeDeCampo() {
        return BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Tema.BORDE, 1, true),
                BorderFactory.createEmptyBorder(6, 8, 6, 8));
    }

    public static <T extends JComponent> T altoFijo(T componente) {
        componente.setMaximumSize(new Dimension(Integer.MAX_VALUE, componente.getPreferredSize().height));
        componente.setAlignmentX(JComponent.LEFT_ALIGNMENT);
        return componente;
    }

    public static JLabel insignia(String texto, Color fondo, Color frente) {
        JLabel insignia = new JLabel(texto);
        insignia.setOpaque(true);
        insignia.setBackground(fondo);
        insignia.setForeground(frente);
        insignia.setFont(Tema.FUENTE_PEQUENA.deriveFont(java.awt.Font.BOLD));
        insignia.setBorder(BorderFactory.createEmptyBorder(3, 8, 3, 8));
        return insignia;
    }
}
