package br.com.assistenteacademico.view;

import javax.swing.*;
import java.awt.*;

public final class EstiloVisual {

    public static final Color FUNDO = new Color(245, 247, 251);
    private EstiloVisual() {
    }

    public static void configurarTema() {
        Font fonte = new Font("SansSerif", Font.PLAIN, 14);
        UIManager.put("Label.font", fonte);
        UIManager.put("Button.font", fonte);
        UIManager.put("TextField.font", fonte);
        UIManager.put("PasswordField.font", fonte);
        UIManager.put("ComboBox.font", fonte);
        UIManager.put("Table.font", fonte);
        UIManager.put("TableHeader.font", fonte.deriveFont(Font.BOLD));
        UIManager.put("Panel.background", FUNDO);
        UIManager.put("OptionPane.messageFont", fonte);
    }

    public static class BotaoAcao extends JButton {
        private static final Color ROXO = new Color(111, 95, 211);
        private static final Color ROXO_HOVER = new Color(122, 105, 221);

        public BotaoAcao(String texto) {
            super(texto);
            setFont(new Font("SansSerif", Font.BOLD, 17));
            setForeground(Color.WHITE);
            setPreferredSize(new Dimension(230, 52));
            setFocusPainted(false);
            setContentAreaFilled(false);
            setOpaque(false);
            setBorder(BorderFactory.createEmptyBorder(12, 20, 12, 20));
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(getModel().isRollover() ? ROXO_HOVER : ROXO);
            g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 18, 18);
            g2.dispose();
            super.paintComponent(g);
        }
    }

    public static final class AcoesIcon implements Icon {
        @Override public int getIconWidth() { return 82; }
        @Override public int getIconHeight() { return 42; }

        @Override
        public void paintIcon(Component component, Graphics graphics, int x, int y) {
            Graphics2D g = (Graphics2D) graphics.create();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.setColor(new Color(246, 243, 255));
            g.fillRoundRect(x, y + 1, 38, 38, 10, 10);
            g.setColor(new Color(255, 240, 244));
            g.fillRoundRect(x + 44, y + 1, 38, 38, 10, 10);

            g.setColor(new Color(103, 82, 206));
            g.setStroke(new BasicStroke(3.2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g.drawLine(x + 11, y + 29, x + 27, y + 13);
            g.drawLine(x + 25, y + 11, x + 30, y + 16);
            g.drawLine(x + 11, y + 29, x + 18, y + 27);
            g.drawLine(x + 11, y + 29, x + 13, y + 22);

            g.setColor(new Color(227, 38, 79));
            g.setStroke(new BasicStroke(2.8f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g.drawRoundRect(x + 56, y + 14, 15, 18, 3, 3);
            g.drawLine(x + 53, y + 11, x + 74, y + 11);
            g.drawLine(x + 60, y + 7, x + 67, y + 7);
            g.drawLine(x + 61, y + 19, x + 61, y + 27);
            g.drawLine(x + 67, y + 19, x + 67, y + 27);
            g.dispose();
        }
    }

}
