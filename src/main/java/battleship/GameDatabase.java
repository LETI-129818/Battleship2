package battleship;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Gestor responsável por guardar e carregar o histórico de jogadas em ficheiro JSON.
 */
public class GameDatabase {
    private static final String FILE_PATH = "jogadas.json";
    private final ObjectMapper mapper;

    public GameDatabase() {
        this.mapper = new ObjectMapper();
        // Formata o JSON com quebras de linha e indentação para ficar legível
        this.mapper.enable(SerializationFeature.INDENT_OUTPUT);
    }

    /**
     * Guarda a lista completa de jogadas no ficheiro JSON.
     *
     * @param shots Lista de jogadas efetuadas.
     */
    public void saveShots(List<battleship.ShotRecord> shots) {
        try {
            mapper.writeValue(new File(FILE_PATH), shots);
            System.out.println("Jogadas guardadas com sucesso em: " + FILE_PATH);
        } catch (IOException e) {
            System.err.println("Erro ao guardar jogadas no ficheiro: " + e.getMessage());
        }
    }

    /**
     * Carrega a lista de jogadas gravadas no ficheiro JSON.
     *
     * @return Lista de jogadas guardadas.
     */
    public List<battleship.ShotRecord> loadShots() {
        File file = new File(FILE_PATH);
        if (!file.exists()) {
            return new ArrayList<>();
        }
        try {
            return mapper.readValue(file, mapper.getTypeFactory().constructCollectionType(List.class, battleship.ShotRecord.class));
        } catch (IOException e) {
            System.err.println("Erro ao carregar jogadas do ficheiro: " + e.getMessage());
            return new ArrayList<>();
        }
    }
}
