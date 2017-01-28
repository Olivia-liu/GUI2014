/**
 * @author Peixuan Liu <p8liu@ucsd.edu>
 * PID: A92100730
 * Date: 02/03/2016
 * Login: cs8bwaqh
 *
 * File: Board.java
 * This class is used represent a 2048 game board and implements some functions.
 */
import java.util.*;
import java.io.*;

public class Board {
  public final int NUM_START_TILES = 2;
  public final int TWO_PROBABILITY = 90;
  public final int GRID_SIZE;
  
  private final Random random;
  private int[][] grid;
  private int score;
  
  /**
   * Constructor which constructs a fresh board with 2 random tiles
   */
  public Board(int boardSize, Random random) {
    // Initialize the instance variables
    this.GRID_SIZE = boardSize;
    this.random = random;
    this.grid = new int[GRID_SIZE][GRID_SIZE];
    this.score = 0;
    
    for ( int i = 0; i < NUM_START_TILES; i++ )
      this.addRandomTile();
  }
  
  /**
   * Constructor whic constructs a board based on an input file
   */
  public Board(String inputBoard, Random random) throws IOException {
    // Initialize the instance variables
    this.random = random; 
    Scanner input = new Scanner( new File(inputBoard) );
    this.GRID_SIZE = input.nextInt();
    this.score = input.nextInt();
    this.grid = new int[GRID_SIZE][GRID_SIZE];
    
    // copy the "grid" in the file to the grid
    for ( int i = 0; i < this.grid.length; i++ )
      for ( int j = 0; j < this.grid[i].length ; j++ )
      this.grid[i][j] = input.nextInt();
    input.close();
  }
  
  
  /**
   * save the board to the file with name outputBoard
   * @param String outputBoard
   */
  public void saveBoard(String outputBoard) throws IOException {
    PrintWriter output = new PrintWriter( new File(outputBoard) );
    // save the size and score
    output.println( this.GRID_SIZE );
    output.println( this.score );
    // save the board
    for (int i = 0; i < this.grid.length; i++ ){
      for (int j = 0; j < this.grid[i].length; j++ )
        output.print( this.grid[i][j] + " ");
      output.println();
    }
    output.close();
  }
  
  
  /**
   * Adds one random tile to the board
   */
  public void addRandomTile() {
    // To count how many empty space are
    int count = 0;
    for ( int i = 0; i < this.grid.length; i++ )
      for (int j = 0; j < this.grid[i].length; j++ ){
      if ( this.grid[i][j] == 0 )
        count++;
    }
    // quit the method if no space is available
    if ( count == 0 )
      return;
    
    // get a random location to put the the number
    int location = this.random.nextInt( count );
    // get a random value to help determine whether to put 2 or 4
    int value = this.random.nextInt( 100 );
    
    // set the currentLocation to -1 
    // so that the first empty space will be location 0
    int currentLocation = -1;
    // go through the grid row by row
    for ( int i = 0; i < this.grid.length; i++ )
      for ( int j = 0; j < this.grid[i].length; j++ ){
      if ( this.grid[i][j] == 0 )
        // implement currentLocation by 1 when meet a empty space
        currentLocation++;
      // put the new tile when meet the location
      if ( currentLocation == location ){
        if ( value < TWO_PROBABILITY ){
          this.grid[i][j] = 2;
          // exist the method immediately after a new tile is added
          return;
        }
        else {
          this.grid[i][j] = 4;
          // exist the method immediately after a new tile is added
          return;
        }
      }
    }              
  }
  
  
  /**
   * Rotates the grid by 90 degrees clockwise if true is passed in
   * Rotates the grid by 90 degrees counter-clockwise if false is passed in
   * @param boolean rotateClockwise
   */
  public void rotate(boolean rotateClockwise) {
    int[][] newGrid = new int[ this.grid.length ][ this.grid[0].length ];
    
    // rotate clockwise
    if ( rotateClockwise ){
      for (int sourceRow = 0, targetColumn = newGrid[0].length-1; 
           sourceRow < this.grid.length && targetColumn >= 0;
           sourceRow++, targetColumn-- )
        for (int sourceColumn = 0, targetRow = 0;
             sourceColumn < this.grid[sourceRow].length && 
             targetRow < newGrid.length;
             sourceColumn++, targetRow++ )
        newGrid[targetRow][targetColumn] = this.grid[sourceRow][sourceColumn];
    }
    
    // rotate counter-clockwise
    else {
      for (int sourceRow = 0, targetColumn = 0; 
           sourceRow < this.grid.length && targetColumn < newGrid[0].length;
           sourceRow++, targetColumn++ )
        for (int sourceColumn = 0, targetRow = newGrid.length-1;
             sourceColumn < this.grid[sourceRow].length && 
             targetRow >= 0 ;
             sourceColumn++, targetRow-- )
        newGrid[targetRow][targetColumn] = this.grid[sourceRow][sourceColumn];
    }
    
    this.grid = newGrid;
  }
  
  
  //Complete this method ONLY if you want to attempt at getting the extra credit
  //Returns true if the file to be read is in the correct format, else return
  //false
  public static boolean isInputFileCorrectFormat(String inputFile) {
    //The try and catch block are used to handle any exceptions
    //Do not worry about the details, just write all your conditions inside the
    //try block
    try {
      //write your code to check for all conditions and return true if it satisfies
      //all conditions else return false
      return true;
    } catch (Exception e) {
      return false;
    }
  }
  
  
  /**
   * Moves the board according to the direction passed in.
   * @param Direction direction, it can be up, down, left, or right
   * @return boolean, true when the move is made, false when no move is made
   */
  public boolean move(Direction direction) {
    if ( direction.equals(Direction.UP) ){
      if ( this.canMove(Direction.UP) ){
        this.moveUp();
        return true;
      }
      return false;
    }
    
    else if ( direction.equals(Direction.DOWN) ){
      if ( this.canMove(Direction.DOWN) ){
        this.moveDown();
        return true;
      }
      return false;
    }
    
    else if ( direction.equals(Direction.LEFT) ){
      if ( this.canMove(Direction.LEFT) ){
        this.moveLeft();
        return true;
      }
      return false;
    }
    
    else if ( direction.equals(Direction.RIGHT) ){
      if ( this.canMove(Direction.RIGHT) ){
        this.moveRight();
        return true;
      }
      return false;
    }
    // if the direction is not valid, return false because can't move
    return false;
  }
  
  
  
  /**
   * This method moves the board up,
   * This means moving all the tiles to the most top and merge same tiles
   */
  public void moveUp(){
    // to move all the tiles to the top, but don't merge
    for ( int column = 0; column < grid[0].length ; column++ )
      for ( int row = 0; row < grid.length; row++ ){
      
      // set a maxRounds, so that when all the tiles in the column are 0,
      // the while loop won't run infinite times
      int maxRounds = 0;
      
      // keep moving up all the tiles in the "column", until a non zero
      // number reaches the tile in the "row"
      while ( grid[row][column] == 0 && maxRounds < grid.length - row){
        for ( int i = row; i < grid.length - 1; i++ ){
          grid[i][column] = grid[i+1][column];
        }
        grid[grid.length-1][column] = 0;
        maxRounds++;
      }
    }
    
    // merge pairs of same numbers
    for ( int column = 0; column < grid[0].length ; column++ )
      for ( int row = 0; row < grid.length - 1; row++ ){
      if ( grid[row+1][column] == grid[row][column] ){
        grid[row][column] *= 2;
        // update the score
        score += grid[row][column];
        // move up all tiles below the newly merged tile
        for ( int i = row + 1; i < grid.length - 1; i++ ){
          grid[i][column] = grid[i+1][column];
        }
        grid[grid.length-1][column] = 0;
      }
    }
  }
  
  
  /**
   * This method moves the board down,
   * This means moving all the tiles to the most bottom and merging same tiles
   */
  public void moveDown(){
    // to move all the tiles to the bottom, but not merge
    for ( int column = 0; column < grid[0].length ; column++ )
      for ( int row = grid.length - 1; row >= 0; row-- ){
      // set a maxRounds, so that when all the tiles in the column are 0,
      // the while loop won't run infinite times
      int maxRounds = 0;
      // keep moving down all the tiles in the "column", until a non zero
      // number reaches the tile in the "row"
      while ( grid[row][column] == 0 && maxRounds <= row ){
        for ( int i = row; i > 0; i-- ){
          grid[i][column] = grid[i-1][column];
        }
        grid[0][column] = 0;
        maxRounds++;
      }
    }
    
    // merge pairs of same numbers
    for ( int column = 0; column < grid[0].length ; column++)
      for ( int row = grid.length - 1; row > 0; row-- ){
      if ( grid[row - 1][column] == grid[row][column] ){
        grid[row][column] *= 2;
        // update the score
        score += grid[row][column];
        // move down all tiles above the newly merged tile
        for ( int i = row - 1; i > 0; i-- ){
          grid[i][column] = grid[i-1][column];
        }
        grid[0][column] = 0;
      }
    }
  }
  
  
  /**
   * This method moves the board right,
   * This means moving all the tiles to the most right and merging same tiles
   */
  public void moveRight(){
    // to move all the tiles to the most right, but not merge
    for ( int row = 0; row < grid.length; row++ )
      for ( int column = grid[row].length - 1; column >= 0; column-- ){
      // set a maxRounds, so that when all the tiles in the row are 0,
      // the while loop won't run infinite times
      int maxRounds = 0;
      // keep moving right all the tiles in the "row", until a non zero
      // number reaches the tile in the "column"
      while ( grid[row][column] == 0 && maxRounds <= column ){
        for ( int j = column; j > 0; j-- ){
          grid[row][j] = grid[row][j-1];
        }
        grid[row][0] = 0;
        maxRounds++;
      }
    }
    
    // merge pairs of same numbers
    for ( int row = 0; row < grid.length; row++ )
      for ( int column = grid[row].length - 1; column > 0; column-- ){
      if ( grid[row][column] == grid[row][column - 1] ){
        grid[row][column] *= 2;
        // update the score
        score += grid[row][column];
        // move right all tiles on the left side of the newly merged tile
        for ( int j = column - 1; j > 0; j-- ){
          grid[row][j] = grid[row][j-1];
        }
        grid[row][0] = 0;
      }
    }
  }
  
  
  /**
   * This method moves the board left,
   * This means moving all the tiles to the most left and merging same tiles
   */
  public void moveLeft(){
    // to move all the tiles to the most left, but not merge
    for ( int row = 0; row < grid.length; row++ )
      for ( int column = 0; column < grid[row].length ; column++ ){
      // set a maxRounds, so that when all the tiles in the row are 0,
      // the while loop won't run infinite times
      int maxRounds = 0;
      // keep moving right all the tiles in the "row", until a non zero
      // number reaches the tile in the "column"
      while ( grid[row][column] == 0 && maxRounds < grid[row].length - column ){
        for ( int j = column; j < grid[row].length - 1; j++ ){
          grid[row][j] = grid[row][j + 1];
        }
        grid[row][grid[row].length-1] = 0;
        maxRounds++;
      }
    }
    
    // merge pairs of same numbers
    for ( int row = 0; row < grid.length; row++ )
      for ( int column = 0; column < grid[row].length - 1; column++ ){
      if ( grid[row][column] == grid[row][column + 1] && grid[row][column] != 0 ){
        grid[row][column] *= 2;
        // update the score
        score += grid[row][column];
        // move left all tiles on the right side of the newly merged tile
        for ( int j = column + 1; j < grid[row].length - 1; j++ ){
          grid[row][j] = grid[row][ j+1 ];
        }
        grid[row][grid[row].length-1] = 0;
      }
    }
  }
  
  
  /**
   * This method determines whether the game is over
   * @return booolean, true when the game is over
   */
  public boolean isGameOver() {
    // the game can continue if the user can make a move
    if ( canMove(Direction.UP) || canMove(Direction.DOWN) || 
        canMove(Direction.LEFT) || canMove(Direction.RIGHT))
      return false;
    
    // the game is over if the user can't move to any direction
    System.out.println( "Game Over!" );
    return true;
  }
  
  
  /**
   * This method determines whether the tiles can be moved to a certain derection
   * @return booolean, true when the tiles can be moved
   */
  public boolean canMove(Direction direction) {
    if ( direction.equals(Direction.UP) ){
      if( this.canMoveUp() )
        return true;
    }
    else if ( direction.equals(Direction.DOWN) ){
      if( this.canMoveDown() )
        return true;
    }
    else if ( direction.equals(Direction.LEFT) ){
      if( this.canMoveLeft() )
        return true;
    }
    else if ( direction.equals(Direction.RIGHT) ){
      if( this.canMoveRight() )
        return true;
    }
    return false;
  }
  
  
  /**
   * This method determines whether the tiles can be moved up
   * @return booolean, true when the tiles can be moved
   */
  public boolean canMoveUp(){
    for ( int row = 0; row < grid.length - 1; row++ )
      for ( int column = 0; column < grid[row].length; column++ ){
      // can move when there's a non zero tile below a zero tile
      if ( grid[row][column] == 0 ){
        for ( int i = row + 1; i < grid.length; i++ ){
          if ( grid[i][column] != 0 )
            return true;
        }
      }
      // can move when two neighbor tiles are same in a column
      else {
        if ( grid[row][column] == grid[row + 1][column] )
          return true;
      }
    }
    return false;
  }
  
  
  /**
   * This method determines whether the tiles can be moved down
   * @return booolean, true when the tiles can be moved
   */
  public boolean canMoveDown(){
    for ( int row = 1; row < grid.length ; row++ )
      for ( int column = 0; column < grid[row].length; column++ ){
      // can move when there's a non zero tile above a zero tile
      if ( grid[row][column] == 0 ){
        for ( int i = row - 1; i >= 0; i-- ){
          if ( grid[i][column] != 0 )
            return true;
        }
      }
      // can move when two neighbor tiles are same in a column
      else {
        if ( grid[row][column] == grid[row - 1][column] )
          return true;
      }
    }
    return false;
  }
  
  
  /**
   * This method determines whether the tiles can be moved left
   * @return booolean, true when the tiles can be moved
   */
  public boolean canMoveLeft(){
    for ( int row = 0; row < grid.length; row++ )
      for ( int column = 0; column < grid[row].length - 1; column++ ){
      // can move when there's a non zero tile on the right side of a zero tile
      if ( grid[row][column] == 0 ){
        for ( int j = column + 1; j < grid[row].length; j++ ){
          if ( grid[row][j] != 0 )
            return true;
        }
      }
      // can move when two neighbor tiles are same in a row
      else {
        if ( grid[row][column] == grid[row][column + 1] )
          return true;
      }
    }
    return false;
  }
  
  
  /**
   * This method determines whether the tiles can be moved right
   * @return booolean, true when the tiles can be moved
   */
  public boolean canMoveRight(){
    for ( int row = 0; row < grid.length ; row++ )
      for ( int column = grid.length - 1; column > 0; column-- ){
      // can move when there's a non zero tile on the left side of a zero tile
      if ( grid[row][column] == 0 ){
        for ( int j = column - 1; j >= 0; j-- ){
          if ( grid[row][j] != 0 )
            return true;
        }
      }
      // can move when two neighbor tiles are same in a row
      else {
        if ( grid[row][column] == grid[row][column - 1] )
          return true;
      }
    }
    return false;
  }
  
  
  /**
   * Returns the reference to the 2048 Grid
   * @return int[][] grid
   */
  public int[][] getGrid() {
    return grid;
  }
  
  
  /**
   * Returns the score
   * @return int score
   */
  public int getScore() {
    return score;
  }
  
  
  /**
   * Determines how to print the board on the console
   * @return String
   */
  @Override
  public String toString() {
    StringBuilder outputString = new StringBuilder();
    outputString.append(String.format("Score: %d\n", score));
    for (int row = 0; row < GRID_SIZE; row++) {
      for (int column = 0; column < GRID_SIZE; column++)
        outputString.append(grid[row][column] == 0 ? "    -" :
                              String.format("%5d", grid[row][column]));
      
      outputString.append("\n");
    }
    return outputString.toString();
  }
}
