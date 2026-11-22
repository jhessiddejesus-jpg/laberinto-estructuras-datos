package laberinto.estructuras;

// Conjuntos disjuntos para Kruskal
public final class UnionFind {
    private final int[] padre;
    private final int[] rango;
    private int conjuntos;

    public UnionFind(int elementos) {
        padre = new int[elementos];
        rango = new int[elementos];
        conjuntos = elementos;
        for (int i = 0; i < elementos; i++) {
            padre[i] = i;
        }
    }

    public int buscar(int elemento) {
        if (padre[elemento] != elemento) {
            padre[elemento] = buscar(padre[elemento]);
        }
        return padre[elemento];
    }

    public boolean unir(int a, int b) {
        int raizA = buscar(a);
        int raizB = buscar(b);
        if (raizA == raizB) {
            return false;
        }
        if (rango[raizA] < rango[raizB]) {
            padre[raizA] = raizB;
        } else if (rango[raizA] > rango[raizB]) {
            padre[raizB] = raizA;
        } else {
            padre[raizB] = raizA;
            rango[raizA]++;
        }
        conjuntos--;
        return true;
    }

    public boolean mismoConjunto(int a, int b) {
        return buscar(a) == buscar(b);
    }

    public int numeroConjuntos() {
        return conjuntos;
    }
}
