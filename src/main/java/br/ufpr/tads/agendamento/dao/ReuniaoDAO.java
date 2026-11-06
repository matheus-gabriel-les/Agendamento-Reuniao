
package br.ufpr.tads.agendamento.dao;

import java.sql.SQLException;
import java.util.List;

import br.ufpr.tads.agendamento.model.Reuniao;

public interface ReuniaoDAO {

    void inserir(Reuniao reuniao) throws SQLException;

    Reuniao buscarPorId(Long id) throws SQLException;

    Reuniao buscarPorTokenPublico(String token)
            throws SQLException;

    Reuniao buscarPorTokenAdmin(String token)
            throws SQLException;

    List<Reuniao> listar() throws SQLException;

    void excluir(Long id) throws SQLException;
}
