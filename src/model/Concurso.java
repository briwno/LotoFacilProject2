package model;

import java.util.Date;
import java.util.List;

public class Concurso {
    private int id;
    private Date dataCriacao;
    private Date dataSorteio;
    private String horario;
    private List<Integer> numerosSorteados;
    private String situacao;
    private List<Aposta> apostas;
    private double premioAcumulado;

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Date getDataCriacao() {
        return dataCriacao;
    }

    public void setDataCriacao(Date dataCriacao) {
        this.dataCriacao = dataCriacao;
    }

    public Date getDataSorteio() {
        return dataSorteio;
    }

    public void setDataSorteio(Date dataSorteio) {
        this.dataSorteio = dataSorteio;
    }

    public String getHorario() {
        return horario;
    }

    public void setHorario(String horario) {
        this.horario = horario;
    }

    public List<Integer> getNumerosSorteados() {
        return numerosSorteados;
    }

    public void setNumerosSorteados(List<Integer> numerosSorteados) {
        this.numerosSorteados = numerosSorteados;
    }

    public String getSituacao() {
        return situacao;
    }

    public void setSituacao(String situacao) {
        this.situacao = situacao;
    }

    public List<Aposta> getApostas() {
        return apostas;
    }

    public void setApostas(List<Aposta> apostas) {
        this.apostas = apostas;
    }

    public double getPremioAcumulado() {
        return premioAcumulado;
    }

    public void setPremioAcumulado(double premioAcumulado) {
        this.premioAcumulado = premioAcumulado;
    }
}
