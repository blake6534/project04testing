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

			//assign unique behaviors to defenders
			if(i ==0){
			actions[0] = carefulChaser(game, i);
			}
			else if(i == 1){
			actions[1] = getInFront(game, i);
			}
			else if(i == 2 ){
			actions[2] = getBehind(game, i);
			}
			else if(i ==3){
			actions[3] = alwaysChasing(game, i);
			}
		}

		this.previousGameState = this.currentGameState;
		return actions;
	}

	//find the direction that will optimize the ghosts distance to the player; defender 0
	public static int carefulChaser(Game gameState, int ghostNumber){
		int chosenDirection;

		double upDistance, temporaryUp;
		double downDistance, temporaryDown;
		double leftDistance, temporaryLeft;
		double rightDistance, temporaryRight;

		int ghostXValue, ghostYValue;		//ghost (x,y)
		int playerXValue, playerYValue;		//attacker (x,y)

		//for defender (x,y)
		ghostXValue = gameState.getDefender(ghostNumber).getLocation().getX();
		ghostYValue = gameState.getDefender(ghostNumber).getLocation().getY();

		//for player (x,y)
		playerXValue = gameState.getAttacker().getLocation().getX();
		playerYValue = gameState.getAttacker().getLocation().getY();

		//conversion for ghost y coordinate
		ghostYValue = yConverter(ghostYValue);

		//conversion for player y coordinate
		playerYValue = yConverter(playerYValue);

		//find which direction would optimize the distance to the player
		temporaryUp = ghostYValue + 1;
		temporaryDown = ghostYValue - 1;
		temporaryLeft = ghostXValue - 1;
		temporaryRight = ghostXValue + 1;

		upDistance = distanceCalculator(ghostXValue, temporaryUp, playerXValue, playerYValue);
		downDistance = distanceCalculator(ghostXValue, temporaryDown, playerXValue, playerYValue);
		leftDistance = distanceCalculator(temporaryLeft, ghostYValue, playerXValue, playerYValue);
		rightDistance = distanceCalculator(temporaryRight, ghostYValue, playerXValue, playerYValue);

		//small chain to apply the mode (going to / going away from) of the ghosts
		if(gameState.getDefender(ghostNumber).isVulnerable()) {
			chosenDirection = getMaximum(upDistance, downDistance, leftDistance, rightDistance);
		}
		else{
			chosenDirection = getMinimum(upDistance, downDistance, leftDistance, rightDistance);
		}

		return chosenDirection;
	}

	//find the direction to place defender in front of ghost; defender 1
	public static int getInFront(Game gameStatus, int ghostNumber){
		int direction;

		int defenderX, defenderY;
		int playerX, playerY;
		int targetX, targetY;

		int playerDirection;

		double upDistance, temporaryUp;
		double downDistance, temporaryDown;
		double leftDistance, temporaryLeft;
		double rightDistance, temporaryRight;

		//defender (x,y)
		defenderX = gameStatus.getDefender(ghostNumber).getLocation().getX();
		defenderY = gameStatus.getDefender(ghostNumber).getLocation().getY();

		//player (x,y)
		playerX = gameStatus.getAttacker().getLocation().getX();
		playerY = gameStatus.getAttacker().getLocation().getY();

		//convert Y to actual
		defenderY = yConverter(defenderY);
		playerY = yConverter(playerY);

		//find player direction
		playerDirection = gameStatus.getAttacker().getDirection();

		//optimal targeting point based on where the player is facing
		if(playerDirection == 0){
			targetX = playerX;
			targetY = playerY + 4;
		}
		else if(playerDirection == 1){
			targetX = playerX + 4;
			targetY = playerY;

		}
		else if(playerDirection == 2){
			targetX = playerX;
			targetY = playerY - 4;

		}
		else if (playerDirection == 3){
			targetX = playerX - 4;
			targetY = playerY;
		}
		else{
			targetX = playerX;
			targetY = playerY;
		}

		//find direction to optimize distance to target direction in front of player
		temporaryUp = defenderY + 1;
		temporaryDown = defenderY - 1;
		temporaryLeft = defenderX - 1;
		temporaryRight = defenderX + 1;

		upDistance = distanceCalculator(defenderX, temporaryUp, targetX, targetY);
		downDistance = distanceCalculator(defenderX, temporaryDown, targetX, targetY);
		leftDistance = distanceCalculator(temporaryLeft, defenderY, targetX, targetY);
		rightDistance = distanceCalculator(temporaryRight, defenderY, targetX, targetY);

		//determine what to do if vulnerable or close to defender
		if(gameStatus.getDefender(ghostNumber).isVulnerable()){
			direction = carefulChaser(gameStatus, ghostNumber);
		}
		else{
			if(distanceCalculator(playerX, playerY, defenderX, defenderY) <= 4){
				direction = carefulChaser(gameStatus, ghostNumber);
			}
			else{
				direction = getMinimum(upDistance, downDistance, leftDistance, rightDistance);
			}
		}

		return direction;
	}

	//find direction to place defender behind ghost; defender 2
	public static int getBehind(Game gameStatus, int ghostNumber){
		int direction = -1;

		int defenderX, defenderY;
		int playerX, playerY;
		int targetX, targetY;

		int playerDirection;

		double upDistance, temporaryUp;
		double downDistance, temporaryDown;
		double leftDistance, temporaryLeft;
		double rightDistance, temporaryRight;

		//defender (x,y)
		defenderX = gameStatus.getDefender(ghostNumber).getLocation().getX();
		defenderY = gameStatus.getDefender(ghostNumber).getLocation().getY();

		//player (x,y)
		playerX = gameStatus.getAttacker().getLocation().getX();
		playerY = gameStatus.getAttacker().getLocation().getY();

		//convert Y to actual
		defenderY = yConverter(defenderY);
		playerY = yConverter(playerY);

		//find player direction
		playerDirection = gameStatus.getAttacker().getDirection();

		//optimal targeting point based on where the attacker is facing
		if(playerDirection == 0){
			targetX = playerX;
			targetY = playerY - 4;
		}
		else if(playerDirection == 1){
			targetX = playerX - 4;
			targetY = playerY;
		}
		else if(playerDirection == 2){
			targetX = playerX;
			targetY = playerY + 4;
		}
		else if (playerDirection == 3){
			targetX = playerX + 4;
			targetY = playerY;
		}
		else{
			targetX = playerX;
			targetY = playerY;
		}

		//find direction to optimize distance to target direction behind player
		temporaryUp = defenderY + 1;
		temporaryDown = defenderY - 1;
		temporaryLeft = defenderX - 1;
		temporaryRight = defenderX + 1;

		upDistance = distanceCalculator(defenderX, temporaryUp, targetX, targetY);
		downDistance = distanceCalculator(defenderX, temporaryDown, targetX, targetY);
		leftDistance = distanceCalculator(temporaryLeft, defenderY, targetX, targetY);
		rightDistance = distanceCalculator(temporaryRight, defenderY, targetX, targetY);

		//determine what to do if vulnerable or close to defender
		if(gameStatus.getDefender(ghostNumber).isVulnerable()){
			direction = carefulChaser(gameStatus, ghostNumber);
		}
		else{
			if(distanceCalculator(playerX, playerY, defenderX, defenderY) <= 4){
				direction = carefulChaser(gameStatus, ghostNumber);
			}
			else{
				direction = getMinimum(upDistance, downDistance, leftDistance, rightDistance);
			}
		}

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

	//find the smallest distance that would occur if the ghost moves in a given direction
	public static int getMinimum(double up, double down, double left, double right){
		int finalAction = -1;

		//assign a direction based on which direction would reduce the distance to the attacker the most
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

	//find the largest distance that would occur if the ghost moves in a given direction
	public static int getMaximum(double up, double down, double left, double right){
		int finalAction= -1;

		//assign a direction based on which direction would increase the distance to the attacker the most
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

}