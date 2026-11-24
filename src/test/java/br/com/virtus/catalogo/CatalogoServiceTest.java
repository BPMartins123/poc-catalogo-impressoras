package br.com.virtus.catalogo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/**
 * Testes da versão com problemas.
 *
 * Problema 6 (Cobertura): só o básico do serviço é testado e o controller
 * não tem teste nenhum, então a cobertura fica abaixo dos 80% do Quality Gate.
 */
class CatalogoServiceTest {

    private final CatalogoService service = new CatalogoService(new CatalogoRepository());

    @Test
    void listaTodosOsAcessorios() {
        assertEquals(8, service.listar().size());
    }

    @Test
    void encontraAcessorioPeloCodigo() {
        assertTrue(service.buscarPorCodigo("TON-58A").isPresent());
    }
}
