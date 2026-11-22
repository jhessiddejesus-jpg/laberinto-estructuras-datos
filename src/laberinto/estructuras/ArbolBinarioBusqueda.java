package laberinto.estructuras;

import java.util.function.Consumer;

// Árbol binario de búsqueda
public final class ArbolBinarioBusqueda<V> {
    public static final class Nodo<V> {
        private final int clave;
        private final V valor;
        private Nodo<V> izquierdo;
        private Nodo<V> derecho;

        private Nodo(int clave, V valor) {
            this.clave = clave;
            this.valor = valor;
        }

        public int clave() {
            return clave;
        }

        public V valor() {
            return valor;
        }

        public Nodo<V> izquierdo() {
            return izquierdo;
        }

        public Nodo<V> derecho() {
            return derecho;
        }

        public int numeroHijos() {
            return (izquierdo == null ? 0 : 1) + (derecho == null ? 0 : 1);
        }
    }

    public record Hallazgo<V>(Nodo<V> nodo, int nivel, ArregloDinamico<Nodo<V>> recorrido) {
        public boolean encontrado() {
            return nodo != null;
        }

        public String caminoComoTexto() {
            StringBuilder texto = new StringBuilder();
            for (int i = 0; i < recorrido.tamanio(); i++) {
                if (i > 0) {
                    texto.append(" → ");
                }
                texto.append(recorrido.obtener(i).clave());
            }
            return texto.toString();
        }
    }

    private Nodo<V> raiz;
    private int tamanio;

    public void insertar(int clave, V valor) {
        Nodo<V> nuevo = new Nodo<>(clave, valor);
        tamanio++;
        if (raiz == null) {
            raiz = nuevo;
            return;
        }
        Nodo<V> actual = raiz;
        while (true) {
            if (clave < actual.clave) {
                if (actual.izquierdo == null) {
                    actual.izquierdo = nuevo;
                    return;
                }
                actual = actual.izquierdo;
            } else {
                if (actual.derecho == null) {
                    actual.derecho = nuevo;
                    return;
                }
                actual = actual.derecho;
            }
        }
    }

    public Hallazgo<V> buscar(int clave) {
        ArregloDinamico<Nodo<V>> recorrido = new ArregloDinamico<>();
        Nodo<V> actual = raiz;
        int nivel = 0;
        while (actual != null) {
            recorrido.agregar(actual);
            if (clave == actual.clave) {
                return new Hallazgo<>(actual, nivel, recorrido);
            }
            actual = clave < actual.clave ? actual.izquierdo : actual.derecho;
            nivel++;
        }
        return new Hallazgo<>(null, -1, recorrido);
    }

    public void inorden(Consumer<Nodo<V>> accion) {
        inorden(raiz, accion);
    }

    private void inorden(Nodo<V> nodo, Consumer<Nodo<V>> accion) {
        if (nodo == null) {
            return;
        }
        inorden(nodo.izquierdo, accion);
        accion.accept(nodo);
        inorden(nodo.derecho, accion);
    }

    public ArregloDinamico<Integer> clavesInorden() {
        ArregloDinamico<Integer> claves = new ArregloDinamico<>();
        inorden(nodo -> claves.agregar(nodo.clave));
        return claves;
    }

    public ArregloDinamico<V> primeros(int k) {
        ArregloDinamico<V> mejores = new ArregloDinamico<>();
        inorden(nodo -> {
            if (mejores.tamanio() < k) {
                mejores.agregar(nodo.valor);
            }
        });
        return mejores;
    }

    // Nodos sin contar la raíz
    public int peso() {
        return tamanio == 0 ? 0 : tamanio - 1;
    }

    public int altura() {
        return altura(raiz);
    }

    private int altura(Nodo<V> nodo) {
        if (nodo == null) {
            return -1;
        }
        return 1 + Math.max(altura(nodo.izquierdo), altura(nodo.derecho));
    }

    public int hojas() {
        return hojas(raiz);
    }

    private int hojas(Nodo<V> nodo) {
        if (nodo == null) {
            return 0;
        }
        if (nodo.numeroHijos() == 0) {
            return 1;
        }
        return hojas(nodo.izquierdo) + hojas(nodo.derecho);
    }

    public int grado() {
        return grado(raiz);
    }

    private int grado(Nodo<V> nodo) {
        if (nodo == null) {
            return 0;
        }
        return Math.max(nodo.numeroHijos(), Math.max(grado(nodo.izquierdo), grado(nodo.derecho)));
    }

    // Cada nodo: cero o dos
    public boolean esCompleto() {
        return esCompleto(raiz);
    }

    private boolean esCompleto(Nodo<V> nodo) {
        if (nodo == null) {
            return true;
        }
        if (nodo.numeroHijos() == 1) {
            return false;
        }
        return esCompleto(nodo.izquierdo) && esCompleto(nodo.derecho);
    }

    public Nodo<V> raiz() {
        return raiz;
    }

    public int tamanio() {
        return tamanio;
    }

    public boolean estaVacio() {
        return tamanio == 0;
    }

    public void vaciar() {
        raiz = null;
        tamanio = 0;
    }
}
