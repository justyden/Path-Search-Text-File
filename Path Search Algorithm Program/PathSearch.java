
/* This program will implement a stack to try and traverse
   a grid from a text file that contains a path. It will try
   and find a path to the end and when it does it succeeds.
   If it does not find the end that means that there was
   not path that exists to it. Reads a file as input.
 */
import java.util.Stack;
import java.util.Scanner;
import java.io.File;
import java.io.FileReader;
import java.lang.Exception;
import java.util.LinkedList;

// This class will represent information about a part in the board and will be used by the 
// algorithim to determine what to do next.
class Node {

  private static Stack<Node> pathLocation = new Stack<>(); // Create a stack that will hold nodes for the algorithm
                                                           // to traverse the area.

  private static LinkedList<int[]> checkedLocations = new LinkedList<int[]>();

  private static int totalMoves = 0;
  private int rowNumber, columnNumber;
  private boolean checked;

  public Node() { // Constructor for a default node.
  }

  // Gets the current node in the stack.
  public Node getLocaiton() {
    return pathLocation.peek();
  }

  // Removes a node from the stack.
  public void removeLocation() {
    pathLocation.pop();
  }

  // Adds a new node to the stack.
  public void newLocation(Node nextPath) {
    pathLocation.push(nextPath);
  }

  public Stack<Node> getStack() {
    return pathLocation;
  }

  public int getRowLocation() {
    return rowNumber;
  }

  public int getColumnLocation() {
    return columnNumber;
  }

  public void setRowLocation(int inputRow) {
    rowNumber = inputRow;
  }

  public void setColumnLocation(int inputColumn) {
    columnNumber = inputColumn;
  }

  public void setChecked(boolean input) {
    checked = input;
  }

  public boolean getChecked() {
    return checked;
  }

  public int getTotalMoves() {
    return totalMoves;
  }

  public void setTotalMoves(int input) {
    totalMoves = input;
  }

  public boolean getChecked(int inputColumn, int inputRow) {
    for (int[] i : checkedLocations) {
      if (inputColumn == i[0] && inputRow == i[1]) {
        return true;
      }
    }
    return false;
  }

  public boolean addChecked(int inputColumn, int inputRow) {
    this.setColumnLocation(inputColumn);
    this.setRowLocation(inputRow);

    for (int[] i : checkedLocations) {
      if (this.getColumnLocation() == i[0] && this.getRowLocation() == i[1]) {
        --totalMoves;
        this.checked = true;
        return this.checked;
      }
    }

    int[] temp = { this.getColumnLocation(), this.getRowLocation() };
    checkedLocations.push(temp);
    this.checked = false;
    ++totalMoves;
    return this.checked;
  }

}

class Board {

  private int size; // This will determine the size of the board which is obtained from the
  // BoardFile class.
  private char[][] boardArea; // The current location on the board. This current program supports double
  // array. Could later be upgraded to support more dimensions.
  private int boardColumn;
  private int boardRow;

  Board(BoardFile inputBoard) throws Exception { // This constructor could throw and exception.
    Scanner boardRead = inputBoard.processedBoard(); // Obtained the scanner from the boardFile class.
    String temp; // Strings that will be processed and saved by the scanner.

    while (boardRead.hasNext()) { // Processes infromation from the board as long as there is information to read.
      temp = boardRead.nextLine(); // Sets a temp string equal to what was next in the file.
      int tempSize; // Remembers the size of the temp string.
      size = temp.length(); // Sets the total size of game area.
      boardColumn = size;
      boardArea = new char[size][size];
      int j = 0;

      boolean startFound = false, endFound = false; // Makes sure there is a finish and a start.

      while (temp.length() == size) {
        for (int i = 0; i < temp.length(); ++i) { // This loop will construct the game board and
                                                  // and makes sure everything is correct.
          if (temp.charAt(i) == 'X' || temp.charAt(i) == 'O' || temp.charAt(i) == 'F' || temp.charAt(i) == 'S') {
            boardArea[i][j] = temp.charAt(i);
            // The following if statements check to make sure there is only one instance of
            // finish and start.
            if (temp.charAt(i) == 'F' && !endFound) {
              endFound = true;
            }

            else if (temp.charAt(i) == 'F' && endFound) {
              throw new Exception("The finish was already found. Please check and try again.");
            }

            else if (temp.charAt(i) == 'S' && !startFound) {
              startFound = true;
            }

            else if (temp.charAt(i) == 'S' && startFound) {
              throw new Exception("The start was already found. Please check and try again.");
            }
          }

          else {
            // throws an exception in case one of the characters was not excpected.
            throw new Exception("There was a incorrect character within the board. Please check and try again.");
          }
        }

        if (boardRead.hasNext()) {
          tempSize = temp.length(); // Assign the previous line size.
          temp = boardRead.nextLine(); // Assign the next line.

          if (tempSize != temp.length()) { // Check to make sure both lines are the same size.
            throw new Exception("The size of the board is not correct. Please check and try again.");
          }
        }

        else {
          break;
        }

        ++j; // Increment the row.
        if (j > size - 1) { // Makes sure the area in the map is correct.
          throw new Exception("The area in the map is not the same. Please check and try again.");
        }

        boardRow = j; // Sets the row number.
      }

      if (j != size - 1) {
        throw new Exception("The area in the map is not the same. Please check and try again.");
      }

      if (!endFound) {
        throw new Exception("The end was not found. Please check and try again.");
      }

      if (!startFound) {
        throw new Exception("The start was not found. Please check and try again.");
      }
    }
  }

  public char[][] getBoardArea() { // Returns the array.
    return boardArea;
  }

  public char getBoardLocation(int inputColumn, int inputRow) { // Gets a character a certain location
    return boardArea[inputColumn][inputRow]; // within the array.
  }

  public int getBoardColumn() { // Gets the column total.
    return boardColumn - 1;
  }

  public int getBoardRow() { // Gets the row total.
    return boardRow;
  }
}

// The class used to read a file.
class BoardFile {
  private File file;
  private FileReader createdBoard;
  private Scanner readBoard;

  BoardFile(String fileName) throws Exception { // Creates a file reader object.
    file = new File(fileName);
    if (!file.exists()) { // Makes sure the file is found.
      throw new Exception("The file could not be found.");
    }
    createdBoard = new FileReader(file); // Creates the file reader.
    readBoard = new Scanner(createdBoard); // Creates a new scanner object to read the board.
  }

  public Scanner processedBoard() { // Gets the scanner object from the read board.
    return this.readBoard;
  }
}

// This class is the actual searching and will be used to traverse the map.
public class PathSearch {
  private Node node = new Node(); // Creates the node object to traverse the map area.
  private Board board; // Creates the map.
  private Integer[] saveLocation = new Integer[2]; // Create an array that saves a certain location.

  PathSearch() throws Exception { // Accepts nothing but asks for a string that searchs for a file.
    Scanner input = new Scanner(System.in);
    String userFile;
    System.out.println("Please enter the name of the file.");
    userFile = input.nextLine();
    input.close();

    board = new Board(new BoardFile(userFile)); // This creates the board from a file that was input.

    for (int i = 0; i < board.getBoardColumn(); ++i) { // Searches the board for the starting point.
      for (int j = 0; j < board.getBoardRow(); ++j) {
        if (board.getBoardLocation(i, j) == 'S') {
          node.newLocation(new Node()); // Creates a new element in the stack.
          saveLocation[0] = i; // Saves the column in which the node was found.
          saveLocation[1] = j; // Saves the row in which the node was found.
          System.out.println("Start found at " + (saveLocation[0]) + " " + (saveLocation[1]));
          break; // Exits the loop since the information that was needed was processed.
        }
      }
    }

    if (saveLocation == null) {
      throw new Exception("No starting point was found. Please check and try again.");
    }

    checkPath(saveLocation[0], saveLocation[1]);

  }

  // This method accepts two seperate inputs because it needs to remember if it
  // checked the position it is currently at.
  public Stack<Node> checkPath(int inputColumn, int inputRow) throws Exception {
    node.addChecked(inputColumn, inputRow); // Get the current node and check if has been used already.
    if (node.getChecked() && !node.getStack().empty()) { // Sees if it was checked and if the stack is not empty.
      node.removeLocation(); // Removes the current node from the stack.
      checkPath(node.getLocaiton()); // Uses the previous node to keep checking,
    }

    else {
      Node tempNode = new Node(); // Creates a new temp node.
      tempNode.setColumnLocation(inputColumn);
      tempNode.setRowLocation(inputRow);
      node.newLocation(tempNode); // Adds the temp node into the stack.
      checkPath(node.getLocaiton()); // Starts checking at the new location.
    }

    return null; // Path was not found.

  }

  // This is a overloaded version of checkPath that accepts the current node
  // and begins looking at the current node.
  public Stack<Node> checkPath(Node node) throws Exception {
    int inputColumn = node.getLocaiton().getColumnLocation();
    int inputRow = node.getLocaiton().getRowLocation();

    if (board.getBoardLocation(inputColumn, inputRow) == 'F') {
      System.out.println("The end was found at " + node.getLocaiton().getColumnLocation()
          + " " + node.getLocaiton().getRowLocation());
      System.out.println("Here is the path.");

      while (!node.getStack().empty()) { // While the stack has information.
        System.out.println(node.getLocaiton().getColumnLocation() + " " + node.getLocaiton().getRowLocation());
        node.removeLocation(); // Remove the node to process the next.
      }

      System.out.println("The total moves were " + node.getTotalMoves());

      return node.getStack();
    }

    // All statements to determine where to move next within the board.
    if (inputColumn < board.getBoardColumn() && (board.getBoardLocation(inputColumn + 1, inputRow) == 'O'
        || board.getBoardLocation(inputColumn + 1, inputRow) == 'F')
        && !node.getChecked(inputColumn + 1, inputRow)) {
      checkPath(inputColumn + 1, inputRow);
    }

    if (inputColumn > 0 && (board.getBoardLocation(inputColumn - 1, inputRow) == 'O'
        || board.getBoardLocation(inputColumn - 1, inputRow) == 'F')
        && !node.getChecked(inputColumn - 1, inputRow)) {
      checkPath(inputColumn - 1, inputRow);
    }

    if (inputRow < board.getBoardRow() && (board.getBoardLocation(inputColumn, inputRow + 1) == 'O'
        || board.getBoardLocation(inputColumn, inputRow + 1) == 'F')
        && !node.getChecked(inputColumn, inputRow + 1)) {
      checkPath(inputColumn, inputRow + 1);
    }

    if (inputRow > 0 && (board.getBoardLocation(inputColumn, inputRow - 1) == 'O'
        || board.getBoardLocation(inputColumn, inputRow - 1) == 'F')
        && !node.getChecked(inputColumn, inputRow - 1)) {
      checkPath(inputColumn, inputRow - 1);
    }

    return null; // No path was found at that location.
  }

}
