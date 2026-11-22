package laberinto.generacion;

// Contrato de los generadores
public interface GeneradorLaberinto {
    ResultadoGeneracion generar(int tamanio, long semilla);

    String nombre();
}
