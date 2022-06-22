package game;

import static javafx.scene.layout.GridPane.setConstraints;

import csse2002.block.world.GrassBlock;
import csse2002.block.world.SoilBlock;
import csse2002.block.world.StoneBlock;
import csse2002.block.world.TooLowException;
import csse2002.block.world.WoodBlock;
import csse2002.block.world.WorldMap;
import csse2002.block.world.Tile;
import csse2002.block.world.Position;
import java.util.HashMap;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.shape.Polygon;

/**
 * a class that paints all 81 tiles around the builder's current tile and update
 * the map display once called.
 */

public class Painter {
    /* the map that the painter is drawing tiles from*/
    private WorldMap map;
    /*currentile that the builder is on*/
    private Tile currentTile;
    /*position of the buildder's current tile*/
    private Position currentPosition;
    /*gridpane to put all the rendered tiles inside*/
    private GridPane mainPane;

    /**
     * Constructor of painter
     * when a painter is created, its assigns the given worldmap to map,
     * and puts all the tiles created into the mainPane
     * @param map - chosen world to draw tiles on
     * @param mainPane - pane to put all the render tiles inside
     */

    Painter(WorldMap map, GridPane mainPane) {
        this.map = map;
        this.currentTile = map.getTile(map.getStartPosition());
        this.currentPosition = map.getStartPosition();
        this.mainPane = mainPane;

    }

    /**
     * updates the display map of tiles by showing 81 tiles around the current
     * tile the builder is on. translate all 81 tile's position into a new
     * relative position system with current tile having coordinates(4,4). this
     * method then loops through all positions to draw tiles by calling tile
     * drawer.
     *
     * @param currentPosition - the current position of the builder
     * @throws TooLowException - if the tile does not have any blocks on it
     */

    void update(Position currentPosition) throws TooLowException {
        this.currentPosition = currentPosition;
        this.currentTile = map.getTile(currentPosition);

        HashMap<Position, Tile> posToTile = new HashMap<Position, Tile>();
        /*loop through all 81 positions around the current position and
        find the positions with a tile on it
         */
        for (int x = currentPosition.getX() - 4;
                x <= currentPosition.getX() + 4; x++) {
            for (int y = currentPosition.getY() - 4;
                    y <= currentPosition.getY() + 4; y++) {
                Position pos = new Position(x, y);

                if (map.getTile(pos) != null) {
                    /* for the positions that have a tile,
                    translate the position sin terms of its location within the
                    81 tiles around the current tile that the builder is on
                    always centred at(4,4)
                     */

                    int s = 4 + (pos.getX() - currentPosition.getX());
                    int r = 4 + ((pos.getY() - currentPosition.getY()));
                    posToTile.put(new Position(s, r),
                            map.getTile(pos));

                }
            }
        }
        /*loop through all 81 positions and draw blank rectangles for position that
        does not have a tile on it and draws the blocknumber, topblock colour and etc
        for positions that have a tile
         */
        for (int g = 0; g <= 8; g++) {
            for (int k = 0; k <= 8; k++) {
                Position pos = new Position(g, k);
                if (posToTile.get(pos) == null) {
                    TileDrawer tile = new TileDrawer(true, null);
                    mainPane.getChildren().add(tile);
                    tile.setTranslateX(g * 50);
                    tile.setTranslateY(k * 50);
                } else {
                    TileDrawer tile = new TileDrawer(false, posToTile.get(pos));
                    mainPane.getChildren().add(tile);
                    tile.setTranslateY(k * 50);
                    tile.setTranslateX(g * 50);

                }

            }
        }


    }

    /**
     * a class that extends StackPane, used to draw tiles inside the worldmap
     * and present them in the form of Stackpanes.
     */
    private class TileDrawer extends StackPane {


        private Polygon east = new Polygon();
        ;
        private Polygon north = new Polygon();
        ;
        private Polygon west = new Polygon();
        ;
        private Polygon south = new Polygon();
        ;
        private int numberOfTile;
        private String DisplayNumberOfBlocks;
        private Label blockNumber = new Label();
        private Label builder = new Label();
        ;

        /**
         * draws the tiles according to if they are blank or not, if blank then
         * draw a white rectangle, if not blank then fill the rectangle
         * accordingly to the current tile's top block colour, and shows the
         * number of block this tile has.If the builder is on this tile, then a
         * yellow dot will be drawn.
         *
         * @param isEmpty - boolean value on if the tile is empty or not
         * @param t - the tile that needs to be drawn
         * @throws TooLowException - if there are no blocks on the tile
         */

        public TileDrawer(boolean isEmpty, Tile t) throws TooLowException {
            Tile TiletoDraw = t;
            /*  draw the grids that are empty*/
            if (isEmpty) {
                Rectangle tileRectangle = new Rectangle(50, 50);
                tileRectangle.setFill(Color.WHITE);
                //tileRectangle.setStroke(Color.BLACK);
                getChildren().addAll(tileRectangle);

                TileDrawer.setAlignment(tileRectangle, Pos.CENTER);
                //System.out.println("i am drawing white spaces");

            }
            /* draw the grids that are not empty*/
            if (isEmpty != true) {

                Rectangle tileRectangle = new Rectangle(50, 50);
                numberOfTile = TiletoDraw.getBlocks().size();
                DisplayNumberOfBlocks = new Integer(numberOfTile).toString();
                blockNumber.setText(DisplayNumberOfBlocks);
                blockNumber.setTextFill(Color.WHITE);

                /* handles the colours of tiles*/

                if (TiletoDraw.getTopBlock() instanceof GrassBlock) {
                    tileRectangle.setFill(Color.GREEN);
                } else if (TiletoDraw.getTopBlock() instanceof StoneBlock) {
                    tileRectangle.setFill(Color.GREY);
                } else if (TiletoDraw.getTopBlock() instanceof SoilBlock) {
                    tileRectangle.setFill(Color.BLACK);
                } else if (TiletoDraw.getTopBlock() instanceof WoodBlock) {
                    tileRectangle.setFill(Color.BROWN);
                }


                /* handles exits on tile indicated by triangle */
                if (TiletoDraw.getExits().get("north") != null) {
                    north.setFill(Color.WHITE);
                    north.getPoints().addAll(new Double[]{
                            5.0, -5.0,
                            2.5, 0.0,
                            7.5, 0.0});

                    setAlignment(north, Pos.TOP_CENTER);
                }
                if (TiletoDraw.getExits().get("south") != null) {
                    south.setFill(Color.WHITE);
                    south.getPoints().addAll(new Double[]{
                            5.0, 5.0,
                            2.5, 0.0,
                            7.5, 0.0});

                    setAlignment(south, Pos.BOTTOM_CENTER);
                }
                if (TiletoDraw.getExits().get("east") != null) {
                    east.setFill(Color.WHITE);
                    east.getPoints().addAll(new Double[]{
                            10.0, 0.0,
                            5.0, 2.5,
                            5.0, -2.5});

                    setAlignment(east, Pos.CENTER_RIGHT);
                }
                if (TiletoDraw.getExits().get("west") != null) {
                    west.setFill(Color.WHITE);
                    west.getPoints().addAll(new Double[]{
                            0.0, 0.0,
                            5.0, 2.5,
                            5.0, -2.5});

                    setAlignment(west, Pos.CENTER_LEFT);
                }

                if (map.getBuilder().getCurrentTile() == TiletoDraw) {
                    blockNumber.setText(
                            DisplayNumberOfBlocks + "•");
                    blockNumber.setTextFill(Color.YELLOW);

                }

                tileRectangle.setStroke(Color.BLACK);
                getChildren()
                        .addAll(tileRectangle, west, east, north, south,
                                blockNumber, builder);
                //TileDrawer.setAlignment();
                TileDrawer.setAlignment(tileRectangle, Pos.CENTER);
            }

        }/*bracket for isEmpty*/
    }


}
