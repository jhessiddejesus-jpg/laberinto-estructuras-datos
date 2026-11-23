package laberinto.gui;

import java.awt.BorderLayout;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JTextArea;

import laberinto.modelo.DibujoTexto;
import laberinto.modelo.Laberinto;
import laberinto.modelo.ResultadoBusqueda;

// Camino de ida y regreso
public final class PanelCamino extends JPanel {
    private static final long serialVersionUID = 1L;
    private static final String FLECHA = " → ";

    private final JTextArea texto = Componentes.areaDeTexto(true);

    public PanelCamino() {
        super(new BorderLayout());
        setOpaque(false);
        setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        add(Tarjeta.conTitulo("Lista doblemente enlazada del camino",
                "Ida con el enlace siguiente · regreso con el enlace anterior (no es una lista copiada)",
                Componentes.desplazable(texto)), BorderLayout.CENTER);
        limpiar();
    }

    public void limpiar() {
        texto.setText("Resuelva el laberinto para ver aquí las coordenadas del camino.");
    }

    public void mostrar(ResultadoBusqueda resultado, Laberinto laberinto) {
        StringBuilder contenido = new StringBuilder();
        agregarResultado(contenido, resultado, laberinto);
        texto.setText(contenido.toString());
        texto.setCaretPosition(0);
    }

    public void mostrarComparacion(ResultadoBusqueda cola, ResultadoBusqueda pila, Laberinto laberinto) {
        StringBuilder contenido = new StringBuilder();
        agregarResultado(contenido, cola, laberinto);
        contenido.append("\n").append("─".repeat(48)).append("\n\n");
        agregarResultado(contenido, pila, laberinto);
        texto.setText(contenido.toString());
        texto.setCaretPosition(0);
    }

    private static void agregarResultado(StringBuilder contenido, ResultadoBusqueda r, Laberinto laberinto) {
        contenido.append(r.algoritmo().toUpperCase()).append("\n");
        if (!r.encontrado()) {
            contenido.append("No hay salida. La lista del camino está vacía (0 nodos).\n");
            return;
        }
        contenido.append(r.pasos()).append(" pasos · costo ").append(r.costo())
                .append(r.garantizaMinimo() ? "" : " · no es la distancia mínima").append("\n\n");
        contenido.append("IDA (enlace siguiente)\n").append(r.camino().textoIda(FLECHA)).append("\n\n");
        contenido.append("REGRESO (enlace anterior)\n").append(r.camino().textoRegreso(FLECHA)).append("\n\n");
        contenido.append("DIBUJO (* = camino, sin cubrir S ni E)\n").append(DibujoTexto.conCamino(laberinto, r.camino()));
    }
}
