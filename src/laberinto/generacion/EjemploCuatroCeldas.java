package laberinto.generacion;

import laberinto.estructuras.ArregloDinamico;
import laberinto.estructuras.UnionFind;

// Ejemplo pequeño Kruskal y Prim
public final class EjemploCuatroCeldas {
    public static final String[] NOMBRES = {"A", "B", "C", "D"};

    public record AristaEjemplo(int desde, int hacia, int peso) {
        public String nombre() {
            return NOMBRES[desde] + "—" + NOMBRES[hacia];
        }
    }

    public record PasoEjemplo(AristaEjemplo arista, boolean aceptada, String explicacion) {
        @Override
        public String toString() {
            return (aceptada ? "Se acepta " : "Se rechaza ") + arista.nombre() + " (peso " + arista.peso() + "): " + explicacion;
        }
    }

    private static final AristaEjemplo[] ARISTAS = {
        new AristaEjemplo(0, 1, 1),
        new AristaEjemplo(0, 2, 2),
        new AristaEjemplo(1, 3, 3),
        new AristaEjemplo(2, 3, 4)
    };

    private EjemploCuatroCeldas() {
    }

    public static AristaEjemplo[] aristas() {
        return ARISTAS.clone();
    }

    public static ArregloDinamico<PasoEjemplo> kruskal() {
        ArregloDinamico<AristaEjemplo> ordenadas = new ArregloDinamico<>();
        for (AristaEjemplo arista : ARISTAS) {
            ordenadas.agregar(arista);
        }
        ordenadas.ordenar((a, b) -> Integer.compare(a.peso(), b.peso()));

        UnionFind conjuntos = new UnionFind(NOMBRES.length);
        ArregloDinamico<PasoEjemplo> pasos = new ArregloDinamico<>();
        for (int i = 0; i < ordenadas.tamanio(); i++) {
            AristaEjemplo arista = ordenadas.obtener(i);
            if (conjuntos.unir(arista.desde(), arista.hacia())) {
                pasos.agregar(new PasoEjemplo(arista, true,
                        NOMBRES[arista.desde()] + " y " + NOMBRES[arista.hacia()] + " estaban en conjuntos distintos."));
            } else {
                pasos.agregar(new PasoEjemplo(arista, false,
                        "A, B, C y D ya quedaron unidos; abrirla formaría un ciclo."));
            }
        }
        return pasos;
    }

    public static ArregloDinamico<PasoEjemplo> prim() {
        boolean[] dentro = new boolean[NOMBRES.length];
        boolean[] revisada = new boolean[ARISTAS.length];
        dentro[0] = true;
        ArregloDinamico<PasoEjemplo> pasos = new ArregloDinamico<>();

        while (true) {
            int elegida = fronteraMasBarata(dentro, revisada);
            if (elegida < 0) {
                return pasos;
            }
            revisada[elegida] = true;
            AristaEjemplo arista = ARISTAS[elegida];
            boolean desdeDentro = dentro[arista.desde()];
            boolean haciaDentro = dentro[arista.hacia()];
            if (desdeDentro && haciaDentro) {
                pasos.agregar(new PasoEjemplo(arista, false, "las dos celdas ya están en el laberinto."));
            } else {
                int nueva = desdeDentro ? arista.hacia() : arista.desde();
                dentro[nueva] = true;
                pasos.agregar(new PasoEjemplo(arista, true, NOMBRES[nueva] + " todavía no estaba: entra al laberinto."));
            }
        }
    }

    private static int fronteraMasBarata(boolean[] dentro, boolean[] revisada) {
        int mejor = -1;
        for (int i = 0; i < ARISTAS.length; i++) {
            boolean tocaZona = dentro[ARISTAS[i].desde()] || dentro[ARISTAS[i].hacia()];
            if (!revisada[i] && tocaZona && (mejor < 0 || ARISTAS[i].peso() < ARISTAS[mejor].peso())) {
                mejor = i;
            }
        }
        return mejor;
    }
}
