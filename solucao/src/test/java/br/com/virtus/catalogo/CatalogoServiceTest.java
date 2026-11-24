package br.com.virtus.catalogo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.Test;

/** Testes do serviço do catálogo (versão corrigida): cobrem todas as regras e ramos. */
class CatalogoServiceTest {

    private final CatalogoRepository repository = new CatalogoRepository();
    private final CatalogoService service = new CatalogoService(repository, "segredo");

    private static List<String> codigos(List<Acessorio> acessorios) {
        return acessorios.stream().map(Acessorio::codigo).toList();
    }

    private static Acessorio comEstoque(int estoque) {
        return new Acessorio("TESTE", "Acessório de teste", Categoria.CABO, List.of(),
                BigDecimal.TEN, estoque);
    }

    @Test
    void listaTodosOsAcessorios() {
        assertEquals(8, service.listar().size());
    }

    @Test
    void encontraAcessorioPeloCodigoSemDiferenciarMaiusculas() {
        assertEquals("Toner HP 58A Preto", service.buscarPorCodigo("ton-58a").orElseThrow().nome());
    }

    @Test
    void naoEncontraCodigoInexistente() {
        assertTrue(service.buscarPorCodigo("NAO-EXISTE").isEmpty());
    }

    @Test
    void semFiltrosRetornaOCatalogoInteiro() {
        assertEquals(8, service.buscar(null, null, null, null, false).size());
    }

    @Test
    void filtraPorTermoNoNome() {
        assertEquals(List.of("TON-58A", "TON-TN2370"), codigos(service.buscar("toner", null, null, null, false)));
    }

    @Test
    void filtraPorCategoria() {
        assertEquals(List.of("CAR-T544-PT", "CAR-664-CO"),
                codigos(service.buscar(null, Categoria.CARTUCHO, null, null, false)));
    }

    @Test
    void filtraPorImpressoraCompativel() {
        // Texto montado em tempo de execução: só funciona se a comparação usar equals()
        String impressora = String.join(" ", "Brother", "HL-L2360DW");
        assertEquals(List.of("TON-TN2370", "CIL-DR2340", "CAB-USB-AB"),
                codigos(service.buscar(null, null, impressora, null, false)));
    }

    @Test
    void filtraPorPrecoMaximo() {
        assertEquals(List.of("PAP-FOTO-A4", "CAB-USB-AB"),
                codigos(service.buscar(null, null, null, new BigDecimal("50.00"), false)));
    }

    @Test
    void filtraSomenteItensEmEstoque() {
        List<String> emEstoque = codigos(service.buscar(null, null, null, null, true));
        assertEquals(7, emEstoque.size());
        assertFalse(emEstoque.contains("TON-TN2370"));
    }

    @Test
    void combinaFiltros() {
        assertEquals(List.of("TON-58A"),
                codigos(service.buscar(null, Categoria.TONER, null, null, true)));
    }

    @Test
    void disponibilidadeConformeEstoque() {
        assertEquals("ESGOTADO", service.disponibilidade(comEstoque(0)));
        assertEquals("ULTIMAS_UNIDADES", service.disponibilidade(comEstoque(4)));
        assertEquals("DISPONIVEL", service.disponibilidade(comEstoque(5)));
    }

    @Test
    void autenticaFornecedorSomenteComASenhaConfigurada() {
        assertTrue(service.autenticarFornecedor("segredo"));
        assertFalse(service.autenticarFornecedor("errada"));
        assertFalse(service.autenticarFornecedor(null));
    }

    @Test
    void semSenhaConfiguradaNinguemAutentica() {
        CatalogoService semSenha = new CatalogoService(repository, "");
        assertFalse(semSenha.autenticarFornecedor(""));
    }

    @Test
    void versaoDoCatalogoEUmHashSha256Estavel() throws Exception {
        String versao = service.versaoCatalogo();
        assertTrue(versao.matches("[0-9a-f]{64}"));
        assertEquals(versao, new CatalogoService(new CatalogoRepository(), "").versaoCatalogo());
    }
}
