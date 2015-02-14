package fvs.taxe.controller;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.RunnableAction;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;

import fvs.taxe.actor.TrainActor;
import gameLogic.Game;
import gameLogic.Player;
import gameLogic.map.CollisionStation;
import gameLogic.map.IPositionable;
import gameLogic.map.Position;
import gameLogic.map.Station;
import gameLogic.resource.Resource;
import gameLogic.resource.Train;

import java.util.ArrayList;
import java.util.List;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.moveTo;

public class TrainMoveController {
    private Context context;
    private Train train;
    public static List<TrainMoveController> controllers = new ArrayList<TrainMoveController>();

    public TrainMoveController(Context context, Train train) {
        this.context = context;
        this.train = train;

        refreshMoveActions();
        controllers.add(this);
    }

    // an action for the train to run before it starts moving across the screen
    private RunnableAction beforeAction() {
        return new RunnableAction() {
            public void run() {
                train.getActor().setVisible(true);
                train.setPosition(new Position(-1, -1));
            }
        };
    }

    // this action will run every time the train reaches a station within a route
    private RunnableAction perStationAction(final Station station) {
        return new RunnableAction() {
            public void run() {
                train.getRoute().remove(0);
                train.addHistory(station.getName(), context.getGameLogic().getPlayerManager().getTurnNumber());
                System.out.println("Added to history: passed " + station.getName() + " on turn "
                        + context.getGameLogic().getPlayerManager().getTurnNumber());
                // train.setPosition(station.getLocation());

                collisions(station);
            }
        };
    }

    // an action for the train to run after it has moved the whole route
    private RunnableAction afterAction() {
        return new RunnableAction() {
            public void run() {
                ArrayList<String> completedGoals = context.getGameLogic().getGoalManager().trainArrived(train, train.getPlayer());
                for(String message : completedGoals) {
                    context.getTopBarController().displayFlashMessage(message, Color.WHITE, 2);
                }
                System.out.println(train.getFinalDestination().getLocation().getX() + "," + train.getFinalDestination().getLocation().getY());
                train.setPosition(train.getFinalDestination().getLocation());
                train.getActor().setVisible(false);
                train.setFinalDestination(null);
                Game.getInstance().setUpdateGoalsOnScreen(true);
            }
        };
    }

    /**
     * This method is called every turn to ensure that a train doesn't have invalid move actions.
     * The previous move actions are cleared and regenerated, taking into account broken CollisionStations
     * and Connections (so a train does not move past something that is broken).
     */
    public void refreshMoveActions() {
        // If a route is empty there are no moves to refresh.
        if (train.getRoute().isEmpty()) return;

        SequenceAction actionSequence = Actions.sequence();
        IPositionable current = train.getPosition();
        /* If the train is not at a station, it is not given an intermediate position,
        but is assigned -1,-1 for some reason. This sets the location of the train to
        the position of it's image (actor) in this case, so that we can properly calculate
        the speed it should travel when it is moved from a point other than a station.
        (As the speed is calculated based of the delta of it's current and target/next position)
         */

        if (current.getX() == -1){
            current.setX((int) train.getActor().getX());
            current.setY((int) train.getActor().getY());
        }

        int routeLength = 0;
        actionSequence.addAction(beforeAction());

        /* We are iterating through the stations in the trains route.
        We will prune all stations in the route if it is inaccessible
        (so if it is broken, or there is no connection to it from the previous station in the route).
         */
        for (int i = 0; i < train.getRoute().size(); i++) {
            Station station = train.getRoute().get(i);

            // Break if the station is broken (as not to increase the route length).
            if (station instanceof CollisionStation){
                if (((CollisionStation) station).isBroken()) {
                    break;
                }
            }

            // Break if there is no connection to the station (as not to increase the route length).
            if (i < train.getRoute().size()-1){
                Station nextStation = train.getRoute().get(i+1);

                if (!context.getGameLogic().getMap().doesConnectionExist(station.getName(), nextStation.getName())){
                    break;
                }
            }

            // If we have got here then we can reach that station, and increase our route length
            routeLength++;
        }

        // Based on the routeLength calculated prior, we are trimming the route down to size.
        while (train.getRoute().size() - 1 > routeLength) {
            // Removing the last station in the route until we are at the right size.
            train.getRoute().remove(train.getRoute().size() -1);
        }

        // Populating the actionSequence with move actions for stations in the new pruned route.
        for (final Station station : train.getRoute()) {
            IPositionable next = station.getLocation();
            float duration = getDistance(current, next) / train.getSpeed();
            actionSequence.addAction(moveTo(next.getX() - TrainActor.width / 2, next.getY() - TrainActor.height / 2, duration));
            actionSequence.addAction(perStationAction(station));
            current = next;
        }
        actionSequence.addAction(afterAction());

        train.setFinalDestination(train.getRoute().get(train.getRoute().size()-1));

        // remove previous actions to be cautious
        train.getActor().clearActions();
        train.getActor().addAction(actionSequence);
    }

    private float getDistance(IPositionable a, IPositionable b) {
        return Vector2.dst(a.getX(), a.getY(), b.getX(), b.getY());
    }

    private void collisions(Station station) {
        //test for train collisions at Junction point
        if(!(station instanceof CollisionStation)) {
            return;
        }

        List<Train> trainsToDestroy = trainsToDestroy();

        if(trainsToDestroy.size() > 0) {
            for(Train trainToDestroy : trainsToDestroy) {
                trainToDestroy.getActor().remove();
                trainToDestroy.getPlayer().removeResource(trainToDestroy);
            }

            context.getTopBarController().displayFlashMessage("Two trains collided at a Junction.  They were both destroyed.", Color.RED, 2);
        }
    }

    private List<Train> trainsToDestroy() {
        List<Train> trainsToDestroy = new ArrayList<Train>();

        for(Player player : context.getGameLogic().getPlayerManager().getAllPlayers()) {
            for(Resource resource : player.getResources()) {
                if(resource instanceof Train) {
                    Train otherTrain = (Train) resource;
                    if(otherTrain.getActor() == null) continue;
                    if(otherTrain == train) continue;

                    if(train.getActor().getBounds().overlaps(otherTrain.getActor().getBounds())) {
                        //destroy trains that have crashed and burned
                        trainsToDestroy.add(train);
                        trainsToDestroy.add(otherTrain);
                    }

                }
            }
        }

        return trainsToDestroy;
    }
}
