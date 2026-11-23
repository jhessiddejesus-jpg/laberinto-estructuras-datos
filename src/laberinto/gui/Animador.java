package laberinto.gui;

import java.util.function.IntConsumer;

import javax.swing.Timer;

// Avanza una animación por cuadros
final class Animador {
    private static final int CUADROS_POR_SEGUNDO = 60;
    private static final double SEGUNDOS_MAXIMOS = 9.0;
    private static final double SEGUNDOS_POR_NIVEL = 0.9;

    private Timer temporizador;

    void iniciar(int total, int velocidad, IntConsumer alAvanzar, Runnable alTerminar) {
        detener();
        double segundos = SEGUNDOS_MAXIMOS - SEGUNDOS_POR_NIVEL * (velocidad - 1);
        int porCuadro = Math.max(1, (int) Math.ceil(total / (segundos * CUADROS_POR_SEGUNDO)));
        int[] progreso = {0};
        temporizador = new Timer(1000 / CUADROS_POR_SEGUNDO, evento -> {
            progreso[0] = Math.min(total, progreso[0] + porCuadro);
            alAvanzar.accept(progreso[0]);
            if (progreso[0] >= total) {
                detener();
                alTerminar.run();
            }
        });
        temporizador.start();
    }

    void detener() {
        if (temporizador != null) {
            temporizador.stop();
            temporizador = null;
        }
    }
}
