package com.aaa.spaceship;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import java.util.Random;

public class SpaceShip extends ApplicationAdapter {
    SpriteBatch batch;
    Texture background;
    Texture[] bird;
    int birdStateFlag = 0;
    float flyHeight;
    float fallingSpeed = 0;
    int gameStateFlag = 0;

    Texture topShip;
    Texture bottomShip;
    //velechina prostranctva mejdu trubami
    int spaceBetweenShips = 600;
    Random random;
    int shipSpeed = 3;
    int shipsNumber = 4;
    float shipX[] = new float[shipsNumber];
    float shipShift[] = new float[shipsNumber];
    float distanceBetweenShips;

    Circle birdCircle;
    Rectangle[] topShipRectangles;
    Rectangle[] bottomShipRectangles;
//    ShapeRenderer shapeRenderer;  // otobrajenie textur v treugolniki, krugi, kvadrati....
    int gameScore = 0;
    // index proidennogo ship
    int passedShipUndex =0;
    BitmapFont scoreFount;

    Texture gameOver;



    @Override
    public void create() {
        batch = new SpriteBatch();
        background = new Texture("background.jpg");
//        shapeRenderer = new ShapeRenderer();

        birdCircle = new Circle();
        topShipRectangles = new Rectangle[shipsNumber];
        bottomShipRectangles = new Rectangle[shipsNumber];


        bird = new Texture[2];
        bird[0] = new Texture("bird_wings_up.png");
        bird[1] = new Texture("bird_wings_down.png");

        topShip = new Texture("top_tube.png");
        bottomShip = new Texture("bottom_tube.png");
        random = new Random();
        scoreFount = new BitmapFont();
        scoreFount.setColor(Color.CYAN);
        scoreFount.getData().setScale(10);

        gameOver = new Texture("game_over.png");

        distanceBetweenShips = Gdx.graphics.getWidth() /2;

        initGame();
    }

    public void initGame() {
        flyHeight = Gdx.graphics.getHeight() / 2 - bird[0].getHeight() / 2;
        for (int i = 0; i < shipsNumber; i++) {
            shipX[i] = Gdx.graphics.getWidth() / 2 - topShip.getWidth() / 2 +
                    Gdx.graphics.getWidth() + i * distanceBetweenShips * 1.5f; // rastoyanie mejdu ship *1.5
            //dvijenia ship
            shipShift[i] = (random.nextFloat() - 0.6f) *
                    (Gdx.graphics.getHeight() - spaceBetweenShips - 500);
            topShipRectangles[i] = new Rectangle();
            bottomShipRectangles[i] = new Rectangle();
        }
    }

    @Override// fun loop
    public void render() {
        //set background
        batch.begin();
        batch.draw(background, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        if (gameStateFlag == 1) {
            Gdx.app.log("Game_score", String.valueOf(gameScore));

            if (shipX[passedShipUndex] < Gdx.graphics.getWidth() / 2) {
                gameScore ++;
                if (passedShipUndex < shipsNumber - 1) {
                    passedShipUndex ++;
                } else {
                    passedShipUndex = 0;
                }
            }
            if (Gdx.input.justTouched()) {
                //podbros ptici
                fallingSpeed = -25;
            }

            for (int i = 0; i < shipsNumber; i++) {
                if (shipX[i] < - topShip.getWidth()) {
                    shipX[i] = shipsNumber * distanceBetweenShips;
                } else {
                    shipX[i] -= shipSpeed;
                }
                batch.draw(topShip, shipX[i], Gdx.graphics.getHeight() / 2
                        + spaceBetweenShips / 2 + shipShift[i]);

                batch.draw(bottomShip, shipX[i], Gdx.graphics.getHeight() / 2
                        - spaceBetweenShips / 2 - bottomShip.getHeight() + shipShift[i]);

                topShipRectangles[i] = new Rectangle(shipX[i], Gdx.graphics.getHeight() / 2
                        + spaceBetweenShips / 2 + shipShift[i], topShip.getWidth(), topShip.getHeight());

                bottomShipRectangles[i] = new Rectangle(shipX[i], Gdx.graphics.getHeight() / 2
                        - spaceBetweenShips / 2 - bottomShip.getHeight() + shipShift[i], bottomShip.getWidth(),
                        bottomShip.getHeight());
            }

            if (flyHeight > 0) {
                //padenie ptici, chem nije ptica tem bistrei budet padat
                fallingSpeed++;
                flyHeight -= fallingSpeed;
            } else {
                gameStateFlag = 2;
            }

        } else if (gameStateFlag == 0) {
            if (Gdx.input.justTouched()) {
                Gdx.app.log("Tap", "Oops!");
                gameStateFlag = 1;
            }
        } else if (gameStateFlag == 2) {
            batch.draw(gameOver, Gdx.graphics.getWidth() / 2 - gameOver.getWidth() / 2,
                    Gdx.graphics.getHeight() / 2 - gameOver.getHeight() / 2);

            if (Gdx.input.justTouched()) {
                Gdx.app.log("Tap", "Oops!");
                gameStateFlag = 1;
                initGame();
                gameScore = 0;
                passedShipUndex = 0;
                fallingSpeed = 0;
            }
        }
        if (birdStateFlag == 0) {
            birdStateFlag = 1;
        } else {
            birdStateFlag = 0;
        }
        // ship_up location center
        batch.draw(bird[birdStateFlag], Gdx.graphics.getWidth() / 2 - bird[birdStateFlag].getWidth() / 2,
                flyHeight);
        scoreFount.draw(batch, String.valueOf(gameScore), 100, 200);
        batch.end();
        birdCircle.set(Gdx.graphics.getWidth() / 2, flyHeight + bird[birdStateFlag].getHeight()/2,
                bird[birdStateFlag].getWidth() / 2);
//        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
//        shapeRenderer.setColor(Color.CYAN);
//        shapeRenderer.circle(birdCircle.x, birdCircle.y, birdCircle.radius);
        for (int i = 0; i < shipsNumber; i++) {
//            shapeRenderer.rect(shipX[i], Gdx.graphics.getHeight() / 2
//                    + spaceBetweenShips / 2 + shipShift[i], topShip.getWidth(), topShip.getHeight());
//
//            shapeRenderer.rect(shipX[i], Gdx.graphics.getHeight() / 2
//                            - spaceBetweenShips / 2 - bottomShip.getHeight() + shipShift[i], bottomShip.getWidth(),
//                    bottomShip.getHeight());
            if (Intersector.overlaps(birdCircle, topShipRectangles[i]) ||
                    Intersector.overlaps(birdCircle, bottomShipRectangles[i])) {
                Gdx.app.log("Intersected",  "Bump!");
                gameStateFlag = 2;
            }
        }
//        shapeRenderer.end();
    }

    @Override
    public void dispose() { }
}
