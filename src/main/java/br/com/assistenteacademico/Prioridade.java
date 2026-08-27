package br.com.assistenteacademico;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class Prioridade {

    public static String calcular(LocalDate data) {

        LocalDate hoje = LocalDate.now();

        long dias =
                ChronoUnit.DAYS.between(hoje, data);

        if (dias < 0) {
            return "ATRASADO";
        }

        if (dias == 0) {
            return "HOJE";
        }

        if (dias <= 3) {
            return "URGENTE";
        }

        if (dias <= 7) {
            return "PRÓXIMO";
        }

        return "FUTURO";
    }
}