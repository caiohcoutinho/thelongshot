package longshot.ws;

import longshot.TestRoot;
import longshot.model.*;
import longshot.model.stagetype.PlainStage;
import org.atmosphere.cpr.Broadcaster;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Naiara on 11/10/2015.
 */
public class BattleWebSocketServiceTest extends TestRoot {

    private BattleWebSocketService service;
    private BroadcastFinder broadcastFinderMock;
    private Broadcaster broadcasterMock;
    private RequestHelper requestHelperMock;
    private EntityManager entityManagerMock;

    @Before
    public void setMocks() {
        this.service = new BattleWebSocketService();
        this.broadcastFinderMock = Mockito.mock(BroadcastFinder.class);
        this.broadcasterMock = Mockito.mock(Broadcaster.class);
        Mockito.when(this.broadcastFinderMock.findAndAddBroadcaster(Mockito.any(HttpServletRequest.class), Mockito.anyLong(), Mockito.anyString())).thenReturn(this.broadcasterMock);
        this.requestHelperMock = Mockito.mock(RequestHelper.class);
        this.entityManagerMock = Mockito.mock(EntityManager.class);
        this.setField(service, "finder", this.broadcastFinderMock);
        this.setField(service, "requestHelper", this.requestHelperMock);
        this.setField(service, "em", this.entityManagerMock);
    }

    private void assertBroadcastErrorMessage(Long code, String message) {
        ArgumentCaptor<String> messageCaptor = ArgumentCaptor.forClass(String.class);
        Mockito.verify(this.broadcasterMock).broadcast(messageCaptor.capture());
        ErrorMessage errorMessage = CustomGson.GSON.fromJson(messageCaptor.getValue(), ErrorMessage.class);
        Assert.assertEquals(code, errorMessage.getCode());
        Assert.assertEquals(message, errorMessage.getMessage());
    }

    @Test
    public void testShotOutOfTurn() {
        Stage stage = new PlainStage();
        List<Player> players = new ArrayList<Player>();
        Player p1 = new Player();
        String userId = "asd";
        p1.setUserId(userId);
        p1.setId(1L);
        players.add(p1);
        stage.setPlayers(players);
        stage.setActivePlayerId(2L);

        Turn turn = new Turn();
        turn.setShot(new Shot());
        Long stageId = 1L;
        HttpServletRequest request = null;

        Mockito.when(this.requestHelperMock.getUserId(Mockito.any(HttpServletRequest.class))).thenReturn(userId);
        Mockito.when(this.entityManagerMock.find(Stage.class, 1L)).thenReturn(stage);

        this.service.broadcast(turn, stageId, request);

        this.assertBroadcastErrorMessage(BattleWebSocketService.OUT_OF_TURN, "Out of turn!");
    }

    @Test
    public void testShootPlayerAlreadyShot() {
        Stage stage = new PlainStage();
        List<Player> players = new ArrayList<Player>();
        Player p1 = new Player();
        String userId = "asd";
        p1.setUserId(userId);
        Long id = 1L;
        p1.setId(id);
        players.add(p1);
        stage.setPlayers(players);
        stage.setActivePlayerId(id);
        stage.setActivePlayerShot(true);

        Turn turn = new Turn();
        turn.setShot(new Shot());
        Long stageId = 2L;
        HttpServletRequest request = null;

        Mockito.when(this.requestHelperMock.getUserId(Mockito.any(HttpServletRequest.class))).thenReturn(userId);
        Mockito.when(this.entityManagerMock.find(Stage.class, stageId)).thenReturn(stage);

        this.service.broadcast(turn, stageId, request);

        this.assertBroadcastErrorMessage(BattleWebSocketService.SHOOT_FLAG_IS_TRUE, "Already shot!");
    }

    @Test
    public void testShootPlayerOutOfPosition() {
        Stage stage = new PlainStage();
        List<Player> players = new ArrayList<Player>();
        Player p1 = new Player();
        String userId = "asd";
        p1.setUserId(userId);
        Long id = 1L;
        p1.setId(id);
        players.add(p1);
        stage.setPlayers(players);
        stage.setActivePlayerId(id);
        stage.setActivePlayerShot(false);
        p1.setPosition(new Position(1.0, 2.0, 3.0));

        Turn turn = new Turn();
        Shot shot = new Shot();
        shot.setPosition(new Position(2.0, 3.0, 4.0));
        turn.setShot(shot);
        Long stageId = 2L;
        HttpServletRequest request = null;

        Mockito.when(this.requestHelperMock.getUserId(Mockito.any(HttpServletRequest.class))).thenReturn(userId);
        Mockito.when(this.entityManagerMock.find(Stage.class, stageId)).thenReturn(stage);

        this.service.broadcast(turn, stageId, request);

        this.assertBroadcastErrorMessage(BattleWebSocketService.PLAYER_OUT_OF_POSITION, "Player out of position!");
    }

    // TODO: This test actually tests ALCA50 behaviour. It will eventually be replaced by a mocked weapon type.
    @Test
    public void testShotDontStartFromUserPosition() {
        Stage stage = new PlainStage();
        List<Player> players = new ArrayList<Player>();
        Player p1 = new Player();
        String userId = "asd";
        p1.setUserId(userId);
        Long id = 1L;
        p1.setId(id);
        players.add(p1);
        stage.setPlayers(players);
        stage.setActivePlayerId(id);
        stage.setActivePlayerShot(false);
        Position position = new Position(1.0, 2.0, 3.0);
        p1.setPosition(position);

        Turn turn = new Turn();
        Shot shot = new Shot();
        shot.setPosition(position);
        String function = "50+t";
        shot.setFx(function);
        shot.setFy(function);
        shot.setFz(function);
        turn.setShot(shot);
        Long stageId = 2L;
        HttpServletRequest request = null;

        Mockito.when(this.requestHelperMock.getUserId(Mockito.any(HttpServletRequest.class))).thenReturn(userId);
        Mockito.when(this.entityManagerMock.find(Stage.class, stageId)).thenReturn(stage);

        this.service.broadcast(turn, stageId, request);

        this.assertBroadcastErrorMessage(BattleWebSocketService.EQUATION_EVALUATION_PROBLEM, "O tiro deve partir da posição do seu veículo (X0, Y0, Z0).");
    }

    @Test
    public void testShotEvaluationProblem() {
        Stage stage = new PlainStage();
        List<Player> players = new ArrayList<Player>();
        Player p1 = new Player();
        String userId = "asd";
        p1.setUserId(userId);
        Long id = 1L;
        p1.setId(id);
        players.add(p1);
        stage.setPlayers(players);
        stage.setActivePlayerId(id);
        stage.setActivePlayerShot(false);
        Position position = new Position(1.0, 2.0, 3.0);
        p1.setPosition(position);

        Turn turn = new Turn();
        Shot shot = new Shot();
        shot.setPosition(position);
        String function = "50+asd 231234 W#!RWFD t";
        shot.setFx(function);
        shot.setFy(function);
        shot.setFz(function);
        turn.setShot(shot);
        Long stageId = 2L;
        HttpServletRequest request = null;

        Mockito.when(this.requestHelperMock.getUserId(Mockito.any(HttpServletRequest.class))).thenReturn(userId);
        Mockito.when(this.entityManagerMock.find(Stage.class, stageId)).thenReturn(stage);

        this.service.broadcast(turn, stageId, request);

        this.assertBroadcastErrorMessage(BattleWebSocketService.EQUATION_EVALUATION_PROBLEM, "Expression is invalid.");
    }

    @Test
    public void testCorrectShot() {
        Stage stage = new PlainStage();
        List<Player> players = new ArrayList<Player>();
        Player p1 = new Player();
        p1.setPoints(0L);
        String userId = "asd";
        p1.setUserId(userId);
        Long id = 1L;
        p1.setId(id);
        players.add(p1);
        stage.setPlayers(players);
        stage.setActivePlayerId(p1.getId());
        stage.setActivePlayerShot(false);
        stage.setActivePlayerMoved(false);
        stage.setShotResults(new ArrayList<ShotResult>());
        Position position = new Position(1.0, 2.0, 3.0);
        p1.setPosition(position);

        Turn turn = new Turn();
        Shot shot = new Shot();
        shot.setPosition(position);
        shot.setFx("X0+t");
        shot.setFy("Y0+t");
        shot.setFz("Z0+t");
        turn.setShot(shot);
        Long stageId = 2L;
        HttpServletRequest request = null;

        Mockito.when(this.requestHelperMock.getUserId(Mockito.any(HttpServletRequest.class))).thenReturn(userId);
        Mockito.when(this.entityManagerMock.find(Stage.class, stageId)).thenReturn(stage);

        this.service.broadcast(turn, stageId, request);

        ArgumentCaptor<String> returnCaptor = ArgumentCaptor.forClass(String.class);
        Mockito.verify(this.broadcasterMock).broadcast(returnCaptor.capture());
        PlainStage returnedStage = CustomGson.GSON.fromJson(returnCaptor.getValue(), PlainStage.class);
        Assert.assertTrue(returnedStage.isActivePlayerShot());
    }

    @Test
    public void testShootAfterMove(){
        Stage stage = new PlainStage();
        List<Player> players = new ArrayList<Player>();
        Player p1 = new Player();
        Player p2 = new Player();
        p1.setPoints(0L);
        p2.setPoints(0L);
        p1.setUserId("asd");
        p2.setUserId("zxc");
        Long id = 1L;
        p1.setId(id);
        Long otherUserId = 2L;
        p2.setId(otherUserId);
        players.add(p1);
        players.add(p2);
        stage.setPlayers(players);
        stage.setPlayersNumber(2L);
        stage.setActivePlayerId(id);
        stage.setActivePlayerShot(false);
        stage.setActivePlayerMoved(true);
        stage.setShotResults(new ArrayList<ShotResult>());
        Position position = new Position(1.0, 2.0, 3.0);
        p1.setPosition(position);
        Position otherPosition = new Position(5.0, 6.0, 7.0);
        p2.setPosition(otherPosition);

        Turn turn = new Turn();
        Shot shot = new Shot();
        shot.setPosition(position);
        shot.setFx("X0+t");
        shot.setFy("Y0+t");
        shot.setFz("Z0+t");
        turn.setShot(shot);
        Long stageId = 2L;
        HttpServletRequest request = null;

        Mockito.when(this.requestHelperMock.getUserId(Mockito.any(HttpServletRequest.class))).thenReturn("asd");
        Mockito.when(this.entityManagerMock.find(Stage.class, stageId)).thenReturn(stage);

        this.service.broadcast(turn, stageId, request);

        ArgumentCaptor<String> returnCaptor = ArgumentCaptor.forClass(String.class);
        Mockito.verify(this.broadcasterMock).broadcast(returnCaptor.capture());

        PlainStage returnedStage = CustomGson.GSON.fromJson(returnCaptor.getAllValues().get(0), PlainStage.class);
        Assert.assertFalse(returnedStage.isActivePlayerShot());
        Assert.assertFalse(returnedStage.isActivePlayerMoved());
        Assert.assertEquals(otherUserId, stage.getActivePlayerId());
        Assert.assertEquals(BattleWebSocketService.MAX_FUEL, stage.getPlayer(p2.getUserId()).getFuel());
    }

    @Test
    public void testMoveAfterShotEndOfTurn(){
        Stage stage = new PlainStage();
        List<Player> players = new ArrayList<Player>();
        Player p1 = new Player();
        Player p2 = new Player();
        p1.setFuel(1L);
        p1.setPoints(0L);
        p2.setPoints(0L);
        p1.setUserId("asd");
        p2.setUserId("zxc");
        Long id = 1L;
        p1.setId(id);
        Long otherUserId = 2L;
        p2.setId(otherUserId);
        players.add(p1);
        players.add(p2);
        stage.setPlayers(players);
        stage.setPlayersNumber(2L);
        stage.setActivePlayerId(id);
        stage.setActivePlayerShot(true);
        stage.setActivePlayerMoved(false);
        Position position = new Position(1.0, 2.0, 3.0);
        p1.setPosition(position);
        p1.setRotation(position);
        Position otherPosition = new Position(5.0, 6.0, 7.0);
        p2.setPosition(otherPosition);

        Turn turn = new Turn();
        Move move = new Move();
        move.setPosition(position);
        move.setRotation(position);
        move.setNewPosition(otherPosition);
        move.setNewRotation(position);
        turn.setMove(move);

        Long stageId = 2L;
        HttpServletRequest request = null;

        Mockito.when(this.requestHelperMock.getUserId(Mockito.any(HttpServletRequest.class))).thenReturn("asd");
        Mockito.when(this.entityManagerMock.find(Stage.class, stageId)).thenReturn(stage);

        this.service.broadcast(turn, stageId, request);

        ArgumentCaptor<String> returnCaptor = ArgumentCaptor.forClass(String.class);
        Mockito.verify(this.broadcasterMock).broadcast(returnCaptor.capture());

        PlainStage returnedStage = CustomGson.GSON.fromJson(returnCaptor.getAllValues().get(0), PlainStage.class);
        Assert.assertFalse(returnedStage.isActivePlayerShot());
        Assert.assertFalse(returnedStage.isActivePlayerMoved());
        Assert.assertEquals(otherUserId, stage.getActivePlayerId());
        Assert.assertEquals(BattleWebSocketService.MAX_FUEL, stage.getPlayer(p2.getUserId()).getFuel());
    }

    @Test
    public void testMoveAfterShotNotEndOfTurn(){
        Stage stage = new PlainStage();
        List<Player> players = new ArrayList<Player>();
        Player p1 = new Player();
        Player p2 = new Player();
        Long fuel = 5L;
        p1.setFuel(fuel);
        p1.setPoints(0L);
        p2.setPoints(0L);
        p1.setUserId("asd");
        p2.setUserId("zxc");
        Long id = 1L;
        p1.setId(id);
        Long otherUserId = 2L;
        p2.setId(otherUserId);
        players.add(p1);
        players.add(p2);
        stage.setPlayers(players);
        stage.setPlayersNumber(2L);
        stage.setActivePlayerId(id);
        stage.setActivePlayerShot(true);
        stage.setActivePlayerMoved(false);
        Position position = new Position(1.0, 2.0, 3.0);
        p1.setPosition(position);
        p1.setRotation(position);
        Position otherPosition = new Position(5.0, 6.0, 7.0);
        p2.setPosition(otherPosition);

        Turn turn = new Turn();
        Move move = new Move();
        move.setPosition(position);
        move.setRotation(position);
        move.setNewPosition(otherPosition);
        move.setNewRotation(position);
        turn.setMove(move);

        Long stageId = 2L;
        HttpServletRequest request = null;

        Mockito.when(this.requestHelperMock.getUserId(Mockito.any(HttpServletRequest.class))).thenReturn("asd");
        Mockito.when(this.entityManagerMock.find(Stage.class, stageId)).thenReturn(stage);

        this.service.broadcast(turn, stageId, request);

        ArgumentCaptor<String> returnCaptor = ArgumentCaptor.forClass(String.class);
        Mockito.verify(this.broadcasterMock).broadcast(returnCaptor.capture());

        PlainStage returnedStage = CustomGson.GSON.fromJson(returnCaptor.getAllValues().get(0), PlainStage.class);
        Assert.assertTrue(returnedStage.isActivePlayerShot());
        Assert.assertFalse(returnedStage.isActivePlayerMoved());
        Assert.assertEquals(id, stage.getActivePlayerId());
        Assert.assertEquals(new Long(fuel - 1), stage.getPlayer(p1.getUserId()).getFuel());
    }

    @Test
    public void testMoveOutOfTurn() {
        Stage stage = new PlainStage();
        List<Player> players = new ArrayList<Player>();
        Player p1 = new Player();
        String userId = "asd";
        p1.setUserId(userId);
        p1.setId(1L);
        players.add(p1);
        stage.setPlayers(players);
        stage.setActivePlayerId(2L);

        Turn turn = new Turn();
        turn.setMove(new Move());
        Long stageId = 1L;
        HttpServletRequest request = null;

        Mockito.when(this.requestHelperMock.getUserId(Mockito.any(HttpServletRequest.class))).thenReturn(userId);
        Mockito.when(this.entityManagerMock.find(Stage.class, 1L)).thenReturn(stage);

        this.service.broadcast(turn, stageId, request);

        this.assertBroadcastErrorMessage(BattleWebSocketService.OUT_OF_TURN, "Out of turn!");
    }

    @Test
    public void testMoveButFlagIsTrue() {
        Stage stage = new PlainStage();
        List<Player> players = new ArrayList<Player>();
        Player p1 = new Player();
        String userId = "asd";
        p1.setUserId(userId);
        Long id = 1L;
        p1.setId(id);
        players.add(p1);
        stage.setPlayers(players);
        stage.setActivePlayerMoved(true);
        stage.setActivePlayerId(id);

        Turn turn = new Turn();
        turn.setMove(new Move());
        Long stageId = 2L;
        HttpServletRequest request = null;

        Mockito.when(this.requestHelperMock.getUserId(Mockito.any(HttpServletRequest.class))).thenReturn(userId);
        Mockito.when(this.entityManagerMock.find(Stage.class, stageId)).thenReturn(stage);

        this.service.broadcast(turn, stageId, request);

        this.assertBroadcastErrorMessage(BattleWebSocketService.MOVE_FLAG_IS_TRUE, "Player already moved this turn!");
    }

    @Test
    public void testMoveButFuelIsNull() {
        Stage stage = new PlainStage();
        List<Player> players = new ArrayList<Player>();
        Player p1 = new Player();
        String userId = "asd";
        p1.setUserId(userId);
        Long id = 1L;
        p1.setId(id);
        p1.setFuel(null);
        players.add(p1);
        stage.setPlayers(players);
        stage.setActivePlayerMoved(false);
        stage.setActivePlayerId(id);

        Turn turn = new Turn();
        turn.setMove(new Move());
        Long stageId = 2L;
        HttpServletRequest request = null;

        Mockito.when(this.requestHelperMock.getUserId(Mockito.any(HttpServletRequest.class))).thenReturn(userId);
        Mockito.when(this.entityManagerMock.find(Stage.class, stageId)).thenReturn(stage);

        this.service.broadcast(turn, stageId, request);

        this.assertBroadcastErrorMessage(BattleWebSocketService.FUEL_IS_NULL, "Player fuel is null!");
    }

    @Test
    public void testMoveButFuelIsEmpty() {
        Stage stage = new PlainStage();
        List<Player> players = new ArrayList<Player>();
        Player p1 = new Player();
        String userId = "asd";
        p1.setUserId(userId);
        Long id = 1L;
        p1.setId(id);
        p1.setFuel(0L);
        players.add(p1);
        stage.setPlayers(players);
        stage.setActivePlayerMoved(false);
        stage.setActivePlayerId(id);

        Turn turn = new Turn();
        turn.setMove(new Move());
        Long stageId = 2L;
        HttpServletRequest request = null;

        Mockito.when(this.requestHelperMock.getUserId(Mockito.any(HttpServletRequest.class))).thenReturn(userId);
        Mockito.when(this.entityManagerMock.find(Stage.class, stageId)).thenReturn(stage);

        this.service.broadcast(turn, stageId, request);

        this.assertBroadcastErrorMessage(BattleWebSocketService.FUEL_IS_EMPTY, "Player fuel is empty!");
    }

    @Test
    public void testMoveButPlayerIsOutOfPosition() {
        Stage stage = new PlainStage();
        List<Player> players = new ArrayList<Player>();
        Player p1 = new Player();
        String userId = "asd";
        p1.setUserId(userId);
        Long id = 1L;
        p1.setId(id);
        p1.setFuel(5L);
        p1.setPosition(new Position(1.0, 2.0, 3.0));
        players.add(p1);
        stage.setPlayers(players);
        stage.setActivePlayerMoved(false);
        stage.setActivePlayerId(id);

        Turn turn = new Turn();
        Move move = new Move();
        move.setPosition(new Position(1.5, 2.5, 3.5));
        turn.setMove(move);
        Long stageId = 2L;
        HttpServletRequest request = null;

        Mockito.when(this.requestHelperMock.getUserId(Mockito.any(HttpServletRequest.class))).thenReturn(userId);
        Mockito.when(this.entityManagerMock.find(Stage.class, stageId)).thenReturn(stage);

        this.service.broadcast(turn, stageId, request);

        this.assertBroadcastErrorMessage(BattleWebSocketService.PLAYER_OUT_OF_POSITION, "Player out of position!");
    }

    @Test
    public void testMoveButPlayerIsOutOfRotation() {
        Stage stage = new PlainStage();
        List<Player> players = new ArrayList<Player>();
        Player p1 = new Player();
        String userId = "asd";
        p1.setUserId(userId);
        Long id = 1L;
        p1.setId(id);
        p1.setFuel(5L);
        Position position = new Position(1.0, 2.0, 3.0);
        p1.setPosition(position);
        p1.setRotation(new Position(1.0, 2.0, 3.0));
        players.add(p1);
        stage.setPlayers(players);
        stage.setActivePlayerMoved(false);
        stage.setActivePlayerId(id);

        Turn turn = new Turn();
        Move move = new Move();
        move.setPosition(position);
        move.setRotation(new Position(1.5, 2.5, 3.5));
        turn.setMove(move);
        Long stageId = 2L;
        HttpServletRequest request = null;

        Mockito.when(this.requestHelperMock.getUserId(Mockito.any(HttpServletRequest.class))).thenReturn(userId);
        Mockito.when(this.entityManagerMock.find(Stage.class, stageId)).thenReturn(stage);

        this.service.broadcast(turn, stageId, request);

        this.assertBroadcastErrorMessage(BattleWebSocketService.PLAYER_OUT_OF_ROTATION, "Player out of rotation!");
    }

    @Test
    public void testStageDoNotValidateMovement() {
        Stage stage = Mockito.mock(PlainStage.class);
        List<Player> players = new ArrayList<Player>();
        Player p1 = new Player();
        String userId = "asd";
        p1.setUserId(userId);
        Long id = 1L;
        p1.setId(id);
        p1.setFuel(5L);
        Position position = new Position(1.0, 2.0, 3.0);
        p1.setPosition(position);
        p1.setRotation(position);
        players.add(p1);
        Mockito.when(stage.getPlayers()).thenReturn(players);
        Mockito.when(stage.isActivePlayerMoved()).thenReturn(false);
        Mockito.when(stage.getActivePlayerId()).thenReturn(id);
        Mockito.when(stage.getPlayer(userId)).thenReturn(p1);

        Turn turn = new Turn();
        Move move = new Move();
        move.setPosition(position);
        move.setRotation(position);
        turn.setMove(move);
        Long stageId = 2L;
        HttpServletRequest request = null;

        Mockito.when(this.requestHelperMock.getUserId(Mockito.any(HttpServletRequest.class))).thenReturn(userId);
        Mockito.when(this.entityManagerMock.find(Stage.class, stageId)).thenReturn(stage);
        Mockito.when(stage.checkMovement(p1, turn.getMove())).thenReturn(false);

        this.service.broadcast(turn, stageId, request);

        this.assertBroadcastErrorMessage(BattleWebSocketService.MOVEMENT_INVALID_ON_STAGE, "Movement is invalid on this stage!");
    }

    @Test
    public void testCorrectMovement() {
        Stage stage = new PlainStage();
        List<Player> players = new ArrayList<Player>();
        Player p1 = new Player();
        String userId = "asd";
        p1.setUserId(userId);
        Long id = 1L;
        p1.setId(id);
        Long fuel = 5L;
        p1.setFuel(fuel);
        Position position = new Position(1.0, 2.0, 3.0);
        p1.setPosition(position);
        p1.setRotation(position);
        players.add(p1);
        stage.setPlayers(players);
        stage.setActivePlayerMoved(false);
        stage.setActivePlayerId(id);

        Turn turn = new Turn();
        Move move = new Move();
        move.setPosition(position);
        move.setRotation(position);
        Position newPosition = new Position(2.0, 3.0, 4.0);
        move.setNewPosition(newPosition);
        move.setNewRotation(newPosition);
        turn.setMove(move);
        Long stageId = 2L;
        HttpServletRequest request = null;

        Mockito.when(this.requestHelperMock.getUserId(Mockito.any(HttpServletRequest.class))).thenReturn(userId);
        Mockito.when(this.entityManagerMock.find(Stage.class, stageId)).thenReturn(stage);

        this.service.broadcast(turn, stageId, request);

        ArgumentCaptor<Object> entityCaptor = ArgumentCaptor.forClass(Object.class);
        Mockito.verify(this.entityManagerMock, Mockito.times(2)).merge(entityCaptor.capture());

        Object mergedObject = entityCaptor.getAllValues().get(0);
        Assert.assertTrue(mergedObject instanceof Player);
        Player mergedPlayer = (Player) mergedObject;
        Assert.assertEquals(newPosition, mergedPlayer.getPosition());
        Assert.assertEquals(newPosition, mergedPlayer.getRotation());
        Assert.assertEquals(new Long(fuel - 1L), mergedPlayer.getFuel());

        Object mergedObject2 = entityCaptor.getAllValues().get(1);
        Assert.assertTrue(mergedObject2 instanceof Stage);
        Stage mergedStage = (Stage) mergedObject2;
        Assert.assertEquals(false, mergedStage.isActivePlayerMoved());

        ArgumentCaptor<String> messageCaptor = ArgumentCaptor.forClass(String.class);
        Mockito.verify(this.broadcasterMock).broadcast(messageCaptor.capture());
        PlainStage resultTurn = CustomGson.GSON.fromJson(messageCaptor.getValue(), PlainStage.class);
        Assert.assertEquals(p1.getId(), resultTurn.getActivePlayerId());

    }
}
