package br.com.ada.lms.rest;

import br.com.ada.lms.modelo.Transacao;
import br.com.ada.lms.rest.dto.TransacaoRequest;
import br.com.ada.lms.servico.TransacaoService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;

import java.net.URI;
import java.util.List;

import org.jboss.resteasy.reactive.RestForm;
import org.jboss.resteasy.reactive.multipart.FileUpload;

/**
 * CRUD de alunos.
 *
 *   GET    /alunos       -> 200 lista
 *   GET    /alunos/{id}  -> 200 aluno | 404
 *   POST   /alunos       -> 201 aluno criado + header Location | 400 | 409
 *   PUT    /alunos/{id}  -> 200 aluno atualizado (todos os campos) | 400 | 404 | 409
 *   PATCH  /alunos/{id}  -> 200 aluno atualizado (só campos enviados) | 400 | 404 | 409
 *   DELETE /alunos/{id}  -> 204 sem corpo | 404
 *
 * Os 4xx são gerados por exceções (ApiExceptionMapper) e pela validação (@Valid).
 */
@Path("/transacoes")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class TransacaoResource {

    private final TransacaoService service;

    public TransacaoResource(TransacaoService service) {
        this.service = service;
    }

    @GET
    public List<Transacao> listar() {
        return service.listar();
    }

    @GET
    @Path("/{id}")
    public Transacao buscar(@PathParam("id") Long id) {
        return service.buscar(id);
    }

    /**
     * Aqui retornamos Response (e não Aluno) porque queremos controlar o status:
     * 201 Created + header Location apontando para o novo recurso.
     */
    @POST
    public Response criar(@NotNull @Valid TransacaoRequest dados, @Context UriInfo uriInfo) {
        Transacao transacao = service.criar(dados);
        URI location = uriInfo.getAbsolutePathBuilder().path(transacao.getId().toString()).build();
        return Response.created(location).entity(transacao).build();
    }

    @PUT
    @Path("/{id}")
    public Transacao atualizar(@PathParam("id") Long id, @NotNull @Valid TransacaoRequest dados) {
        return service.atualizar(id, dados);
    }

    @DELETE
    @Path("/{id}")
    public Response remover(@PathParam("id") Long id) {
        service.remover(id);
        return Response.noContent().build();
    }

    @POST
    @Path("/importar")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response importar(@RestForm("file") FileUpload file) {
        List<Transacao> transacoes = service.importar(file);
        return Response.ok(transacoes).build();
    }

    @GET
    @Path("/saldo")
    public double calcularSaldo() {
        return service.calcularSaldo();
    }
}
