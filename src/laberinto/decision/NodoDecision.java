package laberinto.decision;

import java.util.function.Predicate;

// Pregunta u hoja del árbol
public final class NodoDecision {
    private final String texto;
    private final Predicate<ContextoVecino> condicion;
    private final NodoDecision hijoSi;
    private final NodoDecision hijoNo;
    private final Accion accion;

    private NodoDecision(String texto, Predicate<ContextoVecino> condicion,
                         NodoDecision hijoSi, NodoDecision hijoNo, Accion accion) {
        this.texto = texto;
        this.condicion = condicion;
        this.hijoSi = hijoSi;
        this.hijoNo = hijoNo;
        this.accion = accion;
    }

    public static NodoDecision pregunta(String texto, Predicate<ContextoVecino> condicion,
                                        NodoDecision siEsVerdad, NodoDecision siEsFalso) {
        return new NodoDecision(texto, condicion, siEsVerdad, siEsFalso, null);
    }

    public static NodoDecision hoja(Accion accion) {
        return new NodoDecision(accion.descripcion(), null, null, null, accion);
    }

    public boolean esHoja() {
        return accion != null;
    }

    public boolean responder(ContextoVecino contexto) {
        return condicion.test(contexto);
    }

    public String texto() {
        return texto;
    }

    public NodoDecision hijoSi() {
        return hijoSi;
    }

    public NodoDecision hijoNo() {
        return hijoNo;
    }

    public Accion accion() {
        return accion;
    }
}
