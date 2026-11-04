
package br.ufpr.tads.agendamento.model;

public class Disponibilidade {

    private Long id;
    private Long participanteId;
    private Long horarioId;
    private RespostaDisponibilidade resposta;

    public Disponibilidade() {
    }

    public Disponibilidade(Long participanteId,
                           Long horarioId,
                           RespostaDisponibilidade resposta) {
        this.participanteId = participanteId;
        this.horarioId = horarioId;
        this.resposta = resposta;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getParticipanteId() {
        return participanteId;
    }

    public void setParticipanteId(Long participanteId) {
        this.participanteId = participanteId;
    }

    public Long getHorarioId() {
        return horarioId;
    }

    public void setHorarioId(Long horarioId) {
        this.horarioId = horarioId;
    }

    public RespostaDisponibilidade getResposta() {
        return resposta;
    }

    public void setResposta(RespostaDisponibilidade resposta) {
        this.resposta = resposta;
    }
}
