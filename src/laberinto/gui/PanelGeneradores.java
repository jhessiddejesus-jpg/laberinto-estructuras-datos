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
import javax.swing.JPanel;
import javax.swing.JTextArea;

import laberinto.estructuras.ArregloDinamico;
import laberinto.generacion.EjemploCuatroCeldas;
import laberinto.generacion.ResultadoGeneracion;
import laberinto.generacion.VerificadorSinCiclos;
import laberinto.servicio.JuegoLaberinto;

// Kruskal, Prim y cuatro celdas
public final class PanelGeneradores extends JPanel {
    private static final long serialVersionUID = 1L;

    private final JuegoLaberinto juego;
    private final DibujoCuatroCeldas dibujo = new DibujoCuatroCeldas();
    private final JTextArea pasosTexto = Componentes.areaDeTexto(false);
    private final JTextArea grafoTexto = Componentes.areaDeTexto(false);
    private ArregloDinamico<EjemploCuatroCeldas.PasoEjemplo> pasos = EjemploCuatroCeldas.kruskal();
    private String algoritmo = "Kruskal";
    private int pasosVisibles;

    public PanelGeneradores(JuegoLaberinto juego) {
        super(new GridLayout(1, 2, 10, 0));
        this.juego = juego;
        setOpaque(false);
        setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        JPanel barra = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        barra.setOpaque(false);
        agregarBoton(barra, "Kruskal", BotonTema.Estilo.SECUNDARIO, () -> elegir("Kruskal", EjemploCuatroCeldas.kruskal()));
        agregarBoton(barra, "Prim", BotonTema.Estilo.SECUNDARIO, () -> elegir("Prim", EjemploCuatroCeldas.prim()));
        agregarBoton(barra, "Siguiente paso", BotonTema.Estilo.PRIMARIO, this::siguientePaso);

        JPanel ejemplo = new JPanel(new BorderLayout(0, 8));
        ejemplo.setOpaque(false);
        ejemplo.add(barra, BorderLayout.NORTH);
        ejemplo.add(dibujo, BorderLayout.CENTER);
        ejemplo.add(pasosTexto, BorderLayout.SOUTH);
        pasosTexto.setRows(6);
        add(Tarjeta.conTitulo("Ejemplo de cuatro celdas", "A—B = 1 · A—C = 2 · B—D = 3 · C—D = 4", ejemplo));
        add(Tarjeta.conTitulo("Laberinto actual como grafo", "nodo = celda libre · arista = paso a una vecina libre",
                Componentes.desplazable(grafoTexto)));
        elegir("Kruskal", EjemploCuatroCeldas.kruskal());
    }

    public void actualizar(ResultadoGeneracion generacion) {
        if (!juego.hayLaberinto()) {
            return;
        }
        VerificadorSinCiclos.Verificacion v = juego.verificarSinCiclos();
        StringBuilder texto = new StringBuilder();
        texto.append("Semilla ").append(juego.laberintoActual().semilla()).append(" · ")
                .append(juego.laberintoActual().tamanio()).append("×").append(juego.laberintoActual().tamanio()).append("\n\n");
        texto.append("Nodos (celdas libres): ").append(v.nodos()).append("\n");
        texto.append("Aristas (pasos entre vecinas): ").append(v.aristas()).append("\n");
        texto.append("Conexo: ").append(v.conexo() ? "sí" : "no").append("\n");
        texto.append("Sin ciclos (aristas = nodos − 1): ").append(v.sinCiclos() ? "SÍ" : "no, tiene "
                + v.ciclosIndependientes() + " ciclos independientes").append("\n\n");
        if (generacion != null) {
            texto.append("Generado con ").append(generacion.algoritmo()).append(":\n");
            texto.append("• paredes abiertas: ").append(generacion.paredesAbiertas()).append("\n");
            texto.append("• paredes rechazadas para no cerrar un ciclo: ").append(generacion.paredesRechazadas()).append("\n");
        } else if (!v.sinCiclos()) {
            texto.append("La matriz publicada (o modificada) no viene de un generador; por eso puede tener ciclos. ")
                    .append("Genere otra semilla con Kruskal o Prim para ver un laberinto sin ciclos.");
        }
        grafoTexto.setText(texto.toString());
    }

    private void elegir(String nombre, ArregloDinamico<EjemploCuatroCeldas.PasoEjemplo> nuevosPasos) {
        algoritmo = nombre;
        pasos = nuevosPasos;
        pasosVisibles = 0;
        pasosTexto.setText(nombre.equals("Kruskal")
                ? "Kruskal ordena las aristas por peso y abre una solo si une conjuntos distintos (Union-Find).\nPulse «Siguiente paso»."
                : "Prim parte de A y abre la frontera más barata hacia una celda nueva. (El generador real la elige al azar.)\nPulse «Siguiente paso».");
        dibujo.repaint();
    }

    private void siguientePaso() {
        if (pasosVisibles >= pasos.tamanio()) {
            pasosVisibles = 0;
        } else {
            pasosVisibles++;
        }
        StringBuilder texto = new StringBuilder(algoritmo).append(":\n");
        for (int i = 0; i < pasosVisibles; i++) {
            texto.append(i + 1).append(". ").append(pasos.obtener(i)).append("\n");
        }
        if (pasosVisibles == pasos.tamanio()) {
            texto.append("Quedan tres pasajes y ningún ciclo.");
        }
        pasosTexto.setText(texto.toString());
        dibujo.repaint();
    }

    private static void agregarBoton(JPanel barra, String texto, BotonTema.Estilo estilo, Runnable accion) {
        BotonTema boton = new BotonTema(texto, estilo);
        boton.addActionListener(e -> accion.run());
        barra.add(boton);
    }

    private final class DibujoCuatroCeldas extends JComponent {
        private static final long serialVersionUID = 1L;
        private static final int LADO = 54;

        DibujoCuatroCeldas() {
            setPreferredSize(new Dimension(300, 230));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            Tema.suavizar(g2);
            int separacion = 150;
            int x0 = (getWidth() - separacion - LADO) / 2;
            int y0 = (getHeight() - separacion - LADO) / 2;
            int[][] centros = {
                {x0 + LADO / 2, y0 + LADO / 2}, {x0 + separacion + LADO / 2, y0 + LADO / 2},
                {x0 + LADO / 2, y0 + separacion + LADO / 2}, {x0 + separacion + LADO / 2, y0 + separacion + LADO / 2}
            };
            for (EjemploCuatroCeldas.AristaEjemplo arista : EjemploCuatroCeldas.aristas()) {
                dibujarArista(g2, arista, centros);
            }
            for (int i = 0; i < 4; i++) {
                g2.setColor(Tema.PRIMARIO_SUAVE);
                g2.fillRoundRect(centros[i][0] - LADO / 2, centros[i][1] - LADO / 2, LADO, LADO, 14, 14);
                g2.setColor(Tema.PRIMARIO);
                g2.setStroke(new BasicStroke(2f));
                g2.drawRoundRect(centros[i][0] - LADO / 2, centros[i][1] - LADO / 2, LADO, LADO, 14, 14);
                g2.setFont(Tema.FUENTE_TITULO);
                FontMetrics medidas = g2.getFontMetrics();
                String nombre = EjemploCuatroCeldas.NOMBRES[i];
                g2.drawString(nombre, centros[i][0] - medidas.stringWidth(nombre) / 2, centros[i][1] + medidas.getAscent() / 2 - 3);
            }
            g2.dispose();
        }

        private void dibujarArista(Graphics2D g2, EjemploCuatroCeldas.AristaEjemplo arista, int[][] centros) {
            Boolean estado = estadoDe(arista);
            Color color = estado == null ? Tema.BORDE : (estado ? Tema.EXITO : Tema.PELIGRO);
            float[] guiones = estado != null && !estado ? new float[]{8f, 6f} : null;
            g2.setColor(color);
            g2.setStroke(new BasicStroke(estado == null ? 2f : 5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 1f, guiones, 0f));
            int[] a = centros[arista.desde()];
            int[] b = centros[arista.hacia()];
            g2.drawLine(a[0], a[1], b[0], b[1]);
            g2.setFont(Tema.FUENTE_TEXTO_FUERTE);
            String peso = String.valueOf(arista.peso());
            int mx = (a[0] + b[0]) / 2;
            int my = (a[1] + b[1]) / 2;
            g2.setColor(Tema.SUPERFICIE);
            g2.fillOval(mx - 12, my - 12, 24, 24);
            g2.setColor(color == Tema.BORDE ? Tema.TEXTO_SUAVE : color);
            g2.setStroke(new BasicStroke(2f));
            g2.drawOval(mx - 12, my - 12, 24, 24);
            FontMetrics medidas = g2.getFontMetrics();
            g2.drawString(peso, mx - medidas.stringWidth(peso) / 2, my + medidas.getAscent() / 2 - 2);
        }

        private Boolean estadoDe(EjemploCuatroCeldas.AristaEjemplo arista) {
            for (int i = 0; i < pasosVisibles; i++) {
                if (pasos.obtener(i).arista().equals(arista)) {
                    return pasos.obtener(i).aceptada();
                }
            }
            return null;
        }
    }
}
