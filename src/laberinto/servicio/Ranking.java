package laberinto.servicio;

import laberinto.estructuras.ArbolBinarioBusqueda;
import laberinto.estructuras.ArregloDinamico;
import laberinto.modelo.RegistroPartida;

// Ranking de partidas en ABB
public final class Ranking {
    public static final int MEJORES = 5;

    private static final int[] PASOS_DE_PRUEBA = {22, 18, 30, 14, 25};
    private static final int PASOS_COLA = 18;
    private static final int PASOS_PILA = 22;

    public record Estadisticas(int nodos, int peso, int altura, int hojas, int grado, boolean completo) {
    }

    private final ArbolBinarioBusqueda<RegistroPartida> arbol = new ArbolBinarioBusqueda<>();

    public void registrar(RegistroPartida registro) {
        arbol.insertar(registro.pasos(), registro);
    }

    public void cargarPrueba(long semilla, double msPorNiveles, double msEnProfundidad) {
        arbol.vaciar();
        for (int pasos : PASOS_DE_PRUEBA) {
            registrar(registroDePrueba(pasos, semilla, msPorNiveles, msEnProfundidad));
        }
    }

    private static RegistroPartida registroDePrueba(int pasos, long semilla, double msCola, double msPila) {
        return switch (pasos) {
            case PASOS_COLA -> new RegistroPartida(semilla, "Por niveles (cola)", pasos, msCola);
            case PASOS_PILA -> new RegistroPartida(semilla, "En profundidad (pila)", pasos, msPila);
            default -> new RegistroPartida(RegistroPartida.SIN_SEMILLA, "registro de prueba", pasos, 0);
        };
    }

    public ArregloDinamico<RegistroPartida> mejores() {
        return arbol.primeros(MEJORES);
    }

    public ArregloDinamico<Integer> pasosEnInorden() {
        return arbol.clavesInorden();
    }

    public ArbolBinarioBusqueda.Hallazgo<RegistroPartida> buscar(int pasos) {
        return arbol.buscar(pasos);
    }

    public Estadisticas estadisticas() {
        return new Estadisticas(arbol.tamanio(), arbol.peso(), arbol.altura(), arbol.hojas(), arbol.grado(),
                arbol.esCompleto());
    }

    public ArbolBinarioBusqueda.Nodo<RegistroPartida> raiz() {
        return arbol.raiz();
    }

    public boolean estaVacio() {
        return arbol.estaVacio();
    }

    public void vaciar() {
        arbol.vaciar();
    }
}
