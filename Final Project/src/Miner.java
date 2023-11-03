import processing.core.PImage;

import java.util.List;

abstract public class Miner extends Moving {

    protected int resourceLimit;
    protected int resourceCount;


    public Miner(String id, Point position, List<PImage> images, int resourceLimit, int resourceCount, int actionPeriod, int animationPeriod) {

        super(id, position, images, actionPeriod, animationPeriod);
        this.resourceCount = resourceCount;
        this.resourceLimit = resourceLimit;

    }
    public int getResourceLimit(){
        return resourceLimit;
    }
    public int getResourceCount(){
        return resourceCount;
    }


    abstract public void act(WorldModel world, ImageStore imageStore, EventScheduler scheduler);



}