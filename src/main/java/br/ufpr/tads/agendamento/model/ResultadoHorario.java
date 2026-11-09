
package br.ufpr.tads.agendamento.model;

public class ResultadoHorario {

    private Horario horario;
    private int quantidadeSim;
    private int quantidadeTalvez;
    private int quantidadeNao;
    private int quantidadeSemResposta;

    public ResultadoHorario() {
    }

    public ResultadoHorario(
            Horario horario,
            int quantidadeSim,
            int quantidadeTalvez,
            int quantidadeNao,
            int quantidadeSemResposta) {

        this.horario = horario;
        this.quantidadeSim = quantidadeSim;
        this.quantidadeTalvez = quantidadeTalvez;
        this.quantidadeNao = quantidadeNao;
        this.quantidadeSemResposta = quantidadeSemResposta;
    }

    // Mantem compatibilidade com chamadas antigas.
    public ResultadoHorario(
            Horario horario,
            int quantidadeSim,
            int quantidadeTalvez,
            int quantidadeSemResposta) {

        this(
                horario,
                quantidadeSim,
                quantidadeTalvez,
                0,
                quantidadeSemResposta);
    }

    public Horario getHorario() {
        return horario;
    }

    public void setHorario(Horario horario) {
        this.horario = horario;
    }

    public int getQuantidadeSim() {
        return quantidadeSim;
    }

    public void setQuantidadeSim(int quantidadeSim) {
        this.quantidadeSim = quantidadeSim;
    }

    public int getQuantidadeTalvez() {
        return quantidadeTalvez;
    }

    public void setQuantidadeTalvez(int quantidadeTalvez) {
        this.quantidadeTalvez = quantidadeTalvez;
    }

    public int getQuantidadeNao() {
        return quantidadeNao;
    }

    public void setQuantidadeNao(int quantidadeNao) {
        this.quantidadeNao = quantidadeNao;
    }

    public int getQuantidadeSemResposta() {
        return quantidadeSemResposta;
    }

    public void setQuantidadeSemResposta(
            int quantidadeSemResposta) {

        this.quantidadeSemResposta =
                quantidadeSemResposta;
    }
}
