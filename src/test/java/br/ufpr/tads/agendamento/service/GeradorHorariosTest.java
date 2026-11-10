
package br.ufpr.tads.agendamento.service;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import br.ufpr.tads.agendamento.model.Horario;
import br.ufpr.tads.agendamento.model.ModoDivisao;
import br.ufpr.tads.agendamento.model.Periodo;
import br.ufpr.tads.agendamento.model.Reuniao;

class GeradorHorariosTest {

    private GeradorHorarios gerador;

    @BeforeEach
    void preparar() {
        gerador = new GeradorHorarios();
    }

    private Reuniao criarReuniao(
            LocalDate inicio,
            LocalDate fim,
            ModoDivisao modo) {

        Reuniao reuniao = new Reuniao();
        reuniao.setId(1L);
        reuniao.setTitulo("Reuniao de teste");
        reuniao.setDataInicio(inicio);
        reuniao.setDataFim(fim);
        reuniao.setModoDivisao(modo);

        return reuniao;
    }

    @Test
    void deveGerarUmHorarioPorDia() {
        Reuniao reuniao = criarReuniao(
                LocalDate.of(2026, 11, 10),
                LocalDate.of(2026, 11, 12),
                ModoDivisao.DIA);

        List<Horario> horarios = gerador.gerar(reuniao);

        assertEquals(3, horarios.size());
        assertEquals(LocalDate.of(2026, 11, 10),
                horarios.get(0).getDia());
        assertEquals(LocalDate.of(2026, 11, 11),
                horarios.get(1).getDia());
        assertEquals(LocalDate.of(2026, 11, 12),
                horarios.get(2).getDia());

        for (Horario horario : horarios) {
            assertEquals(1L, horario.getReuniaoId());
            assertNull(horario.getPeriodo());
            assertNull(horario.getHoraInicio());
            assertNull(horario.getHoraFim());
        }
    }

    @Test
    void deveGerarTresPeriodosPorDia() {
        Reuniao reuniao = criarReuniao(
                LocalDate.of(2026, 11, 10),
                LocalDate.of(2026, 11, 11),
                ModoDivisao.PERIODOS);

        List<Horario> horarios = gerador.gerar(reuniao);

        assertEquals(6, horarios.size());
        assertEquals(Periodo.MANHA, horarios.get(0).getPeriodo());
        assertEquals(Periodo.TARDE, horarios.get(1).getPeriodo());
        assertEquals(Periodo.NOITE, horarios.get(2).getPeriodo());
        assertEquals(Periodo.MANHA, horarios.get(3).getPeriodo());
        assertEquals(Periodo.NOITE, horarios.get(5).getPeriodo());
        assertEquals(LocalDate.of(2026, 11, 11),
                horarios.get(3).getDia());
    }

    @Test
    void deveGerarIntervalosDeUmaHora() {
        Reuniao reuniao = criarReuniao(
                LocalDate.of(2026, 11, 10),
                LocalDate.of(2026, 11, 10),
                ModoDivisao.HORAS);

        reuniao.setHoraInicio(LocalTime.of(8, 0));
        reuniao.setHoraFim(LocalTime.of(12, 0));
        reuniao.setDuracaoMinutos(60);

        List<Horario> horarios = gerador.gerar(reuniao);

        assertEquals(4, horarios.size());
        assertEquals(LocalTime.of(8, 0),
                horarios.get(0).getHoraInicio());
        assertEquals(LocalTime.of(9, 0),
                horarios.get(0).getHoraFim());
        assertEquals(LocalTime.of(11, 0),
                horarios.get(3).getHoraInicio());
        assertEquals(LocalTime.of(12, 0),
                horarios.get(3).getHoraFim());
    }

    @Test
    void naoDeveGerarIntervaloIncompleto() {
        Reuniao reuniao = criarReuniao(
                LocalDate.of(2026, 11, 10),
                LocalDate.of(2026, 11, 10),
                ModoDivisao.HORAS);

        reuniao.setHoraInicio(LocalTime.of(8, 0));
        reuniao.setHoraFim(LocalTime.of(10, 30));
        reuniao.setDuracaoMinutos(60);

        List<Horario> horarios = gerador.gerar(reuniao);

        assertEquals(2, horarios.size());
        assertEquals(LocalTime.of(10, 0),
                horarios.get(1).getHoraFim());
    }

    @Test
    void deveGerarUmDiaQuandoDatasIguais() {
        Reuniao reuniao = criarReuniao(
                LocalDate.of(2026, 11, 10),
                LocalDate.of(2026, 11, 10),
                ModoDivisao.DIA);

        assertEquals(1, gerador.gerar(reuniao).size());
    }

    @Test
    void deveAceitarLimiteDe366Dias() {
        LocalDate inicio = LocalDate.of(2026, 1, 1);

        Reuniao reuniao = criarReuniao(
                inicio,
                inicio.plusDays(365),
                ModoDivisao.DIA);

        assertEquals(366, gerador.gerar(reuniao).size());
    }

    @Test
    void deveRejeitarMaisDe366Dias() {
        LocalDate inicio = LocalDate.of(2026, 1, 1);

        Reuniao reuniao = criarReuniao(
                inicio,
                inicio.plusDays(366),
                ModoDivisao.DIA);

        assertThrows(IllegalArgumentException.class,
                () -> gerador.gerar(reuniao));
    }

    @Test
    void deveRejeitarDataFinalAnterior() {
        Reuniao reuniao = criarReuniao(
                LocalDate.of(2026, 11, 12),
                LocalDate.of(2026, 11, 10),
                ModoDivisao.DIA);

        assertThrows(IllegalArgumentException.class,
                () -> gerador.gerar(reuniao));
    }

    @Test
    void deveRejeitarReuniaoNula() {
        assertThrows(IllegalArgumentException.class,
                () -> gerador.gerar(null));
    }

    @Test
    void deveRejeitarDatasAusentes() {
        Reuniao reuniao = criarReuniao(
                null,
                LocalDate.of(2026, 11, 10),
                ModoDivisao.DIA);

        assertThrows(IllegalArgumentException.class,
                () -> gerador.gerar(reuniao));
    }

    @Test
    void deveRejeitarModoAusente() {
        Reuniao reuniao = criarReuniao(
                LocalDate.of(2026, 11, 10),
                LocalDate.of(2026, 11, 10),
                null);

        assertThrows(IllegalArgumentException.class,
                () -> gerador.gerar(reuniao));
    }

    @Test
    void deveRejeitarDuracaoZero() {
        Reuniao reuniao = criarReuniao(
                LocalDate.of(2026, 11, 10),
                LocalDate.of(2026, 11, 10),
                ModoDivisao.HORAS);

        reuniao.setHoraInicio(LocalTime.of(8, 0));
        reuniao.setHoraFim(LocalTime.of(12, 0));
        reuniao.setDuracaoMinutos(0);

        assertThrows(IllegalArgumentException.class,
                () -> gerador.gerar(reuniao));
    }

    @Test
    void deveRejeitarDuracaoNegativa() {
        Reuniao reuniao = criarReuniao(
                LocalDate.of(2026, 11, 10),
                LocalDate.of(2026, 11, 10),
                ModoDivisao.HORAS);

        reuniao.setHoraInicio(LocalTime.of(8, 0));
        reuniao.setHoraFim(LocalTime.of(12, 0));
        reuniao.setDuracaoMinutos(-30);

        assertThrows(IllegalArgumentException.class,
                () -> gerador.gerar(reuniao));
    }

    @Test
    void deveRejeitarDuracaoMaiorQueIntervalo() {
        Reuniao reuniao = criarReuniao(
                LocalDate.of(2026, 11, 10),
                LocalDate.of(2026, 11, 10),
                ModoDivisao.HORAS);

        reuniao.setHoraInicio(LocalTime.of(8, 0));
        reuniao.setHoraFim(LocalTime.of(9, 0));
        reuniao.setDuracaoMinutos(90);

        assertThrows(IllegalArgumentException.class,
                () -> gerador.gerar(reuniao));
    }

    @Test
    void deveRejeitarHoraFinalAnterior() {
        Reuniao reuniao = criarReuniao(
                LocalDate.of(2026, 11, 10),
                LocalDate.of(2026, 11, 10),
                ModoDivisao.HORAS);

        reuniao.setHoraInicio(LocalTime.of(12, 0));
        reuniao.setHoraFim(LocalTime.of(8, 0));
        reuniao.setDuracaoMinutos(30);

        assertThrows(IllegalArgumentException.class,
                () -> gerador.gerar(reuniao));
    }

    @Test
    void deveRejeitarHorasAusentes() {
        Reuniao reuniao = criarReuniao(
                LocalDate.of(2026, 11, 10),
                LocalDate.of(2026, 11, 10),
                ModoDivisao.HORAS);

        reuniao.setDuracaoMinutos(30);

        assertThrows(IllegalArgumentException.class,
                () -> gerador.gerar(reuniao));
    }
}
