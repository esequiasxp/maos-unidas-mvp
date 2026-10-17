package br.org.maosunidas.mvp.service;

import br.org.maosunidas.mvp.model.Beneficiario;
import br.org.maosunidas.mvp.model.LoteAlimento;
import br.org.maosunidas.mvp.repository.BeneficiarioRepository;
import br.org.maosunidas.mvp.repository.LoteAlimentoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class AtendimentoService {

    private final BeneficiarioRepository beneficiarioRepository;
    private final LoteAlimentoRepository loteAlimentoRepository;

    public AtendimentoService(BeneficiarioRepository beneficiarioRepository, LoteAlimentoRepository loteAlimentoRepository) {
        this.beneficiarioRepository = beneficiarioRepository;
        this.loteAlimentoRepository = loteAlimentoRepository;
    }

    public List<LoteAlimento> listarLotesPrioritarios() {
        return loteAlimentoRepository.findAllByOrderByDataValidadeAsc();
    }

    public boolean verificarElegibilidade(String cpf) {
        String cpfLimpo = cpf.replaceAll("\\D", "");
        return beneficiarioRepository.findByCpf(cpfLimpo)
                .map(beneficiario -> {
                    if (beneficiario.getDataUltimoAtendimento() == null) {
                        return true;
                    }
                    long dias = ChronoUnit.DAYS.between(beneficiario.getDataUltimoAtendimento(), LocalDate.now());
                    return dias >= 30;
                })
                .orElse(true);
    }

    @Transactional
    public void registrarAtendimento(String cpf, Long loteId) {
        if (!verificarElegibilidade(cpf)) {
            throw new IllegalStateException("Beneficiario em periodo de carencia de 30 dias.");
        }

        String cpfLimpo = cpf.replaceAll("\\D", "");
        Beneficiario beneficiario = beneficiarioRepository.findByCpf(cpfLimpo)
                .orElseThrow(() -> new IllegalArgumentException("Beneficiario nao encontrado."));

        LoteAlimento lote = loteAlimentoRepository.findById(loteId)
                .orElseThrow(() -> new IllegalArgumentException("Lote nao encontrado."));

        if (lote.getQuantidadeDisponivel() <= 0) {
            throw new IllegalStateException("Lote sem estoque disponivel.");
        }

        lote.setQuantidadeDisponivel(lote.getQuantidadeDisponivel() - 1);
        beneficiario.setDataUltimoAtendimento(LocalDate.now());

        loteAlimentoRepository.save(lote);
        beneficiarioRepository.save(beneficiario);
    }
}
