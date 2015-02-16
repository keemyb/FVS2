package fvs.taxe.dialog;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import fvs.taxe.MainMenuScreen;
import fvs.taxe.TaxeGame;
import gameLogic.Player;
import gameLogic.PlayerManager;
import gameLogic.goal.Goal;

public class DialogEndGame extends Dialog{
	private TaxeGame game;
	
	public DialogEndGame(TaxeGame game, PlayerManager pm, Skin skin) {
		super("GAME OVER", skin);
		this.game = game;
		
		int highscore = 0;
		int playernum = 0;
		int player1Score = 0;
		int player2Score = 0;
		for(Player player : pm.getAllPlayers()) {
			int goalsComplete = 0;
			for(Goal goal : player.getGoals()) {
				if(goal.getComplete()){
					goalsComplete++;
				}
			}
			
			text("Player " + player.getPlayerNumber() + " scored " + player.getScore() + " points.");
			getContentTable().row();

			if(player.getScore() > highscore) {
				highscore = goalsComplete;
				playernum = player.getPlayerNumber();
			}

			if (playernum == 1){
				player1Score =player.getScore();
			}

			if (playernum == 2){
				player2Score =player.getScore();
			}

			else {
				playernum = 0;
			}
		}
		if(player1Score > player2Score) {
			text("PLAYER 1 WINS!");
			
		} else if (player2Score > player1Score) {
			text("PLAYER 2 WINS!");

		} else {
			text("TIE GAME");
		}
		
		//button("Main Menu","MENU");
		button("Exit","EXIT");
	}
	
	@Override
	public Dialog show(Stage stage) {
		show(stage, null);
		setPosition(Math.round((stage.getWidth() - getWidth()) / 2), Math.round((stage.getHeight() - getHeight()) / 2));
		return this;
	}
	
	@Override
	public void hide() {
		hide(null);
	}
	
	@Override
	protected void result(Object obj) {
		if(obj == "EXIT"){
			Gdx.app.exit();
		} else {
			game.setScreen(new MainMenuScreen(game));
		}
	}
}
