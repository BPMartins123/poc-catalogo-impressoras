package br.com.virtus.catalogo;

import java.math.BigDecimal;
import java.util.List;
import org.springframework.stereotype.Repository;

/** Catálogo em memória (simula o banco de dados do site). */
@Repository
public class CatalogoRepository {

    private static final List<Acessorio> ACESSORIOS = List.of(
            new Acessorio("TON-58A", "Toner HP 58A Preto", Categoria.TONER,
                    List.of("HP LaserJet Pro M404", "HP LaserJet Pro MFP M428"),
                    new BigDecimal("489.90"), 12),
            new Acessorio("TON-TN2370", "Toner Brother TN-2370", Categoria.TONER,
                    List.of("Brother HL-L2360DW", "Brother DCP-L2540DW"),
                    new BigDecimal("329.00"), 0),
            new Acessorio("CAR-T544-PT", "Garrafa de Tinta Epson T544 Preta", Categoria.CARTUCHO,
                    List.of("Epson EcoTank L3250", "Epson EcoTank L3150"),
                    new BigDecimal("59.90"), 40),
            new Acessorio("CAR-664-CO", "Cartucho HP 664 Colorido", Categoria.CARTUCHO,
                    List.of("HP DeskJet Ink Advantage 2676"),
                    new BigDecimal("94.90"), 3),
            new Acessorio("CIL-DR2340", "Cilindro Brother DR-2340", Categoria.CILINDRO,
                    List.of("Brother HL-L2360DW", "Brother DCP-L2540DW"),
                    new BigDecimal("399.00"), 7),
            new Acessorio("PAP-FOTO-A4", "Papel Fotográfico Glossy A4 (50 folhas)", Categoria.PAPEL,
                    List.of("Epson EcoTank L3250", "HP DeskJet Ink Advantage 2676"),
                    new BigDecimal("49.90"), 25),
            new Acessorio("CAB-USB-AB", "Cabo USB 2.0 A/B 1,8 m", Categoria.CABO,
                    List.of("HP LaserJet Pro M404", "Brother HL-L2360DW", "Epson EcoTank L3250"),
                    new BigDecimal("24.90"), 60),
            new Acessorio("MAN-KIT-M404", "Kit de Manutenção HP LaserJet M404", Categoria.MANUTENCAO,
                    List.of("HP LaserJet Pro M404"),
                    new BigDecimal("899.00"), 2));

    /** Todos os acessórios cadastrados. */
    public List<Acessorio> todos() {
        return ACESSORIOS;
    }
}
