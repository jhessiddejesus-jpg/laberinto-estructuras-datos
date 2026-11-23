package laberinto.gui;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.RenderingHints;

// Colores y fuentes
public final class Tema {
    public static final Color FONDO = new Color(0xEEF2FF);
    public static final Color SUPERFICIE = Color.WHITE;
    public static final Color SUPERFICIE_SUAVE = new Color(0xF8FAFF);
    public static final Color BORDE = new Color(0xC7D2FE);
    public static final Color BORDE_SUAVE = new Color(0xE2E8F0);
    public static final Color TEXTO = new Color(0x1E1B4B);
    public static final Color TEXTO_SUAVE = new Color(0x475569);

    public static final Color PRIMARIO = new Color(0x4F46E5);
    public static final Color PRIMARIO_HOVER = new Color(0x4338CA);
    public static final Color PRIMARIO_SUAVE = new Color(0xE0E7FF);
    public static final Color ACENTO = new Color(0xEA580C);
    public static final Color ACENTO_SUAVE = new Color(0xFFEDD5);
    public static final Color EXITO = new Color(0x15803D);
    public static final Color EXITO_SUAVE = new Color(0xDCFCE7);
    public static final Color AVISO = new Color(0xA16207);
    public static final Color AVISO_SUAVE = new Color(0xFEF3C7);
    public static final Color PELIGRO = new Color(0xDC2626);
    public static final Color PELIGRO_SUAVE = new Color(0xFEE2E2);

    public static final Color CELDA_PARED = new Color(0x1E1B4B);
    public static final Color CELDA_CAMINO = Color.WHITE;
    public static final Color CELDA_REJILLA = new Color(0xE2E8F0);
    public static final Color CELDA_INICIO = new Color(0x0072B2);
    public static final Color CELDA_SALIDA = new Color(0x009E73);
    public static final Color CELDA_FRONTERA = new Color(0xFDE68A);
    public static final Color CELDA_EXPLORADA = new Color(0xC7D2FE);
    public static final Color CELDA_SOLUCION = new Color(0xFED7AA);
    public static final Color LINEA_SOLUCION = ACENTO;
    public static final Color LINEA_PILA = new Color(0x7C3AED);
    public static final Color CELDA_BARRO = new Color(0x92400E);

    private static final String FAMILIA = elegirFamilia("Segoe UI", "Inter", "SansSerif");
    private static final String FAMILIA_MONO = elegirFamilia("Cascadia Mono", "Consolas", "Monospaced");

    public static final Font FUENTE_TITULO = new Font(FAMILIA, Font.BOLD, 20);
    public static final Font FUENTE_SUBTITULO = new Font(FAMILIA, Font.BOLD, 15);
    public static final Font FUENTE_SECCION = new Font(FAMILIA, Font.BOLD, 12);
    public static final Font FUENTE_TEXTO = new Font(FAMILIA, Font.PLAIN, 13);
    public static final Font FUENTE_TEXTO_FUERTE = new Font(FAMILIA, Font.BOLD, 13);
    public static final Font FUENTE_PEQUENA = new Font(FAMILIA, Font.PLAIN, 12);
    public static final Font FUENTE_BOTON = new Font(FAMILIA, Font.BOLD, 13);
    public static final Font FUENTE_METRICA = new Font(FAMILIA, Font.BOLD, 34);
    public static final Font FUENTE_MONO = new Font(FAMILIA_MONO, Font.PLAIN, 13);

    public static final int RADIO = 12;
    public static final int ESPACIO = 8;
    public static final int ANCHO_BARRA_LATERAL = 300;

    private Tema() {
    }

    public static void suavizar(Graphics2D g) {
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
    }

    public static Color conAlfa(Color color, int alfa) {
        return new Color(color.getRed(), color.getGreen(), color.getBlue(), alfa);
    }

    private static String elegirFamilia(String... candidatas) {
        String[] disponibles = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
        for (String candidata : candidatas) {
            for (String disponible : disponibles) {
                if (disponible.equalsIgnoreCase(candidata)) {
                    return disponible;
                }
            }
        }
        return candidatas[candidatas.length - 1];
    }
}
