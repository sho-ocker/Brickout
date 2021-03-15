package game.brickout.components;

import com.almasb.fxgl.core.math.Vec2;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.physics.PhysicsComponent;


import static com.almasb.fxgl.dsl.FXGL.getAppWidth;

public class BatComponent extends Component{

    private static final int BAT_SPEED = 1050;
    private static final float BOUNCE_FACTOR = 1.5f;
    private static final float SPEED_DECAY = 0.66f;

    private PhysicsComponent physics;
    private float speed = BAT_SPEED;

    private Vec2 velocity = new Vec2();
    private double xBat;

    public double getxBat() {
        return xBat;
    }

    @Override
    public void onUpdate(double tpf) {
        xBat = getEntity().getX();

        speed = BAT_SPEED * (float)tpf;

        velocity.mulLocal(SPEED_DECAY);

        if (entity.getX() < 0) {
            velocity.set(BOUNCE_FACTOR * (float) -entity.getX(), 0);
        } else if (entity.getRightX() > getAppWidth()) {
            velocity.set(BOUNCE_FACTOR * (float) -(entity.getRightX() - getAppWidth()), 0);
        }

        physics.setBodyLinearVelocity(velocity);
    }

    public void left() {
        velocity.set(-speed, 0);
    }

    public void right() {
        velocity.set(speed, 0);
    }

    public void stop() {
        entity.setScaleX(1);
        entity.setScaleY(1);
    }
}
