package br.org.maosunidas.mvp.repository;

import br.org.maosunidas.mvp.model.Beneficiario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BeneficiarioRepository extends JpaRepository<Beneficiario, Long> {
    Optional<Beneficiario> findByCpf(String cpf);
}
