package pathingHelp.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;

import static pathingHelp.PathingHelp.logger;

public class Selector extends ClickableUIElement {

    private final PathMenu.SelectorId id;
    private final boolean increment;

    private PathMenu pathMenu;

    Selector(PathMenu parent, Text txtBox, PathMenu.SelectorId id, Boolean i, Texture texture, float x, float y) {
        super(texture, x, y);
        this.pathMenu = parent;
        this.id = id;
        this.increment = i;
    }

    @Override
    protected void onClick() {
        pathMenu.selectorChangeValue(id, increment);
    }
}
