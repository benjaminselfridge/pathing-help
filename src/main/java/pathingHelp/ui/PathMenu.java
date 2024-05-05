package pathingHelp.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.map.MapRoomNode;
import pathingHelp.Path;
import static pathingHelp.PathingHelp.logger;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class PathMenu {

    static final Color FONT_COLOR = new Color(0xD0_D0_D0_FF);
    private static final Texture LEFT_ARROW = ImageMaster.CF_LEFT_ARROW;
    private static final Texture RIGHT_ARROW = ImageMaster.CF_RIGHT_ARROW;
    private static final float menuX = Settings.WIDTH - 160.0f; // Legend.X + 75.0f;
    private static final float menuY = 48.0f;
    private static final float ySpacing = 45.0f;
    private static final float arrowXSpacing = 74.0f;

    private final List<Renderable> renderables = new LinkedList<>();
    private ArrayList<Path> paths;
    private ArrayList<Path> currentPaths;

    public static final int ANY = -1;

    private int numRests;
    private int numElites;
    private int numShops;
    private int numEvents;
    private int currentPathIx;

    private final Text restSelectorText;
    private final Text eliteSelectorText;
    private final Text shopSelectorText;
    private final Text eventSelectorText;
    private final Text pathSelectorText;

    public void selectorChangeValue(SelectorId id, boolean increment) {
        if (id == SelectorId.PATH) {
            if (increment) {
                setCurrentPathIx((currentPathIx + 1) % currentPaths.size());
            } else {
                if (currentPathIx == 0) {
                    setCurrentPathIx(currentPaths.size() - 1);
                } else {
                    setCurrentPathIx(currentPathIx - 1);
                }
            }
        } else {
            if (increment) {
                incrementValue(id);
            } else {
                decrementValue(id);
            }
        }
        resetTexts();
    }

    private void resetTexts() {
        this.restSelectorText.setVal(numRests);
        this.eliteSelectorText.setVal(numElites);
        this.shopSelectorText.setVal(numShops);
        this.eventSelectorText.setVal(numEvents);
        this.pathSelectorText.setVal(currentPathIx+1);
    }

    private static ArrayList<MapRoomNode> computeNodesTaken() {
        ArrayList<MapRoomNode> nodesTaken = new ArrayList<MapRoomNode>();
        for (ArrayList<MapRoomNode> row : AbstractDungeon.map) {
            for (MapRoomNode node : row) {
                if (node.taken) nodesTaken.add(node);
            }
        }
        if (AbstractDungeon.getCurrMapNode().y > -1) {
            nodesTaken.add(AbstractDungeon.getCurrMapNode());
        }
        return nodesTaken;
    }

    private void recomputeCurrentPaths() {
        ArrayList<MapRoomNode> nodesTaken = computeNodesTaken();
        currentPaths = Path.filterPaths(paths, nodesTaken, numRests, numEvents, numElites, numShops);
        if (currentPaths.isEmpty()) {
            currentPaths = Path.filterPaths(paths, new ArrayList(), numRests, numEvents, numElites, numShops);
        }
        setCurrentPathIx(0);
    }

    private void setNumRests(int n) {
        logger.info("number of rests set to " + n);
        this.numRests = n;
        recomputeCurrentPaths();
    }

    private void setNumShops(int n) {
        logger.info("number of shops set to " + n);
        this.numShops = n;
        recomputeCurrentPaths();
    }

    private void setNumElites(int n) {
        logger.info("number of elites set to " + n);
        this.numElites = n;
        recomputeCurrentPaths();
    }

    private void setNumEvents(int n) {
        logger.info("number of events set to " + n);
        this.numEvents = n;
        recomputeCurrentPaths();
    }

    public void setCurrentPathIx(int currentPathIx) {
        this.currentPathIx = currentPathIx;
    }

    private void decrementValue(SelectorId id) {
        ArrayList<Integer> options;
        int cur;
        switch(id) {
            case REST:
                cur = numRests;
                setNumRests(ANY);
                options = Path.numRestOptions(currentPaths);
                if (cur == ANY) {
                    setNumRests(options.get(options.size()-1));
                } else {
                    setNumRests(largestUnder(options, cur));
                }
                break;
            case SHOP:
                cur = numShops;
                setNumShops(ANY);
                options = Path.numShopOptions(currentPaths);
                if (cur == ANY) {
                    setNumShops(options.get(options.size()-1));
                } else {
                    setNumShops(largestUnder(options, cur));
                }
                break;
            case ELITE:
                cur = numElites;
                setNumElites(ANY);
                options = Path.numEliteOptions(currentPaths);
                if (cur == ANY) {
                    setNumElites(options.get(options.size()-1));
                } else {
                    setNumElites(largestUnder(options, cur));
                }
                break;
            case EVENT:
                cur = numEvents;
                setNumEvents(ANY);
                options = Path.numEventOptions(currentPaths);
                if (cur == ANY) {
                    setNumEvents(options.get(options.size()-1));
                } else {
                    setNumEvents(largestUnder(options, cur));
                }
                break;
        }
    }

    private void incrementValue(SelectorId id) {
        ArrayList<Integer> options;
        int cur;
        switch(id) {
            case REST:
                cur = numRests;
                setNumRests(ANY);
                options = Path.numRestOptions(currentPaths);
                if (cur == ANY) {
                    setNumRests(options.get(0));
                } else {
                    setNumRests(smallestOver(options, cur));
                }
                break;
            case SHOP:
                cur = numShops;
                setNumShops(ANY);
                options = Path.numShopOptions(currentPaths);
                if (cur == ANY) {
                    setNumShops(options.get(0));
                } else {
                    setNumShops(smallestOver(options, cur));
                }
                break;
            case ELITE:
                cur = numElites;
                setNumElites(ANY);
                options = Path.numEliteOptions(currentPaths);
                logger.info(options);
                if (cur == ANY) {
                    setNumElites(options.get(0));
                } else {
                    setNumElites(smallestOver(options, cur));
                }
                break;
            case EVENT:
                cur = numEvents;
                setNumEvents(ANY);
                options = Path.numEventOptions(currentPaths);
                if (cur == ANY) {
                    setNumEvents(options.get(0));
                } else {
                    setNumEvents(smallestOver(options, cur));
                }
                break;
        }
    }

    private static int largestUnder(ArrayList<Integer> options, int upperLimit) {
        int max = ANY;

        for (int x : options) {
            if (x < upperLimit && max < x) max = x;
        }

        return max;
    }

    private static int smallestOver(ArrayList<Integer> options, int lowerLimit) {
        int min = ANY;

        for (int x : options) {
            if ( x > lowerLimit && (min > x || min == ANY) ) min = x;
        }

        return min;
    }

    public enum SelectorId { SHOP, REST, EVENT, ELITE, PATH };

    public PathMenu(ArrayList<Path> paths) {
        this.paths = paths;
        this.currentPaths = new ArrayList<Path>();
        this.currentPaths.addAll(this.paths);
        this.currentPathIx = 0;

        this.numRests = ANY;
        this.numShops = ANY;
        this.numElites = ANY;
        this.numEvents = ANY;

        float rowY = menuY;

        this.pathSelectorText = createWidgetRow(rowY, SelectorId.PATH, "Path selection:", this.currentPathIx);
        rowY += ySpacing;
        this.shopSelectorText = createWidgetRow(rowY, SelectorId.SHOP,"Shops:", this.numShops);
        rowY += ySpacing;
        this.restSelectorText = createWidgetRow(rowY, SelectorId.REST,"Rests:", this.numRests);
        rowY += ySpacing;
        this.eventSelectorText = createWidgetRow(rowY, SelectorId.EVENT,"Events:", this.numElites);
        rowY += ySpacing;
        this.eliteSelectorText = createWidgetRow(rowY, SelectorId.ELITE,"Elites:", this.numEvents);
    }

    private Text createWidgetRow(float y, SelectorId id, String label, int n) {
        float weightX = menuX + LEFT_ARROW.getWidth() + ((arrowXSpacing - LEFT_ARROW.getWidth()) / 2);
        renderables.add(new LabelText(menuX, y, label));
        Text txtBox = new Text(id, weightX, y, n);
        renderables.add(new Selector(this, txtBox, id, false, LEFT_ARROW, menuX, y));
        renderables.add(txtBox);
        renderables.add(new Selector(this, txtBox, id, true, RIGHT_ARROW, menuX + arrowXSpacing, y));

        return txtBox;
    }

    public void update() {
        for (Renderable r : renderables) {
            r.update();
        }
    }

    public void render(SpriteBatch sb) {
        sb.setColor(Color.WHITE);
        for (Renderable r : renderables) {
            r.render(sb);
        }
        this.currentPaths.get(currentPathIx).render(sb);
    }

    public void setPaths(ArrayList<Path> paths) {
        this.paths = paths;
        this.currentPaths.clear();
        this.currentPaths.addAll(this.paths);

        setNumRests(ANY);
        setNumElites(ANY);
        setNumShops(ANY);
        setNumEvents(ANY);
        setCurrentPathIx(0);
    }

    /*
    @Override
    public void receivePostUpdate() {
        if (AbstractDungeon.screen == AbstractDungeon.CurrentScreen.MAP && !WeightedPaths.roomValues.isEmpty()) {
            update();
        }
    }
     */
}