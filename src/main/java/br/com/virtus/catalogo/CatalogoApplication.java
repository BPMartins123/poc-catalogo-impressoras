package br.com.virtus.catalogo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/** Inicia a API do catálogo de acessórios de impressoras. */
@SpringBootApplication
public class CatalogoApplication {

    public static void main(String[] args) {
        SpringApplication.run(CatalogoApplication.class, args);
    }
}
