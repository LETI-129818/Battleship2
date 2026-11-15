package battleship;

import org.junit.jupiter.api.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Testes da classe Scoreboard")
class ScoreboardTest {

    /** Mesmo ficheiro que o Scoreboard usa. */
    private static final Path FILE = Path.of("data", "scoreboard.json");

    private byte[] backup;
    private final PrintStream originalOut = System.out;
    private final PrintStream originalErr = System.err;
    private final ByteArrayOutputStream out = new ByteArrayOutputStream();
    private final ByteArrayOutputStream err = new ByteArrayOutputStream();

    @BeforeEach
    void setUp() throws IOException {
        // guarda o scoreboard real e começa cada teste com o ficheiro vazio
        backup = Files.exists(FILE) && !Files.isDirectory(FILE) ? Files.readAllBytes(FILE) : null;
        Files.createDirectories(FILE.getParent());
        Files.write(FILE, new byte[0]);

        System.setOut(new PrintStream(out, true, StandardCharsets.UTF_8));
        System.setErr(new PrintStream(err, true, StandardCharsets.UTF_8));
    }

    @AfterEach
    void tearDown() throws IOException {
        System.setOut(originalOut);
        System.setErr(originalErr);

        // repõe o scoreboard real tal como estava
        if (Files.isDirectory(FILE)) Files.delete(FILE);
        if (backup != null) Files.write(FILE, backup);
        else Files.deleteIfExists(FILE);
    }

    private String output() {
        return out.toString(StandardCharsets.UTF_8);
    }

    @Nested
    @DisplayName("Carregamento (load)")
    class Load {

        @Test
        @DisplayName("Com o ficheiro vazio, o scoreboard começa sem entradas")
        void emptyFile() {
            assertTrue(new Scoreboard().top(10).isEmpty());
        }

        @Test
        @DisplayName("Sem ficheiro, o scoreboard começa sem entradas")
        void missingFile() throws IOException {
            Files.delete(FILE);
            assertTrue(new Scoreboard().top(10).isEmpty());
        }

        @Test
        @DisplayName("Com JSON inválido, mostra erro de leitura e começa vazio")
        void invalidJson() throws IOException {
            Files.writeString(FILE, "isto não é JSON");
            Scoreboard sb = new Scoreboard();

            assertTrue(sb.top(10).isEmpty());
            assertTrue(err.toString(StandardCharsets.UTF_8).contains("Não foi possível ler o scoreboard"));
        }

        @Test
        @DisplayName("Os jogos gravados são lidos por uma nova instância")
        void persistsBetweenInstances() {
            new Scoreboard().add("Ana", 5, 15, 120);

            List<ScoreEntry> top = new Scoreboard().top(10);
            assertEquals(1, top.size());
            assertEquals("Ana", top.getFirst().player());
            assertEquals(5, top.getFirst().bursts());
            assertEquals(15, top.getFirst().shots());
            assertEquals(120, top.getFirst().durationSeconds());
        }
    }

    @Nested
    @DisplayName("Gravação (add / save)")
    class Save {

        @Test
        @DisplayName("add regista a entrada com a data atual (sem nanossegundos)")
        void addSetsDate() {
            Scoreboard sb = new Scoreboard();
            sb.add("Rui", 3, 9, 60);

            String date = sb.top(1).getFirst().date();
            assertNotNull(date);
            assertFalse(date.contains("."), "A data não deve ter nanossegundos");
        }

        @Test
        @DisplayName("Se não for possível gravar, mostra erro mas mantém a entrada em memória")
        void saveError() throws IOException {
            // um diretório com o nome do ficheiro faz a escrita falhar
            Files.delete(FILE);
            Files.createDirectory(FILE);

            Scoreboard sb = new Scoreboard();
            sb.add("Ana", 5, 15, 120);

            assertEquals(1, sb.top(10).size());
            assertTrue(err.toString(StandardCharsets.UTF_8).contains("Não foi possível gravar o scoreboard"));
        }
    }

    @Nested
    @DisplayName("Ranking (top)")
    class Top {

        @Test
        @DisplayName("Ordena por menos rajadas primeiro")
        void sortsByBursts() {
            Scoreboard sb = new Scoreboard();
            sb.add("C", 9, 27, 100);
            sb.add("A", 3, 9, 100);
            sb.add("B", 6, 18, 100);

            List<String> players = sb.top(3).stream().map(ScoreEntry::player).toList();
            assertEquals(List.of("A", "B", "C"), players);
        }

        @Test
        @DisplayName("Em empate de rajadas, desempata pela menor duração")
        void tieBreakByDuration() {
            Scoreboard sb = new Scoreboard();
            sb.add("Lento", 5, 15, 300);
            sb.add("Rapido", 5, 15, 60);

            assertEquals("Rapido", sb.top(2).getFirst().player());
            assertEquals("Lento", sb.top(2).get(1).player());
        }

        @Test
        @DisplayName("Devolve no máximo n entradas")
        void limitsToN() {
            Scoreboard sb = new Scoreboard();
            sb.add("A", 1, 3, 10);
            sb.add("B", 2, 6, 10);
            sb.add("C", 3, 9, 10);

            assertEquals(2, sb.top(2).size());
            assertEquals(3, sb.top(10).size());
            assertTrue(sb.top(0).isEmpty());
        }
    }

    @Nested
    @DisplayName("Impressão (print)")
    class Print {

        @Test
        @DisplayName("Sem jogos, mostra mensagem a indicar que não há registos")
        void printEmpty() {
            new Scoreboard().print(5);
            assertTrue(output().contains("Ainda não há jogos registados."));
        }

        @Test
        @DisplayName("Com jogos, mostra cabeçalho, posições, jogadores e duração formatada")
        void printEntries() {
            Scoreboard sb = new Scoreboard();
            sb.add("Ana", 5, 15, 120);
            sb.add("Rui", 7, 21, 3725);
            out.reset();

            sb.print(3);
            String text = output();

            assertAll(
                    () -> assertTrue(text.contains("SCOREBOARD - TOP 3")),
                    () -> assertTrue(text.contains("Jogador")),
                    () -> assertTrue(text.contains("Ana")),
                    () -> assertTrue(text.contains("Rui")),
                    () -> assertTrue(text.contains("00:02:00")),   // 120 s
                    () -> assertTrue(text.contains("01:02:05")),   // 3725 s
                    () -> assertTrue(text.indexOf("Ana") < text.indexOf("Rui"), "Ana (menos rajadas) aparece primeiro"),
                    () -> assertFalse(text.contains("T"  + "00"), "A data é mostrada com espaço em vez de 'T'")
            );
        }
    }
}