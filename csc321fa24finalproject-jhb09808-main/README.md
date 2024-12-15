# Go for the Gold - By Jerome Bustarga (12/10/2024)

<img width="598" alt="Screenshot 2024-12-09 at 9 35 17 PM" src="https://github.com/user-attachments/assets/50feb0d9-1da6-4d24-bc0f-0c74b5dc8208">

## Overview
"Go for the Gold" is an exciting autonomous robot navigation game where players guide an AI-controlled robot through a perilous world in search of treasure. Navigate through obstacles, collect coins, avoid bad guys, and reach the golden prize!

## How to Play
1. Choose your game mode: Console or GUI
2. Select a difficulty level: Easy, Medium, or Hard
3. Enter the desired grid size (5-20)
4. Watch as the AI robot navigates through the maze, collecting coins and avoiding dangers
5. The game ends when the robot reaches the gold, runs out of money, or gets caught by a bad guy

## Game Elements
- Robot: The main character you're guiding (blue circle in GUI)
- Gold: The ultimate goal (yellow square)
- Coins: Collectibles that increase your score and money (green circles)
- Bad Guys: Enemies trying to catch the robot (red squares)
- Obstacles: Impassable terrain (dark gray squares)
- Power-ups: Special items that give the robot temporary abilities (magenta circles)

## Technology Stack
- Language: Java
- GUI: Java Swing
- Data Structures: 
  - Custom HashTable
  - ArrayList
  - LinkedList
  - PriorityQueue
  - Stack
- Algorithms: 
  - A* Pathfinding
  - Depth-First Search (for maze generation)

## Key Features
- Autonomous robot navigation using A* pathfinding
- Dynamic maze generation
- Multiple difficulty levels
- Adjustable grid size
- High score tracking
- Power-up system
- Undo move functionality

## How to Run
1. Compile the Java files: javac goforthegold/.java goforthegold//*.java
2. Run the main class: java goforthegold.GoForTheGold


## Controls (GUI mode)
- **New Game**: Starts a new game
- **Reset Game**: Resets the current game
- **Undo Move**: Undoes the last move (if possible)

## Future Enhancements
- Multiplayer mode
- Additional power-ups and obstacles
- Level editor
- Mobile version

Enjoy playing "Go for the Gold" and may your robot find its way to riches!
