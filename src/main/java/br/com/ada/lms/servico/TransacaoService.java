package br.com.ada.lms.servico;

import br.com.ada.lms.excecao.ConflitoException;
import br.com.ada.lms.excecao.RecursoNaoEncontradoException;
import br.com.ada.lms.modelo.Transacao;
import br.com.ada.lms.repositorio.TransacaoRepository;
import br.com.ada.lms.rest.dto.TransacaoRequest;
import io.quarkus.panache.common.Sort;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

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

   // private final Map<Long, Transacao> transacoes = new ConcurrentHashMap<>();
   // private final AtomicLong sequencia = new AtomicLong();

    private final TransacaoRepository repository;
    private final ValidadorPatch validadorPatch;

    public TransacaoService(TransacaoRepository repository, ValidadorPatch validadorPatch) {
        this.repository = repository;
        this.validadorPatch = validadorPatch;
    }


/*
    public TransacaoService() {

        // alguns dados iniciais para testar o GET logo de cara
        criar(new TransacaoRequest("Compra feita no Débito - Mercado Livre", "Débito", "Market Place", 99.9, LocalDate.of(2026, 10, 1)));
        criar(new TransacaoRequest("Transferência feita via PIX para Fernanda", "Débito", "Transferência", 100, LocalDate.of(2026, 10, 3)));
        criar(new TransacaoRequest("Compra no iFood", "Crédito", "Alimentação", 87.50, LocalDate.of(2026, 10, 4)));
    }
*/
public List<Transacao> listar() {
    return repository.listAll(Sort.by("id"));
}

    public Transacao buscar(Long id) {
        return repository.findByIdOptional(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Aluno", id));
    }

    @Transactional
    public Transacao criar(TransacaoRequest dados) {
       // verificarEmailDisponivel(dados.email(), null);
        Transacao transacao = new Transacao(dados.descricao(), dados.tipo(), dados.categoria(), dados.valor(), dados.data());
        repository.persist(transacao); // INSERT; o id é preenchido pelo banco
        return transacao;
    }

    /** PUT: substitui TODOS os campos do recurso. */
    @Transactional
    public Transacao atualizar(Long id, TransacaoRequest dados) {
        Transacao transacao = buscar(id);
     //   verificarEmailDisponivel(dados.email(), id);
        transacao.setDescricao(dados.descricao());
        transacao.setTipo(dados.tipo());
        transacao.setData(dados.data());
        transacao.setCategoria(dados.categoria());
        transacao.setValor(dados.valor());

        return transacao;
    }
/*
    /** PATCH: altera somente os campos enviados (os que vierem null são mantidos).
    @Transactional
    public Transacao atualizarParcialmente(Long id, TransacaoRequest dados) {
        Transacao transacao = buscar(id);
        validadorPatch.validarCamposInformados(dados);
        if (dados.descricao() != null) {
            transacao.setDescricao(dados.descricao());
        }
       if (dados.tipo() != null) {
            verificarEmailDisponivel(dados.email(), id);
            aluno.setEmail(dados.email());
        }
        return transacao;
    }
*/
    @Transactional
    public void remover(Long id) {
        if (!repository.deleteById(id)) {
            throw new RecursoNaoEncontradoException("Transacao", id);
        }
    }
/*
    private void verificarEmailDisponivel(String email, Long idAtual) {
        if (repository.emailEmUso(email, idAtual)) {
            throw new ConflitoException("Já existe um aluno com o email " + email + ".");
        }

 */

    }

