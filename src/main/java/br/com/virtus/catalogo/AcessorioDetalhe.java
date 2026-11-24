package br.com.virtus.catalogo;

/**
 * Resposta da página de detalhe de um acessório.
 *
 * @param acessorio       o acessório
 * @param disponibilidade situação do estoque exibida no site
 */
public record AcessorioDetalhe(Acessorio acessorio, String disponibilidade) {
}
