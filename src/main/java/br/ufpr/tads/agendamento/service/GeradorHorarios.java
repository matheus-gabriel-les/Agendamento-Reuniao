
package br.ufpr.tads.agendamento.service;

import br.ufpr.tads.agendamento.model.Horario;
import br.ufpr.tads.agendamento.model.ModoDivisao;
import br.ufpr.tads.agendamento.model.Periodo;
import br.ufpr.tads.agendamento.model.Reuniao;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

public class GeradorHorarios {

    public List<Horario> gerar(Reuniao reuniao) {

        validar(reuniao);

        List<Horario> horarios = new ArrayList<>();
        LocalDate dia = reuniao.getDataInicio();

        while (!dia.isAfter(reuniao.getDataFim())) {

            switch (reuniao.getModoDivisao()) {

                case DIA:
                    horarios.add(new Horario(
                            reuniao.getId(),
                            dia,
                            null,
                            null,
                            null
                    ));
                    break;

                case PERIODOS:
                    for (Periodo periodo : Periodo.values()) {
                        horarios.add(new Horario(
                                reuniao.getId(),
                                dia,
                                periodo,
                                null,
                                null
                        ));
                    }
                    break;

                case HORAS:
                    LocalTime inicio = reuniao.getHoraInicio();
                    LocalTime fim = reuniao.getHoraFim();
                    int duracao = reuniao.getDuracaoMinutos();

                    while (inicio.plusMinutes(duracao)
                            .compareTo(fim) <= 0) {

                        LocalTime proximo =
                                inicio.plusMinutes(duracao);

                        horarios.add(new Horario(
                                reuniao.getId(),
                                dia,
                                null,
                                inicio,
                                proximo
                        ));

                        inicio = proximo;
                    }
                    break;

                default:
                    throw new IllegalArgumentException(
                            "Modo de divisao invalido.");
            }

            dia = dia.plusDays(1);
        }

        return horarios;
    }

    public void validar(Reuniao reuniao) {

        if (reuniao == null) {
            throw new IllegalArgumentException(
                    "A reuniao nao pode ser nula.");
        }

        if (reuniao.getDataInicio() == null
                || reuniao.getDataFim() == null) {
            throw new IllegalArgumentException(
                    "As datas sao obrigatorias.");
        }

        if (reuniao.getDataFim()
                .isBefore(reuniao.getDataInicio())) {
            throw new IllegalArgumentException(
                    "A data final nao pode ser anterior "
                    + "a data inicial.");
        }

        long quantidadeDias = ChronoUnit.DAYS.between(
                reuniao.getDataInicio(),
                reuniao.getDataFim()
        ) + 1;

        if (quantidadeDias > 366) {
            throw new IllegalArgumentException(
                    "O intervalo nao pode ultrapassar 366 dias.");
        }

        if (reuniao.getModoDivisao() == null) {
            throw new IllegalArgumentException(
                    "O modo de divisao e obrigatorio.");
        }

        if (reuniao.getModoDivisao() == ModoDivisao.HORAS) {

            LocalTime inicio = reuniao.getHoraInicio();
            LocalTime fim = reuniao.getHoraFim();
            Integer duracao = reuniao.getDuracaoMinutos();

            if (inicio == null || fim == null
                    || duracao == null) {
                throw new IllegalArgumentException(
                        "Informe as horas de inicio, fim "
                        + "e duracao.");
            }

            if (!fim.isAfter(inicio)) {
                throw new IllegalArgumentException(
                        "A hora final deve ser posterior "
                        + "a hora inicial.");
            }

            if (duracao <= 0) {
                throw new IllegalArgumentException(
                        "A duracao deve ser maior que zero.");
            }

            long minutosDisponiveis =
                    ChronoUnit.MINUTES.between(inicio, fim);

            if (duracao > minutosDisponiveis) {
                throw new IllegalArgumentException(
                        "A duracao ultrapassa o intervalo.");
            }
        }
    }
}
