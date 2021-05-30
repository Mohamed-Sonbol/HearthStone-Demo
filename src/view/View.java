package view;

import engine.Controller;

import exceptions.CannotAttackException;
import exceptions.FullFieldException;
import exceptions.FullHandException;
import exceptions.InvalidTargetException;
import exceptions.NotEnoughManaException;
import exceptions.NotSummonedException;
import exceptions.NotYourTurnException;
import exceptions.TauntBypassException;
import javafx.animation.Animation;
import javafx.animation.FadeTransition;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Light;
import javafx.scene.effect.Lighting;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.media.AudioClip;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.util.Duration;
import model.heroes.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter.DEFAULT;

public class View extends Application implements EventHandler<ActionEvent>{

    private Stage window;
    private Scene start,choose,game;
    private Button mage,hunter,priest,paladin,warlock,PICK;
    private String currenthero;
    private Hero Player1,Player2;
    private Label chooseLabel;
    private boolean flag = false;
    private Pane layout;
    private HBox heroHand,heroField,oppHand,oppField;
    private VBox p1Mana,p2Mana;
    
    
	public VBox getP1Mana() {
		return p1Mana;
	}
	public VBox getP2Mana() {
		return p2Mana;
	}


	public static MediaPlayer mediaPlayer;
	private int pickCount;
	
    
  
    public Scene getChoose(){
    	return choose;
    }
    public Pane getLayout() {
		return layout;
	}
    
	public static void main(String[] args) {
        launch(args);
    }

	
	public static void putImage(String path,Button b) throws FileNotFoundException{
		// create a input stream 
        FileInputStream input = new FileInputStream(path); 

        // create a image 
        Image image = new Image(input); 
    
        

        // create a background image 
        BackgroundImage backgroundimage = new BackgroundImage(image,  
                                         BackgroundRepeat.NO_REPEAT,  
                                         BackgroundRepeat.NO_REPEAT,  
                                         BackgroundPosition.CENTER,  
                                         new BackgroundSize(image.getWidth(), image.getHeight(), true ,true ,true,false)) ; 

        // create Background 
        Background background = new Background(backgroundimage); 
        b.setBackground(background);
   
	}

	public static void putSound(String path,int time, double volume){
	
		String musicFile = path;     // For example
		Media sound = new Media(new File(musicFile).toURI().toString());
		mediaPlayer = new MediaPlayer(sound);
		if(time==0){
			mediaPlayer.setCycleCount(mediaPlayer.INDEFINITE);
		}
		else{
		mediaPlayer.setStartTime(Duration.ZERO);
		mediaPlayer.setStopTime(Duration.seconds(time));
		}
		mediaPlayer.play();
		mediaPlayer.setVolume(volume);
		
		
	}
	
	public static void putSoundInPlay(Exception e, Hero h){
				
	if(h instanceof Mage ){
		
		if( e instanceof FullHandException ){
			putSound("sounds/soundOfTruth/handTooFullMage.mp3", 2, 0.5);
		}
		else if(e instanceof FullFieldException)
		{
			putSound("sounds/soundOfTruth/fullFieldMage.mp3", 2, 0.5);
		}
		else if(e instanceof NotEnoughManaException)
		{
			putSound("sounds/soundOfTruth/cantPlayThatMage.mp3", 2, 0.5);
		}
		else if(e instanceof TauntBypassException)
		{
			putSound("sounds/soundOfTruth/invalidTargetMage.mp3", 2, 0.5);
		}
		else if(e instanceof CannotAttackException)
		{
			putSound("sounds/soundOfTruth/cantPlayThatMage.mp3", 2, 0.5);
		}
		else if(e instanceof InvalidTargetException)
		{
			putSound("sounds/soundOfTruth/invalidTargetMage.mp3", 2, 0.5);
		}
		else if(e instanceof NotYourTurnException)
		{
			putSound("sounds/soundOfTruth/cantPlayThatMage.mp3", 2, 0.5);
		}
		else if(e instanceof NotSummonedException)
		{
			putSound("sounds/soundOfTruth/invalidTargetMage.mp3", 2, 0.5);
		}
		
	}
	else
	{
		if( e instanceof FullHandException ){
			putSound("sounds/soundOfTruth/handTooFull.mp3", 2, 0.5);
		}
		else if(e instanceof FullFieldException)
		{
			putSound("sounds/soundOfTruth/fullField.mp3", 2, 0.5);
		}
		else if(e instanceof NotEnoughManaException)
		{
			putSound("sounds/soundOfTruth/manaError.mp3", 2, 0.5);
		}
		else if(e instanceof TauntBypassException)
		{
			putSound("sounds/soundOfTruth/tauntBypass.mp3", 2, 0.5);
		}
		else if(e instanceof CannotAttackException)
		{
			putSound("sounds/soundOfTruth/cantPlayThat.mp3", 2, 0.5);
		}
		else if(e instanceof InvalidTargetException)
		{
			putSound("sounds/soundOfTruth/invalidTarget.mp3", 2, 0.5);
		}
		else if(e instanceof NotYourTurnException)
		{
			putSound("sounds/soundOfTruth/cantPlayThat.mp3", 2, 0.5);
		}
		else if(e instanceof NotSummonedException)
		{
			putSound("sounds/soundOfTruth/invalidTarget.mp3", 2, 0.5);
		}
	}
	}
	
	public static void backgroundSound(String path, double volume){
		
		String musicFile = path; 
		AudioClip soundClip = new AudioClip(new File(musicFile).toURI().toString());
        soundClip.setCycleCount(5);
        soundClip.play();
        mediaPlayer.setVolume(volume);
	}
	
	
	public static void setImageForCard(String s,Button b) throws FileNotFoundException
	//Shadow Word: Death has a problem
	//frostwolf warlord not found
	//tirion folding
	{
       switch(s)
       {
       //in hand
       case "Argent Commander" : View.putImage("images/hearthstonePlayingCards/"+ s , b);break;
       case "King Krush" : View.putImage("images/hearthstonePlayingCards/"+ s , b);break;
       case "Bloodfen Raptor" : View.putImage("images/hearthstonePlayingCards/"+ s , b);break;
       case "Boulderfist Ogre" : View.putImage("images/hearthstonePlayingCards/"+ s , b);break;
       case "Chilwind Yeti" : View.putImage("images/hearthstonePlayingCards/"+ s , b);break;
       case "Chromaggus" : View.putImage("images/hearthstonePlayingCards/"+ s , b);break;
       case "Colossus of the Moon" : View.putImage("images/hearthstonePlayingCards/"+ s , b);break;
       case "Core Hound" : View.putImage("images/hearthstonePlayingCards/"+ s , b);break;
       case "Curse of Weakness" : View.putImage("images/hearthstonePlayingCards/"+ s , b);break;
       case "Divine Spirit" : View.putImage("images/hearthstonePlayingCards/"+ s , b);break;
       case "Flamestrike" : View.putImage("images/hearthstonePlayingCards/"+ s , b);break;
       case "Frostwolf Grunt" : View.putImage("images/hearthstonePlayingCards/"+ s , b);break;
       case "Goldshire Footman" : View.putImage("images/hearthstonePlayingCards/"+ s , b);break;
       case "Holy Nova" : View.putImage("images/hearthstonePlayingCards/"+ s , b);break;
       case "Icehowl" : View.putImage("images/hearthstonePlayingCards/"+ s , b);break;
       case "Kalycgos" : View.putImage("images/hearthstonePlayingCards/"+ s , b);break;
       case "Kill Command" : View.putImage("images/hearthstonePlayingCards/"+ s , b);break;
       case "Level Up!" : View.putImage("images/hearthstonePlayingCards/"+ s , b);break;
       case "Multi-Shot" : View.putImage("images/hearthstonePlayingCards/"+ s , b);break;
       case "Polymorph" : View.putImage("images/hearthstonePlayingCards/"+ s , b);break;
       case "Prophet Velen" : View.putImage("images/hearthstonePlayingCards/"+ s , b);break;
       case "Pyroblast" : View.putImage("images/hearthstonePlayingCards/"+ s , b);break;
       case "Seal of Champions" : View.putImage("images/hearthstonePlayingCards/"+ s , b);break;
       case "Shadow Word: Death" : View.putImage("images/hearthstonePlayingCards/"+ "Shadow Word Death", b);break;
       case "Siphon Soul" : View.putImage("images/hearthstonePlayingCards/"+ s , b);break;
       case "Stonetusk Boar" : View.putImage("images/hearthstonePlayingCards/"+ s , b);break;
       case "Sunwalker" : View.putImage("images/hearthstonePlayingCards/"+ s , b);break;
       case "The LichKing" : View.putImage("images/hearthstonePlayingCards/"+ s , b);break;
       case "Twisting Nether" : View.putImage("images/hearthstonePlayingCards/"+ s , b);break;
       case "Wilfred Fizzlebang" : View.putImage("images/hearthstonePlayingCards/"+ s , b);break;
       case "Wolfrider" : View.putImage("images/hearthstonePlayingCards/"+ s , b);break;
       case "Tirion Fordring" : View.putImage("images/hearthstonePlayingCards/"+ s , b);break;
       case "Sheep" : View.putImage("images/hearthstonePlayingCards/"+ s , b);break;
       case "Silver Hand Recruit" : View.putImage("images/hearthstonePlayingCards/"+ s , b);break;
       
       //in play
       case "inplay_minion_Argent Commander" : View.putImage("images/minions_inplay/"+ s , b);break;
       case "inplay_minion_King Krush" : View.putImage("images/minions_inplay/"+ s , b);break;
       case "inplay_minion_Colossus of the Moon" : View.putImage("images/minions_inplay/"+ s , b);break;
       case "inplay_minion_Bloodfen Raptor" : View.putImage("images/minions_inplay/"+ s , b);break;
       case "inplay_minion_Boulderfist Ogre" : View.putImage("images/minions_inplay/"+ s , b);break;
       case "inplay_minion_Chilwind Yeti" : View.putImage("images/minions_inplay/"+ s , b);break;
       case "inplay_minion_Chromaggus" : View.putImage("images/minions_inplay/"+ s , b);break;
       case "inplay_minion_Core Hound" : View.putImage("images/minions_inplay/"+ s , b);break;
       case "inplay_minion_Frostwolf Grunt" : View.putImage("images/minions_inplay/"+ s , b);break;
       case "inplay_minion_Goldshire Footman" : View.putImage("images/minions_inplay/"+ s , b);break;
       case "inplay_minion_Icehowl" : View.putImage("images/minions_inplay/"+ s , b);break;
       case "inplay_minion_Kalycgos" : View.putImage("images/minions_inplay/"+ s , b);break;
       case "inplay_minion_Level Up!" : View.putImage("images/minions_inplay/"+ s , b);break;
       case "inplay_minion_Prophet Velen" : View.putImage("images/minions_inplay/"+ s , b);break;
       case "inplay_minion_Stonetusk Boar" : View.putImage("images/minions_inplay/"+ s , b);break;
       case "inplay_minion_Sunwalker" : View.putImage("images/minions_inplay/"+ s , b);break;
       case "inplay_minion_The LichKing" : View.putImage("images/minions_inplay/"+ s , b);break;
       case "inplay_minion_Wilfred Fizzlebang" : View.putImage("images/minions_inplay/"+ s , b);break;
       case "inplay_minion_Wolfrider" : View.putImage("images/minions_inplay/"+ s , b);break;
       case "inplay_minion_Tirion Fordring" : View.putImage("images/minions_inplay/"+ s , b);break;
       case "inplay_minion_Sheep" : View.putImage("images/minions_inplay/"+ s , b);break;
       case "inplay_minion_Silver Hand Recruit" : View.putImage("images/minions_inplay/"+ s , b);break;
       
       //heroes
       case "Anduin Wrynn" : View.putImage("images/inplay_heroes/"+ s , b);break;
       case "Gul'dan" : View.putImage("images/inplay_heroes/"+ s , b);break;
       case "Jaina Proudmoore" : View.putImage("images/inplay_heroes/"+ s , b);break;
       case "Rexxar" : View.putImage("images/inplay_heroes/"+ s , b);break;
       case "Uther Lightbringer" : View.putImage("images/inplay_heroes/"+ s , b);break;
       
       //heropowers
       case "Anduin Wrynn_power" : View.putImage("images/heropowers/"+ s  , b);break;
       case "Gul'dan_power" : View.putImage("images/heropowers/"+ s  , b);break;
       case "Jaina Proudmoore_power" : View.putImage("images/heropowers/"+ s  , b);break;
       case "Rexxar_power" : View.putImage("images/heropowers/"+ s  , b);break;
       case "Uther Lightbringer_power" : View.putImage("images/heropowers/"+ s  , b);break;
       case "Card back" : View.putImage("images/hearthstonePlayingCards/"+ s  , b);break;
       
       //divine minions
       
       case "inplay_minion_Argent Commander_divine" : View.putImage("images/Divine_Minions/"+ s , b);break;
       case "inplay_minion_King Krush_divine" : View.putImage("images/Divine_Minions/"+ s , b);break;
       case "inplay_minion_Bloodfen Raptor_divine" : View.putImage("images/Divine_Minions/"+ s , b);break;
       case "inplay_minion_Boulderfist Ogre_divine" : View.putImage("images/Divine_Minions/"+ s , b);break;
       case "inplay_minion_Chilwind Yeti_divine" : View.putImage("images/Divine_Minions/"+ s , b);break;
       case "inplay_minion_Chromaggus_divine" : View.putImage("images/Divine_Minions/"+ s , b);break;
       case "inplay_minion_Core Hound_divine" : View.putImage("images/Divine_Minions/"+ s , b);break;
       case "inplay_minion_Frostwolf Grunt_divine" : View.putImage("images/Divine_Minions/"+ s , b);break;
       case "inplay_minion_Goldshire Footman_divine" : View.putImage("images/Divine_Minions/"+ s , b);break;
       case "inplay_minion_Icehowl_divine" : View.putImage("images/Divine_Minions/"+ s , b);break;
       case "inplay_minion_Kalycgos_divine" : View.putImage("images/Divine_Minions/"+ s , b);break;
       case "inplay_minion_Prophet Velen_divine" : View.putImage("images/Divine_Minions/"+ s , b);break;
       case "inplay_minion_Stonetusk Boar_divine" : View.putImage("images/Divine_Minions/"+ s , b);break;
       case "inplay_minion_The LichKing_divine" : View.putImage("images/Divine_Minions/"+ s , b);break;
       case "inplay_minion_Wilfred Fizzlebang_divine" : View.putImage("images/Divine_Minions/"+ s , b);break;
       case "inplay_minion_Wolfrider_divine" : View.putImage("images/Divine_Minions/"+ s , b);break;
       case "inplay_minion_Tirion Fordring_divine" : View.putImage("images/Divine_Minions/"+ s , b);break;
       case "inplay_minion_Colossus of the Moon_divine" : View.putImage("images/Divine_Minions/"+ s , b);break;
       case "inplay_minion_Sunwalker_divine" : View.putImage("images/Divine_Minions/"+ s , b);break;
       case "inplay_minion_Sheep_divine" : View.putImage("images/Divine_Minions/"+ s, b);break;
       case "inplay_minion_Silver Hand Recruit_divine" : View.putImage("images/Divine_Minions/"+ s , b);break;
       
       //sleep minions
       
    
    
       case "inplay_minion_Bloodfen Raptor_sleep" : View.putImage("images/Sleep_Minions/"+ s , b);break;
       case "inplay_minion_Boulderfist Ogre_sleep" : View.putImage("images/Sleep_Minions/"+ s , b);break;
       case "inplay_minion_Chilwind Yeti_sleep" : View.putImage("images/Sleep_Minions/"+ s , b);break;
       case "inplay_minion_Chromaggus_sleep" : View.putImage("images/Sleep_Minions/"+ s , b);break;
       case "inplay_minion_Core Hound_sleep" : View.putImage("images/Sleep_Minions/"+ s , b);break;
       case "inplay_minion_Frostwolf Grunt_sleep" : View.putImage("images/Sleep_Minions/"+ s, b);break;
       case "inplay_minion_Goldshire Footman_sleep" : View.putImage("images/Sleep_Minions/"+ s , b);break;
       case "inplay_minion_Icehowl_sleep" : View.putImage("images/Sleep_Minions/"+ s, b);break;
       case "inplay_minion_Kalycgos_sleep" : View.putImage("images/Sleep_Minions/"+ s , b);break;
       case "inplay_minion_Prophet Velen_sleep" : View.putImage("images/Sleep_Minions/"+ s , b);break;
       case "inplay_minion_The LichKing_sleep" : View.putImage("images/Sleep_Minions/"+ s , b);break;
       case "inplay_minion_Wilfred Fizzlebang_sleep" : View.putImage("images/Sleep_Minions/"+ s , b);break;
       case "inplay_minion_Tirion Fordring_sleep" : View.putImage("images/Sleep_Minions/"+ s , b);break;
       case "inplay_minion_Colossus of the Moon_sleep" : View.putImage("images/Sleep_Minions/"+ s , b);break;
       case "inplay_minion_Sunwalker_sleep" : View.putImage("images/Sleep_Minions/"+ s , b);break;
       case "inplay_minion_Sheep_sleep" : View.putImage("images/Sleep_Minions/"+ s , b);break;
       case "inplay_minion_Silver Hand Recruit_sleep" : View.putImage("images/Sleep_Minions/"+ s , b);break;
       
       
       //divine and sleep
       case "inplay_minion_Bloodfen Raptor_divine_sleep" : View.putImage("images/Sleep_Minions/"+ s , b);break;
       case "inplay_minion_Boulderfist Ogre_divine_sleep" : View.putImage("images/Sleep_Minions/"+ s , b);break;
       case "inplay_minion_Chilwind Yeti_divine_sleep" : View.putImage("images/Sleep_Minions/"+ s , b);break;
       case "inplay_minion_Chromaggus_divine_sleep" : View.putImage("images/Sleep_Minions/"+ s , b);break;
       case "inplay_minion_Core Hound_divine_sleep" : View.putImage("images/Sleep_Minions/"+ s , b);break;
       case "inplay_minion_Frostwolf Grunt_divine_sleep" : View.putImage("images/Sleep_Minions/"+ s, b);break;
       case "inplay_minion_Goldshire Footman_divine_sleep" : View.putImage("images/Sleep_Minions/"+ s , b);break;
       case "inplay_minion_Icehowl_divine_sleep" : View.putImage("images/Sleep_Minions/"+ s, b);break;
       case "inplay_minion_Kalycgos_divine_sleep" : View.putImage("images/Sleep_Minions/"+ s , b);break;
       case "inplay_minion_Prophet Velen_divine_sleep" : View.putImage("images/Sleep_Minions/"+ s , b);break;
       case "inplay_minion_The LichKing_divine_sleep" : View.putImage("images/Sleep_Minions/"+ s , b);break;
       case "inplay_minion_Wilfred Fizzlebang_divine_sleep" : View.putImage("images/Sleep_Minions/"+ s , b);break;
       case "inplay_minion_Tirion Fordring_divine_sleep" : View.putImage("images/Sleep_Minions/"+ s , b);break;
       case "inplay_minion_Colossus of the Moon_divine_sleep" : View.putImage("images/Sleep_Minions/"+ s , b);break;
       case "inplay_minion_Sunwalker_divine_sleep" : View.putImage("images/Sleep_Minions/"+ s , b);break;
       case "inplay_minion_Sheep_divine_sleep" : View.putImage("images/Sleep_Minions/"+ s , b);break;
       case "inplay_minion_Silver Hand Recruit_divine_sleep" : View.putImage("images/Sleep_Minions/"+ s , b);break;
       
       }
		

	}
	
	public static void putFadeAnimation(Button b)
	{
		 FadeTransition trans2 = new FadeTransition();
	        trans2.setDuration(Duration.seconds(1));
			trans2.setFromValue(1.0f);
			trans2.setToValue(0.4f);
			trans2.setAutoReverse(true);
			trans2.setCycleCount(Animation.INDEFINITE);
			trans2.setNode(b);
			trans2.play();;
	}
	 public static void putGlowAnimation(Button b , Color c){
		 int depth = 70; //Setting the uniform variable for the glow width and height
	        
	        DropShadow borderGlow= new DropShadow();
	        borderGlow.setOffsetY(0f);
	        borderGlow.setOffsetX(0f);
	        borderGlow.setColor(c); 
	        borderGlow.setWidth(depth);
	        borderGlow.setHeight(depth);
	         
	        b.setEffect(borderGlow); //Apply the borderGlow effect to the JavaFX node
	 }
	 public static void putGlowAnimationForMainMenu(Button b , Color c){
		    int depth = 70; //Setting the uniform variable for the glow width and height
	        DropShadow borderGlow= new DropShadow();
	        borderGlow.setOffsetY(0f);
	        borderGlow.setOffsetX(0f);
	        borderGlow.setColor(c); 
	        borderGlow.setWidth(depth);
	        borderGlow.setHeight(depth); 
	        b.setEffect(null);
	        
	        b.setOnMouseEntered( f ->{ 
	        b.setEffect(borderGlow);
		});
		b.setOnMouseExited( h -> {
		 b.setEffect(null);
		});
	 }
    
	
	
    @Override
    public void start(Stage primaryStage) throws Exception {
        window = primaryStage;
        window.setTitle("HearthStone");
        window.centerOnScreen();
        window.sizeToScene();
        window.setResizable(false);
      
        putSound("sounds/duel.mp3", 0, 0.5);
        
        
		Button buttonStart = new Button();
        buttonStart.setMinSize(140, 150); 
        putGlowAnimationForMainMenu(buttonStart, Color.GOLD);
        putImage("images/mainmenuPlaybutton.png", buttonStart);
        buttonStart.setFont(Font.font("verdana", FontWeight.BOLD, FontPosture.REGULAR, 40));
        buttonStart.setOnAction(e -> {
        	window.setScene(choose);
        	mediaPlayer.stop();
        	putSound("sounds/Choose your character Sound Effect.mp3",2,0.5);
        	window.centerOnScreen();
        });
        

        FileInputStream input1 = new FileInputStream("images/hearthstone2.jpg"); 
        Image image1 = new Image(input1);
        BackgroundImage backgroundimage1 = new BackgroundImage(image1,  
                                         BackgroundRepeat.NO_REPEAT,  
                                         BackgroundRepeat.NO_REPEAT,  
                                         BackgroundPosition.CENTER,  
                                            BackgroundSize.DEFAULT) ; 
        Background background1 = new Background(backgroundimage1); 

      
        BorderPane layout1 = new BorderPane();
        layout1.setBackground(background1);
        
        HBox home =new HBox();
        home.getChildren().add(buttonStart);
        layout1.setBottom(home);
        home.setSpacing(20);
        home.setAlignment(Pos.CENTER);

        start = new Scene(layout1,1280,720);
        window.setScene(start);
        window.centerOnScreen();
        //window.setFullScreen(true);
        window.show();
        
        mage = new Button();
        mage.setMinSize(200, 200); 
        putImage("images/magemenu.png", mage);
        
		
        priest = new Button();
        putImage("images/priestmenu.png", priest);
        priest.setMinSize(200, 200); 

		
        paladin = new Button();
        putImage("images/paladinmenu.png", paladin);
        paladin.setMinSize(200, 200); 

		
        warlock = new Button();
        putImage("images/warlockmenu.png", warlock);
        warlock.setMinSize(200, 200); 

		
        hunter = new Button();
        putImage("images/hunterbutton2.png", hunter);
        hunter.setMinSize(200, 200); 

		
        PICK = new Button();
        putImage("images/Pick_Button.png", PICK);
        PICK.setMinSize(150, 150);
        
       
        
        mage.setOnAction(this);
        priest.setOnAction(this);
        paladin.setOnAction(this);
        warlock.setOnAction(this);
        hunter.setOnAction(this);
        PICK.setOnAction(this);
        
        
        putGlowAnimationForMainMenu(mage, Color.GOLD);
        putGlowAnimationForMainMenu(priest, Color.GOLD);
        putGlowAnimationForMainMenu(paladin, Color.GOLD);
        putGlowAnimationForMainMenu(warlock, Color.GOLD);
        putGlowAnimationForMainMenu(hunter, Color.GOLD);
        putGlowAnimationForMainMenu(PICK, Color.GOLD);
      
        FileInputStream input2 = new FileInputStream("images/hearthstonemainmenu.jpg"); 
        Image image2 = new Image(input2); 
        BackgroundImage backgroundimage2 = new BackgroundImage(image2,  
                                         BackgroundRepeat.NO_REPEAT,  
                                         BackgroundRepeat.NO_REPEAT,  
                                         BackgroundPosition.CENTER,  
                                            BackgroundSize.DEFAULT) ; 
        Background background2 = new Background(backgroundimage2); 
        
        BorderPane layout2 = new BorderPane();
        layout2.setBackground(background2);
        HBox midrow = new HBox();
        midrow.getChildren().addAll(mage,priest,paladin,warlock,hunter);
        
        
        HBox botrow = new HBox();
        botrow.getChildren().add(PICK);
        layout2.setBottom(botrow);
        
        StackPane toprow= new StackPane();
        chooseLabel = new Label("Choose Your Character");
        chooseLabel.setFont(Font.font("verdana", FontWeight.BOLD, FontPosture.REGULAR, 50));
        chooseLabel.setTextFill(Color.WHITE);
        layout2.setTop(toprow);
        toprow.getChildren().add(chooseLabel);
        chooseLabel.setAlignment(Pos.TOP_CENTER);
       
        layout2.setCenter(midrow);
       // midrow.setSpacing(0);
        midrow.setAlignment(Pos.CENTER);
       // botrow.setSpacing(0);
        botrow.setAlignment(Pos.BOTTOM_CENTER);
        choose = new Scene(layout2,1280,720);
        
       
        
        //defining Boxes
         heroHand = new HBox(); 
         oppHand = new HBox();     
         heroField = new HBox();
         oppField = new HBox();
        
        //setting boxes positions
        heroHand.setLayoutX(98);
        heroHand.setLayoutY(560);
        oppHand.setLayoutX(92);
        oppHand.setLayoutY(-18);
        heroField.setLayoutX(242);
        heroField.setLayoutY(351);
        oppField.setLayoutX(242);
        oppField.setLayoutY(217);
        
        //setting boxes dimensions
        heroHand.setPrefSize(1085, 160);
        oppHand.setPrefSize(1085, 160);
        heroField.setPrefSize(726, 126);
        oppField.setPrefSize(726, 126);
        heroHand.setAlignment(Pos.CENTER);
        oppHand.setAlignment(Pos.CENTER);
        heroField.setAlignment(Pos.CENTER);
        oppField.setAlignment(Pos.CENTER);
        
   
        layout = new Pane();
        FileInputStream input3 = new FileInputStream("images/Uldaman_Board.emptytest400"); 
        Image image3 = new Image(input3); 
        BackgroundImage backgroundimage3 = new BackgroundImage(image3,  
                                         BackgroundRepeat.NO_REPEAT,  
                                         BackgroundRepeat.NO_REPEAT,  
                                         BackgroundPosition.CENTER,  
                                         new BackgroundSize(layout.getWidth(), layout.getHeight(), true, true, true, true)) ; 

        Background background3 = new Background(backgroundimage3); 
        layout.setBackground(background3);
        
        //setting pane layout
        //layout.getChildren().addAll(heroHand,oppHand,heroField,oppField);
       
        
        game = new Scene(layout,1280,720);
        
    }

    public HBox getHeroHand() {
		return heroHand;
	}

	public HBox getOppHand() {
		return oppHand;
	}

	public HBox getHeroField() {
		return heroField;
	}

	public HBox getOppField() {
		return oppField;
	}

    public Stage getWindow()
    {
    	return window;
    }
 

	public Scene getGame() {
		return game;
	}
	public void setGame() {
		game = new Scene(layout,1280,720);
	}
	

	@Override
	public void handle(ActionEvent event) {
    		
    	if(event.getSource() == hunter) 
		{
    		if(hunter.getEffect() == null)
    			hunter.setEffect(new DropShadow());
    		else
    			hunter.setEffect(null);
    		currenthero = "Hunter";
		}
		
		if(event.getSource() == mage) 
		{
			if(mage.getEffect() == null)
				mage.setEffect(new DropShadow());
    		else
    			mage.setEffect(null);
			  currenthero = "Mage";
		}
		
		if(event.getSource() == priest) 
		{
			if(priest.getEffect() == null)
				priest.setEffect(new DropShadow());
    		else
    			priest.setEffect(null);
			  currenthero = "Priest";
		}
		
		if(event.getSource() == warlock) 
		{
			if(warlock.getEffect() == null)
				warlock.setEffect(new DropShadow());
    		else
    			warlock.setEffect(null);
			  currenthero = "Warlock";
		}
		
		if(event.getSource() == paladin) 
		{
			if(paladin.getEffect() == null)
				paladin.setEffect(new DropShadow());
    		else
    			paladin.setEffect(null);
			  currenthero = "Paladin";
		}
		  
		 if(event.getSource() == PICK && flag == false) 
		 {
			if(currenthero == null)  
			{
				chooseLabel.setText("Pick a hero first!");
			}
			else
			{
				putGlowAnimation(PICK, Color.WHITE);
				pickCount++;
				switch(currenthero)
				{
					case "Hunter": 
					try {
						putFadeAnimation(hunter);
						Player1 = new Hunter();
					} catch (IOException | CloneNotSupportedException e) {
						e.printStackTrace();
					}
						break;	
					
					case "Mage":
					try {
						putFadeAnimation(mage);
						Player1 = new Mage();
					} catch (IOException | CloneNotSupportedException e) {
						e.printStackTrace();
					}
						break;
						
					case "Priest": 
					try {
						putFadeAnimation(priest);
						Player1 = new Priest();
					} catch (IOException | CloneNotSupportedException e) {
						e.printStackTrace();
					}
						break;	
						
					case "Warlock":
					try {
						putFadeAnimation(warlock);
						Player1 = new Warlock();
					} catch (IOException | CloneNotSupportedException e) {
						e.printStackTrace();
					}
						break;	
					
					case "Paladin": 
					try {
						putFadeAnimation(paladin);
						Player1 = new Paladin();
					} catch (IOException | CloneNotSupportedException e) {
						e.printStackTrace();
					}
						break;	
				}
				chooseLabel.setText(" First hero chosen is " + currenthero + "\n");
				currenthero = null;
				flag = true;
			}
		 }
		 
		 
		 else if(event.getSource() == PICK && flag == true) 
		 {
			 if(currenthero == null)  
				{
				 	chooseLabel.setText("Please select a hero first!! \n");
				}
				else
				{
					pickCount++;
					putGlowAnimation(PICK, Color.WHITE);
					switch(currenthero)
					{
						case "Hunter": 
						try {
							putFadeAnimation(hunter);
							Player2 = new Hunter();
						} catch (IOException | CloneNotSupportedException e) {
							e.printStackTrace();
						}
							break;	
							
						case "Mage":
						try {
							putFadeAnimation(mage);
							Player2 = new Mage();
						} catch (IOException | CloneNotSupportedException e) {
							e.printStackTrace();
						}
							break;	
						
						case "Priest": 
						try {
							putFadeAnimation(priest);
							Player2 = new Priest();
						} catch (IOException | CloneNotSupportedException e) {
							e.printStackTrace();
						}
							break;	
						
						case "Warlock":
						try {
							putFadeAnimation(warlock);
							Player2 = new Warlock();
						} catch (IOException | CloneNotSupportedException e) {
							e.printStackTrace();
						}
							break;	
						
						case "Paladin": 
						try {
							putFadeAnimation(paladin);
							Player2 = new Paladin();
						} catch (IOException | CloneNotSupportedException e) {
							e.printStackTrace();
						}
							break;
						
					}
			
	                chooseLabel.setText(" Second hero chosen is " + currenthero + "\n");
					currenthero = null;
					flag = true;
	            	mediaPlayer.stop();
	                window.setScene(game);
	                window.centerOnScreen();
	                
	                try {
						new Controller(Player1,Player2,this);
					} catch (FullHandException | CloneNotSupportedException
							| IOException e) {
						e.printStackTrace();
					} 
				}
		 }
		
	}


}
