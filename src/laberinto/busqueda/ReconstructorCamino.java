package laberinto.busqueda;

import laberinto.estructuras.ListaDoble;
import laberinto.modelo.Coordenada;

// Arma el camino desde padres
public final class ReconstructorCamino {
    private ReconstructorCamino() {
    }

    public static ListaDoble<Coordenada> desdePadres(Coordenada[][] padres, Coordenada inicio, Coordenada salida) {
        ListaDoble<Coordenada> camino = new ListaDoble<>();
        Coordenada actual = salida;
        while (actual != null) {
            camino.agregarAlInicio(actual);
            if (actual.equals(inicio)) {
                return camino;
            }
            actual = padres[actual.fila()][actual.columna()];
        }
        throw new IllegalStateException("La cadena de padres no llega al inicio: el recorrido está mal formado.");
    }
}
