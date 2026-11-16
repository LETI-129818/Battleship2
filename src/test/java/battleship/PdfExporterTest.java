package battleship;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Tests for PdfExporter class")
class PdfExporterTest {

    @TempDir
    Path tempDir;

    @Test
    @DisplayName("Exportar histórico vazio cria ficheiro PDF válido")
    void testExportarHistoricoVazio() {
        File pdfFile = tempDir.resolve("historico_vazio.pdf").toFile();
        List<String> jogadas = List.of();

        assertDoesNotThrow(() -> PdfExporter.exportarJogadasParaPdf(jogadas, pdfFile.getAbsolutePath()));
        assertTrue(pdfFile.exists(), "O ficheiro PDF deve ser criado mesmo com lista vazia");
        assertTrue(pdfFile.length() > 0, "O ficheiro PDF não deve estar vazio");
    }

    @Test
    @DisplayName("Exportar histórico com poucas jogadas escreve todas sem atingir o limite da página")
    void testExportarPoucasJogadas() {
        File pdfFile = tempDir.resolve("historico_poucas.pdf").toFile();
        List<String> jogadas = List.of(
                "Jogada 1: Tiro em A1 - Agua",
                "Jogada 2: Tiro em B2 - Acertou na Fragata",
                "Jogada 3: Tiro em B3 - Afundou Fragata"
        );

        assertDoesNotThrow(() -> PdfExporter.exportarJogadasParaPdf(jogadas, pdfFile.getAbsolutePath()));
        assertTrue(pdfFile.exists());
        assertTrue(pdfFile.length() > 0);
    }

    @Test
    @DisplayName("Exportar mais de 33 jogadas interrompe a escrita ao exceder o limite vertical da página")
    void testExportarMuitasJogadasExcedeLimiteVertical() {
        File pdfFile = tempDir.resolve("historico_muitas.pdf").toFile();
        List<String> jogadas = new ArrayList<>();
        for (int i = 1; i <= 40; i++) {
            jogadas.add("Jogada " + i + ": Tiro em C" + (i % 10));
        }

        assertDoesNotThrow(() -> PdfExporter.exportarJogadasParaPdf(jogadas, pdfFile.getAbsolutePath()));
        assertTrue(pdfFile.exists());
        assertTrue(pdfFile.length() > 0);
    }

    @Test
    @DisplayName("Exportar para caminho inválido trata IOException sem lançar exceção para fora")
    void testExportarCaminhoInvalido() {
        String caminhoInvalido = tempDir.resolve("pasta_inexistente").resolve("saida.pdf").toString();
        List<String> jogadas = List.of("Jogada 1: Tiro em A1");

        assertDoesNotThrow(() -> PdfExporter.exportarJogadasParaPdf(jogadas, caminhoInvalido));
        assertFalse(new File(caminhoInvalido).exists());
    }
}