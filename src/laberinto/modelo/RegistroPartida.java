package laberinto.modelo;

// Partida guardada en ranking
public record RegistroPartida(long semilla, String algoritmo, int pasos, double milisegundos) {
    public static final long SIN_SEMILLA = 0;

    @Override
    public String toString() {
        if (semilla == SIN_SEMILLA) {
            return pasos + " pasos · " + algoritmo;
        }
        return String.format("%d pasos · semilla %d · %s · %.3f ms", pasos, semilla, algoritmo, milisegundos);
    }
}
