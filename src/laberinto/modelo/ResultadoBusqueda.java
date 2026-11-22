package laberinto.modelo;

import laberinto.estructuras.ArregloDinamico;
import laberinto.estructuras.ListaDoble;

// Resultado de un recorrido
public final class ResultadoBusqueda {
    private final String algoritmo;
    private final String estructura;
    private final boolean garantizaMinimo;
    private final boolean encontrado;
    private final ListaDoble<Coordenada> camino;
    private final int costo;
    private final ArregloDinamico<PasoExploracion> exploracion;
    private final int celdasExpandidas;
    private final int celdasMarcadas;
    private final int elementosEnFrontera;
    private final long nanosegundos;

    private ResultadoBusqueda(Datos datos, boolean encontrado, ListaDoble<Coordenada> camino, int costo) {
        this.algoritmo = datos.algoritmo;
        this.estructura = datos.estructura;
        this.garantizaMinimo = datos.garantizaMinimo;
        this.exploracion = datos.exploracion;
        this.celdasExpandidas = datos.celdasExpandidas;
        this.celdasMarcadas = datos.celdasMarcadas;
        this.elementosEnFrontera = datos.elementosEnFrontera;
        this.nanosegundos = datos.nanosegundos;
        this.encontrado = encontrado;
        this.camino = camino;
        this.costo = costo;
    }

    public static final class Datos {
        private final String algoritmo;
        private final String estructura;
        private final boolean garantizaMinimo;
        private ArregloDinamico<PasoExploracion> exploracion = new ArregloDinamico<>();
        private int celdasExpandidas;
        private int celdasMarcadas;
        private int elementosEnFrontera;
        private long nanosegundos;

        public Datos(String algoritmo, String estructura, boolean garantizaMinimo) {
            this.algoritmo = algoritmo;
            this.estructura = estructura;
            this.garantizaMinimo = garantizaMinimo;
        }

        public Datos exploracion(ArregloDinamico<PasoExploracion> exploracion) {
            this.exploracion = exploracion;
            return this;
        }

        public Datos celdasExpandidas(int cantidad) {
            this.celdasExpandidas = cantidad;
            return this;
        }

        public Datos celdasMarcadas(int cantidad) {
            this.celdasMarcadas = cantidad;
            return this;
        }

        public Datos elementosEnFrontera(int cantidad) {
            this.elementosEnFrontera = cantidad;
            return this;
        }

        public Datos nanosegundos(long nanos) {
            this.nanosegundos = nanos;
            return this;
        }
    }

    public static ResultadoBusqueda exito(Datos datos, ListaDoble<Coordenada> camino, int costo) {
        return new ResultadoBusqueda(datos, true, camino, costo);
    }

    public static ResultadoBusqueda sinSalida(Datos datos) {
        return new ResultadoBusqueda(datos, false, new ListaDoble<>(), -1);
    }

    public int pasos() {
        return encontrado ? camino.tamanio() - 1 : -1;
    }

    public double milisegundos() {
        return nanosegundos / 1_000_000.0;
    }

    public String mensaje() {
        if (!encontrado) {
            return "No hay salida. La " + estructura + " terminó vacía y la lista del camino está vacía.";
        }
        String base = algoritmo + ": " + pasos() + " pasos, costo " + costo + ".";
        return garantizaMinimo ? base : base + " No es la distancia mínima.";
    }

    public String algoritmo() {
        return algoritmo;
    }

    public String estructura() {
        return estructura;
    }

    public boolean garantizaMinimo() {
        return garantizaMinimo;
    }

    public boolean encontrado() {
        return encontrado;
    }

    public ListaDoble<Coordenada> camino() {
        return camino;
    }

    public int costo() {
        return costo;
    }

    public ArregloDinamico<PasoExploracion> exploracion() {
        return exploracion;
    }

    public int celdasExpandidas() {
        return celdasExpandidas;
    }

    public int celdasMarcadas() {
        return celdasMarcadas;
    }

    public int elementosEnFrontera() {
        return elementosEnFrontera;
    }

    public boolean fronteraVacia() {
        return elementosEnFrontera == 0;
    }
}
