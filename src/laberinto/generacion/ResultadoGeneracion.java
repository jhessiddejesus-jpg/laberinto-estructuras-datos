package laberinto.generacion;

import laberinto.estructuras.ArregloDinamico;
import laberinto.modelo.Coordenada;
import laberinto.modelo.Laberinto;

// Laberinto generado y su orden
public record ResultadoGeneracion(Laberinto laberinto, String algoritmo, ArregloDinamico<Coordenada> ordenApertura,
                                  int paredesAbiertas, int paredesRechazadas) {
}
