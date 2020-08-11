package com.vasciie.bkbl.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.vasciie.bkbl.MyGdxGame;
import com.vasciie.bkbl.gamespace.GameMap;
import com.vasciie.bkbl.gamespace.tools.SettingsPrefsIO;
import com.vasciie.bkbl.gui.Button;
import com.vasciie.bkbl.gui.GUIRenderer;
import com.vasciie.bkbl.gui.Label;

public class GameOverScreen implements Screen, GUIRenderer {

	MyGdxGame game;
	
	ShapeRenderer shape;
	SpriteBatch batch;
	BitmapFont font, bigFont, infoFont, resultsFont;
	OrthographicCamera cam;
	
	Label gameOver, winInfo, highscore;
	Label playersLeft, basketScore, totalScore, mainPlayerScore;
	Button newGame, quit;
	
	Texture basketBall;
	
	boolean isHighscore;
	
	private static final String highscoreText = "HIGHSCORE! Previous Score: ", playersLeftText = "Players Left: x", basketScoreText = "Basket Score: ", totalScoreText = "Total Score: ", mainPlayerScoreText = "Score From You: ";
	
	public GameOverScreen(MyGdxGame mg) {
		game = mg;
		
		cam = new OrthographicCamera();
		
		shape = new ShapeRenderer();
		batch = new SpriteBatch();
		font = new BitmapFont();
		font.getData().setScale(MyGdxGame.GUI_SCALE);
		resultsFont = new BitmapFont();
		resultsFont.getData().setScale(2);
		infoFont = new BitmapFont();
		infoFont.getData().setScale(2.3f);
		bigFont = new BitmapFont();
		bigFont.getData().setScale(4);
		
		basketBall = new Texture(Gdx.files.internal("application/basketball_ball_gameover.png"));
		
		newGame = new Button("New Game", font, Color.ORANGE.cpy().sub(0, 0.3f, 0, 1), true, true, this);
		quit = new Button("Quit", font, Color.ORANGE.cpy().sub(0, 0.3f, 0, 1), true, true, this);
		
		gameOver = new Label("Game Over!", bigFont, Color.RED, false, this);
		highscore = new Label(highscoreText, resultsFont, Color.GREEN, false, this);
		winInfo = new Label("You Win!", infoFont, Color.BLUE, false, this);
		
		playersLeft = new Label(playersLeftText, resultsFont, Color.WHITE, false, this);
		basketScore = new Label(basketScoreText, resultsFont, Color.WHITE, false, this);
		mainPlayerScore = new Label(mainPlayerScoreText, resultsFont, Color.WHITE, false, this);
		totalScore = new Label(totalScoreText, resultsFont, Color.WHITE, false, this);
	}
	
	private boolean checkChallenge() {
		GameMap map = game.getMap();
		
		return (map.isChallenge() && !map.getChallenges().getBrokenChallenge().isLosable() && (map.getChallenges().getBrokenChallenge().getId().equals("team_score") || !map.getChallenges().containsCurrentChallenge("team_score")) || !map.isChallenge());
	}
	
	@Override
	public void show() {
		GameMap map = game.getMap();
		boolean ch = checkChallenge();
		
		if(ch && map.getTeammates().size() > 0 && map.getTeammates().contains(map.getMainPlayer()) && (map.getTeamScore() > map.getOppScore() || map.getOpponents().size() == 0)) {
			winInfo.setText("You Win!");
			winInfo.setColor(Color.BLUE);
		}else if(ch && map.getTeamScore() == map.getOppScore() && map.getTeammates().size() > 0 && map.getTeammates().contains(map.getMainPlayer())) {
			winInfo.setText("Draw Game!");
			winInfo.setColor(Color.ORANGE);
		}else {
			winInfo.setText("You Lose!");
			winInfo.setColor(Color.RED);
		}
		
		int basketDiff = ch && map.getTeammates().contains(map.getMainPlayer()) ? Math.max(map.getTeamScore() - map.getOppScore(), 0) : 0;
		int teammatesLeft = map.getTeammates().size();
		int mainPlayerSc = map.getMainPlayerScore();
		int total = basketDiff * 10 * teammatesLeft + mainPlayerSc * 10;
		
		
		String level;
		if(map.isChallenge())
			level = game.getChallengeDataStr();
		else level = map.getDifficulty() + "" + game.game.getAmount();
		
		int prevScore = SettingsPrefsIO.readSettingInteger(level);
		if(prevScore < total) {
			if(prevScore > 0) {
				isHighscore = true;
				highscore.setText(highscoreText + prevScore);
			}else isHighscore = false;
			
			SettingsPrefsIO.writeSettingInt(level, total);
			SettingsPrefsIO.flush();
		}else isHighscore = false;
		
		playersLeft.setText(playersLeftText + teammatesLeft);
		basketScore.setText(basketScoreText + map.getTeamScore() + "-" + map.getOppScore());
		mainPlayerScore.setText(mainPlayerScoreText + mainPlayerSc);
		totalScore.setText(totalScoreText + (total));
	}

	@Override
	public void render(float delta) {
		newGame.update();
		quit.update();
		
		gameOver.update();
		if(isHighscore)
			highscore.update();
		winInfo.update();
		playersLeft.update();
		basketScore.update();
		mainPlayerScore.update();
		totalScore.update();
		
		if(newGame.justReleased()) {
			game.game.reset();
			game.setScreen(game.game);
			return;
		}else if(quit.justReleased()) {
			game.setScreen(game.main);
			game.game.reset();
			game.getMap().getChallenges().reset();
			game.main.sendWebPageMessage();
			return;
		}
		
		
		batch.begin();
		batch.draw(basketBall, cam.viewportWidth / 2 - basketBall.getWidth() / 2 - 15, cam.viewportHeight / 2 - basketBall.getHeight() / 2 - 15);
		batch.end();
		
		newGame.draw();
		quit.draw();
		
		gameOver.draw();
		highscore.draw();
		winInfo.draw();
		playersLeft.draw();
		basketScore.draw();
		mainPlayerScore.draw();
		totalScore.draw();
	}

	@Override
	public void resize(int width, int height) {
		cam.setToOrtho(false, width, height);
		
		newGame.setSize(game.pixelXByCurrentSize(223 * MyGdxGame.GUI_SCALE), game.pixelYByCurrentSize(30 * MyGdxGame.GUI_SCALE));
		newGame.setPos(width / 4 - newGame.getWidth() / 2 - 60, game.pixelYByCurrentSize(150 / MyGdxGame.GUI_SCALE));
		quit.setSize(game.pixelXByCurrentSize(223 * MyGdxGame.GUI_SCALE), game.pixelYByCurrentSize(30 * MyGdxGame.GUI_SCALE));
		quit.setPos(width * 3 / 4 - newGame.getWidth() / 2 + 60, game.pixelYByCurrentSize(150 / MyGdxGame.GUI_SCALE));
		
		gameOver.setPos(width / 2, height - 40);
		winInfo.setPos(width / 2, gameOver.getY() - 39);
		highscore.setPos(width / 2, winInfo.getY() - 39);
		
		playersLeft.setPos(width / 2 - 13, height / 2 + 56);
		basketScore.setPos(width / 2 - 13, playersLeft.getY() - 39);
		mainPlayerScore.setPos(width / 2 - 13,  basketScore.getY() - 39);
		totalScore.setPos(width / 2 - 13, mainPlayerScore.getY() - 78);
	}

	@Override
	public void pause() {
		
		
	}

	@Override
	public void resume() {
		resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight()); //Development purposes!
		
	}

	@Override
	public void hide() {
		
		
	}

	@Override
	public void dispose() {
		batch.dispose();
		shape.dispose();
		font.dispose();
		bigFont.dispose();
		infoFont.dispose();
		resultsFont.dispose();
		basketBall.dispose();
	}

	@Override
	public SpriteBatch getSpriteBatch() {
		return batch;
	}

	@Override
	public ShapeRenderer getShapeRenderer() {
		return shape;
	}

	@Override
	public OrthographicCamera getCam() {
		return cam;
	}

}
