
package br.ufpr.tads.agendamento.dao;

import java.sql.SQLException;
import java.util.List;

import br.ufpr.tads.agendamento.model.Disponibilidade;

public interface DisponibilidadeDAO {

    void inserir(Disponibilidade disponibilidade)
            throws SQLException;

    void salvar(Disponibilidade disponibilidade)
            throws SQLException;

    Disponibilidade buscarPorId(Long id)
            throws SQLException;

    List<Disponibilidade> listarPorParticipante(Long participanteId)
            throws SQLException;

    List<Disponibilidade> listarPorHorario(Long horarioId)
            throws SQLException;

    void excluir(Long id) throws SQLException;

    void excluirPorParticipante(Long participanteId)
            throws SQLException;
}
