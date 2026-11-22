package laberinto.estructuras;

import java.util.function.Consumer;

// Lista doblemente enlazada propia
public final class ListaDoble<T> {
    private NodoDoble<T> primero;
    private NodoDoble<T> ultimo;
    private int tamanio;

    public void agregarAlFinal(T dato) {
        NodoDoble<T> nuevo = new NodoDoble<>(dato);
        if (estaVacia()) {
            primero = nuevo;
        } else {
            ultimo.siguiente = nuevo;
            nuevo.anterior = ultimo;
        }
        ultimo = nuevo;
        tamanio++;
    }

    public void agregarAlInicio(T dato) {
        NodoDoble<T> nuevo = new NodoDoble<>(dato);
        if (estaVacia()) {
            ultimo = nuevo;
        } else {
            nuevo.siguiente = primero;
            primero.anterior = nuevo;
        }
        primero = nuevo;
        tamanio++;
    }

    public void recorrerIda(Consumer<T> accion) {
        for (NodoDoble<T> nodo = primero; nodo != null; nodo = nodo.siguiente) {
            accion.accept(nodo.dato);
        }
    }

    // Usa el enlace anterior
    public void recorrerRegreso(Consumer<T> accion) {
        for (NodoDoble<T> nodo = ultimo; nodo != null; nodo = nodo.anterior) {
            accion.accept(nodo.dato);
        }
    }

    public T obtener(int indice) {
        if (indice < 0 || indice >= tamanio) {
            throw new IndexOutOfBoundsException("Índice " + indice + " fuera de la lista de tamaño " + tamanio + ".");
        }
        NodoDoble<T> nodo = primero;
        for (int i = 0; i < indice; i++) {
            nodo = nodo.siguiente;
        }
        return nodo.dato;
    }

    public T obtenerPrimero() {
        if (estaVacia()) {
            throw new EstructuraVaciaException("La lista está vacía: no tiene primer elemento.");
        }
        return primero.dato;
    }

    public T obtenerUltimo() {
        if (estaVacia()) {
            throw new EstructuraVaciaException("La lista está vacía: no tiene último elemento.");
        }
        return ultimo.dato;
    }

    public boolean contiene(T dato) {
        for (NodoDoble<T> nodo = primero; nodo != null; nodo = nodo.siguiente) {
            if (nodo.dato.equals(dato)) {
                return true;
            }
        }
        return false;
    }

    public String textoIda(String separador) {
        StringBuilder texto = new StringBuilder();
        recorrerIda(dato -> agregarConSeparador(texto, dato, separador));
        return texto.toString();
    }

    public String textoRegreso(String separador) {
        StringBuilder texto = new StringBuilder();
        recorrerRegreso(dato -> agregarConSeparador(texto, dato, separador));
        return texto.toString();
    }

    private static void agregarConSeparador(StringBuilder texto, Object dato, String separador) {
        if (texto.length() > 0) {
            texto.append(separador);
        }
        texto.append(dato);
    }

    public void vaciar() {
        primero = null;
        ultimo = null;
        tamanio = 0;
    }

    public int tamanio() {
        return tamanio;
    }

    public boolean estaVacia() {
        return tamanio == 0;
    }
}
