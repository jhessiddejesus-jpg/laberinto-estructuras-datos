package laberinto.gui;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import laberinto.estructuras.ArbolBinarioBusqueda;
import laberinto.estructuras.ArregloDinamico;
import laberinto.modelo.RegistroPartida;
import laberinto.servicio.Ranking;

// Dibuja el árbol ranking
public final class PanelRanking extends JPanel {
    private static final long serialVersionUID = 1L;

    private final Ranking ranking;
    private final DibujoArbol dibujo = new DibujoArbol();
    private final JTextField campoBusqueda = new JTextField("18", 5);
    private final JLabel medidas = new JLabel();
    private final JTextArea detalle = Componentes.areaDeTexto(false);
    private ArbolBinarioBusqueda.Hallazgo<RegistroPartida> hallazgo;

    public PanelRanking(Ranking ranking, Runnable cargarPrueba) {
        super(new BorderLayout(0, 10));
        this.ranking = ranking;
        setOpaque(false);
        setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        JPanel barra = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        barra.setOpaque(false);
        barra.add(Componentes.etiqueta("Buscar pasos"));
        campoBusqueda.setFont(Tema.FUENTE_TEXTO);
        campoBusqueda.setBorder(Componentes.bordeDeCampo());
        campoBusqueda.addActionListener(e -> buscar());
        barra.add(campoBusqueda);
        BotonTema botonBuscar = new BotonTema("Buscar", BotonTema.Estilo.PRIMARIO);
        botonBuscar.addActionListener(e -> buscar());
        barra.add(botonBuscar);
        BotonTema botonPrueba = new BotonTema("Cargar 22 · 18 · 30 · 14 · 25", BotonTema.Estilo.SECUNDARIO);
        botonPrueba.addActionListener(e -> cargarPrueba.run());
        barra.add(botonPrueba);
        BotonTema botonVaciar = new BotonTema("Vaciar", BotonTema.Estilo.SECUNDARIO);
        botonVaciar.addActionListener(e -> {
            ranking.vaciar();
            refrescar();
        });
        barra.add(botonVaciar);

        JPanel arriba = new JPanel(new BorderLayout(0, 8));
        arriba.setOpaque(false);
        arriba.add(barra, BorderLayout.NORTH);
        medidas.setFont(Tema.FUENTE_TEXTO);
        medidas.setForeground(Tema.TEXTO);
        arriba.add(medidas, BorderLayout.SOUTH);

        JPanel centro = new JPanel(new GridLayout(2, 1, 0, 10));
        centro.setOpaque(false);
        centro.add(Tarjeta.conTitulo("Árbol binario de búsqueda", "clave = pasos · menores a la izquierda · iguales a la derecha",
                Componentes.desplazable(dibujo)));
        centro.add(Tarjeta.conTitulo("Inorden = ranking", "de menos pasos a más · los 5 mejores", Componentes.desplazable(detalle)));

        add(arriba, BorderLayout.NORTH);
        add(centro, BorderLayout.CENTER);
        refrescar();
    }

    public void refrescar() {
        hallazgo = null;
        actualizarTextos();
        dibujo.revalidate();
        dibujo.repaint();
    }

    public void buscar() {
        try {
            hallazgo = ranking.buscar(Integer.parseInt(campoBusqueda.getText().trim()));
        } catch (NumberFormatException error) {
            hallazgo = null;
            detalle.setText("Escriba un número entero de pasos.");
            return;
        }
        actualizarTextos();
        dibujo.repaint();
    }

    private void actualizarTextos() {
        if (ranking.estaVacio()) {
            medidas.setText("El ranking está vacío. Resuelva un laberinto o cargue los registros de prueba.");
            detalle.setText("");
            return;
        }
        Ranking.Estadisticas e = ranking.estadisticas();
        medidas.setText("<html>Nodos <b>" + e.nodos() + "</b> · peso <b>" + e.peso() + "</b> (sin la raíz) · altura <b>"
                + e.altura() + "</b> · hojas <b>" + e.hojas() + "</b> · grado <b>" + e.grado() + "</b> · completo <b>"
                + (e.completo() ? "sí" : "no") + "</b></html>");

        StringBuilder texto = new StringBuilder("Inorden: ").append(ranking.pasosEnInorden().unir(", ")).append("\n\n");
        ArregloDinamico<RegistroPartida> mejores = ranking.mejores();
        for (int i = 0; i < mejores.tamanio(); i++) {
            texto.append(i + 1).append(". ").append(mejores.obtener(i)).append("\n");
        }
        if (hallazgo != null) {
            texto.append("\n");
            if (hallazgo.encontrado()) {
                texto.append("Búsqueda: camino ").append(hallazgo.caminoComoTexto()).append(" · nivel ")
                        .append(hallazgo.nivel()).append("\nRegistro: ").append(hallazgo.nodo().valor());
            } else {
                texto.append("Búsqueda: no está. Se recorrió ").append(hallazgo.caminoComoTexto());
            }
        }
        detalle.setText(texto.toString());
        detalle.setCaretPosition(0);
    }

    private final class DibujoArbol extends JComponent {
        private static final long serialVersionUID = 1L;
        private static final int DIAMETRO = 42;
        private static final int SEPARACION_X = 58;
        private static final int SEPARACION_Y = 66;
        private static final int MARGEN_IZQUIERDO = 74;
        private static final int MARGEN_SUPERIOR = 30;

        @Override
        public Dimension getPreferredSize() {
            int nodos = Math.max(1, ranking.estadisticas().nodos());
            int niveles = Math.max(1, ranking.estadisticas().altura() + 1);
            return new Dimension(MARGEN_IZQUIERDO + nodos * SEPARACION_X + 20, MARGEN_SUPERIOR + niveles * SEPARACION_Y + 10);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            Tema.suavizar(g2);
            g2.setColor(Tema.SUPERFICIE);
            g2.fillRect(0, 0, getWidth(), getHeight());
            if (ranking.estaVacio()) {
                g2.dispose();
                return;
            }
            dibujarNiveles(g2);
            colocar(g2, ranking.raiz(), 0, new int[]{0}, true);
            colocar(g2, ranking.raiz(), 0, new int[]{0}, false);
            g2.dispose();
        }

        private void dibujarNiveles(Graphics2D g2) {
            g2.setFont(Tema.FUENTE_PEQUENA);
            for (int nivel = 0; nivel <= ranking.estadisticas().altura(); nivel++) {
                g2.setColor(Tema.TEXTO_SUAVE);
                g2.drawString("nivel " + nivel, 10, centroY(nivel) + 4);
                g2.setColor(Tema.BORDE_SUAVE);
                g2.drawLine(60, centroY(nivel), getWidth() - 10, centroY(nivel));
            }
        }

        private int colocar(Graphics2D g2, ArbolBinarioBusqueda.Nodo<RegistroPartida> nodo, int profundidad,
                            int[] contador, boolean soloAristas) {
            if (nodo == null) {
                return -1;
            }
            int xIzquierdo = colocar(g2, nodo.izquierdo(), profundidad + 1, contador, soloAristas);
            int x = MARGEN_IZQUIERDO + contador[0]++ * SEPARACION_X + DIAMETRO / 2;
            int xDerecho = colocar(g2, nodo.derecho(), profundidad + 1, contador, soloAristas);
            int y = centroY(profundidad);
            if (soloAristas) {
                arista(g2, x, y, xIzquierdo, centroY(profundidad + 1), enBusqueda(nodo) && enBusqueda(nodo.izquierdo()));
                arista(g2, x, y, xDerecho, centroY(profundidad + 1), enBusqueda(nodo) && enBusqueda(nodo.derecho()));
            } else {
                circulo(g2, nodo, x, y);
            }
            return x;
        }

        private void arista(Graphics2D g2, int x1, int y1, int x2, int y2, boolean resaltada) {
            if (x2 < 0) {
                return;
            }
            g2.setColor(resaltada ? Tema.PRIMARIO : Tema.BORDE);
            g2.setStroke(new BasicStroke(resaltada ? 3f : 2f));
            g2.drawLine(x1, y1, x2, y2);
        }

        private void circulo(Graphics2D g2, ArbolBinarioBusqueda.Nodo<RegistroPartida> nodo, int x, int y) {
            boolean encontrado = hallazgo != null && hallazgo.encontrado() && hallazgo.nodo() == nodo;
            boolean enCamino = enBusqueda(nodo);
            Color fondo = encontrado ? Tema.ACENTO : (enCamino ? Tema.PRIMARIO : Tema.PRIMARIO_SUAVE);
            Color frente = encontrado || enCamino ? Color.WHITE : Tema.TEXTO;
            g2.setColor(fondo);
            g2.fillOval(x - DIAMETRO / 2, y - DIAMETRO / 2, DIAMETRO, DIAMETRO);
            g2.setColor(encontrado ? Tema.ACENTO : Tema.PRIMARIO);
            g2.setStroke(new BasicStroke(2f));
            g2.drawOval(x - DIAMETRO / 2, y - DIAMETRO / 2, DIAMETRO, DIAMETRO);
            g2.setColor(frente);
            g2.setFont(Tema.FUENTE_SUBTITULO);
            FontMetrics medidasFuente = g2.getFontMetrics();
            String clave = String.valueOf(nodo.clave());
            g2.drawString(clave, x - medidasFuente.stringWidth(clave) / 2, y + medidasFuente.getAscent() / 2 - 2);
        }

        private boolean enBusqueda(ArbolBinarioBusqueda.Nodo<RegistroPartida> nodo) {
            return nodo != null && hallazgo != null && hallazgo.recorrido().contiene(nodo);
        }

        private int centroY(int profundidad) {
            return MARGEN_SUPERIOR + profundidad * SEPARACION_Y;
        }
    }
}
