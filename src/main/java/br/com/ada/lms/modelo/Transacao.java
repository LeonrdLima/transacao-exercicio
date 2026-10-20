package br.com.ada.lms.modelo;

import java.time.LocalDate;

public class Transacao {

    private Long id;
    private String descricao;
    private String tipo;
    private String categoria;
    private double valor;
    private LocalDate data;

    public Transacao(Long id, String descricao, String tipo, String categoria, double valor, LocalDate data) {
        this.id = id;
        this.descricao = descricao;
        this.tipo = tipo;
        this.categoria = categoria;
        this.valor = valor;
        this.data = data;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public double getValor() {
        return valor;
    }

    public void setValor(double valor) {
        this.valor = valor;
    }

    public LocalDate getData() {
        return data;
    }

    public void setData(LocalDate data) {
        this.data = data;
    }

    
}
