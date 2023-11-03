import processing.core.PImage;

import java.util.List;
import java.util.Optional;

public class Explosion extends Moving{
    private static final String FLAME_KEY = "flame";

    public Explosion(String id, Point position, List<PImage> images, int actionPeriod, int animationPeriod) {
        super(id,position,images,actionPeriod,animationPeriod);
    }
    public static Explosion createExplosion(String id, Point position,
                                            int actionPeriod, int animationPeriod, List<PImage> images)
    {
        return new Explosion( id, position, images, actionPeriod, animationPeriod);
    }



    public  void execute( WorldModel world, ImageStore imageStore, EventScheduler scheduler)
    {
        Optional<Entity> nearbyExplosion = world.findNearest( this.getPosition(), Miner.class);
        long nextPeriod = this.getActionPeriod();

        if (nearbyExplosion.isPresent())
        {
            Point p = nearbyExplosion.get().getPosition();
        }

        scheduler.scheduleEvent( this,
                ActivityAction.createActivityAction(this, world, imageStore),
                nextPeriod);
    }



    public boolean moveTo( WorldModel world, Entity target, EventScheduler scheduler, ImageStore imageStore) {

        if (Point.adjacent(this.getPosition(), target.getPosition()) && world.getOccupant(target.getPosition()).isPresent()) {

            Miner flame = (Miner) (world.getOccupant(target.getPosition()).get());

            flame.setImageIndex(3);
            flame.setImages(imageStore.getImageList(FLAME_KEY));
            flame.setAnimationPeriod(5);
            flame.setActionPeriod(5);

            return true;

        }
        else {
            Point nextPos = this.nextPosition(world, target.getPosition());

            if (!this.getPosition().equals(nextPos))
            {
                Optional<Entity> occupant = world.getOccupant(nextPos);
                if (occupant.isPresent())
                {
                    scheduler.unscheduleAllEvents(occupant.get());
                }
                world.moveEntity(this, nextPos);
            }
            return false;
        }
    }
}
