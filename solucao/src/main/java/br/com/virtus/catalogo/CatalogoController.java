package br.com.virtus.catalogo;

import java.math.BigDecimal;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/** API do catálogo, consumida pelo site (versão corrigida). */
@RestController
@RequestMapping("/api/acessorios")
public class CatalogoController {

    private final CatalogoService service;

    public CatalogoController(CatalogoService service) {
        this.service = service;
    }

    // Correção 5: o Spring converte categoria e preço; valor inválido vira erro 400,
    // em vez de ser engolido por um catch genérico
    /** Lista o catálogo, com filtros opcionais (busca do site). */
    @GetMapping
    public List<Acessorio> buscar(@RequestParam(required = false) String termo,
                                  @RequestParam(required = false) Categoria categoria,
                                  @RequestParam(required = false) String impressora,
                                  @RequestParam(required = false) BigDecimal precoMax,
                                  @RequestParam(defaultValue = "false") boolean emEstoque) {
        return service.buscar(termo, categoria, impressora, precoMax, emEstoque);
    }

    /** Página de detalhe de um acessório. */
    @GetMapping("/{codigo}")
    public ResponseEntity<AcessorioDetalhe> detalhar(@PathVariable String codigo) {
        return service.buscarPorCodigo(codigo)
                .map(a -> ResponseEntity.ok(new AcessorioDetalhe(a, service.disponibilidade(a))))
                .orElse(ResponseEntity.notFound().build());
    }
}
