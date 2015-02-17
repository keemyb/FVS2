package fvs.taxe;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

import fvs.taxe.actor.CollisionStationActor;
import fvs.taxe.controller.*;
import fvs.taxe.dialog.DialogEndGame;
import gameLogic.Game;
import gameLogic.GameState;
import gameLogic.GameStateListener;
import gameLogic.TurnListener;
import gameLogic.map.CollisionStation;
import gameLogic.map.Map;
import gameLogic.map.Station;
import gameLogic.map.Connection;

import java.util.Random;


public class GameScreen extends ScreenAdapter {
    /* Using a random instance so we don't have keep instantiating new
    ones every time we need a random number.
    */
    private static Random random = new Random();

    final private TaxeGame game;
    private Stage stage;
    private Texture mapTexture;
    private Game gameLogic;
    private Skin skin;
    private Map map;
    private float timeAnimated = 0;
    public static final int ANIMATION_TIME = 2;
    private Tooltip tooltip;
    private Context context;
    private int currentTurn;

    private Station failedJunction;
    private Connection failedConnection;
    private int lastJunctionBreakOrFix;
    private int lastConnectionBreakOrFix;
    private int brokenConnections = 0;

    private StationController stationController;
    private TopBarController topBarController;
    private ResourceController resourceController;
    private GoalController goalController;
    private RouteController routeController;

    // The probability a CollisionStation will be broken/fixed.
    private static final float JUNCTION_BREAK_PROBABILITY = 0.4f;
    private static final float JUNCTION_FIX_PROBABILITY = 0.5f;
    /* A junction will only be toggled broken after this many turns.
    This is so that players are overwhelmed in decision making
    because things are changing so rapidly.
     */
    private static final int JUNCTION_BREAK_OR_FIX_EVERY_X_TURNS = 2;

    // As above
    private static final float CONNECTION_BREAK_PROBABILITY = 0.7f;
    private static final int CONNECTION_BREAK_OR_FIX_EVERY_TURN = 2;
    private static final int MAX_BROKEN_CONNECTIONS = 1;

    public GameScreen(TaxeGame game) {
        this.game = game;
        stage = new Stage();
        skin = new Skin(Gdx.files.internal("data/uiskin.json"));

        gameLogic = Game.getInstance();
        context = new Context(stage, skin, game, gameLogic);
        Gdx.input.setInputProcessor(stage);

        mapTexture = new Texture(Gdx.files.internal("gamemap.png"));
        map = gameLogic.getMap();

        tooltip = new Tooltip(skin);
        stage.addActor(tooltip);

        stationController = new StationController(context, tooltip);
        topBarController = new TopBarController(context);
        resourceController = new ResourceController(context);
        goalController = new GoalController(context);
        routeController = new RouteController(context);

        context.setRouteController(routeController);
        context.setTopBarController(topBarController);

        lastConnectionBreakOrFix = gameLogic.TOTAL_TURNS;
        lastJunctionBreakOrFix = gameLogic.TOTAL_TURNS;


        // Every time the turn is changed, break or fix a CollisionStation/connection if appropriate.
        gameLogic.getPlayerManager().subscribeTurnChanged(new TurnListener() {
            @Override
            public void changed() {
                gameLogic.setState(GameState.ANIMATING);
                topBarController.displayFlashMessage("Time is passing...", Color.BLACK);
                if (lastJunctionBreakOrFix > JUNCTION_BREAK_OR_FIX_EVERY_X_TURNS){
                    breakJunction();
                }
                lastJunctionBreakOrFix++;
                    
                if (lastConnectionBreakOrFix > CONNECTION_BREAK_OR_FIX_EVERY_TURN) {
                	if (brokenConnections == MAX_BROKEN_CONNECTIONS) {
                		fixConnection(failedConnection);
                		return;
                	}
                	
                	Map map = Game.getInstance().getMap();
                    breakConnection(map.getRandomConnection());
                }
                
                lastConnectionBreakOrFix++;

                for (TrainMoveController controller : TrainMoveController.controllers) {
                    controller.refreshMoveActions();
                }
            }
        });
        gameLogic.subscribeStateChanged(new GameStateListener() {
        	@Override
        	public void changed(GameState state){
        		if(gameLogic.getPlayerManager().getTurnNumber() == gameLogic.TOTAL_TURNS && state == GameState.NORMAL) {
        			DialogEndGame dia = new DialogEndGame(GameScreen.this.game, gameLogic.getPlayerManager(), skin);
        			dia.show(stage);
        		}
        	}
        });
    }


    /**
     * Breaks/Fixes a Junction depending on probability. Called every turn.
     * Not that the fixJunction method is only called via this method,
     * if a junction is already broken.
     */
    private void breakJunction() {
        if (failedJunction != null){
            fixJunction();
            return;
        }

        if (random.nextFloat() > JUNCTION_BREAK_PROBABILITY) return;

        Map map = Game.getInstance().getMap();
        failedJunction = map.getRandomStation();
        while (!(failedJunction instanceof CollisionStation)) {
            failedJunction = map.getRandomStation();
        }

        lastJunctionBreakOrFix = 0;
        ((CollisionStation) failedJunction).setBroken(true);
        context.getTopBarController().displayFlashMessage("The junction " + failedJunction.getName() + " is broken!", Color.RED);

        // change the Junction image to indicate it's current state.
        updateFailedJunctionImage();
    }

    private void fixJunction() {
        if (random.nextFloat() > JUNCTION_FIX_PROBABILITY) return;

        lastJunctionBreakOrFix = 0;
        ((CollisionStation) failedJunction).setBroken(false);
        context.getTopBarController().displayFlashMessage("The junction " + failedJunction.getName() + " is fixed!" , Color.BLUE);

        // change the Junction image to indicate it's current state.
        updateFailedJunctionImage();
        failedJunction = null;
    }

    /**
     * Breaks/Fixes a connection depending on probability. Called every turn.
     * Not that the fixConnection method is only called via this method,
     * if a connection is already broken.
     */
    private void breakConnection(Connection connection) {
        if (connection.isBroken()) {
    		fixConnection(connection);
    		return;	
    	}
    	
    	if (random.nextFloat() > CONNECTION_BREAK_PROBABILITY) return;
    	
    	connection.setBroken(true);
    	failedConnection = connection;
    	lastConnectionBreakOrFix = 0;
    	brokenConnections += 1;
    	context.getTopBarController().displayFlashMessage("The track between " + connection.getStation1().getName() + " and " + connection.getStation2().getName() + " is broken!" , Color.RED);
    }
    
    private void fixConnection(Connection connection) {
    	connection.setBroken(false);
    	lastConnectionBreakOrFix = 0;
    	brokenConnections -= 1;
    	context.getTopBarController().displayFlashMessage("The track between " + connection.getStation1().getName() + " and " + connection.getStation2().getName() + " has been fixed!" , Color.BLUE);
    }

    /**
     * Updating the image displayed so that it reflects it's un/broken state.
     */
    private void updateFailedJunctionImage() {
        for (Actor actor : context.getStage().getActors()) {
            if (actor instanceof CollisionStationActor) {
                if (((CollisionStationActor) actor).getCollisionStation() == failedJunction) {
                    ((CollisionStationActor) actor).updateImage();
                }
            }
        }
    }

    // called every frame
    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        game.batch.begin();
        game.batch.draw(mapTexture, 0, 0);
        game.batch.end();

        topBarController.drawBackground();

        stationController.renderConnections(map.getConnections(), Color.GRAY);

        if(gameLogic.getState() == GameState.ROUTING) {
            routeController.drawRoute(Color.BLACK);
        }

        if(gameLogic.getState() == GameState.ANIMATING) {
            timeAnimated += delta;
            if (timeAnimated >= ANIMATION_TIME) {
                gameLogic.setState(GameState.NORMAL);
                timeAnimated = 0;
            }
        }
        
        if(gameLogic.getState() == GameState.NORMAL || gameLogic.getState() == GameState.PLACING){
        	stationController.displayNumberOfTrainsAtStations();
        }

        stage.act(Gdx.graphics.getDeltaTime());
        stage.draw();
        
        game.batch.begin();

        if (gameLogic.getPlayerManager().getTurnNumber() < 30){
            currentTurn = gameLogic.getPlayerManager().getTurnNumber() + 1;
        } else {
            currentTurn = 30;
        }
        game.fontSmall.draw(game.batch, "Turn " + (currentTurn) + "/" + gameLogic.TOTAL_TURNS, (float) TaxeGame.WIDTH - 90.0f, 20.0f);
        float y = ((float) TaxeGame.HEIGHT)- 10.0f - TopBarController.CONTROLS_HEIGHT;
        game.fontSmall.draw(game.batch, "Scores", (float) TaxeGame.WIDTH - 140.0f, y);
        game.fontSmall.draw(game.batch, "Player 1: " + gameLogic.getPlayerManager().getAllPlayers().get(0).getScore(), (float) TaxeGame.WIDTH - 140.0f, y-25.0f);
        game.fontSmall.draw(game.batch, "Player 2: " + gameLogic.getPlayerManager().getAllPlayers().get(1).getScore(), (float) TaxeGame.WIDTH - 140.0f, y-50.0f);
        game.batch.end();

        resourceController.drawHeaderText();
        goalController.showCurrentPlayerHeader();
        if(gameLogic.getUpdateGoalsOnScreen()) { //only redraw the goals if they have changed (turn or goal complete
        	goalController.showCurrentPlayerGoals(); // allows us to add listeners to the buttons for the goals
        	gameLogic.setUpdateGoalsOnScreen(false);
        }
    }

    @Override
    // Called when GameScreen becomes current screen of the game
    public void show() {
        stationController.renderStations();
        topBarController.addEndTurnButton();
        resourceController.drawPlayerResources(gameLogic.getPlayerManager().getCurrentPlayer());
    }


    @Override
    public void dispose() {
        mapTexture.dispose();
        stage.dispose();
    }

}