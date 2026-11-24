package br.com.virtus.catalogo;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/** Regras do catálogo: listar, detalhar e buscar acessórios (versão corrigida). */
@Service
public class CatalogoService {

    static final int LIMITE_ULTIMAS_UNIDADES = 5;

    private final CatalogoRepository repository;
    private final String senhaFornecedor;

    // Correção 1: a senha vem da configuração (variável de ambiente), não do código
    public CatalogoService(CatalogoRepository repository,
                           @Value("${catalogo.fornecedor.senha:}") String senhaFornecedor) {
        this.repository = repository;
        this.senhaFornecedor = senhaFornecedor;
    }

    public List<Acessorio> listar() {
        return repository.todos();
    }

    public Optional<Acessorio> buscarPorCodigo(String codigo) {
        return repository.todos().stream()
                .filter(a -> a.codigo().equalsIgnoreCase(codigo))
                .findFirst();
    }

    // Correção 2: impressora comparada com equals() (via contains)
    public List<Acessorio> buscar(String termo, Categoria categoria, String impressora,
                                  BigDecimal precoMaximo, boolean somenteEmEstoque) {
        return repository.todos().stream()
                .filter(a -> termo == null || minusculo(a.nome()).contains(minusculo(termo)))
                .filter(a -> categoria == null || a.categoria() == categoria)
                .filter(a -> impressora == null || a.impressorasCompativeis().contains(impressora))
                .filter(a -> precoMaximo == null || a.preco().compareTo(precoMaximo) <= 0)
                .filter(a -> !somenteEmEstoque || a.estoque() > 0)
                .toList();
    }

    // Correção 3: sem condição repetida
    public String disponibilidade(Acessorio acessorio) {
        if (acessorio.estoque() == 0) {
            return "ESGOTADO";
        }
        if (acessorio.estoque() < LIMITE_ULTIMAS_UNIDADES) {
            return "ULTIMAS_UNIDADES";
        }
        return "DISPONIVEL";
    }

    public boolean autenticarFornecedor(String senha) {
        if (senha == null || senhaFornecedor.isEmpty()) {
            return false;
        }
        return MessageDigest.isEqual(
                senhaFornecedor.getBytes(StandardCharsets.UTF_8),
                senha.getBytes(StandardCharsets.UTF_8));
    }

    // Correção 4: SHA-256 no lugar de MD5
    /** Versão do catálogo, usada pelo site para saber se precisa recarregar a lista. */
    public String versaoCatalogo() throws NoSuchAlgorithmException {
        MessageDigest sha256 = MessageDigest.getInstance("SHA-256");
        for (Acessorio a : repository.todos()) {
            sha256.update((a.codigo() + a.preco() + a.estoque()).getBytes(StandardCharsets.UTF_8));
        }
        return HexFormat.of().formatHex(sha256.digest());
    }

    private static String minusculo(String texto) {
        return texto.toLowerCase(Locale.ROOT);
    }
}
