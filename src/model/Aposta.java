package model;

import java.time.LocalDate;
import java.util.List;

public class Aposta {
    private int id;
    private double valorPago;
    private LocalDate dataCriacao;
    private List<Integer> numerosSelecionados;
    private int qtdeAcertos;
    private String apostador;
    private double valorGanho;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public String getApostador() {
        return apostador;
    }

    public void setApostador(String apostador) {
        this.apostador = apostador;
    }

    public double getValorGanho() {
        return valorGanho;
    }

    public void setValorGanho(double valorGanho) {
        this.valorGanho = valorGanho;
    }

}
