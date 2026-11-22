package laberinto.estructuras;

// Nodo con enlace siguiente
final class NodoSimple<T> {
    final T dato;
    NodoSimple<T> siguiente;

    NodoSimple(T dato) {
        this.dato = dato;
    }
}
