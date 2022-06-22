
package game;

import static javafx.scene.layout.GridPane.setConstraints;

import csse2002.block.world.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.scene.Scene;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.scene.control.*;

/**
 * MainApplication class extends from javafx.application.Application. this class
 * is used to create the world of blocks
 */


public class MainApplication extends Application {

    /* the current worldmap loaded*/
    private WorldMap currentWorldMap;
    /*current builder in the world*/
    private Builder currentBuilder;
    /*label for displaying builderInventory*/
    private Label builderInventory;
    /*painter used to render the tiles displayed in mapDisplay*/
    private Painter painter;
    /*current position of builder*/
    private Position currentPosition;
    /*list to store builder's inventory*/
    ArrayList<String> inventoryList = new ArrayList<String>();
    /*gridpane used to dis play the map of tiles*/
    private GridPane mapDisplay = new GridPane();
    /*main gridpane of the whole application*/
    private GridPane root;
    /*fileMenu*/
    private Menu fileMenu;
    /* map that is used to store direction and
    its relative X coordinate difference to current tile*/
    private Map<String, Integer> directionMapX = new HashMap<>();
    /* map that is used to store direction and
    its relative Y ccordinate difference to current tile*/
    private Map<String, Integer> directionMapY = new HashMap<>();


    public static void main(String[] args) {
        launch(args);
    }

    /**
     * opens up a file chooser window that allows the user to chose the map
     * from.Alert boxes with guidence will be shown for both successful attempts
     * and fail to load
     *
     * @param primaryStage -The primary stage of the JavaFX application
     */

    public void LoadMap(Stage primaryStage) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Resource File");

        File file = fileChooser.showOpenDialog(primaryStage);
        if (file != null) {
            String filename = file.getAbsolutePath();
            CreateMap(filename);

        }
    }

    /**
     * opens up a filechooser window that allows the user to chose the file to
     * save to(by selecting the file).Alert boxs will be shown if the save is
     * successful or if the chosen file cannot be saved.
     *
     * @param primaryStage -The primary stage of the JavaFX application
     */
    public void SaveMap(Stage primaryStage) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Resource File");

        File file = fileChooser.showOpenDialog(primaryStage);
        if (file != null) {
            String filename = file.getAbsolutePath();
            try {
                currentWorldMap.saveMap(filename);

                alertBox("MAP SAVED", "Map saved into target", 0);
            } catch (IOException IOE) {
                alertBox("MAP CANNOT BE SAVED ",
                        "File cannot be opened or save to", 2);
            }

        }
    }

    /**
     * The method createMap intakes the chosen map's file name and tries to load
     * that particular map into the GUI application.Alert boxes will be shown
     * if: the map is legal and loaded successfully, the map format is incorrect
     * and the map is inconsistent and the file is not found.
     *
     * @param filename - the filename of the chosen map
     */

    private void CreateMap(String filename) {
        try {
            currentWorldMap = new WorldMap(filename);
            currentBuilder = currentWorldMap.getBuilder();
            currentPosition = currentWorldMap.getStartPosition();
            alertBox("WORLDMAP LOADED",
                    "WorldMap Successfully Loaded ", 2);

        } catch (WorldMapFormatException WFE) {
            alertBox("WORLDMAP LOADING PROBLEM",
                    "CANNOT LOAD WORLDMAP DUE TO FORMAT PROBLEM",
                    1);

        } catch (WorldMapInconsistentException WIE) {
            alertBox("WORLDMAP LOADING PROBLEM",
                    "CANNOT LOAD WORLDMAP DUE TO INCONSISTENCY PROBLEM",
                    1);

        } catch (FileNotFoundException FNE) {
            alertBox("WORLDMAP LOADING PROBLEM",
                    "FILE NOT FOUND", 2);
        }

        painter = new Painter(currentWorldMap, mapDisplay);
        try {
            //renders the map from start position of the currentmap

            painter.update(currentWorldMap.getStartPosition());
        } catch (TooLowException tie) {
            alertBox("DONT DIG ANYMORE",
                    "THE CURRENT TILE HAS NO BLOCKS", 2);

        }

        root.getChildren().get(1).setDisable(false);
        fileMenu.getItems().get(0).setDisable(false);

        inventoryList.clear();

        /*initalize inventory*/
        for (int g = 0; g < currentWorldMap.getBuilder().getInventory().size();
                g++) {
            inventoryList
                    .add(currentBuilder.getInventory().get(g).getBlockType());
        }
        builderInventory
                .setText("Builder Inventory :\n" + inventoryList.toString());

    }

    /**
     * This is where the JavaFX windows starts, a main gridpane containing
     * control buttons and mapview is pack on to the primaryStage
     *
     * @param primaryStage - The primary stage of the JavaFX application
     */

    @Override
    public void start(Stage primaryStage) {

        //set up the size of the mapDisplay
        mapDisplay.setPrefSize(450, 450);

        //main grid pane
        root = new GridPane();
        primaryStage.setTitle("BlockWorld of Tom");

        //GridPane that contains controls
        GridPane right = new GridPane();
        //set the padding of 20 all around
        right.setPadding(new Insets(20, 20, 20, 20));

        //file menu
        fileMenu = new Menu("File");
        MenuItem m1 = new MenuItem("SaveMap");
        MenuItem m2 = new MenuItem("Load Map");
        m2.setOnAction(e -> LoadMap(primaryStage));
        m1.setOnAction(e -> SaveMap(primaryStage));
        fileMenu.getItems().addAll(m1, m2);

        //mainMenu
        MenuBar menuBar = new MenuBar();
        menuBar.getMenus().addAll(fileMenu);
        gameControl(right);

        //adds all the items into the main gridpane
        setConstraints(menuBar, 0, 0);
        setConstraints(right, 1, 1);
        setConstraints(mapDisplay, 0, 1);
        mapDisplay.setStyle("-fx-background-color: #C0C0C0;");
        builderInventoryLayout(root);
        root.getChildren().addAll(right, builderInventory, menuBar, mapDisplay);

        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.show();
        //enable the controls and savemap
        root.getChildren().get(1).setDisable(true);
        fileMenu.getItems().get(0).setDisable(true);

    }

    /**
     * creates a scroll pane to store the builder's inventory and put it inside
     * the given gridpane.
     *
     * @param main - the Gridpane to put the scrollpane inside
     */
    private void builderInventoryLayout(GridPane main) {
        ScrollPane builderInventoryPane = new ScrollPane();
        builderInventory = new Label(
                "Builder Inventory  :\n" + inventoryList.toString());

        builderInventoryPane.setContent(builderInventory);
        builderInventoryPane.setPrefViewportWidth(100);
        builderInventoryPane.setPrefViewportHeight(40);

        main.add(builderInventoryPane, 0, 4);
    }

    /**
     * this method is created to be able to put all the controls of the gui into
     * 1 pane, and calls corresponding event handlers methods(e.g:handle dig)
     * once the buttons are been clicked.
     * @param right - the Grid pane to put all the controls(dig,move,drop etc)
     * inside
     */


    private void gameControl(GridPane right) {

        ChoiceBox builderChoice = new ChoiceBox(
                FXCollections
                        .observableArrayList("Move_builder", "Move_block"));
        builderChoice.setValue("Move_builder");

        /*Handles the event of North here*/

        Button north = new Button("North");
        north.setOnAction(e -> handleMove(builderChoice, "north"));


        /*hanldes the even of west here*/
        Button west = new Button("West");
        west.setOnAction(e -> handleMove(builderChoice, "west"));

        /*handles the event of east here*/
        Button east = new Button("East");
        east.setOnAction(e -> handleMove(builderChoice, "east"));

        /* handles the even of south*/
        Button south = new Button("South");
        south.setOnAction(e -> handleMove(builderChoice, "south"));

        Button dig = new Button("Dig");
        dig.setOnAction(e -> handleDig());
        Button drop = new Button("drop");
        TextField dropIndex = new TextField();
        drop.setOnAction(e -> handleDrop(dropIndex.getText()));

        right.add(north, 1, 0);
        right.add(west, 0, 1);
        right.add(east, 2, 1);
        right.add(south, 1, 3);

        right.add(builderChoice, 0, 4);
        right.add(dig, 0, 5);
        right.add(drop, 0, 6);
        right.add(dropIndex, 1, 6);
    }

    /**
     * handles the action that need to be performed once movebuilder or
     * moveblock buttons are been clicked. This method will try to move the
     * builder or block(through the choice box chosen) in the specified
     * direction(through which button is been clicked,eg:north,south,etc) and
     * try to updated and render the GUI accordingly(by calling update in
     * painter).Alert boxes with guidance will been shown if the action is
     * illegal to perform(e.g alertbox will be shown if the user clicked on
     * movebuilder north and the north exits for the current tile does not
     * exit.
     *
     * @param choiceBox - checkbox choice chosen(between moveblock and
     * movebuilder)
     * @param direction - the direction of movement for builder or block
     */


    private void handleMove(ChoiceBox<String> choiceBox, String direction) {
        if (choiceBox.getValue().equals("Move_builder")) {

            try {

                currentBuilder
                        .moveTo(currentWorldMap.getBuilder().getCurrentTile()
                                .getExits()
                                .get(direction));

                directionMapX.put("north", 0);
                directionMapY.put("north", -1);
                directionMapX.put("south", 0);
                directionMapY.put("south", +1);
                directionMapX.put("east", +1);
                directionMapY.put("east", 0);
                directionMapX.put("west", -1);
                directionMapY.put("west", 0);

                int shiftX = directionMapX.get(direction);
                int shiftY = directionMapY.get(direction);

                int CoordinateXNorth = currentPosition.getX() + shiftX;
                int CoordinateYNorth = currentPosition.getY() + shiftY;
                currentPosition = new Position(CoordinateXNorth,
                        CoordinateYNorth);
                try {
                    painter.update(currentPosition);

                } catch (TooLowException TLE) {
                    alertBox("TOO LOW",
                            "THIS TILE HAS NO BLOCKS!", 0);

                }


            } catch (NoExitException NEE) {
                alertBox("CANNOT MOVE BUILDER",
                        "Cannot move builder " + direction
                                + "\n No Exits this way"
                                + "\n Or height of target tile is not compatible",
                        0);
            }
        }
        if (choiceBox.getValue().equals("Move_block")) {
            try {
                currentBuilder.getCurrentTile().moveBlock(direction);

                try {
                    painter.update(currentPosition);

                } catch (TooLowException TLE) {
                    alertBox("TOO LOW",
                            "THIS TILE HAS NO BLOCKS!", 0);

                }

            } catch (TooHighException E) {
                alertBox("CANNOT MOVE BLOCK",
                        "Cannot move block " + direction
                                + "\nTarget tile is too high ", 0);

            } catch (InvalidBlockException e) {
                alertBox("CANNOT MOVE BLOCK",
                        "Cannot move block " + direction
                                + "\nTop block on current tile is not moveable",
                        0);
            } catch (NoExitException e) {
                alertBox("CANNOT MOVE BLOCK",
                        "Cannot move block " + direction
                                + "\n No Exits this way",
                        0);
            }
        }
    }

    /**
     * handles the action that needs to be done, once the dig button is clicked
     * by calling dig on current tile and update(in painter), and updates the
     * worldmap and GUI accordingly. Alert boxes with guidance will be shown if
     * dig cannot performed(e.g the currentTile is not diggable etc).
     */
    private void handleDig() {
        try {
            currentBuilder.digOnCurrentTile();
            inventoryList.clear();

            for (int g = 0;
                    g < currentWorldMap.getBuilder().getInventory().size();
                    g++) {
                inventoryList
                        .add(currentBuilder.getInventory().get(g)
                                .getBlockType());
            }
            builderInventory.setText(
                    "Builder Inventory :\n" + inventoryList.toString());

            painter.update(currentPosition);

        } catch (InvalidBlockException e) {
            alertBox("INVALID DIG ACTION",
                    "Top block on current tile is not diggable", 0);

        } catch (TooLowException e) {
            alertBox("INVALID DIG ACTION",
                    "there are no blocks on the current tile", 0);
        }


    }

    /**
     * handles the action that needs to be done, once the drop button is
     * clicked.This method will perform the drop action on the current tile with
     * the specified index given(index Entered), this also updates the GUI's
     * builder's  inventory and the actual map displayed(calling update in
     * painter).Alert boxes will be shown if the drop action cannot be
     * performed(i.e if the given index is out of range from inventory list and
     * etc).
     *
     * @param indexEntered - index of the inventory that needs to be dropped on
     * the currentTile from the builder's inventory.
     */
    public void handleDrop(String indexEntered) {
        try {
            int Index = Integer.parseInt(indexEntered);
            try {
                currentBuilder.dropFromInventory(Index);
                inventoryList.remove(Index);
                builderInventory.setText(
                        "Builder Inventory :\n" + inventoryList.toString());
                try {
                    painter.update(currentPosition);

                } catch (TooLowException TLE) {
                    alertBox("TOO LOW",
                            "THIS TILE HAS NO BLOCKS!", 0);

                }

            } catch (InvalidBlockException IBE) {
                alertBox("INVALID TO DROP",
                        "Inventory index out of range", 0);
            } catch (TooHighException THE) {
                alertBox("INVALID TO DROP",
                        "Current tile is too high to drop on", 0);
            }

        } catch (NumberFormatException e) {
            alertBox("INVALID INPUT",
                    "Please Enter An Integer", 0);
        }
    }

    /**
     * creates an alert box once this method is called, with specific title
     * content and alert type. integer 0 represents warning, 1 represents error
     * and 2 represents information
     *
     * @param Title - title of the alert
     * @param Content - message of the alert
     * @param type - type of alert
     */

    private void alertBox(String Title, String Content, int type) {

        Alert alert = new Alert(AlertType.NONE);
        if (type == 0) {
            alert.setAlertType(AlertType.WARNING);
        }
        if (type == 1) {
            alert.setAlertType(AlertType.ERROR);
        }
        if (type == 2) {
            alert.setAlertType(AlertType.INFORMATION);
        }
        alert.setTitle(Title);
        alert.setHeaderText(Content);
        alert.showAndWait();
    }


}
