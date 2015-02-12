package fvs.taxe.actor;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;

import gameLogic.map.IPositionable;

public class StationActor extends Image {
    private static int width = 20;
    private static int height = 20;

    private static String normalImage = "station_dot.png";
    private static String startImage = "station_dot_start.png";
    private static String finishImage = "station_dot_finish.png";
    private static String highlightedImage = "station_dot_highlight.png";

    public StationActor(IPositionable location) {
        super(new Texture(Gdx.files.internal(normalImage)));

        setSize(width, height);
        setPosition(location.getX() - width / 2, location.getY() - height / 2);
    }
    
    public void setNormalImage() {
    	super.setDrawable(new SpriteDrawable(new Sprite(new Texture(normalImage))));
    }
    
    public void setHighlightedImage() {
    	super.setDrawable(new SpriteDrawable(new Sprite(new Texture(highlightedImage))));
    }

    public void setStartImage() {
        super.setDrawable(new SpriteDrawable(new Sprite(new Texture(startImage))));
    }

    public void setFinishImage() {
        super.setDrawable(new SpriteDrawable(new Sprite(new Texture(finishImage))));
    }
}
