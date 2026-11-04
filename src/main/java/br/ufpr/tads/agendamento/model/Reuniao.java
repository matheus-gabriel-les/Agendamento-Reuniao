
package br.ufpr.tads.agendamento.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class Reuniao {

    private Long id;
    private String titulo;
    private String descricao;
    private String localReuniao;
    private String nomeProponente;
    private String emailProponente;
    private String tokenPublico;
    private String tokenAdmin;
    private LocalDate dataInicio;
    private LocalDate dataFim;
    private ModoDivisao modoDivisao;
    private LocalTime horaInicio;
    private LocalTime horaFim;
    private Integer duracaoMinutos;
    private LocalDateTime criadaEm;

    public Reuniao() {
    }

    public Reuniao(String titulo, String descricao,
                   String localReuniao, String nomeProponente,
                   String emailProponente, LocalDate dataInicio,
                   LocalDate dataFim, ModoDivisao modoDivisao) {
        this.titulo = titulo;
        this.descricao = descricao;
        this.localReuniao = localReuniao;
        this.nomeProponente = nomeProponente;
        this.emailProponente = emailProponente;
        this.dataInicio = dataInicio;
        this.dataFim = dataFim;
        this.modoDivisao = modoDivisao;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getLocalReuniao() {
        return localReuniao;
    }

    public void setLocalReuniao(String localReuniao) {
        this.localReuniao = localReuniao;
    }

    public String getNomeProponente() {
        return nomeProponente;
    }

    public void setNomeProponente(String nomeProponente) {
        this.nomeProponente = nomeProponente;
    }

    public String getEmailProponente() {
        return emailProponente;
    }

    public void setEmailProponente(String emailProponente) {
        this.emailProponente = emailProponente;
    }

    public String getTokenPublico() {
        return tokenPublico;
    }

    public void setTokenPublico(String tokenPublico) {
        this.tokenPublico = tokenPublico;
    }

    public String getTokenAdmin() {
        return tokenAdmin;
    }

    public void setTokenAdmin(String tokenAdmin) {
        this.tokenAdmin = tokenAdmin;
    }

    public LocalDate getDataInicio() {
        return dataInicio;
    }

    public void setDataInicio(LocalDate dataInicio) {
        this.dataInicio = dataInicio;
    }

    public LocalDate getDataFim() {
        return dataFim;
    }

    public void setDataFim(LocalDate dataFim) {
        this.dataFim = dataFim;
    }

    public ModoDivisao getModoDivisao() {
        return modoDivisao;
    }

    public void setModoDivisao(ModoDivisao modoDivisao) {
        this.modoDivisao = modoDivisao;
    }

    public LocalTime getHoraInicio() {
        return horaInicio;
    }

    public void setHoraInicio(LocalTime horaInicio) {
        this.horaInicio = horaInicio;
    }

    public LocalTime getHoraFim() {
        return horaFim;
    }

    public void setHoraFim(LocalTime horaFim) {
        this.horaFim = horaFim;
    }

    public Integer getDuracaoMinutos() {
        return duracaoMinutos;
    }

    public void setDuracaoMinutos(Integer duracaoMinutos) {
        this.duracaoMinutos = duracaoMinutos;
    }

    public LocalDateTime getCriadaEm() {
        return criadaEm;
    }

    public void setCriadaEm(LocalDateTime criadaEm) {
        this.criadaEm = criadaEm;
    }
}
