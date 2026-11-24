package br.com.virtus.catalogo;

import java.math.BigDecimal;
import java.util.List;

/**
 * Um acessório de impressora do catálogo.
 *
 * @param codigo                 código do produto (SKU)
 * @param nome                   nome exibido no site
 * @param categoria              categoria do acessório
 * @param impressorasCompativeis modelos de impressora compatíveis
 * @param preco                  preço em reais
 * @param estoque                unidades disponíveis
 */
public record Acessorio(
        String codigo,
        String nome,
        Categoria categoria,
        List<String> impressorasCompativeis,
        BigDecimal preco,
        int estoque) {
}
