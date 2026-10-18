package battleship;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DurationFormatUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Scoreboard {
    private static final Path FILE = Path.of("data", "scoreboard.json");
    private final ObjectMapper mapper = new ObjectMapper();
    private final List<ScoreEntry> entries = new ArrayList<>();

    public Scoreboard() {
        load();
    }

    /** Regista o resultado de um jogo terminado e grava-o em disco. */
    public void add(String player, int bursts, int shots, long durationSeconds) {
        entries.add(new ScoreEntry(player, LocalDateTime.now().withNano(0).toString(),
                bursts, shots, durationSeconds));
        save();
    }

    /** Melhores jogos: menos rajadas primeiro, desempate pela menor duração. */
    public List<ScoreEntry> top(int n) {
        return entries.stream()
                .sorted(Comparator.comparingInt(ScoreEntry::bursts)
                        .thenComparingLong(ScoreEntry::durationSeconds))
                .limit(n)
                .toList();
    }

    public void print(int n) {
        List<ScoreEntry> best = top(n);
        if (best.isEmpty()) {
            System.out.println("Ainda não há jogos registados.");
            return;
        }
        System.out.println(StringUtils.center(" SCOREBOARD - TOP " + n + " ", 66, '='));
        System.out.println(StringUtils.rightPad("#", 4) + StringUtils.rightPad("Jogador", 16)
                + StringUtils.rightPad("Data", 20) + StringUtils.leftPad("Rajadas", 8)
                + StringUtils.leftPad("Tiros", 7) + StringUtils.leftPad("Tempo", 10));
        int pos = 1;
        for (ScoreEntry e : best) {
            System.out.println(StringUtils.rightPad(String.valueOf(pos++), 4)
                    + StringUtils.rightPad(e.player(), 16)
                    + StringUtils.rightPad(e.date().replace('T', ' '), 20)
                    + StringUtils.leftPad(String.valueOf(e.bursts()), 8)
                    + StringUtils.leftPad(String.valueOf(e.shots()), 7)
                    + StringUtils.leftPad(DurationFormatUtils.formatDuration(e.durationSeconds() * 1000, "HH:mm:ss"), 10));
        }
    }

    private void load() {
        try {
            if (Files.exists(FILE) && Files.size(FILE) > 0) {
                entries.addAll(mapper.readValue(FILE.toFile(), new TypeReference<List<ScoreEntry>>() {}));
            }
        } catch (IOException e) {
            System.err.println("Não foi possível ler o scoreboard: " + e.getMessage());
        }
    }

    private void save() {
        try {
            Files.createDirectories(FILE.getParent());
            mapper.writerWithDefaultPrettyPrinter().writeValue(FILE.toFile(), entries);
        } catch (IOException e) {
            System.err.println("Não foi possível gravar o scoreboard: " + e.getMessage());
        }
    }
}
