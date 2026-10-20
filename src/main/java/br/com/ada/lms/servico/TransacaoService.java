package br.com.ada.lms.servico;

import br.com.ada.lms.excecao.RecursoNaoEncontradoException;
import br.com.ada.lms.modelo.Transacao;
import br.com.ada.lms.rest.dto.TransacaoRequest;
import jakarta.enterprise.context.ApplicationScoped;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Regras de negócio + "banco de dados" em memória (um Map).
 * Os dados somem quando a aplicação reinicia; persistência real fica para as próximas aulas.
 *
 * @ApplicationScoped: o Quarkus cria UMA instância e a injeta em quem pedir.
 * ConcurrentHashMap/AtomicLong porque várias requisições podem chegar ao mesmo tempo.
 */
@ApplicationScoped
public class TransacaoService {

    private final Map<Long, Transacao> transacoes = new ConcurrentHashMap<>();
    private final AtomicLong sequencia = new AtomicLong();


    public TransacaoService() {

        // alguns dados iniciais para testar o GET logo de cara
        criar(new TransacaoRequest("Compra feita no Débito - Mercado Livre", "Débito", "Market Place", 99.9, LocalDate.of(2026, 10, 1)));
        criar(new TransacaoRequest("Transferência feita via PIX para Fernanda", "Débito", "Transferência", 100, LocalDate.of(2026, 10, 3)));
        criar(new TransacaoRequest("Compra no iFood", "Crédito", "Alimentação", 87.50, LocalDate.of(2026, 10, 4)));
    }

    public List<Transacao> listar() {
        return transacoes.values().stream()
                .sorted(Comparator.comparing(Transacao::getId))
                .toList();
    }

    public Transacao buscar(Long id) {
        Transacao transacao = transacoes.get(id);
        if (transacao == null) {
            throw new RecursoNaoEncontradoException("Transacao", id);
        }
        return transacao;
    }

    public Transacao criar(TransacaoRequest dados) {
        Transacao transacao = new Transacao(sequencia.incrementAndGet(), dados.descricao(), dados.tipo(), dados.categoria(), dados.valor(), dados.data());
        transacoes.put(transacao.getId(), transacao);
        return transacao;
    }

    /** PUT: substitui TODOS os campos do recurso. */
    public Transacao atualizar(Long id, TransacaoRequest dados) {
        Transacao transacao = buscar(id);
        transacao.setDescricao(dados.descricao());
        transacao.setCategoria(dados.categoria());
        return transacao;
    }

    public void remover(Long id) {
        if (transacoes.remove(id) == null) {
            throw new RecursoNaoEncontradoException("Transacao", id);
        }
    }

}
