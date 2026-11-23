package laberinto.generacion;

import java.util.Comparator;
import java.util.Random;

import laberinto.estructuras.ArregloDinamico;
import laberinto.estructuras.UnionFind;
import laberinto.modelo.Coordenada;
import laberinto.modelo.Laberinto;

// Generador principal con Kruskal
public final class GeneradorKruskal implements GeneradorLaberinto {
    @Override
    public ResultadoGeneracion generar(int tamanio, long semilla) {
        ReticulaDeCeldas reticula = new ReticulaDeCeldas(tamanio);
        Random azar = new Random(semilla);
        char[][] matriz = reticula.matrizDeParedes();
        ArregloDinamico<Coordenada> ordenApertura = new ArregloDinamico<>();

        for (int i = 0; i < reticula.numeroCeldas(); i++) {
            reticula.abrir(matriz, reticula.celda(i), ordenApertura);
        }

        ArregloDinamico<ParedEntreCeldas> paredes = reticula.todasLasParedes(azar);
        paredes.ordenar(Comparator.comparingDouble(ParedEntreCeldas::peso));

        UnionFind conjuntos = new UnionFind(reticula.numeroCeldas());
        int abiertas = 0;
        int rechazadas = 0;
        for (int i = 0; i < paredes.tamanio(); i++) {
            ParedEntreCeldas pared = paredes.obtener(i);
            boolean estabanSeparadas = conjuntos.unir(reticula.indice(pared.desde()), reticula.indice(pared.hacia()));
            if (estabanSeparadas) {
                reticula.abrir(matriz, pared.pared(), ordenApertura);
                abiertas++;
            } else {
                rechazadas++;
            }
        }

        reticula.colocarInicioYSalida(matriz, ordenApertura);
        return new ResultadoGeneracion(Laberinto.desdeMatriz(matriz, semilla), nombre(), ordenApertura,
                abiertas, rechazadas);
    }

    @Override
    public String nombre() {
        return "Kruskal";
    }
}
