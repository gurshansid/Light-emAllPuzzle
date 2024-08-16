import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Random;
import javalib.impworld.*;
import java.awt.Color;
import javalib.worldimages.*;
import tester.*;

class LightEmAll extends World {
  // a list of columns of GamePieces,
  // i.e., represents the board in column-major order
  ArrayList<ArrayList<GamePiece>> board;
  // a list of all nodes
  ArrayList<GamePiece> nodes;
  //a list of all edges
  ArrayList<Edge> edges;
  // a list of edges of the minimum spanning tree
  ArrayList<Edge> mst;
  // the width and height of the board
  int width;
  int height;
  // the current location of the power station,
  // as well as its effective radius
  int powerRow; // row of the power station
  int powerCol; // column of the power station
  Random random; // random object to rotate tiles 
  boolean gameWon; // checks game win condition
  boolean gaveUp; // checks if player decided to give up
  double score; // current score of game

  // constructor for LightEmAll game
  LightEmAll(int width, int height) {
    //initializes board as empty
    board = new ArrayList<ArrayList<GamePiece>>();
    // initializes list of all nodes on the board
    nodes = new ArrayList<GamePiece>();
    // game width and height
    this.width = width;
    this.height = height;
    // random object
    random = new Random();
    // initializes list of edges
    edges = new ArrayList<Edge>();
    // initializes the solutiuon of the board
    mst = new ArrayList<Edge>();
    // initializes power row and column to be 0
    this.powerRow = 0; 
    this.powerCol = 0;

    // generates the board 
    this.generatePieces();

    // generates all the edges on this board
    this.generateEdges();

    // generates a unique puzzle for the board 
    this.generatePuzzle();

    // directs all the edges on this board
    this.directEdges();

    // initializes nodes field
    this.initializeNodes();

    // rotates each game piece randomly 
    this.rotateTiles();
  }

  //constructor for LightEmAll game for testing
  LightEmAll() {
    //initializes board as empty
    board = new ArrayList<ArrayList<GamePiece>>();
    // initializes list of all nodes on the board
    nodes = new ArrayList<GamePiece>();
    // random object
    random = new Random();
    // initializes list of edges
    edges = new ArrayList<Edge>();
    // initializes the solutiuon of the board
    mst = new ArrayList<Edge>();
    // width and height of game preset to a 3 by 3 grid
    this.width = 3;
    this.height = 3;

    // generates the board 
    this.generatePieces();

    // generates all the edges on this board
    this.generateEdges();

    // directs all the edges on this board
    this.directEdges();

    // initializes nodes field
    this.initializeNodes();
  }

  // generates the board 
  void generatePieces() {
    for (int r = 0; r < height; r++) {
      board.add(new ArrayList<GamePiece>());
      for (int c = 0; c < width; c++) {
        GamePiece defaultGamePiece = new GamePiece(r, c, false, false, false, false, false, false);
        board.get(r).add(defaultGamePiece); 

        // places power station
        if (r == 0 && c == 0) {
          board.get(r).get(c).powerStation = true;
          board.get(r).get(c).powered = true;
          this.powerRow = r;
          this.powerCol = c;
        }
        if (board.get(r).get(c).powerStation) {
          this.powerRow = r;
          this.powerCol = c;
        }
      }
    }
  }

  // generates all the edges 
  void generateEdges() {
    for (int r = 0; r < height; r++) {
      for (int c = 0; c < width; c++) {

        if (c < width - 1) {
          edges.add(new Edge(board.get(r).get(c), board.get(r).get(c + 1)));
        }

        if (r < height - 1) {
          edges.add(new Edge(board.get(r).get(c), board.get(r + 1).get(c)));
        }
      }
    }
  }

  // find method for kruskal's
  GamePiece find(HashMap<GamePiece, GamePiece> hash, GamePiece g) {
    if (hash.get(g).equals(g)) {
      return g;
    }
    else {
      return find(hash, hash.get(g));
    }
  }

  // union method for kruskal's
  void union(HashMap<GamePiece, GamePiece> hash, GamePiece g1, GamePiece g2) {
    hash.put(g1, g2);
  }

  // generates a solution to the board
  void generatePuzzle() {
    // initializes representatives 
    HashMap<GamePiece, GamePiece> representatives = new HashMap<GamePiece, GamePiece>();
    ArrayList<Edge> worklist = this.edges;
    worklist.sort(new EdgeCompare());

    // initializing every node's representative to itself
    for (int r = 0; r < height; r++) {
      for (int c = 0; c < width; c++) {
        representatives.put(board.get(r).get(c), board.get(r).get(c));
      }
    }

    // kruskal's algorithm 
    while (worklist.size() > 0) {
      Edge curr = worklist.get(0);

      if (find(representatives, curr.fromNode).equals(find(representatives, curr.toNode))) {
        worklist.remove(0);
      }
      else {
        this.mst.add(curr); 
        union(representatives, find(representatives, curr.fromNode), 
            find(representatives, curr.toNode));
      }
    }
  }

  // modifies each edge to have correct directional fields
  void directEdges() {
    for (Edge e : this.mst) {
      if (e.fromNode.row == e.toNode.row) {
        e.fromNode.right = true;
        e.toNode.left = true;
      }
      else {
        e.fromNode.bottom = true;
        e.toNode.top = true;
      }
    }
  }

  // initializes nodes field
  void initializeNodes() {
    for (int r = 0; r < height; r++) {
      for (int c = 0; c < width; c++) {
        nodes.add(board.get(r).get(c));
      }
    }
  }

  // rotates each game piece randomly 
  void rotateTiles() {
    for (int r = 0; r < height; r++) {
      for (int c = 0; c < width; c++) {
        int randomInteger = this.random.nextInt(5);
        int counter = 0;
        while (counter < randomInteger) {
          board.get(r).get(c).rotate();
          counter++;
        }
      }
    }
  }

  // draws scene
  public WorldScene makeScene() {
    WorldScene scene = new WorldScene(width * 50, height * 50);



    for (int r = 0; r < height; r++) {
      for (int c = 0; c < width; c++) {
        GamePiece current = board.get(r).get(c);
        scene.placeImageXY(current.tileImage(50, 5,
            current.powerStation, new Color(
                Math.max(255 - (15 * (Math.max((Math.abs(this.powerCol - c)), 
                    Math.abs(this.powerRow - r)))), 0), 
                Math.max(255 - (25 * (Math.max((Math.abs(this.powerCol - c)), 
                    Math.abs(this.powerRow - r)))), 0), 0)), 
            (c * 40) + 25, (r * 50) + 25);
      }
    }

    // connects game pieces for power effect
    this.connect();

    // checks if game has been won
    int progress = 0; // checks how many tiles have been powered 
    for (int r = 0; r < height; r++) {
      for (int c = 0; c < width; c++) {
        if ((board.get(r).get(c).powered)) {
          progress++;
        }
      }
    }

    // checks if win condition has been met
    if (progress == height * width && !this.gaveUp) {
      this.gameWon = true;
    }

    // returns game won screen
    if (this.gameWon) {
      TextImage winMessage = new TextImage("YOU WON", 
          30, FontStyle.BOLD, Color.GREEN);
      scene.placeImageXY(winMessage, width * 50 / 2, height * 50 / 2);
    }

    // returns gave up screen
    if (this.gaveUp) {
      TextImage winMessage = new TextImage("YOU GAVE UP :(", 
          30, FontStyle.BOLD, Color.RED);
      scene.placeImageXY(winMessage, width * 50 / 2, height * 50 / 2);
    }

    scene.placeImageXY(new RectangleImage(width * 10 , height * 100, "solid", Color.BLACK), 
        45 * width, height);

    scene.placeImageXY(new AboveImage(new TextImage("Current Score: ", 
        11, FontStyle.BOLD, Color.WHITE),
        new TextImage(Double.toString(this.score) , 
            11, FontStyle.BOLD, Color.WHITE)), 45 * width, height + 15);


    scene.placeImageXY(new TextImage("Press:", 
        18, FontStyle.BOLD, Color.WHITE), 45 * width, height + 85);


    scene.placeImageXY(new TextImage("''r'' to restart!", 
        13, FontStyle.BOLD, Color.WHITE), 45 * width, height + 120);


    scene.placeImageXY(new TextImage("''f'' to give up!", 
        13, FontStyle.BOLD, Color.WHITE), 45 * width, height + 150);



    return scene;
  }

  // on mouse click function for rotation
  public void onMouseClicked(Posn pos, String buttonName) {

    GamePiece coordinates = 
        board.get((int)Math.floor((pos.y) / 50)).get((int)Math.floor((pos.x / 40)));

    if (buttonName.equals("LeftButton")) {
      coordinates.rotate();
      this.score = score + 10;
    }
  }

  // onKeyEvent handler
  public void onKeyEvent(String key) {
    boolean moved = false; 

    if (key.equals("up")) { 

      for (int r = 0; r < height; r++) {
        for (int c = 0; c < width; c++) {

          GamePiece current = board.get(r).get(c);

          if (current.powerStation && current.top && r > 0 && !moved) {
            if (board.get(r - 1).get(c).powered && board.get(r - 1).get(c).bottom) {
              current.powerStation = false; 
              board.get(r - 1).get(c).powerStation = true;
              this.powerRow = powerRow - 1;
              this.powerCol = powerCol;
              moved = true;
            }
          }
          if ( r > 0 ) {
            if (board.get(r - 1).get(c).powerStation) {
              this.powerRow = r - 1;
              this.powerCol = c;
            }
          }
        }
      }
    }

    if (key.equals("down")) { 

      for (int r = 0; r < height; r++) {
        for (int c = 0; c < width; c++) {

          GamePiece current = board.get(r).get(c);

          if (current.powerStation && current.bottom && r < this.height - 1 && !moved) {
            if (board.get(r + 1).get(c).powered && board.get(r + 1).get(c).top) {
              current.powerStation = false; 
              board.get(r + 1).get(c).powerStation = true;
              this.powerRow = powerRow + 1;
              this.powerCol = powerCol;
              moved = true;
            }
          }
          if (board.get(r).get(c).powerStation) {
            this.powerRow = r;
            this.powerCol = c;
          }
        }
      }
    }

    if (key.equals("left")) { 

      for (int r = 0; r < height; r++) {
        for (int c = 0; c < width; c++) {

          GamePiece current = board.get(r).get(c);

          if (current.powerStation && current.left && c > 0 && !moved) {
            if (board.get(r).get(c - 1).powered && board.get(r).get(c - 1).right) {
              current.powerStation = false; 
              board.get(r).get(c - 1).powerStation = true;
              this.powerRow = powerRow;
              this.powerCol = powerCol - 1;
              moved = true;
            }
          }

          if ( c > 1 ) {
            if (board.get(r).get(c - 1).powerStation) {
              this.powerRow = r;
              this.powerCol = c - 1;
            }
          }
        }
      }
    }

    if (key.equals("right")) { 

      for (int r = 0; r < height; r++) {
        for (int c = 0; c < width; c++) {

          GamePiece current = board.get(r).get(c);

          if (current.powerStation && current.right && c < this.width - 1 && !moved) {
            if (board.get(r).get(c + 1).powered && board.get(r).get(c + 1).left) {
              current.powerStation = false; 
              board.get(r).get(c + 1).powerStation = true;
              this.powerRow = powerRow;
              this.powerCol = powerCol + 1;
              moved = true;
            }
          }
          if (board.get(r).get(c).powerStation) {
            this.powerRow = r;
            this.powerCol = c;
          }
        }
      }
    }

    if (key.equals("f")) {
      this.gaveUp = true;

      // generates a unique puzzle for the board 
      this.generatePuzzle();

      // directs all the edges on this board
      this.directEdges();

      // initializes nodes field
      this.initializeNodes();
      for (int r = 0; r < height; r++) {
        for (int c = 0; c < width; c++) {
          if (r == 0 && c == 0) {
            board.get(r).get(c).powerStation = true;
          }
          else {
            board.get(r).get(c).powerStation = false;
          }
        }
      }
    }

    if (key.equals("r")) {
      this.score = 0;
      this.gameWon = false;
      this.gaveUp = false;

      //initializes board as empty
      board = new ArrayList<ArrayList<GamePiece>>();
      // initializes list of all nodes on the board
      nodes = new ArrayList<GamePiece>();
      // game width and height
      this.width = width;
      this.height = height;
      // random object
      random = new Random();
      // initializes list of edges
      edges = new ArrayList<Edge>();
      // initializes the solutiuon of the board
      mst = new ArrayList<Edge>();
      // initializes power row and column to be 0
      this.powerRow = 0; 
      this.powerCol = 0;

      // generates the board 
      this.generatePieces();

      // generates all the edges on this board
      this.generateEdges();

      // generates a unique puzzle for the board 
      this.generatePuzzle();

      // directs all the edges on this board
      this.directEdges();

      // initializes nodes field
      this.initializeNodes();

      // rotates each game piece randomly 
      this.rotateTiles();


      for (int r = 0; r < height; r++) {
        for (int c = 0; c < width; c++) {
          board.get(r).get(c).powered = false;
          if (r == 0 && c == 0) {
            board.get(r).get(c).powerStation = true;
          }
          else {
            board.get(r).get(c).powerStation = false;
          }
        }
      }
    } 
  }

  // connects tiles for power effect 
  public void connect() {
    // list of games pieces that need to be processed
    ArrayList<GamePiece> list1 = new ArrayList<GamePiece>();

    // initializes game pieces as not powered 
    for (GamePiece gp : this.nodes) {
      gp.powered = false;
    }

    // adds power station to head of list
    list1.add(board.get(this.powerRow).get(this.powerCol));

    // while loop that checks surrounding cells for power up
    while (!list1.isEmpty()) {

      if (list1.get(0).top && list1.get(0).row > 0) {
        if (board.get((list1.get(0).row) - 1).get(list1.get(0).col).bottom 
            && !board.get((list1.get(0).row) - 1).get(list1.get(0).col).powered) {
          board.get((list1.get(0).row) - 1).get(list1.get(0).col).powered = true;
          list1.add(board.get((list1.get(0).row) - 1).get(list1.get(0).col));
        }
      }

      if (list1.get(0).bottom && list1.get(0).row < this.height - 1) {
        if (board.get((list1.get(0).row) + 1).get(list1.get(0).col).top 
            && !board.get((list1.get(0).row) + 1).get(list1.get(0).col).powered) {
          board.get((list1.get(0).row) + 1).get(list1.get(0).col).powered = true;
          list1.add(board.get((list1.get(0).row) + 1).get(list1.get(0).col));
        }
      }

      if (list1.get(0).left && list1.get(0).col > 0) {
        if (board.get(list1.get(0).row).get((list1.get(0).col) - 1).right 
            && !board.get(list1.get(0).row).get((list1.get(0).col) - 1).powered) {
          board.get(list1.get(0).row).get((list1.get(0).col) - 1).powered = true;
          list1.add(board.get(list1.get(0).row).get((list1.get(0).col) - 1));
        }
      }


      if (list1.get(0).right && list1.get(0).col < this.width - 1) {
        if (board.get(list1.get(0).row).get((list1.get(0).col) + 1).left 
            && !board.get(list1.get(0).row).get((list1.get(0).col) + 1).powered) {
          board.get(list1.get(0).row).get((list1.get(0).col) + 1).powered = true;
          list1.add(board.get(list1.get(0).row).get((list1.get(0).col) + 1));
        }
      }
      // removes already processed game piece 
      list1.remove(0);
    }
  }
}

//class to compare the weights of two edges
class EdgeCompare implements Comparator<Edge> {
  public int compare(Edge first, Edge second) {
    return first.weight - second.weight;
  }
}

// class for a game piece 
class GamePiece {
  // in logical coordinates, with the origin
  // at the top-left corner of the screen
  int row;
  int col;
  // whether this GamePiece is connected to the
  // adjacent left, right, top, or bottom pieces
  boolean left;
  boolean right;
  boolean top;
  boolean bottom;
  // whether the power station is on this piece
  boolean powerStation;
  boolean powered;

  // constructor for GamePiece 
  GamePiece(int row, int col, boolean left, boolean right, 
      boolean top, boolean bottom, boolean powerStation, boolean powered) {
    this.row = row;
    this.col = col;
    this.left = left;
    this.right = right;
    this.top = top;
    this.bottom = bottom;
    this.powerStation = powerStation;
    this.powered = powered;
  }

  // constructor for GamePiece 
  GamePiece(boolean left, boolean right, boolean top, boolean bottom) {
    this.left = left;
    this.right = right;
    this.top = top;
    this.bottom = bottom;   
  }

  // rotates this game piece
  public void rotate() {
    boolean left = this.left;
    boolean right = this.right;
    boolean top = this.top;
    boolean bottom = this.bottom;

    this.top = left;
    this.right = top;
    this.bottom = right;
    this.left = bottom;
  }

  // Generate an image of this, the given GamePiece.
  // - size: the size of the tile, in pixels
  // - wireWidth: the width of wires, in pixels
  // - wireColor: the Color to use for rendering wires on this
  // - hasPowerStation: if true, draws a fancy star on this tile to represent the power station
  WorldImage tileImage(int size, int wireWidth, boolean hasPowerStation, Color colorMaker) {

    Color wireColor = Color.gray;

    // color of the wire in this game piece
    if (this.powered || this.powerStation) {
      wireColor = colorMaker; 
    }

    // Start tile image off as a blue square with a wire-width square in the middle,
    // to make image "cleaner" (will look strange if tile has no wire, but that can't be)
    WorldImage image = new OverlayImage(new RectangleImage(size - 1, size - 1, 
        OutlineMode.OUTLINE, Color.BLACK),
        new OverlayImage(new RectangleImage(wireWidth, wireWidth, OutlineMode.SOLID, wireColor),
            new RectangleImage(size, size, OutlineMode.SOLID, Color.DARK_GRAY)));
    WorldImage vWire = new RectangleImage(wireWidth, (size + 1) / 2, OutlineMode.SOLID, wireColor);
    WorldImage hWire = new RectangleImage((size + 1) / 2, wireWidth, OutlineMode.SOLID, wireColor);

    if (this.top) { 
      image = new OverlayOffsetAlign(AlignModeX.CENTER, AlignModeY.TOP, vWire, 0, 0, image); 
    }
    if (this.right) { 
      image = new OverlayOffsetAlign(AlignModeX.RIGHT, AlignModeY.MIDDLE, hWire, 0, 0, image); 
    }
    if (this.bottom) { 
      image = new OverlayOffsetAlign(AlignModeX.CENTER, AlignModeY.BOTTOM, vWire, 0, 0, image); 
    }
    if (this.left) { 
      image = new OverlayOffsetAlign(AlignModeX.LEFT, AlignModeY.MIDDLE, hWire, 0, 0, image); 
    }
    if (hasPowerStation) {
      image = new OverlayImage(
          new OverlayImage(
              new StarImage(size / 3, 7, OutlineMode.OUTLINE, new Color(255, 128, 0)),
              new StarImage(size / 3, 7, OutlineMode.SOLID, new Color(0, 255, 255))),
          image);
    }
    return image;
  }
}

// class to represent an edge between two game pieces 
class Edge {
  GamePiece fromNode;
  GamePiece toNode;
  int weight;
  Random random; // random object to assign random weight

  // constructor for edge 
  Edge(GamePiece fromNode, GamePiece toNode) {
    this.fromNode = fromNode;
    this.toNode = toNode;
    random = new Random();
    this.weight = random.nextInt(100);
  }

  // constructor for edge for testing
  Edge(GamePiece fromNode, GamePiece toNode, int weight) {
    this.fromNode = fromNode;
    this.toNode = toNode;
    this.weight = weight;
  }
}

//examples class for testing
class ExamplesLightEmAll {

  GamePiece gamep1;
  GamePiece gamep2;
  GamePiece gamep3;
  GamePiece gamep4;
  GamePiece gamep5;
  GamePiece gamep6;
  GamePiece gamep7;
  GamePiece gamep8;
  GamePiece gamep9;

  LightEmAll g1;
  LightEmAll g2;

  Random rand;


  // initial data
  void initData() {

    this.gamep1 = new GamePiece(true, true, false, false);
    this.gamep2 = new GamePiece(false, true, false, false);
    this.gamep3 = new GamePiece(true, true, true, true);
    this.gamep4 = new GamePiece(false, true, true, false);
    this.gamep5 = new GamePiece(false, true, false, true);
    this.gamep6 = new GamePiece(false, false, false, true);
    this.gamep7 = new GamePiece(1, 1, true, true, true, false, true, true);
    this.gamep8 = new GamePiece(1, 1, true, true, true, false, false, false);
    this.gamep9 = new GamePiece(1, 1, true, true, true, false, false, true);

    this.g1 = new LightEmAll(8, 9);
    this.g2 = new LightEmAll();

    rand = new Random();

  }



  // tests for rotate method
  void testRotate(Tester t) {
    this.initData();

    this.gamep1.rotate();
    this.gamep5.rotate();

    t.checkExpect(this.gamep1, new GamePiece(false, false, true, true));
    t.checkExpect(this.gamep5, new GamePiece(true, false, false, true));
  }

  //tests for the onMouseClicked method
  void testOnMouseClicked(Tester t) {

    // initialize data
    this.initData();

    //saving the initial GamePiece booleans
    GamePiece savedPiece = new GamePiece(1, 1, this.g1.board.get(1).get(1).left,
        this.g1.board.get(1).get(1).right, this.g1.board.get(1).get(1).top, 
        this.g1.board.get(1).get(1).bottom, false, false);

    // rotating the original GamePiece
    savedPiece.rotate();

    // test for left click
    this.g1.onMouseClicked(new Posn(50, 50), "LeftButton"); 

    // check if post clicked GamePiece is the same as the original GamePiece rotated
    t.checkExpect(this.g1.board.get(1).get(1), savedPiece);
  }

  //tests for the onKeyEvent method
  void testOnKeyEvent(Tester t) {

    // initialize data
    this.initData();


    // test for down button
    this.g1.board.get(1).get(0).powered = true;
    this.g1.board.get(1).get(0).top = true;
    this.g1.board.get(0).get(0).bottom = true;


    this.g1.onKeyEvent("down"); 

    // check if changes occurred
    t.checkExpect(this.g1.board.get(1).get(0).powerStation, true);

    // test for up button
    this.g1.board.get(0).get(0).powered = true;
    this.g1.board.get(0).get(0).bottom = true;

    this.g1.onKeyEvent("up"); 

    // check if changes occurred
    t.checkExpect(this.g1.board.get(0).get(0).powerStation, true);


    // test for right button
    this.g1.board.get(0).get(0).right = true;
    this.g1.board.get(0).get(1).powered = true;
    this.g1.board.get(0).get(1).left = true;

    this.g1.onKeyEvent("right"); 

    // check if changes occurred
    t.checkExpect(this.g1.board.get(0).get(1).powerStation, true);

    // test for left button
    this.g1.board.get(0).get(0).powered = true;
    this.g1.board.get(0).get(0).right = true;

    this.g1.onKeyEvent("left"); 

    // check if changes occurred
    t.checkExpect(this.g1.board.get(0).get(0).powerStation, true);

    //first check the before
    t.checkExpect(this.g1.gaveUp, false);

    // test give up
    this.g1.onKeyEvent("f");

    t.checkExpect(this.g1.gaveUp, true);

    // test restart
    this.g1.onKeyEvent("r");

    // check if correct changes occurred
    t.checkExpect(this.g1.gaveUp, false);
    t.checkExpect(this.g1.board.get(0).get(0).powerStation, true);
  }

  // tests for generate pieces method
  void testGeneratePieces(Tester t) {

    // creating game
    LightEmAll game = new LightEmAll();

    // check to see if power station is placed correctly
    t.checkExpect(game.board.get(0).get(0).powerStation, true);
    t.checkExpect(game.board.get(1).get(1).powered, false);
    t.checkExpect(game.board.get(1).get(1).left, false);
    t.checkExpect(game.board.get(1).get(1).right, false);
    t.checkExpect(game.board.get(1).get(1).top, false);
    t.checkExpect(game.board.get(1).get(1).bottom, false);

    // check to see if top row is placed correctly 
    t.checkExpect(game.board.get(0).get(0).powerStation, true);
    t.checkExpect(game.board.get(0).get(0).powered, true);
    t.checkExpect(game.board.get(0).get(0).top, false);
    t.checkExpect(game.board.get(0).get(0).bottom, false);
    t.checkExpect(game.board.get(0).get(0).left, false);
    t.checkExpect(game.board.get(0).get(0).right, false);

    // check to see if bottom row is placed correctly 
    t.checkExpect(game.board.get(2).get(0).powerStation, false);
    t.checkExpect(game.board.get(2).get(0).powered, false);
    t.checkExpect(game.board.get(2).get(0).top, false);
    t.checkExpect(game.board.get(2).get(0).bottom, false);
    t.checkExpect(game.board.get(2).get(0).left, false);
    t.checkExpect(game.board.get(2).get(0).right, false);

    // check for middle left tile 
    t.checkExpect(game.board.get(1).get(0).powerStation, false);
    t.checkExpect(game.board.get(1).get(0).powered, false);
    t.checkExpect(game.board.get(1).get(0).top, false);
    t.checkExpect(game.board.get(1).get(0).right, false);
    t.checkExpect(game.board.get(1).get(0).bottom, false);
    t.checkExpect(game.board.get(1).get(0).left, false);

    // check for middle right tile 
    t.checkExpect(game.board.get(1).get(2).powerStation, false);
    t.checkExpect(game.board.get(1).get(2).powered, false);
    t.checkExpect(game.board.get(1).get(2).top, false);
    t.checkExpect(game.board.get(1).get(2).right, false);
    t.checkExpect(game.board.get(1).get(2).bottom, false);
    t.checkExpect(game.board.get(1).get(2).left, false);
  }

  // tests for initializeNodes method
  void testInitializeNodes(Tester t) {
    //initialize data
    this.initData();

    t.checkExpect(this.g2.nodes, Arrays.asList(this.g2.board.get(0).get(0),
        this.g2.board.get(0).get(1), this.g2.board.get(0).get(2), this.g2.board.get(1).get(0),
        this.g2.board.get(1).get(1), this.g2.board.get(1).get(2), this.g2.board.get(2).get(0),
        this.g2.board.get(2).get(1), this.g2.board.get(2).get(2)));
  }

  // tests for initializeNodes method
  void testRotateTiles(Tester t) {

    //initialize data
    this.initData();

    // initializes the nodes
    this.g2.initializeNodes();

    // rotates tiles with a seeded random
    for (int r = 0; r < g2.height; r++) {
      for (int c = 0; c < g2.width; c++) {
        int randomInteger = rand.nextInt(5);
        int counter = 0;
        while (counter < randomInteger) {
          g2.board.get(r).get(c).rotate();
          counter++;
        }
      }
    }

    // test to see of rotation matches and stays consistent
    t.checkExpect(this.g2.board.get(0).get(0), this.g2.nodes.get(0));
  }

  //tests for the connect method
  void testConnect(Tester t) {

    // initialize data
    this.initData();

    // modification for above tile
    this.g2.board.get(0).get(1).left = true;
    this.g2.board.get(0).get(1).bottom = true;
    this.g2.board.get(0).get(1).powered = false;

    // modification for the below tile 
    this.g2.board.get(1).get(0).bottom = true;
    this.g2.board.get(1).get(0).top = true;
    this.g2.board.get(1).get(0).powered = false;

    // connecting the pieces
    this.g2.connect();

    // check to see if the tiles around the power station are now powered or not
    t.checkExpect(this.g2.board.get(0).get(1).powered, false);
    t.checkExpect(this.g2.board.get(0).get(1).powered, false);
    t.checkExpect(this.g2.board.get(1).get(0).powered, false);
    t.checkExpect(this.g2.board.get(1).get(0).powered, false);
  }

  // tests for makeScene method 
  void testMakeScene(Tester t) {

    this.initData();

    // creating scene for test
    WorldScene scene = new WorldScene(150, 150);

    // connects game pieces for power effect
    g2.connect();

    // modify scene 
    for (int r = 0; r < g2.height; r++) {
      for (int c = 0; c < g2.width; c++) {
        GamePiece current = g2.board.get(r).get(c);
        scene.placeImageXY(current.tileImage(50, 5,  
            current.powerStation, new Color(
                Math.max(255 - (15 * (Math.max((Math.abs(g2.powerCol - c)), 
                    Math.abs(g2.powerRow - r)))), 0), 
                Math.max(255 - (25 * (Math.max((Math.abs(g2.powerCol - c)), 
                    Math.abs(g2.powerRow - r)))), 0), 0)), (c * 40) + 25, (r * 50) + 25);
      }
    }

    int progress = 0;  
    for (int r = 0; r < g2.height; r++) {
      for (int c = 0; c < g2.width; c++) {
        if ((g2.board.get(r).get(c).powered)) {
          progress++;
        }
      }
    }

    if (progress == g2.height * g2.width) {
      g2.gameWon = true;
    }

    if (g2.gameWon) {
      TextImage winMessage = new TextImage("YOU WON", 
          40, FontStyle.BOLD, Color.GREEN);
      scene.placeImageXY(winMessage, g2.width * 50 / 2, g2.height * 50 / 2);
    }
    // returns gave up screen
    if (g2.gaveUp) {
      TextImage winMessage = new TextImage("YOU GAVE UP :(", 
          30, FontStyle.BOLD, Color.RED);
      scene.placeImageXY(winMessage, g2.width * 50 / 2, g2.height * 50 / 2);
    }

    scene.placeImageXY(new RectangleImage(g2.width * 10 , g2.height * 100, "solid", Color.BLACK), 
        45 * g2.width, g2.height);

    scene.placeImageXY(new AboveImage(new TextImage("Current Score: ", 
        11, FontStyle.BOLD, Color.WHITE),
        new TextImage(Double.toString(g2.score) , 
            11, FontStyle.BOLD, Color.WHITE)), 45 * g2.width, g2.height + 15);


    scene.placeImageXY(new TextImage("Press:", 
        18, FontStyle.BOLD, Color.WHITE), 45 * g2.width, g2.height + 85);


    scene.placeImageXY(new TextImage("r to restart", 
        13, FontStyle.BOLD, Color.WHITE), 45 * g2.width, g2.height + 120);


    scene.placeImageXY(new TextImage("f to give up :(", 
        11, FontStyle.BOLD, Color.WHITE), 45 * g2.width, g2.height + 150);

    // check to see if correct scene is displayed
    t.checkExpect(this.g2.makeScene(), scene);
  }

  // test for the generateEdges method
  void testGenerateEdges(Tester t) {
    // initialize data 
    this.initData();

    t.checkExpect(this.g2.board.size(), 3);
    t.checkExpect(this.g2.board.get(0).size(), 3);

    // check to see if all edges were created
    t.checkExpect(this.g2.edges.size(), 12);
  }

  // test for the directEdges method
  void testDirectEdges(Tester t) {
    // initialize data 
    this.initData();

    // checks to if edges are initially directed correctly 
    t.checkExpect(this.g2.board.get(0).get(0).right, false);
    t.checkExpect(this.g2.board.get(0).get(0).bottom, false);
    t.checkExpect(this.g2.board.get(0).get(1).left, false);
    t.checkExpect(this.g2.board.get(0).get(1).right, false);
    t.checkExpect(this.g2.board.get(1).get(0).top, false);
    t.checkExpect(this.g2.board.get(1).get(0).bottom, false);
  }

  // test for EdgeCompare comparator
  void testEdgeCompare(Tester t) {
    // initialize data 
    this.initData();

    // construct examples of edges using game pieces 
    Edge e1 = new Edge(gamep1, gamep2, 10);
    Edge e2 = new Edge(gamep2, gamep3, 8);
    EdgeCompare ec = new EdgeCompare();

    // check to see if the comparator works
    t.checkExpect(ec.compare(e1, e2), 2);
    t.checkExpect(ec.compare(e2, e1), -2);
  }

  // test for generatePuzzle method
  void testGeneratePuzzle(Tester t) {
    //initialize data 
    this.initData();

    // check to see size of mst before kruskal's
    t.checkExpect(this.g2.mst.size(), 0);

    // kruskal's for a 3 by 3 game
    g2.generatePuzzle();

    //check to see if size of mst is correct after kruskal's
    t.checkExpect(this.g2.mst.size(), 8);
  }

  // testt for union method 
  void testUnion(Tester t) {
    // initialize data
    this.initData();

    // hash map for testing
    HashMap<GamePiece, GamePiece> representatives = new HashMap<GamePiece, GamePiece>();

    // initializing every node's representative to itself
    for (int r = 0; r < g2.height; r++) {
      for (int c = 0; c < g2.width; c++) {
        representatives.put(g2.board.get(r).get(c), g2.board.get(r).get(c));
      }
    }

    // run the union method
    g2.union(representatives, gamep2, gamep1);

    // check to see if union works properly
    t.checkExpect(representatives.get(gamep2), gamep1);
  }

  // test for find method
  void testFind(Tester t) {
    // initialize data
    this.initData();

    // hash map for testing
    HashMap<GamePiece, GamePiece> representatives = new HashMap<GamePiece, GamePiece>();

    // initializing every node's representative to itself
    for (int r = 0; r < g2.height; r++) {
      for (int c = 0; c < g2.width; c++) {
        representatives.put(g2.board.get(r).get(c), g2.board.get(r).get(c));
      }
    }

    // check to see if find works properly
    t.checkExpect(g2.find(representatives, g2.board.get(1).get(1)), g2.board.get(1).get(1));
  }

  // renders the world
  void testBigBang(Tester t) {
    LightEmAll world = new LightEmAll(9, 8);
    int worldWidth = world.width * 50;
    int worldHeight = world.height * 50;
    world.bigBang(worldWidth, worldHeight);
  }
}

