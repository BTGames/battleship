package com.example.battleship.app;

import android.widget.TextView;

import java.util.Random;

/**
 * Created by ghost on 07.05.2014.
 */
public class AI {

	private final int SIZE = 10;
	private final int UNVISITED = 8;
	private final int HIT = 7;
	private final int SUNK = 6;
	private final int EMPTY = 5;
	private final int NOT_SUNK = 9;
	private final int HORIZONTAL = 0;
	private final int VERTICAL = 1;

	private final int[] SHIP_LENGTHS = {2, 3, 3, 4, 5};
	private final int[] SHIP_VALUES = {0, 1, 2, 3, 4};
	private int[][][] SHIP_LOCATIONS = new int[5][5][2];

	private int[][] tracking = new int[SIZE][SIZE];
	private int[][] primary = new int[SIZE][SIZE];
	private boolean[] sunk = {false, false, false, false, false};
	private boolean mode;
	private Player player;

	private Random rand = new Random();

	public AI(Player player) {
		//Initial values
		mode = false;
		this.player = player;

		//Fill tables with 0.

		for (int i = 0; i < SIZE; i++) {
			for (int j = 0; j < SIZE; j++) {
				primary[i][j] = EMPTY;
				tracking[i][j] = UNVISITED;
			}
		}

		//Fill ships.

		for (int i = 0; i < SHIP_LENGTHS.length; i++) {
			boolean control;
			int x, y, axis;

			do {
				x = rand.nextInt(SIZE);
				y = rand.nextInt(SIZE);
				axis = rand.nextInt(2);
				control = isPuttable(x, y, SHIP_LENGTHS[i], axis);
			} while (!control);

			if (axis == VERTICAL) {
				for (int j = 0; j < SHIP_LENGTHS[i]; j++) {
					primary[x + j][y] = SHIP_VALUES[i];
					SHIP_LOCATIONS[i][j][0] = x+j;
					SHIP_LOCATIONS[i][j][1] = y;
				}
			} else {
				for (int j = 0; j < SHIP_LENGTHS[i]; j++) {
					primary[x][y + j] = SHIP_VALUES[i];
					SHIP_LOCATIONS[i][j][0] = x;
					SHIP_LOCATIONS[i][j][1] = y+j;
				}
			}
		}
	}



	public void hunt(Player player, TextView text) {
		/*
			Hunts.
		 */
		int[] tmp;
		/*
		Check for HIT targets.
		*/
		for (int i=0; i<SIZE; i++) {
			for (int j=0; j<SIZE; j++) {
				if (tracking[i][j] == HIT) {
					mode = true;
				}
			}
		}
		int[] coordinates = findTarget(mode);
		tmp = player.hit(coordinates[0], coordinates[1]);

		if (tmp[0] == HIT) {
			if (tmp[1] != NOT_SUNK) {
				/* Sunk a ship. */
				sunk[tmp[1]] = true;
				sink(tmp[1]);
				mode = false;

				text.setText("Sunk a ship at (" + coordinates[0] + "," + coordinates[1] + ").");
			} else {
				/* Hit a ship without sinking it. */
				tracking[coordinates[0]][coordinates[1]] = HIT;
				mode = true;
				text.setText("Hit a ship at (" + coordinates[0] + "," + coordinates[1] + ").");
			}
		} else {
			tracking[coordinates[0]][coordinates[1]] = EMPTY;
			text.setText("Missed at (" + coordinates[0] + "," + coordinates[1] + ").");
		}
	}

	private int[] findTarget(boolean mode) {
		/*
		Finds a target to hit. Returns coordinates.
		 */
		int[] max = {0, 0};
		int[][] probabilities = new int[SIZE][SIZE];

		for (int i=0; i<SIZE; i++) {
			for (int j=0; j<SIZE; j++) {
				probabilities[i][j] = 0;
			}
		}

		for (int i = 0; i < SHIP_LENGTHS.length; i++) { //Iterate for each ship.
			if(!sunk[i]) {
				for (int j = 0; j < SIZE; j++) { //Iterate every tile of the grid.
					for (int k = 0; k < SIZE; k++) {
						for (int l = 0; l < 2; l++) { //Iterate for every axis.
							if (isValidShip(j, k, SHIP_LENGTHS[i], l)) {
								for (int m = 0; m < SHIP_LENGTHS[i]; m++) {
									if (l == 0) {
										/*
										Horizontal
										 */
										probabilities[j][k+m]++;
									} else {
										/*
										Vertical
										 */
										probabilities[j+m][k]++;
									}
								}
							}
						}
					}
				}
			}
		}

		if (mode) {
			/*
			There is a target.
			 */
			int maxval = -1;
			for (int i = 0; i < SIZE; i++) {
				for (int j = 0; j < SIZE; j++) {
					if (tracking[i][j] == HIT) {
						for (int k=-1; k<2; k += 2) {
							if ((i+k>=0) &&(i+k<SIZE)) {
								if (probabilities[i + k][j] > maxval) {
									max[0] = i + k;
									max[1] = j;
									maxval = probabilities[max[0]][max[1]];
								}
							}
							if ((j+k>=0) && (j+k<SIZE)) {
								if (probabilities[i][j + k] > maxval) {
									max[0] = i;
									max[1] = j + k;
									maxval = probabilities[max[0]][max[1]];
								}
							}
						}
					}
				}
			}
		} else {
			/*
			There is no target.
			 */
			for (int i = 0; i < SIZE; i++) {
				for (int j = 0; j < SIZE; j++) {
					if (probabilities[i][j] >= probabilities[max[0]][max[1]]) {
						max[0] = i;
						max[1] = j;
					}
				}
			}
		}

		return max;
	}

	private boolean isValidShip(int x, int y, int len, int axis) {
		/*
		Checks if the given ship is valid according to the tracking grid.

		@param Beginning of the ship, x coordinate.
		@param Beginning of the ship, y coordinate.
		@param Length of the ship.
		@param Axis of the ship. 0 for horizontal, 1 for vertical.

		@return True for valid, false for invalid.
		 */

		if (axis == VERTICAL) {
			/*
			Check vertically.
			 */
			if (x + len < SIZE) {
				for (int i = x; i < x + len; i++) {
					if ((tracking[i][y] != UNVISITED) && (tracking[i][y] != HIT)) {
						/*
						Collision detected. Invalid ship.
						 */
						return false;
					}
				}
				/*
				No collision. Valid ship.
				 */
				return true;
			} else {
				/*
				Out of battlefield. Invalid ship.
				 */
				return false;
			}

		} else {
			/*
			Check horizontally.
			 */
			if (y + len < SIZE) {
				for (int i = y; i < y + len; i++) {
					if ((tracking[x][i] != UNVISITED) && (tracking[i][y] != HIT)) {
						/*
						Collision detected. Invalid ship.
						 */
						return false;
					}
				}
				/*
				No collision. Valid ship.
				 */
				return true;
			} else {
				/*
				Out of battlefield. Invalid ship.
				 */
				return false;
			}

		}

	}


	private boolean isPuttable(int x, int y, int len, int axis) {
		/*
		Checks if the given ship is valid according to the primary grid.

		@param Beginning of the ship, x coordinate.
		@param Beginning of the ship, y coordinate.
		@param Length of the ship.
		@param Axis of the ship. 0 for horizontal, 1 for vertical.

		@return True for valid, false for invalid.
		 */

		if (axis == VERTICAL) {
			/*
			Check vertically.
			 */
			if (x + len < SIZE) {
				for (int i = x; i < x+len; i++) {
					if (primary[i][y] != EMPTY) {
						/*
						Collision detected. Invalid ship.
						 */
						return false;
					}
				}
				/*
				No collision. Valid ship.
				 */
				return true;
			} else {
				/*
				Out of battlefield. Invalid ship.
				 */
				return false;
			}

		} else {
			/*
			Check horizontally.
			 */
			if (y + len < SIZE) {
				for (int i = y; i < y+len; i++) {
					if (primary[x][i] != EMPTY) {
						/*
						Collision detected. Invalid ship.
						 */
						return false;
					}
				}
				/*
				No collision. Valid ship.
				 */
				return true;
			} else {
				/*
				Out of battlefield. Invalid ship.
				 */
				return false;
			}

		}

	}

	public boolean isVictorious() {
		/*
		Checks if victorious.
		 */
		for (boolean aSunk : sunk) {
			if (!aSunk) {
				return false;
			}
		}
		return true;
	}

	public int[] hit(int x, int y) {
		/*
		Returns result, status.
		Result is HIT or not. If HIT, we hit a ship.
		Status is NOT_SUNK or value of the sunk ship.
		*/
		int[] result = {0, 0};
		if ((primary[x][y] != EMPTY) && (primary[x][y] != HIT)
				&& (primary[x][y] != SUNK)) {
			primary[x][y] = HIT;
			result[0] = HIT;
			result[1] = isSunk(primary[x][y]) ? primary[x][y] : NOT_SUNK;
			if (result[1] != NOT_SUNK) {
				sink(result[1]);
			}

		}
		return result;
	}

	private boolean isSunk(int val) {
		/*
		Checks whether given ship exists or not.
		 */
		for (int i=0; i<SIZE; i++) {
			for (int j=0; j<SIZE; j++) {
				if (val == primary[i][j]) {
					return false;
				}
			}
		}
		return true;
	}

	private void sink(int val) {
		/*
		Clears a ship from AI's grid.
		 */
		int[][] coords = player.getLocations(val);
		for (int i=0; i<SHIP_LENGTHS[val]; i++) {
			tracking[coords[i][0]][coords[i][1]] = SUNK;
		}
	}

	public int[][] getLocations(int i) {
		return SHIP_LOCATIONS[i];
	}
}
