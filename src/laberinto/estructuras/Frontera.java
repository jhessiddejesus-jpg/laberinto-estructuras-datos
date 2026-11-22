package laberinto.estructuras;

// Contrato común cola y pila
public interface Frontera<T> {
    void agregar(T dato);

    T retirar();

    boolean estaVacia();

    int tamanio();

    String verboAgregar();

    String nombreEstructura();
}
