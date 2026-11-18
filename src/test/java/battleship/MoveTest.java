package battleship;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MoveTest {

    @Test
    @DisplayName("Verificar o número da jogada")
    void getNumber() {
        Move move = new Move(1, new ArrayList<>(), new ArrayList<>());

        assertEquals(1, move.getNumber());
    }

    @Test
    @DisplayName("Verificar a lista de tiros")
    void getShots() {
        List<IPosition> shots = new ArrayList<>();
        Move move = new Move(1, shots, new ArrayList<>());

        assertSame(shots, move.getShots());
    }

    @Test
    @DisplayName("Verificar a lista de resultados dos tiros")
    void getShotResults() {
        List<IGame.ShotResult> results = new ArrayList<>();
        Move move = new Move(1, new ArrayList<>(), results);

        assertSame(results, move.getShotResults());
    }

    @Test
    @DisplayName("Verificar a representação textual da jogada")
    void testToString() {
        Move move = new Move(1, new ArrayList<>(), new ArrayList<>());

        assertEquals("Move{number=1, shots=0, results=0}", move.toString());
    }


    @Test
    @DisplayName("Verificar um tiro válido que falha o navio")
    void processEnemyFire() throws Exception {

        // Criar um tiro válido, não repetido e que falha
        IGame.ShotResult result =
                new IGame.ShotResult(true, false, null, false);

        // Criar a lista de resultados
        List<IGame.ShotResult> results = List.of(result);

        // Criar uma jogada com esses resultados
        Move move = new Move(1, new ArrayList<>(), results);

        // Executar o método
        String json = move.processEnemyFire(false);

        // Converter o JSON para verificar os valores
        ObjectMapper mapper = new ObjectMapper();
        JsonNode response = mapper.readTree(json);

        // Verificar os resultados
        assertEquals(1, response.get("validShots").asInt());
        assertEquals(1, response.get("missedShots").asInt());
        assertEquals(0, response.get("repeatedShots").asInt());
        assertEquals(Game.NUMBER_SHOTS - 1,
                response.get("outsideShots").asInt());

        assertEquals(0, response.get("sunkBoats").size());
        assertEquals(0, response.get("hitsOnBoats").size());
    }

    @Test
    @DisplayName("Verificar um tiro inválido")
    void testInvalidShot() throws Exception {

        IGame.ShotResult result =
                new IGame.ShotResult(false, false, null, false);

        Move move = new Move(
                1,
                new ArrayList<>(),
                List.of(result)
        );

        String json = move.processEnemyFire(false);

        ObjectMapper mapper = new ObjectMapper();
        JsonNode response = mapper.readTree(json);

        assertEquals(0, response.get("validShots").asInt());
        assertEquals(0, response.get("repeatedShots").asInt());
        assertEquals(0, response.get("missedShots").asInt());
        assertEquals(Game.NUMBER_SHOTS,
                response.get("outsideShots").asInt());
    }

    @Test
    @DisplayName("Verificar um tiro repetido")
    void testRepeatedShot() throws Exception {

        IGame.ShotResult result =
                new IGame.ShotResult(true, true, null, false);

        Move move = new Move(
                1,
                new ArrayList<>(),
                List.of(result)
        );

        String json = move.processEnemyFire(false);

        ObjectMapper mapper = new ObjectMapper();
        JsonNode response = mapper.readTree(json);

        assertEquals(0, response.get("validShots").asInt());
        assertEquals(1, response.get("repeatedShots").asInt());
        assertEquals(0, response.get("missedShots").asInt());
        assertEquals(Game.NUMBER_SHOTS - 1,
                response.get("outsideShots").asInt());
    }

    @Test
    @DisplayName("Verificar tiro que atinge um navio sem o afundar")
    void testHitShip() throws Exception {

        // Criar um navio
        IShip ship = new Barge(
                Compass.EAST,
                new Position(0, 0)
        );

        // Criar resultado: tiro válido, não repetido,
        // atinge navio, mas não o afunda
        IGame.ShotResult result =
                new IGame.ShotResult(true, false, ship, false);

        Move move = new Move(
                1,
                new ArrayList<>(),
                List.of(result)
        );

        String json = move.processEnemyFire(false);

        ObjectMapper mapper = new ObjectMapper();
        JsonNode response = mapper.readTree(json);

        assertEquals(1, response.get("validShots").asInt());
        assertEquals(0, response.get("missedShots").asInt());

        assertEquals(1, response.get("hitsOnBoats").size());
        assertEquals(ship.getCategory(),
                response.get("hitsOnBoats").get(0).get("type").asText());
        assertEquals(1,
                response.get("hitsOnBoats").get(0).get("hits").asInt());

        assertEquals(0, response.get("sunkBoats").size());
    }

    @Test
    @DisplayName("Verificar tiro que afunda um navio")
    void testSunkShip() throws Exception {

        // Criar um navio
        IShip ship = new Barge(
                Compass.EAST,
                new Position(0, 0)
        );

        // Tiro válido que afunda o navio
        IGame.ShotResult result =
                new IGame.ShotResult(true, false, ship, true);

        Move move = new Move(
                1,
                new ArrayList<>(),
                List.of(result)
        );

        String json = move.processEnemyFire(false);

        ObjectMapper mapper = new ObjectMapper();
        JsonNode response = mapper.readTree(json);

        assertEquals(1, response.get("validShots").asInt());
        assertEquals(0, response.get("missedShots").asInt());
        assertEquals(0, response.get("repeatedShots").asInt());

        // Verificar que o navio foi registado como afundado
        assertEquals(1, response.get("sunkBoats").size());
        assertEquals(ship.getCategory(),
                response.get("sunkBoats").get(0).get("type").asText());
        assertEquals(1,
                response.get("sunkBoats").get(0).get("count").asInt());

        // Um navio afundado não aparece nos acertos
        assertEquals(0, response.get("hitsOnBoats").size());
    }

    @Test
    @DisplayName("Verificar modo verbose com três tiros na água")
    void testVerboseThreeMisses() throws Exception {

        List<IGame.ShotResult> results = List.of(
                new IGame.ShotResult(true, false, null, false),
                new IGame.ShotResult(true, false, null, false),
                new IGame.ShotResult(true, false, null, false)
        );

        Move move = new Move(
                1,
                new ArrayList<>(),
                results
        );

        // Executar com verbose ativado
        String json = move.processEnemyFire(true);

        ObjectMapper mapper = new ObjectMapper();
        JsonNode response = mapper.readTree(json);

        assertEquals(3, response.get("validShots").asInt());
        assertEquals(3, response.get("missedShots").asInt());
        assertEquals(0, response.get("repeatedShots").asInt());
        assertEquals(0, response.get("outsideShots").asInt());
    }

    @Test
    @DisplayName("Verificar modo verbose com dois tiros repetidos")
    void testVerboseRepeatedShots() throws Exception {

        List<IGame.ShotResult> results = List.of(
                new IGame.ShotResult(true, true, null, false),
                new IGame.ShotResult(true, true, null, false)
        );

        Move move = new Move(
                1,
                new ArrayList<>(),
                results
        );

        String json = move.processEnemyFire(true);

        ObjectMapper mapper = new ObjectMapper();
        JsonNode response = mapper.readTree(json);

        assertEquals(0, response.get("validShots").asInt());
        assertEquals(2, response.get("repeatedShots").asInt());
        assertEquals(0, response.get("missedShots").asInt());
        assertEquals(1, response.get("outsideShots").asInt());
    }

    @Test
    @DisplayName("Verificar modo verbose com tiro válido e repetido")
    void testVerboseValidAndRepeated() throws Exception {

        List<IGame.ShotResult> results = List.of(
                new IGame.ShotResult(true, false, null, false),
                new IGame.ShotResult(true, true, null, false)
        );

        Move move = new Move(
                1,
                new ArrayList<>(),
                results
        );

        String json = move.processEnemyFire(true);

        ObjectMapper mapper = new ObjectMapper();
        JsonNode response = mapper.readTree(json);

        assertEquals(1, response.get("validShots").asInt());
        assertEquals(1, response.get("repeatedShots").asInt());
        assertEquals(1, response.get("missedShots").asInt());
        assertEquals(1, response.get("outsideShots").asInt());
    }

    @Test
    @DisplayName("Verificar verbose com dois acertos num navio")
    void testVerboseShipHits() throws Exception {

        IShip ship = new Barge(
                Compass.EAST,
                new Position(0, 0)
        );

        List<IGame.ShotResult> results = List.of(
                new IGame.ShotResult(true, false, ship, false),
                new IGame.ShotResult(true, false, ship, false)
        );

        Move move = new Move(1, new ArrayList<>(), results);

        String json = move.processEnemyFire(true);

        JsonNode response = new ObjectMapper().readTree(json);

        assertEquals(2, response.get("validShots").asInt());
        assertEquals(0, response.get("missedShots").asInt());
        assertEquals(1, response.get("hitsOnBoats").size());
        assertEquals(2,
                response.get("hitsOnBoats").get(0).get("hits").asInt());
        assertEquals(0, response.get("sunkBoats").size());
    }

    @Test
    @DisplayName("Verificar verbose com navio afundado")
    void testVerboseSunkShip() throws Exception {

        IShip ship = new Barge(
                Compass.EAST,
                new Position(0, 0)
        );

        List<IGame.ShotResult> results = List.of(
                new IGame.ShotResult(true, false, ship, true)
        );

        Move move = new Move(1, new ArrayList<>(), results);

        String json = move.processEnemyFire(true);

        JsonNode response = new ObjectMapper().readTree(json);

        assertEquals(1, response.get("validShots").asInt());
        assertEquals(1, response.get("sunkBoats").size());
        assertEquals(1,
                response.get("sunkBoats").get(0).get("count").asInt());
        assertEquals(0, response.get("hitsOnBoats").size());
    }

    @Test
    @DisplayName("Verificar dois navios do mesmo tipo afundados")
    void testTwoSunkShips() throws Exception {

        IShip ship1 = new Barge(
                Compass.EAST,
                new Position(0, 0)
        );

        IShip ship2 = new Barge(
                Compass.EAST,
                new Position(2, 2)
        );

        List<IGame.ShotResult> results = List.of(
                new IGame.ShotResult(true, false, ship1, true),
                new IGame.ShotResult(true, false, ship2, true)
        );

        Move move = new Move(1, new ArrayList<>(), results);

        String json = move.processEnemyFire(true);

        JsonNode response = new ObjectMapper().readTree(json);

        assertEquals(2, response.get("validShots").asInt());
        assertEquals(1, response.get("sunkBoats").size());

        assertEquals("Barca",
                response.get("sunkBoats").get(0).get("type").asText());

        assertEquals(2,
                response.get("sunkBoats").get(0).get("count").asInt());

        assertEquals(0, response.get("hitsOnBoats").size());
    }

    @Test
    @DisplayName("Verificar modo verbose sem tiros registados")
    void testVerboseNoShots() throws Exception {

        Move move = new Move(
                1,
                new ArrayList<>(),
                new ArrayList<>()
        );

        String json = move.processEnemyFire(true);

        JsonNode response = new ObjectMapper().readTree(json);

        assertEquals(0, response.get("validShots").asInt());
        assertEquals(0, response.get("repeatedShots").asInt());
        assertEquals(0, response.get("missedShots").asInt());

        assertEquals(Game.NUMBER_SHOTS,
                response.get("outsideShots").asInt());

        assertEquals(0, response.get("sunkBoats").size());
        assertEquals(0, response.get("hitsOnBoats").size());
    }

    @Test
    @DisplayName("Verificar navio atingido e afundado na mesma jogada")
    void testShipHitAndSunk() throws Exception {

        IShip ship = new Barge(
                Compass.EAST,
                new Position(0, 0)
        );

        List<IGame.ShotResult> results = List.of(
                new IGame.ShotResult(true, false, ship, true),
                new IGame.ShotResult(true, false, ship, false)
        );

        Move move = new Move(1, new ArrayList<>(), results);

        String json = move.processEnemyFire(true);

        JsonNode response = new ObjectMapper().readTree(json);

        assertEquals(2, response.get("validShots").asInt());
        assertEquals(0, response.get("missedShots").asInt());

        assertEquals(1, response.get("sunkBoats").size());
        assertEquals(1,
                response.get("sunkBoats").get(0).get("count").asInt());

        assertEquals(0, response.get("hitsOnBoats").size());
    }

    @Test
    @DisplayName("Verificar modo verbose com um tiro repetido")
    void testVerboseSingleRepeatedShot() throws Exception {

        IGame.ShotResult result =
                new IGame.ShotResult(true, true, null, false);

        Move move = new Move(
                1,
                new ArrayList<>(),
                List.of(result)
        );

        String json = move.processEnemyFire(true);

        JsonNode response = new ObjectMapper().readTree(json);

        assertEquals(0, response.get("validShots").asInt());
        assertEquals(1, response.get("repeatedShots").asInt());
        assertEquals(0, response.get("missedShots").asInt());

        assertEquals(Game.NUMBER_SHOTS - 1,
                response.get("outsideShots").asInt());
    }

    @Test
    @DisplayName("Verificar verbose com um acerto num navio")
    void testVerboseSingleShipHit() throws Exception {

        IShip ship = new Barge(
                Compass.EAST,
                new Position(0, 0)
        );

        IGame.ShotResult result =
                new IGame.ShotResult(true, false, ship, false);

        Move move = new Move(
                1,
                new ArrayList<>(),
                List.of(result)
        );

        String json = move.processEnemyFire(true);

        JsonNode response = new ObjectMapper().readTree(json);

        assertEquals(1, response.get("validShots").asInt());
        assertEquals(0, response.get("missedShots").asInt());

        assertEquals(1, response.get("hitsOnBoats").size());

        assertEquals(1,
                response.get("hitsOnBoats").get(0).get("hits").asInt());

        assertEquals(ship.getCategory(),
                response.get("hitsOnBoats").get(0).get("type").asText());

        assertEquals(0, response.get("sunkBoats").size());
    }
    @Test
    @DisplayName("Verificar modo verbose com dois tiros válidos e dois repetidos")
    void testVerboseTwoValidAndTwoRepeatedShots() throws Exception {

        List<IGame.ShotResult> results = List.of(
                new IGame.ShotResult(true, false, null, false),
                new IGame.ShotResult(true, false, null, false),
                new IGame.ShotResult(true, true, null, false),
                new IGame.ShotResult(true, true, null, false)
        );

        Move move = new Move(
                1,
                new ArrayList<>(),
                results
        );

        String json = move.processEnemyFire(true);

        JsonNode response = new ObjectMapper().readTree(json);

        assertEquals(2, response.get("validShots").asInt());
        assertEquals(2, response.get("repeatedShots").asInt());
        assertEquals(2, response.get("missedShots").asInt());
        assertEquals(Game.NUMBER_SHOTS - 4,
                response.get("outsideShots").asInt());
    }





}