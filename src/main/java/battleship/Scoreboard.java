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
    public static final int PLAYER_WIDTH = 16;
    public static final int RANK_WIDTH = 4;
    public static final int DATE_WIDTH = 20;
    public static final int BURSTS_WIDTH = 8;
    public static final int SHOTS_WIDTH = 7;
    public static final int TIME_WIDTH = 10;
    public static final int TABLE_WIDTH = 66;
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
        System.out.println(StringUtils.center(" SCOREBOARD - TOP " + n + " ", TABLE_WIDTH, '='));
        System.out.println(StringUtils.rightPad("#", RANK_WIDTH) + StringUtils.rightPad("Jogador", PLAYER_WIDTH)
                + StringUtils.rightPad("Data", DATE_WIDTH) + StringUtils.leftPad("Rajadas", BURSTS_WIDTH)
                + StringUtils.leftPad("Tiros", SHOTS_WIDTH) + StringUtils.leftPad("Tempo", TIME_WIDTH));
        int rank = 1;
        for (ScoreEntry entry : best) {
            System.out.println(StringUtils.rightPad(String.valueOf(rank++), RANK_WIDTH)
                    + StringUtils.rightPad(entry.player(), PLAYER_WIDTH)
                    + StringUtils.rightPad(entry.date().replace('T', ' '), DATE_WIDTH)
                    + StringUtils.leftPad(String.valueOf(entry.bursts()), BURSTS_WIDTH)
                    + StringUtils.leftPad(String.valueOf(entry.shots()), SHOTS_WIDTH)
                    + StringUtils.leftPad(DurationFormatUtils.formatDuration(entry.durationSeconds() * 1000, "HH:mm:ss"), TIME_WIDTH));
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
