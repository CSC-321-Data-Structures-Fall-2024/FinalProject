package goforthegold.model;

/**
 * Represents a chaser type bad guy.
 */
public class ChaserBadGuy extends BadGuy {
    public ChaserBadGuy(int x, int y) {
        super(x,y,BadGuyType.CHASER);
    }

   @Override
   public void move(World world ,Robot robot,Difficulty difficulty){
       if(Math.random()<getDifficultyIntelligence(difficulty)){
           moveIntelligently(world ,robot);
       }else{
           moveRandomly(world);
       }
   }

   private void moveIntelligently(World world ,Robot robot){
       if(distanceToRobot(robot)<=3){
           
           int dx=Integer.compare(robot.getX(),x);
           int dy=Integer.compare(robot.getY(),y);
           if(world.isValidMove(x+dx,y+dy)){
               x+=dx;
               y+=dy;
           } 
       } else { 
           moveRandomly(world); 
       }
   }

   private void moveRandomly(World world){
       
       int dx=(int)(Math.random()*3)-1; 
       int dy=(int)(Math.random()*3)-1; 
       if(world.isValidMove(x+dx,y+dy)){ 
           x+=dx; 
           y+=dy; 
       } 
   } 
}