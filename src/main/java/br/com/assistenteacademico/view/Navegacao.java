package br.com.assistenteacademico.view;

import javax.swing.*;
import java.awt.*;
import java.util.function.Supplier;

final class Navegacao {
    private static JFrame hospedeira;
    private static boolean preparando;

    private Navegacao() {
    }

    static boolean estaPreparando() {
        return preparando;
    }

    static void trocar(JFrame atual, Supplier<JFrame> criador) {
        if (hospedeira == null) {
            hospedeira = atual;
        }

        JFrame proxima;
        preparando = true;
        try {
            proxima = criador.get();
        } finally {
            preparando = false;
        }

        Container conteudo = proxima.getContentPane();
        proxima.setContentPane(new JPanel());
        String titulo = proxima.getTitle();
        proxima.dispose();

        hospedeira.setTitle(titulo);
        hospedeira.setContentPane(conteudo);
        hospedeira.setExtendedState(JFrame.MAXIMIZED_BOTH);
        hospedeira.setVisible(true);
        hospedeira.revalidate();
        hospedeira.repaint();
    }

    static void sair(JFrame atual) {
        JFrame janela = hospedeira == null ? atual : hospedeira;
        hospedeira = null;
        janela.dispose();
        new TelaLogin();
    }
}
