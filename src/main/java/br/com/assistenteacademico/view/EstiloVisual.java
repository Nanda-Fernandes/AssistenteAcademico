package br.com.assistenteacademico.view;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.JTableHeader;
import java.awt.*;

public final class EstiloVisual {

    public static final Color AZUL = new Color(35, 91, 168);
    public static final Color AZUL_ESCURO = new Color(25, 65, 125);
    public static final Color FUNDO = new Color(245, 247, 251);
    public static final Color FUNDO_CARD = Color.WHITE;

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

    public static void configurarJanela(JFrame janela) {
        janela.getContentPane().setBackground(FUNDO);
    }

    public static void configurarPainel(JPanel painel) {
        painel.setBackground(FUNDO);
        painel.setBorder(new EmptyBorder(20, 20, 20, 20));
    }

    public static void configurarBotao(JButton botao, boolean destaque) {
        botao.setFont(botao.getFont().deriveFont(Font.BOLD));
        botao.setFocusPainted(false);
        botao.setCursor(new Cursor(Cursor.HAND_CURSOR));
        botao.setOpaque(true);
        botao.setBorder(BorderFactory.createEmptyBorder(10, 18, 10, 18));
        botao.setBackground(destaque ? AZUL : Color.WHITE);
        botao.setForeground(destaque ? Color.WHITE : AZUL_ESCURO);
    }

    public static void configurarTabela(JTable tabela) {
        tabela.setRowHeight(26);
        tabela.setShowGrid(false);
        tabela.setIntercellSpacing(new Dimension(0, 1));
        tabela.setSelectionBackground(new Color(215, 229, 250));
        tabela.setSelectionForeground(Color.BLACK);
        JTableHeader cabecalho = tabela.getTableHeader();
        cabecalho.setBackground(AZUL_ESCURO);
        cabecalho.setForeground(Color.WHITE);
        cabecalho.setReorderingAllowed(false);
    }
}
