import java.util.List;

import processing.core.PImage;

abstract public class Animationing extends Acting{
    public Animationing(String id, Point position, List<PImage> images, int actionPeriod, int animationPeriod) {
        super(id,position,images,actionPeriod,animationPeriod);
    }
}
