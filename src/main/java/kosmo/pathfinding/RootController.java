package kosmo.pathfinding;

import javafx.beans.Observable;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.shape.Rectangle;

import java.util.LinkedList;

public class RootController
{
    // --------------------------
    // Attributes

    // Grid
    @FXML private GridPane gridPane;
    private final int GRID_COLUMNS = 30;    // redundant variables / use constants from Scene instead
    private final int GRID_ROWS = 15;
    private final double SQUARE_SIZE = 25;
    private final GridSquare[][] gridElements = new GridSquare[GRID_ROWS][GRID_COLUMNS];

    // Scene Saving / Deleting / Update
    @FXML private Button saveButton;
    @FXML private Button updateButton;
    @FXML private Button deleteButton;
    @FXML private TextField filenameField;

    // Choice Boxes
    @FXML private ChoiceBox<Algorithm> algorithmChoiceBox;
    @FXML private ChoiceBox<String> sceneChoiceBox;

    // Output Console
    @FXML private TextArea consoleTextArea;

    // Vis Timer
    @FXML private Label speedLabel;

    // Scenes
    private LinkedList<Scene> scenes;
    private final LinkedList<String> sceneNames = new LinkedList<>();

    // Selections
    private Algorithm currentAlgorithm;
    private String currentScene;

    // --------------------------
    // Initialization Methods
    @FXML public void initialize()
    {
        initializeConsole();
        initializeGrid();
        initializeWand();
        initializeScenes();
        initializeChoiceBoxes();
        initializeVisTimer();
        initializeListeners();

        // Tests
        test();
    }

    private void initializeGrid()
    {
        // Clear the previous content
        gridPane.getChildren().clear();

        // Fill the grid with squares
        for(int row = 0; row < GRID_ROWS; row++ )
        {
            for(int col = 0; col < GRID_COLUMNS; col++)
            {
                Rectangle square = new Rectangle(SQUARE_SIZE, SQUARE_SIZE);
                gridPane.add(square, col, row);
                GridPane.setMargin(square, new Insets(1)); // Add a margin of 1 pixel

                // Add to the array
                gridElements[row][col] = new GridSquare(square, row, col);
            }
        }

        gridElements[0][0].setState(State.ORIGIN);
        gridElements[Scene.GRID_ROWS - 1][Scene.GRID_COLUMNS - 1].setState(State.DESTINATION);

        // Make the squares visible
        gridPane.setGridLinesVisible(true);
    }

    private void initializeWand()
    {
        PaintWand.get().setOrigin(gridElements[0][0]);
        PaintWand.get().setDestination(gridElements[GRID_ROWS - 1][GRID_COLUMNS - 1]);
    }

    private void initializeScenes()
    {
        scenes = SceneLoader.loadScenes();

        // Add first scene to list as default
        scenes.addFirst(SceneLoader.createEmptyScene("default"));

        // Copy scene names
        for(Scene scene : scenes)
            sceneNames.addLast(scene.getName());

        currentScene = scenes.getFirst().getName();
        setScene(scenes.get(sceneNames.indexOf(currentScene)));

        // Set buttons
        saveButton.setDisable(false);
        updateButton.setDisable(true);
        deleteButton.setDisable(true);
    }

    private void initializeChoiceBoxes()
    {
        // Algorithm Choice Box
        algorithmChoiceBox.getItems().addAll(Algorithm.values());
        algorithmChoiceBox.setValue(Algorithm.TEST1);
        currentAlgorithm = Algorithm.TEST1;

        // Scene Choice Box
        sceneChoiceBox.getItems().addAll(sceneNames);
        sceneChoiceBox.setValue(sceneNames.getFirst());
        currentScene = sceneNames.getFirst();
    }

    private void initializeConsole()
    {
        OutputConsole.get().setOutputArea(consoleTextArea);
    }

    private void initializeVisTimer()
    {
        Execution.get().setSpeedText(speedLabel);
        Execution.get().setSpeed(1);
    }

    // Listeners
    private void initializeListeners()
    {
        // Algorithm choice box
        algorithmChoiceBox.getSelectionModel().selectedItemProperty().addListener(this::algorithmChangeEvent);

        // Scene Choice Box
        sceneChoiceBox.getSelectionModel().selectedItemProperty().addListener(this::sceneChangeEvent);
    }

    // --------------------------
    // Event Methods

    // Simulation Running
    public void startSimulation()
    {
        Test1Algorithm algorithm = switch (currentAlgorithm)
        {
            case TEST1 -> new Test1Algorithm(gridElements);
            case TEST2 -> new Test1Algorithm(gridElements);
        };

        Thread algorithmThread = new Thread(algorithm);
        algorithmThread.start();
    }

    // Paint Wand
    @FXML private void originSelectedEvent()
    {
        PaintWand.get().setFunction(State.ORIGIN);
    }

    @FXML private void destinationSelectedEvent()
    {
        PaintWand.get().setFunction(State.DESTINATION);
    }

    @FXML private void obstacleSelectedEvent()
    {
        PaintWand.get().setFunction(State.OBSTACLE);
    }

    // Simulation Control
    @FXML private void decreaseSpeedEvent()
    {
        Execution.get().setSpeed(Execution.get().getSpeed() - 0.1);
    }

    @FXML private void increaseSpeedEvent()
    {
        Execution.get().setSpeed(Execution.get().getSpeed() + 0.1);
    }

    @FXML private void togglePauseEvent()
    {
        if(Execution.get().isRunning())
            Execution.get().setPaused(!Execution.get().isPaused());
    }

    @FXML private void rewindEvent()
    {
        if(Execution.get().isRunning())
        {
            // Wrap up
            Execution.get().ceaseExecution();
        }
        else if(Execution.get().isRefreshed())
        {
            // Start up
            Execution.get().setPaused(false);
            startSimulation();

            // Disable data buttons
            saveButton.setDisable(true);
            updateButton.setDisable(true);
            deleteButton.setDisable(true);
        }
        else // Refresh
        {
            resetAlgorithmAndScene();
        }

    }

    // Choice Boxes
    private void algorithmChangeEvent(Observable observable, Algorithm oldValue, Algorithm newValue)
    {
        if (newValue != null)
        {
            System.out.println("Algorithm changed to: " + newValue);
            currentAlgorithm = newValue;

            OutputConsole.get().writeSeparator();
            waitForExecution();
        }
    }

    private void sceneChangeEvent(Observable observable, String oldValue, String newValue)
    {
        if (newValue != null)
        {
            System.out.println("Scene changed to: " + newValue);
            currentScene = newValue;

            OutputConsole.get().writeSeparator();
            waitForExecution();
        }
    }

    private void waitForExecution()
    {
        Execution.get().ceaseExecution();

        new Thread(() ->
        {
            while (Execution.get().isRunning())
            {
                try { Thread.sleep(100);}
                catch (InterruptedException e)
                {
                    Thread.currentThread().interrupt();
                    return;
                }
            }
            resetAlgorithmAndScene();

        }).start();
    }

    private void resetAlgorithmAndScene()
    {
        Execution.get().setRefreshed(true);
        OutputConsole.get().writeLn("Ready to reset the scene");
        setScene(scenes.get(sceneNames.indexOf(currentScene)));
    }

    private void setScene(Scene sceneToSet)
    {
        OutputConsole.get().writeLn("Setting up a scene");

        // Set buttons
        boolean isReadOnly = scenes.get(sceneNames.indexOf(currentScene)).isReadOnly();
        saveButton.setDisable(false);
        updateButton.setDisable(isReadOnly);
        deleteButton.setDisable(isReadOnly);

        // Remove previous origin from the Wand
        PaintWand.get().getOrigin().setState(State.NONE, false);
        PaintWand.get().getDestination().setState(State.NONE, false);

        GridSquare[][] gridSquares = sceneToSet.copyGrid();

        // Assign selected scene grid to displayed gird
        for(int row = 0; row < Scene.GRID_ROWS; row++)
        {
            for(int col = 0; col < Scene.GRID_COLUMNS; col++)
            {
                gridElements[row][col].setState(gridSquares[row][col].getState(), false);

                // Update Wand references
                if(gridElements[row][col].getState() == State.ORIGIN)
                    PaintWand.get().setOrigin(gridElements[row][col]);

                if(gridElements[row][col].getState() == State.DESTINATION)
                    PaintWand.get().setDestination(gridElements[row][col]);
            }
        }

        OutputConsole.get().writeLn("Scene " + sceneToSet.getName() + " is set");
        OutputConsole.get().writeSeparator();
    }

    // Map Saving / Update / Deletion
    @FXML private void saveSceneEvent()
    {
        saveAndReloadScene(false);
        filenameField.setText("");
    }

    @FXML private void updateSceneEvent()
    {
        saveAndReloadScene(true);
    }

    @FXML private void deleteSceneEvent()
    {
        // Clear RAM
        scenes.remove(scenes.get(sceneNames.indexOf(currentScene)));
        sceneNames.remove(currentScene);
        sceneChoiceBox.getItems().remove(currentScene);

        // Delete the file
        SceneLoader.deleteScene(currentScene);

        // Load default / first scene
        sceneChoiceBox.setValue(sceneNames.getFirst());
    }

    private void saveAndReloadScene(boolean overwrite)
    {
        OutputConsole.get().writeSeparator();

        // Create a new one using the wand and grid in rootController
        String name = filenameField.getText();
        if(overwrite) name = currentScene;

        Scene scene = new Scene(gridElements, name, PaintWand.get().getOrigin(), PaintWand.get().getDestination(), false);

        // This is terrible, but I spent more than three hours on trying to make it work at all
        // Scene is saved and then manually loaded again from the file
        // The scene would not set its origin and destination after saving, no matter what I did during it's entire session
        if(SceneLoader.saveScene(scene, overwrite))
        {
            try
            {
                scenes.addLast(SceneLoader.loadScene(name + "." + SceneLoader.extension));
                sceneNames.addLast(scenes.getLast().getName());
                sceneChoiceBox.getItems().addLast(sceneNames.getLast());

                // Delete previous instance
                if(overwrite)
                {
                    sceneNames.remove(currentScene);
                    scenes.remove(scenes.get(sceneNames.indexOf(currentScene)));
                    sceneChoiceBox.getItems().remove(currentScene);
                }

                sceneChoiceBox.setValue(scenes.getLast().getName());
            }
            catch (Exception e)
            {
                System.out.println("Error occurred during scene reload: " + e.getMessage());
            }
        }

        OutputConsole.get().writeSeparator();
    }

    // -------------------------
    // Testing Methods
    private void test()
    {

    }
}