import processing.core.PImage;

import java.util.List;
import java.util.Optional;

public class Oreblob extends Moving{

    public static final String QUAKE_KEY = "quake";
    public static final String QUAKE_ID = "quake";


    public Oreblob(String id, Point position, List<PImage> images, int actionPeriod, int animationPeriod) {
        super(id, position, images, actionPeriod, animationPeriod);
    }

    public static Oreblob createOreBlob(String id, Point position, int actionPeriod, int animationPeriod,
                                        List<PImage> images) {
        return new Oreblob( id, position, images, actionPeriod, animationPeriod);
    }


    public boolean moveToOreBlob( WorldModel world, Entity target, EventScheduler scheduler) {
        if (Point.adjacent(this.getPosition(), target.getPosition())) {
            world.removeEntity(target);
            scheduler.unscheduleAllEvents( target);
            return true;
        } else {
            Point nextPos = this.nextPosition(world, target.getPosition());
            if (!this.getPosition().equals(nextPos)) {
                Optional<Entity> occupant = world.getOccupant( nextPos);
                if (occupant.isPresent()) {
                    scheduler.unscheduleAllEvents(occupant.get());
                }
                world.moveEntity(this, nextPos);
            }
            return false;
        }
    }


    public  void act( WorldModel world, ImageStore imageStore, EventScheduler scheduler) {
        Optional<Entity> blobTarget = world.findNearest( this.getPosition(), Vein.class);
        long nextPeriod = this.getActionPeriod();

        if (blobTarget.isPresent()) {
            Point tgtPos = blobTarget.get().getPosition();

            if (moveToOreBlob(world, blobTarget.get(), scheduler)) {
                Entity quake = Quake.createQuake(tgtPos, imageStore.getImageList(QUAKE_KEY));

                world.addEntity( quake);
                nextPeriod += this.getActionPeriod();
                ((Quake)quake).scheduleActions( scheduler, world, imageStore);
            }
        }

        scheduler.scheduleEvent( this, ActivityAction.createActivityAction(this, world,
                imageStore), nextPeriod);
    }
}
