package laberinto.generacion;

import java.util.Random;

import laberinto.estructuras.ArregloDinamico;
import laberinto.modelo.Coordenada;
import laberinto.modelo.Direccion;
import laberinto.modelo.Laberinto;
import laberinto.modelo.TipoCelda;

// Rejilla base para generar
final class ReticulaDeCeldas {
    private final int tamanio;
    private final int celdasPorLado;

    ReticulaDeCeldas(int tamanio) {
        Laberinto.validarTamanio(tamanio);
        this.tamanio = tamanio;
        int ultimoIndicePar = tamanio % 2 == 1 ? tamanio - 1 : tamanio - 2;
        this.celdasPorLado = ultimoIndicePar / 2 + 1;
    }

    int numeroCeldas() {
        return celdasPorLado * celdasPorLado;
    }

    int indice(Coordenada celda) {
        return (celda.fila() / 2) * celdasPorLado + celda.columna() / 2;
    }

    Coordenada celda(int indice) {
        return new Coordenada((indice / celdasPorLado) * 2, (indice % celdasPorLado) * 2);
    }

    char[][] matrizDeParedes() {
        char[][] matriz = new char[tamanio][tamanio];
        for (int fila = 0; fila < tamanio; fila++) {
            for (int columna = 0; columna < tamanio; columna++) {
                matriz[fila][columna] = TipoCelda.PARED.simbolo();
            }
        }
        return matriz;
    }

    ArregloDinamico<ParedEntreCeldas> paredesAlrededor(Coordenada celda, Random azar) {
        ArregloDinamico<ParedEntreCeldas> paredes = new ArregloDinamico<>();
        for (Direccion direccion : Direccion.ordenDeInsercion()) {
            Coordenada vecina = new Coordenada(celda.fila() + 2 * direccion.deltaFila(),
                    celda.columna() + 2 * direccion.deltaColumna());
            if (esCelda(vecina)) {
                paredes.agregar(new ParedEntreCeldas(celda, vecina, azar.nextDouble()));
            }
        }
        return paredes;
    }

    ArregloDinamico<ParedEntreCeldas> todasLasParedes(Random azar) {
        ArregloDinamico<ParedEntreCeldas> paredes = new ArregloDinamico<>();
        for (int i = 0; i < numeroCeldas(); i++) {
            Coordenada celda = celda(i);
            Coordenada sur = new Coordenada(celda.fila() + 2, celda.columna());
            Coordenada este = new Coordenada(celda.fila(), celda.columna() + 2);
            if (esCelda(sur)) {
                paredes.agregar(new ParedEntreCeldas(celda, sur, azar.nextDouble()));
            }
            if (esCelda(este)) {
                paredes.agregar(new ParedEntreCeldas(celda, este, azar.nextDouble()));
            }
        }
        return paredes;
    }

    void abrir(char[][] matriz, Coordenada c, ArregloDinamico<Coordenada> ordenApertura) {
        matriz[c.fila()][c.columna()] = TipoCelda.CAMINO.simbolo();
        ordenApertura.agregar(c);
    }

    void colocarInicioYSalida(char[][] matriz, ArregloDinamico<Coordenada> ordenApertura) {
        int ultimo = tamanio - 1;
        if (tamanio % 2 == 0) {
            abrir(matriz, new Coordenada(ultimo - 1, ultimo), ordenApertura);
            abrir(matriz, new Coordenada(ultimo, ultimo), ordenApertura);
        }
        matriz[0][0] = TipoCelda.INICIO.simbolo();
        matriz[ultimo][ultimo] = TipoCelda.SALIDA.simbolo();
    }

    private boolean esCelda(Coordenada c) {
        return c.fila() >= 0 && c.columna() >= 0
                && c.fila() / 2 < celdasPorLado && c.columna() / 2 < celdasPorLado
                && c.fila() % 2 == 0 && c.columna() % 2 == 0;
    }
}
