package br.com.ada.lms.modelo;

import java.time.LocalDate;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Transacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // id gerado pelo banco (coluna identity)
    private Long id;

    @Column(nullable = false)
    private String descricao;

    @Column(nullable = false)
    private String tipo;

    @Column(nullable = false)
    private String categoria;

    @Column(nullable = false)
    private Double valor;

    @Column(nullable = false)
    private LocalDate data;

    /** Construtor vazio exigido pelo JPA (o Hibernate cria o objeto e depois preenche os campos). */

    protected Transacao() {
    }

    public Transacao(String descricao, String tipo, String categoria,
                     double valor, LocalDate data) {
        this.descricao = descricao;
        this.tipo = tipo;
        this.categoria = categoria;
        this.valor = valor;
        this.data = data;
    }


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
