package pathingHelp.ui;

import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.helpers.Hitbox;
import com.megacrit.cardcrawl.helpers.HitboxListener;

public interface HitboxClickListener extends HitboxListener {

    @Override
    default void hoverStarted(Hitbox hb) {}

    @Override
    default void startClicking(Hitbox hb) {
        CardCrawlGame.sound.play("UI_CLICK_1");
    }
}