package goforthegold.model;

import java.util.*;

/**
 * Implements the A* pathfinding algorithm for the Go for the Gold game.
 * This class is responsible for finding the optimal path for the robot,
 * considering coins, bad guys, and obstacles.
 */
public class AStarPathfinder {
	
    /**
     * Represents a node in the A* search algorithm.
     */
    private static class Node implements Comparable<Node> {
        int x, y;
        int g, h;
        Node parent;

        /**
         * Constructs a new Node with given coordinates.
         * 
         * @param x The x-coordinate of the node
         * @param y The y-coordinate of the node
         */

        Node(int x, int y) {
            this.x = x;
            this.y = y;
        }

        /**
         * Calculates the f-score of the node (f = g + h).
         * 
         * @return The f-score
         */
        int f() {
            return g + h;
        }

        @Override
        public int compareTo(Node other) {
            return Integer.compare(this.f(), other.f());
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Node node = (Node) o;
            return x == node.x && y == node.y;
        }

        @Override
        public int hashCode() {
            return Objects.hash(x, y);
        }
    }
    
    /**
     * Finds the optimal path from start to goal using the A* algorithm.
     * 
     * @param world The game world
     * @param startX The starting x-coordinate
     * @param startY The starting y-coordinate
     * @param goalX The goal x-coordinate
     * @param goalY The goal y-coordinate
     * @param coins List of coins in the world
     * @param badGuys List of bad guys in the world
     * @param visitedLocations Hash table of visited locations
     * @return A list of coordinates representing the path, or null if no path is found
     */
    public static List<int[]> findPath(World world, int startX, int startY, int goalX, int goalY, List<Coin> coins, List<BadGuy> badGuys, HashTable<String, Boolean> visitedLocations) {
        PriorityQueue<Node> openSet = new PriorityQueue<>();
        Set<Node> closedSet = new HashSet<>();

        Node startNode = new Node(startX, startY);
        startNode.g = 0;
        
        startNode.h = heuristic(startX, startY, goalX, goalY, coins, badGuys);

        openSet.add(startNode);

        boolean greedyMode = true; 

        while (!openSet.isEmpty()) {
            Node current = openSet.poll();

            
            if (current.x == goalX && current.y == goalY || coins.isEmpty()) {
                return reconstructPath(current);
            }

            closedSet.add(current);

            for (int[] dir : new int[][]{{-1, 0}, {1, 0}, {0, -1}, {0, 1}}) {
                int newX = current.x + dir[0];
                int newY = current.y + dir[1];

                if (!world.isValidMove(newX, newY) || world.isObstacle(newX, newY) || visitedLocations.get(newX + "," + newY) != null) {
                    continue;
                }

                Node neighbor = new Node(newX, newY);
                if (closedSet.contains(neighbor)) {
                    continue;
                }

                int tentativeG = current.g + 1;

                if (!openSet.contains(neighbor) || tentativeG < neighbor.g) {
                    neighbor.parent = current;
                    neighbor.g = tentativeG;

                    if (greedyMode) {
                        neighbor.h = heuristic(newX, newY, goalX, goalY, coins, badGuys);
                        
                        for (Coin coin : coins) {
                            if (coin.getX() == newX && coin.getY() == newY) {
                                neighbor.h -= 1000; 
                                break;
                            }
                        }
                        
                        if (tentativeG > 5) { 
                            greedyMode = false;
                        }
                    } else {
                        neighbor.h = heuristic(newX, newY, goalX, goalY, coins, badGuys);
                    }

                    

                    if (!openSet.contains(neighbor)) {
                        openSet.add(neighbor);
                    }
                }
            }
        }

        return null; 
    }

    /**
     * Calculates the heuristic value for a given position.
     * 
     * @param x Current x-coordinate
     * @param y Current y-coordinate
     * @param goalX Goal x-coordinate
     * @param goalY Goal y-coordinate
     * @param coins List of coins
     * @param badGuys List of bad guys
     * @return The calculated heuristic value
     */
    private static int heuristic(int x, int y, int goalX, int goalY, List<Coin> coins, List<BadGuy> badGuys) {
        int distanceToGoal = manhattan(x, y, goalX, goalY);
        int closestCoinDistance = Integer.MAX_VALUE;
        int closestBadGuyDistance = Integer.MAX_VALUE;

        for (Coin coin : coins) {
            int coinDistance = manhattan(x, y, coin.getX(), coin.getY());
            closestCoinDistance = Math.min(closestCoinDistance, coinDistance);
        }

        for (BadGuy badGuy : badGuys) {
            int badGuyDistance = manhattan(x, y, badGuy.getX(), badGuy.getY());
            closestBadGuyDistance = Math.min(closestBadGuyDistance, badGuyDistance);
        }

        int coinWeight = 150;
        int goalWeight = 100;
        int badGuyWeight = 200;

        return goalWeight * distanceToGoal + 
               coinWeight * (closestCoinDistance == Integer.MAX_VALUE ? 0 : closestCoinDistance) - 
               badGuyWeight * (10 - Math.min(closestBadGuyDistance, 10));
    }

    /**
     * Calculates the Manhattan distance between two points.
     * 
     * @param x1 First point x-coordinate
     * @param y1 First point y-coordinate
     * @param x2 Second point x-coordinate
     * @param y2 Second point y-coordinate
     * @return The Manhattan distance
     */
    private static int manhattan(int x1, int y1, int x2, int y2) {
        return Math.abs(x1 - x2) + Math.abs(y1 - y2);
    }

    /**
     * Reconstructs the path from the goal node to the start node.
     * 
     * @param goal The goal node
     * @return A list of coordinates representing the path
     */
    private static List<int[]> reconstructPath(Node goal) {
        List<int[]> path = new ArrayList<>();
        Node current = goal;

        while (current != null) {
            path.add(0, new int[]{current.x, current.y});
            current = current.parent;
        }

        return path;
    }
}