package laberinto.modelo;

// Posición fila y columna
public record Coordenada(int fila, int columna) {
    public Coordenada vecina(Direccion direccion) {
        return new Coordenada(fila + direccion.deltaFila(), columna + direccion.deltaColumna());
    }

    public Coordenada puntoMedio(Coordenada otra) {
        return new Coordenada((fila + otra.fila) / 2, (columna + otra.columna) / 2);
    }

    @Override
    public String toString() {
        return "(" + fila + "," + columna + ")";
    }
}
