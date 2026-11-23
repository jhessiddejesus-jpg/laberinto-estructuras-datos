package laberinto.gui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.function.IntConsumer;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.ToolTipManager;

import laberinto.estructuras.ArregloDinamico;
import laberinto.estructuras.ListaDoble;
import laberinto.modelo.Coordenada;
import laberinto.modelo.Laberinto;
import laberinto.modelo.PasoExploracion;
import laberinto.modelo.ResultadoBusqueda;
import laberinto.modelo.TipoCelda;

// Dibuja el tablero animado
public final class PanelLaberinto extends JPanel {
    private static final long serialVersionUID = 1L;

    public interface OyenteCeldas {
        void celdaSeleccionada(Coordenada celda);

        void alternarBarro(Coordenada celda);
    }

    private static final int MARGEN_EJES = 22;
    private static final int RELLENO = 10;
    private static final int SIN_ESTADO = 0;
    private static final int EN_FRONTERA = 1;
    private static final int EXPLORADA = 2;

    private Laberinto laberinto;
    private ArregloDinamico<PasoExploracion> exploracion = new ArregloDinamico<>();
    private int eventosVisibles;
    private ListaDoble<Coordenada> caminoPrincipal;
    private ListaDoble<Coordenada> caminoPila;
    private boolean caminosVisibles;
    private ArregloDinamico<Coordenada> aperturas;
    private int aperturasVisibles;
    private Coordenada celdaBajoCursor;
    private Coordenada celdaResaltada;
    private final Animador animador = new Animador();
    private OyenteCeldas oyente;

    private int origenX;
    private int origenY;
    private int lado;

    public PanelLaberinto() {
        setOpaque(false);
        setPreferredSize(new Dimension(520, 520));
        ToolTipManager.sharedInstance().registerComponent(this);
        MouseAdapter raton = new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evento) {
                Coordenada celda = celdaEn(evento.getX(), evento.getY());
                if (celda == null || oyente == null) {
                    return;
                }
                if (SwingUtilities.isRightMouseButton(evento)) {
                    oyente.alternarBarro(celda);
                } else {
                    oyente.celdaSeleccionada(celda);
                }
            }

            @Override
            public void mouseMoved(MouseEvent evento) {
                Coordenada celda = celdaEn(evento.getX(), evento.getY());
                if (celda == null ? celdaBajoCursor != null : !celda.equals(celdaBajoCursor)) {
                    celdaBajoCursor = celda;
                    repaint();
                }
            }

            @Override
            public void mouseExited(MouseEvent evento) {
                celdaBajoCursor = null;
                repaint();
            }
        };
        addMouseListener(raton);
        addMouseMotionListener(raton);
    }

    public void setOyente(OyenteCeldas oyente) {
        this.oyente = oyente;
    }

    public void mostrarLaberinto(Laberinto nuevo) {
        detenerAnimacion();
        laberinto = nuevo;
        limpiarCapas();
        repaint();
    }

    public void limpiarRecorrido() {
        detenerAnimacion();
        limpiarCapas();
        repaint();
    }

    public void mostrarResultado(ResultadoBusqueda resultado, boolean animar, int velocidad, Runnable alTerminar) {
        detenerAnimacion();
        limpiarCapas();
        exploracion = resultado.exploracion();
        boolean esPila = "pila".equals(resultado.estructura());
        if (esPila) {
            caminoPila = resultado.camino();
        } else {
            caminoPrincipal = resultado.camino();
        }
        if (!animar) {
            eventosVisibles = exploracion.tamanio();
            caminosVisibles = true;
            repaint();
            alTerminar.run();
            return;
        }
        animar(exploracion.tamanio(), velocidad, cantidad -> eventosVisibles = cantidad, () -> {
            caminosVisibles = true;
            repaint();
            alTerminar.run();
        });
    }

    public void mostrarComparacion(ResultadoBusqueda cola, ResultadoBusqueda pila) {
        detenerAnimacion();
        limpiarCapas();
        caminoPrincipal = cola.camino();
        caminoPila = pila.camino();
        caminosVisibles = true;
        repaint();
    }

    public void animarGeneracion(Laberinto nuevo, ArregloDinamico<Coordenada> orden, int velocidad, Runnable alTerminar) {
        detenerAnimacion();
        laberinto = nuevo;
        limpiarCapas();
        aperturas = orden;
        aperturasVisibles = 0;
        animar(orden.tamanio(), velocidad, cantidad -> aperturasVisibles = cantidad, () -> {
            aperturas = null;
            repaint();
            alTerminar.run();
        });
    }

    public void resaltar(Coordenada celda) {
        celdaResaltada = celda;
        repaint();
    }

    public void detenerAnimacion() {
        animador.detener();
    }

    private void limpiarCapas() {
        exploracion = new ArregloDinamico<>();
        eventosVisibles = 0;
        caminoPrincipal = null;
        caminoPila = null;
        caminosVisibles = false;
        aperturas = null;
        celdaResaltada = null;
    }

    private void animar(int total, int velocidad, IntConsumer alAvanzar, Runnable alTerminar) {
        animador.iniciar(total, velocidad, cantidad -> {
            alAvanzar.accept(cantidad);
            repaint();
        }, alTerminar);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (laberinto == null) {
            return;
        }
        Graphics2D g2 = (Graphics2D) g.create();
        Tema.suavizar(g2);
        calcularGeometria();
        boolean[][] abiertas = celdasAbiertas();
        int[][] estados = estadosDeExploracion();

        dibujarEjes(g2);
        dibujarCeldas(g2, abiertas, estados);
        if (caminosVisibles) {
            dibujarCamino(g2, caminoPila, Tema.LINEA_PILA, true);
            dibujarCamino(g2, caminoPrincipal, Tema.LINEA_SOLUCION, false);
        }
        dibujarInicioYSalida(g2, abiertas);
        dibujarMarco(g2, celdaResaltada, Tema.ACENTO, 3f);
        dibujarMarco(g2, celdaBajoCursor, Tema.PRIMARIO, 2f);
        g2.dispose();
    }

    private void calcularGeometria() {
        int n = laberinto.tamanio();
        int disponible = Math.min(getWidth() - MARGEN_EJES - 2 * RELLENO, getHeight() - MARGEN_EJES - 2 * RELLENO);
        lado = Math.max(6, disponible / n);
        int anchoTablero = lado * n;
        origenX = MARGEN_EJES + (getWidth() - MARGEN_EJES - anchoTablero) / 2;
        origenY = MARGEN_EJES + (getHeight() - MARGEN_EJES - anchoTablero) / 2;
    }

    private boolean[][] celdasAbiertas() {
        int n = laberinto.tamanio();
        boolean[][] abiertas = new boolean[n][n];
        if (aperturas == null) {
            for (int fila = 0; fila < n; fila++) {
                for (int columna = 0; columna < n; columna++) {
                    abiertas[fila][columna] = laberinto.esTransitable(new Coordenada(fila, columna));
                }
            }
            return abiertas;
        }
        for (int i = 0; i < aperturasVisibles; i++) {
            Coordenada c = aperturas.obtener(i);
            abiertas[c.fila()][c.columna()] = true;
        }
        abiertas[0][0] = true;
        return abiertas;
    }

    private int[][] estadosDeExploracion() {
        int n = laberinto.tamanio();
        int[][] estados = new int[n][n];
        for (int i = 0; i < eventosVisibles; i++) {
            PasoExploracion paso = exploracion.obtener(i);
            Coordenada c = paso.celda();
            if (paso.tipo() == PasoExploracion.Tipo.EXPANDIDA) {
                estados[c.fila()][c.columna()] = EXPLORADA;
            } else if (estados[c.fila()][c.columna()] == SIN_ESTADO) {
                estados[c.fila()][c.columna()] = EN_FRONTERA;
            }
        }
        return estados;
    }

    private void dibujarEjes(Graphics2D g2) {
        int n = laberinto.tamanio();
        g2.setFont(Tema.FUENTE_PEQUENA.deriveFont(Math.min(12f, Math.max(9f, lado * 0.4f))));
        g2.setColor(Tema.TEXTO_SUAVE);
        FontMetrics medidas = g2.getFontMetrics();
        for (int i = 0; i < n; i++) {
            String numero = String.valueOf(i);
            int centro = i * lado + lado / 2;
            g2.drawString(numero, origenX + centro - medidas.stringWidth(numero) / 2, origenY - 6);
            g2.drawString(numero, origenX - 6 - medidas.stringWidth(numero), origenY + centro + medidas.getAscent() / 2 - 1);
        }
    }

    private void dibujarCeldas(Graphics2D g2, boolean[][] abiertas, int[][] estados) {
        int n = laberinto.tamanio();
        for (int fila = 0; fila < n; fila++) {
            for (int columna = 0; columna < n; columna++) {
                Coordenada c = new Coordenada(fila, columna);
                int x = origenX + columna * lado;
                int y = origenY + fila * lado;
                if (!abiertas[fila][columna]) {
                    g2.setColor(Tema.CELDA_PARED);
                    g2.fillRect(x, y, lado, lado);
                    continue;
                }
                boolean barro = laberinto.esBarro(c);
                g2.setColor(barro ? Tema.CELDA_BARRO : Tema.CELDA_CAMINO);
                g2.fillRect(x, y, lado, lado);
                dibujarEstado(g2, estados[fila][columna], barro, x, y);
                if (caminosVisibles && enAlgunCamino(c) && !barro) {
                    g2.setColor(Tema.CELDA_SOLUCION);
                    g2.fillRect(x, y, lado, lado);
                }
                g2.setColor(Tema.CELDA_REJILLA);
                g2.drawRect(x, y, lado, lado);
                if (barro) {
                    dibujarCostoDeBarro(g2, c, x, y);
                }
            }
        }
    }

    private void dibujarEstado(Graphics2D g2, int estado, boolean barro, int x, int y) {
        if (estado == SIN_ESTADO) {
            return;
        }
        Color color = estado == EXPLORADA ? Tema.CELDA_EXPLORADA : Tema.CELDA_FRONTERA;
        if (barro) {
            Stroke anterior = g2.getStroke();
            g2.setStroke(new BasicStroke(Math.max(2f, lado / 9f)));
            g2.setColor(color);
            int borde = Math.max(2, lado / 10);
            g2.drawRect(x + borde, y + borde, lado - 2 * borde, lado - 2 * borde);
            g2.setStroke(anterior);
        } else {
            g2.setColor(color);
            g2.fillRect(x, y, lado, lado);
        }
    }

    private void dibujarCostoDeBarro(Graphics2D g2, Coordenada c, int x, int y) {
        g2.setColor(Color.WHITE);
        g2.setFont(Tema.FUENTE_TEXTO_FUERTE.deriveFont(Math.max(9f, lado * 0.36f)));
        FontMetrics medidas = g2.getFontMetrics();
        String costo = String.valueOf(laberinto.costoDeEntrar(c));
        g2.drawString(costo, x + lado - medidas.stringWidth(costo) - Math.max(2, lado / 8), y + medidas.getAscent());
    }

    private boolean enAlgunCamino(Coordenada c) {
        return (caminoPrincipal != null && caminoPrincipal.contiene(c)) || (caminoPila != null && caminoPila.contiene(c));
    }

    private void dibujarCamino(Graphics2D g2, ListaDoble<Coordenada> camino, Color color, boolean discontinuo) {
        if (camino == null || camino.tamanio() < 2) {
            return;
        }
        float grosor = Math.max(2f, lado / (discontinuo ? 6f : 4.5f));
        int desplazamiento = discontinuo && caminoPrincipal != null ? lado / 6 : 0;
        Stroke trazo = discontinuo
                ? new BasicStroke(grosor, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 1f, new float[]{grosor * 1.6f, grosor * 1.4f}, 0f)
                : new BasicStroke(grosor, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
        g2.setStroke(trazo);
        g2.setColor(color);
        int[] anterior = {-1, -1};
        camino.recorrerIda(c -> {
            int x = origenX + c.columna() * lado + lado / 2 + desplazamiento;
            int y = origenY + c.fila() * lado + lado / 2 + desplazamiento;
            if (anterior[0] >= 0) {
                g2.drawLine(anterior[0], anterior[1], x, y);
            }
            anterior[0] = x;
            anterior[1] = y;
        });
        g2.setStroke(new BasicStroke(1f));
    }

    private void dibujarInicioYSalida(Graphics2D g2, boolean[][] abiertas) {
        dibujarInsigniaDeCelda(g2, laberinto.inicio(), Tema.CELDA_INICIO, TipoCelda.INICIO.simbolo(), abiertas);
        dibujarInsigniaDeCelda(g2, laberinto.salida(), Tema.CELDA_SALIDA, TipoCelda.SALIDA.simbolo(), abiertas);
    }

    private void dibujarInsigniaDeCelda(Graphics2D g2, Coordenada c, Color color, char letra, boolean[][] abiertas) {
        if (!abiertas[c.fila()][c.columna()]) {
            return;
        }
        int margen = Math.max(2, lado / 9);
        int x = origenX + c.columna() * lado + margen;
        int y = origenY + c.fila() * lado + margen;
        int tamanio = lado - 2 * margen;
        g2.setColor(color);
        g2.fillRoundRect(x, y, tamanio, tamanio, tamanio / 3, tamanio / 3);
        g2.setColor(Color.WHITE);
        g2.setFont(Tema.FUENTE_TEXTO_FUERTE.deriveFont(Font.BOLD, Math.max(10f, lado * 0.5f)));
        FontMetrics medidas = g2.getFontMetrics();
        String texto = String.valueOf(letra);
        g2.drawString(texto, x + (tamanio - medidas.stringWidth(texto)) / 2,
                y + (tamanio - medidas.getHeight()) / 2 + medidas.getAscent());
    }

    private void dibujarMarco(Graphics2D g2, Coordenada c, Color color, float grosor) {
        if (c == null || !laberinto.estaDentro(c)) {
            return;
        }
        g2.setColor(color);
        g2.setStroke(new BasicStroke(grosor));
        g2.drawRect(origenX + c.columna() * lado + 1, origenY + c.fila() * lado + 1, lado - 2, lado - 2);
        g2.setStroke(new BasicStroke(1f));
    }

    private Coordenada celdaEn(int x, int y) {
        if (laberinto == null || lado == 0) {
            return null;
        }
        int columna = Math.floorDiv(x - origenX, lado);
        int fila = Math.floorDiv(y - origenY, lado);
        Coordenada celda = new Coordenada(fila, columna);
        return laberinto.estaDentro(celda) ? celda : null;
    }

    @Override
    public String getToolTipText(MouseEvent evento) {
        Coordenada celda = celdaEn(evento.getX(), evento.getY());
        if (celda == null) {
            return null;
        }
        String tipo = laberinto.tipoEn(celda) == TipoCelda.PARED ? "pared" : "transitable";
        String costo = laberinto.esTransitable(celda) ? " · cuesta " + laberinto.costoDeEntrar(celda) : "";
        return "<html><b>" + celda + "</b> " + tipo + costo
                + "<br>Clic: probar el árbol de decisión<br>Clic derecho: poner o quitar barro</html>";
    }
}
