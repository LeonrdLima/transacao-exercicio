package br.com.ada.lms.servico;

import br.com.ada.lms.excecao.RecursoNaoEncontradoException;
import br.com.ada.lms.modelo.Transacao;
import br.com.ada.lms.repositorio.TransacaoRepository;
import br.com.ada.lms.rest.dto.TransacaoRequest;
import io.quarkus.panache.common.Sort;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.jboss.resteasy.reactive.multipart.FileUpload;

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
                .orElseThrow(() -> new RecursoNaoEncontradoException("Transacao", id));
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

    @Transactional
    public List<Transacao> importar(FileUpload file) {
        List<Transacao> transacoes = new ArrayList<>();

        try(BufferedReader reader = Files.newBufferedReader(file.filePath(), StandardCharsets.UTF_8)) {
            String linha;
            reader.readLine();

            String categoriaPadrao = "outro";
            DateTimeFormatter formato = DateTimeFormatter.ofPattern("dd/MM/yyyy");

            while ((linha = reader.readLine()) != null) {
                if (linha.isBlank()) {
                    continue;
                }

                String[] campos = linha.split(",");
                if (campos.length != 4) {
                    throw new IllegalArgumentException("Linha de CSV inválida: " + linha);
                }

                LocalDate data = LocalDate.parse(campos[0], formato);
                double valor = Double.parseDouble(campos[1]);
                String descricao = campos[3];
                String categoria = categoriaPadrao;
                String tipo = valor < 0 ? "Saída" : "Entrada";

                Transacao transacao = new Transacao(descricao, tipo, categoria, valor, data);
                repository.persist(transacao);
                transacoes.add(transacao);
            }
        } catch (IOException e) {
            throw new RuntimeException("Erro ao importar arquivo", e);
        }

        return transacoes;
    }

    // calculo do extrato
    public double calcularSaldo() {
        List<Transacao> transacoes = listar();
        double saldo = 0.0;
        for(Transacao transacao : transacoes) {
            saldo += transacao.getValor();
        }
        return saldo;
    }
}
