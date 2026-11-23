package laberinto.gui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;

import laberinto.modelo.ResultadoBusqueda;

// Métricas de cada recorrido
public final class PanelResultados extends JPanel {
    private static final long serialVersionUID = 1L;

    private final TarjetaMetrica tarjetaCola = new TarjetaMetrica("Por niveles · cola");
    private final TarjetaMetrica tarjetaPila = new TarjetaMetrica("En profundidad · pila");
    private final TarjetaMetrica tarjetaDijkstra = new TarjetaMetrica("Dijkstra · costo");
    private final JTextArea conclusion = Componentes.areaDeTexto(false);
    private final JTextArea estructuras = Componentes.areaDeTexto(false);
    private ResultadoBusqueda ultimoCola;
    private ResultadoBusqueda ultimoPila;
    private ResultadoBusqueda ultimoDijkstra;

    public PanelResultados() {
        super(new BorderLayout(0, 12));
        setOpaque(false);
        setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        JPanel metricas = new JPanel(new GridLayout(1, 3, 10, 0));
        metricas.setOpaque(false);
        metricas.add(tarjetaCola);
        metricas.add(tarjetaPila);
        metricas.add(tarjetaDijkstra);
        add(metricas, BorderLayout.NORTH);

        JPanel textos = new JPanel(new GridLayout(2, 1, 0, 10));
        textos.setOpaque(false);
        textos.add(Tarjeta.conTitulo("Conclusión", "Qué significan los números", conclusion));
        textos.add(Tarjeta.conTitulo("Estado de las estructuras al terminar", "Qué queda vacío y qué no", estructuras));
        add(textos, BorderLayout.CENTER);
        limpiar();
    }

    public void limpiar() {
        ultimoCola = null;
        ultimoPila = null;
        ultimoDijkstra = null;
        tarjetaCola.vaciar();
        tarjetaPila.vaciar();
        tarjetaDijkstra.vaciar();
        conclusion.setText("Resuelva el laberinto con la cola y con la pila para comparar las dos longitudes.");
        estructuras.setText("Todavía no hay un recorrido.");
    }

    public void mostrar(ResultadoBusqueda resultado) {
        switch (resultado.estructura()) {
            case "cola" -> {
                ultimoCola = resultado;
                tarjetaCola.mostrar(resultado, false);
            }
            case "pila" -> {
                ultimoPila = resultado;
                tarjetaPila.mostrar(resultado, false);
            }
            default -> {
                ultimoDijkstra = resultado;
                tarjetaDijkstra.mostrar(resultado, true);
            }
        }
        estructuras.setText(describirEstructuras(resultado));
        conclusion.setText(redactarConclusion());
    }

    private String redactarConclusion() {
        StringBuilder texto = new StringBuilder();
        boolean ambos = ultimoCola != null && ultimoPila != null && ultimoCola.encontrado() && ultimoPila.encontrado();
        if (ambos) {
            texto.append("La cola llegó en ").append(ultimoCola.pasos()).append(" pasos y la pila en ")
                    .append(ultimoPila.pasos()).append(". ");
            if (ultimoPila.pasos() > ultimoCola.pasos()) {
                texto.append(ultimoPila.pasos()).append(" pasos no es la distancia mínima: la pila sigue un pasillo ")
                        .append("hasta el fondo y se queda con el primer camino que toca la salida. La cola avanza ")
                        .append("por niveles, así que la primera vez que llega lo hace con el mínimo de pasos.");
            } else {
                texto.append("Aquí coinciden, pero la pila no lo garantiza: con otro orden de vecinos o en otro ")
                        .append("laberinto su camino puede ser más largo.");
            }
        } else if (hayAlgunoSinSalida()) {
            texto.append("No hay salida. La frontera terminó vacía y la lista del camino quedó vacía: ")
                    .append("no se presenta el tramo recorrido como si fuera una solución.");
        } else {
            texto.append("Falta resolver con la cola o con la pila para comparar las dos longitudes.");
        }
        if (ultimoDijkstra != null && ultimoDijkstra.encontrado()) {
            texto.append("\n\nDijkstra mide COSTO, no pasos: costo ").append(ultimoDijkstra.costo()).append(" en ")
                    .append(ultimoDijkstra.pasos()).append(" pasos.");
            if (ultimoCola != null && ultimoCola.encontrado() && ultimoCola.costo() > ultimoDijkstra.costo()) {
                texto.append(" El camino de la cola cuesta ").append(ultimoCola.costo())
                        .append(" porque atraviesa el barro: la cola no mira el peso, así que el rodeo conviene.");
            }
        }
        return texto.toString();
    }

    private boolean hayAlgunoSinSalida() {
        return (ultimoCola != null && !ultimoCola.encontrado()) || (ultimoPila != null && !ultimoPila.encontrado())
                || (ultimoDijkstra != null && !ultimoDijkstra.encontrado());
    }

    private static String describirEstructuras(ResultadoBusqueda r) {
        String nombre = r.estructura();
        StringBuilder texto = new StringBuilder("Último recorrido: ").append(r.algoritmo()).append("\n");
        texto.append("• ").append(capitalizar(nombre)).append(": ")
                .append(r.fronteraVacia() ? "VACÍA (0 elementos)" : r.elementosEnFrontera() + " elementos sin explorar")
                .append("\n");
        texto.append("• Lista doble del camino: ")
                .append(r.camino().estaVacia() ? "VACÍA (0 nodos)" : r.camino().tamanio() + " nodos").append("\n");
        texto.append("• Celdas marcadas: ").append(r.celdasMarcadas()).append(" (no queda vacía)\n");
        texto.append("• Celdas exploradas: ").append(r.celdasExpandidas()).append("\n");
        texto.append(String.format("• Tiempo: %.3f ms", r.milisegundos()));
        return texto.toString();
    }

    private static String capitalizar(String texto) {
        return Character.toUpperCase(texto.charAt(0)) + texto.substring(1);
    }

    private static final class TarjetaMetrica extends Tarjeta {
        private static final long serialVersionUID = 1L;
        private final JLabel valor = new JLabel();
        private final JLabel unidad = new JLabel();
        private final JLabel detalle = new JLabel();
        private final JPanel contenedorInsignia = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));

        private TarjetaMetrica(String titulo) {
            JPanel cuerpo = new JPanel();
            cuerpo.setOpaque(false);
            cuerpo.setLayout(new BoxLayout(cuerpo, BoxLayout.Y_AXIS));
            JLabel etiquetaTitulo = new JLabel(titulo);
            etiquetaTitulo.setFont(Tema.FUENTE_TEXTO_FUERTE);
            etiquetaTitulo.setForeground(Tema.TEXTO_SUAVE);
            valor.setFont(Tema.FUENTE_METRICA);
            valor.setForeground(Tema.TEXTO);
            unidad.setFont(Tema.FUENTE_PEQUENA);
            unidad.setForeground(Tema.TEXTO_SUAVE);
            detalle.setFont(Tema.FUENTE_PEQUENA);
            detalle.setForeground(Tema.TEXTO_SUAVE);
            contenedorInsignia.setOpaque(false);
            for (javax.swing.JComponent parte : new javax.swing.JComponent[]{etiquetaTitulo, valor, unidad, detalle, contenedorInsignia}) {
                parte.setAlignmentX(LEFT_ALIGNMENT);
                cuerpo.add(parte);
            }
            add(cuerpo, BorderLayout.CENTER);
        }

        private void vaciar() {
            valor.setText("—");
            valor.setForeground(Tema.TEXTO_SUAVE);
            unidad.setText(" ");
            detalle.setText("Sin resolver");
            ponerInsignia(null);
        }

        private void mostrar(ResultadoBusqueda r, boolean destacarCosto) {
            if (!r.encontrado()) {
                valor.setText("Sin salida");
                valor.setForeground(Tema.PELIGRO);
                unidad.setText(" ");
                detalle.setText("La " + r.estructura() + " terminó vacía");
                ponerInsignia(Componentes.insignia("No hay salida", Tema.PELIGRO_SUAVE, Tema.PELIGRO));
                return;
            }
            valor.setForeground(Tema.TEXTO);
            valor.setText(String.valueOf(destacarCosto ? r.costo() : r.pasos()));
            unidad.setText(destacarCosto ? "de costo" : "pasos");
            detalle.setText(destacarCosto
                    ? r.pasos() + " pasos · " + String.format("%.3f ms", r.milisegundos())
                    : "costo " + r.costo() + " · " + String.format("%.3f ms", r.milisegundos()));
            ponerInsignia(r.garantizaMinimo()
                    ? Componentes.insignia(destacarCosto ? "Costo mínimo" : "Mínimo garantizado", Tema.EXITO_SUAVE, Tema.EXITO)
                    : Componentes.insignia("No es la distancia mínima", Tema.AVISO_SUAVE, Tema.AVISO));
        }

        private void ponerInsignia(JLabel insignia) {
            contenedorInsignia.removeAll();
            contenedorInsignia.setBorder(BorderFactory.createEmptyBorder(6, 0, 0, 0));
            if (insignia != null) {
                contenedorInsignia.add(insignia);
            }
            contenedorInsignia.revalidate();
            contenedorInsignia.repaint();
        }
    }
}
