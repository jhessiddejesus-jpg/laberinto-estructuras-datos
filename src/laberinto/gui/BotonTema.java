package laberinto.gui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JButton;
import javax.swing.border.EmptyBorder;

// Botón con estilo propio
public final class BotonTema extends JButton {
    private static final long serialVersionUID = 1L;

    public enum Estilo {
        PRIMARIO, SECUNDARIO, PELIGRO
    }

    private final Estilo estilo;
    private boolean mouseEncima;

    public BotonTema(String texto, Estilo estilo) {
        super(texto);
        this.estilo = estilo;
        setFont(Tema.FUENTE_BOTON);
        setForeground(estilo == Estilo.SECUNDARIO ? Tema.TEXTO : Color.WHITE);
        setFocusPainted(false);
        setContentAreaFilled(false);
        setBorderPainted(false);
        setOpaque(false);
        setBorder(new EmptyBorder(9, 14, 9, 14));
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        setAlignmentX(LEFT_ALIGNMENT);
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                mouseEncima = true;
                repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                mouseEncima = false;
                repaint();
            }
        });
    }

    public BotonTema anchoCompleto() {
        setMaximumSize(new Dimension(Integer.MAX_VALUE, getPreferredSize().height));
        return this;
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        Tema.suavizar(g2);
        int ancho = getWidth() - 1;
        int alto = getHeight() - 1;

        g2.setColor(colorDeFondo());
        g2.fillRoundRect(0, 0, ancho, alto, Tema.RADIO, Tema.RADIO);
        if (estilo == Estilo.SECUNDARIO) {
            g2.setColor(mouseEncima && isEnabled() ? Tema.PRIMARIO : Tema.BORDE);
            g2.drawRoundRect(0, 0, ancho, alto, Tema.RADIO, Tema.RADIO);
        }
        if (isFocusOwner()) {
            g2.setColor(Tema.conAlfa(Tema.PRIMARIO, 160));
            g2.setStroke(new BasicStroke(2f));
            g2.drawRoundRect(1, 1, ancho - 2, alto - 2, Tema.RADIO, Tema.RADIO);
        }
        g2.dispose();
        super.paintComponent(g);
    }

    private Color colorDeFondo() {
        boolean presionado = getModel().isPressed();
        if (!isEnabled()) {
            return Tema.BORDE_SUAVE;
        }
        switch (estilo) {
            case PRIMARIO:
                return presionado || mouseEncima ? Tema.PRIMARIO_HOVER : Tema.PRIMARIO;
            case PELIGRO:
                return presionado || mouseEncima ? Tema.PELIGRO.darker() : Tema.PELIGRO;
            default:
                return presionado ? Tema.PRIMARIO_SUAVE : (mouseEncima ? Tema.SUPERFICIE_SUAVE : Tema.SUPERFICIE);
        }
    }
}
