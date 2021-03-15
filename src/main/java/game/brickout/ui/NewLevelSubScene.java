package game.brickout.ui;

import com.almasb.fxgl.animation.Interpolators;
import com.almasb.fxgl.particle.ParticleEmitters;
import com.almasb.fxgl.particle.ParticleSystem;
import com.almasb.fxgl.scene.SubScene;
import javafx.geometry.Point2D;
import javafx.scene.paint.Color;
import javafx.util.Duration;

import static com.almasb.fxgl.dsl.FXGL.*;


public class NewLevelSubScene extends SubScene {

    private ParticleSystem particleSystem;

    public NewLevelSubScene(int level) {

        var emitter = ParticleEmitters.newRainEmitter(100);
      //  var emitter = ParticleEmitters.newFireEmitter(100);

        var t = texture("particles/moje.png", 256.0, 256.0);

        emitter.setSourceImage(t);
        emitter.setSize(256, 128);
        emitter.setEmissionRate(0.0005);
        emitter.setMaxEmissions(Integer.MAX_VALUE);
        emitter.setNumParticles(100);
        emitter.setVelocityFunction(i -> new Point2D(random(-100, 100), 0));
        emitter.setExpireFunction(i -> Duration.seconds(random(3, 4)));
    //    emitter.setBlendMode(getSettings().isExperimentalNative() ? BlendMode.SRC_OVER : BlendMode.ADD);

        particleSystem = new ParticleSystem();
        particleSystem.addParticleEmitter(emitter, getAppWidth() / 2.0 - 256, getAppHeight() / 2.0 - 150);

        var text = getUIFactoryService().newText("LEVEL " + level, Color.WHITE, 66);
        text.setTranslateX(getAppWidth() / 2.0  - 100);
        text.setTranslateY(getAppHeight() / 2.0 + 30);

        if (!getSettings().isExperimentalNative()) {
            getContentRoot().getChildren().addAll(particleSystem.getPane());
        }
        getContentRoot().getChildren().addAll(text);

        animationBuilder()
                .onFinished(() -> {
                    animationBuilder()
                            .onFinished(() -> getSceneService().popSubScene())
                            .delay(Duration.seconds(0.1))
                            .duration(Duration.seconds(0.7))
                            .interpolator(Interpolators.EXPONENTIAL.EASE_OUT())
                            .translate(getContentRoot())
                            .from(new Point2D(0, 0))
                            .to(new Point2D(1500, 0))
                            .buildAndPlay(this);
                })
                .delay(Duration.seconds(0.1))
                .duration(Duration.seconds(random(1.5, 2.3)))
                .interpolator(Interpolators.EXPONENTIAL.EASE_OUT())
                .translate(getContentRoot())
                .from(new Point2D(-1050, 0))
                .to(new Point2D(0, 0))
                .buildAndPlay(this);
    }

    @Override
    protected void onUpdate(double tpf) {
        particleSystem.onUpdate(tpf);
    }
}