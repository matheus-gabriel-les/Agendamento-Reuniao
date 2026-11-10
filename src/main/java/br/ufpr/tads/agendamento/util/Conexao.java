
package br.ufpr.tads.agendamento.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Conexao {

    private static final String HOST =
            "jdbc:mysql://localhost:3306/";

    private static final String BANCO_PADRAO =
            "agendamento_reunioes";

    public static Connection conectar() throws SQLException {

        String usuario = System.getenv("DB_USER");
        String senha = System.getenv("DB_PASSWORD");
        String banco = System.getenv("DB_NAME");

        if (usuario == null || usuario.isBlank()
                || senha == null || senha.isEmpty()) {

            throw new SQLException(
                    "Configure DB_USER e DB_PASSWORD.");
        }

        if (banco == null || banco.isBlank()) {
            banco = BANCO_PADRAO;
        }

        if (!banco.matches("[a-zA-Z0-9_]+")) {
            throw new SQLException(
                    "Nome de banco de dados invalido.");
        }

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new SQLException(
                    "Driver JDBC do MySQL nao encontrado.", e);
        }

        return DriverManager.getConnection(
                HOST + banco,
                usuario,
                senha);
    }
}
