package laberinto.modelo;

// Evento de la exploración
public record PasoExploracion(Coordenada celda, Tipo tipo) {
    public enum Tipo {
        AGREGADA,
        EXPANDIDA
    }

    public static PasoExploracion agregada(Coordenada celda) {
        return new PasoExploracion(celda, Tipo.AGREGADA);
    }

    public static PasoExploracion expandida(Coordenada celda) {
        return new PasoExploracion(celda, Tipo.EXPANDIDA);
    }
}
