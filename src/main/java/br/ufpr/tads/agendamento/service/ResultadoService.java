
package br.ufpr.tads.agendamento.service;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import br.ufpr.tads.agendamento.dao.jdbc.DisponibilidadeDAOImpl;
import br.ufpr.tads.agendamento.dao.jdbc.HorarioDAOImpl;
import br.ufpr.tads.agendamento.dao.jdbc.ParticipanteDAOImpl;
import br.ufpr.tads.agendamento.dao.jdbc.ReuniaoDAOImpl;
import br.ufpr.tads.agendamento.model.Disponibilidade;
import br.ufpr.tads.agendamento.model.Horario;
import br.ufpr.tads.agendamento.model.Participante;
import br.ufpr.tads.agendamento.model.RespostaDisponibilidade;
import br.ufpr.tads.agendamento.model.ResultadoHorario;

public class ResultadoService {

    private final ReuniaoDAOImpl reuniaoDAO;
    private final HorarioDAOImpl horarioDAO;
    private final ParticipanteDAOImpl participanteDAO;
    private final DisponibilidadeDAOImpl disponibilidadeDAO;

    public ResultadoService() {
        reuniaoDAO = new ReuniaoDAOImpl();
        horarioDAO = new HorarioDAOImpl();
        participanteDAO = new ParticipanteDAOImpl();
        disponibilidadeDAO = new DisponibilidadeDAOImpl();
    }

    public List<ResultadoHorario> calcular(Long reuniaoId)
            throws SQLException {

        if (reuniaoId == null || reuniaoId <= 0) {
            throw new IllegalArgumentException(
                    "ID da reuniao invalido.");
        }

        if (reuniaoDAO.buscarPorId(reuniaoId) == null) {
            throw new IllegalArgumentException(
                    "Reuniao nao encontrada.");
        }

        List<Horario> horarios =
                horarioDAO.listarPorReuniao(reuniaoId);

        List<Participante> participantes =
                participanteDAO.listarPorReuniao(reuniaoId);

        int totalParticipantes = participantes.size();

        List<ResultadoHorario> resultados =
                new ArrayList<>();

        for (Horario horario : horarios) {

            List<Disponibilidade> disponibilidades =
                    disponibilidadeDAO.listarPorHorario(
                            horario.getId());

            int sim = 0;
            int talvez = 0;
            int nao = 0;

            for (Disponibilidade disponibilidade
                    : disponibilidades) {

                RespostaDisponibilidade resposta =
                        disponibilidade.getResposta();

                if (resposta == RespostaDisponibilidade.SIM) {

                    sim++;

                } else if (resposta
                        == RespostaDisponibilidade.TALVEZ) {

                    talvez++;

                } else if (resposta
                        == RespostaDisponibilidade.NAO) {

                    nao++;
                }
            }

            int semResposta =
                    totalParticipantes - sim - talvez - nao;

            if (semResposta < 0) {
                throw new IllegalStateException(
                        "Quantidade de respostas inconsistente.");
            }

            ResultadoHorario resultado =
                    new ResultadoHorario(
                            horario,
                            sim,
                            talvez,
                            nao,
                            semResposta);

            resultados.add(resultado);
        }

        // Prioriza mais respostas SIM.
        // Em seguida, mais respostas TALVEZ.
        // Em caso de empate, usa a data e o ID.
        resultados.sort(
                Comparator.comparingInt(
                        ResultadoHorario::getQuantidadeSim)
                        .reversed()
                        .thenComparing(
                                Comparator.comparingInt(
                                        ResultadoHorario::getQuantidadeTalvez)
                                        .reversed())
                        .thenComparing(
                                r -> r.getHorario().getDia())
                        .thenComparing(
                                r -> r.getHorario().getId())
        );

        return resultados;
    }
}
