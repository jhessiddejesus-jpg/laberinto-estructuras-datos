package laberinto.modelo;

// Cuatro direcciones de movimiento
public enum Direccion {
    SUR(1, 0),
    NORTE(-1, 0),
    ESTE(0, 1),
    OESTE(0, -1);

    private static final Direccion[] ORDEN_DE_INSERCION = {SUR, NORTE, ESTE, OESTE};

    private final int deltaFila;
    private final int deltaColumna;

    Direccion(int deltaFila, int deltaColumna) {
        this.deltaFila = deltaFila;
        this.deltaColumna = deltaColumna;
    }

    public int deltaFila() {
        return deltaFila;
    }

    public int deltaColumna() {
        return deltaColumna;
    }

    public static Direccion[] ordenDeInsercion() {
        return ORDEN_DE_INSERCION.clone();
    }
}
