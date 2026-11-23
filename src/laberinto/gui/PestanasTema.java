package laberinto.gui;

import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Rectangle;

import javax.swing.plaf.basic.BasicTabbedPaneUI;

// Dibujo propio de pestañas
final class PestanasTema extends BasicTabbedPaneUI {
    private static final int GROSOR_BARRA = 3;

    @Override
    protected void installDefaults() {
        super.installDefaults();
        tabInsets = new Insets(8, 14, 8, 14);
        selectedTabPadInsets = new Insets(0, 0, 0, 0);
        tabAreaInsets = new Insets(0, 4, 0, 4);
        contentBorderInsets = new Insets(1, 0, 0, 0);
        tabPane.setBackground(Tema.FONDO);
        tabPane.setOpaque(true);
    }

    @Override
    protected void paintTabBackground(Graphics g, int ubicacion, int indice, int x, int y, int ancho, int alto,
                                      boolean seleccionada) {
        if (seleccionada) {
            Graphics2D g2 = (Graphics2D) g.create();
            Tema.suavizar(g2);
            g2.setColor(Tema.SUPERFICIE);
            g2.fillRoundRect(x, y + 2, ancho, alto + 8, 10, 10);
            g2.dispose();
        }
    }

    @Override
    protected void paintTabBorder(Graphics g, int ubicacion, int indice, int x, int y, int ancho, int alto,
                                  boolean seleccionada) {
        if (seleccionada) {
            g.setColor(Tema.PRIMARIO);
            g.fillRect(x + 6, y + alto - GROSOR_BARRA, ancho - 12, GROSOR_BARRA);
        }
    }

    @Override
    protected void paintText(Graphics g, int ubicacion, Font fuente, FontMetrics medidas, int indice, String titulo,
                             Rectangle zonaTexto, boolean seleccionada) {
        Graphics2D g2 = (Graphics2D) g.create();
        Tema.suavizar(g2);
        g2.setFont(fuente);
        g2.setColor(seleccionada ? Tema.PRIMARIO : Tema.TEXTO_SUAVE);
        g2.drawString(titulo, zonaTexto.x, zonaTexto.y + medidas.getAscent());
        g2.dispose();
    }

    @Override
    protected void paintFocusIndicator(Graphics g, int ubicacion, Rectangle[] zonas, int indice, Rectangle zonaIcono,
                                       Rectangle zonaTexto, boolean seleccionada) {
    }

    @Override
    protected void paintContentBorder(Graphics g, int ubicacion, int indiceSeleccionado) {
        int y = calculateTabAreaHeight(ubicacion, runCount, maxTabHeight);
        g.setColor(Tema.BORDE);
        g.fillRect(0, y - 1, tabPane.getWidth(), 1);
    }
}
