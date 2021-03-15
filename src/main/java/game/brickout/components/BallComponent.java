package game.brickout.components;

import com.almasb.fxgl.core.math.Vec2;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.physics.PhysicsComponent;


import static com.almasb.fxgl.dsl.FXGL.*;
import static game.brickout.BrickoutType.BAT;
import static java.lang.Math.abs;
import static java.lang.Math.signum;

public class BallComponent extends Component{

    private static final int BALL_MIN_SPEED = 300;
 //   private static final int BALL_SLOW_SPEED = 100;

    private PhysicsComponent physics;
    private boolean checkVelocityLimit = false;

    boolean follow = true;

    @Override
    public void onUpdate(double tpf) {
        if (checkVelocityLimit) {
            limitVelocity();
        }

        if(follow)
            getEntity().setX(getGameWorld().getSingleton(BAT).getComponent(BatComponent.class).getxBat() + 70);
    }

    private void limitVelocity() {
        double x = physics.getVelocityX();
        double y = physics.getVelocityY();

        if (abs(x) < BALL_MIN_SPEED) {
            var signX = signum(physics.getVelocityX());

            if (signX == 0.0)
                signX = 1.0;

            physics.setVelocityX(signX * BALL_MIN_SPEED);
        }

        if (abs(y) < BALL_MIN_SPEED) {
            var signY = signum(physics.getVelocityY());

            if (signY == 0.0)
                signY = -1.0;

            physics.setVelocityY(signY * BALL_MIN_SPEED);
        }
    }

    public void release() {
        if(!physics.isMovingY()) {
            checkVelocityLimit = true;
            physics.setBodyLinearVelocity(new Vec2(random(-10, 10), random(5, 10)));
            follow = false;
        }
    }
}
