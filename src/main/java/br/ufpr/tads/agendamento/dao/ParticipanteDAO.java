
package br.ufpr.tads.agendamento.dao;

import java.sql.SQLException;
import java.util.List;

import br.ufpr.tads.agendamento.model.Participante;

public interface ParticipanteDAO {

    void inserir(Participante participante) throws SQLException;

    Participante buscarPorId(Long id) throws SQLException;

    Participante buscarPorToken(String token)
            throws SQLException;

    List<Participante> listarPorReuniao(Long reuniaoId)
            throws SQLException;

    void atualizarResposta(Long id) throws SQLException;

    void atualizarEmailEnviado(Long id) throws SQLException;

    void excluir(Long id) throws SQLException;
}
