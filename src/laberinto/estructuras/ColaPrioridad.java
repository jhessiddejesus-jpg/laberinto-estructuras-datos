package laberinto.estructuras;

// Montículo mínimo para Dijkstra
public final class ColaPrioridad<T> {
    private static final int CAPACIDAD_INICIAL = 16;

    private static final class Entrada<T> {
        private final T dato;
        private final int prioridad;
        private final long orden;

        private Entrada(T dato, int prioridad, long orden) {
            this.dato = dato;
            this.prioridad = prioridad;
            this.orden = orden;
        }

        private boolean vaAntesQue(Entrada<T> otra) {
            if (prioridad != otra.prioridad) {
                return prioridad < otra.prioridad;
            }
            // Empate: sale el más reciente
            return orden > otra.orden;
        }
    }

    private Entrada<T>[] monticulo = crearArreglo(CAPACIDAD_INICIAL);
    private int tamanio;
    private long contadorLlegadas;

    public void insertar(T dato, int prioridad) {
        if (tamanio == monticulo.length) {
            Entrada<T>[] mayor = crearArreglo(monticulo.length * 2);
            System.arraycopy(monticulo, 0, mayor, 0, tamanio);
            monticulo = mayor;
        }
        monticulo[tamanio] = new Entrada<>(dato, prioridad, contadorLlegadas++);
        subir(tamanio);
        tamanio++;
    }

    public T extraerMinimo() {
        if (estaVacia()) {
            throw new EstructuraVaciaException("La cola de prioridad está vacía.");
        }
        T minimo = monticulo[0].dato;
        tamanio--;
        monticulo[0] = monticulo[tamanio];
        monticulo[tamanio] = null;
        if (tamanio > 0) {
            bajar(0);
        }
        return minimo;
    }

    public int prioridadMinima() {
        if (estaVacia()) {
            throw new EstructuraVaciaException("La cola de prioridad está vacía.");
        }
        return monticulo[0].prioridad;
    }

    private void subir(int indice) {
        while (indice > 0) {
            int padre = (indice - 1) / 2;
            if (!monticulo[indice].vaAntesQue(monticulo[padre])) {
                return;
            }
            intercambiar(indice, padre);
            indice = padre;
        }
    }

    private void bajar(int indice) {
        while (true) {
            int izquierdo = 2 * indice + 1;
            int derecho = izquierdo + 1;
            int menor = indice;
            if (izquierdo < tamanio && monticulo[izquierdo].vaAntesQue(monticulo[menor])) {
                menor = izquierdo;
            }
            if (derecho < tamanio && monticulo[derecho].vaAntesQue(monticulo[menor])) {
                menor = derecho;
            }
            if (menor == indice) {
                return;
            }
            intercambiar(indice, menor);
            indice = menor;
        }
    }

    private void intercambiar(int a, int b) {
        Entrada<T> temporal = monticulo[a];
        monticulo[a] = monticulo[b];
        monticulo[b] = temporal;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static <T> Entrada<T>[] crearArreglo(int capacidad) {
        return (Entrada<T>[]) new Entrada[capacidad];
    }

    public boolean estaVacia() {
        return tamanio == 0;
    }

    public int tamanio() {
        return tamanio;
    }
}
