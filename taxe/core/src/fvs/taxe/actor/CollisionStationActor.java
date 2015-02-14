package fvs.taxe.actor;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;
import gameLogic.map.CollisionStation;
import gameLogic.map.IPositionable;

public class CollisionStationActor extends Image {
	private static int width = 16;
    private static int height = 16;
    private static String notBrokenFileName = "junction_dot.png";
    private static String brokenFileName = "junction_dot_broken.png";
    private CollisionStation collisionStation;

    private CollisionStationActor(CollisionStation collisionStation, String imageFileName) {
        super(new Texture(Gdx.files.internal(imageFileName)));

        this.collisionStation = collisionStation;
        setSize(width, height);
        setPosition(collisionStation.getLocation().getX() - width / 2, collisionStation.getLocation().getY() - height / 2);
    }

    /**
     * Updating the image displayed so that it reflects it's un/broken state.
     */
    public void updateImage() {
        String imageFileName;
        if (collisionStation.isBroken()) {
            imageFileName = brokenFileName;
        } else {
            imageFileName = notBrokenFileName;
        }
        super.setDrawable(new SpriteDrawable(new Sprite(new Texture(imageFileName))));
    }

    public CollisionStation getCollisionStation() {
        return collisionStation;
    }

    public static CollisionStationActor createCollisionStationActor(CollisionStation collisionStation) {
        String imageFileName;
        if (collisionStation.isBroken()) {
            imageFileName = brokenFileName;
        } else {
            imageFileName = notBrokenFileName;
        }
        return new CollisionStationActor(collisionStation, imageFileName);
    }
}
