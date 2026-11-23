package laberinto.grafo;

import laberinto.estructuras.ArregloDinamico;
import laberinto.modelo.Coordenada;
import laberinto.modelo.Direccion;
import laberinto.modelo.Laberinto;
import laberinto.modelo.TipoCelda;

// Grafo desde la matriz
public final class Grafo {
    private static final int SIN_NODO = -1;

    private final ArregloDinamico<NodoGrafo> nodos = new ArregloDinamico<>();
    private final int[][] idPorCelda;
    private int arcos;

    private Grafo(int tamanio) {
        idPorCelda = new int[tamanio][tamanio];
    }

    // Peso = costo de entrar
    public static Grafo desdeMatriz(char[][] matriz, int[][] costos) {
        int tamanio = matriz.length;
        Grafo grafo = new Grafo(tamanio);

        for (int fila = 0; fila < tamanio; fila++) {
            for (int columna = 0; columna < tamanio; columna++) {
                if (matriz[fila][columna] == TipoCelda.PARED.simbolo()) {
                    grafo.idPorCelda[fila][columna] = SIN_NODO;
                } else {
                    int id = grafo.nodos.tamanio();
                    grafo.idPorCelda[fila][columna] = id;
                    grafo.nodos.agregar(new NodoGrafo(id, new Coordenada(fila, columna)));
                }
            }
        }

        for (int id = 0; id < grafo.nodos.tamanio(); id++) {
            NodoGrafo nodo = grafo.nodos.obtener(id);
            for (Direccion direccion : Direccion.ordenDeInsercion()) {
                Coordenada vecina = nodo.coordenada().vecina(direccion);
                int idVecina = grafo.idDe(vecina);
                if (idVecina != SIN_NODO) {
                    nodo.agregarArista(new Arista(idVecina, costos[vecina.fila()][vecina.columna()]));
                    grafo.arcos++;
                }
            }
        }
        return grafo;
    }

    public static Grafo desdeLaberinto(Laberinto laberinto) {
        return desdeMatriz(laberinto.copiaDeCeldas(), laberinto.copiaDeCostos());
    }

    public int idDe(Coordenada c) {
        boolean dentro = c.fila() >= 0 && c.columna() >= 0
                && c.fila() < idPorCelda.length && c.columna() < idPorCelda.length;
        return dentro ? idPorCelda[c.fila()][c.columna()] : SIN_NODO;
    }

    public NodoGrafo nodo(int id) {
        return nodos.obtener(id);
    }

    public int numeroNodos() {
        return nodos.tamanio();
    }

    public int numeroAristas() {
        return arcos / 2;
    }
}
