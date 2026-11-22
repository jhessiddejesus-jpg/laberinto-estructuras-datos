package laberinto.estructuras;

import java.util.function.Consumer;

// Pila enlazada LIFO propia
public final class Pila<T> implements Frontera<T> {
    private NodoSimple<T> cima;
    private int tamanio;

    public void apilar(T dato) {
        NodoSimple<T> nuevo = new NodoSimple<>(dato);
        nuevo.siguiente = cima;
        cima = nuevo;
        tamanio++;
    }

    public T desapilar() {
        T dato = verCima();
        cima = cima.siguiente;
        tamanio--;
        return dato;
    }

    public T verCima() {
        if (estaVacia()) {
            throw new EstructuraVaciaException("La pila está vacía: no hay elementos en la cima.");
        }
        return cima.dato;
    }

    public void recorrer(Consumer<T> accion) {
        for (NodoSimple<T> nodo = cima; nodo != null; nodo = nodo.siguiente) {
            accion.accept(nodo.dato);
        }
    }

    @Override
    public void agregar(T dato) {
        apilar(dato);
    }

    @Override
    public T retirar() {
        return desapilar();
    }

    @Override
    public boolean estaVacia() {
        return tamanio == 0;
    }

    @Override
    public int tamanio() {
        return tamanio;
    }

    @Override
    public String verboAgregar() {
        return "apilar";
    }

    @Override
    public String nombreEstructura() {
        return "pila";
    }
}
