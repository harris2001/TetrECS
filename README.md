# Second semester UI coursework for the module COMP1206 at University of Southampton
## Game logic
The first step to implementing the game is to implement the basic game logic. This includes:

1. Add the logic to handle placing pieces
    * Can a piece be played?
    * Place a piece onto the grid 
1. Add the logic to keep track of pieces
    * Keep track of the current piece
    * Create new pieces on demand 
1. Add the logic to handle when a piece is played
    * Clear any lines 

To do this:
* In the Grid class:
    * Add a canPlayPiece method
        * Which takes a GamePiece with a given x and y of the grid will return true or false if that piece can be played 
    * Add a playPiece method
        * Which takes a GamePiece with a given x and y of the grid will place that piece in the grid
        * Tip: Remember, you will need to offset the x and y co-ordinates to ensure a piece is played by it's centre! 
* In the Game class:
    * Add a spawnPiece method to return a GamePiece
        * Create a new random GamePiece by calling GamePiece.createPiece 
    * Add a currentPiece GamePiece field to the Game class
        * This will keep track of the current piece 
    * When the game is initialised, spawn a new GamePiece and set it as the currentPiece
    * Add a nextPiece method
        * Replace the current piece with a new piece 
    * Update the blockClicked method to play the current piece from it's centre if possible, then fetch the next piece
    * Add an afterPiece method, and add logic to handle the clearance of lines
        * To be called after playing a piece
        * This should clear any full vertical/horizontal lines that have been made
        * Any horizontal and vertical lines that have just been made should be cleared (including intersecting lines - multiple lines may be cleared at once). 
> Total Hours spend: 2.5 Hours
## Build the User Inteface


Next, we want to build the basics of the user interface for the game:

1. The user interface should keep track of
    * Score
    * Level
    * Lives
    * Multiplier 
1. Show these in the UI
1. Update them appropriately when events happen
    * Implement Scoring
    * Implement Multiplier 
1. Add Background Music 

To do this:

* Add bindable properties for the score, level, lives and multiplier to the Game class, with appropriate accessor methods.
    * These should default to 0 score, level 0, 3 lives and 1 x multiplier respectively. 
* Add UI elements to show the score, level, multiplier and lives in the ChallengeScene by binding to the game properties.
    * Tip: Use the .asString method on an Integer Property to get it as a bindable string! 
* In the Game class, add a score method which takes the number of lines and number of blocks and call it in afterPiece. It should add a score based on the following formula:
    * number of lines * number of grid blocks cleared * 10 * the current multiplier
    * If no lines are cleared, no score is added
    * For example, if a piece was added that cleared 2 intersecting lines, 2 lines would be cleared and 9 blocks would be cleared (because 1 block appears in two lines but is counted only once) 
* Implement the multiplier
    * Every time you clear at least one line with a piece, the multiplier increases by exactly 1
    * The multiplier resets as soon as you play a piece or a piece expires without clearing any lines
    * The multiplier is increased by 1 if the next piece also clears lines. It is increased after the score for the cleared set of lines is applied
    * The multiplier is reset to 1 when a piece is placed that doesn't clear any lines
    * Example: If you clear 4 lines in one go, the multiplier increases once (now at 2x). If then clear 1 line with the next piece, the multiplier increases again (now at 3x). If you then clear 2 lines with the next piece, it increases again (now at 4x). The next piece you play clears no lines (multiplier resets to 1x) 
* Implement the level
    * The level should increase per 1000 points (at the start, you begin at level 0. After 1000 points, you reach level 1. At 3000 points you would be level 3) 
* Create a Multimedia class
    * Add two MediaPlayer fields to handle an audio player and music player
        * These need to be fields, not local variables, to avoid them being garbage collected and sound stopping shortly after it starts 
    * Add a method to play an audio file
    * Add a method to play background music
        * The background music should loop 
* Implement background music on the Menu and in the Game using your new class 


* Important: Use listeners to link your Game (the game model and state) and the GameBoard (the UI) via the ChallengeScene. Do not directly include the GameBoard inside your Game (read the FAQ as to why!) 
> Total Hours spend: 3 Hours
## Enchance the User Interface
Now we have the basics down, we want to enhance the user interface further:

1. Make a better Menu
1. Add an Instructions Screen
1. Make a custom component to show a specific piece
1. Add a listener for handling a next piece being ready
1. Use the component the upcoming piece in the UI
To do this:

* Create a PieceBoard as a new component which extends GameBoard
    * This can be used to display an upcoming piece in a 3x3 grid
    * It should have a method for setting a piece to display
    * Add it to the ChallengeScene
* Update the MenuScene
    * Add pictures, animations, styles and a proper menu
    * Add appropriate events by calling the methods on GameWindow to change scene
* Create a new InstructionsScene
    * This should show the game instructions
    * Add an action from the Menu to the Instructions
* In the InstructionsScene, add a dynamically generated display of all 15 pieces in the game
    * You can create a GridPane of PieceBoards
    * You do not need to worry about handling more than the 15 default pieces
* Add keyboard listeners to allow the user to press escape to exit the challenge or instructions or the game itself
    * Tip: You want to listen for keyboard input on the scene - if you try to add it to a control, how would it know which control should receive the event?
    * Tip: You will need to add a method to shutdown the game in the ChallengeScene to end and clean up all parts of the game, before going back - or it'll keep playing!
* Create your own NextPieceListener interface which a nextPiece method which takes the next GamePiece as a parameter
* Add a NextPieceListener field and a setNextPieceListener method to Game. Ensure the listener is called when the next piece is generated.
* Create a NextPieceListener in the ChallengeScene to listen to new pieces inside game and call an appropriate method.
    * In this method, pass the new piece to the PieceBoard so it displays.
>Total: 5 hours

## Events
Now we need to handle some of the main events and actions in the game:

1. Add the next tile in advance
1. Add piece rotation
1. Add piece swapping
1. Add sound effects
1. Add keyboard support

To do this:

* Add a rotateCurrentPiece method in Game to rotate the next piece, using GamePiece's provided rotate method
* Add a followingPiece to Game. Initialise it at the start of the game.
* Update nextPiece to move the following peice to the current piece, and then replace the following piece.
* Add another, smaller PieceBoard to show the following peice to the ChallengeScene
* Update the NextPieceListener to pass the following piece as well, and use this to update the following piece board.
* Add a swapCurrentPiece method to swap the current and following pieces
* Add a RightClicked listener and corresponding setOnRightClicked method to the GameBoard
* Implement it so that right clicking on the main GameBoard or left clicking on the current piece board rotates the next piece
* Add sounds on events, using your Media class, such as placing pieces, rotating pieces, swapping pieces.
* Add keyboard support to the game, allowing positioning and dropping pieces via the keyboard.
    * You will need to keep track of the current aim (x and y)
    * Drop the piece on enter
    * Move the aim when relevant buttons are pressed

>Total: 5 hours

## Graphics
Let's now enhance the graphics by working with our canvas:

1. Add tiles to the game, not just squares
1. Add hovering
1. Add animations on clearing to show tiles cleared

To do this:

* Update the GameBlock drawing to produce prettier filled tiles and empty tiles
* Update the PieceBoard and GameBlock to show an indicator (e.g. a circle) on the middle square
    * Ensure that any pieces placed on the board are placed relative to this.
* Add events and drawing code to update the GameBoard and GameBlock to highlight the block * currently hovered over
* Create a new fadeOut method on the GameBlock
    * By implementing an AnimationTimer, use this to flash and then fades out to indicate a cleared block
    * Tip: You could do this by painting the block empty, then filling them in with a semi-transparent (gradually getting more transparent) fill each frame
* Create a new fadeOut method on the GameBoard which takes a Set of GameBlockCoordinates and triggers the fadeOut method for each block
* Create a LineClearedListener which takes a Set of GameBlockCoordinates (that hold an x and y in the grid of blocks cleared) and add it to the Game class to trigger when lines are cleared.
* Use the LineClearedListener in the ChallengeScene to receive blocks cleared from the Game and pass them to fade out to the GameBoard
> Total: 8 hours

## Game Loop
Next we need to handle the game progression, which is done via the timer - when no piece is played, we move on to the next piece. We need to:

1. Add a timer to count down how long there is until the piece must be placed
1. When the timer runs out, move on to the next piece and lose a life
1. Show the timer in the game UI

To do this:

* Add a getTimerDelay function in Game
    * Calculate the delay at the maximum of either 2500 milliseconds or 12000 - 500 * the current level
    * So it'll start at 12000, then drop to 11500, then 11000 and keep on going until it reaches 2500 at which point it won't drop any lower
* Implement a Timer or ExecutorService inside the Game class which calls a gameLoop method
    * This should be started when the game starts and repeat at the interval specified by the getTimerDelay function
    * When gameLoop fires (the timer reaches 0): lose a life, the current piece is discarded and the timer restarts. The multiplier is set back to 1.
    * The timer should be reset when a piece is played, to the new timer delay (which may have changed)
* Create a GameLoopListener
    * and a setOnGameLoop method to link it to a listener
    * Use the GameLoopListener to link the timer inside the game with the UI timer
* Create and add an animated timer bar to the ChallengeScene.
    * Use Transitions or an AnimationTimer to implement the timer bar
    * The ChallengeScene should use the GameLoopListener to listen on the GameLoop starting and reset the bar and animation
    * The timer bar should change colour to indicate urgency.
    * Tip: You may want to use a Timeline or create a Transition to animate the bar, which provides a smooth animation using interpolation
    * Tip: The demo uses a Rectangle
* When the number of lives goes below 0, the game should end.
>Total: 3 hours
