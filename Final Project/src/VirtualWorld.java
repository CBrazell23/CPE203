import java.io.File;
import java.io.FileNotFoundException;
import java.util.Optional;
import java.util.Scanner;
import java.awt.*;

import processing.core.*;

public final class VirtualWorld extends PApplet
{
    private static final int TIMER_ACTION_PERIOD = 100;
    private static final int SMALL_RADIUS = 2;
    private static final int BIG_RADIUS = 3;
    private static final int VIEW_WIDTH = 640;
    private static final int VIEW_HEIGHT = 480;
    private static final int TILE_WIDTH = 32;
    private static final int TILE_HEIGHT = 32;
    private static final int WORLD_WIDTH_SCALE = 2;
    private static final int WORLD_HEIGHT_SCALE = 2;
    private static final int QUAKE_ANIMATION_REPEAT_COUNT = 10;

    private static final int VIEW_COLS = VIEW_WIDTH / TILE_WIDTH;
    private static final int VIEW_ROWS = VIEW_HEIGHT / TILE_HEIGHT;
    private static final int WORLD_COLS = VIEW_COLS * WORLD_WIDTH_SCALE;
    private static final int WORLD_ROWS = VIEW_ROWS * WORLD_HEIGHT_SCALE;

    private static final String IMAGE_LIST_FILE_NAME = "imagelist";
    private static final String DEFAULT_IMAGE_NAME = "background_default";
    private static final int DEFAULT_IMAGE_COLOR = 0x808080;

    private static final String LOAD_FILE_NAME = "world.sav";

    private static final String FAST_FLAG = "-fast";
    private static final String FASTER_FLAG = "-faster";
    private static final String FASTEST_FLAG = "-fastest";
    private static final double FAST_SCALE = 0.5;
    private static final double FASTER_SCALE = 0.25;
    private static final double FASTEST_SCALE = 0.10;

    private static double timeScale = 1.0;

    private ImageStore imageStore;
    private WorldModel world;
    private WorldView view;
    private EventScheduler scheduler;

    private long nextTime;

    private boolean clicked;

    private static final String VOLCANO_KEY = "volcano";
    private static final String FLAME_KEY = "flame";
    private static final String EXPLOSION_KEY = "explosion";


    private static final int EXPLOSION_ACTION_PERIOD = 3;
    private static final int EXPLOSION_ANIMATION_PERIOD = 4;



    public void settings() {
        size(VIEW_WIDTH, VIEW_HEIGHT);
    }

    /*
       Processing entry point for "sketch" setup.
    */

    private void eventNewEntity(Point pressed)
    {
        long nextPeriod = EXPLOSION_ACTION_PERIOD;

        Entity explosion = Explosion.createExplosion("explosion",pressed,
                EXPLOSION_ACTION_PERIOD, EXPLOSION_ANIMATION_PERIOD, imageStore.images.get("explosion"));
        world.addEntity(explosion);
        nextPeriod += EXPLOSION_ACTION_PERIOD;
        VirtualWorld.scheduleActions(world, scheduler, imageStore);
    }

    private Point mouseToPoint(int x, int y)
    {
        Point pressed = view.getViewport().viewportToWorld(mouseX/TILE_WIDTH, mouseY/TILE_HEIGHT);
        return pressed;
    }

    // 3 x 3 area of effect, spawns volcano at mouse click
    private void eventVisualization(Point pressed)
    {

        Background volcano = new Background("volcano", imageStore.images.get("volcano"));
        for(int y = -1; y < SMALL_RADIUS; y++)
        {
            for(int x = -1; x < SMALL_RADIUS; x++)
            {
                Point areaP = new Point(pressed.getX() + x, pressed.getY() + y);

                if(x == 0 && y == 0) {world.setBackground(pressed, volcano); }
            }
        }

    }

    // 5 x 5 changes behavior and appearance of any MINERS in vicinity
    private void eventEffect(Point pressed)
    {

        for(int y = -2; y < BIG_RADIUS; y++)
        {
            for(int x = -2; x < BIG_RADIUS; x++) {
                Point areaP = new Point(pressed.getX() + x, pressed.getY() + y);
                Optional<Entity> occupant = world.getOccupant(areaP);

                if(occupant.isPresent() && occupant.get() instanceof Oreblob)
                {
                    List<PImage> p = (imageStore.getImageList("lavablob"));
                    Point minerSpawn = ((Oreblob)world.getOccupant(areaP).get()).getPosition();

                    // FLAME IN PLACE OF BLOb, miners change to flames

                    Flame newFlame = Flame.createFlame("flame", minerSpawn, 75, 2,p);
                    world.removeEntity(world.getOccupant(areaP).get());

                    world.addEntity(newFlame);
                    VirtualWorld.scheduleActions(world, scheduler, imageStore);
                }

            }
        }

    }


    public void mousePressed()
    {
        Point pressed = mouseToPoint(mouseX , mouseY );
        eventVisualization(pressed);
        eventEffect(pressed);
        eventNewEntity(pressed);

    }

    public void setup() {
        this.imageStore = new ImageStore(
                createImageColored(TILE_WIDTH, TILE_HEIGHT,
                        DEFAULT_IMAGE_COLOR));
        this.world = new WorldModel(WORLD_ROWS, WORLD_COLS,
                createDefaultBackground(imageStore));
        this.view = new WorldView(VIEW_ROWS, VIEW_COLS, this, world, TILE_WIDTH,
                TILE_HEIGHT);
        this.scheduler = new EventScheduler(timeScale);

        loadImages(IMAGE_LIST_FILE_NAME, imageStore, this);
        loadWorld(world, LOAD_FILE_NAME, imageStore);

        scheduleActions(world, scheduler, imageStore);

        nextTime = System.currentTimeMillis() + TIMER_ACTION_PERIOD;
        clicked = false;
    }

    public void draw() {
        long time = System.currentTimeMillis();
        if (time >= nextTime) {
            this.scheduler.updateOnTime(time);
            nextTime = time + TIMER_ACTION_PERIOD;
        }

        this.view.drawViewport(view);
    }

    public void keyPressed() {
        if (key == CODED) {
            int dx = 0;
            int dy = 0;

            switch (keyCode) {
                case UP:
                    dy = -1;
                    break;
                case DOWN:
                    dy = 1;
                    break;
                case LEFT:
                    dx = -1;
                    break;
                case RIGHT:
                    dx = 1;
                    break;
            }
            this.view.shiftView(dx, dy);
        }
    }

    public void mouseClicked() {
        if(!clicked){  //if clicked dont change all blacksmiths into dr wells
            clickHelper(1,1);
            clicked = true;
        }
        redraw();
    }

    private void clickHelper(int animP, int actP){

    }

    public static Background createDefaultBackground(ImageStore imageStore) {
        return new Background(DEFAULT_IMAGE_NAME,
                imageStore.getImageList(DEFAULT_IMAGE_NAME));
    }


    public PImage createImageColored(int width, int height, int color) {
        PImage img = new PImage(width, height, RGB);
        img.loadPixels();
        for (int i = 0; i < img.pixels.length; i++) {
            img.pixels[i] = color;
        }
        img.updatePixels();
        return img;
    }

    private void loadImages(
            String filename, ImageStore imageStore, PApplet screen)
    {
        try {
            Scanner in = new Scanner(new File(filename));
            ImageStore.loadImages(in, imageStore, screen);
        }
        catch (FileNotFoundException e) {
            System.err.println(e.getMessage());
        }
    }

    public void loadWorld(
            WorldModel world, String filename, ImageStore imageStore)
    {
        try {
            Scanner in = new Scanner(new File(filename));
            Functions.load(in, world, imageStore);
        }
        catch (FileNotFoundException e) {
            System.err.println(e.getMessage());
        }
    }

    public static void scheduleActions(WorldModel world, EventScheduler scheduler, ImageStore imageStore)
    {
        for (Entity entity : world.getEntities()) {
            if (entity instanceof Acting) {
                ((Acting) entity).scheduleActions(scheduler, world, imageStore);
            }
        }
    }

    public static void parseCommandLine(String[] args) {
        for (String arg : args) {
            switch (arg) {
                case FAST_FLAG:
                    timeScale = Math.min(FAST_SCALE, timeScale);
                    break;
                case FASTER_FLAG:
                    timeScale = Math.min(FASTER_SCALE, timeScale);
                    break;
                case FASTEST_FLAG:
                    timeScale = Math.min(FASTEST_SCALE, timeScale);
                    break;
            }
        }
    }

    public static void main(String[] args) {
        parseCommandLine(args);
        PApplet.main(VirtualWorld.class);
    }
}
