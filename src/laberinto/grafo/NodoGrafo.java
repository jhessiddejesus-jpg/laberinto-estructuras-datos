package laberinto.grafo;

import laberinto.estructuras.ArregloDinamico;
import laberinto.modelo.Coordenada;

// Vértice con lista de aristas
public final class NodoGrafo {
    private final int id;
    private final Coordenada coordenada;
    private final ArregloDinamico<Arista> adyacentes = new ArregloDinamico<>();

    NodoGrafo(int id, Coordenada coordenada) {
        this.id = id;
        this.coordenada = coordenada;
    }

    void agregarArista(Arista arista) {
        adyacentes.agregar(arista);
    }

    public int id() {
        return id;
    }

    public Coordenada coordenada() {
        return coordenada;
    }

    public ArregloDinamico<Arista> adyacentes() {
        return adyacentes;
    }

    public int grado() {
        return adyacentes.tamanio();
    }
}
