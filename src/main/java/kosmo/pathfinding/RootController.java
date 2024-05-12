package kosmo.pathfinding;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.layout.GridPane;
import javafx.scene.shape.Rectangle;

public class RootController
{
    // --------------------------
    // Attributes

    // Grid
    @FXML private GridPane grid;
    private final int GRID_COLUMNS = 30;
    private final int GRID_ROWS = 15;
    private final double SQUARE_SIZE = 25;
    private final GridSquare[][] gridElements = new GridSquare[GRID_ROWS][GRID_COLUMNS];

    // Buttons
    @FXML private Button originButton;
    @FXML private Button destinationButton;
    @FXML private Button obstacleButton;

    // Choice Boxes
    @FXML private ChoiceBox<Algorithm> algorithmChoiceBox;
    @FXML private ChoiceBox<Map> mapChoiceBox;

    // Selections
    @FXML private final PaintWand paintWand = PaintWand.getInstance();

    // --------------------------
    // Initialization Methods
    @FXML public void initialize()
    {
        initializeGrid();
        initializeWand();
        initializeChoiceBoxes();
        initializeListeners();

        // TESTS
        //startAlgorithm();
    }

    private void initializeGrid()
    {
        // Clear the previous content
        grid.getChildren().clear();

        // Fill the grid with squares
        for(int row = 0; row < GRID_ROWS; row++ )
        {
            for(int col = 0; col < GRID_COLUMNS; col++)
            {
                Rectangle square = new Rectangle(SQUARE_SIZE, SQUARE_SIZE);
                grid.add(square, col, row);
                GridPane.setMargin(square, new javafx.geometry.Insets(1)); // Add a margin of 1 pixel

                // Add to the array
                gridElements[row][col] = new GridSquare(square, row, col);
            }
        }

        // Set two furthest elements as origin and destination by default
        gridElements[0][0].setState(State.ORIGIN);
        gridElements[GRID_ROWS - 1][GRID_COLUMNS - 1].setState(State.DESTINATION);

        // Make the squares visible
        grid.setGridLinesVisible(true);
    }

    private void initializeWand()
    {
        paintWand.setOrigin(gridElements[0][0]);
        paintWand.setDestination(gridElements[GRID_ROWS - 1][GRID_COLUMNS - 1]);
    }

    private void initializeChoiceBoxes()
    {
        // Algorithm Choice Box
        algorithmChoiceBox.getItems().addAll(Algorithm.values());
        algorithmChoiceBox.setValue(Algorithm.TEST1);

        // Map Choice Box
        mapChoiceBox.getItems().addAll(Map.values());
        mapChoiceBox.setValue(Map.TEST_MAP1);
    }
    
    // Listeners
    private void initializeListeners()
    {
        // Algorithm choice box
        algorithmChoiceBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null)
            {
                System.out.println("Algorithm changed to: " + newValue);
            }
        });

        // Map Choice Box
        mapChoiceBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null)
            {
                System.out.println("Map changed to: " + newValue);
            }
        });
    }

    // --------------------------
    // Event Methods
    @FXML private void originSelectedEvent()
    {
        paintWand.setFunction(State.ORIGIN);
    }

    @FXML private void destinationSelectedEvent()
    {
        paintWand.setFunction(State.DESTINATION);
    }

    @FXML private void obstacleSelectedEvent()
    {
        paintWand.setFunction(State.OBSTACLE);
    }

    // -------------------------
    // Testing Methods
    public void startAlgorithm()
    {
        PathfindingAlgorithm algorithm = new PathfindingAlgorithm(gridElements);
        Thread algorithmThread = new Thread(algorithm);
        algorithmThread.start();
    }

}