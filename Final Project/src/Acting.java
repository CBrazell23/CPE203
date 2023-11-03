import java.util.List;

import processing.core.PImage;

abstract public class Acting extends Entity{

    public Acting(String id, Point position, List<PImage> images, int actionPeriod, int animationPeriod) {
        super(id,position,images,actionPeriod,animationPeriod);
    }

    public void scheduleActions (EventScheduler scheduler, WorldModel world, ImageStore imageStore) {
        scheduler.scheduleEvent(this,
                ActivityAction.createActivityAction(this, world, imageStore), this.getActionPeriod());
        scheduler.scheduleEvent(this, AnimationAction.createAnimationAction(this, 0),
                this.getAnimationPeriod());
    }
}
