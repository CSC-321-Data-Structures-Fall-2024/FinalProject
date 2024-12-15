package goforthegold.model;

/**
 * Represents a patroller type bad guy.
 */
public class PatrollerBadGuy extends BadGuy {
    private int[][] patrolPoints;
    private int currentPointIndex;

    public PatrollerBadGuy(int x,int y,int[][] patrolPoints){
       super(x,y,BadGuyType.PATROLLER);
       this.patrolPoints=patrolPoints;
       this.currentPointIndex=0;
   }

   @Override
   public void move(World world ,Robot robot,Difficulty difficulty){
       if(Math.random()<getDifficultyIntelligence(difficulty)){
           moveIntelligently(world ,robot);
       }else{
           movePatrol(world);
       }
   }

   private void moveIntelligently(World world ,Robot robot){
       if(distanceToRobot(robot)<=3){
           
           moveTowardsRobot(world ,robot);
       }else{
           movePatrol(world);
       }
   }

   private void moveTowardsRobot(World world ,Robot robot){
       
       int dx=Integer.compare(robot.getX(),x);
       int dy=Integer.compare(robot.getY(),y);
       if(world.isValidMove(x+dx,y+dy)){
           x+=dx;
           y+=dy;
       }
   }

   private void movePatrol(World world){
       
       int[] target=patrolPoints[currentPointIndex];
       int dx=Integer.compare(target[0],x);
       int dy=Integer.compare(target[1],y);
       if(world.isValidMove(x+dx,y+dy)){
           x+=dx;
           y+=dy;
       }
       if(x==target[0] && y==target[1]){
           currentPointIndex=(currentPointIndex+1)%patrolPoints.length;
       }
   }
}