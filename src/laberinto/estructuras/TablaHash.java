package laberinto.estructuras;

// Tabla hash con encadenamiento
public final class TablaHash<V> {
    private static final class NodoHash<V> {
        private final long clave;
        private V valor;
        private NodoHash<V> siguiente;

        private NodoHash(long clave, V valor) {
            this.clave = clave;
            this.valor = valor;
        }
    }

    private final NodoHash<V>[] cubetas;
    private int tamanio;

    @SuppressWarnings({"unchecked", "rawtypes"})
    public TablaHash(int numeroCubetas) {
        if (numeroCubetas <= 0) {
            throw new IllegalArgumentException("La tabla necesita al menos una cubeta.");
        }
        cubetas = (NodoHash<V>[]) new NodoHash[numeroCubetas];
    }

    // Cubeta = semilla mod 5
    public int cubetaDe(long clave) {
        return Math.floorMod(clave, cubetas.length);
    }

    public void poner(long clave, V valor) {
        int indice = cubetaDe(clave);
        NodoHash<V> existente = buscarNodo(clave);
        if (existente != null) {
            existente.valor = valor;
            return;
        }
        NodoHash<V> nuevo = new NodoHash<>(clave, valor);
        if (cubetas[indice] == null) {
            cubetas[indice] = nuevo;
        } else {
            NodoHash<V> ultimo = cubetas[indice];
            while (ultimo.siguiente != null) {
                ultimo = ultimo.siguiente;
            }
            ultimo.siguiente = nuevo;
        }
        tamanio++;
    }

    public V obtener(long clave) {
        NodoHash<V> nodo = buscarNodo(clave);
        return nodo == null ? null : nodo.valor;
    }

    public boolean contiene(long clave) {
        return buscarNodo(clave) != null;
    }

    public ArregloDinamico<Long> clavesDeCubeta(int indice) {
        if (indice < 0 || indice >= cubetas.length) {
            throw new IndexOutOfBoundsException("La cubeta " + indice + " no existe.");
        }
        ArregloDinamico<Long> claves = new ArregloDinamico<>();
        for (NodoHash<V> nodo = cubetas[indice]; nodo != null; nodo = nodo.siguiente) {
            claves.agregar(nodo.clave);
        }
        return claves;
    }

    private NodoHash<V> buscarNodo(long clave) {
        for (NodoHash<V> nodo = cubetas[cubetaDe(clave)]; nodo != null; nodo = nodo.siguiente) {
            if (nodo.clave == clave) {
                return nodo;
            }
        }
        return null;
    }

    public int numeroCubetas() {
        return cubetas.length;
    }

    public int tamanio() {
        return tamanio;
    }
}
