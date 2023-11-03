import processing.core.PImage;
import java.util.List;
import java.util.Optional;
import java.util.List;

public class Moving extends Acting{

    public Moving(String id, Point position, List<PImage> images,
                  int actionPeriod, int animationPeriod)
    {
        super(id,position,images,actionPeriod,animationPeriod);
    }

    public Point nextPosition( WorldModel world, Point destPos)
    {
        int horiz = Integer.signum(destPos.getX() - this.getPosition().getX());
        Point newPos = new Point(this.getPosition().getX()+ horiz,
                this.getPosition().getY());

        Optional<Entity> occupant = world.getOccupant( newPos);

        if (horiz == 0 ||
                (occupant.isPresent() && !(occupant.get().getClass() == Ore.class )))
        {
            int vert = Integer.signum(destPos.getY() - this.getPosition().getY());
            newPos = new Point(this.getPosition().getX(), this.getPosition().getY() + vert);
            occupant = world.getOccupant( newPos);

            if ((vert == 0) ||
                    (occupant.isPresent() && !(occupant.get().getClass() == Ore.class)))
            {
                newPos = this.getPosition();
            }
        }

        return newPos;
    }


}
