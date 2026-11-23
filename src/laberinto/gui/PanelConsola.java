package laberinto.gui;

import java.awt.BorderLayout;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;

// Salida de la verificación
public final class PanelConsola extends JPanel {
    private static final long serialVersionUID = 1L;

    private final JTextArea texto = Componentes.areaDeTexto(true);
    private final JLabel resumen = new JLabel(" ");

    public PanelConsola() {
        super(new BorderLayout(0, 8));
        setOpaque(false);
        setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        texto.setLineWrap(false);
        texto.setText("Pulse «Corrida de verificación completa» en la barra lateral.");
        resumen.setFont(Tema.FUENTE_TEXTO_FUERTE);
        add(resumen, BorderLayout.NORTH);
        add(Tarjeta.conTitulo("Corrida de verificación", "Cada comprobación compara con el valor publicado en el enunciado",
                Componentes.desplazable(texto)), BorderLayout.CENTER);
    }

    public void mostrar(String salida, int fallas) {
        texto.setText(salida);
        texto.setCaretPosition(0);
        resumen.setOpaque(true);
        resumen.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
        if (fallas == 0) {
            resumen.setText("Todas las comprobaciones pasaron.");
            resumen.setBackground(Tema.EXITO_SUAVE);
            resumen.setForeground(Tema.EXITO);
        } else {
            resumen.setText(fallas + " comprobaciones fallaron. Busque [FALLA] en el texto.");
            resumen.setBackground(Tema.PELIGRO_SUAVE);
            resumen.setForeground(Tema.PELIGRO);
        }
    }
}
