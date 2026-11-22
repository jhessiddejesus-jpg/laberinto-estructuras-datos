package laberinto.modelo;

import laberinto.estructuras.ListaDoble;

// Convierte el laberinto en texto
public final class DibujoTexto {
    private static final char MARCA_CAMINO = '*';

    private DibujoTexto() {
    }

    public static String simple(Laberinto laberinto) {
        return unirFilas(laberinto.copiaDeCeldas());
    }

    public static String conIndices(Laberinto laberinto) {
        char[][] celdas = laberinto.copiaDeCeldas();
        StringBuilder texto = new StringBuilder("   ");
        for (int columna = 0; columna < celdas.length; columna++) {
            texto.append(columna % 10);
        }
        texto.append('\n');
        for (int fila = 0; fila < celdas.length; fila++) {
            texto.append(String.format("%2d ", fila)).append(celdas[fila]).append('\n');
        }
        return texto.toString();
    }

    public static String conCamino(Laberinto laberinto, ListaDoble<Coordenada> camino) {
        char[][] celdas = laberinto.copiaDeCeldas();
        camino.recorrerIda(c -> {
            if (celdas[c.fila()][c.columna()] == TipoCelda.CAMINO.simbolo()) {
                celdas[c.fila()][c.columna()] = MARCA_CAMINO;
            }
        });
        return unirFilas(celdas);
    }

    private static String unirFilas(char[][] celdas) {
        StringBuilder texto = new StringBuilder();
        for (char[] fila : celdas) {
            texto.append(fila).append('\n');
        }
        return texto.toString();
    }
}
