
package br.ufpr.tads.agendamento.model;

import java.time.LocalDateTime;

public class Participante {

    private Long id;
    private Long reuniaoId;
    private String nome;
    private String email;
    private String token;
    private boolean proponente;
    private LocalDateTime convidadoEm;
    private LocalDateTime respondidoEm;
    private LocalDateTime emailEnviadoEm;

    public Participante() {
    }

    public Participante(Long reuniaoId, String nome,
                        String email, String token,
                        boolean proponente) {
        this.reuniaoId = reuniaoId;
        this.nome = nome;
        this.email = email;
        this.token = token;
        this.proponente = proponente;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getReuniaoId() {
        return reuniaoId;
    }

    public void setReuniaoId(Long reuniaoId) {
        this.reuniaoId = reuniaoId;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public boolean isProponente() {
        return proponente;
    }

    public void setProponente(boolean proponente) {
        this.proponente = proponente;
    }

    public LocalDateTime getConvidadoEm() {
        return convidadoEm;
    }

    public void setConvidadoEm(LocalDateTime convidadoEm) {
        this.convidadoEm = convidadoEm;
    }

    public LocalDateTime getRespondidoEm() {
        return respondidoEm;
    }

    public void setRespondidoEm(LocalDateTime respondidoEm) {
        this.respondidoEm = respondidoEm;
    }

    public LocalDateTime getEmailEnviadoEm() {
        return emailEnviadoEm;
    }

    public void setEmailEnviadoEm(LocalDateTime emailEnviadoEm) {
        this.emailEnviadoEm = emailEnviadoEm;
    }
}
