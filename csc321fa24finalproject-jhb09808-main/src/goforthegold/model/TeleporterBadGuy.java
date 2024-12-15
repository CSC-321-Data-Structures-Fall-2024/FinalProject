package goforthegold.model;

/**
 * Represents a teleporter type bad guy in the game.
 */
public class TeleporterBadGuy extends BadGuy {
   private int teleportCooldown;

   /**
    * Constructs a new TeleporterBadGuy.
    * @param x The initial x-coordinate
    * @param y The initial y-coordinate
    */
   public TeleporterBadGuy(int x, int y) {
      super(x, y, BadGuyType.TELEPORTER);
      this.teleportCooldown = 0;
   }

   @Override
   public void move(World world, Robot robot, Difficulty difficulty) {
      if (Math.random() < getDifficultyIntelligence(difficulty)) {
          moveIntelligently(world, robot);
      } else {
          moveRandomly(world);
      }
   }

   private void moveIntelligently(World world, Robot robot) {
      if (teleportCooldown == 0) {
          teleportNearRobot(world, robot);
      } else {
          moveTowardsRobot(world, robot);
      }
   }

   private void moveRandomly(World world) {
      if (teleportCooldown == 0) {
          teleportCooldown = 5;
          teleportToRandomCell(world);
      } else {
          teleportCooldown--;
          randomMove(world);
      }
   }

   private void teleportToRandomCell(World world) {
      for (int i = 0; i < 10; i++) {
          int[] position = world.getRandomUnvisitedCell();
          if (world.isValidMove(position[0], position[1])) {
              this.x = position[0];
              this.y = position[1];
              break;
          }
      }
   }

   private void randomMove(World world) {
      int dx = (int)(Math.random() * 3) - 1;
      int dy = (int)(Math.random() * 3) - 1;
      if (world.isValidMove(x + dx, y + dy)) {
          x += dx;
          y += dy;
      }
   }

   private void moveTowardsRobot(World world, Robot robot) {
      int dx = Integer.compare(robot.getX(), x);
      int dy = Integer.compare(robot.getY(), y);
      if (world.isValidMove(x + dx, y + dy)) {
          x += dx;
          y += dy;
      }
      teleportCooldown--;
   }


   private void teleportNearRobot(World world, Robot robot) {
      int maxAttempts = 10;
      for (int i = 0; i < maxAttempts; i++) {
          int newX = robot.getX() + (int)(Math.random() * 5) - 2; 
          int newY = robot.getY() + (int)(Math.random() * 5) - 2;
          if (world.isValidMove(newX, newY)) {
              x = newX;
              y = newY;
              teleportCooldown = 5; 
              return;
          }
      }
      
      randomMove(world);
   }
}