package game.brickout.components;

import com.almasb.fxgl.core.math.Vec2;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.physics.PhysicsComponent;


public class LineComponent extends Component {

    private static final int BAT_SPEED = 1050;

    private PhysicsComponent physics;
    private float speed = BAT_SPEED;

    private Vec2 velocity = new Vec2();

    @Override
    public void onUpdate(double tpf) {
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
