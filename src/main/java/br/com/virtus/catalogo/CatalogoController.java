package br.com.virtus.catalogo;

import java.math.BigDecimal;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * API do catálogo, consumida pelo site.
 *
 * ATENÇÃO: esta versão tem problemas colocados DE PROPÓSITO para a POC.
 */
@RestController
@RequestMapping("/api/acessorios")
public class CatalogoController {

    private final CatalogoService service;

    public CatalogoController(CatalogoService service) {
        this.service = service;
    }

    /** Lista o catálogo, com filtros opcionais (busca do site). */
    @GetMapping
    public List<Acessorio> buscar(@RequestParam(required = false) String termo,
                                  @RequestParam(required = false) String categoria,
                                  @RequestParam(required = false) String impressora,
                                  @RequestParam(required = false) String precoMax,
                                  @RequestParam(defaultValue = "false") boolean emEstoque) {
        Categoria cat = null;
        BigDecimal preco = null;
        try {
            cat = Categoria.valueOf(categoria.toUpperCase());
            preco = new BigDecimal(precoMax);
        // Problema 5 (Code Smell - java:S2221): captura genérica de Exception,
        // que ainda descarta um filtro válido quando o outro vem vazio
        } catch (Exception e) {
            // ignora filtros inválidos
        }
        return service.buscar(termo, cat, impressora, preco, emEstoque);
    }

    /** Página de detalhe de um acessório. */
    @GetMapping("/{codigo}")
    public ResponseEntity<AcessorioDetalhe> detalhar(@PathVariable String codigo) {
        return service.buscarPorCodigo(codigo)
                .map(a -> ResponseEntity.ok(new AcessorioDetalhe(a, service.disponibilidade(a))))
                .orElse(ResponseEntity.notFound().build());
    }
}
