package laberinto.modelo;

import laberinto.estructuras.ListaDoble;

// Matriz de celdas y costos
public final class Laberinto {
    public static final int COSTO_NORMAL = 1;
    private static final int[] TAMANIOS_PERMITIDOS = {10, 15, 21};

    private final char[][] celdas;
    private final int[][] costos;
    private final long semilla;
    private final Coordenada inicio;
    private final Coordenada salida;

    private Laberinto(char[][] celdas, int[][] costos, long semilla) {
        this.celdas = celdas;
        this.costos = costos;
        this.semilla = semilla;
        this.inicio = buscarCeldaUnica(TipoCelda.INICIO);
        this.salida = buscarCeldaUnica(TipoCelda.SALIDA);
    }

    public static Laberinto desdeFilas(String[] filas, long semilla) {
        validarTamanio(filas.length);
        char[][] matriz = new char[filas.length][];
        for (int fila = 0; fila < filas.length; fila++) {
            if (filas[fila].length() != filas.length) {
                throw new IllegalArgumentException("La fila " + fila + " tiene " + filas[fila].length()
                        + " columnas; se esperaban " + filas.length + ".");
            }
            matriz[fila] = filas[fila].toCharArray();
        }
        return desdeMatriz(matriz, semilla);
    }

    public static Laberinto desdeMatriz(char[][] matriz, long semilla) {
        int tamanio = matriz.length;
        validarTamanio(tamanio);
        char[][] copia = new char[tamanio][tamanio];
        int[][] costos = new int[tamanio][tamanio];
        for (int fila = 0; fila < tamanio; fila++) {
            for (int columna = 0; columna < tamanio; columna++) {
                TipoCelda.desdeSimbolo(matriz[fila][columna]);
                copia[fila][columna] = matriz[fila][columna];
                costos[fila][columna] = COSTO_NORMAL;
            }
        }
        return new Laberinto(copia, costos, semilla);
    }

    // Rechaza antes de crear matriz
    public static void validarTamanio(int tamanio) {
        if (!esTamanioPermitido(tamanio)) {
            throw new IllegalArgumentException("Tamaño " + tamanio + " rechazado: solo se aceptan 10, 15 o 21. "
                    + "No se creó ninguna matriz.");
        }
    }

    public static boolean esTamanioPermitido(int tamanio) {
        for (int permitido : TAMANIOS_PERMITIDOS) {
            if (permitido == tamanio) {
                return true;
            }
        }
        return false;
    }

    public static int[] tamaniosPermitidos() {
        return TAMANIOS_PERMITIDOS.clone();
    }

    public Laberinto copia() {
        Laberinto copia = desdeMatriz(celdas, semilla);
        for (int fila = 0; fila < tamanio(); fila++) {
            System.arraycopy(costos[fila], 0, copia.costos[fila], 0, tamanio());
        }
        return copia;
    }

    public int tamanio() {
        return celdas.length;
    }

    public long semilla() {
        return semilla;
    }

    public Coordenada inicio() {
        return inicio;
    }

    public Coordenada salida() {
        return salida;
    }

    public boolean estaDentro(Coordenada c) {
        return c.fila() >= 0 && c.columna() >= 0 && c.fila() < tamanio() && c.columna() < tamanio();
    }

    public TipoCelda tipoEn(Coordenada c) {
        return TipoCelda.desdeSimbolo(celdas[c.fila()][c.columna()]);
    }

    public boolean esTransitable(Coordenada c) {
        return estaDentro(c) && tipoEn(c).esTransitable();
    }

    public boolean esSalida(Coordenada c) {
        return estaDentro(c) && tipoEn(c) == TipoCelda.SALIDA;
    }

    public int costoDeEntrar(Coordenada c) {
        return costos[c.fila()][c.columna()];
    }

    public boolean esBarro(Coordenada c) {
        return costoDeEntrar(c) > COSTO_NORMAL;
    }

    public boolean tieneBarro() {
        for (int[] fila : costos) {
            for (int costo : fila) {
                if (costo > COSTO_NORMAL) {
                    return true;
                }
            }
        }
        return false;
    }

    public int costoDeCamino(ListaDoble<Coordenada> camino) {
        int[] total = {0};
        camino.recorrerIda(c -> {
            if (!c.equals(inicio)) {
                total[0] += costoDeEntrar(c);
            }
        });
        return total[0];
    }

    public char[][] copiaDeCeldas() {
        char[][] copia = new char[tamanio()][];
        for (int fila = 0; fila < tamanio(); fila++) {
            copia[fila] = celdas[fila].clone();
        }
        return copia;
    }

    public int[][] copiaDeCostos() {
        int[][] copia = new int[tamanio()][];
        for (int fila = 0; fila < tamanio(); fila++) {
            copia[fila] = costos[fila].clone();
        }
        return copia;
    }

    public void ponerCosto(Coordenada c, int costo) {
        if (!esTransitable(c)) {
            throw new IllegalArgumentException("Solo se puede poner barro en una celda transitable; " + c + " no lo es.");
        }
        if (costo < COSTO_NORMAL) {
            throw new IllegalArgumentException("El costo de una celda debe ser al menos " + COSTO_NORMAL + ".");
        }
        costos[c.fila()][c.columna()] = costo;
    }

    public void quitarBarro() {
        for (int[] fila : costos) {
            for (int columna = 0; columna < fila.length; columna++) {
                fila[columna] = COSTO_NORMAL;
            }
        }
    }

    public int sellarFila(int fila) {
        if (fila < 0 || fila >= tamanio()) {
            throw new IllegalArgumentException("La fila " + fila + " no existe en un laberinto de " + tamanio() + ".");
        }
        int selladas = 0;
        for (int columna = 0; columna < tamanio(); columna++) {
            if (celdas[fila][columna] == TipoCelda.CAMINO.simbolo()) {
                celdas[fila][columna] = TipoCelda.PARED.simbolo();
                costos[fila][columna] = COSTO_NORMAL;
                selladas++;
            }
        }
        return selladas;
    }

    private Coordenada buscarCeldaUnica(TipoCelda tipo) {
        Coordenada encontrada = null;
        for (int fila = 0; fila < tamanio(); fila++) {
            for (int columna = 0; columna < tamanio(); columna++) {
                if (celdas[fila][columna] == tipo.simbolo()) {
                    if (encontrada != null) {
                        throw new IllegalArgumentException("Hay más de una celda '" + tipo.simbolo() + "'.");
                    }
                    encontrada = new Coordenada(fila, columna);
                }
            }
        }
        if (encontrada == null) {
            throw new IllegalArgumentException("El laberinto no tiene celda '" + tipo.simbolo() + "'.");
        }
        return encontrada;
    }
}
