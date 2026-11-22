package laberinto.estructuras;

import java.util.function.Consumer;

// Cola enlazada FIFO propia
public final class Cola<T> implements Frontera<T> {
    private NodoSimple<T> frente;
    private NodoSimple<T> fin;
    private int tamanio;

    public void encolar(T dato) {
        NodoSimple<T> nuevo = new NodoSimple<>(dato);
        if (estaVacia()) {
            frente = nuevo;
        } else {
            fin.siguiente = nuevo;
        }
        fin = nuevo;
        tamanio++;
    }

    public T desencolar() {
        T dato = verFrente();
        frente = frente.siguiente;
        if (frente == null) {
            fin = null;
        }
        tamanio--;
        return dato;
    }

    public T verFrente() {
        if (estaVacia()) {
            throw new EstructuraVaciaException("La cola está vacía: no hay elementos que atender.");
        }
        return frente.dato;
    }

    public void recorrer(Consumer<T> accion) {
        for (NodoSimple<T> nodo = frente; nodo != null; nodo = nodo.siguiente) {
            accion.accept(nodo.dato);
        }
    }

    @Override
    public void agregar(T dato) {
        encolar(dato);
    }

    @Override
    public T retirar() {
        return desencolar();
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
        return "encolar";
    }

    @Override
    public String nombreEstructura() {
        return "cola";
    }
}
