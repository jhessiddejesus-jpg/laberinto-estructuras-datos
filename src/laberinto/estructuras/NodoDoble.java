package laberinto.estructuras;

// Nodo con siguiente y anterior
final class NodoDoble<T> {
    final T dato;
    NodoDoble<T> anterior;
    NodoDoble<T> siguiente;

    NodoDoble(T dato) {
        this.dato = dato;
    }
}
