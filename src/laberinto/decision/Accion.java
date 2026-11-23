package laberinto.decision;

// Hojas del árbol decisión
public enum Accion {
    NO_SE_VISITA("no se visita"),
    RECONSTRUIR_CAMINO("se reconstruye el camino con los padres"),
    DESCARTAR("se descarta"),
    AGREGAR_A_FRONTERA("se marca y se encola, o se apila");

    private final String descripcion;

    Accion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String descripcion() {
        return descripcion;
    }

    public String descripcionPara(String verboAgregar) {
        if (this != AGREGAR_A_FRONTERA) {
            return descripcion;
        }
        return "se marca y se " + conjugar(verboAgregar);
    }

    private static String conjugar(String infinitivo) {
        return infinitivo.endsWith("r") ? infinitivo.substring(0, infinitivo.length() - 1) : infinitivo;
    }
}
