package laberinto.generacion;

import laberinto.modelo.Coordenada;

// Pared candidata a abrirse
public record ParedEntreCeldas(Coordenada desde, Coordenada hacia, double peso) {
    public Coordenada pared() {
        return desde.puntoMedio(hacia);
    }
}
