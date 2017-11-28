package ufl.cs1.controllers;

import game.controllers.DefenderController;
import game.models.*;
import game.system.*;
import java.math.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ListIterator;

public final class StudentController implements DefenderController
{
	Game currentGameState;
	Game previousGameState;

	public void init(Game game) { }

	public void shutdown(Game game) { }

	public int[] update(Game game,long timeDue) {
		this.currentGameState = game;
		if (this.previousGameState == null) {
			this.previousGameState = game;
		}
		
		int[] actions = new int[Game.NUM_DEFENDER];

		for (int i = 0; i < actions.length; i++) {

			actions[i] = distanceOptimizer(game, i);

			//commented out platform to implement each behavior
//			if(i ==0){
//			actions[0] = distanceOptimizer(game, i);
//			}
//			else if(i == 1){
//			actions[1] = getInFront(game, i);
//			}
//			else if(i == 2 ){
//			actions[2] = getBehind(game, i);
//			}
//			else if(i ==3){
//			actions[3] = alwaysChasing(game, i);
//			}


		}

		this.previousGameState = this.currentGameState;
		return actions;
	}


	//find the direction that will put optimize the ghosts distance to the player; defender 0
	public static int distanceOptimizer(Game gameState, int ghostNumber){
		int chosenDirection;

		double upDistance, temporaryUp;
		double downDistance, temporaryDown;
		double leftDistance, temporaryLeft;
		double rightDistance, temporaryRight;

		int ghostXValue, ghostYValue;		//ghost (x,y)
		int playerXValue, playerYValue;		//attacker (x,y)

		int convertedYplayer, convertedYGhost;

		//for defender (x,y)
		ghostXValue = gameState.getDefender(ghostNumber).getLocation().getX();
		ghostYValue = gameState.getDefender(ghostNumber).getLocation().getY();

		//for player (x,y)
		playerXValue = gameState.getAttacker().getLocation().getX();
		playerYValue = gameState.getAttacker().getLocation().getY();

		//conversion for ghost y coordinate
		convertedYGhost = yConverter(ghostYValue);

		//conversion for player y coordinate
		convertedYplayer = yConverter(playerYValue);

		//find which direction would optimize the distance to the player
		temporaryUp = convertedYGhost + 1;
		temporaryDown = convertedYGhost - 1;
		temporaryLeft = ghostXValue - 1;
		temporaryRight = ghostXValue + 1;

		upDistance = distanceCalculator(ghostXValue, temporaryUp, playerXValue, convertedYplayer);
		downDistance = distanceCalculator(ghostXValue, temporaryDown, playerXValue, convertedYplayer);
		leftDistance = distanceCalculator(temporaryLeft, convertedYGhost, playerXValue, convertedYplayer);
		rightDistance = distanceCalculator(temporaryRight, convertedYGhost, playerXValue, convertedYplayer);

		//small chain to apply the mode (going to / going away from) of the ghosts
		if(gameState.getDefender(ghostNumber).isVulnerable()) {
			chosenDirection = getMaximum(upDistance, downDistance, leftDistance, rightDistance);
		}
		else{
			chosenDirection = getMinimum(upDistance, downDistance, leftDistance, rightDistance);
		}

		return chosenDirection;
	}

	//method to place defender in front of ghost based on rectangular abstraction; defender 1
	public static int getInFront(Game gameStatues, int ghostNumber){
		int direction = -1;

		return direction;
	}

	//method to place defender behind ghost based on rectangular abstraction; defender 2
	public static int getBehind(Game gameStatus, int ghostNumber){
		int direction = -1;

		return direction;
	}

	//method to make defender always chase attacker; defender 3
	public static int alwaysChasing(Game gameStatus, int ghostNumber){
		int direction;

		double upDistance, tempUp;
		double downDistance, tempDown;
		double leftDistance, tempLeft;
		double rightDistance, tempRight;

		int ghostXValue, ghostYValue;		//ghost (x,y)
		int playerXValue, playerYValue;		//attacker (x,y)

		int convertedYplayer, convertedYGhost;

		//for defender (x,y)
		ghostXValue = gameStatus.getDefender(ghostNumber).getLocation().getX();
		ghostYValue = gameStatus.getDefender(ghostNumber).getLocation().getY();

		//for player (x,y)
		playerXValue = gameStatus.getAttacker().getLocation().getX();
		playerYValue = gameStatus.getAttacker().getLocation().getY();

		//conversion for ghost y coordinate
		convertedYGhost = yConverter(ghostYValue);

		//conversion for player y coordinate
		convertedYplayer = yConverter(playerYValue);

		//find which direction would optimize the distance to the player
		tempUp = convertedYGhost + 1;
		tempDown = convertedYGhost - 1;
		tempLeft = ghostXValue - 1;
		tempRight = ghostXValue + 1;

		upDistance = distanceCalculator(ghostXValue, tempUp, playerXValue, convertedYplayer);
		downDistance = distanceCalculator(ghostXValue, tempDown, playerXValue, convertedYplayer);
		leftDistance = distanceCalculator(tempLeft, convertedYGhost, playerXValue, convertedYplayer);
		rightDistance = distanceCalculator(tempRight, convertedYGhost, playerXValue, convertedYplayer);

		direction = getMinimum(upDistance, downDistance, leftDistance, rightDistance);

		return direction;
	}

	//Y value conversion into a normal cartesian system, accounts for inverse y-values
	public static int yConverter(int yValue){
		int intermediateVar;
		int convertedY;

		if(yValue < 58){
			intermediateVar = 58 - yValue;
			convertedY = intermediateVar + 58;
		}
		else if(yValue > 58){
			intermediateVar = yValue - 58;
			convertedY = 58 - intermediateVar;
		}
		else{
			convertedY = yValue;
		}

		return convertedY;
	}

	//use standard cartesian distance formula
	public static double distanceCalculator(double x1, double y1, double x2, double y2){
		double distance;

		distance =  Math.sqrt((Math.pow((x2 - x1), 2.0) + (Math.pow((y2 - y1), 2.0))));

		return distance;
	}

	//find the smallest of 4 distances
	public static int getMinimum(double up, double down, double left, double right){
		int finalAction = -1;

		//assign a direction based on which would reduce the distance to the attacker the most
		if((up == Math.min(up, down)) && (up == Math.min(up, left)) && (up == Math.min(up, right))){
			finalAction = 0;
		}
		else if((down == Math.min(down, up)) && (down == Math.min(down, left)) && (down == Math.min(down, right))){
			finalAction = 2;
		}
		else if((left == Math.min(left, down)) && (left == Math.min(left, up)) && (left == Math.min(left, right))){
			finalAction = 3;
		}
		else if((right == Math.min(right, up)) && (right == Math.min(right, left)) && (right == Math.min(right, down))){
			finalAction = 1;
		}

		return finalAction;
	}

	//find the largest of 4 distances
	public static int getMaximum(double up, double down, double left, double right){
		int finalAction= -1;

		//assign a direction based on which would increase the distance to the attacker the most
		if((up == Math.max(up, down)) && (up == Math.max(up, left)) && (up == Math.max(up, right))){
			finalAction = 0;
		}
		else if((down == Math.max(down, up)) && (down == Math.max(down, left)) && (down == Math.max(down, right))){
			finalAction = 2;
		}
		else if((left == Math.max(left, down)) && (left == Math.max(left, up)) && (left == Math.max(left, right))){
			finalAction = 3;
		}
		else if((right == Math.max(right, up)) && (right == Math.max(right, left)) && (right == Math.max(right, down))){
			finalAction = 1;
		}

		return finalAction;
	}






	//determine the player or the ghost is closer to a powerPill, I didnt use this for my method but you guys might be able to - blake
	public static boolean powerPillProximity(Game gameState, int ghostNumber, Maze mazeState){
		boolean proximity;

		double proximityPlayer;
		double proximityGhost;

		List<Node> powerPills;

		double closestPowerPillDistancePlayer;
		double closestPowerPillDistanceGhost;

		//for finding remaining power pills
		powerPills = mazeState.getPowerPillNodes();

		int [] powerPillsNodesPlayer = new int[powerPills.size()];
		int [] powerPillsNodesGhost = new int [powerPills.size()];

		if(powerPills.size() != 0){
			for(int i = 0; i < powerPills.size(); i++) {
				powerPillsNodesPlayer[i] = gameState.getAttacker().getLocation().getPathDistance(powerPills.get(i));
				powerPillsNodesGhost[i] = gameState.getDefender(ghostNumber).getLocation().getPathDistance(powerPills.get(i));
			}
			Arrays.sort(powerPillsNodesPlayer);
			Arrays.sort(powerPillsNodesGhost);
		}

		//shortest distances
		closestPowerPillDistancePlayer = powerPillsNodesPlayer[0];
		closestPowerPillDistanceGhost = powerPillsNodesGhost[0];

		//closest Power Pill
		proximityPlayer = closestPowerPillDistancePlayer;
		proximityGhost = closestPowerPillDistanceGhost;

		//if the player is closer to the power pill, the ghost needs to start escaping
		if(proximityPlayer == Math.min(proximityPlayer, proximityGhost)){
			proximity = true;
		}
		else{
			proximity = false;
		}

		return proximity;
	}

	/*find direction pacMan last took, this can also be done by just using the code in the method,
	i also didnt use this but it might still be helpful- blake
	 */
	public static int getPlayerDirection(Game gameState){
		int playerDirection;

		playerDirection = gameState.getAttacker().getDirection();

		return playerDirection;
	}

	//assign the ghost to be either attacking or fleeing- i didnt use this either however this may be useful to avoid repeated code later - blake
	public static int ghostMode(Game gameState, int ghostNumber){
		//for mode
		int mode;

		//determine if ghost is vulnerable
		if(gameState.getDefender(ghostNumber).isVulnerable()) {
			mode = 0;	//run away from pacman
		}
		else{
			mode = 1;	//chase pacman
		}

		return mode;
	}

}