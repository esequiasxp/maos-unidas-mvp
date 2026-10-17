package br.org.maosunidas.mvp.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.time.LocalDate;

@Entity
public class Beneficiario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;
    private String cpf;
    private LocalDate dataUltimoAtendimento;

    public Beneficiario() {}

    public Beneficiario(Long id, String nome, String cpf, LocalDate dataUltimoAtendimento) {
        this.id = id;
        this.nome = nome;
        this.cpf = cpf;
        this.dataUltimoAtendimento = dataUltimoAtendimento;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public LocalDate getDataUltimoAtendimento() {
        return dataUltimoAtendimento;
    }

    public void setDataUltimoAtendimento(LocalDate dataUltimoAtendimento) {
        this.dataUltimoAtendimento = dataUltimoAtendimento;
    }
}
