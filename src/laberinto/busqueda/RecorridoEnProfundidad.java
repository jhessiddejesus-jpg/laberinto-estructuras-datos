package laberinto.busqueda;

import laberinto.decision.ArbolDecision;
import laberinto.estructuras.Frontera;
import laberinto.estructuras.Pila;
import laberinto.modelo.Coordenada;

// Búsqueda en profundidad con pila
public final class RecorridoEnProfundidad extends Recorrido {
    public RecorridoEnProfundidad(ArbolDecision arbolDecision) {
        super(arbolDecision);
    }

    @Override
    protected Frontera<Coordenada> crearFrontera() {
        return new Pila<>();
    }

    @Override
    public String nombre() {
        return "En profundidad (pila)";
    }

    @Override
    public boolean garantizaDistanciaMinima() {
        return false;
    }
}
