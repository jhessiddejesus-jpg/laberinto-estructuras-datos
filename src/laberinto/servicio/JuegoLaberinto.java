package laberinto.servicio;

import laberinto.busqueda.Recorrido;
import laberinto.busqueda.RecorridoEnProfundidad;
import laberinto.busqueda.RecorridoPorNiveles;
import laberinto.decision.ArbolDecision;
import laberinto.decision.ContextoVecino;
import laberinto.generacion.GeneradorKruskal;
import laberinto.generacion.GeneradorLaberinto;
import laberinto.generacion.GeneradorPrim;
import laberinto.generacion.VerificadorSinCiclos;
import laberinto.grafo.Dijkstra;
import laberinto.grafo.Grafo;
import laberinto.modelo.Coordenada;
import laberinto.modelo.Laberinto;
import laberinto.modelo.LaberintosPredefinidos;
import laberinto.modelo.RegistroPartida;
import laberinto.modelo.ResultadoBusqueda;

// Fachada que une todo
public final class JuegoLaberinto {
    private final RepositorioSemillas repositorio = new RepositorioSemillas();
    private final Ranking ranking = new Ranking();
    private final ArbolDecision arbolDecision = new ArbolDecision();
    private final Recorrido porNiveles = new RecorridoPorNiveles(arbolDecision);
    private final Recorrido enProfundidad = new RecorridoEnProfundidad(arbolDecision);
    private final Dijkstra dijkstra = new Dijkstra();
    private final GeneradorLaberinto kruskal = new GeneradorKruskal();
    private final GeneradorLaberinto prim = new GeneradorPrim();

    private Laberinto original;
    private Laberinto actual;

    public RepositorioSemillas.Consulta cargar(long semilla, int tamanio, GeneradorLaberinto generador) {
        RepositorioSemillas.Consulta consulta = repositorio.pedir(semilla, tamanio, generador);
        original = consulta.laberinto();
        actual = original.copia();
        return consulta;
    }

    public RepositorioSemillas.Consulta cargarSemillaDelLaboratorio() {
        return cargar(LaberintosPredefinidos.SEMILLA_LABORATORIO, 10, kruskal);
    }

    public ResultadoBusqueda resolverPorNiveles() {
        return registrar(porNiveles.resolver(laberintoActual()));
    }

    public ResultadoBusqueda resolverEnProfundidad() {
        return registrar(enProfundidad.resolver(laberintoActual()));
    }

    public ResultadoBusqueda resolverConDijkstra() {
        return registrar(dijkstra.resolver(laberintoActual()));
    }

    private ResultadoBusqueda registrar(ResultadoBusqueda resultado) {
        if (resultado.encontrado()) {
            ranking.registrar(new RegistroPartida(actual.semilla(), resultado.algoritmo(), resultado.pasos(),
                    resultado.milisegundos()));
        }
        return resultado;
    }

    public void cargarRankingDePrueba() {
        Laberinto publicado = LaberintosPredefinidos.semilla2026();
        double msCola = porNiveles.resolver(publicado).milisegundos();
        double msPila = enProfundidad.resolver(publicado).milisegundos();
        ranking.cargarPrueba(LaberintosPredefinidos.SEMILLA_LABORATORIO, msCola, msPila);
    }

    public boolean alternarBarro(Coordenada celda, int costo) {
        Laberinto laberinto = laberintoActual();
        boolean ponerBarro = !laberinto.esBarro(celda);
        laberinto.ponerCosto(celda, ponerBarro ? costo : Laberinto.COSTO_NORMAL);
        return ponerBarro;
    }

    public void quitarBarro() {
        laberintoActual().quitarBarro();
    }

    public int sellarFila(int fila) {
        return laberintoActual().sellarFila(fila);
    }

    public void restaurar() {
        actual = laberintoGuardado().copia();
    }

    public ArbolDecision.Decision probarDecision(Coordenada celda) {
        Laberinto laberinto = laberintoActual();
        boolean[][] visitadas = new boolean[laberinto.tamanio()][laberinto.tamanio()];
        visitadas[laberinto.inicio().fila()][laberinto.inicio().columna()] = true;
        return arbolDecision.decidirConRuta(new ContextoVecino(laberinto, celda, visitadas));
    }

    public Grafo grafoActual() {
        return Grafo.desdeLaberinto(laberintoActual());
    }

    public VerificadorSinCiclos.Verificacion verificarSinCiclos() {
        return VerificadorSinCiclos.verificar(laberintoActual());
    }

    public Laberinto laberintoActual() {
        return exigir(actual);
    }

    public Laberinto laberintoGuardado() {
        return exigir(original);
    }

    private static Laberinto exigir(Laberinto laberinto) {
        if (laberinto == null) {
            throw new IllegalStateException("Primero cargue o genere un laberinto.");
        }
        return laberinto;
    }

    public boolean hayLaberinto() {
        return actual != null;
    }

    public RepositorioSemillas repositorio() {
        return repositorio;
    }

    public Ranking ranking() {
        return ranking;
    }

    public ArbolDecision arbolDecision() {
        return arbolDecision;
    }

    public GeneradorLaberinto kruskal() {
        return kruskal;
    }

    public GeneradorLaberinto prim() {
        return prim;
    }
}
