package pathingHelp;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.map.MapRoomNode;
import com.megacrit.cardcrawl.rooms.*;
import com.megacrit.cardcrawl.screens.DungeonMapScreen;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static pathingHelp.ui.PathMenu.ANY;
import static pathingHelp.PathingHelp.logger;

public class Path {
    public ArrayList<MapRoomNode> nodes;

    public Path(ArrayList<MapRoomNode> nodes) {
        this.nodes = nodes;
    }

    public int numRests() {
        int n = 0;
        for (MapRoomNode node : nodes) {
            if (node.getRoom() instanceof RestRoom) n++;
        }
        return n;
    }

    public int numEvents() {
        int n = 0;
        for (MapRoomNode node : nodes) {
            if (node.getRoom() instanceof EventRoom) n++;
        }
        return n;
    }

    public int numElites() {

        int n = 0;
        for (MapRoomNode node : nodes) {
            if (node.getRoom() instanceof MonsterRoomElite) n++;
        }
        return n;
    }

    public int numShops() {

        int n = 0;
        for (MapRoomNode node : nodes) {
            if (node.getRoom() instanceof ShopRoom) n++;
        }
        return n;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append(numRests()).append(" rests, ");
        sb.append(numElites()).append(" elites, ");
        sb.append(numShops()).append(" shops, ");
        sb.append(numEvents()).append(" events: ");

        int final_index = nodes.size() - 1;
        for(int i = 0; i < final_index; i++) {
            sb.append("(")
                    .append(nodes.get(i).x)
                    .append(",")
                    .append(nodes.get(i).y)
                    .append(")");
            sb.append(" -> ");
        }
        sb.append("(")
                .append(nodes.get(final_index).x)
                .append(",")
                .append(nodes.get(final_index).y)
                .append(")");

        return sb.toString();
    }

    public static ArrayList<Path> filterPaths(ArrayList<Path> paths, ArrayList<MapRoomNode> mustContainNodes, int numRests, int numEvents, int numElites, int numShops) {
        ArrayList<Path> result = new ArrayList<Path>();

        /*
        MapRoomNode curNode = AbstractDungeon.getCurrMapNode();
        logger.info("In filterPaths, current map node is " + curNode);
        */
        for (Path path : paths) {
            if ( (numRests == ANY || numRests == path.numRests()) &&
                    (numElites == ANY || numElites == path.numElites()) &&
                    (numEvents == ANY || numEvents == path.numEvents()) &&
                    (numShops == ANY || numShops == path.numShops()) &&
                    path.containsAllNodes(mustContainNodes)
                    // (curNode.y == -1 || path.containsNode(curNode))
               ) {
                result.add(path);
            }
        }

        return result;
    }

    private boolean containsNode(MapRoomNode cNode) {
        boolean contains = false;
        for (MapRoomNode node : nodes) {
            if (node.x == cNode.x && node.y == cNode.y) {
                contains = true;
            }
        }
        return contains;
    }

    private boolean containsAllNodes(ArrayList<MapRoomNode> cNodes) {
        boolean containsAll = true;
        for (MapRoomNode cNode : cNodes) {
            if (!containsNode(cNode)) {
                containsAll = false;
            }
        }
        return containsAll;
    }

    public static ArrayList<Integer> numRestOptions(ArrayList<Path> paths) {
        Set<Integer> options = new HashSet<Integer>();

        for (Path path : paths) {
            options.add(path.numRests());
        }

        ArrayList<Integer> optionsList = new ArrayList<Integer>(options);
        Collections.sort(optionsList);
        return optionsList;
    }

    public static ArrayList<Integer> numEliteOptions(ArrayList<Path> paths) {
        Set<Integer> options = new HashSet<Integer>();

        for (Path path : paths) {
            options.add(path.numElites());
        }

        ArrayList<Integer> optionsList = new ArrayList<Integer>(options);
        Collections.sort(optionsList);
        return optionsList;
    }

    public static ArrayList<Integer> numEventOptions(ArrayList<Path> paths) {
        Set<Integer> options = new HashSet<Integer>();

        for (Path path : paths) {
            options.add(path.numEvents());
        }

        ArrayList<Integer> optionsList = new ArrayList<Integer>(options);
        Collections.sort(optionsList);
        return optionsList;
    }

    public static ArrayList<Integer> numShopOptions(ArrayList<Path> paths) {
        Set<Integer> options = new HashSet<Integer>();

        for (Path path : paths) {
            options.add(path.numShops());
        }

        ArrayList<Integer> optionsList = new ArrayList<Integer>(options);
        Collections.sort(optionsList);
        return optionsList;
    }
    private static final int IMG_WIDTH = (int) (Settings.xScale * 64.0f);
    private static final float SPACING_X = Settings.isMobile ? (float) IMG_WIDTH * 2.2f : (float) IMG_WIDTH * 2.0f;
    private static final float OFFSET_X = Settings.isMobile ? 496.0f * Settings.xScale : 560.0f * Settings.xScale;
    private static final float OFFSET_Y = 180.0f * Settings.scale;

    public static float computeXFromNode(MapRoomNode node) {
        //return (node.x * SPACING_X + OFFSET_X + node.offsetX) / Settings.xScale - 64.0f;
        return node.x * SPACING_X + OFFSET_X + node.offsetX;
    }

    public static float computeYFromNode(MapRoomNode node) {
        // return (node.y * Settings.MAP_DST_Y + OFFSET_Y + DungeonMapScreen.offsetY + node.offsetY) / Settings.yScale - 64.0f;
        return node.y * Settings.MAP_DST_Y + OFFSET_Y + DungeonMapScreen.offsetY + node.offsetY;
    }

    private static Texture greenDot = new Texture("pathingHelp/images/green_dot.png");
    public void render(SpriteBatch sb) {
        for (MapRoomNode node : nodes) {
            float x = computeXFromNode(node);
            float y = computeYFromNode(node);
            sb.draw(greenDot, x, y, 50f * Settings.scale,50f * Settings.scale);
        }
    }
}
