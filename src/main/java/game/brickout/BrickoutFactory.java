package game.brickout;

import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.EntityFactory;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.Spawns;
import com.almasb.fxgl.entity.components.IrremovableComponent;
import com.almasb.fxgl.entity.components.TimeComponent;
import com.almasb.fxgl.physics.BoundingShape;
import com.almasb.fxgl.physics.BoxShapeData;
import com.almasb.fxgl.physics.HitBox;
import com.almasb.fxgl.physics.PhysicsComponent;
import com.almasb.fxgl.physics.box2d.dynamics.BodyDef;
import com.almasb.fxgl.physics.box2d.dynamics.BodyType;
import com.almasb.fxgl.physics.box2d.dynamics.FixtureDef;
import game.brickout.components.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;


import static com.almasb.fxgl.dsl.FXGL.*;
import static game.brickout.BrickoutType.*;


public class BrickoutFactory implements EntityFactory {

    @Spawns("background")
    public Entity newBackground(SpawnData data) {
        return entityBuilder(data)
                .zIndex(-1)
                .type(BACKGROUND)
                .with(new IrremovableComponent())
                .with(new BackgroundStarsComponent(
                        texture("background/as1.jpg")
                      //  texture("background/as.jpg")
                      //  texture("background/as2.jpg")
                ))
                .build();
    }

    @Spawns("brick")
    public Entity newBrick(SpawnData data) {
      //  var color = Color.valueOf(data.<String>get("color").toUpperCase());

        return entityBuilder(data)
                .type(BRICK)
                .bbox(new HitBox(BoundingShape.box(96, 32)))
             //   .viewWithBBox(new Rectangle(96, 32, Color.RED))
                .collidable()
             //   .viewWithBBox(texture("brick_blue.png", 96, 32))
                .with(new PhysicsComponent())
                .with(new BrickComponent())
                .build();
    }

    @Spawns("bat")
    public Entity newBat(SpawnData data) {
        PhysicsComponent physics = new PhysicsComponent();
        physics.setBodyType(BodyType.KINEMATIC);

        return entityBuilder(data)
                .type(BAT)
                .at(getAppWidth() / 2.0 - 50, getAppHeight() - 70)
                .viewWithBBox(texture("bat.png", 464 / 3.0, 102 / 3.0))
                .scaleOrigin(464 / 3.0 / 2, 0)
                .collidable()
                .with(physics)
             //   .with(new EffectComponent())
                .with(new BatComponent())
                .build();
    }

    @Spawns("ball")
    public Entity newBall(SpawnData data) {
        PhysicsComponent physics = new PhysicsComponent();
        physics.setBodyType(BodyType.DYNAMIC);
        physics.setFixtureDef(new FixtureDef().restitution(1f).density(0.03f));

        var bd = new BodyDef();
        bd.setType(BodyType.DYNAMIC);
        bd.setFixedRotation(true);

        physics.setBodyDef(bd);

        var e = entityBuilder(data)
                .type(BALL)
                .bbox(new HitBox(BoundingShape.circle(64)))
                .view("ball.png")
                .collidable()
                .with(physics)
              //  .with(new TimeComponent())
             //   .with(new EffectComponent())
                .with(new BallComponent())
                .scaleOrigin(0, 0)
                .scale(0.1, 0.1)
                .build();

        return e;
    }
    @Spawns("line")
    public Entity newLine(SpawnData data){
        return entityBuilder(data)
                .type(LINE)
                .bbox(new HitBox(BoundingShape.box(20, 100)))
                .at(getAppWidth() / 2.0 - 50, getAppHeight() - 150)
                .with(new LineComponent())
                .build();
    }
}
