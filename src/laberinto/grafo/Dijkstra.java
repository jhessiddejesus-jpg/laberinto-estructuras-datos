package laberinto.grafo;

import laberinto.estructuras.ArregloDinamico;
import laberinto.estructuras.ColaPrioridad;
import laberinto.estructuras.ListaDoble;
import laberinto.modelo.Coordenada;
import laberinto.modelo.Laberinto;
import laberinto.modelo.PasoExploracion;
import laberinto.modelo.ResultadoBusqueda;

// Camino de menor costo
public final class Dijkstra {
    private static final int INFINITO = Integer.MAX_VALUE;
    private static final int SIN_PADRE = -1;

    public String nombre() {
        return "Dijkstra (cola de prioridad)";
    }

    public ResultadoBusqueda resolver(Laberinto laberinto) {
        long reloj = System.nanoTime();
        Grafo grafo = Grafo.desdeLaberinto(laberinto);
        int origen = grafo.idDe(laberinto.inicio());
        int destino = grafo.idDe(laberinto.salida());

        int[] costoAcumulado = new int[grafo.numeroNodos()];
        int[] padre = new int[grafo.numeroNodos()];
        boolean[] cerrado = new boolean[grafo.numeroNodos()];
        for (int i = 0; i < grafo.numeroNodos(); i++) {
            costoAcumulado[i] = INFINITO;
            padre[i] = SIN_PADRE;
        }

        ArregloDinamico<PasoExploracion> exploracion = new ArregloDinamico<>();
        ColaPrioridad<Integer> pendientes = new ColaPrioridad<>();
        costoAcumulado[origen] = 0;
        pendientes.insertar(origen, 0);
        exploracion.agregar(PasoExploracion.agregada(laberinto.inicio()));
        int expandidas = 0;
        int marcadas = 1;

        while (!pendientes.estaVacia()) {
            int actual = pendientes.extraerMinimo();
            if (cerrado[actual]) {
                continue;
            }
            cerrado[actual] = true;
            expandidas++;
            exploracion.agregar(PasoExploracion.expandida(grafo.nodo(actual).coordenada()));
            if (actual == destino) {
                break;
            }
            ArregloDinamico<Arista> aristas = grafo.nodo(actual).adyacentes();
            for (int i = 0; i < aristas.tamanio(); i++) {
                Arista arista = aristas.obtener(i);
                int nuevoCosto = costoAcumulado[actual] + arista.peso();
                if (!cerrado[arista.destino()] && nuevoCosto < costoAcumulado[arista.destino()]) {
                    if (costoAcumulado[arista.destino()] == INFINITO) {
                        marcadas++;
                    }
                    costoAcumulado[arista.destino()] = nuevoCosto;
                    padre[arista.destino()] = actual;
                    pendientes.insertar(arista.destino(), nuevoCosto);
                    exploracion.agregar(PasoExploracion.agregada(grafo.nodo(arista.destino()).coordenada()));
                }
            }
        }

        ResultadoBusqueda.Datos datos = new ResultadoBusqueda.Datos(nombre(), "cola de prioridad", true)
                .exploracion(exploracion)
                .celdasExpandidas(expandidas)
                .celdasMarcadas(marcadas)
                .elementosEnFrontera(pendientes.tamanio())
                .nanosegundos(System.nanoTime() - reloj);

        if (!cerrado[destino]) {
            return ResultadoBusqueda.sinSalida(datos);
        }
        return ResultadoBusqueda.exito(datos, reconstruir(grafo, padre, destino), costoAcumulado[destino]);
    }

    private static ListaDoble<Coordenada> reconstruir(Grafo grafo, int[] padre, int destino) {
        ListaDoble<Coordenada> camino = new ListaDoble<>();
        for (int nodo = destino; nodo != SIN_PADRE; nodo = padre[nodo]) {
            camino.agregarAlInicio(grafo.nodo(nodo).coordenada());
        }
        return camino;
    }
}
