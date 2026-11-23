package laberinto.gui;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

import laberinto.estructuras.ArregloDinamico;
import laberinto.servicio.RepositorioSemillas;

// Muestra las cubetas
public final class PanelTablaHash extends JPanel {
    private static final long serialVersionUID = 1L;

    private final RepositorioSemillas repositorio;
    private final Dibujo dibujo = new Dibujo();
    private final JLabel resumen = new JLabel();
    private long semillaResaltada = Long.MIN_VALUE;

    public PanelTablaHash(RepositorioSemillas repositorio) {
        super(new BorderLayout());
        this.repositorio = repositorio;
        setOpaque(false);
        setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        resumen.setFont(Tema.FUENTE_TEXTO);
        resumen.setForeground(Tema.TEXTO);
        resumen.setBorder(BorderFactory.createEmptyBorder(8, 2, 0, 0));

        JPanel contenido = new JPanel(new BorderLayout());
        contenido.setOpaque(false);
        contenido.add(Componentes.desplazable(dibujo), BorderLayout.CENTER);
        contenido.add(resumen, BorderLayout.SOUTH);
        add(Tarjeta.conTitulo("Tabla hash de semillas · " + RepositorioSemillas.NUMERO_CUBETAS + " cubetas",
                "cubeta = semilla mod 5 · las colisiones se encadenan en una lista", contenido), BorderLayout.CENTER);
        actualizar(Long.MIN_VALUE, false);
    }

    public void actualizar(long semilla, boolean reutilizada) {
        semillaResaltada = semilla;
        if (semilla == Long.MIN_VALUE) {
            resumen.setText("Cargue una semilla para guardarla en la tabla.");
        } else {
            int cubeta = repositorio.cubetaDe(semilla);
            resumen.setText("<html>Última petición: <b>" + semilla + "</b> → " + semilla + " mod 5 = <b>cubeta " + cubeta
                    + "</b> · " + (reutilizada ? "<font color='#15803D'><b>reutilizada</b>, no se volvió a construir</font>"
                    : "<b>construida</b> y guardada") + " · matrices construidas: " + repositorio.construcciones() + "</html>");
        }
        dibujo.revalidate();
        dibujo.repaint();
    }

    private final class Dibujo extends JComponent {
        private static final long serialVersionUID = 1L;
        private static final int ALTO_FILA = 56;
        private static final int ANCHO_CUBETA = 104;
        private static final int ANCHO_NODO = 96;
        private static final int SEPARACION = 34;
        private static final int MARGEN = 16;

        @Override
        public Dimension getPreferredSize() {
            int maximo = 1;
            for (int i = 0; i < repositorio.numeroCubetas(); i++) {
                maximo = Math.max(maximo, repositorio.semillasEnCubeta(i).tamanio());
            }
            int ancho = 2 * MARGEN + ANCHO_CUBETA + maximo * (ANCHO_NODO + SEPARACION) + 40;
            return new Dimension(ancho, 2 * MARGEN + repositorio.numeroCubetas() * ALTO_FILA);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            Tema.suavizar(g2);
            g2.setColor(Tema.SUPERFICIE);
            g2.fillRect(0, 0, getWidth(), getHeight());
            for (int cubeta = 0; cubeta < repositorio.numeroCubetas(); cubeta++) {
                int y = MARGEN + cubeta * ALTO_FILA;
                caja(g2, MARGEN, y, ANCHO_CUBETA, "Cubeta " + cubeta, Tema.PRIMARIO_SUAVE, Tema.TEXTO, Tema.BORDE);
                ArregloDinamico<Long> semillas = repositorio.semillasEnCubeta(cubeta);
                int x = MARGEN + ANCHO_CUBETA;
                if (semillas.estaVacio()) {
                    g2.setColor(Tema.TEXTO_SUAVE);
                    g2.setFont(Tema.FUENTE_PEQUENA);
                    g2.drawString("vacía (null)", x + 14, y + 25);
                }
                for (int i = 0; i < semillas.tamanio(); i++) {
                    long semilla = semillas.obtener(i);
                    flecha(g2, x + 4, x + SEPARACION - 4, y + 20);
                    x += SEPARACION;
                    boolean resaltada = semilla == semillaResaltada;
                    String texto = semilla + "  ·  " + repositorio.tamanioGuardado(semilla) + "×" + repositorio.tamanioGuardado(semilla);
                    caja(g2, x, y, ANCHO_NODO + 28, texto,
                            resaltada ? Tema.PRIMARIO : Tema.SUPERFICIE_SUAVE,
                            resaltada ? Color.WHITE : Tema.TEXTO,
                            resaltada ? Tema.PRIMARIO : Tema.BORDE);
                    x += ANCHO_NODO + 28;
                }
            }
            g2.dispose();
        }

        private void caja(Graphics2D g2, int x, int y, int ancho, String texto, Color fondo, Color frente, Color borde) {
            g2.setColor(fondo);
            g2.fillRoundRect(x, y, ancho, 40, 10, 10);
            g2.setColor(borde);
            g2.drawRoundRect(x, y, ancho, 40, 10, 10);
            g2.setColor(frente);
            g2.setFont(Tema.FUENTE_TEXTO_FUERTE);
            FontMetrics medidas = g2.getFontMetrics();
            g2.drawString(texto, x + (ancho - medidas.stringWidth(texto)) / 2, y + 25);
        }

        private void flecha(Graphics2D g2, int desde, int hasta, int y) {
            g2.setColor(Tema.TEXTO_SUAVE);
            g2.setStroke(new BasicStroke(2f));
            g2.drawLine(desde, y, hasta, y);
            g2.fillPolygon(new int[]{hasta, hasta - 7, hasta - 7}, new int[]{y, y - 5, y + 5}, 3);
            g2.setStroke(new BasicStroke(1f));
        }
    }
}
