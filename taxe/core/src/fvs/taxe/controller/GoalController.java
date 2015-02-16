package fvs.taxe.controller;

import fvs.taxe.TaxeGame;
import fvs.taxe.Tooltip;
import gameLogic.Player;
import gameLogic.PlayerManager;
import gameLogic.goal.Goal;
import gameLogic.map.Station;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

public class GoalController {
	private Context context;
	private Group goalButtons = new Group();

	private Tooltip originTip;
	private Tooltip destTip;
	private Tooltip viaTip;

	public GoalController(Context context) {
		this.context = context;
		originTip = new Tooltip(context.getSkin());
		destTip = new Tooltip(context.getSkin());
		viaTip = new Tooltip(context.getSkin());
		context.getStage().addActor(originTip);
		context.getStage().addActor(destTip);
		context.getStage().addActor(viaTip);
	}

	private List<Goal> playerGoals() {
		ArrayList<Goal> goals = new ArrayList<Goal>();
		PlayerManager pm = context.getGameLogic().getPlayerManager();
		Player currentPlayer = pm.getCurrentPlayer();

		for (Goal goal : currentPlayer.getGoals()) {
			if (goal.getComplete()) {
				continue;
			}

			goals.add(goal);
		}

		return goals;
	}

	public void showCurrentPlayerHeader() {
		TaxeGame game = context.getTaxeGame();

		float top = (float) TaxeGame.HEIGHT;
		float x = 10.0f;
		float y = top - 10.0f - TopBarController.CONTROLS_HEIGHT;

		game.batch.begin();
		game.fontSmall.setColor(Color.BLACK);
		game.fontSmall.draw(game.batch, playerGoalHeader(), x, y);
		game.batch.end();
	}

	public void showCurrentPlayerGoals() {

		goalButtons.remove();
		goalButtons.clear();

		float top = (float) TaxeGame.HEIGHT;
		float x = 10.0f;
		float y = top - 10.0f - TopBarController.CONTROLS_HEIGHT;

		y -= 15;

		for (Goal goal : playerGoals()) {
			y -= 30;

			TextButton button = new TextButton(goal.toString(),
					context.getSkin());
			button.setPosition(x, y);

			final Station origin = goal.getOrigin();
			final Station dest = goal.getDestination();
			final Station via = goal.getVia();

			button.addListener(new ClickListener() {

				@Override
				public void enter(InputEvent event, float x, float y,
						int pointer, Actor fromActor) {
					originTip.setPosition(origin.getLocation().getX()+10, origin
							.getLocation().getY()+10);
					originTip.show(origin.getName());
					
					destTip.setPosition(dest.getLocation().getX()+10, dest.getLocation()
							.getY()+10);
					destTip.show(dest.getName());
					
					if (via != null) {
						viaTip.setPosition(via.getLocation().getX()+10, via
								.getLocation().getY()+10);
						viaTip.show(via.getName());
					}
					
				}

				@Override
				public void exit(InputEvent event, float x, float y,
						int pointer, Actor toActor) { //return to normal
					originTip.hide();
					destTip.hide();
					viaTip.hide();
				}
			});
			goalButtons.addActor(button);
		}

		context.getStage().addActor(goalButtons);
	}

	private String playerGoalHeader() {
		return "Player "
				+ context.getGameLogic().getPlayerManager().getCurrentPlayer()
						.getPlayerNumber() + " Goals:";
	}
}
