package br.com.virtus.catalogo;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

/** Testes da API do catálogo, chamando os endpoints como o site faria. */
@WebMvcTest(CatalogoController.class)
@Import({CatalogoService.class, CatalogoRepository.class})
class CatalogoControllerTest {

    private final MockMvc mockMvc;

    CatalogoControllerTest(@Autowired MockMvc mockMvc) {
        this.mockMvc = mockMvc;
    }

    @Test
    void listaOCatalogoCompleto() throws Exception {
        mockMvc.perform(get("/api/acessorios"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(8));
    }

    @Test
    void buscaTonerEmEstoque() throws Exception {
        mockMvc.perform(get("/api/acessorios").param("categoria", "TONER").param("emEstoque", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].codigo").value("TON-58A"));
    }

    @Test
    void buscaPorImpressoraEPrecoMaximo() throws Exception {
        mockMvc.perform(get("/api/acessorios")
                        .param("impressora", "Epson EcoTank L3250")
                        .param("precoMax", "55"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].codigo").value("PAP-FOTO-A4"));
    }

    @Test
    void categoriaInvalidaRetornaErro400() throws Exception {
        mockMvc.perform(get("/api/acessorios").param("categoria", "GRAMPEADOR"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void detalhaUmAcessorio() throws Exception {
        mockMvc.perform(get("/api/acessorios/TON-58A"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.acessorio.nome").value("Toner HP 58A Preto"))
                .andExpect(jsonPath("$.disponibilidade").value("DISPONIVEL"));
    }

    @Test
    void acessorioInexistenteRetorna404() throws Exception {
        mockMvc.perform(get("/api/acessorios/NAO-EXISTE"))
                .andExpect(status().isNotFound());
    }
}
