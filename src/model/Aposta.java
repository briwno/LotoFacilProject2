package model;

import java.time.LocalDate;
import java.util.List;

public class Aposta {
    private int idAposta;
    private int idApostador;
    private int idConcurso;
    private double valorPago;
    private LocalDate dataCriacao;
    private List<Integer> numerosSelecionados;
    private int qtdeAcertos;
    private double valorGanho;


    public int getIdAposta() {
        return idAposta;
    }

    public void setIdAposta(int idAposta) {
        this.idAposta = idAposta;
    }

    public int getIdApostador() {
        return idApostador;
    }

    public void setIdApostador(int idApostador) {
        this.idApostador = idApostador;
    }

    public int getIdConcurso() {
        return idConcurso;
    }

    public void setIdConcurso(int idConcurso) {
        this.idConcurso = idConcurso;
    }

    public double getValorPago() {
        return valorPago;
    }

    public void setValorPago(double valorPago) {
        this.valorPago = valorPago;
    }

    public LocalDate getDataCriacao() {
        return dataCriacao;
    }

    public void setDataCriacao(LocalDate dataCriacao) {
        this.dataCriacao = dataCriacao;
    }

    public List<Integer> getNumerosSelecionados() {
        return numerosSelecionados;
    }

    public void setNumerosSelecionados(List<Integer> numerosSelecionados) {
        this.numerosSelecionados = numerosSelecionados;
    }

    public int getQtdeAcertos() {
        return qtdeAcertos;
    }

    public void setQtdeAcertos(int qtdeAcertos) {
        this.qtdeAcertos = qtdeAcertos;
    }

    public double getValorGanho() {
        return valorGanho;
    }

    public void setValorGanho(double valorGanho) {
        this.valorGanho = valorGanho;
    }

}
