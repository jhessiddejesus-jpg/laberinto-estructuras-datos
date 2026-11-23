package laberinto.gui;

// Acciones que pide la interfaz
public interface AccionesUsuario {
    void cargar(String semilla, String tamanio, boolean usarPrim);

    void resolverPorNiveles();

    void resolverEnProfundidad();

    void comparar();

    void resolverConDijkstra();

    void alternarBarroDelLaboratorio();

    void sellarFila(int fila);

    void restaurar();

    void probarArbolDelLaboratorio();

    void cargarRankingDePrueba();

    void correrVerificacion();
}
