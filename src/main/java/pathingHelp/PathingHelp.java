package pathingHelp;

import basemod.AutoAdd;
import basemod.BaseMod;
import basemod.interfaces.*;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.map.MapEdge;
import com.megacrit.cardcrawl.map.MapRoomNode;
import pathingHelp.ui.PathMenu;
import com.badlogic.gdx.Files;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl.LwjglFileHandle;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.evacipated.cardcrawl.modthespire.Loader;
import com.evacipated.cardcrawl.modthespire.ModInfo;
import com.evacipated.cardcrawl.modthespire.Patcher;
import com.evacipated.cardcrawl.modthespire.lib.SpireInitializer;
import com.google.gson.Gson;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.localization.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.scannotation.AnnotationDB;

import java.nio.charset.StandardCharsets;
import java.util.*;

@SpireInitializer
public class PathingHelp implements
        PostDungeonInitializeSubscriber,
        PostUpdateSubscriber,
        PostInitializeSubscriber,
        RenderSubscriber {

    private static PathingHelp instance;

    public static ModInfo info;
    public static String modID; //Edit your pom.xml to change this

    static {
        loadModInfo();
    }

    public static final Logger logger = LogManager.getLogger(modID); //Used to output to the console.

    private PathMenu pathMenu;
    private ArrayList<Path> paths;

    //This is used to prefix the IDs of various objects like cards and relics,
    //to avoid conflicts between different mods using the same name for things.
    public static String makeID(String id) {
        return modID + ":" + id;
    }

    //This will be called by ModTheSpire because of the @SpireInitializer annotation at the top of the class.
    public static void initialize() {
        instance = new PathingHelp();
    }

    public PathingHelp() {
        BaseMod.subscribe(this); //This will make BaseMod trigger all the subscribers at their appropriate times.
        logger.info(modID + " subscribed to BaseMod.");
    }

    @Override
    public void receivePostInitialize() {
        //This loads the image used as an icon in the in-game mods menu.
        Texture badgeTexture = new Texture("pathingHelp/images/badge.png");
        //Set up the mod information displayed in the in-game mods menu.
        //The information used is taken from your pom.xml file.

        //If you want to set up a config panel, that will be done here.
        //The Mod Badges page has a basic example of this, but setting up config is overall a bit complex.
        BaseMod.registerModBadge(badgeTexture, info.Name, info.Authors[0], info.Description, null);

        paths = new ArrayList<Path>();
        pathMenu = new PathMenu(paths);
    }

    /**
     * This determines the mod's ID based on information stored by ModTheSpire.
     */
    private static void loadModInfo() {
        Optional<ModInfo> infos = Arrays.stream(Loader.MODINFOS).filter((modInfo) -> {
            AnnotationDB annotationDB = Patcher.annotationDBMap.get(modInfo.jarURL);
            if (annotationDB == null)
                return false;
            Set<String> initializers = annotationDB.getAnnotationIndex().getOrDefault(SpireInitializer.class.getName(), Collections.emptySet());
            return initializers.contains(PathingHelp.class.getName());
        }).findFirst();
        if (infos.isPresent()) {
            info = infos.get();
            modID = info.ID;
        } else {
            throw new RuntimeException("Failed to determine mod info/ID based on initializer.");
        }
    }

    private boolean pathsNeedRefresh = true;

    @Override
    public void receivePostDungeonInitialize() {
        logger.info("Dungeon Initialized");

        logger.info("Dungeon:" + AbstractDungeon.map);

        pathsNeedRefresh = true;
/*
        StringBuilder str = new StringBuilder();

        str.append(AbstractDungeon.map);

        logger.info(str.toString());

 */
    }

    private static boolean inMap = false;
    private static int actNum = -1;

    @Override
    public void receivePostUpdate() {
        if (!CardCrawlGame.isInARun() || AbstractDungeon.screen != AbstractDungeon.CurrentScreen.MAP) {
            inMap = false;
            return;
        }
        pathMenu.update();

        if (actNum != AbstractDungeon.actNum) {
            pathsNeedRefresh = true;
            actNum = AbstractDungeon.actNum;
        }

        if (!inMap) {
            logger.info("Map opened");
            inMap = true;

            if (pathsNeedRefresh) {
                // ArrayList<Path> paths = new ArrayList<Path>();
                paths.clear();
                for (MapRoomNode startNode : AbstractDungeon.map.get(0)) {
                    paths.addAll(computeAllPaths(AbstractDungeon.map, startNode));
                /*
                for (Path path : computeAllPaths(AbstractDungeon.map, startNode)) {
                    logger.info("    " + path);
                }
                 */
                }
                logger.info("Computed " + paths.size() + " paths.");
                logger.info("  * Num rest options: " + Path.numRestOptions(paths));

                pathMenu.setPaths(this.paths);

                pathsNeedRefresh = false;
            }
            /*
            logger.info("Paths:");
            for (MapRoomNode startNode : AbstractDungeon.map.get(0)) {
                for (Path path : computeAllPaths(AbstractDungeon.map, startNode)) {
                    logger.info("    " + path);
                }
            }

             */
        }
    }

    public static ArrayList<Path> computeAllPaths(ArrayList<ArrayList<MapRoomNode>> map, MapRoomNode node) {
        ArrayList<ArrayList<MapRoomNode>> nodeLists = _computeAllPaths(map, node);

        ArrayList<Path> paths = new ArrayList<Path>();
        for (ArrayList<MapRoomNode> nodeList : nodeLists) {
            Collections.reverse(nodeList);
            paths.add(new Path(nodeList));
        }

        return paths;
    }

    public static ArrayList<ArrayList<MapRoomNode>> _computeAllPaths(ArrayList<ArrayList<MapRoomNode>> map, MapRoomNode node) {

        // If there are no edges, return a singleton
        ArrayList<ArrayList<MapRoomNode>> l = new ArrayList<ArrayList<MapRoomNode>>();

        // If the node is at the top of the map, return a singleton
        if (node.y == map.size() - 1) {
            ArrayList<MapRoomNode> endPath = new ArrayList<MapRoomNode>(Collections.singletonList(node));
            l.add(endPath);
        }
        // If the node is not at the top of the map, but there are no outgoing edges, that means this node isn't really in the map
        // (NB: This should only happen in the 0th row)
        else if (node.hasEdges()) {
            // For each edge this node leads to, compute all of its paths
            for (MapEdge edge : node.getEdges()) {
                MapRoomNode nextNode = map.get(edge.dstY).get(edge.dstX);
                ArrayList<ArrayList<MapRoomNode>> nextPaths = _computeAllPaths(map, nextNode);
                // For each path from the next node, add this node to the front of the path, and then add that path to l
                for (ArrayList<MapRoomNode> nextPath : nextPaths) {
                    nextPath.add(node);
                    l.add(nextPath);
                }
            }
        }

        return l;
    }

    @Override
    public void receiveRender(SpriteBatch sb) {
        if (CardCrawlGame.isInARun() && inMap) {
            instance.pathMenu.render(sb);
        }
    }
}