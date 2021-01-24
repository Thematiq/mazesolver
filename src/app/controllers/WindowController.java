package app.controllers;

import engine.graph.Graph;
import engine.interfaces.Algorithm;
import engine.interfaces.AlgorithmTable;
import engine.interfaces.GeneratorTable;
import engine.tools.Vector;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.util.Duration;

import java.net.URL;
import java.util.Random;
import java.util.ResourceBundle;
import java.util.regex.Pattern;

public class WindowController implements Initializable {
    private final Image king = new Image(getClass().getResource("../../resources/king.png").toString());
    private Vector kingPos = new Vector(0, 0);
    private Vector endPos = new Vector(5,5 );
    private Graph g;
    private Algorithm solver;
    private boolean semaphore;
    private boolean paused;
    private Vector lastSolved;
    private Timeline solvingAnimation;

    @FXML
    private GridPane boardGrid;
    @FXML
    private Canvas boardCanvas;
    @FXML
    private ComboBox<String> algorithmCombo;
    @FXML
    private ComboBox<String> generatorCombo;
    @FXML
    private Button startButton;
    @FXML
    private Button resizeButton;
    @FXML
    private Button stopButton;
    @FXML
    private Button resetButton;
    @FXML
    private Button generateButton;
    @FXML
    private Button clearButton;
    @FXML
    private Button pauseButton;
    @FXML
    private RadioButton adventureRadio;
    @FXML
    private RadioButton solvingRadio;
    @FXML
    private RadioButton drawingRadio;
    @FXML
    private TextField widthText;
    @FXML
    private TextField heightText;

    private GraphicsContext gc;
    private int width;
    private int height;
    private int cellSize;
    private boolean[][] map;

    private final Pattern numericPattern = Pattern.compile("^([1-9][0-9]*)?$");

    public void inputHandler(KeyEvent event) {
        if (this.adventureRadio.isSelected() && !this.semaphore) {
            Vector newPos = switch(event.getCode()) {
                case A -> new Vector(Math.max(this.kingPos.x - 1, 0), this.kingPos.y);
                case D -> new Vector(Math.min(this.kingPos.x + 1, this.width - 1), this.kingPos.y);
                case S -> new Vector(this.kingPos.x ,Math.min(this.kingPos.y + 1, this.height - 1));
                case W -> new Vector(this.kingPos.x, Math.max(this.kingPos.y - 1, 0));
                default -> this.kingPos;
            };
            if (!this.map[newPos.x][newPos.y]) {
                this.kingPos = newPos;
            }
            this.draw();
        }

    }

    private TextFormatter.Change numericChange(TextFormatter.Change change) {
        if (numericPattern.matcher(change.getControlNewText()).matches()) {
            return change;
        } else {
            return null;
        }
    }

    @FXML
    private void stopSolver() {
        this.solvingAnimation.stop();
        do {
            this.lastSolved = this.solver.nextStep();
        } while(lastSolved != null && !lastSolved.equals(this.endPos));
        this.semaphoreAction(false);
        this.drawSolver();
    }

    @FXML
    private void clearSolver() {
        this.draw();
    }

    @FXML
    private void pauseSolver() {
        if (this.paused) {
            this.pauseButton.setText("Pause solver");
            this.solvingAnimation.play();
        } else {
            this.pauseButton.setText("Unpause solver");
            this.solvingAnimation.pause();
        }
        this.paused = !this.paused;
    }


    @FXML
    private void generateMaze() {

        String alg = this.generatorCombo.getValue();
        if (alg == null) {
            return;
        }
        this.map = GeneratorTable.fromString(alg).getMap(this.width, this.height, this.kingPos);

        Random r = new Random();
        while (this.map[this.endPos.x][this.endPos.y]) {
            this.endPos = new Vector(r.nextInt(this.width), r.nextInt(this.height));
        }
        this.draw();
    }

    @FXML
    private void resizeMap() {
        if (this.widthText.getText().equals("") || this.heightText.getText().equals("")) {
            return;
        }
        this.setBoard(
                Integer.parseInt(this.widthText.getText()),
                Integer.parseInt(this.heightText.getText())
        );
    }

    private void semaphoreAction(boolean val) {
        this.semaphore = val;
        this.resetButton.setDisable(this.semaphore);
        this.startButton.setDisable(this.semaphore);
        this.resizeButton.setDisable(this.semaphore);
        this.stopButton.setDisable(!this.semaphore);
        this.generateButton.setDisable(this.semaphore);
        this.clearButton.setDisable(this.semaphore);
        this.pauseButton.setDisable(!this.semaphore);
    }

    @FXML
    private void animate() {
        this.semaphoreAction(true);
        this.paused = false;

        String alg = this.algorithmCombo.getValue();
        if (alg == null) {
            return;
        }

        this.solver = AlgorithmTable.fromString(alg).getAlgorithm();
        this.solver.loadGraph(Graph.getFromHashMap(this.map, this.width, this.height));
        this.solver.initAlgorithm(this.kingPos, this.endPos);

        this.solvingAnimation = new Timeline(new KeyFrame(Duration.millis(50), e -> this.nextStep()));
        this.solvingAnimation.setCycleCount(Animation.INDEFINITE);
        this.solvingAnimation.play();

    }

    private void nextStep() {
        this.lastSolved = this.solver.nextStep();
        if (this.lastSolved == null || this.lastSolved.equals(this.endPos) ) {
            this.solvingAnimation.stop();
            this.semaphoreAction(false);
        }
        this.drawSolver();
    }

    @FXML
    private void resetBoard() {
        this.map = new boolean[this.width][this.height];
        this.draw();
    }

    @FXML
    private void setTile(MouseEvent e) {
        if (this.semaphore) {
            return;
        }
        Vector posClicked = new Vector(
                (int) e.getX() / this.cellSize,
                (int) e.getY() / this.cellSize
        );
        if (this.solvingRadio.isSelected()) {
            if (e.getButton() == MouseButton.PRIMARY) {
                this.kingPos = posClicked;
            } else if (e.getButton() == MouseButton.SECONDARY) {
                this.endPos = posClicked;
            }
        } else if (this.drawingRadio.isSelected()) {
            this.g = Graph.getFromHashMap(this.map, this.width, this.height);
            if (!posClicked.equals(this.endPos) && !posClicked.equals(this.kingPos)) {
                if (e.getButton() == MouseButton.PRIMARY) {
                    this.map[posClicked.x][posClicked.y] = true;
                } else if (e.getButton() == MouseButton.SECONDARY) {
                    this.map[posClicked.x][posClicked.y] = false;
                }
            }
        }
        this.draw();
    }

    private void listenCanvasResize(ObservableValue<? extends Number> observableValue, Number number, Number number1) {
        this.boardCanvas.setHeight(this.boardGrid.getHeight());
        this.boardCanvas.setWidth(this.boardGrid.getWidth());
        this.initDrawer();
        this.draw();
    }

    private void initDrawer() {
        this.cellSize = (int) Math.min(this.boardCanvas.getHeight() / this.height, this.boardCanvas.getWidth() / this.width);
        this.boardCanvas.setWidth(this.cellSize * this.width);
        this.boardCanvas.setHeight(this.cellSize * this.height);
    }

    void drawSolver() {
        this.draw();
        this.drawVisited();
        this.drawPath();
        this.drawEndpoints();
    }

    private void drawEndpoints() {
        this.gc.setFill(Color.DARKBLUE);
        this.gc.fillRect(this.endPos.x * this.cellSize, this.endPos.y * this.cellSize, this.cellSize, this.cellSize);
        this.gc.drawImage(this.king, this.kingPos.x * this.cellSize, this.kingPos.y * this.cellSize, this.cellSize, this.cellSize);
    }

    void draw() {
        for (int x = 0; x < this.width; ++x) {
            for (int y = 0; y < this.height; ++y) {
                if ((x + y) % 2 == 0) {
                    this.gc.setFill(Color.BEIGE);
                } else {
                    this.gc.setFill(Color.DARKGRAY);
                }
                if (this.map[x][y]) {
                    this.gc.setFill(Color.DARKRED);
                }
                this.gc.fillRect(x * this.cellSize, y * this.cellSize, this.cellSize, this.cellSize);
            }
        }
        this.drawEndpoints();
    }

    void drawVisited() {
        Vector ii;
        boolean[] visited = this.solver.getVisited();
        for (int i = 0; i < visited.length; ++i) {
            if (visited[i]) {
                ii = Graph.nodeToVector(i, this.width);
                if ((ii.x + ii.y) % 2 == 0) {
                    this.gc.setFill(Color.YELLOW);
                } else {
                    this.gc.setFill(Color.GOLD);
                }
                this.gc.fillRect(ii.x * this.cellSize, ii.y * this.cellSize, this.cellSize, this.cellSize);
            }
        }
    }

    void drawPath() {
        if (this.lastSolved == null) {
            return;
        }
        int shift = this.cellSize / 2;
        int currentNode = Graph.vectorToNode(this.lastSolved, this.width);
        int lastNode = this.solver.getParent(currentNode);
        Vector currentVector;
        Vector lastVector;
        this.gc.setStroke(Color.HOTPINK);
        this.gc.setLineWidth(5);
        while (lastNode != -1) {
            lastVector = Graph.nodeToVector(lastNode, this.width);
            currentVector = Graph.nodeToVector(currentNode, this.width);
            this.gc.strokeLine(
                    lastVector.x * this.cellSize + shift,
                    lastVector.y * this.cellSize + shift,
                    currentVector.x * this.cellSize + shift,
                    currentVector.y * this.cellSize + shift
            );
            currentNode = lastNode;
            lastNode = this.solver.getParent(currentNode);
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.gc = this.boardCanvas.getGraphicsContext2D();
        this.boardGrid.heightProperty().addListener(this::listenCanvasResize);
        this.boardGrid.widthProperty().addListener(this::listenCanvasResize);

        this.widthText.setTextFormatter(new TextFormatter<>(this::numericChange));
        this.heightText.setTextFormatter(new TextFormatter<>(this::numericChange));
        this.semaphoreAction(false);

        this.algorithmCombo.getItems().add("DFS");
        this.algorithmCombo.getItems().add("BFS");
        this.algorithmCombo.getItems().add("AStar");
        this.algorithmCombo.setValue("BFS");

        this.generatorCombo.getItems().add("Prim");
        this.generatorCombo.getItems().add("RDFS");
        this.generatorCombo.setValue("Prim");

        ToggleGroup tg = new ToggleGroup();
        this.adventureRadio.setToggleGroup(tg);
        this.solvingRadio.setToggleGroup(tg);
        this.drawingRadio.setToggleGroup(tg);
        this.solvingRadio.setSelected(true);
        this.setBoard(8, 8);
        this.g = Graph.getFromHashMap(this.map, this.width, this.height);
    }

    public void setBoard(int width, int height) {
        this.width = width;
        this.height = height;
        this.map = new boolean[width][height];
        this.initDrawer();
        this.draw();
    }
}
