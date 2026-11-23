package laberinto.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.function.Supplier;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.UIManager;
import javax.swing.WindowConstants;

import laberinto.consola.CorridaVerificacion;
import laberinto.generacion.GeneradorLaberinto;
import laberinto.modelo.Coordenada;
import laberinto.modelo.Laberinto;
import laberinto.modelo.LaberintosPredefinidos;
import laberinto.modelo.ResultadoBusqueda;
import laberinto.servicio.JuegoLaberinto;
import laberinto.servicio.RepositorioSemillas;

// Ventana principal de la aplicación
public final class VentanaPrincipal extends JFrame implements AccionesUsuario, PanelLaberinto.OyenteCeldas {
    private static final long serialVersionUID = 1L;
    private static final int PESTANA_RESULTADOS = 0;
    private static final int PESTANA_RANKING = 3;
    private static final int PESTANA_DECISION = 4;
    private static final int PESTANA_VERIFICACION = 6;

    private final JuegoLaberinto juego = new JuegoLaberinto();
    private final PanelLaberinto panelLaberinto = new PanelLaberinto();
    private final PanelControles panelControles;
    private final PanelResultados panelResultados = new PanelResultados();
    private final PanelCamino panelCamino = new PanelCamino();
    private final PanelTablaHash panelTabla = new PanelTablaHash(juego.repositorio());
    private final PanelRanking panelRanking;
    private final PanelDecision panelDecision;
    private final PanelGeneradores panelGeneradores = new PanelGeneradores(juego);
    private final PanelConsola panelConsola = new PanelConsola();
    private final BarraEstado barraEstado = new BarraEstado();
    private final JTabbedPane pestanas = new JTabbedPane();
    private final JLabel tituloLaberinto = new JLabel();
    private final JLabel ayudaLaberinto = new JLabel();

    public VentanaPrincipal() {
        super("Laberinto · Salida de menor distancia");
        aplicarAspectoDelSistema();
        panelControles = new PanelControles(this);
        panelRanking = new PanelRanking(juego.ranking(), this::cargarRankingDePrueba);
        panelDecision = new PanelDecision(juego, this::celdaSeleccionada);
        panelLaberinto.setOyente(this);

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(1180, 720));
        setSize(1440, 860);
        setLocationRelativeTo(null);
        setContentPane(construirContenido());

        cargar(String.valueOf(LaberintosPredefinidos.SEMILLA_LABORATORIO), "10", false);
    }

    private JPanel construirContenido() {
        JPanel raiz = new JPanel(new BorderLayout());
        raiz.setBackground(Tema.FONDO);
        raiz.add(construirEncabezado(), BorderLayout.NORTH);

        JScrollPane barraLateral = new JScrollPane(panelControles);
        barraLateral.setBorder(BorderFactory.createEmptyBorder());
        barraLateral.getVerticalScrollBar().setUnitIncrement(16);
        barraLateral.setPreferredSize(new Dimension(Tema.ANCHO_BARRA_LATERAL, 0));
        barraLateral.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        raiz.add(barraLateral, BorderLayout.WEST);

        pestanas.setUI(new PestanasTema());
        pestanas.setFont(Tema.FUENTE_TEXTO_FUERTE);
        pestanas.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
        agregarPestana("Resultados", panelResultados);
        agregarPestana("Camino", panelCamino);
        agregarPestana("Tabla hash", panelTabla);
        agregarPestana("Ranking", panelRanking);
        agregarPestana("Decisión", panelDecision);
        agregarPestana("Generadores", panelGeneradores);
        agregarPestana("Verificación", panelConsola);

        JSplitPane division = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, construirZonaLaberinto(), pestanas);
        division.setResizeWeight(0.48);
        division.setBorder(BorderFactory.createEmptyBorder(0, 0, 12, 12));
        division.setOpaque(false);
        division.setDividerSize(8);
        raiz.add(division, BorderLayout.CENTER);
        raiz.add(barraEstado, BorderLayout.SOUTH);
        return raiz;
    }

    private void agregarPestana(String titulo, JPanel panel) {
        panel.setOpaque(true);
        panel.setBackground(Tema.FONDO);
        pestanas.addTab(titulo, panel);
    }

    private JComponent construirEncabezado() {
        JPanel encabezado = new JPanel();
        encabezado.setLayout(new BoxLayout(encabezado, BoxLayout.Y_AXIS));
        encabezado.setBackground(Tema.FONDO);
        encabezado.setBorder(BorderFactory.createEmptyBorder(14, 18, 8, 18));
        JLabel titulo = new JLabel("Laberinto y salida de menor distancia");
        titulo.setFont(Tema.FUENTE_TITULO);
        titulo.setForeground(Tema.TEXTO);
        JLabel subtitulo = new JLabel("Laboratorio de Estructuras de Datos · Proyecto 5 · cola, pila, lista doble, "
                + "tabla hash, ABB, árbol de decisión y grafo");
        subtitulo.setFont(Tema.FUENTE_PEQUENA);
        subtitulo.setForeground(Tema.TEXTO_SUAVE);
        encabezado.add(titulo);
        encabezado.add(subtitulo);
        return encabezado;
    }

    private JComponent construirZonaLaberinto() {
        Tarjeta tarjeta = new Tarjeta();
        JPanel encabezado = new JPanel();
        encabezado.setOpaque(false);
        encabezado.setLayout(new BoxLayout(encabezado, BoxLayout.Y_AXIS));
        tituloLaberinto.setFont(Tema.FUENTE_SUBTITULO);
        tituloLaberinto.setForeground(Tema.TEXTO);
        ayudaLaberinto.setFont(Tema.FUENTE_PEQUENA);
        ayudaLaberinto.setForeground(Tema.TEXTO_SUAVE);
        ayudaLaberinto.setText("Clic: probar el árbol de decisión · Clic derecho: barro");
        encabezado.add(tituloLaberinto);
        encabezado.add(ayudaLaberinto);
        tarjeta.add(encabezado, BorderLayout.NORTH);
        tarjeta.add(panelLaberinto, BorderLayout.CENTER);
        tarjeta.add(new Leyenda(), BorderLayout.SOUTH);

        JPanel envoltura = new JPanel(new BorderLayout());
        envoltura.setOpaque(false);
        envoltura.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 4));
        envoltura.add(tarjeta, BorderLayout.CENTER);
        envoltura.setMinimumSize(new Dimension(420, 420));
        return envoltura;
    }

    private static void aplicarAspectoDelSistema() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ReflectiveOperationException | javax.swing.UnsupportedLookAndFeelException error) {
        }
        UIManager.put("ToolTip.font", Tema.FUENTE_PEQUENA);
    }

    @Override
    public void cargar(String textoSemilla, String textoTamanio, boolean usarPrim) {
        Long semilla = leerNumero(textoSemilla, "La semilla debe ser un número entero.");
        Long tamanio = leerNumero(textoTamanio, "El tamaño debe ser un número: 10, 15 o 21.");
        if (semilla == null || tamanio == null) {
            return;
        }
        GeneradorLaberinto generador = usarPrim ? juego.prim() : juego.kruskal();
        try {
            RepositorioSemillas.Consulta consulta = juego.cargar(semilla, tamanio.intValue(), generador);
            alCargar(consulta, semilla, tamanio.intValue());
        } catch (IllegalArgumentException rechazo) {
            barraEstado.error(rechazo.getMessage());
        }
    }

    private Long leerNumero(String texto, String error) {
        try {
            return Long.parseLong(texto.trim());
        } catch (NumberFormatException noEsNumero) {
            barraEstado.error(error);
            return null;
        }
    }

    private void limpiarResultados() {
        panelResultados.limpiar();
        panelCamino.limpiar();
    }

    private void alCargar(RepositorioSemillas.Consulta consulta, long semilla, int tamanioPedido) {
        Laberinto laberinto = juego.laberintoActual();
        limpiarResultados();
        panelControles.actualizarBotonBarro(false);
        panelControles.ajustarFilas(laberinto.tamanio());
        panelTabla.actualizar(semilla, consulta.reutilizado());
        panelGeneradores.actualizar(consulta.generacion());
        actualizarTitulo(consulta);

        if (consulta.generacion() != null && panelControles.debeAnimar()) {
            panelLaberinto.animarGeneracion(laberinto, consulta.generacion().ordenApertura(), panelControles.velocidad(), () -> { });
        } else {
            panelLaberinto.mostrarLaberinto(laberinto);
        }

        barraEstado.exito(mensajeDeCarga(consulta, semilla, tamanioPedido));
    }

    private static String mensajeDeCarga(RepositorioSemillas.Consulta consulta, long semilla, int tamanioPedido) {
        int tamanio = consulta.laberinto().tamanio();
        String cubeta = "cubeta " + consulta.cubeta();
        if (consulta.reutilizado()) {
            return "Semilla " + semilla + " reutilizada desde la " + cubeta + ": no se volvió a construir la matriz.";
        }
        if (consulta.generacion() == null) {
            String nota = tamanioPedido != tamanio ? " (2026 siempre es la matriz publicada de 10×10)" : "";
            return "Semilla " + semilla + ": matriz publicada cargada y guardada en la " + cubeta + nota + ".";
        }
        return "Semilla " + semilla + " generada con " + consulta.generacion().algoritmo() + " ("
                + tamanio + "×" + tamanio + ") y guardada en la " + cubeta + ".";
    }

    private void actualizarTitulo(RepositorioSemillas.Consulta consulta) {
        Laberinto laberinto = juego.laberintoActual();
        String origen = consulta.generacion() != null ? "generado con " + consulta.generacion().algoritmo()
                : (laberinto.semilla() == LaberintosPredefinidos.SEMILLA_LABORATORIO ? "matriz publicada" : "guardado");
        tituloLaberinto.setText("Semilla " + laberinto.semilla() + " · " + laberinto.tamanio() + "×" + laberinto.tamanio()
                + " · " + origen);
    }

    @Override
    public void resolverPorNiveles() {
        resolver(juego::resolverPorNiveles);
    }

    @Override
    public void resolverEnProfundidad() {
        resolver(juego::resolverEnProfundidad);
    }

    @Override
    public void resolverConDijkstra() {
        resolver(juego::resolverConDijkstra);
    }

    private void resolver(Supplier<ResultadoBusqueda> algoritmo) {
        if (!juego.hayLaberinto()) {
            return;
        }
        ResultadoBusqueda resultado = algoritmo.get();
        barraEstado.informar("Explorando con " + resultado.algoritmo() + "…");
        panelLaberinto.mostrarResultado(resultado, panelControles.debeAnimar(), panelControles.velocidad(),
                () -> alTerminarDeResolver(resultado));
    }

    private void alTerminarDeResolver(ResultadoBusqueda resultado) {
        panelResultados.mostrar(resultado);
        panelCamino.mostrar(resultado, juego.laberintoActual());
        panelRanking.refrescar();
        pestanas.setSelectedIndex(PESTANA_RESULTADOS);
        if (!resultado.encontrado()) {
            barraEstado.error(resultado.mensaje());
        } else if (resultado.garantizaMinimo()) {
            barraEstado.exito(resultado.mensaje());
        } else {
            barraEstado.aviso(resultado.mensaje());
        }
    }

    @Override
    public void comparar() {
        ResultadoBusqueda cola = juego.resolverPorNiveles();
        ResultadoBusqueda pila = juego.resolverEnProfundidad();
        panelLaberinto.mostrarComparacion(cola, pila);
        panelResultados.mostrar(cola);
        panelResultados.mostrar(pila);
        panelCamino.mostrarComparacion(cola, pila, juego.laberintoActual());
        panelRanking.refrescar();
        pestanas.setSelectedIndex(PESTANA_RESULTADOS);
        if (cola.encontrado() && pila.encontrado()) {
            barraEstado.aviso("Cola: " + cola.pasos() + " pasos (mínimo garantizado). Pila: " + pila.pasos()
                    + " pasos (no es la distancia mínima).");
        } else {
            barraEstado.error("No hay salida: la cola y la pila terminaron vacías.");
        }
    }

    @Override
    public void alternarBarroDelLaboratorio() {
        alternarBarro(LaberintosPredefinidos.CELDA_BARRO);
    }

    @Override
    public void alternarBarro(Coordenada celda) {
        try {
            boolean conBarro = juego.alternarBarro(celda, LaberintosPredefinidos.COSTO_BARRO);
            panelLaberinto.limpiarRecorrido();
            panelResultados.limpiar();
            panelControles.actualizarBotonBarro(juego.laberintoActual().esBarro(LaberintosPredefinidos.CELDA_BARRO));
            if (conBarro) {
                barraEstado.aviso("Barro en " + celda + ": entrar cuesta " + LaberintosPredefinidos.COSTO_BARRO
                        + ". Compare la cola con Dijkstra.");
            } else {
                barraEstado.informar("Barro quitado de " + celda + ": vuelve a costar 1.");
            }
        } catch (IllegalArgumentException rechazo) {
            barraEstado.error(rechazo.getMessage());
        }
    }

    @Override
    public void sellarFila(int fila) {
        try {
            int selladas = juego.sellarFila(fila);
            panelLaberinto.mostrarLaberinto(juego.laberintoActual());
            limpiarResultados();
            panelGeneradores.actualizar(null);
            barraEstado.aviso("Fila " + fila + " sellada (" + selladas + " celdas pasaron a pared). Resuelva para ver el aviso.");
        } catch (IllegalArgumentException rechazo) {
            barraEstado.error(rechazo.getMessage());
        }
    }

    @Override
    public void restaurar() {
        if (!juego.hayLaberinto()) {
            return;
        }
        juego.restaurar();
        panelLaberinto.mostrarLaberinto(juego.laberintoActual());
        limpiarResultados();
        panelControles.actualizarBotonBarro(false);
        panelGeneradores.actualizar(null);
        barraEstado.informar("Laberinto restaurado: sin barro y sin filas selladas.");
    }

    @Override
    public void celdaSeleccionada(Coordenada celda) {
        panelDecision.probar(celda);
        panelLaberinto.resaltar(juego.laberintoActual().estaDentro(celda) ? celda : null);
        pestanas.setSelectedIndex(PESTANA_DECISION);
        barraEstado.informar("Árbol de decisión aplicado a " + celda + ".");
    }

    @Override
    public void probarArbolDelLaboratorio() {
        panelDecision.probarCeldasDelLaboratorio();
        panelLaberinto.resaltar(new Coordenada(1, 0));
        pestanas.setSelectedIndex(PESTANA_DECISION);
        barraEstado.exito("(0,1) es pared → no se visita · (1,0) es camino nuevo → se encola.");
    }

    @Override
    public void cargarRankingDePrueba() {
        juego.cargarRankingDePrueba();
        panelRanking.refrescar();
        panelRanking.buscar();
        pestanas.setSelectedIndex(PESTANA_RANKING);
        barraEstado.exito("Ranking de prueba cargado: inorden 14, 18, 22, 25, 30 · peso 4 · altura 2 · no es completo.");
    }

    @Override
    public void correrVerificacion() {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        PrintStream salida = new PrintStream(bytes, true, StandardCharsets.UTF_8);
        int fallas = new CorridaVerificacion(salida).ejecutar();
        panelConsola.mostrar(bytes.toString(StandardCharsets.UTF_8), fallas);
        pestanas.setSelectedIndex(PESTANA_VERIFICACION);
        if (fallas == 0) {
            barraEstado.exito("Corrida de verificación: todas las comprobaciones pasaron.");
        } else {
            barraEstado.error("Corrida de verificación: " + fallas + " comprobaciones fallaron.");
        }
    }
}
