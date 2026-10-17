package br.org.maosunidas.mvp.repository;

import br.org.maosunidas.mvp.model.LoteAlimento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LoteAlimentoRepository extends JpaRepository<LoteAlimento, Long> {
    List<LoteAlimento> findAllByOrderByDataValidadeAsc();
}
