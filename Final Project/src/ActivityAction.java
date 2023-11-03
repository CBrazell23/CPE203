public class ActivityAction extends Action{


    public ActivityAction( Entity entity, WorldModel world, ImageStore imageStore, int repeatCount) {
        super(entity, world, imageStore, repeatCount);
    }

    public static ActivityAction createActivityAction(Entity entity, WorldModel world, ImageStore imageStore) {
        return new ActivityAction(entity, world, imageStore, 0);
    }

    public void executeAction(EventScheduler scheduler) {
        if(this.entity instanceof MinerFull) {
            ((MinerFull) this.entity).act(this.world, this.imageStore, scheduler);
        }
        else if(this.entity instanceof MinerNotFull) {
            ((MinerNotFull) this.entity).act(this.world, this.imageStore, scheduler);
        }
        else if(this.entity instanceof Ore) {
            ((Ore) this.entity).act(this.world, this.imageStore, scheduler);
        }
        else if(this.entity instanceof Oreblob) {
            ((Oreblob) this.entity).act(this.world, this.imageStore, scheduler);
        }
        else if(this.entity instanceof Quake) {
            ((Quake) this.entity).act(this.world, this.imageStore, scheduler);
        }
        else if(this.entity instanceof Vein) {
            ((Vein)this.entity).act(this.world, this.imageStore, scheduler);
        }
    }
}
