package laberinto.modelo;

// Datos publicados del laboratorio
public final class LaberintosPredefinidos {
    public static final long SEMILLA_LABORATORIO = 2026;
    public static final long SEMILLA_COLISION = 2031;

    public static final Coordenada CELDA_BARRO = new Coordenada(4, 2);
    public static final int COSTO_BARRO = 4;

    public static final int FILA_SELLADA = 5;

    private static final String[] FILAS_SEMILLA_2026 = {
        "S#...##...",
        ".....#.#..",
        "...##.....",
        "#..#....#.",
        ".....#....",
        ".#....####",
        "..#...#...",
        "..........",
        "..###.....",
        ".#.......E"
    };

    private LaberintosPredefinidos() {
    }

    public static Laberinto semilla2026() {
        return Laberinto.desdeFilas(FILAS_SEMILLA_2026, SEMILLA_LABORATORIO);
    }
}
