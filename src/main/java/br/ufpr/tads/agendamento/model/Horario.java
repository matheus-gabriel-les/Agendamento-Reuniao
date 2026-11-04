
package br.ufpr.tads.agendamento.model;

import java.time.LocalDate;
import java.time.LocalTime;

public class Horario {

    private Long id;
    private Long reuniaoId;
    private LocalDate dia;
    private Periodo periodo;
    private LocalTime horaInicio;
    private LocalTime horaFim;

    public Horario() {
    }

    public Horario(Long reuniaoId, LocalDate dia,
                   Periodo periodo, LocalTime horaInicio,
                   LocalTime horaFim) {
        this.reuniaoId = reuniaoId;
        this.dia = dia;
        this.periodo = periodo;
        this.horaInicio = horaInicio;
        this.horaFim = horaFim;
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

    public LocalDate getDia() {
        return dia;
    }

    public void setDia(LocalDate dia) {
        this.dia = dia;
    }

    public Periodo getPeriodo() {
        return periodo;
    }

    public void setPeriodo(Periodo periodo) {
        this.periodo = periodo;
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
}
