package br.org.maosunidas.mvp.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.time.LocalDate;

@Entity
public class LoteAlimento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nomeAlimento;
    private LocalDate dataValidade;
    private Integer quantidadeDisponivel;

    public LoteAlimento() {}

    public LoteAlimento(Long id, String nomeAlimento, LocalDate dataValidade, Integer quantidadeDisponivel) {
        this.id = id;
        this.nomeAlimento = nomeAlimento;
        this.dataValidade = dataValidade;
        this.quantidadeDisponivel = quantidadeDisponivel;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNomeAlimento() {
        return nomeAlimento;
    }

    public void setNomeAlimento(String nomeAlimento) {
        this.nomeAlimento = nomeAlimento;
    }

    public LocalDate getDataValidade() {
        return dataValidade;
    }

    public void setDataValidade(LocalDate dataValidade) {
        this.dataValidade = dataValidade;
    }

    public Integer getQuantidadeDisponivel() {
        return quantidadeDisponivel;
    }

    public void setQuantidadeDisponivel(Integer quantidadeDisponivel) {
        this.quantidadeDisponivel = quantidadeDisponivel;
    }
}
