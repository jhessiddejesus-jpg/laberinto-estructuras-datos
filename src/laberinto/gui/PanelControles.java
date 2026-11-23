package laberinto.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Rectangle;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.Scrollable;
import javax.swing.SpinnerNumberModel;

import laberinto.modelo.LaberintosPredefinidos;

// Barra lateral de pasos
public final class PanelControles extends JPanel implements Scrollable {
    private static final long serialVersionUID = 1L;

    private final AccionesUsuario acciones;
    private final JTextField campoSemilla = new JTextField(String.valueOf(LaberintosPredefinidos.SEMILLA_LABORATORIO));
    private final JComboBox<String> comboTamanio = new JComboBox<>(new String[]{"10", "15", "21"});
    private final JComboBox<String> comboGenerador = new JComboBox<>(new String[]{"Kruskal (principal)", "Prim (alternativo)"});
    private final JCheckBox casillaAnimar = new JCheckBox("Animar la exploración", true);
    private final JSlider deslizadorVelocidad = new JSlider(1, 10, 6);
    private final SpinnerNumberModel modeloFila = new SpinnerNumberModel(LaberintosPredefinidos.FILA_SELLADA, 0, 9, 1);
    private final BotonTema botonBarro;

    public PanelControles(AccionesUsuario acciones) {
        this.acciones = acciones;
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(Tema.FONDO);
        setBorder(BorderFactory.createEmptyBorder(4, 16, 16, 16));
        botonBarro = new BotonTema("Poner barro en (4,2) · cuesta 4", BotonTema.Estilo.SECUNDARIO).anchoCompleto();

        construirSeccionLaberinto();
        construirSeccionResolver();
        construirSeccionModificar();
        construirSeccionPruebas();
        add(Box.createVerticalGlue());
    }

    private void construirSeccionLaberinto() {
        add(Componentes.seccion("1", "Laberinto"));
        add(Componentes.etiqueta("Semilla"));
        estilizarCampo(campoSemilla);
        campoSemilla.addActionListener(e -> pedirCarga());
        agregarCampo(campoSemilla);

        add(Componentes.etiqueta("Tamaño"));
        comboTamanio.setEditable(true);
        comboTamanio.setToolTipText("Se aceptan 10, 15 o 21. Escriba otro número para ver el rechazo.");
        agregarCampo(comboTamanio);

        add(Componentes.etiqueta("Generador"));
        agregarCampo(comboGenerador);

        agregarBoton(new BotonTema("Cargar o generar", BotonTema.Estilo.PRIMARIO), this::pedirCarga);
        add(Componentes.ayuda("2026 carga la matriz publicada. Otra semilla genera un laberinto nuevo y se guarda en la tabla hash."));
    }

    private void construirSeccionResolver() {
        add(Componentes.seccion("2", "Resolver"));
        agregarBoton(new BotonTema("Por niveles · cola", BotonTema.Estilo.PRIMARIO), acciones::resolverPorNiveles);
        agregarBoton(new BotonTema("En profundidad · pila", BotonTema.Estilo.SECUNDARIO), acciones::resolverEnProfundidad);
        agregarBoton(new BotonTema("Comparar cola y pila", BotonTema.Estilo.SECUNDARIO), acciones::comparar);
        agregarBoton(new BotonTema("Dijkstra · menor costo", BotonTema.Estilo.SECUNDARIO), acciones::resolverConDijkstra);

        casillaAnimar.setOpaque(false);
        casillaAnimar.setFont(Tema.FUENTE_TEXTO);
        casillaAnimar.setForeground(Tema.TEXTO);
        casillaAnimar.setAlignmentX(LEFT_ALIGNMENT);
        add(Box.createVerticalStrut(4));
        add(casillaAnimar);

        JPanel filaVelocidad = new JPanel(new BorderLayout(8, 0));
        filaVelocidad.setOpaque(false);
        filaVelocidad.add(Componentes.etiqueta("Velocidad"), BorderLayout.WEST);
        deslizadorVelocidad.setOpaque(false);
        filaVelocidad.add(deslizadorVelocidad, BorderLayout.CENTER);
        add(Componentes.altoFijo(filaVelocidad));
    }

    private void construirSeccionModificar() {
        add(Componentes.seccion("3", "Modificar"));
        agregarBoton(botonBarro, acciones::alternarBarroDelLaboratorio);

        JPanel filaSellar = new JPanel(new BorderLayout(8, 0));
        filaSellar.setOpaque(false);
        JSpinner selectorFila = new JSpinner(modeloFila);
        selectorFila.setFont(Tema.FUENTE_TEXTO);
        selectorFila.setPreferredSize(new Dimension(64, 34));
        filaSellar.add(selectorFila, BorderLayout.WEST);
        BotonTema botonSellar = new BotonTema("Sellar fila", BotonTema.Estilo.PELIGRO);
        botonSellar.addActionListener(e -> acciones.sellarFila((Integer) modeloFila.getValue()));
        filaSellar.add(botonSellar, BorderLayout.CENTER);
        add(Componentes.altoFijo(filaSellar));
        add(Box.createVerticalStrut(Tema.ESPACIO));

        agregarBoton(new BotonTema("Restaurar laberinto", BotonTema.Estilo.SECUNDARIO), acciones::restaurar);
        add(Componentes.ayuda("Clic derecho sobre una celda libre del tablero: poner o quitar barro."));
    }

    private void construirSeccionPruebas() {
        add(Componentes.seccion("4", "Pruebas del laboratorio"));
        agregarBoton(new BotonTema("Árbol de decisión: (0,1) y (1,0)", BotonTema.Estilo.SECUNDARIO), acciones::probarArbolDelLaboratorio);
        agregarBoton(new BotonTema("Ranking: 22 · 18 · 30 · 14 · 25", BotonTema.Estilo.SECUNDARIO), acciones::cargarRankingDePrueba);
        agregarBoton(new BotonTema("Corrida de verificación completa", BotonTema.Estilo.PRIMARIO), acciones::correrVerificacion);
    }

    public boolean debeAnimar() {
        return casillaAnimar.isSelected();
    }

    public int velocidad() {
        return deslizadorVelocidad.getValue();
    }

    public void ajustarFilas(int tamanio) {
        modeloFila.setMaximum(tamanio - 1);
        if ((Integer) modeloFila.getValue() > tamanio - 1) {
            modeloFila.setValue(tamanio - 1);
        }
    }

    public void actualizarBotonBarro(boolean barroActivo) {
        botonBarro.setText(barroActivo ? "Quitar barro de (4,2)" : "Poner barro en (4,2) · cuesta 4");
    }

    @Override
    public boolean getScrollableTracksViewportWidth() {
        return true;
    }

    @Override
    public boolean getScrollableTracksViewportHeight() {
        return false;
    }

    @Override
    public Dimension getPreferredScrollableViewportSize() {
        return getPreferredSize();
    }

    @Override
    public int getScrollableUnitIncrement(Rectangle visible, int orientacion, int direccion) {
        return 16;
    }

    @Override
    public int getScrollableBlockIncrement(Rectangle visible, int orientacion, int direccion) {
        return visible.height - 32;
    }

    private void pedirCarga() {
        Object tamanio = comboTamanio.getEditor().getItem();
        acciones.cargar(campoSemilla.getText(), tamanio == null ? "" : tamanio.toString(),
                comboGenerador.getSelectedIndex() == 1);
    }

    private void agregarBoton(BotonTema boton, Runnable accion) {
        boton.anchoCompleto();
        boton.addActionListener(e -> accion.run());
        add(boton);
        add(Box.createVerticalStrut(Tema.ESPACIO));
    }

    private void agregarCampo(JComponent campo) {
        campo.setFont(Tema.FUENTE_TEXTO);
        add(Box.createVerticalStrut(4));
        add(Componentes.altoFijo(campo));
        add(Box.createVerticalStrut(10));
    }

    private static void estilizarCampo(JTextField campo) {
        campo.setBorder(Componentes.bordeDeCampo());
        campo.setForeground(Tema.TEXTO);
        campo.setCaretColor(Tema.PRIMARIO);
    }
}
