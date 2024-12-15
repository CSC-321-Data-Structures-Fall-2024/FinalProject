package goforthegold.util;

import goforthegold.model.World;
import java.util.Stack;

/**
 * Utility class for generating a maze in the game world.
 */
public class MazeGenerator {
    private static final int WALL = 1;
    private static final int PATH = 0;

    /**
     * Generates a maze in the given world using a depth-first search algorithm.
     * 
     * @param world The World object to generate the maze in
     */
    public static void generateMaze(World world) {
        int size = world.getSize();
        int[][] maze = new int[size][size];

        
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                maze[i][j] = WALL;
            }
        }

        
        int startX = 1;
        int startY = 1;
        maze[startX][startY] = PATH;

        Stack<int[]> stack = new Stack<>();
        stack.push(new int[]{startX, startY});

        while (!stack.isEmpty()) {
            int[] current = stack.peek();
            int x = current[0];
            int y = current[1];

            int[][] directions = {{0, -2}, {0, 2}, {-2, 0}, {2, 0}};
            boolean deadEnd = true;

            for (int[] dir : directions) {
                int newX = x + dir[0];
                int newY = y + dir[1];

                if (newX > 0 && newX < size - 1 && newY > 0 && newY < size - 1 && maze[newX][newY] == WALL) {
                    maze[newX][newY] = PATH;
                    maze[x + dir[0] / 2][y + dir[1] / 2] = PATH;
                    stack.push(new int[]{newX, newY});
                    deadEnd = false;
                    break;
                }
            }

            if (deadEnd) {
                stack.pop();
            }
        }

        
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (maze[i][j] == WALL) {
                    world.setObstacle(i, j);
                }
            }
        }
    }
}