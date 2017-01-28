/**
 * @author Peixuan Liu <p8liu@ucsd.edu>
 * PID: A92100730
 * Date: 03/02/2016
 * Login: cs8bwaqh
 *
 * File: Gui2048.java
 * This class implements the GUI of the game 2048.
 */

import javafx.application.*;
import javafx.scene.control.*;
import javafx.scene.*;
import javafx.scene.paint.*;
import javafx.scene.shape.*;
import javafx.scene.layout.*;
import javafx.stage.*;
import javafx.event.*;
import javafx.scene.input.*;
import javafx.scene.text.*;
import javafx.geometry.*;
import java.util.*;
import java.io.*;

public class Gui2048 extends Application
{
  private String outputBoard; // The filename for where to save the Board
  private Board board; // The 2048 Game Board
  
  private static final int TILE_WIDTH = 106;
  
  private static final int TEXT_SIZE_LOW = 55; // Low value tiles (2,4,8,etc)
  private static final int TEXT_SIZE_MID = 45; // Mid value tiles 
  //(128, 256, 512)
  private static final int TEXT_SIZE_HIGH = 35; // High value tiles 
  //(1024, 2048, Higher)
  
  // Fill colors for each of the Tile values
  private static final Color COLOR_EMPTY = Color.rgb(238, 228, 218, 0.35);
  private static final Color COLOR_2 = Color.rgb(238, 228, 218);
  private static final Color COLOR_4 = Color.rgb(237, 224, 200);
  private static final Color COLOR_8 = Color.rgb(242, 177, 121);
  private static final Color COLOR_16 = Color.rgb(245, 149, 99);
  private static final Color COLOR_32 = Color.rgb(246, 124, 95);
  private static final Color COLOR_64 = Color.rgb(246, 94, 59);
  private static final Color COLOR_128 = Color.rgb(237, 207, 114);
  private static final Color COLOR_256 = Color.rgb(237, 204, 97);
  private static final Color COLOR_512 = Color.rgb(237, 200, 80);
  private static final Color COLOR_1024 = Color.rgb(237, 197, 63);
  private static final Color COLOR_2048 = Color.rgb(237, 194, 46);
  private static final Color COLOR_OTHER = Color.BLACK;
  private static final Color COLOR_GAME_OVER = Color.rgb(238, 228, 218, 0.73);
  // For tiles >= 8
  private static final Color COLOR_VALUE_LIGHT = Color.rgb(249, 246, 242); 
  // For tiles < 8
  private static final Color COLOR_VALUE_DARK = Color.rgb(119, 110, 101); 
  
  private GridPane gridPane = new GridPane();
  private static final Color TRANSPARENT = Color.rgb(255, 255, 255, 0);
  private int[][] grid;
  private Rectangle[][] tile;
  private Text[][] numberOnTile;
  private BorderPane borderPane = new BorderPane();;
  private static final Text gameName = new Text("2048");
  private Text score = new Text();
  private HBox hBoxNameAndScore = new HBox(100);
  private StackPane stackPane;
  private static final int SPACE_BETWEEN_TILES = 15;  
  private static final Text gameOverText = new Text("Game Over!");
  
  
  @Override
  public void start(Stage primaryStage)
  {
    // Process Arguments and Initialize the Game Board
    processArgs(getParameters().getRaw().toArray(new String[0]));
    
    // put the tiles in the center
    gridPane.setAlignment(Pos.CENTER);
    
    // set the score and game name and their font
    score.setText("Score: " + board.getScore() );
    gameName.setFont
      (Font.font("Britannic Bold", FontWeight.BOLD, 45));
    score.setFont
      (Font.font("Britannic Bold", FontWeight.BOLD, 30));
    
    // create a gameOverText which is transparent when game is not over
    // but has some color when the game is over
    gameOverText.setFont
      (Font.font("Britannic Bold", FontWeight.BOLD, 75));
    gameOverText.setFill(TRANSPARENT);
    
    // hBoxNameAndScore.spacingProperty().bind(100);
    hBoxNameAndScore.getChildren().add(gameName);
    hBoxNameAndScore.getChildren().add(score);
    hBoxNameAndScore.setAlignment(Pos.TOP_CENTER);
    
    // set the backgroud color
    borderPane.setStyle("-fx-background-color: rgb(187, 173, 160)");
    // set the hBox at the top of the borderPane
    borderPane.setTop(hBoxNameAndScore);
    // set the gridPane at the center of the borderPane
    borderPane.setCenter(gridPane);
    BorderPane.setAlignment(gridPane, Pos.CENTER);
    BorderPane.setMargin(hBoxNameAndScore, new Insets(12,12,12,12));
    
    stackPane = new StackPane();
    
    // create a transparent rectangle which 
    // becomes half-transparent when game is over
    Rectangle gameOverRectangle = new Rectangle();
    gameOverRectangle.widthProperty().bind(stackPane.widthProperty());
    gameOverRectangle.heightProperty().bind(stackPane.heightProperty());
    gameOverRectangle.setFill(TRANSPARENT);
    
    // stackPane holds all the visual objects
    stackPane.getChildren().add(borderPane);
    stackPane.getChildren().add(gameOverRectangle);
    stackPane.getChildren().add(gameOverText);
    
    
    // load the grid for the first time
    grid = board.getGrid();
    // initialize gridPane
    tile = iniEmptyTile( grid.length );
    // initialize text
    numberOnTile = iniEmptyNumberOnTile( grid.length );
    // put numbers and color in the tiles
    refresh();
        
    // add tile to gridPane
    for ( int row = 0; row < tile.length; row++ ){
      for ( int column = 0; column < tile[row].length; column++ ) {
        gridPane.add(tile[row][column], column, row);
      }
    }
    
    // add numbers to gridPane
    for ( int row = 0; row < numberOnTile.length; row++ ){
      for ( int column = 0; column < numberOnTile[row].length; column++ ) {
        gridPane.add(numberOnTile[row][column], column, row);
        GridPane.setHalignment(numberOnTile[row][column], HPos.CENTER);
      }
    }
    
    Scene scene = new Scene(stackPane,600,600);
    // Set the spacing between the tiles
    gridPane.hgapProperty().bind
      (scene.heightProperty().divide(8.25).divide(grid.length)); 
    gridPane.vgapProperty().bind
      (scene.heightProperty().divide(8.25).divide(grid.length));
    // set the stage
    primaryStage.setTitle("Gui2048");
    primaryStage.setScene(scene);
    primaryStage.show();
    
    // The following is the EVENT!!
    scene.setOnKeyPressed(e -> {
      // When game is over
      if ( board.isGameOver() ){
        // make game over interface visible when game is over
        gameOverRectangle.setFill(COLOR_GAME_OVER);
        gameOverText.setFill(COLOR_VALUE_DARK);
        // use can save the board even though game is over
        if ( e.getCode() == KeyCode.S ){
          try {
          board.saveBoard(outputBoard);
          System.out.println("Saving Board to " + outputBoard );
        } catch (IOException i) { 
          System.out.println("saveBoard threw an Exception");
        }
        }
      }
      
      // When game is not over
      // user can triger events by pressing key codes 
      else{
        switch ( e.getCode() ) {
          // move down
          case DOWN: 
            if ( board.move(Direction.DOWN) ){
            System.out.println("Moving DOWN");
            board.addRandomTile();
            refresh();
          }
            break;
          // move up
          case UP: 
            if ( board.move(Direction.UP) ){
            System.out.println("Moving UP");  
            board.addRandomTile();
            refresh();
          }
            break;
          // move left
          case LEFT: 
            if ( board.move(Direction.LEFT) ){
            System.out.println("Moving LEFT"); 
            board.addRandomTile();
            refresh(); 
          }
            break;
          // move right
          case RIGHT: 
            if ( board.move(Direction.RIGHT) ){
            System.out.println("Moving RIGHT"); 
            board.addRandomTile(); 
            refresh(); 
          }
            break;
          // save board
          case S:
            try {
            board.saveBoard(outputBoard);
            System.out.println("Saving Board to " + outputBoard );
          } catch (IOException i) { 
            System.out.println("saveBoard threw an Exception");
          }
          break;
          // rotate board
          case R:
            board.rotate(true);
            refresh();
            System.out.println("Rotate clockwise");
            break;
          // press any other keys
          default:
            break;
        }
        if ( board.isGameOver() ){
        // make game over interface visible when game is over
        gameOverRectangle.setFill(COLOR_GAME_OVER);
        gameOverText.setFill(COLOR_VALUE_DARK);
        }
      } 
    });  
  }
  
  /**
   * Initializes the empty tiles (a rectangle array)
   * @param int gridSize
   * @return Rectangle[][]
   */ 
  private Rectangle[][] iniEmptyTile( int gridSize ){
    Rectangle[][] tile = new Rectangle[gridSize][gridSize];
    for ( int row = 0; row < tile.length; row ++ ){
      for (int column = 0; column < tile[row].length; column++ ){
        tile[row][column] = new Rectangle();
        tile[row][column].heightProperty().bind
          (gridPane.heightProperty().divide(1.25).divide(grid.length));
        tile[row][column].widthProperty().bind
          (gridPane.heightProperty().divide(1.25).divide(grid.length));
      }
    }
    return tile;
  }
  
  /**
   * Initializes the empty text on the empty tiles
   * @param int gridSize
   * @return Text[][]
   */ 
  private Text[][] iniEmptyNumberOnTile( int gridSize ){
    Text[][] numberOnTile = new Text[gridSize][gridSize];
    
    for(int row = 0; row < numberOnTile.length; row++ )
      for ( int column = 0; column < numberOnTile[row].length; column++ ){
      numberOnTile[row][column] = new Text();
    }
    return numberOnTile;
  }
  
  /**
   * Refreshes all visual objects, 
   * including numbers on the tiles,
   * the size and color of the numbers, 
   * color of the tiles 
   * and the score.
   */ 
  private void refresh(){
    // get the updated grid
    grid = board.getGrid();
    
    // get and set the updated score
    score.setText("Score: " + board.getScore() );
    
    // add new numbers to text objects
    for(int row = 0; row < numberOnTile.length; row++ )
      for ( int column = 0; column < numberOnTile[row].length; column++ ){
      if ( grid[row][column] == 0 ){
        numberOnTile[row][column].setText( "" );
      }
      if ( grid[row][column] != 0 ){
        numberOnTile[row][column].setText( "" + grid[row][column] );
      }
    }
    
    // set new font for numbers
    for(int row = 0; row < numberOnTile.length; row++ )
      for ( int column = 0; column < numberOnTile[row].length; column++ ){
      
      // small numbers
      if ( grid[row][column] < 128 )
        numberOnTile[row][column].setFont
        (Font.font("Britannic Bold", FontWeight.BOLD, TEXT_SIZE_LOW));
      // middle numbers
      else if ( grid[row][column] >= 128 && grid[row][column] < 1024 )
        numberOnTile[row][column].setFont
        (Font.font("Britannic Bold", FontWeight.BOLD, TEXT_SIZE_MID));
      // big numbers
      else numberOnTile[row][column].setFont
        (Font.font("Britannic Bold", FontWeight.BOLD, TEXT_SIZE_HIGH));
      
      // smaller numbers
      if ( grid[row][column] < 8 )
        numberOnTile[row][column].setFill(COLOR_VALUE_DARK);
      // biger numbers
      else 
        numberOnTile[row][column].setFill(COLOR_VALUE_LIGHT);
    }
    
    // color the tiles
    for ( int row = 0; row < tile.length; row++ )
      for ( int column = 0; column < tile[row].length; column++ ){
      if ( grid[row][column] == 0 )
        tile[row][column].setFill(COLOR_EMPTY);
      else if ( grid[row][column] == 2 )
        tile[row][column].setFill(COLOR_2);
      else if ( grid[row][column] == 4 )
        tile[row][column].setFill(COLOR_4);
      else if ( grid[row][column] == 8 )
        tile[row][column].setFill(COLOR_8);
      else if ( grid[row][column] == 16 )
        tile[row][column].setFill(COLOR_16);
      else if ( grid[row][column] == 32 )
        tile[row][column].setFill(COLOR_32);
      else if ( grid[row][column] == 64 )
        tile[row][column].setFill(COLOR_64);
      else if ( grid[row][column] == 128 )
        tile[row][column].setFill(COLOR_128);
      else if ( grid[row][column] == 256 )
        tile[row][column].setFill(COLOR_256);
      else if ( grid[row][column] == 512 )
        tile[row][column].setFill(COLOR_512);
      else if ( grid[row][column] == 1024 )
        tile[row][column].setFill(COLOR_1024);
      else if ( grid[row][column] == 2048 )
        tile[row][column].setFill(COLOR_2048);
      else 
        tile[row][column].setFill(COLOR_OTHER); 
    }
  }
  
  
  
  /** DO NOT EDIT BELOW */
  
  // The method used to process the command line arguments
  private void processArgs(String[] args)
  {
    String inputBoard = null;   // The filename for where to load the Board
    int boardSize = 0;          // The Size of the Board
    
    // Arguments must come in pairs
    if((args.length % 2) != 0)
    {
      printUsage();
      System.exit(-1);
    }
    
    // Process all the arguments 
    for(int i = 0; i < args.length; i += 2)
    {
      if(args[i].equals("-i"))
      {   // We are processing the argument that specifies
        // the input file to be used to set the board
        inputBoard = args[i + 1];
      }
      else if(args[i].equals("-o"))
      {   // We are processing the argument that specifies
        // the output file to be used to save the board
        outputBoard = args[i + 1];
      }
      else if(args[i].equals("-s"))
      {   // We are processing the argument that specifies
        // the size of the Board
        boardSize = Integer.parseInt(args[i + 1]);
      }
      else
      {   // Incorrect Argument 
        printUsage();
        System.exit(-1);
      }
    }
    
    // Set the default output file if none specified
    if(outputBoard == null)
      outputBoard = "2048.board";
    // Set the default Board size if none specified or less than 2
    if(boardSize < 2)
      boardSize = 4;
    
    // Initialize the Game Board
    try{
      if(inputBoard != null)
        board = new Board(inputBoard, new Random());
      else
        board = new Board(boardSize, new Random());
    }
    catch (Exception e)
    {
      System.out.println(e.getClass().getName() + 
                         " was thrown while creating a " +
                         "Board from file " + inputBoard);
      System.out.println("Either your Board(String, Random) " +
                         "Constructor is broken or the file isn't " +
                         "formated correctly");
      System.exit(-1);
    }
  }
  
  // Print the Usage Message 
  private static void printUsage()
  {
    System.out.println("Gui2048");
    System.out.println("Usage:  Gui2048 [-i|o file ...]");
    System.out.println();
    System.out.println("  Command line arguments come in pairs of the "+ 
                       "form: <command> <argument>");
    System.out.println();
    System.out.println("  -i [file]  -> Specifies a 2048 board that " + 
                       "should be loaded");
    System.out.println();
    System.out.println("  -o [file]  -> Specifies a file that should be " + 
                       "used to save the 2048 board");
    System.out.println("                If none specified then the " + 
                       "default \"2048.board\" file will be used");  
    System.out.println("  -s [size]  -> Specifies the size of the 2048" + 
                       "board if an input file hasn't been"); 
    System.out.println("                specified.  If both -s and -i" + 
                       "are used, then the size of the board"); 
    System.out.println("                will be determined by the input" +
                       " file. The default size is 4.");
  }
}
