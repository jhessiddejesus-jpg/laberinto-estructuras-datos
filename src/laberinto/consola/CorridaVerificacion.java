package laberinto.consola;

import java.io.PrintStream;

import laberinto.decision.Accion;
import laberinto.decision.ArbolDecision;
import laberinto.estructuras.ArbolBinarioBusqueda;
import laberinto.estructuras.ArregloDinamico;
import laberinto.generacion.EjemploCuatroCeldas;
import laberinto.generacion.GeneradorLaberinto;
import laberinto.generacion.VerificadorSinCiclos;
import laberinto.grafo.Grafo;
import laberinto.modelo.Coordenada;
import laberinto.modelo.DibujoTexto;
import laberinto.modelo.Laberinto;
import laberinto.modelo.LaberintosPredefinidos;
import laberinto.modelo.RegistroPartida;
import laberinto.modelo.ResultadoBusqueda;
import laberinto.servicio.JuegoLaberinto;
import laberinto.servicio.Ranking;
import laberinto.servicio.RepositorioSemillas;

// Comprueba los resultados esperados
public final class CorridaVerificacion {
    private static final String FLECHA = " → ";

    private static final String IDA_COLA = "(0,0) → (1,0) → (2,0) → (2,1) → (3,1) → (4,1) → (4,2) → (5,2) → (5,3) → "
            + "(6,3) → (7,3) → (7,4) → (7,5) → (8,5) → (9,5) → (9,6) → (9,7) → (9,8) → (9,9)";
    private static final String REGRESO_COLA = "(9,9) → (9,8) → (9,7) → (9,6) → (9,5) → (8,5) → (7,5) → (7,4) → "
            + "(7,3) → (6,3) → (5,3) → (5,2) → (4,2) → (4,1) → (3,1) → (2,1) → (2,0) → (1,0) → (0,0)";
    private static final String IDA_PILA = "(0,0) → (1,0) → (1,1) → (1,2) → (2,2) → (3,2) → (3,1) → (4,1) → (4,0) → "
            + "(5,0) → (6,0) → (6,1) → (7,1) → (7,2) → (7,3) → (7,4) → (7,5) → (7,6) → (7,7) → (7,8) → (7,9) → "
            + "(8,9) → (9,9)";
    private static final String IDA_DIJKSTRA_BARRO = "(0,0) → (1,0) → (1,1) → (2,1) → (3,1) → (4,1) → (4,0) → "
            + "(5,0) → (6,0) → (6,1) → (7,1) → (7,2) → (7,3) → (7,4) → (7,5) → (7,6) → (7,7) → (7,8) → (7,9) → "
            + "(8,9) → (9,9)";

    private final PrintStream salida;
    private final JuegoLaberinto juego = new JuegoLaberinto();
    private int aprobadas;
    private int fallas;

    private ResultadoBusqueda porNiveles;
    private ResultadoBusqueda enProfundidad;

    public CorridaVerificacion(PrintStream salida) {
        this.salida = salida;
    }

    public int ejecutar() {
        salida.println("LABORATORIO DE ESTRUCTURAS DE DATOS · Proyecto 5 · Corrida de verificación");
        semilla2026();
        recorridoPorNiveles();
        recorridoEnProfundidad();
        arbolDeDecision();
        tablaDeSemillas();
        ranking();
        grafo();
        barro();
        filaSellada();
        tamaniosYGeneradores();
        ejemploCuatroCeldas();
        titulo("Resumen");
        salida.println("  " + aprobadas + " comprobaciones correctas, " + fallas + " fallas.");
        return fallas;
    }

    private void semilla2026() {
        titulo("Semilla 2026");
        RepositorioSemillas.Consulta consulta = juego.cargarSemillaDelLaboratorio();
        Laberinto laberinto = consulta.laberinto();
        salida.print(DibujoTexto.conIndices(laberinto));
        comprobar(laberinto.tamanio() == 10, "Diez líneas de diez celdas");
        comprobar(laberinto.inicio().equals(new Coordenada(0, 0)), "S en la esquina superior izquierda (0,0)");
        comprobar(laberinto.salida().equals(new Coordenada(9, 9)), "E en la esquina inferior derecha (9,9)");
    }

    private void recorridoPorNiveles() {
        titulo("Por niveles (cola)");
        porNiveles = juego.resolverPorNiveles();
        dato("Pasos", String.valueOf(porNiveles.pasos()));
        dato("Ida", porNiveles.camino().textoIda(FLECHA));
        dato("Regreso", porNiveles.camino().textoRegreso(FLECHA) + "   (enlace anterior)");
        salida.print(DibujoTexto.conCamino(juego.laberintoActual(), porNiveles.camino()));
        comprobar(porNiveles.pasos() == 18, "La cola encuentra la salida en 18 pasos");
        comprobar(porNiveles.camino().textoIda(FLECHA).equals(IDA_COLA), "La ida coincide con el camino publicado");
        comprobar(porNiveles.camino().textoRegreso(FLECHA).equals(REGRESO_COLA),
                "El regreso (9,9) → ... → (0,0) se lee con el enlace anterior");
    }

    private void recorridoEnProfundidad() {
        titulo("En profundidad (pila)");
        enProfundidad = juego.resolverEnProfundidad();
        dato("Pasos", enProfundidad.pasos() + ". No es la distancia mínima.");
        dato("Ida", enProfundidad.camino().textoIda(FLECHA));
        dato("Orden", "se apilan sur, norte, este, oeste → se explora primero el oeste");
        comprobar(enProfundidad.pasos() == 22, "La pila usa 22 pasos");
        comprobar(enProfundidad.camino().textoIda(FLECHA).equals(IDA_PILA), "El camino coincide con la referencia publicada");
        comprobar(enProfundidad.pasos() > porNiveles.pasos(), "22 > 18: la longitud de la pila no es la distancia mínima");
        salida.println("  Por qué: la pila sigue un pasillo hasta el fondo y se queda con el primer camino que");
        salida.println("  toca la salida; solo la cola, que avanza por niveles, garantiza el menor número de pasos.");
    }

    private void arbolDeDecision() {
        titulo("Árbol de decisión");
        ArbolDecision.Decision pared = juego.probarDecision(new Coordenada(0, 1));
        ArbolDecision.Decision nueva = juego.probarDecision(new Coordenada(1, 0));
        dato("(0,1) pared", pared.accion().descripcion());
        dato("(1,0) camino nuevo", Accion.AGREGAR_A_FRONTERA.descripcionPara("encolar")
                + "   (con la pila: " + Accion.AGREGAR_A_FRONTERA.descripcionPara("apilar") + ")");
        comprobar(pared.accion() == Accion.NO_SE_VISITA, "(0,1) no se visita");
        comprobar(nueva.accion() == Accion.AGREGAR_A_FRONTERA, "(1,0) entra a la frontera");
        comprobar(pared.accion() != nueva.accion(), "Las dos celdas reciben acciones distintas: el árbol sí decide");
    }

    private void tablaDeSemillas() {
        titulo("Tabla hash de semillas (5 cubetas)");
        Laberinto primera2026 = juego.laberintoGuardado();
        juego.cargar(LaberintosPredefinidos.SEMILLA_COLISION, 10, juego.kruskal());
        RepositorioSemillas.Consulta otraVez = juego.cargar(LaberintosPredefinidos.SEMILLA_LABORATORIO, 10, juego.kruskal());
        RepositorioSemillas repositorio = juego.repositorio();

        dato("2026 mod 5", String.valueOf(repositorio.cubetaDe(2026)));
        dato("2031 mod 5", String.valueOf(repositorio.cubetaDe(2031)));
        dato("Cubeta 1", repositorio.semillasEnCubeta(1).unir(FLECHA));
        dato("Construcciones", repositorio.construcciones() + " (la segunda petición de 2026 no construye)");
        comprobar(repositorio.cubetaDe(2026) == 1 && repositorio.cubetaDe(2031) == 1, "2026 y 2031 caen en la cubeta 1");
        comprobar(repositorio.semillasEnCubeta(1).contiene(2026L) && repositorio.semillasEnCubeta(1).contiene(2031L),
                "Las dos se recuperan recorriendo la lista de esa cubeta");
        comprobar(otraVez.reutilizado() && otraVez.laberinto() == primera2026,
                "La segunda petición de 2026 reutiliza la misma matriz guardada");
    }

    private void ranking() {
        titulo("Ranking (árbol binario de búsqueda)");
        Ranking ranking = juego.ranking();
        ranking.cargarPrueba(LaberintosPredefinidos.SEMILLA_LABORATORIO, porNiveles.milisegundos(), enProfundidad.milisegundos());
        Ranking.Estadisticas estadisticas = ranking.estadisticas();
        ArbolBinarioBusqueda.Hallazgo<RegistroPartida> hallazgo = ranking.buscar(18);

        dato("Insertados", "22, 18, 30, 14, 25");
        dato("Inorden", ranking.pasosEnInorden().unir(", "));
        dato("Medidas", "peso " + estadisticas.peso() + ", altura " + estadisticas.altura() + ", hojas "
                + estadisticas.hojas() + ", grado " + estadisticas.grado() + ", completo "
                + (estadisticas.completo() ? "sí" : "no"));
        dato("Buscar 18", "camino " + hallazgo.caminoComoTexto() + ", nivel " + hallazgo.nivel());
        dato("Registro 18", hallazgo.nodo().valor().toString());
        ArregloDinamico<RegistroPartida> mejores = ranking.mejores();
        for (int i = 0; i < mejores.tamanio(); i++) {
            salida.println("    " + (i + 1) + ". " + mejores.obtener(i));
        }
        comprobar(ranking.pasosEnInorden().unir(", ").equals("14, 18, 22, 25, 30"), "Inorden 14, 18, 22, 25, 30");
        comprobar(estadisticas.peso() == 4 && estadisticas.altura() == 2 && !estadisticas.completo(),
                "Peso 4, altura 2 y no es completo");
        comprobar(hallazgo.encontrado() && hallazgo.nivel() == 1, "18 está en el nivel 1, bajando por 22 → 18");
    }

    private void grafo() {
        titulo("Grafo construido desde la matriz");
        Grafo grafo = juego.grafoActual();
        int transitables = contarTransitables(juego.laberintoActual());
        dato("Nodos", grafo.numeroNodos() + " (una por cada celda que no es pared)");
        dato("Aristas", grafo.numeroAristas() + " (pasos entre vecinas libres)");
        dato("Grado de (0,0)", String.valueOf(grafo.nodo(grafo.idDe(new Coordenada(0, 0))).grado()));
        comprobar(grafo.numeroNodos() == transitables, "Un nodo por cada celda transitable");
    }

    private void barro() {
        titulo("Barro: entrar a (4,2) cuesta 4");
        juego.restaurar();
        juego.alternarBarro(LaberintosPredefinidos.CELDA_BARRO, LaberintosPredefinidos.COSTO_BARRO);
        ResultadoBusqueda dijkstra = juego.resolverConDijkstra();
        ResultadoBusqueda colaConBarro = juego.resolverPorNiveles();
        dato("Dijkstra", "costo " + dijkstra.costo() + ", " + dijkstra.pasos() + " pasos");
        dato("Camino", dijkstra.camino().textoIda(FLECHA));
        dato("Por niveles", colaConBarro.pasos() + " pasos por (4,2), costo real " + colaConBarro.costo()
                + " (la cola no mira el peso)");
        comprobar(dijkstra.costo() == 20, "Dijkstra llega con costo 20");
        comprobar(!dijkstra.camino().contiene(LaberintosPredefinidos.CELDA_BARRO), "Dijkstra evita la celda (4,2)");
        comprobar(dijkstra.camino().textoIda(FLECHA).equals(IDA_DIJKSTRA_BARRO), "El rodeo coincide con el publicado");
        comprobar(colaConBarro.pasos() == 18 && colaConBarro.camino().contiene(LaberintosPredefinidos.CELDA_BARRO),
                "La cola sigue en 18 pasos y pasa por (4,2)");
        comprobar(colaConBarro.costo() == 21, "Atravesar el barro habría costado 21: el rodeo conviene");

        juego.quitarBarro();
        ResultadoBusqueda dijkstraSinBarro = juego.resolverConDijkstra();
        dato("Sin barro", "Dijkstra = " + dijkstraSinBarro.costo() + ", cola = " + porNiveles.pasos());
        comprobar(dijkstraSinBarro.costo() == porNiveles.pasos(), "Sin barro, Dijkstra y la cola dan la misma distancia");
    }

    private void filaSellada() {
        titulo("Fila 5 toda en pared");
        juego.restaurar();
        juego.sellarFila(LaberintosPredefinidos.FILA_SELLADA);
        salida.print(DibujoTexto.conIndices(juego.laberintoActual()));
        ResultadoBusqueda cola = juego.resolverPorNiveles();
        ResultadoBusqueda pila = juego.resolverEnProfundidad();
        ResultadoBusqueda dijkstra = juego.resolverConDijkstra();
        salida.println("  " + cola.mensaje());
        salida.println("  " + pila.mensaje());
        salida.println("  " + dijkstra.mensaje());
        dato("Quedan vacías", "la cola (" + cola.elementosEnFrontera() + "), la pila (" + pila.elementosEnFrontera()
                + "), el montículo (" + dijkstra.elementosEnFrontera() + ") y la lista del camino ("
                + cola.camino().tamanio() + ")");
        dato("No quedan vacías", "las celdas marcadas (" + cola.celdasMarcadas() + " con la cola) y la matriz de padres");
        comprobar(!cola.encontrado() && !pila.encontrado() && !dijkstra.encontrado(), "Ningún algoritmo encuentra E");
        comprobar(cola.fronteraVacia() && pila.fronteraVacia(), "La cola y la pila terminan vacías");
        comprobar(cola.camino().estaVacia() && pila.camino().estaVacia() && dijkstra.camino().estaVacia(),
                "La lista del camino permanece vacía: no se imprime un tramo como solución");
        juego.restaurar();
    }

    private void tamaniosYGeneradores() {
        titulo("Tamaños 15 y 21 con Kruskal y Prim");
        verificarGenerado(juego.kruskal(), 15, 1515);
        verificarGenerado(juego.kruskal(), 21, 2121);
        verificarGenerado(juego.prim(), 15, 1516);
        verificarGenerado(juego.prim(), 21, 2122);
        verificarGenerado(juego.kruskal(), 10, 2031);

        int guardadasAntes = juego.repositorio().semillasGuardadas();
        String mensaje = "";
        try {
            juego.cargar(99, 12, juego.kruskal());
        } catch (IllegalArgumentException rechazo) {
            mensaje = rechazo.getMessage();
        }
        dato("Tamaño 12", mensaje);
        comprobar(!mensaje.isEmpty() && juego.repositorio().semillasGuardadas() == guardadasAntes,
                "El tamaño 12 se rechaza y no crea la matriz");
        juego.cargarSemillaDelLaboratorio();
    }

    private void verificarGenerado(GeneradorLaberinto generador, int tamanio, long semilla) {
        RepositorioSemillas.Consulta consulta = juego.cargar(semilla, tamanio, generador);
        VerificadorSinCiclos.Verificacion verificacion = juego.verificarSinCiclos();
        ResultadoBusqueda cola = juego.resolverPorNiveles();
        String nombre = (consulta.generacion() == null ? "guardado" : consulta.generacion().algoritmo());
        dato(nombre + " " + tamanio + "×" + tamanio, "semilla " + semilla + ", " + verificacion.nodos() + " nodos, "
                + verificacion.aristas() + " aristas, salida a " + cola.pasos() + " pasos");
        comprobar(verificacion.sinCiclos() && cola.encontrado(),
                nombre + " " + tamanio + "×" + tamanio + " sin ciclos (aristas = nodos − 1) y con salida");
    }

    private void ejemploCuatroCeldas() {
        titulo("Ejemplo de cuatro celdas (A B / C D)");
        salida.println("  Kruskal:");
        int aceptadasKruskal = imprimirPasos(EjemploCuatroCeldas.kruskal());
        salida.println("  Prim (partiendo de A):");
        int aceptadasPrim = imprimirPasos(EjemploCuatroCeldas.prim());
        comprobar(aceptadasKruskal == 3 && aceptadasPrim == 3, "Quedan tres pasajes y ningún ciclo");
    }

    private int imprimirPasos(ArregloDinamico<EjemploCuatroCeldas.PasoEjemplo> pasos) {
        int aceptadas = 0;
        for (int i = 0; i < pasos.tamanio(); i++) {
            salida.println("    " + pasos.obtener(i));
            if (pasos.obtener(i).aceptada()) {
                aceptadas++;
            }
        }
        return aceptadas;
    }

    private static int contarTransitables(Laberinto laberinto) {
        int total = 0;
        for (int fila = 0; fila < laberinto.tamanio(); fila++) {
            for (int columna = 0; columna < laberinto.tamanio(); columna++) {
                if (laberinto.esTransitable(new Coordenada(fila, columna))) {
                    total++;
                }
            }
        }
        return total;
    }

    private void titulo(String texto) {
        salida.println();
        salida.println("── " + texto + " " + "─".repeat(Math.max(3, 60 - texto.length())));
    }

    private void dato(String etiqueta, String valor) {
        salida.printf("  %-18s %s%n", etiqueta, valor);
    }

    private void comprobar(boolean condicion, String descripcion) {
        if (condicion) {
            aprobadas++;
        } else {
            fallas++;
        }
        salida.println((condicion ? "  [OK]    " : "  [FALLA] ") + descripcion);
    }
}
