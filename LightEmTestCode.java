import tester.*;

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
