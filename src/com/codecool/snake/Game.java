package com.codecool.snake;

import com.codecool.snake.entities.GameEntity;
import com.codecool.snake.entities.enemies.CirclingEnemy;
import com.codecool.snake.entities.enemies.HomingEnemy;
import com.codecool.snake.entities.enemies.SimpleEnemy;
import com.codecool.snake.entities.powerups.HeartPowerUp;
import com.codecool.snake.entities.powerups.SimplePowerUp;
import com.codecool.snake.entities.powerups.SpeedUpPowerUp;
import com.codecool.snake.entities.snakes.Snake;
import com.codecool.snake.eventhandler.InputHandler;
import com.sun.javafx.geom.Vec2d;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.*;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

public class Game extends Pane {

    public Snake snake = null;
    private GameTimer gameTimer = new GameTimer();
    private ArrayList<GameEntity> sprites = new ArrayList<>();
    private Random rnd = new Random();


    Game() {
        Globals.getInstance().game = this;
        Globals.getInstance().display = new Display(this);
        Globals.getInstance().setupResources();

        init();
    }

    private void init() {
        spawnSnake();
        spawnEnemies(4);
        spawnPowerUps(4);

        GameLoop gameLoop = new GameLoop(snake);
        Globals.getInstance().setGameLoop(gameLoop);
        gameTimer.setup(gameLoop::step);
        gameTimer.play();
    }

    void start() {
        setupInputHandling();
        Globals.getInstance().startGame();
    }

    private void spawnSnake() {
        snake = new Snake(new Vec2d(500, 500));
    }

    void spawnEnemies(int numberOfEnemies) {
        Vec2d snakeHeadPos = snake.head.getSnakeHeadPosition();
        for (int i = 0; i < numberOfEnemies; ++i) {
            GameEntity temp;
            int x = rnd.nextInt(3);
            if (x == 0) {
                temp = new SimpleEnemy(snakeHeadPos);
            } else if (x == 1) {
                temp = new CirclingEnemy(snakeHeadPos);
            } else {
                temp = new HomingEnemy(snakeHeadPos);
            }
            sprites.add(temp);
        }
    }

    void spawnPowerUps(int numberOfPowerUps) {
        GameEntity temp;
        for (int i = 0; i < numberOfPowerUps; ++i) {
            temp = new SimplePowerUp();
            sprites.add(temp);
            if (i % 3 == 0) {
                temp = new HeartPowerUp();
                sprites.add(temp);
            }
        }
        temp = new SpeedUpPowerUp();
        sprites.add(temp);
    }

    private void setupInputHandling() {
        Scene scene = getScene();
        scene.setOnKeyPressed(event -> InputHandler.getInstance().setKeyPressed(event.getCode()));
        scene.setOnKeyReleased(event -> InputHandler.getInstance().setKeyReleased(event.getCode()));
    }

    void cleanUp() {
        Iterator snakeBody = this.snake.body.getList().iterator();
        Iterator sprite = this.sprites.iterator();

        while (snakeBody.hasNext()) {
            ((GameEntity) snakeBody.next()).destroy();
        }
        this.snake.body.clear();

        while (sprite.hasNext()) {
            ((GameEntity) sprite.next()).destroy();
        }
        this.snake.score = 0;
        this.snake.speed = 2;
        this.snake.life.resetHealth(this.snake.startingHealth);
    }

    void setSnake() {
        this.snake.head.setX(500);
        this.snake.head.setY(500);
        snake.addPart(4);

        this.snake.head.setRotationToDefault();
        Globals.getInstance().startGame();
        spawnEnemies(4);
        spawnPowerUps(4);
    }

    public void setTableBackground(Image tableBackground) {
        setBackground(new Background(new BackgroundImage(tableBackground,
                BackgroundRepeat.REPEAT, BackgroundRepeat.REPEAT,
                BackgroundPosition.CENTER, BackgroundSize.DEFAULT)));
    }

}
