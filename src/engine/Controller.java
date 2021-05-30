package engine;


import exceptions.*;
import javafx.scene.Node;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.layout.Background;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.Window;
import model.cards.Card;
import model.cards.minions.Minion;
import model.cards.spells.AOESpell;
import model.cards.spells.FieldSpell;
import model.cards.spells.HeroTargetSpell;
import model.cards.spells.LeechingSpell;
import model.cards.spells.MinionTargetSpell;
import model.cards.spells.Polymorph;
import model.cards.spells.Spell;
import model.heroes.*;
import view.View;



































import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectOutputStream.PutField;
import java.util.ArrayList;



public class Controller extends Application implements EventHandler<javafx.event.ActionEvent>,GameListener {

	private Game model;
	private View view;
	private HBox bottomView;
	private HBox topView;
	private HBox p1FieldView, p2FieldView;
	private Hero player1,player2;
	private Button endTurn,p1Hero,p2Hero,p1HeroPower,p2HeroPower;
	private boolean attackPressed,endflag;
	private ArrayList<Button> p1Field,p2Field,p1Hand,p2Hand;
	private boolean heroPowerPressed;
	private Button curCard, curAttacker, curHero;
	private int lastHeroHandSize, lastOppHandSize;
	private String curAttackerhp, curAttackeratt ;
	private LeechingSpell curLeech;
	private MinionTargetSpell curMinionTargetSpell;
	private Spell hybridSpell;
	private boolean backTurned;
	private Button deathButton;
	private Button p1Deck,p2Deck,p1Mana,p2Mana,popUpCard;
	private Label p1Label , p2Label;

	 //private GameListener gameEnding;

	public Controller(Hero player1, Hero player2,View view) throws FullHandException, CloneNotSupportedException, IOException {
		this.player1=player1;
		this.player2=player2;
		model = new Game(player1,player2);
		this.view = view;
		view.backgroundSound("sounds/Don_t-Let-Your-Guard-Down.mp3",0.2);
		
		
		model.setListener(this);

		if(model.getCurrentHero() == player2)
			endflag = true;

		p2Field = new ArrayList<Button>();
		p1Field = new ArrayList<Button>();


		bottomView =  view.getHeroHand();
		topView =  view.getOppHand();
		p1FieldView = view.getHeroField();
		p2FieldView = view.getOppField();
		
	


		p1Hand = new ArrayList<Button>();
		p2Hand = new ArrayList<Button>();
		deathButton = new Button();
		deathButton.setLayoutX(1149);
		deathButton.setLayoutY(263);
		deathButton.setPrefSize(100, 100);
		deathButton.setVisible(false);
		deathButton.setFont(Font.font("verdana", FontWeight.BOLD, FontPosture.REGULAR, 15));
		deathButton.setAlignment(Pos.TOP_LEFT);
		deathButton.setTextFill(Color.WHITE);
		
		
		popUpCard = new Button();
		popUpCard.setLayoutX(26);
		popUpCard.setLayoutY(263);
		popUpCard.setPrefSize(120, 140);
		popUpCard.setVisible(false);
		popUpCard.setFont(Font.font("verdana", FontWeight.BOLD, FontPosture.REGULAR, 15));
		popUpCard.setAlignment(Pos.TOP_LEFT);
		popUpCard.setTextFill(Color.WHITE);
		
		
		
	    p1Label = new Label();
	    p1Label.setFont(Font.font("verdana", FontWeight.BOLD, FontPosture.REGULAR, 17));
	    p1Label.setTextFill(Color.WHITE);
	    p1Label.setText(player1.getName());
	    p1Label.setAlignment(Pos.CENTER_RIGHT);
	    p1Label.setLayoutX(400);
	    p1Label.setLayoutY(539);
	    p1Label.setVisible(false);
	    
	    p2Label = new Label();
	    p2Label.setFont(Font.font("verdana", FontWeight.BOLD, FontPosture.REGULAR, 17));
	    p2Label.setTextFill(Color.WHITE);
	    p2Label.setText(player2.getName());
	    p2Label.setAlignment(Pos.CENTER_RIGHT);
	    p2Label.setLayoutX(400);
	    p2Label.setLayoutY(150);
	    p2Label.setVisible(false);
		


		//defining buttons
		
		p1Deck=new Button();
		view.putImage("images/deckCards", p1Deck);
		p1Deck.setText(player1.getDeck().size()+"");
		
		p1Deck.setOnMouseEntered( e ->{ ;
		view.putGlowAnimation(p1Deck, Color.GOLD) ; 
	});
		p1Deck.setOnMouseExited( e -> {;
		view.putGlowAnimation(p1Deck, null) ; 
	});
		
		p2Deck= new Button();
		view.putImage("images/deckCards", p2Deck);
		p2Deck.setText(player2.getDeck().size()+"");
		
		p2Deck.setOnMouseEntered( e ->{ ;
		view.putGlowAnimation(p2Deck, Color.GOLD) ; 
	});
		p2Deck.setOnMouseExited( e -> {;
		view.putGlowAnimation(p2Deck, null) ; 
	});
		
		p1Mana = new Button();
		view.putImage("images/mana_crystal", p1Mana);
		p1Mana.setText(player1.getCurrentManaCrystals()+"");
		
		p2Mana= new Button();
		view.putImage("images/mana_crystal", p2Mana);
		p2Mana.setText(player2.getCurrentManaCrystals()+"");
		
		endTurn = new Button();
		endTurn.setOnAction(this);
		view.putGlowAnimation(endTurn, Color.WHITE);
		p1Hero = new Button();
		p1Hero.setOnAction(e -> {
			if(hybridSpell != null)
			{
				try {
					castHeroTargetSpell((HeroTargetSpell) hybridSpell, player1);
					hybridSpell = null;
				} catch (Exception e1) {
					view.putSoundInPlay(e1 , model.getCurrentHero());
					AlertBox.display("error", e1.getMessage());
				}
			}

			else if (model.getCurrentHero() instanceof Mage && heroPowerPressed) {
				try {
					((Mage)(model.getCurrentHero())).useHeroPower(player1);
					view.putSound("sounds/heroPower1.mp3", 3,0.5);
					p1Hero.setText(player1.getCurrentHP() + "");
					heroPowerPressed=false;
					p1Mana.setText(player1.getCurrentManaCrystals()+"");
					p2Mana.setText(player2.getCurrentManaCrystals()+"");
				}
				catch (Exception e7){
					view.putSoundInPlay(e7 , model.getCurrentHero());
					AlertBox.display("Error", e7.getMessage());
				}
			}

			else if (model.getCurrentHero() instanceof Priest && heroPowerPressed) {
				try {
					((Priest)(model.getCurrentHero())).useHeroPower(player1);
					view.putSound("sounds/heroPower1.mp3", 3,0.5);
					p1Hero.setText(player1.getCurrentHP() + "");
					heroPowerPressed=false;
					p1Mana.setText(player1.getCurrentManaCrystals()+"");
					p2Mana.setText(player2.getCurrentManaCrystals()+"");
				}
				catch (Exception e7){
					view.putSoundInPlay(e7 , model.getCurrentHero());
					AlertBox.display("Error", e7.getMessage());
				}
			}

			else{
				if(!endflag)
				{
					CannotAttackException h9 = new CannotAttackException();
					view.putSoundInPlay(h9 , model.getCurrentHero());
					AlertBox.display("Error", "Cannot attack yourself");
					curCard = null;
					attackPressed = false;
					curHero = null;
				}
				else
				{
					if(curCard != null)
					{
						view.putGlowAnimation(p1Hero, Color.RED) ; 
						int attackerind = p2Field.indexOf(curCard);
						Minion attacker = model.getCurrentHero().getField().get(attackerind);
						try {
							model.getCurrentHero().attackWithMinion(attacker, model.getOpponent());
							view.putSound("sounds/hero_weapon_draw.mp3", 2, 0.5);
							p1Hero.setText(model.getOpponent().getCurrentHP()+"");
							curCard = null;
							attackPressed = false;
							curHero = null;
						
						} catch (Exception e1) {
							view.putSoundInPlay(e1 , model.getCurrentHero());
							AlertBox.display("Error", e1.getMessage());
							curCard = null;
							attackPressed = false;
							curHero = null;
							view.putGlowAnimation(p1Hero, Color.WHITE) ;
						}
					}
					else
					{
						AlertBox.display("Error", "Choose a minion to attack with");
						curCard = null;
						attackPressed = false;
						curHero = null;
					}
				}
			}});

		p2Hero = new Button();
		p2Hero.setOnAction(e -> {
			if(hybridSpell != null)
			{
				try {
					castHeroTargetSpell((HeroTargetSpell) hybridSpell, player2);
					hybridSpell = null;
				} catch (Exception e1) {
					AlertBox.display("Error", e1.getMessage());
				}
			}
			else if (model.getCurrentHero() instanceof Mage && heroPowerPressed) {
				try {
					((Mage)(model.getCurrentHero())).useHeroPower(player2);
					view.putSound("sounds/heroPower1.mp3", 3,0.5);
					p2Hero.setText(player2.getCurrentHP() + "");
					heroPowerPressed=false;
					p1Mana.setText(player1.getCurrentManaCrystals()+"");
					p2Mana.setText(player2.getCurrentManaCrystals()+"");
				}
				catch (Exception e7){
					view.putSoundInPlay(e7 , model.getCurrentHero());
					AlertBox.display("Error", e7.getMessage());
				}
			}

			else if (model.getCurrentHero() instanceof Priest && heroPowerPressed) {
				try {
					((Priest)(model.getCurrentHero())).useHeroPower(player2);
					view.putSound("sounds/heroPower1.mp3", 3,0.5);
					p2Hero.setText(player2.getCurrentHP() + "");
					heroPowerPressed=false;
					p1Mana.setText(player1.getCurrentManaCrystals()+"");
					p2Mana.setText(player2.getCurrentManaCrystals()+"");
				}
				catch (Exception e7){
					view.putSoundInPlay(e7 , model.getCurrentHero());
					AlertBox.display("Error", e7.getMessage());
				}
			}

			else {
				if(endflag)
				{
					CannotAttackException h6 = new CannotAttackException();
					view.putSoundInPlay(h6 , model.getCurrentHero());
					AlertBox.display("Error", "Cannot attack yourself");
					curCard = null;
					attackPressed = false;
					curHero = null;
				}
				else
				{
					if(curCard != null)
					{
						view.putGlowAnimation(p2Hero, Color.RED) ; 
						int attackerind = p1Field.indexOf(curCard);
						Minion attacker = model.getCurrentHero().getField().get(attackerind);
						try {
							model.getCurrentHero().attackWithMinion(attacker, model.getOpponent());
							view.putSound("sounds/hero_weapon_draw.mp3", 2, 0.5);
							p2Hero.setText(model.getOpponent().getCurrentHP()+"");
							curCard = null;
							attackPressed = false;
							curHero = null;
						
						} catch (Exception e1) {
							view.putSoundInPlay(e1 , model.getCurrentHero());
							AlertBox.display("Error", e1.getMessage());
							curCard = null;
							attackPressed = false;
							curHero = null;
							view.putGlowAnimation(p2Hero, Color.WHITE) ;
						}
					}
					else
					{
						AlertBox.display("Error", "Choose a minion to attack with");
						curCard = null;
						attackPressed = false;
						curHero = null;
					}
				}
			}});
		p1HeroPower = new Button();
		p1HeroPower.setOnAction(e -> {
			//may cause an error please check hand size
			try{
				model.validateTurn(player1);
				if(model.getCurrentHero() instanceof Warlock) {
					((Warlock)(model.getCurrentHero())).useHeroPower();
					view.putSound("sounds/heroPower1.mp3", 3,0.5);
					updateAll();
					p1Deck.setText(player1.getDeck().size()+"");
					p2Deck.setText(player2.getDeck().size()+"");
					p1Hero.setText(model.getCurrentHero().getCurrentHP() + "");
					heroPowerPressed=false;
					p1Mana.setText(player1.getCurrentManaCrystals()+"");
					p2Mana.setText(player2.getCurrentManaCrystals()+"");
				}

				else if(model.getCurrentHero() instanceof Paladin){
					((Paladin)(model.getCurrentHero())).useHeroPower();
					view.putSound("sounds/heroPower1.mp3", 3,0.5);
					updateField();
					heroPowerPressed=false;
					p1Mana.setText(player1.getCurrentManaCrystals()+"");
					p2Mana.setText(player2.getCurrentManaCrystals()+"");
				}

				else if(model.getCurrentHero() instanceof Hunter) {
					((Hunter)(model.getCurrentHero())).useHeroPower();
					view.putSound("sounds/heroPower1.mp3", 3,0.5);
					p2Hero.setText(model.getOpponent().getCurrentHP() + "");
					heroPowerPressed=false;
					p1Mana.setText(player1.getCurrentManaCrystals()+"");
					p2Mana.setText(player2.getCurrentManaCrystals()+"");
				}

			else
				heroPowerPressed = true;}
			catch(Exception e7){
				view.putSoundInPlay(e7 , model.getCurrentHero());
				AlertBox.display("Error",e7.getMessage());
				
			}
		});
		p2HeroPower = new Button();
		p2HeroPower.setOnAction(e -> {
			
			//may cause an error please check hand size
			try{
				model.validateTurn(player2);
				if(model.getCurrentHero() instanceof Warlock) {
					((Warlock)(model.getCurrentHero())).useHeroPower();
					view.putSound("sounds/heroPower1.mp3", 3,0.5);
					updateAll();
					p1Deck.setText(player1.getDeck().size()+"");
					p2Deck.setText(player2.getDeck().size()+"");
					p2Hero.setText(model.getCurrentHero().getCurrentHP() + "");
					heroPowerPressed=false;
					p1Mana.setText(player1.getCurrentManaCrystals()+"");
					p2Mana.setText(player2.getCurrentManaCrystals()+"");
				}

				else if(model.getCurrentHero() instanceof Paladin){
					((Paladin)(model.getCurrentHero())).useHeroPower();
					view.putSound("sounds/heroPower1.mp3", 3,0.5);
					updateField();
					heroPowerPressed=false;
					p1Mana.setText(player1.getCurrentManaCrystals()+"");
					p2Mana.setText(player2.getCurrentManaCrystals()+"");
				}

				else if(model.getCurrentHero() instanceof Hunter) {
					((Hunter)(model.getCurrentHero())).useHeroPower();
					view.putSound("sounds/heroPower1.mp3", 3,0.5);
					p1Hero.setText(model.getOpponent().getCurrentHP() + "");
					heroPowerPressed=false;
					p1Mana.setText(player1.getCurrentManaCrystals()+"");
					p2Mana.setText(player2.getCurrentManaCrystals()+"");
				}

				else
					heroPowerPressed = true;}
			catch(Exception e7){
				view.putSoundInPlay(e7 , model.getCurrentHero());
				AlertBox.display("Error",e7.getMessage());
			}
		});

		//setting buttons locations
		endTurn.setLayoutX(980);
		endTurn.setLayoutY(325);
		p1Hero.setLayoutX(593);
		p1Hero.setLayoutY(500);
		p2Hero.setLayoutX(593);
		p2Hero.setLayoutY(98);
		p1HeroPower.setLayoutX(719);
		p1HeroPower.setLayoutY(500);
		p2HeroPower.setLayoutX(719);
		p2HeroPower.setLayoutY(132);
		p1Deck.setLayoutX(1098);
		p1Deck.setLayoutY(564);
		p2Deck.setLayoutX(26);
		p2Deck.setLayoutY(35);
		p1Mana.setLayoutX(1041);
		p1Mana.setLayoutY(489);
		p2Mana.setLayoutX(1030);
		p2Mana.setLayoutY(146);
		
		

		//setting buttons dimensions
		endTurn.setPrefSize(93, 36);
		endTurn.setOpacity(0.2);
		p1Hero.setPrefSize(90, 104);
		p2Hero.setPrefSize(90, 96);
		p1HeroPower.setPrefSize(90, 90);
		p2HeroPower.setPrefSize(90, 90);
		p1Deck.setPrefSize(150, 150);
		p2Deck.setPrefSize(150, 150);
		p1Mana.setPrefSize(50, 50);
		p2Mana.setPrefSize(50, 50);
		p1Mana.setFont(Font.font("verdana", FontWeight.BOLD, FontPosture.REGULAR, 18));
		p2Mana.setFont(Font.font("verdana", FontWeight.BOLD, FontPosture.REGULAR, 18));
		p1Deck.setFont(Font.font("verdana", FontWeight.BOLD, FontPosture.REGULAR, 25));
		p2Deck.setFont(Font.font("verdana", FontWeight.BOLD, FontPosture.REGULAR, 25));
		p1Mana.setTextFill(Color.WHITE);
		p2Mana.setTextFill(Color.WHITE);
		p1Mana.setAlignment(Pos.CENTER);
		p2Mana.setAlignment(Pos.CENTER);
		p1Deck.setTextFill(Color.WHITE);
		p2Deck.setTextFill(Color.WHITE);
		

		//setting heroes health
		String p1Health = player1.getCurrentHP()+"";
		p1Hero.setText(p1Health);
		p1Hero.setAlignment(Pos.BOTTOM_RIGHT);
		p1Hero.setTextFill(Color.WHITE);
		p1Hero.setFont(Font.font("verdana", FontWeight.BOLD, FontPosture.REGULAR, 15));
		String p2Health = player2.getCurrentHP()+"";
		p2Hero.setText(p2Health);
		p2Hero.setAlignment(Pos.BOTTOM_RIGHT);
		p2Hero.setTextFill(Color.WHITE);
		p2Hero.setFont(Font.font("verdana", FontWeight.BOLD, FontPosture.REGULAR, 15));

		view.getLayout().getChildren().addAll(endTurn,p1Hero,p2Hero,p1HeroPower,p2HeroPower,deathButton,popUpCard,p1Label,p2Label);
		view.getLayout().getChildren().addAll(bottomView,topView, p1FieldView, p2FieldView,p1Mana,p2Mana,p1Deck,p2Deck);

		

		view.setImageForCard(player1.getName(), p1Hero);
		view.setImageForCard(player2.getName(), p2Hero);
		view.setImageForCard(player1.getName()+"_power", p1HeroPower);
		view.setImageForCard(player2.getName()+"_power", p2HeroPower);
		
		
		p1Hero.setOnMouseEntered( e ->{
			p1Label.setVisible(true);
		});
		p1Hero.setOnMouseExited( e -> {
			p1Label.setVisible(false);
		});
		p2Hero.setOnMouseEntered( e ->{
			p2Label.setVisible(true);
		});
		p2Hero.setOnMouseExited( e -> {
			p2Label.setVisible(false);
		});
		
		
		if(model.getCurrentHero() ==player1){
			 view.putGlowAnimation(p1Hero, Color.GREEN);  
			 view.putGlowAnimation(p2Hero, Color.WHITE);  
		}
		else{
			view.putGlowAnimation(p2Hero, Color.GREEN);  
			 view.putGlowAnimation(p1Hero, Color.WHITE); 
		}

		this.updateAll();

	}

	private void updateField() throws FileNotFoundException {
		if(!endflag) {
			p1Field.clear();
			p1FieldView.getChildren().clear();
			for (int i = 0; i < model.getCurrentHero().getField().size(); i++) {
				Button P2CardButton = new Button();
				P2CardButton.setPrefSize(120, 200);
				view.putGlowAnimation(P2CardButton, Color.WHITE);
				//getting data of card
				String healthOfMinionText;
				String attackOfMinionText;
				String manaOfMinionText;

				Minion minionCard = model.getCurrentHero().getField().get(i);
				int healthOfMinion = minionCard.getCurrentHP();
				healthOfMinionText = healthOfMinion + "";
				int attackOfMinion = minionCard.getAttack();
				attackOfMinionText = attackOfMinion + "";
				int manaOfMinion = minionCard.getManaCost();
				manaOfMinionText = manaOfMinion+"";
				
				//setting glow for button in field
				P2CardButton.setOnMouseEntered( e ->{ 
					view.putGlowAnimation(P2CardButton, Color.GOLD);
				popUpCard.setVisible(true);
				try {
					view.setImageForCard(minionCard.getName(), popUpCard);
					setTextForButton(popUpCard, true, "deadCard", manaOfMinionText , healthOfMinionText, attackOfMinionText);
					
				} catch (Exception e1) {
					view.putSoundInPlay(e1 , model.getCurrentHero());
					AlertBox.display("error", e1.getMessage());;
				}
			});
			P2CardButton.setOnMouseExited( e -> {
			view.putGlowAnimation(P2CardButton, Color.WHITE);
			popUpCard.setVisible(false);
			});

				setTextForButton(P2CardButton, false, "sizeInField", "", healthOfMinionText, attackOfMinionText);
				View.setImageForCard("inplay_minion_" + model.getCurrentHero().getField().get(i).getName() + (((Minion) model.getCurrentHero().getField().get(i)).isDivine() ? "_divine" : "")+(((Minion) model.getCurrentHero().getField().get(i)).isSleeping() ? "_sleep" : ""), P2CardButton);
				P2CardButton.setOnAction(k ->{
					if(curLeech != null) {
						try {
							castLeechingSpell(curLeech, minionCard, P2CardButton);
							curLeech = null;
							curMinionTargetSpell = null;
							//lastHeroHandSize = model.getCurrentHero().getHand().size();
						} catch (Exception e7) {
							view.putSoundInPlay(e7 , model.getCurrentHero());
							AlertBox.display("Error",e7.getMessage());}
					}
					else if(curMinionTargetSpell != null){
						try {
							castMinionTargetSpell(curMinionTargetSpell, minionCard, P2CardButton);
							curLeech = null;
							curMinionTargetSpell = null;
							//lastHeroHandSize = model.getCurrentHero().getHand().size();
						} catch (Exception e7) {
							view.putSoundInPlay(e7 , model.getCurrentHero());
							AlertBox.display("Error",e7.getMessage());}
					}
					else if(hybridSpell != null)
					{
						try {
							castMinionTargetSpell((MinionTargetSpell) hybridSpell, minionCard, P2CardButton);
							hybridSpell = null;
							curLeech = null;
							curMinionTargetSpell = null;
						} catch (Exception e1) {
							view.putSoundInPlay(e1 , model.getCurrentHero());
							AlertBox.display("Error",e1.getMessage());
						}
					}
					else if(heroPowerPressed) {
						try{
							SummonHeroPower(minionCard,P2CardButton);
							heroPowerPressed = false;}
						catch(Exception e7) {
							view.putSoundInPlay(e7 , model.getCurrentHero());
							AlertBox.display("Error",e7.getMessage());
						}
					}
					else
						attack1(minionCard); });

				p1Field.add(P2CardButton);
				p1FieldView.getChildren().add(P2CardButton);
			}

			p2Field.clear();
			p2FieldView.getChildren().clear();
			for (int i = 0; i < player2.getField().size(); i++) {
				Button P2CardButton = new Button();
				P2CardButton.setPrefSize(120, 200);
				view.putGlowAnimation(P2CardButton, Color.WHITE);
				//getting data of card
				String healthOfMinionText;
				String attackOfMinionText;
				String manaOfMinionText;

				Minion minionCard = player2.getField().get(i);
				int healthOfMinion = minionCard.getCurrentHP();
				healthOfMinionText = healthOfMinion + "";
				int attackOfMinion = minionCard.getAttack();
				attackOfMinionText = attackOfMinion + "";
				int manaOfMinion = minionCard.getManaCost();
				manaOfMinionText= manaOfMinion+"";
				

				setTextForButton(P2CardButton, false, "sizeInField", "", healthOfMinionText, attackOfMinionText);
				View.setImageForCard("inplay_minion_" + player2.getField().get(i).getName() + (((Minion) player2.getField().get(i)).isDivine() ? "_divine" : "")+(((Minion) player2.getField().get(i)).isSleeping() ? "_sleep" : ""), P2CardButton);
				
				P2CardButton.setOnMouseEntered( e ->{ 
					view.putGlowAnimation(P2CardButton, Color.GOLD);
					popUpCard.setVisible(true);
					try {
						view.setImageForCard(minionCard.getName(), popUpCard);
						setTextForButton(popUpCard, true, "deadCard", manaOfMinionText , healthOfMinionText, attackOfMinionText);
						
					} catch (Exception e1) {
						view.putSoundInPlay(e1 , model.getCurrentHero());
						AlertBox.display("error", e1.getMessage());;
					}
				});
				P2CardButton.setOnMouseExited( e -> {
				view.putGlowAnimation(P2CardButton, Color.WHITE);
				popUpCard.setVisible(false);
				});
				P2CardButton.setOnAction(k ->{
					if(curLeech != null) {
						try {
							castLeechingSpell(curLeech, minionCard, P2CardButton);
							curLeech = null;
							curMinionTargetSpell = null;
							//lastHeroHandSize = model.getCurrentHero().getHand().size();
						} catch (Exception e7) {
							view.putSoundInPlay(e7 , model.getCurrentHero());
							AlertBox.display("Error",e7.getMessage());}
					}
					else if(curMinionTargetSpell != null){
						try {
							castMinionTargetSpell(curMinionTargetSpell, minionCard, P2CardButton);
							curLeech = null;
							curMinionTargetSpell = null;
							//lastHeroHandSize = model.getCurrentHero().getHand().size();
						} catch (Exception e7) {
							view.putSoundInPlay(e7 , model.getCurrentHero());
							AlertBox.display("Error",e7.getMessage());;}
					}
					else if(hybridSpell != null)
					{
						try {
							castMinionTargetSpell((MinionTargetSpell) hybridSpell, minionCard, P2CardButton);
							hybridSpell = null;
							curLeech = null;
							curMinionTargetSpell = null;
						} catch (Exception e1) {
							view.putSoundInPlay(e1 , model.getCurrentHero());
							AlertBox.display("Error",e1.getMessage());
						}
					}
					else if(heroPowerPressed) {
						try{
							SummonHeroPower(minionCard,P2CardButton);
							heroPowerPressed = false;}
						catch(Exception e7) {
							view.putSoundInPlay(e7 , model.getCurrentHero());
							view.putSoundInPlay(e7 , model.getCurrentHero());
							AlertBox.display("Error",e7.getMessage());
						}
					}
					else
						attack2(minionCard); });
				p2Field.add(P2CardButton);
				p2FieldView.getChildren().add(P2CardButton);
			}
		}

		else {
			p2Field.clear();
			p2FieldView.getChildren().clear();
			for (int i = 0; i < model.getCurrentHero().getField().size(); i++) {
				Button P2CardButton = new Button();
				P2CardButton.setPrefSize(120, 200);
				view.putGlowAnimation(P2CardButton, Color.WHITE);
				//getting data of card
				String healthOfMinionText;
				String attackOfMinionText;
				String manaOfMinionText;

				Minion minionCard = model.getCurrentHero().getField().get(i);
				int healthOfMinion = minionCard.getCurrentHP();
				healthOfMinionText = healthOfMinion + "";
				int attackOfMinion = minionCard.getAttack();
				attackOfMinionText = attackOfMinion + "";
				int manaOfMinion = minionCard.getManaCost();
				manaOfMinionText = manaOfMinion+"";

				setTextForButton(P2CardButton, false, "sizeInField", "", healthOfMinionText, attackOfMinionText);
				View.setImageForCard("inplay_minion_" + model.getCurrentHero().getField().get(i).getName() + (((Minion) model.getCurrentHero().getField().get(i)).isDivine() ? "_divine" : "")+(((Minion) model.getCurrentHero().getField().get(i)).isSleeping() ? "_sleep" : ""), P2CardButton);
				
				P2CardButton.setOnMouseEntered( e ->{ 
					view.putGlowAnimation(P2CardButton, Color.GOLD);
					popUpCard.setVisible(true);
					try {
						view.setImageForCard(minionCard.getName(), popUpCard);
						setTextForButton(popUpCard, true, "deadCard", manaOfMinionText , healthOfMinionText, attackOfMinionText);
						
					} catch (Exception e1) {
						view.putSoundInPlay(e1 , model.getCurrentHero());
						AlertBox.display("error", e1.getMessage());;
					}
				});
				P2CardButton.setOnMouseExited( e -> {
				view.putGlowAnimation(P2CardButton, Color.WHITE);
				popUpCard.setVisible(false);
				});
				P2CardButton.setOnAction(k ->{
					if(curLeech != null) {
						try {
							castLeechingSpell(curLeech, minionCard, P2CardButton);
							curLeech = null;
							curMinionTargetSpell = null;
							//lastHeroHandSize = model.getCurrentHero().getHand().size();
						} catch (Exception e7) {
							view.putSoundInPlay(e7 , model.getCurrentHero());
							AlertBox.display("Error",e7.getMessage());}
					}
					else if(curMinionTargetSpell != null){
						try {
							castMinionTargetSpell(curMinionTargetSpell, minionCard, P2CardButton);
							curLeech = null;
							curMinionTargetSpell = null;
							//lastHeroHandSize = model.getCurrentHero().getHand().size();
						} catch (Exception e7) {
							view.putSoundInPlay(e7 , model.getCurrentHero());
							AlertBox.display("Error",e7.getMessage());}
					}
					else if(hybridSpell != null)
					{
						try {
							castMinionTargetSpell((MinionTargetSpell) hybridSpell, minionCard, P2CardButton);
							hybridSpell = null;
							curLeech = null;
							curMinionTargetSpell = null;
						} catch (Exception e1) {
							view.putSoundInPlay(e1 , model.getCurrentHero());
							AlertBox.display("Error",e1.getMessage());
						}
					}
					else if(heroPowerPressed) {
						try{
							SummonHeroPower(minionCard,P2CardButton);
							heroPowerPressed = false;}
						catch(Exception e7) {
							view.putSoundInPlay(e7 , model.getCurrentHero());
							view.putSoundInPlay(e7 , model.getCurrentHero());
							AlertBox.display("Error",e7.getMessage());
						}
					}
					else
						attack2(minionCard); });
				p2Field.add(P2CardButton);
				p2FieldView.getChildren().add(P2CardButton);
			}

			p1Field.clear();
			p1FieldView.getChildren().clear();
			for (int i = 0; i < player1.getField().size(); i++) {
				Button P2CardButton = new Button();
				P2CardButton.setPrefSize(120, 200);
				view.putGlowAnimation(P2CardButton, Color.WHITE);
				//getting data of card
				String healthOfMinionText;
				String attackOfMinionText;
				String manaOfMinionText;

				Minion minionCard = player1.getField().get(i);
				int healthOfMinion = minionCard.getCurrentHP();
				healthOfMinionText = healthOfMinion + "";
				int attackOfMinion = minionCard.getAttack();
				attackOfMinionText = attackOfMinion + "";
				int manaOfMinion = minionCard.getManaCost();
				manaOfMinionText = manaOfMinion +"";
				
				//setting glow for button in field
				P2CardButton.setOnMouseEntered( e ->{ 
					view.putGlowAnimation(P2CardButton, Color.GOLD);
					popUpCard.setVisible(true);
					try {
						view.setImageForCard(minionCard.getName(), popUpCard);
						setTextForButton(popUpCard, true, "deadCard", manaOfMinionText , healthOfMinionText, attackOfMinionText);
						
					} catch (Exception e1) {
						view.putSoundInPlay(e1 , model.getCurrentHero());
						AlertBox.display("error", e1.getMessage());;
					}
				});
				P2CardButton.setOnMouseExited( e -> {
				view.putGlowAnimation(P2CardButton, Color.WHITE);
				popUpCard.setVisible(false);
				});
				setTextForButton(P2CardButton, false, "sizeInField", "", healthOfMinionText, attackOfMinionText);
				View.setImageForCard("inplay_minion_" + player1.getField().get(i).getName() + (((Minion) player1.getField().get(i)).isDivine() ? "_divine" : "")+(((Minion) player1.getField().get(i)).isSleeping() ? "_sleep" : ""), P2CardButton);
				P2CardButton.setOnAction(k ->{
					if(curLeech != null) {
						try {
							castLeechingSpell(curLeech, minionCard, P2CardButton);
							curLeech = null;
							curMinionTargetSpell = null;
							//lastHeroHandSize = model.getCurrentHero().getHand().size();
						} catch (Exception e7) {
							view.putSoundInPlay(e7 , model.getCurrentHero());
							AlertBox.display("Error",e7.getMessage());}
					}
					else if(curMinionTargetSpell != null){
						try {
							castMinionTargetSpell(curMinionTargetSpell, minionCard, P2CardButton);
							curLeech = null;
							curMinionTargetSpell = null;
							//lastHeroHandSize = model.getCurrentHero().getHand().size();
						} catch (Exception e7) {
							view.putSoundInPlay(e7 , model.getCurrentHero());
							AlertBox.display("Error",e7.getMessage());}
					}
					else if(hybridSpell != null)
					{
						try {
							castMinionTargetSpell((MinionTargetSpell) hybridSpell, minionCard, P2CardButton);
							hybridSpell = null;
							curLeech = null;
							curMinionTargetSpell = null;
						} catch (Exception e1) {
							view.putSoundInPlay(e1 , model.getCurrentHero());
							AlertBox.display("Error",e1.getMessage());
						}
					}
					else if(heroPowerPressed) {
						try{
							SummonHeroPower(minionCard,P2CardButton);
							heroPowerPressed = false;}
						catch(Exception e7) {
							view.putSoundInPlay(e7 , model.getCurrentHero());
							AlertBox.display("Error",e7.getMessage());
						}
					}
					else
						attack1(minionCard); });
				p1Field.add(P2CardButton);
				p1FieldView.getChildren().add(P2CardButton);
			}
		}
	}


	public void handle(javafx.event.ActionEvent event) {

		Button CardButton;
		if(event.getSource() == endTurn) {
			String p1Health = player1.getCurrentHP()+"";
	
			p1Hero.setText(p1Health);
			String p2Health = player2.getCurrentHP()+"";
			
			p2Hero.setText(p2Health);
			if(model.getCurrentHero() ==player1){
				 view.putGlowAnimation(endTurn, Color.WHITE);    
			}
			else{
				view.putGlowAnimation(endTurn, Color.GOLD);   
			}
			try {
				model.endTurn();
				view.putSound("sounds/turn_timer_end_turn_button_flips_over.mp3",2,0.6);
				if(model.getCurrentHero() ==player1){
					 view.putGlowAnimation(p1Hero, Color.GREEN);  
					 view.putGlowAnimation(p2Hero, Color.WHITE);  
				}
				else{
					view.putGlowAnimation(p2Hero, Color.GREEN);  
					 view.putGlowAnimation(p1Hero, Color.WHITE); 
				}
			} catch (FullHandException | CloneNotSupportedException e) {
				if(e instanceof FullHandException)
				{
					String p3Health = player1.getCurrentHP()+"";
			
					p1Hero.setText(p3Health);
					String p4Health = player2.getCurrentHP()+"";
				
					p2Hero.setText(p4Health);
					try {
						view.setImageForCard(((FullHandException) e).getBurned().getName(), deathButton);
						if( ((FullHandException) e).getBurned() instanceof Minion  ){
	                         
							setTextForButton(deathButton, true, "deadCard",((FullHandException) e).getBurned().getManaCost()+"" , ((Minion)(((FullHandException) e).getBurned())).getMaxHP()+"",((Minion)(((FullHandException) e).getBurned())).getAttack()+"");
							deathButton.setPrefSize(120, 139);
						}
						else{
							setTextForButton(deathButton, true, "small",((FullHandException) e).getBurned().getManaCost()+"" , "","");
							deathButton.setPrefSize(100, 140);
						}
						deathButton.setVisible(true);
						
					} catch (FileNotFoundException e1) {
						view.putSoundInPlay(e1 , model.getCurrentHero());
						AlertBox.display("Error",e1.getMessage());
						
					}
					
				}
				else{
					view.putSoundInPlay(e , model.getCurrentHero());
					AlertBox.display("Error",e.getMessage());
				}
				if(model.getCurrentHero() ==player1){
					 view.putGlowAnimation(p1Hero, Color.GREEN);  
					 view.putGlowAnimation(p2Hero, Color.WHITE);  
				}
				else if(model.getCurrentHero() ==player2){
					view.putGlowAnimation(p2Hero, Color.GREEN);  
					 view.putGlowAnimation(p1Hero, Color.WHITE); 
				}
				view.putSoundInPlay(e , model.getCurrentHero());
				AlertBox.display("Error",e.getMessage());
				deathButton.setVisible(false);
			
			}
			
			try {
				endflag = !endflag;
				p1Deck.setText(player1.getDeck().size()+"");
				p2Deck.setText(player2.getDeck().size()+"");
				p1Mana.setText(player1.getCurrentManaCrystals()+"");
				p2Mana.setText(player2.getCurrentManaCrystals()+"");
				this.updateAll();
				this.updateField();
				for(int i= 0; i< model.getOpponent().getHand().size() ;i++){
					if(endflag){
					view.setImageForCard("Card back", p1Hand.get(i) );
					p1Hand.get(i).setText("");
					p1Hand.get(i).setOnMouseEntered(null);
					p1Hand.get(i).setOnMouseExited(null);
					}
					else{
					view.setImageForCard("Card back", p2Hand.get(i) );
					p2Hand.get(i).setText("");
					p2Hand.get(i).setOnMouseEntered(null);
					p2Hand.get(i).setOnMouseExited(null);
					}
				}
				for(int i= 0; i< model.getCurrentHero().getHand().size() ;i++){
					String mana;
					String hp;
					String attack;
					Card x =  model.getCurrentHero().getHand().get(i);
					if(model.getCurrentHero().getHand().get(i) instanceof Minion){
					 mana = model.getCurrentHero().getHand().get(i).getManaCost()+"";
					 hp = ((Minion)model.getCurrentHero().getHand().get(i)).getMaxHP() +"";
					 attack = ((Minion)model.getCurrentHero().getHand().get(i)).getAttack() +"";
					}
					else{
						 mana = model.getCurrentHero().getHand().get(i).getManaCost()+"";
						 hp = "";
						 attack = "";
					}
					if(!endflag){
					view.setImageForCard(model.getCurrentHero().getHand().get(i).getName(), p1Hand.get(i) );
					setTextForButton(p1Hand.get(i) , false, "small", mana, hp, attack);
					p1Hand.get(i).setOnMouseEntered( e ->{ 
						if(model.getCurrentHero().getHand().contains(x)){
						int y = model.getCurrentHero().getHand().indexOf(x);
						p1Hand.get(y).setPrefSize(120, 200);
					    setTextForButton(p1Hand.get(y), true, "big", mana, hp, attack);
						}
					
				});
					p1Hand.get(i).setOnMouseExited( e -> {
						if(model.getCurrentHero().getHand().contains(x)){
							int y = model.getCurrentHero().getHand().indexOf(x);
							p1Hand.get(y).setPrefSize(120, 200);
						    setTextForButton(p1Hand.get(y), true, "small", mana, hp, attack);
							}
				});
					}
					else{
					view.setImageForCard(model.getCurrentHero().getHand().get(i).getName(), p2Hand.get(i) );
					setTextForButton(p2Hand.get(i) , false, "small", mana, hp, attack);
					p2Hand.get(i).setOnMouseEntered( e -> {
						if(model.getCurrentHero().getHand().contains(x)){
							int y = model.getCurrentHero().getHand().indexOf(x);
							p2Hand.get(y).setPrefSize(120, 200);
						    setTextForButton(p2Hand.get(y), true, "big", mana, hp, attack);
							}
				});
					p2Hand.get(i).setOnMouseExited( e -> {
						if(model.getCurrentHero().getHand().contains(x)){
							int y = model.getCurrentHero().getHand().indexOf(x);
							p2Hand.get(y).setPrefSize(120, 200);
						    setTextForButton(p2Hand.get(y), true, "small", mana, hp, attack);
							}
				});
					}
				}
				
				heroPowerPressed=false;
				curHero=null;
				attackPressed=false;
				curCard=null;
				curAttacker=null;
				curLeech=null;
				curMinionTargetSpell=null;
				hybridSpell=null;
			
				//view.putSound("sounds/Don_t-Let-Your-Guard-Down.mp3",0,0.3);
			} catch (FileNotFoundException e) {
				view.putSoundInPlay(e , model.getCurrentHero());
				AlertBox.display("error", e.getMessage());;
			}
		}




	}
	public void updateAll() throws FileNotFoundException
	{
		if(!endflag) {

			for(int i = (lastHeroHandSize); i < model.getCurrentHero().getHand().size(); i++) {

				Button P1CardButton = new Button();
				Button P2CardButton = new Button();
				//getting data of card
				int manaOfCard = model.getCurrentHero().getHand().get(i).getManaCost();
				String manaOfCardText = manaOfCard+"";
				String healthOfMinionText;
				String attackOfMinionText;
				if( model.getCurrentHero().getHand().get(i) instanceof Minion)
				{
					Card minionCard = model.getCurrentHero().getHand().get(i);
					int healthOfMinion= ((Minion)minionCard).getCurrentHP();
					healthOfMinionText= healthOfMinion+"";
					int attackOfMinion = ((Minion)minionCard).getAttack();
					attackOfMinionText = attackOfMinion+"";
				}
				else
				{
					healthOfMinionText= "";
					attackOfMinionText ="";
				}

				setTextForButton(P1CardButton, false, "small", manaOfCardText, healthOfMinionText, attackOfMinionText);
				P1CardButton.setOnMouseEntered( e ->{ P1CardButton.setPrefSize(120, 200);
					setTextForButton(P1CardButton, true, "big", manaOfCardText, healthOfMinionText, attackOfMinionText);
				});
				P1CardButton.setOnMouseExited( e -> {P1CardButton.setPrefSize(80, 100);
					setTextForButton(P1CardButton, true, "small", manaOfCardText, healthOfMinionText, attackOfMinionText);
				});

				View.setImageForCard(model.getCurrentHero().getHand().get(i).getName(), P1CardButton);
				p1Hand.add(P1CardButton);
				bottomView.getChildren().add(P1CardButton);

				if(model.getCurrentHero().getHand().get(i) instanceof Minion) {
					Minion x = (Minion) model.getCurrentHero().getHand().get(i);
					P1CardButton.setOnAction(e -> {
						if(!endflag)
						{
							try {
								P2CardButton.setPrefSize(120, 200);
								view.putGlowAnimation(P2CardButton, Color.WHITE);
								int y = model.getCurrentHero().getHand().indexOf(x);
								model.getCurrentHero().playMinion(x);
								view.putSound("sounds/minionSummon", 2 ,1);
								int pic = model.getCurrentHero().getField().indexOf(x);
								setTextForButton(P2CardButton, false, "sizeInField", "", healthOfMinionText, attackOfMinionText);
								
								//setting glow
								View.setImageForCard("inplay_minion_" + model.getCurrentHero().getField().get(pic).getName() + (((Minion) model.getCurrentHero().getField().get(pic)).isDivine() ? "_divine" : "")+(((Minion) model.getCurrentHero().getField().get(pic)).isSleeping() ? "_sleep" : ""), P2CardButton);
								P2CardButton.setOnMouseEntered( j ->{ 
									view.putGlowAnimation(P2CardButton, Color.GOLD);
									popUpCard.setVisible(true);
									try {
										view.setImageForCard(x.getName(), popUpCard);
										setTextForButton(popUpCard, true, "deadCard", x.getManaCost()+"" , healthOfMinionText, attackOfMinionText);
										
									} catch (Exception e1) {
										view.putSoundInPlay(e1 , model.getCurrentHero());
										AlertBox.display("error", e1.getMessage());;
									}
								});
								P2CardButton.setOnMouseExited( h -> {
									view.putGlowAnimation(P2CardButton, Color.WHITE);
									popUpCard.setVisible(false);
									});

								p1Field.add(P2CardButton);
								p1FieldView.getChildren().add(P2CardButton);
								bottomView.getChildren().remove(p1Hand.get(y));
								p1Hand.remove(y);
								lastHeroHandSize = model.getCurrentHero().getHand().size();
								p1Mana.setText(player1.getCurrentManaCrystals()+"");
								p2Mana.setText(player2.getCurrentManaCrystals()+"");
								

								P2CardButton.setOnAction(k -> {
									
									if(curLeech != null) {
										try {
											castLeechingSpell(curLeech, x, P2CardButton);
											view.putSound("sounds/spellSound", 2 ,1);
											curLeech = null;
											curMinionTargetSpell = null;
											//lastHeroHandSize = model.getCurrentHero().getHand().size();
										} catch (Exception e7) {
											view.putSoundInPlay(e7 , model.getCurrentHero());
											AlertBox.display("Error",e7.getMessage());}
									}
									else if(curMinionTargetSpell != null){
										try {
											castMinionTargetSpell(curMinionTargetSpell, x, P2CardButton);
											view.putSound("sounds/spellSound", 2 ,1);
											curLeech = null;
											curMinionTargetSpell = null;
										} catch (Exception e7) {
											view.putSoundInPlay(e7 , model.getCurrentHero());
											AlertBox.display("error", e7.getMessage());}
									}
									else if(hybridSpell != null) {
										try {
											castMinionTargetSpell((MinionTargetSpell) hybridSpell, x, P2CardButton);
											view.putSound("sounds/spellSound", 2 ,1);
											hybridSpell = null;
											curLeech = null;
											curMinionTargetSpell = null;
										} catch (Exception e1) {
											view.putSoundInPlay(e1 , model.getCurrentHero());
											AlertBox.display("Error",e1.getMessage());
										}
									}
									else if(heroPowerPressed) {
										try{
											SummonHeroPower(x,P2CardButton);
											heroPowerPressed = false;}
										catch(Exception e7) {
											view.putSoundInPlay(e7 , model.getCurrentHero());
											AlertBox.display("Error",e7.getMessage());
										}
									}
									else
										attack1(x); });

							} catch (Exception e1) {
								view.putSoundInPlay(e1 , model.getCurrentHero());
								AlertBox.display("Error", e1.getMessage());
								curCard = null;
								attackPressed = false;
								P1CardButton.setPrefSize(80, 100);
								
								P1CardButton.setOnMouseEntered( f ->{ P1CardButton.setPrefSize(120, 200);
									setTextForButton(P1CardButton, true, "big", manaOfCardText, healthOfMinionText, attackOfMinionText);
								});
								P1CardButton.setOnMouseExited( h -> {P1CardButton.setPrefSize(80, 100);
									setTextForButton(P1CardButton, true, "small", manaOfCardText, healthOfMinionText, attackOfMinionText);
								});

							}}
						else
						if(attackPressed)
						{
							//t3ala hena
							
							NotSummonedException h8 = new NotSummonedException();
							view.putSoundInPlay(h8 , model.getCurrentHero());
							AlertBox.display("Error", "Cannot attack an unsummoned minion");
							attackPressed = false;
							curCard = null;
							
						}
						else{
							
							NotYourTurnException h8= new NotYourTurnException();
						    view.putSoundInPlay( h8 , model.getCurrentHero());
							AlertBox.display("Error", "Not your turn");
						}

					});
				}

				else if(model.getCurrentHero().getHand().get(i) instanceof LeechingSpell) {
					LeechingSpell L = (LeechingSpell) model.getCurrentHero().getHand().get(i);
					P1CardButton.setOnAction(e -> {
						try {
							model.validateTurn(player1);
							curLeech = L;
						} catch (NotYourTurnException e6) {
		
							view.putSoundInPlay(e6 , model.getCurrentHero());
							AlertBox.display("Error",e6.getMessage());
						} });
				}

				else if(model.getCurrentHero().getHand().get(i) instanceof AOESpell) {
					AOESpell x = (AOESpell) model.getCurrentHero().getHand().get(i);
					P1CardButton.setOnAction(e -> {
						try {
							model.validateTurn(player1);
							//model.validateManaCost((Card)x);
							int y = model.getCurrentHero().getHand().indexOf(x);
							model.getCurrentHero().castSpell(x, model.getOpponent().getField());
							view.putSound("sounds/spellSound", 2 ,1);
							bottomView.getChildren().remove(p1Hand.get(y));
							p1Hand.remove(p1Hand.get(y));
							lastHeroHandSize = model.getCurrentHero().getHand().size();
							updateField();
							p1Mana.setText(player1.getCurrentManaCrystals()+"");
							p2Mana.setText(player2.getCurrentManaCrystals()+"");
						} catch (Exception e1) {
							view.putSoundInPlay(e1 , model.getCurrentHero());
							AlertBox.display("Error", e1.getMessage());}});
				}
				else if(model.getCurrentHero().getHand().get(i).getName().equals("Pyroblast") || model.getCurrentHero().getHand().get(i).getName().equals("Kill Command"))
				{
					Spell M = (Spell) model.getCurrentHero().getHand().get(i);
					P1CardButton.setOnAction(e -> {
						try {
							model.validateTurn(player1);
							//model.validateManaCost((Card)M);
							hybridSpell = M;
						} catch (Exception e6) {
							view.putSoundInPlay(e6 , model.getCurrentHero());
							AlertBox.display("Error",e6.getMessage());
						} });
				}

				else if(model.getCurrentHero().getHand().get(i) instanceof MinionTargetSpell) {
					MinionTargetSpell M = (MinionTargetSpell) model.getCurrentHero().getHand().get(i);
					P1CardButton.setOnAction(e -> {
						try {
							model.validateTurn(player1);
							//model.validateManaCost((Card)M);
							curMinionTargetSpell = M;
						} catch (Exception e6) {
							view.putSoundInPlay(e6 , model.getCurrentHero());
							AlertBox.display("Error",e6.getMessage());
						}});
				}

				else if(model.getCurrentHero().getHand().get(i) instanceof FieldSpell)
				{
					FieldSpell x = (FieldSpell) model.getCurrentHero().getHand().get(i);
					P1CardButton.setOnAction(e -> {
						try {
							model.validateTurn(player1);
							int y = model.getCurrentHero().getHand().indexOf(x);
							model.getCurrentHero().castSpell(x);
							view.putSound("sounds/spellSound", 2 ,1);
							bottomView.getChildren().remove(p1Hand.get(y));
							p1Hand.remove(p1Hand.get(y));
							lastHeroHandSize = model.getCurrentHero().getHand().size();
							updateField();
							p1Mana.setText(player1.getCurrentManaCrystals()+"");
							p2Mana.setText(player2.getCurrentManaCrystals()+"");}
						

						catch (Exception e1) {
							view.putSoundInPlay(e1 , model.getCurrentHero());
						AlertBox.display("Error", e1.getMessage());}});


				}

				lastHeroHandSize = model.getCurrentHero().getHand().size();
			}
		}


		else {
			for(int i = (lastOppHandSize); i < model.getCurrentHero().getHand().size(); i++) {
				Button P1CardButton = new Button();
				Button P2CardButton= new Button();

				//getting data of card
				int manaOfCard = model.getCurrentHero().getHand().get(i).getManaCost();
				String manaOfCardText = manaOfCard+"";
				String healthOfMinionText;
				String attackOfMinionText;
				if( model.getCurrentHero().getHand().get(i) instanceof Minion)
				{
					Card minionCard = model.getCurrentHero().getHand().get(i);
					int healthOfMinion= ((Minion)minionCard).getCurrentHP();
					healthOfMinionText= healthOfMinion+"";
					int attackOfMinion = ((Minion)minionCard).getAttack();
					attackOfMinionText = attackOfMinion+"";
				}
				else
				{
					healthOfMinionText= "";
					attackOfMinionText ="";
				}

				setTextForButton(P1CardButton, false, "small", manaOfCardText, healthOfMinionText, attackOfMinionText);
				P1CardButton.setOnMouseEntered( e ->{ P1CardButton.setPrefSize(120, 200);
					setTextForButton(P1CardButton, true, "big", manaOfCardText, healthOfMinionText, attackOfMinionText);
				});
				P1CardButton.setOnMouseExited( e -> {P1CardButton.setPrefSize(80, 100);
					setTextForButton(P1CardButton, true, "small", manaOfCardText, healthOfMinionText, attackOfMinionText);
				});

				View.setImageForCard(model.getCurrentHero().getHand().get(i).getName(), P1CardButton);
				p2Hand.add(P1CardButton);
				topView.getChildren().add(P1CardButton);


				if(model.getCurrentHero().getHand().get(i) instanceof Minion) {
					Minion x = (Minion) model.getCurrentHero().getHand().get(i);
					P1CardButton.setOnAction(e -> {
						if(endflag)
						{
							try {
								P2CardButton.setPrefSize(120, 200);
								view.putGlowAnimation(P2CardButton, Color.WHITE);						
								int y = model.getCurrentHero().getHand().indexOf(x);
								model.getCurrentHero().playMinion(x);
								view.putSound("sounds/minionSummon", 2 ,1);
								int pic = model.getCurrentHero().getField().indexOf(x);
								setTextForButton(P2CardButton, false, "sizeInField", "", healthOfMinionText, attackOfMinionText);
								
								View.setImageForCard("inplay_minion_" + model.getCurrentHero().getField().get(pic).getName() + (((Minion) model.getCurrentHero().getField().get(pic)).isDivine() ? "_divine" : "")+(((Minion) model.getCurrentHero().getField().get(pic)).isSleeping() ? "_sleep" : ""), P2CardButton);
								//setting glow
								P2CardButton.setOnMouseEntered( j ->{ 
									view.putGlowAnimation(P2CardButton, Color.GOLD);
									popUpCard.setVisible(true);
									try {
										view.setImageForCard(x.getName(), popUpCard);
										setTextForButton(popUpCard, true, "deadCard", x.getManaCost()+"" , healthOfMinionText, attackOfMinionText);
										
									} catch (Exception e1) {
										view.putSoundInPlay(e1 , model.getCurrentHero());
										AlertBox.display("error", e1.getMessage());;
									}
								});
								P2CardButton.setOnMouseExited( h -> {
									view.putGlowAnimation(P2CardButton, Color.WHITE);
									popUpCard.setVisible(false);
									});
								p2Field.add(P2CardButton);
								p2FieldView.getChildren().add(P2CardButton);
								topView.getChildren().remove(p2Hand.get(y));
								p2Hand.remove(p2Hand.get(y));
								lastOppHandSize = model.getCurrentHero().getHand().size();
								p1Mana.setText(player1.getCurrentManaCrystals()+"");
								p2Mana.setText(player2.getCurrentManaCrystals()+"");

								P2CardButton.setOnAction(k -> {
									if(curLeech != null) {
										try {
											castLeechingSpell(curLeech, x, P2CardButton);
											view.putSound("sounds/spellSound", 2 ,1);
											curLeech = null;
											curMinionTargetSpell = null;
											//lastOppHandSize = model.getCurrentHero().getHand().size();
										} catch (Exception e7) {
											view.putSoundInPlay(e7 , model.getCurrentHero());
											AlertBox.display("Error", e7.getMessage());}
									}
									else if(curMinionTargetSpell != null){
										try {
											castMinionTargetSpell(curMinionTargetSpell, x, P2CardButton);
											view.putSound("sounds/spellSound", 2 ,1);
											curLeech = null;
											curMinionTargetSpell = null;
											//lastOppHandSize = model.getCurrentHero().getHand().size();
										} catch (Exception e7) {
											view.putSoundInPlay(e7 , model.getCurrentHero());
											AlertBox.display("error", e7.getMessage());}
									}
									else if(hybridSpell != null)
									{
										try {
											castMinionTargetSpell((MinionTargetSpell) hybridSpell, x, P2CardButton);
											view.putSound("sounds/spellSound", 2 ,1);
											hybridSpell = null;
											curLeech = null;
											curMinionTargetSpell = null;
										} catch (Exception e1) {
											view.putSoundInPlay(e1 , model.getCurrentHero());
											AlertBox.display("Error",e1.getMessage());
										}
									}
									else if(heroPowerPressed) {
										try{
											SummonHeroPower(x,P2CardButton);
											heroPowerPressed = false;}
										catch(Exception e7) {
											view.putSoundInPlay(e7 , model.getCurrentHero());
											AlertBox.display("Error",e7.getMessage());
										}
									}
									else
										attack2(x);
								});
							} catch (Exception e1) {
								view.putSoundInPlay(e1 , model.getCurrentHero());
								AlertBox.display("Error", e1.getMessage());
								curCard = null;
								attackPressed = false;
								P1CardButton.setPrefSize(80, 100);
								P1CardButton.setOnMouseEntered( f ->{ P1CardButton.setPrefSize(120, 200);
									setTextForButton(P1CardButton, true, "big", manaOfCardText, healthOfMinionText, attackOfMinionText);
								});
								P1CardButton.setOnMouseExited( h -> {P1CardButton.setPrefSize(80, 100);
									setTextForButton(P1CardButton, true, "small", manaOfCardText, healthOfMinionText, attackOfMinionText);
								});

							}}
						else
						{
						if(attackPressed)
						{
							NotSummonedException h8 = new NotSummonedException();
							view.putSoundInPlay(h8 , model.getCurrentHero());
							AlertBox.display("Error", "Cannot attack an unsummoned minion");
							attackPressed = false;
							curCard = null;
						}
						else{
							NotYourTurnException h8 = new NotYourTurnException() ;
							view.putSoundInPlay(h8 , model.getCurrentHero());
							AlertBox.display("Error", "Not your turn");
						}

					}});
				}
				else if(model.getCurrentHero().getHand().get(i) instanceof LeechingSpell) {
					LeechingSpell L = (LeechingSpell) model.getCurrentHero().getHand().get(i);
					P1CardButton.setOnAction(e -> {
						try {
							model.validateTurn(player2);
							//model.validateManaCost((Card)L);
							curLeech = L;
						} catch (Exception e6) {
							
							view.putSoundInPlay(e6 , model.getCurrentHero());
							AlertBox.display("Error",e6.getMessage());
						} });
				}

				else if(model.getCurrentHero().getHand().get(i) instanceof AOESpell) {
					AOESpell x = (AOESpell) model.getCurrentHero().getHand().get(i);
					P1CardButton.setOnAction(e -> {
						try {
							model.validateTurn(player2);
							//model.validateManaCost((Card)x);
							int y = model.getCurrentHero().getHand().indexOf(x);
							model.getCurrentHero().castSpell(x, model.getOpponent().getField());
							view.putSound("sounds/spellSound", 2 ,1);
							topView.getChildren().remove(p2Hand.get(y));
							p2Hand.remove(p2Hand.get(y));
							lastOppHandSize = model.getCurrentHero().getHand().size();
							updateField();
							p1Mana.setText(player1.getCurrentManaCrystals()+"");
							p2Mana.setText(player2.getCurrentManaCrystals()+"");
						} catch (Exception e1) {
							view.putSoundInPlay(e1 , model.getCurrentHero());
							AlertBox.display("Error", e1.getMessage());}});
				}

				else if(model.getCurrentHero().getHand().get(i).getName().equals("Pyroblast") || model.getCurrentHero().getHand().get(i).getName().equals("Kill Command"))
				{
					Spell M = (Spell) model.getCurrentHero().getHand().get(i);
					P1CardButton.setOnAction(e -> {
						try {
							model.validateTurn(player2);
							//model.validateManaCost((Card)M);
							hybridSpell = M;
						} catch (Exception e6) {
							view.putSoundInPlay(e6 , model.getCurrentHero());
							AlertBox.display("Error",e6.getMessage());
						} });
				}
				else if(model.getCurrentHero().getHand().get(i) instanceof MinionTargetSpell ) {
					MinionTargetSpell M = (MinionTargetSpell) model.getCurrentHero().getHand().get(i);
					P1CardButton.setOnAction(e -> {
						try {
							model.validateTurn(player2);
							//model.validateManaCost((Card)M);
							curMinionTargetSpell = M;
						} catch (Exception e6) {
							view.putSoundInPlay(e6 , model.getCurrentHero());
							AlertBox.display("Error",e6.getMessage());
						} });
				}
				else if(model.getCurrentHero().getHand().get(i) instanceof FieldSpell)
				{
					FieldSpell x = (FieldSpell) model.getCurrentHero().getHand().get(i);
					P1CardButton.setOnAction(e -> {
						try {
							model.validateTurn(player2);
							//model.validateManaCost((Card)x);
							int y = model.getCurrentHero().getHand().indexOf(x);
							model.getCurrentHero().castSpell(x);
							view.putSound("sounds/spellSound", 2 ,1);
							topView.getChildren().remove(p2Hand.get(y));
							p2Hand.remove(p2Hand.get(y));
							lastOppHandSize = model.getCurrentHero().getHand().size();
							updateField();
							p1Mana.setText(player1.getCurrentManaCrystals()+"");
							p2Mana.setText(player2.getCurrentManaCrystals()+"");}

						catch (Exception e1) {
							view.putSoundInPlay(e1 , model.getCurrentHero());
						AlertBox.display("Error", e1.getMessage());}});


				}

				lastOppHandSize = model.getCurrentHero().getHand().size();
			}
		}
	}

	private void SummonHeroPower(Minion x, Button b) throws CloneNotSupportedException, FullFieldException, NotEnoughManaException, NotYourTurnException, HeroPowerAlreadyUsedException, FullHandException, FileNotFoundException {
		int attack = 0;
		int updatedhp = 0;
		if(model.getCurrentHero() instanceof Mage) {
			if(!endflag) {
				if(p1Field.contains(b)){
					int indexOfAttacked1 = player1.getField().indexOf(x);
					attack = player1.getField().get(indexOfAttacked1).getAttack();
					((Mage)(model.getCurrentHero())).useHeroPower(x);
					view.putSound("sounds/heroPower1.mp3", 3,0.5);
					if(!model.getCurrentHero().getField().contains(x))
					{
						p1FieldView.getChildren().remove(b);
						p1Field.remove(indexOfAttacked1);
						view.putSound("sounds/deathOfMinion.mp3", 1, 0.5);
					}
					else{
						updatedhp = model.getCurrentHero().getField().get(indexOfAttacked1).getCurrentHP();
					   View.setImageForCard("inplay_minion_" + model.getCurrentHero().getField().get(indexOfAttacked1).getName() + (((Minion) model.getCurrentHero().getField().get(indexOfAttacked1)).isDivine() ? "_divine" : "")+(((Minion) model.getCurrentHero().getField().get(indexOfAttacked1)).isSleeping() ? "_sleep" : ""), b);
					}
					}
				else {
					int indexOfAttacked2 = player2.getField().indexOf(x);
					attack = player2.getField().get(indexOfAttacked2).getAttack();
					((Mage)(model.getCurrentHero())).useHeroPower(x);
					view.putSound("sounds/heroPower1.mp3", 3,0.5);
					if(!model.getOpponent().getField().contains(x))
					{
						p2FieldView.getChildren().remove(b);
						p2Field.remove(indexOfAttacked2);
						view.putSound("sounds/deathOfMinion.mp3", 1, 0.5);
					}
					else
					{
						updatedhp = model.getOpponent().getField().get(indexOfAttacked2).getCurrentHP();
					 View.setImageForCard("inplay_minion_" + model.getOpponent().getField().get(indexOfAttacked2).getName() + (((Minion) model.getOpponent().getField().get(indexOfAttacked2)).isDivine() ? "_divine" : "")+(((Minion) model.getOpponent().getField().get(indexOfAttacked2)).isSleeping() ? "_sleep" : ""), b);
					}
					}
				
			}
			else {
				if(p2Field.contains(b)){
					int indexOfAttacked2 = player2.getField().indexOf(x);
					attack = player2.getField().get(indexOfAttacked2).getAttack();
					((Mage)(model.getCurrentHero())).useHeroPower(x);
					view.putSound("sounds/heroPower1.mp3", 3,0.5);
					if(!model.getCurrentHero().getField().contains(x))
					{
						p2FieldView.getChildren().remove(b);
						p2Field.remove(indexOfAttacked2);
						view.putSound("sounds/deathOfMinion.mp3", 1, 0.5);
					}
					else{
						updatedhp = model.getCurrentHero().getField().get(indexOfAttacked2).getCurrentHP();
					View.setImageForCard("inplay_minion_" + model.getCurrentHero().getField().get(indexOfAttacked2).getName() + (((Minion) model.getCurrentHero().getField().get(indexOfAttacked2)).isDivine() ? "_divine" : "")+(((Minion) model.getCurrentHero().getField().get(indexOfAttacked2)).isSleeping() ? "_sleep" : ""), b);
					}
					
				}
				else {
					int indexOfAttacked1 = player1.getField().indexOf(x);
					attack = player1.getField().get(indexOfAttacked1).getAttack();
					((Mage)(model.getCurrentHero())).useHeroPower(x);
					view.putSound("sounds/heroPower1.mp3", 3,0.5);
					if(!model.getOpponent().getField().contains(x))
					{
						p1FieldView.getChildren().remove(b);
						p1Field.remove(indexOfAttacked1);
						view.putSound("sounds/deathOfMinion.mp3", 1, 0.5);
					}
					else{
						updatedhp = model.getOpponent().getField().get(indexOfAttacked1).getCurrentHP();
					 View.setImageForCard("inplay_minion_" + model.getOpponent().getField().get(indexOfAttacked1).getName() + (((Minion) model.getOpponent().getField().get(indexOfAttacked1)).isDivine() ? "_divine" : "")+(((Minion) model.getOpponent().getField().get(indexOfAttacked1)).isSleeping() ? "_sleep" : ""), b);
					}
				}
			}
		}

		else if(model.getCurrentHero() instanceof Priest){
			if(!endflag) {
				if(p1Field.contains(b)){
					int indexOfAttacked1 = player1.getField().indexOf(x);
					attack = player1.getField().get(indexOfAttacked1).getAttack();
					((Priest)(model.getCurrentHero())).useHeroPower(x);
					view.putSound("sounds/heroPower1.mp3", 3,0.5);
					updatedhp = model.getCurrentHero().getField().get(indexOfAttacked1).getCurrentHP();
				}
				else {
					int indexOfAttacked2 = player2.getField().indexOf(x);
					attack = player2.getField().get(indexOfAttacked2).getAttack();
					((Priest)(model.getCurrentHero())).useHeroPower(x);
					view.putSound("sounds/heroPower1.mp3", 3,0.5);
					updatedhp = model.getOpponent().getField().get(indexOfAttacked2).getCurrentHP();
				}
			}
			else {
				if(p2Field.contains(b)){
					int indexOfAttacked2 = player2.getField().indexOf(x);
					attack = player2.getField().get(indexOfAttacked2).getAttack();
					((Priest)(model.getCurrentHero())).useHeroPower(x);
					view.putSound("sounds/heroPower1.mp3", 3,0.5);
					updatedhp = model.getCurrentHero().getField().get(indexOfAttacked2).getCurrentHP();
				}
				else {
					int indexOfAttacked1 = player1.getField().indexOf(x);
					attack = player1.getField().get(indexOfAttacked1).getAttack();
					((Priest)(model.getCurrentHero())).useHeroPower(x);
					view.putSound("sounds/heroPower1.mp3", 3,0.5);
					updatedhp = model.getOpponent().getField().get(indexOfAttacked1).getCurrentHP();
				}
			}
		}
		setTextForButton(b, false, "sizeInField", "", updatedhp + "", attack + "");
		
		heroPowerPressed=false;
		p1Mana.setText(player1.getCurrentManaCrystals()+"");
		p2Mana.setText(player2.getCurrentManaCrystals()+"");
	}

	public void castLeechingSpell(LeechingSpell s, Minion m , Button b) throws NotYourTurnException, NotEnoughManaException{
		if(model.getCurrentHero().getCurrentManaCrystals() >= ((Card)s).getManaCost())
		{
			int indexofspell = model.getCurrentHero().getHand().indexOf(s);

			if(!endflag) {
				if(p1Field.contains(b)){
					int indexOfAttacked1 = player1.getField().indexOf(m);
					p1FieldView.getChildren().remove(b);
					p1Field.remove(indexOfAttacked1);
					view.putSound("sounds/deathOfMinion.mp3", 1, 0.5);
				}
				else {
					int indexOfAttacked2 = player2.getField().indexOf(m);
					p2FieldView.getChildren().remove(b);
					p2Field.remove(indexOfAttacked2);
					view.putSound("sounds/deathOfMinion.mp3", 1, 0.5);
				}
				bottomView.getChildren().remove(indexofspell);
				p1Hand.remove(indexofspell);
				model.getCurrentHero().castSpell(s, m);
				lastHeroHandSize = model.getCurrentHero().getHand().size();
				p1Mana.setText(player1.getCurrentManaCrystals()+"");
				p2Mana.setText(player2.getCurrentManaCrystals()+"");
			}
			else{
				if(p2Field.contains(b)){
					int indexOfAttacked2 = player2.getField().indexOf(m);
					p2FieldView.getChildren().remove(b);
					p2Field.remove(indexOfAttacked2);
					view.putSound("sounds/deathOfMinion.mp3", 1, 0.5);
				}
				else {
					int indexOfAttacked1 = player1.getField().indexOf(m);
					p1FieldView.getChildren().remove(b);
					p1Field.remove(indexOfAttacked1);
					view.putSound("sounds/deathOfMinion.mp3", 1, 0.5);
				}
				topView.getChildren().remove(indexofspell);
				p2Hand.remove(indexofspell);
				model.getCurrentHero().castSpell(s, m);
				lastOppHandSize = model.getCurrentHero().getHand().size();
				p1Mana.setText(player1.getCurrentManaCrystals()+"");
				p2Mana.setText(player2.getCurrentManaCrystals()+"");

			}
			
			p1Hero.setText(player1.getCurrentHP()+"");
			p2Hero.setText(player2.getCurrentHP()+"");
			view.putSound("sounds/spellSound", 2 ,1);
		
		}
		else{
			NotEnoughManaException h8 = new NotEnoughManaException();
			view.putSoundInPlay( h8 , model.getCurrentHero());
			AlertBox.display("Error","Not enough mana");
		}
	}



	public void castHeroTargetSpell(HeroTargetSpell s , Hero h) throws NotYourTurnException, NotEnoughManaException{

		int indButton = model.getCurrentHero().getHand().indexOf(s);
		model.getCurrentHero().castSpell(s,h);
		p1Mana.setText(player1.getCurrentManaCrystals()+"");
		p2Mana.setText(player2.getCurrentManaCrystals()+"");
		String updatedHeroHealth = model.getCurrentHero().getCurrentHP()+ "";
		String updatedOppHealth = model.getOpponent().getCurrentHP()+ "";
		if(!endflag)
		{
			p1Hero.setText(updatedHeroHealth);
			p2Hero.setText(updatedOppHealth);
			p1Hand.remove(indButton);
			bottomView.getChildren().remove(indButton);
			lastHeroHandSize = model.getCurrentHero().getHand().size();
			
		}
		else
		{
			p2Hero.setText(updatedHeroHealth);
			p1Hero.setText(updatedOppHealth);
			p2Hand.remove(indButton);
			topView.getChildren().remove(indButton);
			lastOppHandSize = model.getCurrentHero().getHand().size();
		}
		view.putSound("sounds/spellSound", 2 ,1);

	}
	public void castMinionTargetSpell (MinionTargetSpell s , Minion m, Button b ) throws NotYourTurnException, NotEnoughManaException, InvalidTargetException, FileNotFoundException{
			int indexofspell = model.getCurrentHero().getHand().indexOf(s);
			int updateattack = 0;
			int updatedhp = 0;
			if(!endflag) {
				if(p1Field.contains(b)){
					int indexOfAttacked1 = player1.getField().indexOf(m);
					model.getCurrentHero().castSpell(s, m);
					if(!model.getCurrentHero().getField().contains(m))
					{
						p1FieldView.getChildren().remove(b);
						p1Field.remove(indexOfAttacked1);
						view.putSound("sounds/deathOfMinion.mp3", 1, 0.5);
					}
					else{
						updatedhp = model.getCurrentHero().getField().get(indexOfAttacked1).getCurrentHP();
						updateattack = model.getCurrentHero().getField().get(indexOfAttacked1).getAttack();
						Minion attacked =model.getCurrentHero().getField().get(indexOfAttacked1);
						
						
						view.setImageForCard("inplay_minion_"+attacked.getName()+ (attacked.isDivine() ? "_divine" : "")+ (attacked.isSleeping() ? "_sleep" : ""), b);
					}
				}
				else {
					int indexOfAttacked2 = player2.getField().indexOf(m);
					model.getCurrentHero().castSpell(s, m);
					if(!model.getOpponent().getField().contains(m))
					{
						p2FieldView.getChildren().remove(b);
						p2Field.remove(indexOfAttacked2);
						view.putSound("sounds/deathOfMinion.mp3", 1, 0.5);
					}
					else{
						updatedhp = model.getOpponent().getField().get(indexOfAttacked2).getCurrentHP();
						updateattack = model.getOpponent().getField().get(indexOfAttacked2).getAttack();
						Minion attacked =model.getOpponent().getField().get(indexOfAttacked2);

						view.setImageForCard("inplay_minion_"+attacked.getName()+ (attacked.isDivine() ? "_divine" : "")+ (attacked.isSleeping() ? "_sleep" : ""), b);
						
					}
				}
				bottomView.getChildren().remove(indexofspell);
				p1Hand.remove(indexofspell);
				lastHeroHandSize = model.getCurrentHero().getHand().size();
			}
			else {
				if(p2Field.contains(b)){
					int indexOfAttacked2 = player2.getField().indexOf(m);
					model.getCurrentHero().castSpell(s, m);
					if(!model.getCurrentHero().getField().contains(m))
					{
						p2FieldView.getChildren().remove(b);
						p2Field.remove(indexOfAttacked2);
						view.putSound("sounds/deathOfMinion.mp3", 1, 0.5);
					}
					else{
						updatedhp = model.getCurrentHero().getField().get(indexOfAttacked2).getCurrentHP();
						updateattack = model.getCurrentHero().getField().get(indexOfAttacked2).getAttack();
						Minion attacked =model.getCurrentHero().getField().get(indexOfAttacked2);
						view.setImageForCard("inplay_minion_"+attacked.getName()+ (attacked.isDivine() ? "_divine" : "")+ (attacked.isSleeping() ? "_sleep" : ""), b);
					}
				}
				else {
					int indexOfAttacked1 = player1.getField().indexOf(m);
					model.getCurrentHero().castSpell(s, m);
					if(!model.getOpponent().getField().contains(m))
					{
						p1FieldView.getChildren().remove(b);
						p1Field.remove(indexOfAttacked1);
						view.putSound("sounds/deathOfMinion.mp3", 1, 0.5);
					}
					else{
						updatedhp = model.getOpponent().getField().get(indexOfAttacked1).getCurrentHP();
						updateattack = model.getOpponent().getField().get(indexOfAttacked1).getAttack();
						Minion attacked =model.getOpponent().getField().get(indexOfAttacked1);
						view.setImageForCard("inplay_minion_"+attacked.getName()+ (attacked.isDivine() ? "_divine" : "")+ (attacked.isSleeping() ? "_sleep" : ""), b);
					}

				}
				topView.getChildren().remove(indexofspell);
				p2Hand.remove(indexofspell);
				lastOppHandSize = model.getCurrentHero().getHand().size();
			}
			if(s instanceof Polymorph)
			{
				b.setOnMouseEntered( e ->{ 
					view.putGlowAnimation(b, Color.GOLD);
					popUpCard.setVisible(true);
					try {
						view.setImageForCard(m.getName(), popUpCard);
						setTextForButton(popUpCard, true, "deadCard", m.getManaCost()+"" , m.getCurrentHP()+"", m.getAttack()+"");
						
					} catch (Exception e1) {
						view.putSoundInPlay(e1 , model.getCurrentHero());
						AlertBox.display("error", e1.getMessage());;
					}
				});
				b.setOnMouseExited( e -> {
				view.putGlowAnimation(b, Color.WHITE);
				popUpCard.setVisible(false);
				});
			}
			setTextForButton(b, false, "sizeInField", "", updatedhp + "", updateattack + "");
			view.putSound("sounds/spellSound", 2 ,1);
			p1Mana.setText(player1.getCurrentManaCrystals()+"");
			p2Mana.setText(player2.getCurrentManaCrystals()+"");
		
	}
	
	public void setTextForButton(Button b, boolean insideLambda ,String size, String manaOfCard , String healthOfMinion, String attackOfMinion){

		if(insideLambda ==false)
		 {
				if(size=="small")
				{
				if(healthOfMinion !="" && attackOfMinion!= "" && manaOfCard !="")
				{
					int HM = Integer.parseInt(healthOfMinion);
					int AM = Integer.parseInt(attackOfMinion);
					if(AM>=10 || HM >=10){
						b.setText(manaOfCard +"\n"+"\n"+"\n"+"\n"+ attackOfMinion+"   "+healthOfMinion);
						b.setFont(Font.font("verdana", FontWeight.BOLD, FontPosture.REGULAR, 15));
						b.setAlignment(Pos.CENTER);
						b.setTextFill(Color.WHITE);
						b.setPrefSize(80, 100);
					}
					else{
				b.setText(manaOfCard +"\n"+"\n"+"\n"+"\n"+ attackOfMinion+"     "+healthOfMinion);
				b.setFont(Font.font("verdana", FontWeight.BOLD, FontPosture.REGULAR, 15));
				b.setAlignment(Pos.CENTER);
				b.setTextFill(Color.WHITE);
				b.setPrefSize(80, 100);
					}
				}
				else
				{
					b.setText(manaOfCard +"\n"+"\n"+"\n"+"\n"+ attackOfMinion+"     "+healthOfMinion);
					b.setFont(Font.font("verdana", FontWeight.BOLD, FontPosture.REGULAR, 15));
					b.setAlignment(Pos.TOP_LEFT);
					b.setTextFill(Color.WHITE);
					b.setPrefSize(80, 100);
					
				}
			}
				else if (size =="sizeInField"){
					b.setText("" +"\n"+"\n"+"\n"+ attackOfMinion+"       "+healthOfMinion);
					b.setFont(Font.font("verdana", FontWeight.BOLD, FontPosture.REGULAR, 15));
					b.setAlignment(Pos.CENTER);
					b.setTextFill(Color.WHITE);
					b.setPrefSize(120, 200);
				}

		}
			else
			{
			if(!endflag)
			{
				if(size =="big")
				{
					b.setText(manaOfCard +"\n"+"\n"+"\n"+"\n"+"\n"+"\n"+"\n"+ attackOfMinion+"          "+healthOfMinion);
					b.setFont(Font.font("verdana", FontWeight.BOLD, FontPosture.REGULAR, 15));
					//b.setAlignment(Pos.CENTER);
					b.setTextFill(Color.WHITE);
					b.setPrefSize(120, 200);
				}
				else if(size =="deadCard")
				{
					b.setText(manaOfCard +"\n"+"\n"+"\n"+"\n"+"\n"+"\n"+ attackOfMinion+"          "+healthOfMinion);
					b.setFont(Font.font("verdana", FontWeight.BOLD, FontPosture.REGULAR, 15));
					b.setAlignment(Pos.CENTER);
					b.setTextFill(Color.WHITE);
					//b.setPrefSize(120, 200);
				}
				else if (size=="small")
				{
					
					if(healthOfMinion !="" && attackOfMinion!= "" && manaOfCard !="")
					{
						int HM = Integer.parseInt(healthOfMinion);
						int AM = Integer.parseInt(attackOfMinion);
						if(AM>=10 || HM >=10){
							b.setText(manaOfCard +"\n"+"\n"+"\n"+"\n"+ attackOfMinion+"   "+healthOfMinion);
							b.setFont(Font.font("verdana", FontWeight.BOLD, FontPosture.REGULAR, 15));
							b.setAlignment(Pos.CENTER);
							b.setTextFill(Color.WHITE);
							b.setPrefSize(80, 100);
						}
						else{
					b.setText(manaOfCard +"\n"+"\n"+"\n"+"\n"+ attackOfMinion+"      "+healthOfMinion);
					b.setFont(Font.font("verdana", FontWeight.BOLD, FontPosture.REGULAR, 15));
					b.setAlignment(Pos.CENTER);
					b.setTextFill(Color.WHITE);
					b.setPrefSize(80, 100);
						}
					}
					else
					{
						b.setText(manaOfCard +"\n"+"\n"+"\n"+"\n"+ attackOfMinion+"      "+healthOfMinion);
						b.setFont(Font.font("verdana", FontWeight.BOLD, FontPosture.REGULAR, 15));
						b.setAlignment(Pos.TOP_LEFT);
						b.setTextFill(Color.WHITE);
						b.setPrefSize(80, 100);
						
					}
				}
			}
			else
			{
				if(size =="big")
				{
					b.setText(manaOfCard +"\n"+"\n"+"\n"+"\n"+"\n"+"\n"+ attackOfMinion+"           "+healthOfMinion);
					b.setFont(Font.font("verdana", FontWeight.BOLD, FontPosture.REGULAR, 15));
					//b.setAlignment(Pos.CENTER);
					b.setTextFill(Color.WHITE);
					b.setPrefSize(120, 200);
				}
				else if(size =="deadCard")
				{
					b.setText(manaOfCard +"\n"+"\n"+"\n"+"\n"+"\n"+"\n"+ attackOfMinion+"          "+healthOfMinion);
					b.setFont(Font.font("verdana", FontWeight.BOLD, FontPosture.REGULAR, 15));
					b.setAlignment(Pos.CENTER);
					b.setTextFill(Color.WHITE);
					//b.setPrefSize(120, 200);
				}
				else if (size=="small")
				{
					
					if(healthOfMinion !="" && attackOfMinion!= "" && manaOfCard !="")
					{
						int HM = Integer.parseInt(healthOfMinion);
						int AM = Integer.parseInt(attackOfMinion);
						if(AM>=10 || HM >=10){
							b.setText(manaOfCard +"\n"+"\n"+"\n"+"\n"+ attackOfMinion+"   "+healthOfMinion);
							b.setFont(Font.font("verdana", FontWeight.BOLD, FontPosture.REGULAR, 15));
							b.setAlignment(Pos.CENTER);
							b.setTextFill(Color.WHITE);
							b.setPrefSize(80, 100);
						}
						else{
					b.setText(manaOfCard +"\n"+"\n"+"\n"+"\n"+ attackOfMinion+"      "+healthOfMinion);
					b.setFont(Font.font("verdana", FontWeight.BOLD, FontPosture.REGULAR, 15));
					b.setAlignment(Pos.CENTER);
					b.setTextFill(Color.WHITE);
					b.setPrefSize(80, 100);
						}
					}
					else
					{
						b.setText(manaOfCard +"\n"+"\n"+"\n"+"\n"+ attackOfMinion+"      "+healthOfMinion);
						b.setFont(Font.font("verdana", FontWeight.BOLD, FontPosture.REGULAR, 15));
						b.setAlignment(Pos.TOP_LEFT);
						b.setTextFill(Color.WHITE);
						b.setPrefSize(80, 100);
						
					}
				}

			}

			}
		}

	public void attack1(Minion x )
	{
		int pCurrent = model.getCurrentHero().getField().indexOf(x);
		if(!endflag)
		{
			curCard = p1Field.get(pCurrent);
			attackPressed = true;
		}
		else
		{
			int pOpponent = model.getOpponent().getField().indexOf(x);
			if(attackPressed)
			{
				int indofAttacker = p2Field.indexOf(curCard);
				int indofDefender = pOpponent;
				Minion attacker = model.getCurrentHero().getField().get(indofAttacker);
				Minion defender = model.getOpponent().getField().get(indofDefender);
				curAttacker = p2Field.get(indofAttacker);
				curAttackerhp = attacker.getCurrentHP() + "";
				curAttackeratt = attacker.getAttack() + "";
				if(attacker instanceof Minion && defender instanceof Minion)
				{
					try {
						model.getCurrentHero().attackWithMinion((Minion)attacker, (Minion)defender);
						
	
						if(((Minion)attacker).getCurrentHP() <= 0)
						{
							p2Field.remove(indofAttacker);
							p2FieldView.getChildren().remove(indofAttacker);
							view.putSound("sounds/deathOfMinion.mp3", 1, 0.5);
						}
						else
						{
							
							setTextForButton(p2Field.get(indofAttacker), false, "sizeInField","", attacker.getCurrentHP()+"", attacker.getAttack()+"");
							view.setImageForCard("inplay_minion_"+attacker.getName()+ (attacker.isDivine() ? "_divine" : "")+ (attacker.isSleeping() ? "_sleep" : ""),p2Field.get(indofAttacker));
							view.putSound("sounds/Battlecry_1.mp3", 1, 0.5);
						}
						if(((Minion)defender).getCurrentHP() <= 0)
						{
							
							p1Field.remove(indofDefender);
							p1FieldView.getChildren().remove(indofDefender);
							view.putSound("sounds/deathOfMinion.mp3", 1, 0.5);
						}
						else
						{
							setTextForButton(p1Field.get(indofDefender), false, "sizeInField", "", defender.getCurrentHP()+"", defender.getAttack()+"");
							view.setImageForCard("inplay_minion_"+defender.getName()+ (defender.isDivine() ? "_divine" : "")+ (defender.isSleeping() ? "_sleep" : ""), p1Field.get(indofDefender));
							view.putSound("sounds/Battlecry_1.mp3", 1, 0.5);
						}
						curCard = null;
						attackPressed = false;
					} catch (Exception e1) {
						view.putSoundInPlay(e1 , model.getCurrentHero());
						AlertBox.display("Error", e1.getMessage());
						curCard = null;
						attackPressed = false;

					}
				}


			}
			else
			{
				NotYourTurnException h8 = new NotYourTurnException() ;
				view.putSoundInPlay(h8 , model.getCurrentHero());
				AlertBox.display("Error", "Not your turn");
				curCard = null;
				attackPressed = false;
			}
		
		}
		
	}

	public void attack2(Minion x)
	{
		int pCurrent = model.getCurrentHero().getField().indexOf(x);
		if(endflag)
		{
			curCard = p2Field.get(pCurrent);
			attackPressed = true;
		}
		else
		{
			int pOpponent = model.getOpponent().getField().indexOf(x);
			if(attackPressed)
			{
				int indofAttacker = p1Field.indexOf(curCard);
				int indofDefender = pOpponent;
				Minion attacker = model.getCurrentHero().getField().get(indofAttacker);
				Minion defender = model.getOpponent().getField().get(indofDefender);
				curAttacker = p1Field.get(indofAttacker);
				curAttackerhp = attacker.getCurrentHP() + "";
				curAttackeratt = attacker.getAttack() + "";
				if(attacker instanceof Minion && defender instanceof Minion)
				{
					try {
						model.getCurrentHero().attackWithMinion((Minion)attacker, defender);
						if(((Minion)attacker).getCurrentHP() <= 0)
						{
							p1Field.remove(indofAttacker);
							p1FieldView.getChildren().remove(indofAttacker);
							view.putSound("sounds/deathOfMinion.mp3", 1, 0.5);
						}
						else
						{
							
							setTextForButton(p1Field.get(indofAttacker), false, "sizeInField", "", attacker.getCurrentHP()+"", attacker.getAttack()+"");
							view.setImageForCard("inplay_minion_"+attacker.getName()+ (attacker.isDivine() ? "_divine" : "")+ (attacker.isSleeping() ? "_sleep" : ""), p1Field.get(indofAttacker));
							view.putSound("sounds/Battlecry_1.mp3", 1, 0.5);
							
						}
						if(((Minion)defender).getCurrentHP() <= 0)
						{
							p2Field.remove(indofDefender);
							p2FieldView.getChildren().remove(indofDefender);
							view.putSound("sounds/deathOfMinion.mp3", 1, 0.5);
						}
						else
						{
							setTextForButton(p2Field.get(indofDefender), false, "sizeInField" ,"", defender.getCurrentHP()+"", defender.getAttack()+"");
							view.setImageForCard("inplay_minion_"+defender.getName()+ (defender.isDivine() ? "_divine" : "")+ (defender.isSleeping() ? "_sleep" : ""), p2Field.get(indofDefender));
							view.putSound("sounds/Battlecry_1.mp3", 1, 0.5);
						}
				
						curCard = null;
						attackPressed = false;
					} catch (Exception e1) {
						view.putSoundInPlay(e1 , model.getCurrentHero());
						AlertBox.display("Error", e1.getMessage());
						curCard = null;
						attackPressed = false;

					}
				}
			}
			else
			{
				
				NotYourTurnException h8 = new NotYourTurnException() ;
				view.putSoundInPlay(h8 , model.getCurrentHero());
				AlertBox.display("Error", "Not your turn");
				curCard = null;
				attackPressed = false;
			}
			
		}
	}




	@Override
	public void start(Stage primaryStage) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void onGameOver() {
		if(player1.getCurrentHP() <=0){
		    AlertBox.display("GAME OVER", "Game over, " + player2.getName()+ " won the match");
		}
		else{
			AlertBox.display("GAME OVER", "Game over, " + player1.getName()+ " won the match");
		}
		view.getWindow().close();
		
	}
}