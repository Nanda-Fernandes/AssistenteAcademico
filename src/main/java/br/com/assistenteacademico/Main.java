package br.com.assistenteacademico;

import br.com.assistenteacademico.view.TelaLogin;
import br.com.assistenteacademico.view.EstiloVisual;

import javax.swing.*;

public class Main {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            aplicarAparenciaDoSistema();
            EstiloVisual.configurarTema();
            new TelaLogin();
        });
    }

    private static void aplicarAparenciaDoSistema() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ReflectiveOperationException | UnsupportedLookAndFeelException e) {
            System.out.println("Não foi possível aplicar a aparência do sistema: " + e.getMessage());
        }
    }
}