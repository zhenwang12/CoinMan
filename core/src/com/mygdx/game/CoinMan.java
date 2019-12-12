package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;

import java.util.ArrayList;
import java.util.Random;

public class CoinMan extends ApplicationAdapter {
    SpriteBatch batch;
    Texture background;
    Texture[] man;
    Texture dizzy;
    int manState = 0;
    int pause = 0;
    float gravity = 0.4f;
    float velocity = 0;
    int manY = 0;
    Rectangle manRectangle;
    int score;
    int gameState = 0;

    Random mRandom;
    BitmapFont mBitmapFont;
    BitmapFont mStartAndOverFont;

    ArrayList<Integer> coinXs = new ArrayList<>();
    ArrayList<Integer> coinYs = new ArrayList<>();
    ArrayList<Rectangle> coinRectangle = new ArrayList<>();
    Texture coin;
    int coinCount;

    ArrayList<Integer> bombXs = new ArrayList<>();
    ArrayList<Integer> bombYs = new ArrayList<>();
    ArrayList<Rectangle> bombRectangle = new ArrayList<>();
    Texture bomb;
    int bombCount;

    @Override
    public void create() {
        batch = new SpriteBatch();
        initData();
    }

    @Override
    public void render() {
        batch.begin();
        batch.draw(background, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        if (gameState == 1) {
            mStartAndOverFont.setColor(Color.CLEAR);
            startGame();
        } else if (gameState == 0) {
            mStartAndOverFont.draw(batch, "Touch to Start", Gdx.graphics.getWidth() / 2 - 150, Gdx.graphics.getHeight() / 2);
            if (Gdx.input.justTouched()) {
                gameState = 1;
            }
        } else if (gameState == 2) {
            mStartAndOverFont.setColor(Color.BLACK);
            //mStartAndOverFont.draw(batch, "Game Over!", (Gdx.graphics.getWidth() - 250) / 2, Gdx.graphics.getHeight() / 2);
            if (Gdx.input.justTouched()) {
                gameState = 1;
                resetGame();
            }
        }

        if (gameState == 2) {
            batch.draw(dizzy, (Gdx.graphics.getWidth() - man[0].getWidth()) / 2, manY);
        } else {
            batch.draw(man[manState], (Gdx.graphics.getWidth() - man[0].getWidth()) / 2, manY);
        }

        manRectangle = new Rectangle((Gdx.graphics.getWidth() - man[0].getWidth()) / 2, manY, man[manState].getWidth(), man[manState].getHeight());

        for (int i = 0; i < coinRectangle.size(); i++) {
            if (Intersector.overlaps(manRectangle, coinRectangle.get(i))) {
                score++;
                coinRectangle.remove(i);
                coinXs.remove(i);
                coinYs.remove(i);
                break;
            }
        }

        for (int i = 0; i < bombRectangle.size(); i++) {
            if (Intersector.overlaps(manRectangle, bombRectangle.get(i))) {
                gameState = 2;
            }
        }

        mBitmapFont.draw(batch, String.valueOf(score), 100, 200);

        batch.end();
    }

    @Override
    public void dispose() {
        batch.dispose();
    }

    private void initData() {
        background = new Texture("bg.png");
        man = new Texture[4];

        man[0] = new Texture("frame-1.png");
        man[1] = new Texture("frame-2.png");
        man[2] = new Texture("frame-3.png");
        man[3] = new Texture("frame-4.png");

        coin = new Texture("coin.png");
        bomb = new Texture("bomb.png");
        dizzy = new Texture("dizzy-1.png");
        mRandom = new Random();
        mBitmapFont = new BitmapFont();
        mBitmapFont.setColor(Color.BLACK);
        mBitmapFont.getData().setScale(10);

        mStartAndOverFont = new BitmapFont();
        mStartAndOverFont.setColor(Color.BLACK);
        mStartAndOverFont.getData().setScale(6);
    }

    private void animationForManMoving() {
        if (pause < 4) {
            pause++;
        } else {
            pause = 0;
            manState = (manState + 1) % 3;
        }
    }

    private void generateCoinsOnScreen() {
        if (coinCount < 100) {
            coinCount++;
        } else {
            coinCount = 0;
            makeCoins();
        }

        coinRectangle.clear();
        for (int i = 0; i < coinYs.size(); i++) {
            batch.draw(coin, coinXs.get(i), coinYs.get(i));
            coinXs.set(i, coinXs.get(i) - 6);
            coinRectangle.add(new Rectangle(coinXs.get(i), coinYs.get(i), coin.getWidth(), coin.getHeight()));
        }
    }

    private void makeCoins() {
        float height = mRandom.nextFloat() * Gdx.graphics.getHeight() + man[0].getHeight();
        coinYs.add((int) height);
        coinXs.add(Gdx.graphics.getWidth());
    }

    private void generateBombOnScreen() {
        if (bombCount < 220) {
            bombCount++;
        } else {
            bombCount = 0;
            makeBombs();
        }

        bombRectangle.clear();
        for (int i = 0; i < bombXs.size(); i++) {
            batch.draw(bomb, bombXs.get(i), bombYs.get(i));
            bombXs.set(i, bombXs.get(i) - 8);
            bombRectangle.add(new Rectangle(bombXs.get(i), bombYs.get(i), bomb.getWidth(), bomb.getHeight()));
        }
    }

    private void makeBombs() {
        float height = mRandom.nextFloat() * Gdx.graphics.getHeight() + man[0].getHeight();
        bombYs.add((int) height);
        bombXs.add(Gdx.graphics.getWidth());
    }

    private void resetGame() {
        manY = 0;
        score = 0;
        velocity = 0;
        coinXs.clear();
        coinYs.clear();
        coinRectangle.clear();
        coinCount = 0;
        bombXs.clear();
        bombYs.clear();
        bombRectangle.clear();
        bombCount = 0;
    }

    private void startGame() {
        generateCoinsOnScreen();
        generateBombOnScreen();
        if (Gdx.input.isTouched()) {
            velocity = -9;
        }

        animationForManMoving();

        velocity += gravity;
        manY -= velocity;

        if (manY <= 0) {
            manY = 0;
        }

        if (manY >= Gdx.graphics.getHeight() - man[0].getHeight()) {
            manY = Gdx.graphics.getHeight() - man[0].getHeight();
        }
    }
}
