package laberinto.busqueda;

import laberinto.decision.ArbolDecision;
import laberinto.decision.ContextoVecino;
import laberinto.estructuras.ArregloDinamico;
import laberinto.estructuras.Frontera;
import laberinto.estructuras.ListaDoble;
import laberinto.modelo.Coordenada;
import laberinto.modelo.Direccion;
import laberinto.modelo.Laberinto;
import laberinto.modelo.PasoExploracion;
import laberinto.modelo.ResultadoBusqueda;

// Plantilla común de búsqueda
public abstract class Recorrido {
    private final ArbolDecision arbolDecision;

    protected Recorrido(ArbolDecision arbolDecision) {
        this.arbolDecision = arbolDecision;
    }

    protected abstract Frontera<Coordenada> crearFrontera();

    public abstract String nombre();

    public abstract boolean garantizaDistanciaMinima();

    public final ResultadoBusqueda resolver(Laberinto laberinto) {
        long reloj = System.nanoTime();
        int tamanio = laberinto.tamanio();
        boolean[][] visitadas = new boolean[tamanio][tamanio];
        Coordenada[][] padres = new Coordenada[tamanio][tamanio];
        ArregloDinamico<PasoExploracion> exploracion = new ArregloDinamico<>();
        Frontera<Coordenada> frontera = crearFrontera();

        Coordenada inicio = laberinto.inicio();
        visitadas[inicio.fila()][inicio.columna()] = true;
        frontera.agregar(inicio);
        exploracion.agregar(PasoExploracion.agregada(inicio));
        int marcadas = 1;
        int expandidas = 0;

        while (!frontera.estaVacia()) {
            Coordenada actual = frontera.retirar();
            expandidas++;
            exploracion.agregar(PasoExploracion.expandida(actual));

            // Orden: sur, norte, este, oeste
            for (Direccion direccion : Direccion.ordenDeInsercion()) {
                Coordenada vecina = actual.vecina(direccion);
                switch (arbolDecision.decidir(new ContextoVecino(laberinto, vecina, visitadas))) {
                    case NO_SE_VISITA, DESCARTAR -> {
                    }
                    case RECONSTRUIR_CAMINO -> {
                        padres[vecina.fila()][vecina.columna()] = actual;
                        exploracion.agregar(PasoExploracion.agregada(vecina));
                        ListaDoble<Coordenada> camino = ReconstructorCamino.desdePadres(padres, inicio, vecina);
                        ResultadoBusqueda.Datos datos = datos(exploracion, expandidas, marcadas, frontera, reloj);
                        return ResultadoBusqueda.exito(datos, camino, laberinto.costoDeCamino(camino));
                    }
                    case AGREGAR_A_FRONTERA -> {
                        visitadas[vecina.fila()][vecina.columna()] = true;
                        padres[vecina.fila()][vecina.columna()] = actual;
                        frontera.agregar(vecina);
                        exploracion.agregar(PasoExploracion.agregada(vecina));
                        marcadas++;
                    }
                }
            }
        }
        return ResultadoBusqueda.sinSalida(datos(exploracion, expandidas, marcadas, frontera, reloj));
    }

    private ResultadoBusqueda.Datos datos(ArregloDinamico<PasoExploracion> exploracion, int expandidas,
                                          int marcadas, Frontera<Coordenada> frontera, long reloj) {
        return new ResultadoBusqueda.Datos(nombre(), frontera.nombreEstructura(), garantizaDistanciaMinima())
                .exploracion(exploracion)
                .celdasExpandidas(expandidas)
                .celdasMarcadas(marcadas)
                .elementosEnFrontera(frontera.tamanio())
                .nanosegundos(System.nanoTime() - reloj);
    }
}
