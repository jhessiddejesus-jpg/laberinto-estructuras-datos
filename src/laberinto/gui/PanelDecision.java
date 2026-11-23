package laberinto.gui;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.SpinnerNumberModel;

import laberinto.decision.Accion;
import laberinto.decision.ArbolDecision;
import laberinto.decision.NodoDecision;
import laberinto.estructuras.ArregloDinamico;
import laberinto.modelo.Coordenada;
import laberinto.servicio.JuegoLaberinto;

// Dibuja el árbol decisión
public final class PanelDecision extends JPanel {
    private static final long serialVersionUID = 1L;

    private final JuegoLaberinto juego;
    private final DibujoDecision dibujo = new DibujoDecision();
    private final JTextArea explicacion = Componentes.areaDeTexto(false);
    private final SpinnerNumberModel modeloFila = new SpinnerNumberModel(1, -1, 21, 1);
    private final SpinnerNumberModel modeloColumna = new SpinnerNumberModel(0, -1, 21, 1);
    private ArbolDecision.Decision decision;

    public PanelDecision(JuegoLaberinto juego, java.util.function.Consumer<Coordenada> alProbar) {
        super(new BorderLayout(0, 10));
        this.juego = juego;
        setOpaque(false);
        setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        JPanel barra = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        barra.setOpaque(false);
        barra.add(Componentes.etiqueta("Fila"));
        barra.add(new JSpinner(modeloFila));
        barra.add(Componentes.etiqueta("Columna"));
        barra.add(new JSpinner(modeloColumna));
        BotonTema botonProbar = new BotonTema("Probar celda", BotonTema.Estilo.PRIMARIO);
        botonProbar.addActionListener(e -> alProbar.accept(
                new Coordenada((Integer) modeloFila.getValue(), (Integer) modeloColumna.getValue())));
        barra.add(botonProbar);
        BotonTema botonPared = new BotonTema("(0,1)", BotonTema.Estilo.SECUNDARIO);
        botonPared.addActionListener(e -> alProbar.accept(new Coordenada(0, 1)));
        barra.add(botonPared);
        BotonTema botonNueva = new BotonTema("(1,0)", BotonTema.Estilo.SECUNDARIO);
        botonNueva.addActionListener(e -> alProbar.accept(new Coordenada(1, 0)));
        barra.add(botonNueva);

        add(barra, BorderLayout.NORTH);
        add(Tarjeta.conTitulo("Árbol binario de decisión", "Se aplica a cada vecina; es el mismo para la cola y la pila",
                Componentes.desplazable(dibujo)), BorderLayout.CENTER);
        explicacion.setRows(4);
        add(Tarjeta.conTitulo("Resultado", "La celda se evalúa como vecina del inicio (solo S está marcada)", explicacion),
                BorderLayout.SOUTH);
        explicacion.setText("Pruebe (0,1) y (1,0), o haga clic sobre cualquier celda del laberinto.");
    }

    public void probar(Coordenada celda) {
        decision = juego.probarDecision(celda);
        modeloFila.setValue(celda.fila());
        modeloColumna.setValue(celda.columna());
        explicacion.setText(explicar(celda, decision));
        dibujo.repaint();
    }

    public void probarCeldasDelLaboratorio() {
        ArbolDecision.Decision pared = juego.probarDecision(new Coordenada(0, 1));
        probar(new Coordenada(1, 0));
        explicacion.setText(explicar(new Coordenada(0, 1), pared) + "\n" + explicacion.getText()
                + "\n→ Reciben acciones distintas: el árbol sí está decidiendo.");
    }

    private static String explicar(Coordenada celda, ArbolDecision.Decision decision) {
        StringBuilder texto = new StringBuilder("Celda ").append(celda).append(": ");
        for (int i = 0; i < decision.respuestas().tamanio(); i++) {
            texto.append(decision.ruta().obtener(i).texto()).append(" ").append(decision.respuestas().obtener(i) ? "sí" : "no")
                    .append(" → ");
        }
        if (decision.accion() == Accion.AGREGAR_A_FRONTERA) {
            texto.append(Accion.AGREGAR_A_FRONTERA.descripcionPara("encolar")).append(" (cola) / ")
                    .append(Accion.AGREGAR_A_FRONTERA.descripcionPara("apilar")).append(" (pila)");
        } else {
            texto.append(decision.accion().descripcion());
        }
        return texto.toString();
    }

    private final class DibujoDecision extends JComponent {
        private static final long serialVersionUID = 1L;
        private static final int ANCHO_MINIMO_CAJA = 200;
        private static final int ALTO_CAJA = 48;
        private static final int SEPARACION_FILAS = 86;
        private static final int MARGEN = 16;
        private static final int ESPACIO_HORIZONTAL = 56;

        @Override
        public Dimension getPreferredSize() {
            int filas = contarPreguntas() + 1;
            return new Dimension(2 * MARGEN + 2 * ANCHO_MINIMO_CAJA + ESPACIO_HORIZONTAL,
                    2 * MARGEN + (filas - 1) * SEPARACION_FILAS + ALTO_CAJA);
        }

        private int anchoCaja() {
            return Math.max(ANCHO_MINIMO_CAJA, (getWidth() - 2 * MARGEN - ESPACIO_HORIZONTAL) / 2);
        }

        private int contarPreguntas() {
            int preguntas = 0;
            for (NodoDecision nodo = juego.arbolDecision().raiz(); !nodo.esHoja(); nodo = nodo.hijoNo()) {
                preguntas++;
            }
            return preguntas;
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            Tema.suavizar(g2);
            g2.setColor(Tema.SUPERFICIE);
            g2.fillRect(0, 0, getWidth(), getHeight());

            int ancho = anchoCaja();
            NodoDecision nodo = juego.arbolDecision().raiz();
            int fila = 0;
            int xHoja = MARGEN + ancho + ESPACIO_HORIZONTAL;
            while (!nodo.esHoja()) {
                int y = MARGEN + fila * SEPARACION_FILAS;
                boolean tomoSi = enRuta(nodo) && enRuta(nodo.hijoSi());
                boolean tomoNo = enRuta(nodo) && enRuta(nodo.hijoNo());
                conector(g2, MARGEN + ancho, y + ALTO_CAJA / 2, xHoja, y + ALTO_CAJA / 2, "sí", tomoSi);
                conector(g2, MARGEN + 40, y + ALTO_CAJA, MARGEN + 40, y + SEPARACION_FILAS, "no", tomoNo);
                caja(g2, MARGEN, y, ancho, nodo, false);
                caja(g2, xHoja, y, ancho, nodo.hijoSi(), true);
                nodo = nodo.hijoNo();
                fila++;
            }
            caja(g2, MARGEN, MARGEN + fila * SEPARACION_FILAS, ancho, nodo, true);
            g2.dispose();
        }

        private ArregloDinamico<String> partir(String texto, FontMetrics medidas, int anchoMaximo) {
            ArregloDinamico<String> lineas = new ArregloDinamico<String>();
            StringBuilder actual = new StringBuilder();
            for (String palabra : texto.split(" ")) {
                String prueba = actual.length() == 0 ? palabra : actual + " " + palabra;
                if (medidas.stringWidth(prueba) > anchoMaximo && actual.length() > 0) {
                    lineas.agregar(actual.toString());
                    actual = new StringBuilder(palabra);
                } else {
                    actual = new StringBuilder(prueba);
                }
            }
            lineas.agregar(actual.toString());
            return lineas;
        }

        private boolean enRuta(NodoDecision nodo) {
            return decision != null && decision.ruta().contiene(nodo);
        }

        private void caja(Graphics2D g2, int x, int y, int ancho, NodoDecision nodo, boolean esHoja) {
            boolean activa = enRuta(nodo);
            boolean esFinal = activa && nodo.esHoja();
            Color fondo = esFinal ? Tema.ACENTO : (activa ? Tema.PRIMARIO : (esHoja ? Tema.SUPERFICIE_SUAVE : Tema.PRIMARIO_SUAVE));
            g2.setColor(fondo);
            g2.fillRoundRect(x, y, ancho, ALTO_CAJA, 14, 14);
            g2.setColor(activa ? fondo : Tema.BORDE);
            g2.drawRoundRect(x, y, ancho, ALTO_CAJA, 14, 14);
            g2.setColor(activa ? Color.WHITE : Tema.TEXTO);
            g2.setFont(nodo.esHoja() ? Tema.FUENTE_TEXTO : Tema.FUENTE_TEXTO_FUERTE);
            String texto = nodo.esHoja() ? "→ " + nodo.texto() : nodo.texto();
            FontMetrics medidas = g2.getFontMetrics();
            ArregloDinamico<String> lineas = partir(texto, medidas, ancho - 20);
            int altoTexto = lineas.tamanio() * medidas.getHeight();
            int base = y + (ALTO_CAJA - altoTexto) / 2 + medidas.getAscent();
            for (int i = 0; i < lineas.tamanio(); i++) {
                String linea = lineas.obtener(i);
                g2.drawString(linea, x + (ancho - medidas.stringWidth(linea)) / 2, base + i * medidas.getHeight());
            }
        }

        private void conector(Graphics2D g2, int x1, int y1, int x2, int y2, String etiqueta, boolean activo) {
            g2.setColor(activo ? Tema.PRIMARIO : Tema.BORDE);
            g2.setStroke(new BasicStroke(activo ? 3f : 2f));
            g2.drawLine(x1, y1, x2, y2);
            g2.setStroke(new BasicStroke(1f));
            g2.setFont(Tema.FUENTE_TEXTO_FUERTE);
            g2.setColor(activo ? Tema.PRIMARIO : Tema.TEXTO_SUAVE);
            if (y1 == y2) {
                g2.drawString(etiqueta, (x1 + x2) / 2 - 6, y1 - 6);
            } else {
                g2.drawString(etiqueta, x1 + 8, (y1 + y2) / 2 + 4);
            }
        }
    }
}
