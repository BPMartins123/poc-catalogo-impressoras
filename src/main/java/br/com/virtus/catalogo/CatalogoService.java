package br.com.virtus.catalogo;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HexFormat;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;

/**
 * Regras do catálogo: listar, detalhar e buscar acessórios.
 *
 * ATENÇÃO: esta versão tem problemas colocados DE PROPÓSITO para a POC
 * reprovar no Quality Gate. Não use como referência de código bom.
 */
@Service
public class CatalogoService {

    // Problema 1 (Security Hotspot - java:S2068): senha fixa no código
    private static final String SENHA_FORNECEDOR = "fornecedor@2026";

    private final CatalogoRepository repository;

    public CatalogoService(CatalogoRepository repository) {
        this.repository = repository;
    }

    public List<Acessorio> listar() {
        return repository.todos();
    }

    public Optional<Acessorio> buscarPorCodigo(String codigo) {
        return repository.todos().stream()
                .filter(a -> a.codigo().equalsIgnoreCase(codigo))
                .findFirst();
    }

    public List<Acessorio> buscar(String termo, Categoria categoria, String impressora,
                                  BigDecimal precoMaximo, boolean somenteEmEstoque) {
        List<Acessorio> resultado = new ArrayList<>();
        for (Acessorio a : repository.todos()) {
            if (termo != null && !a.nome().toLowerCase().contains(termo.toLowerCase())) {
                continue;
            }
            if (categoria != null && a.categoria() != categoria) {
                continue;
            }
            // Problema 2 (Bug - java:S4973): String comparada com == em vez de equals()
            if (impressora != null && !a.impressorasCompativeis().stream().anyMatch(m -> m == impressora)) {
                continue;
            }
            if (precoMaximo != null && a.preco().compareTo(precoMaximo) > 0) {
                continue;
            }
            if (somenteEmEstoque && a.estoque() <= 0) {
                continue;
            }
            resultado.add(a);
        }
        return resultado;
    }

    public String disponibilidade(Acessorio acessorio) {
        if (acessorio.estoque() == 0) {
            return "ESGOTADO";
        } else if (acessorio.estoque() < 5) {
            return "ULTIMAS_UNIDADES";
        // Problema 3 (Bug - java:S1862): condição repetida, este ramo nunca executa
        } else if (acessorio.estoque() < 5) {
            return "POUCAS_UNIDADES";
        }
        return "DISPONIVEL";
    }

    public boolean autenticarFornecedor(String senha) {
        return SENHA_FORNECEDOR.equals(senha);
    }

    /** Versão do catálogo, usada pelo site para saber se precisa recarregar a lista. */
    public String versaoCatalogo() throws NoSuchAlgorithmException {
        // Problema 4 (Security - java:S4790): MD5 é um algoritmo de hash fraco
        MessageDigest md = MessageDigest.getInstance("MD5");
        for (Acessorio a : repository.todos()) {
            md.update((a.codigo() + a.preco() + a.estoque()).getBytes(StandardCharsets.UTF_8));
        }
        return HexFormat.of().formatHex(md.digest());
    }
}
