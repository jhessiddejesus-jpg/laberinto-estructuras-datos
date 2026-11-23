package laberinto.busqueda;

import laberinto.decision.ArbolDecision;
import laberinto.estructuras.Cola;
import laberinto.estructuras.Frontera;
import laberinto.modelo.Coordenada;

// Búsqueda por niveles con cola
public final class RecorridoPorNiveles extends Recorrido {
    public RecorridoPorNiveles(ArbolDecision arbolDecision) {
        super(arbolDecision);
    }

    @Override
    protected Frontera<Coordenada> crearFrontera() {
        return new Cola<>();
    }

    @Override
    public String nombre() {
        return "Por niveles (cola)";
    }

    @Override
    public boolean garantizaDistanciaMinima() {
        return true;
    }
}
