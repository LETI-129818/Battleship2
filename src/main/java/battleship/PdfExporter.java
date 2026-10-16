package battleship;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;

import java.io.IOException;
import java.util.List;

public class PdfExporter {

    public static void exportarJogadasParaPdf(List<String> historicoJogadas, String nomeFicheiro) {
        // Cria um novo documento PDF
        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage();
            document.addPage(page);

            // Inicia o fluxo de conteúdo para escrever na página
            try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
                contentStream.beginText();
                // Título do documento
                contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD), 16);
                contentStream.newLineAtOffset(50, 750);
                contentStream.showText("Historico de Jogadas - Batalha Naval");
                contentStream.endText();

                contentStream.beginText();
                contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA), 12);
                contentStream.newLineAtOffset(50, 720);

                int offsetVertical = 0;
                for (String jogada : historicoJogadas) {
                    contentStream.showText(jogada);
                    contentStream.newLineAtOffset(0, -20); // Desce uma linha
                    offsetVertical += 20;

                    // Evitar sair da página se houver muitas jogadas
                    if (offsetVertical > 650) {
                        break;
                    }
                }
                contentStream.endText();
            }

            // Guarda o documento no disco
            document.save(nomeFicheiro);
            System.out.println("PDF gerado com sucesso: " + nomeFicheiro);

        } catch (IOException e) {
            System.err.println("Erro ao gerar o PDF: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
