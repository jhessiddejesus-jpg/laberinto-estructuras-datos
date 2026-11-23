package laberinto.gui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

// Explica los colores
public final class Leyenda extends JPanel {
    private static final long serialVersionUID = 1L;
    private static final int MUESTRA = 16;

    private enum Forma { RELLENO, LETRA, LINEA, LINEA_DISCONTINUA, BARRO }

    public Leyenda() {
        super(new GridLayout(0, 3, 8, 4));
        setOpaque(false);
        agregar("Pared", Tema.CELDA_PARED, Forma.RELLENO, "");
        agregar("Camino", Tema.CELDA_CAMINO, Forma.RELLENO, "");
        agregar("Inicio", Tema.CELDA_INICIO, Forma.LETRA, "S");
        agregar("Salida", Tema.CELDA_SALIDA, Forma.LETRA, "E");
        agregar("En la frontera", Tema.CELDA_FRONTERA, Forma.RELLENO, "");
        agregar("Explorada", Tema.CELDA_EXPLORADA, Forma.RELLENO, "");
        agregar("Camino mínimo", Tema.LINEA_SOLUCION, Forma.LINEA, "");
        agregar("Camino pila", Tema.LINEA_PILA, Forma.LINEA_DISCONTINUA, "");
        agregar("Barro (costo)", Tema.CELDA_BARRO, Forma.BARRO, "4");
    }

    private void agregar(String texto, Color color, Forma forma, String marca) {
        JPanel item = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        item.setOpaque(false);
        item.add(new Muestra(color, forma, marca));
        JLabel etiqueta = new JLabel(texto);
        etiqueta.setFont(Tema.FUENTE_PEQUENA);
        etiqueta.setForeground(Tema.TEXTO_SUAVE);
        item.add(etiqueta);
        add(item);
    }

    private static final class Muestra extends JComponent {
        private static final long serialVersionUID = 1L;
        private final Color color;
        private final Forma forma;
        private final String marca;

        private Muestra(Color color, Forma forma, String marca) {
            this.color = color;
            this.forma = forma;
            this.marca = marca;
            setPreferredSize(new Dimension(MUESTRA, MUESTRA));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            Tema.suavizar(g2);
            switch (forma) {
                case LINEA -> {
                    g2.setColor(color);
                    g2.setStroke(new BasicStroke(4f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                    g2.drawLine(2, MUESTRA / 2, MUESTRA - 2, MUESTRA / 2);
                }
                case LINEA_DISCONTINUA -> {
                    g2.setColor(color);
                    g2.setStroke(new BasicStroke(3f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 1f, new float[]{4f, 3f}, 0f));
                    g2.drawLine(1, MUESTRA / 2, MUESTRA - 1, MUESTRA / 2);
                }
                default -> {
                    g2.setColor(color);
                    g2.fillRoundRect(0, 0, MUESTRA - 1, MUESTRA - 1, 5, 5);
                    g2.setColor(Tema.BORDE);
                    g2.drawRoundRect(0, 0, MUESTRA - 1, MUESTRA - 1, 5, 5);
                    if (!marca.isEmpty()) {
                        g2.setColor(Color.WHITE);
                        g2.setFont(Tema.FUENTE_PEQUENA.deriveFont(java.awt.Font.BOLD, 11f));
                        int ancho = g2.getFontMetrics().stringWidth(marca);
                        g2.drawString(marca, (MUESTRA - ancho) / 2, MUESTRA - 4);
                    }
                }
            }
            g2.dispose();
        }
    }
}
