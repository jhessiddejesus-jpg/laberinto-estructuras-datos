package laberinto.decision;

import laberinto.estructuras.ArregloDinamico;

// Decide qué hacer con vecina
public final class ArbolDecision {
    public record Decision(Accion accion, ArregloDinamico<NodoDecision> ruta, ArregloDinamico<Boolean> respuestas) {
    }

    private final NodoDecision raiz = construir();

    private static NodoDecision construir() {
        NodoDecision preguntaVisitada = NodoDecision.pregunta("¿Ya fue visitada?",
                ContextoVecino::yaVisitada,
                NodoDecision.hoja(Accion.DESCARTAR),
                NodoDecision.hoja(Accion.AGREGAR_A_FRONTERA));

        NodoDecision preguntaSalida = NodoDecision.pregunta("¿Es la salida?",
                ContextoVecino::esSalida,
                NodoDecision.hoja(Accion.RECONSTRUIR_CAMINO),
                preguntaVisitada);

        return NodoDecision.pregunta("¿La celda se sale del tablero o es pared?",
                ContextoVecino::seSaleOEsPared,
                NodoDecision.hoja(Accion.NO_SE_VISITA),
                preguntaSalida);
    }

    public Accion decidir(ContextoVecino contexto) {
        NodoDecision actual = raiz;
        while (!actual.esHoja()) {
            actual = actual.responder(contexto) ? actual.hijoSi() : actual.hijoNo();
        }
        return actual.accion();
    }

    public Decision decidirConRuta(ContextoVecino contexto) {
        ArregloDinamico<NodoDecision> ruta = new ArregloDinamico<>();
        ArregloDinamico<Boolean> respuestas = new ArregloDinamico<>();
        NodoDecision actual = raiz;
        while (!actual.esHoja()) {
            ruta.agregar(actual);
            boolean respuesta = actual.responder(contexto);
            respuestas.agregar(respuesta);
            actual = respuesta ? actual.hijoSi() : actual.hijoNo();
        }
        ruta.agregar(actual);
        return new Decision(actual.accion(), ruta, respuestas);
    }

    public NodoDecision raiz() {
        return raiz;
    }
}
