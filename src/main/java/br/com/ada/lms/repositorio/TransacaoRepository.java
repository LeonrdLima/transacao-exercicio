package br.com.ada.lms.repositorio;

import br.com.ada.lms.modelo.Transacao;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

/**
 * Acesso à tabela de transações.
 */
@ApplicationScoped
public class TransacaoRepository implements PanacheRepository<Transacao> {
}
