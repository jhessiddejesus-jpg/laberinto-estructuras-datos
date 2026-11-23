package laberinto.decision;

import laberinto.modelo.Coordenada;
import laberinto.modelo.Laberinto;

// Datos de la vecina evaluada
public record ContextoVecino(Laberinto laberinto, Coordenada celda, boolean[][] visitadas) {
    public boolean seSaleOEsPared() {
        return !laberinto.esTransitable(celda);
    }

    public boolean esSalida() {
        return laberinto.esSalida(celda);
    }

    public boolean yaVisitada() {
        return visitadas[celda.fila()][celda.columna()];
    }
}
