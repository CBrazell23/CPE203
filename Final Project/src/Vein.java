import processing.core.PImage;

import java.util.List;
import java.util.Optional;

public class Vein extends Animationing{

    public static final String ORE_ID_PREFIX = "ore -- ";
    public static final int ORE_CORRUPT_MIN = 20000;
    public static final int ORE_CORRUPT_MAX = 30000;

    public static final String ORE_KEY = "ore";


    public Vein(String id, Point position, List<PImage> images, int resourceLimit, int resourceCount,
                int actionPeriod, int animationPeriod) {
        super(id, position, images, actionPeriod, animationPeriod);
    }

    public static Vein createVein(String id, Point position, int actionPeriod, List<PImage> images) {
        return new Vein( id, position, images, 0, 0,
                actionPeriod, 0);
    }

    public void act( WorldModel world, ImageStore imageStore, EventScheduler scheduler)
    {
        Optional<Point> openPt = world.findOpenAround(this.getPosition());

        if (openPt.isPresent()) {
            Entity ore = Ore.createOre(ORE_ID_PREFIX + this.getId(),
                    openPt.get(), ORE_CORRUPT_MIN +
                            Functions.rand.nextInt(ORE_CORRUPT_MAX - ORE_CORRUPT_MIN),
                    imageStore.getImageList(ORE_KEY));
            world.addEntity(ore);
            ((Ore)ore).scheduleActions( scheduler, world, imageStore);
        }

        scheduler.scheduleEvent( this,
                ActivityAction.createActivityAction(this, world, imageStore),
                this.getActionPeriod());
    }

}
