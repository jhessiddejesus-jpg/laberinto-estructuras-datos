package laberinto.estructuras;

// Error al leer estructura vacía
public class EstructuraVaciaException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public EstructuraVaciaException(String mensaje) {
        super(mensaje);
    }
}
