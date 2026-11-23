package laberinto.generacion;

import java.util.Random;

import laberinto.estructuras.ArregloDinamico;
import laberinto.modelo.Coordenada;
import laberinto.modelo.Laberinto;

// Generador alternativo con Prim
public final class GeneradorPrim implements GeneradorLaberinto {
    @Override
    public ResultadoGeneracion generar(int tamanio, long semilla) {
        ReticulaDeCeldas reticula = new ReticulaDeCeldas(tamanio);
        Random azar = new Random(semilla);
        char[][] matriz = reticula.matrizDeParedes();
        ArregloDinamico<Coordenada> ordenApertura = new ArregloDinamico<>();
        boolean[] enLaberinto = new boolean[reticula.numeroCeldas()];

        Coordenada inicio = new Coordenada(0, 0);
        enLaberinto[reticula.indice(inicio)] = true;
        reticula.abrir(matriz, inicio, ordenApertura);
        ArregloDinamico<ParedEntreCeldas> frontera = reticula.paredesAlrededor(inicio, azar);

        int abiertas = 0;
        int rechazadas = 0;
        while (!frontera.estaVacio()) {
            ParedEntreCeldas pared = frontera.quitarIntercambiandoConUltimo(azar.nextInt(frontera.tamanio()));
            Coordenada otroLado = pared.hacia();
            if (enLaberinto[reticula.indice(otroLado)]) {
                rechazadas++;
                continue;
            }
            enLaberinto[reticula.indice(otroLado)] = true;
            reticula.abrir(matriz, pared.pared(), ordenApertura);
            reticula.abrir(matriz, otroLado, ordenApertura);
            abiertas++;
            ArregloDinamico<ParedEntreCeldas> nuevas = reticula.paredesAlrededor(otroLado, azar);
            for (int i = 0; i < nuevas.tamanio(); i++) {
                if (!enLaberinto[reticula.indice(nuevas.obtener(i).hacia())]) {
                    frontera.agregar(nuevas.obtener(i));
                }
            }
        }

        reticula.colocarInicioYSalida(matriz, ordenApertura);
        return new ResultadoGeneracion(Laberinto.desdeMatriz(matriz, semilla), nombre(), ordenApertura,
                abiertas, rechazadas);
    }

    @Override
    public String nombre() {
        return "Prim";
    }
}
