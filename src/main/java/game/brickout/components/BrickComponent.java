package game.brickout.components;


import com.almasb.fxgl.entity.component.Component;


import static com.almasb.fxgl.dsl.FXGL.*;

public class BrickComponent extends Component{

    private static int size = 0;

    private int lives = 2;

    private boolean canBeHit = true;

//    private Color color;

    public BrickComponent(){
        size++;
    }

    public static int getSize(){
        return size;
    }

  /*  public BrickComponent(Color color) {
        this.color = color;
    }

    public Color getColor() {
        return color;
    }   */

    public void onHit() {
        if (!canBeHit)
            return;

     //   canBeHit = false;

        lives--;

        if (lives == 1) {
            //playHitAnimation();

            entity.getViewComponent().clearChildren();

          //  var colorName = entity.getString("color");

            entity.getViewComponent().addChild(texture("brick_blue_cracked.png"));
        } else if (lives == 0) {

            //    var colorName = entity.getString("color");
      /*      var t = texture("brick_blue_cracked.png");

            var textures = new ArrayList<Texture>();

            var t1 = t.subTexture(new Rectangle2D(0, 0, 32, 32));
            var t2 = t.subTexture(new Rectangle2D(32, 0, 32, 32));
            var t3 = t.subTexture(new Rectangle2D(32 + 32, 0, 32, 32));

            textures.add(t1);
            textures.add(t2);
            textures.add(t3);

            for (int i = 0; i < textures.size(); i++) {
                var te = textures.get(i);

                entityBuilder()
                        .at(entity.getPosition().add(i*32, 0))
                        .view(te)
                        .with(new ProjectileComponent(new Point2D(0, 1), random(550, 700)).allowRotation(false))
                        .with(new ExpireCleanComponent(Duration.seconds(0.7)).animateOpacity())
                        .buildAndAttach();
            }   */

            size--;
            entity.removeFromWorld();
        }
    }
}
