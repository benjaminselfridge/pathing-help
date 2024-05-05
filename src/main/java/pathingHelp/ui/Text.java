package pathingHelp.ui;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.helpers.FontHelper;

import static pathingHelp.PathingHelp.logger;

public class Text implements Renderable {

    private int val;
    private final PathMenu.SelectorId id;
    private final float x, y;

    Text(PathMenu.SelectorId id, float x, float y, int n) {
        this.id = id;
        this.val = n;
        this.x = x;
        this.y = y;
    }

    public void setVal(int n) {
        this.val = n;
    }

    public void render(SpriteBatch sb) {
        /* String weight = String.format("%.1f", WeightedPaths.weights.get(nodeType)); */
        String txt;
        if (this.val == -1) {
            txt = "Any";
        } else {
            txt = Integer.toString(this.val);
        }
        FontHelper.renderFontCentered(sb, FontHelper.tipBodyFont, txt, x, y + 24, PathMenu.FONT_COLOR);
    }
}
