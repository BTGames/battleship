package com.example.battleship.app;

import android.util.Log;
import android.widget.TextView;

import java.util.Random;

/**
 * Created by ghost on 07.05.2014.
 */
public class Player {
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

	private int[][] tracking = new int[SIZE][SIZE];
	private int[][] primary = new int[SIZE][SIZE];
	private boolean[] sunk = {false, false, false, false, false};

	private Random rand = new Random();

	public Player() {
		//Fill tables with EMPTY.

		for (int i = 0; i < SIZE; i++) {
			for (int j = 0; j < SIZE; j++) {
				primary[i][j] = EMPTY;
				tracking[i][j] = EMPTY;
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
				}
			} else {
				for (int j = 0; j < SHIP_LENGTHS[i]; j++) {
					primary[x][y + j] = SHIP_VALUES[i];
				}
			}
		}
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
				for (int i = x; i < len; i++) {
					if ((tracking[i][y] != UNVISITED) || (tracking[i][y] == HIT)) {
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
				for (int i = y; i < len; i++) {
					if (tracking[x][i] != UNVISITED || (tracking[i][y] == HIT)) {
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
				for (int i = x; i < len; i++) {
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
				for (int i = y; i < len; i++) {
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

	public int[] hit(int x, int y) {
		/*
		Returns result, status.
		Result is HIT or not. (If HIT, we hit a ship.
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
		Clears a ship from Player's grid.
		 */
		for (int i=0; i<SIZE; i++) {
			for (int j=0; j<SIZE; j++) {
				if (primary[i][j] == val) {
					primary[i][j] = SUNK;
				}
			}
		}
	}

	public void show(TextView v_primary, TextView v_tracking) {
		/*
		Shows grids.
		 */

		String tmp1, tmp2;
		tmp1 = "";
		tmp2 = "";
		for (int i=0; i<SIZE; i++) {
			tmp1 = tmp1 + "| ";
			tmp2 = tmp2 + "| ";
			for (int j=0; j<SIZE; j++) {
				tmp1 = tmp1 + primary[i][j] + " | ";
				tmp2 = tmp2 + tracking[i][j] + " | ";
			}
			tmp1 = tmp1 + "\n";
			tmp2 = tmp2 + "\n";
		}
		v_primary.setText(tmp1);
		v_tracking.setText(tmp2);
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

}
