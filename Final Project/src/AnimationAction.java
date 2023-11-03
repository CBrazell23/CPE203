public class AnimationAction extends Action{

    public AnimationAction(Entity entity, WorldModel world, ImageStore imageStore, int repeatCount)
    {
        super(entity, world, imageStore, repeatCount);
    }

    public static AnimationAction createAnimationAction(Entity entity, int repeatCount)
    {
        return new AnimationAction( entity, null, null, repeatCount);
    }

    public  void executeAction(EventScheduler scheduler)
    {
        this.entity.setNextImage();

        if (this.repeatCount != 1)
        {
            scheduler.scheduleEvent(this.entity, createAnimationAction(this.entity, Math.max(this.repeatCount - 1, 0) ), this.entity.getAnimationPeriod());
        }
    }

}
