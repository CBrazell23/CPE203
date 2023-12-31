import processing.core.PImage;

import java.util.List;
import java.util.Optional;

public class Ore extends Acting {

    public static final String BLOB_KEY = "blob";
    public static final String BLOB_ID_SUFFIX = " -- blob";
    public static final int BLOB_PERIOD_SCALE = 4;
    public static final int BLOB_ANIMATION_MIN = 50;
    public static final int BLOB_ANIMATION_MAX = 150;


    public Ore(String id, Point position, List<PImage> images,
               int actionPeriod, int animationPeriod) {
        super(id, position, images, actionPeriod, animationPeriod);
    }

    public static Ore createOre(String id, Point position, int actionPeriod, List<PImage> images) {
        return new Ore(id, position, images, actionPeriod, 0);
    }

    public void act(WorldModel world, ImageStore imageStore, EventScheduler scheduler) {
        Point pos = this.getPosition();

        world.removeEntity(this);
        scheduler.unscheduleAllEvents(this);

        Entity blob = Oreblob.createOreBlob(this.getId() + BLOB_ID_SUFFIX,
                pos, this.getActionPeriod() / BLOB_PERIOD_SCALE,
                BLOB_ANIMATION_MIN +
                        Functions.rand.nextInt(BLOB_ANIMATION_MAX - BLOB_ANIMATION_MIN),
                imageStore.getImageList(BLOB_KEY));

        world.addEntity(blob);
        ((Oreblob) blob).scheduleActions(scheduler, world, imageStore);
    }
}
