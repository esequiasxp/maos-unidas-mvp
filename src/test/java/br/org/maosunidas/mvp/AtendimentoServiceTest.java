package br.org.maosunidas.mvp;

import br.org.maosunidas.mvp.model.Beneficiario;
import br.org.maosunidas.mvp.model.LoteAlimento;
import br.org.maosunidas.mvp.repository.BeneficiarioRepository;
import br.org.maosunidas.mvp.repository.LoteAlimentoRepository;
import br.org.maosunidas.mvp.service.AtendimentoService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AtendimentoServiceTest {

    @Mock
    private BeneficiarioRepository beneficiarioRepository;

    @Mock
    private LoteAlimentoRepository loteAlimentoRepository;

    @InjectMocks
    private AtendimentoService atendimentoService;

    @Test
    @DisplayName("Deve priorizar lotes com data de validade mais proxima (Regra FEFO)")
    void deveRetornarLotesOrdenadosPorValidadeFEFO() {
        LoteAlimento loteMaisUrgente = new LoteAlimento(1L, "Arroz Tipo 1", LocalDate.now().plusDays(5), 20);
        LoteAlimento loteMenosUrgente = new LoteAlimento(2L, "Feijao Carioca", LocalDate.now().plusDays(25), 15);

        when(loteAlimentoRepository.findAllByOrderByDataValidadeAsc())
                .thenReturn(Arrays.asList(loteMaisUrgente, loteMenosUrgente));

        List<LoteAlimento> resultado = atendimentoService.listarLotesPrioritarios();

        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        assertEquals("Arroz Tipo 1", resultado.get(0).getNomeAlimento());
        assertTrue(resultado.get(0).getDataValidade().isBefore(resultado.get(1).getDataValidade()));
        verify(loteAlimentoRepository, times(1)).findAllByOrderByDataValidadeAsc();
    }

    @Test
    @DisplayName("Deve permitir entrega para beneficiario com carencia cumprida superior a 30 dias")
    void devePermitirEntregaBeneficiarioApto() {
        String cpf = "12345678901";
        Beneficiario beneficiario = new Beneficiario(1L, "Maria Silva", cpf, LocalDate.now().minusDays(35));
        LoteAlimento lote = new LoteAlimento(1L, "Cesta Basica", LocalDate.now().plusDays(10), 10);

        when(beneficiarioRepository.findByCpf(cpf)).thenReturn(Optional.of(beneficiario));
        when(loteAlimentoRepository.findAllByOrderByDataValidadeAsc()).thenReturn(List.of(lote));

        boolean apto = atendimentoService.verificarElegibilidade(cpf);

        assertTrue(apto);
    }

    @Test
    @DisplayName("Deve bloquear entrega para beneficiario dentro do prazo de carencia de 30 dias")
    void deveBloquearBeneficiarioEmCarencia() {
        String cpf = "98765432100";
        Beneficiario beneficiario = new Beneficiario(2L, "Joao Souza", cpf, LocalDate.now().minusDays(10));

        when(beneficiarioRepository.findByCpf(cpf)).thenReturn(Optional.of(beneficiario));

        boolean apto = atendimentoService.verificarElegibilidade(cpf);

        assertFalse(apto);
    }

    @Test
    @DisplayName("Deve lancar excecao ao tentar registrar entrega para beneficiario em carencia")
    void deveLancarExcecaoEntregaNaoAutorizada() {
        String cpf = "98765432100";
        Beneficiario beneficiario = new Beneficiario(2L, "Joao Souza", cpf, LocalDate.now().minusDays(15));

        when(beneficiarioRepository.findByCpf(cpf)).thenReturn(Optional.of(beneficiario));

        assertThrows(IllegalStateException.class, () -> {
            atendimentoService.registrarAtendimento(cpf, 1L);
        });

        verify(loteAlimentoRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve dar baixa no estoque do lote ao registrar atendimento com sucesso")
    void deveDeduzirEstoqueComSucesso() {
        String cpf = "12345678901";
        Beneficiario beneficiario = new Beneficiario(1L, "Maria Silva", cpf, LocalDate.now().minusDays(40));
        LoteAlimento lote = new LoteAlimento(1L, "Cesta Padrao", LocalDate.now().plusDays(15), 5);

        when(beneficiarioRepository.findByCpf(cpf)).thenReturn(Optional.of(beneficiario));
        when(loteAlimentoRepository.findById(1L)).thenReturn(Optional.of(lote));

        atendimentoService.registrarAtendimento(cpf, 1L);

        assertEquals(4, lote.getQuantidadeDisponivel());
        assertEquals(LocalDate.now(), beneficiario.getDataUltimoAtendimento());
        verify(loteAlimentoRepository, times(1)).save(lote);
        verify(beneficiarioRepository, times(1)).save(beneficiario);
    }
}
