package laberinto.servicio;

import laberinto.estructuras.ArregloDinamico;
import laberinto.estructuras.TablaHash;
import laberinto.generacion.GeneradorLaberinto;
import laberinto.generacion.ResultadoGeneracion;
import laberinto.modelo.Laberinto;
import laberinto.modelo.LaberintosPredefinidos;

// Guarda laberintos por semilla
public final class RepositorioSemillas {
    public static final int NUMERO_CUBETAS = 5;

    public record Consulta(Laberinto laberinto, boolean reutilizado, int cubeta, ResultadoGeneracion generacion) {
    }

    private final TablaHash<Laberinto> tabla = new TablaHash<>(NUMERO_CUBETAS);
    private int construcciones;

    public Consulta pedir(long semilla, int tamanio, GeneradorLaberinto generador) {
        Laberinto.validarTamanio(tamanio);
        int cubeta = tabla.cubetaDe(semilla);
        Laberinto guardado = tabla.obtener(semilla);
        if (guardado != null && (esSemillaPublicada(semilla) || guardado.tamanio() == tamanio)) {
            return new Consulta(guardado, true, cubeta, null);
        }

        construcciones++;
        if (esSemillaPublicada(semilla)) {
            Laberinto publicado = LaberintosPredefinidos.semilla2026();
            tabla.poner(semilla, publicado);
            return new Consulta(publicado, false, cubeta, null);
        }
        ResultadoGeneracion generacion = generador.generar(tamanio, semilla);
        tabla.poner(semilla, generacion.laberinto());
        return new Consulta(generacion.laberinto(), false, cubeta, generacion);
    }

    private static boolean esSemillaPublicada(long semilla) {
        return semilla == LaberintosPredefinidos.SEMILLA_LABORATORIO;
    }

    public int cubetaDe(long semilla) {
        return tabla.cubetaDe(semilla);
    }

    public ArregloDinamico<Long> semillasEnCubeta(int cubeta) {
        return tabla.clavesDeCubeta(cubeta);
    }

    public boolean contiene(long semilla) {
        return tabla.contiene(semilla);
    }

    public int tamanioGuardado(long semilla) {
        Laberinto guardado = tabla.obtener(semilla);
        return guardado == null ? 0 : guardado.tamanio();
    }

    public int numeroCubetas() {
        return tabla.numeroCubetas();
    }

    public int semillasGuardadas() {
        return tabla.tamanio();
    }

    public int construcciones() {
        return construcciones;
    }
}
