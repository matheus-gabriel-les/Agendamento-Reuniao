
package br.ufpr.tads.agendamento.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Conexao {

    private static final String URL =
            "jdbc:mysql://localhost:3306/agendamento_reunioes";

    public static Connection conectar() throws SQLException {

        String usuario = System.getenv("DB_USER");
        String senha = System.getenv("DB_PASSWORD");

        if (usuario == null || usuario.isBlank()
                || senha == null || senha.isEmpty()) {

            throw new SQLException(
                    "Configure DB_USER e DB_PASSWORD.");
        }

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new SQLException(
                    "Driver JDBC do MySQL nao encontrado.", e);
        }

        return DriverManager.getConnection(
                URL, usuario, senha);
    }
}
