package laberinto.modelo;

// Símbolos de la matriz
public enum TipoCelda {
    PARED('#'),
    CAMINO('.'),
    INICIO('S'),
    SALIDA('E');

    private final char simbolo;

    TipoCelda(char simbolo) {
        this.simbolo = simbolo;
    }

    public char simbolo() {
        return simbolo;
    }

    public boolean esTransitable() {
        return this != PARED;
    }

    public static TipoCelda desdeSimbolo(char simbolo) {
        for (TipoCelda tipo : values()) {
            if (tipo.simbolo == simbolo) {
                return tipo;
            }
        }
        throw new IllegalArgumentException("Símbolo de celda desconocido: '" + simbolo + "'. Use #, ., S o E.");
    }
}
