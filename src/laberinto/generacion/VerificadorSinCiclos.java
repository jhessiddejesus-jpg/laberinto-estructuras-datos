package laberinto.generacion;

import laberinto.estructuras.ArregloDinamico;
import laberinto.estructuras.Cola;
import laberinto.grafo.Arista;
import laberinto.grafo.Grafo;
import laberinto.modelo.Laberinto;

// Comprueba que no hay ciclos
public final class VerificadorSinCiclos {
    public record Verificacion(int nodos, int aristas, boolean conexo) {
        public boolean sinCiclos() {
            return conexo && aristas == nodos - 1;
        }

        public int ciclosIndependientes() {
            return Math.max(0, aristas - nodos + 1);
        }
    }

    private VerificadorSinCiclos() {
    }

    public static Verificacion verificar(Laberinto laberinto) {
        Grafo grafo = Grafo.desdeLaberinto(laberinto);
        return new Verificacion(grafo.numeroNodos(), grafo.numeroAristas(), esConexo(grafo, grafo.idDe(laberinto.inicio())));
    }

    private static boolean esConexo(Grafo grafo, int origen) {
        boolean[] alcanzado = new boolean[grafo.numeroNodos()];
        Cola<Integer> pendientes = new Cola<>();
        alcanzado[origen] = true;
        pendientes.encolar(origen);
        int alcanzados = 1;
        while (!pendientes.estaVacia()) {
            ArregloDinamico<Arista> aristas = grafo.nodo(pendientes.desencolar()).adyacentes();
            for (int i = 0; i < aristas.tamanio(); i++) {
                int vecino = aristas.obtener(i).destino();
                if (!alcanzado[vecino]) {
                    alcanzado[vecino] = true;
                    alcanzados++;
                    pendientes.encolar(vecino);
                }
            }
        }
        return alcanzados == grafo.numeroNodos();
    }
}
