package laberinto.estructuras;

import java.util.Comparator;
import java.util.function.Consumer;

// Arreglo que crece solo
public final class ArregloDinamico<T> {
    private static final int CAPACIDAD_INICIAL = 8;

    private Object[] elementos = new Object[CAPACIDAD_INICIAL];
    private int tamanio;

    public void agregar(T dato) {
        if (tamanio == elementos.length) {
            Object[] mayor = new Object[elementos.length * 2];
            System.arraycopy(elementos, 0, mayor, 0, tamanio);
            elementos = mayor;
        }
        elementos[tamanio++] = dato;
    }

    @SuppressWarnings("unchecked")
    public T obtener(int indice) {
        validarIndice(indice);
        return (T) elementos[indice];
    }

    public T quitarIntercambiandoConUltimo(int indice) {
        T quitado = obtener(indice);
        elementos[indice] = elementos[tamanio - 1];
        elementos[--tamanio] = null;
        return quitado;
    }

    public boolean contiene(T dato) {
        for (int i = 0; i < tamanio; i++) {
            if (elementos[i].equals(dato)) {
                return true;
            }
        }
        return false;
    }

    public void recorrer(Consumer<T> accion) {
        for (int i = 0; i < tamanio; i++) {
            accion.accept(obtener(i));
        }
    }

    public void ordenar(Comparator<? super T> criterio) {
        if (tamanio < 2) {
            return;
        }
        Object[] auxiliar = new Object[tamanio];
        ordenarPorMezcla(0, tamanio - 1, auxiliar, criterio);
    }

    private void ordenarPorMezcla(int izquierda, int derecha, Object[] auxiliar, Comparator<? super T> criterio) {
        if (izquierda >= derecha) {
            return;
        }
        int medio = (izquierda + derecha) >>> 1;
        ordenarPorMezcla(izquierda, medio, auxiliar, criterio);
        ordenarPorMezcla(medio + 1, derecha, auxiliar, criterio);
        mezclar(izquierda, medio, derecha, auxiliar, criterio);
    }

    @SuppressWarnings("unchecked")
    private void mezclar(int izquierda, int medio, int derecha, Object[] auxiliar, Comparator<? super T> criterio) {
        int i = izquierda;
        int j = medio + 1;
        int k = izquierda;
        while (i <= medio && j <= derecha) {
            if (criterio.compare((T) elementos[i], (T) elementos[j]) <= 0) {
                auxiliar[k++] = elementos[i++];
            } else {
                auxiliar[k++] = elementos[j++];
            }
        }
        while (i <= medio) {
            auxiliar[k++] = elementos[i++];
        }
        while (j <= derecha) {
            auxiliar[k++] = elementos[j++];
        }
        System.arraycopy(auxiliar, izquierda, elementos, izquierda, derecha - izquierda + 1);
    }

    public String unir(String separador) {
        StringBuilder texto = new StringBuilder();
        for (int i = 0; i < tamanio; i++) {
            if (i > 0) {
                texto.append(separador);
            }
            texto.append(elementos[i]);
        }
        return texto.toString();
    }

    public void vaciar() {
        elementos = new Object[CAPACIDAD_INICIAL];
        tamanio = 0;
    }

    public int tamanio() {
        return tamanio;
    }

    public boolean estaVacio() {
        return tamanio == 0;
    }

    private void validarIndice(int indice) {
        if (indice < 0 || indice >= tamanio) {
            throw new IndexOutOfBoundsException("Índice " + indice + " fuera del arreglo de tamaño " + tamanio + ".");
        }
    }
}
