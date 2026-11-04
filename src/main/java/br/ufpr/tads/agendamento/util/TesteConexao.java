
package br.ufpr.tads.agendamento.util;

import java.sql.Connection;
import java.sql.SQLException;

public class TesteConexao {

    public static void main(String[] args) {
        try (Connection conexao = Conexao.conectar()) {
            System.out.println("Conexao realizada com sucesso!");
        } catch (SQLException e) {
            System.out.println("Erro ao conectar: " + e.getMessage());
        }
    }
}
