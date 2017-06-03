# GUI2048
This is a 2D video Game that functions as the original 2048 Game, 
implemented with Java GUI. Play around and have fun!

# How to compile
Use jdk 1.8
At cmd or terminal， type (don't type the dollar sign)
$ javac Direction.java
$ javac Board.java
$ javac Gui2048.java

# How to run
*While playing, hit "s" on the keyboard to save the current game board*
You can run the game by typing (without dollar sign)
$ java Gui2048
to start a new 4 by 4 board.

Or you can run by passing in argument. Read the following:
$ java Gui2048 [-i|o file ...]

  Command line arguments come in pairs of the form: <command> <argument>

  -i [file]  -> Specifies a 2048 board that should be loaded

  -o [file]  -> Specifies a file that should be used to save the 2048 board
                If none specified then the default "2048.board" file will be used
                
  -s [size]  -> Specifies the size of the 2048board if an input file hasn't been
                specified.  If both -s and -iare used, then the size of the board
                will be determined by the input file. The default size is 4.
                
For example, 
$ java Gui2048 -i 2048.board -o n2048.board
will load the board that is saved in 2048.board, then when the player hit "s" on the 
keyboard while playing, current game status will be saved in n2048.board. 

Another example,
$ java Gui2048 -o n2048.board -s 5
will start a new 5 by 5 board, hit "s" to save the current board to  n2048.board.

