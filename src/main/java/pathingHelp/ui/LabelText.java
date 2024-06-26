package pathingHelp.ui;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.helpers.FontHelper;

public class LabelText implements Renderable {

    private final String label;
    private final float x, y;

    LabelText(@SuppressWarnings("SameParameterValue") float x, float y, String label) {
        this.x = x;
        this.y = y;
        this.label = label;
    }

    @Override
    public void render(SpriteBatch sb) {
        FontHelper.renderFontRightAligned(sb, FontHelper.tipBodyFont, label, x, y + 24, PathMenu.FONT_COLOR);
    }
}