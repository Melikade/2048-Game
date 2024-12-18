package com.example._2048;

import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.fxml.Initializable;
import java.net.URL;
import java.text.FieldPosition;
import java.util.Random;
import java.util.ResourceBundle;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;


public class Board implements Initializable{
    private Node first ;
    private Label[][] labels = new Label[4][4];
    private static Node[][] stackNode = new Node[1000][16];
    private static Node[][] stackNodeRedo = new Node[1000][16];
    private int[] scoreUndo = new int[1000];
    private int[] scoreRedo = new int[1000];
    private static int countUndo = 5;
    private static int countRedo = 5;
    private static int top = -1;
    private static int topRedo = -1;
    @FXML
    public Label score;
    @FXML
    public Button undo;
    @FXML
    public Button redo;
    @FXML
    public Button restart;
    @FXML
    public GridPane board;



    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            score.setText("0");
            initializeLabels();
            addNewTale();
            addNewTale();
            setBoard();
            saveState();
            board.setOnKeyPressed(event -> {
                switch (event.getCode()) {
                    case UP -> moveUp();
                    case DOWN -> moveDown();
                    case LEFT -> moveLeft();
                    case RIGHT -> moveRight();
                }
                isGameOver();
                event.consume();
                board.requestFocus();
            });
            board.setFocusTraversable(true);
            board.requestFocus();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void initializeLabels() {
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                labels[i][j] = new Label("");
                labels[i][j].setFont(new Font("AppleGothic", 25));
                labels[i][j].setTextFill(Color.rgb(141,51,1));
                labels[i][j].alignmentProperty().set(Pos.CENTER);
                board.add(labels[i][j], j,i);
            }
        }
    }

    public void showMessage(String message) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Information Dialog");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    //restarting the game
    @FXML
    public void restart() {
        first = null;
        countUndo = 5;
        countRedo = 5;
        top = -1;
        topRedo = -1;
        for (int i = 0; i < 7; i++) {
            for (int j = 0; j < 16; j++) {
                stackNode[i][j] = new Node(0, -1, -1);
            }
        }
        for (int i = 0; i < 7; i++) {
            for (int j = 0; j < 16; j++) {
                stackNodeRedo[i][j] = new Node(0, -1, -1);
            }
        }
        score.setText("0");
        addNewTale();
        addNewTale();
        setBoard();
        saveState();
        board.requestFocus();
    }

    //saving the boards state
    public void saveState() {
        Node p = first;
        top++;
        for (int i = 0; p != null; i++) {
            Node q = new Node(p.value, p.row, p.col);
            stackNode[top][i] = q;
            p = p.next;
        }

        for (int i = 0; i < stackNode[top].length; i++) {
            if (stackNode[top][i] == null) {
                stackNode[top][i] = new Node(0, -1, -1);
            }
        }
        scoreUndo[top]=Integer.parseInt(score.getText());
//        for (int i = 0; i <= top; i++) {
//            for (int j = 0; j < stackNode[i].length; j++) {
//                if (stackNode[i][j] != null) {
//                    System.out.println(stackNode[i][j].value);
//                    System.out.println(stackNode[i][j].row);
//                    System.out.println(stackNode[i][j].col);
//                }
//            }
//            System.out.println('\n');
//            System.out.println('\n');
//        }
    }

    @FXML
    public void undo() {
        if (top >= 0 && countUndo>0) {
            // Save the current state to redo stack
            topRedo++;
            for (int i = 0; i < stackNode[top].length; i++) {
                Node q = new Node(stackNode[top][i].value, stackNode[top][i].row, stackNode[top][i].col);
                stackNodeRedo[topRedo][i] = q;
                scoreRedo[topRedo] = scoreUndo[top];
                stackNode[top][i].value = 0;
                stackNode[top][i].row = -1;
                stackNode[top][i].col = -1;
            }
            top--;
            score.setText(Integer.toString(scoreUndo[top]));
            // Restore the state from undo stack
            first = null;
            for (int i = 0; i < stackNode[top].length && stackNode[top][i].value != 0; i++) {
                Node newNode = new Node(stackNode[top][i].value, stackNode[top][i].row, stackNode[top][i].col);
                stackNode[top][i].value = 0;
                stackNode[top][i].row = -1;
                stackNode[top][i].col = -1;
                if (first == null) {
                    first = newNode;
                } else {
                    Node p = first;
                    while (p.next != null) {
                        p = p.next;
                    }
                    p.next = newNode;
                }
            }
            top--;
            countUndo--;
            saveState();
            setBoard();
        }
        else if(countUndo == 0){
            showMessage("NO MORE UNDOS LEFT");
        }
        board.requestFocus();
    }


    @FXML
    public void redo() {
        if (topRedo >= 0 && countRedo>0) {
            score.setText(Integer.toString(scoreRedo[topRedo]));
            // Restore the state from redo stack
            first = null;
            for (int i = 0; i < stackNodeRedo[topRedo].length && stackNodeRedo[topRedo][i].value != 0; i++) {
                Node newNode = new Node(stackNodeRedo[topRedo][i].value, stackNodeRedo[topRedo][i].row, stackNodeRedo[topRedo][i].col);
                stackNodeRedo[topRedo][i].value = 0;
                stackNodeRedo[topRedo][i].row = -1;
                stackNodeRedo[topRedo][i].col = -1;
                if (first == null) {
                    first = newNode;
                } else {
                    Node p = first;
                    while (p.next != null) {
                        p = p.next;
                    }
                    p.next = newNode;
                }
            }
            topRedo--;
            countRedo--;
            saveState();
            setBoard();
        }
        else if(countRedo == 0){
            showMessage("NO MORE REDOS LEFT");
        }
        board.requestFocus();
    }

    public void addNewTale() {
        Random rand = new Random();
        int value = rand.nextInt(10) < 7 ? 2 : 4; // 70% chance of 2, 30% chance of 4
        int row, col;

        do {
            row = rand.nextInt(4);
            col = rand.nextInt(4);
        } while (isOccupied(row, col));

        Node newNode = new Node(value, row, col);
        if (first == null) {
            first = newNode;
        } else {
            Node p = first;
            while (p.next != null) {
                p = p.next;
            }
            p.next = newNode;
        }
    }

    private boolean isOccupied(int row, int col) {
        Node p = first;
        while (p != null) {
            if (p.row == row && p.col == col) {
                return true;
            }
            p = p.next;
        }
        return false;
    }

    public void setBoard() {
        clearLabels();
        Node p = first;
        while(p != null){
            labels[p.row][p.col].setText(Integer.toString(p.value));
            p = p.next;
        }
    }
    public void clearLabels() {
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                labels[i][j].setText("");
            }
        }
    }

    // has the player made a move in which the board the changes
    public boolean successfulMove() {
        Node p = first;
        for (int i = 0; i < stackNode[top].length && p != null; i++) {
            if (stackNode[top][i] == null && p != null) {
                return true;
            }
            if (stackNode[top][i] != null && p == null) {
                return true;
            }
            if (stackNode[top][i] != null && p != null && (stackNode[top][i].value != p.value ||  stackNode[top][i].row != p.row || stackNode[top][i].col != p.col)){
                return true;
            }
            p = p.next;
        }
        return false;
    }


    // moving up the tiles
    public void moveUp() {
        // Put each column inside an array
        for (int col = 0; col < 4; col++) {
            Node[] columnNodes = new Node[4];

            // Extract nodes in the current column
            Node p = first;
            while (p != null) {
                if (p.col == col) {
                    columnNodes[p.row] = new Node(p.value, p.row, p.col);
                }
                p = p.next;
            }


            // Shift nodes to fill empty spaces
            for (int i = 1; i < 4; i++) {
                if (columnNodes[i] != null) {
                    int j = i;
                    while (j > 0 && columnNodes[j - 1] == null) {
                        columnNodes[j - 1] = columnNodes[j];
                        columnNodes[j] = null;
                        columnNodes[j - 1].row = j - 1;
                        j--;
                    }
                }
            }

            // Merge nodes
            for (int i = 1; i < 4; i++) {
                if (columnNodes[i] != null) {
                    if (columnNodes[i - 1] != null && columnNodes[i - 1].value == columnNodes[i].value) {
                        columnNodes[i - 1].value *= 2;
                        columnNodes[i] = null;
                        score.setText(Integer.toString(Integer.parseInt(score.getText()) + columnNodes[i - 1].value));
                    }
                }
            }

            // Shift nodes to fill empty spaces
            for (int i = 1; i < 4; i++) {
                if (columnNodes[i] != null) {
                    int j = i;
                    while (j > 0 && columnNodes[j - 1] == null) {
                        columnNodes[j - 1] = columnNodes[j];
                        columnNodes[j] = null;
                        columnNodes[j - 1].row = j - 1;
                        j--;
                    }
                }
            }

            //change the linked list
            Node dummy = new Node(0, -1, -1);
            dummy.next = first;
            Node prev = dummy;
            Node current = first;
            int k = 0;
            while (current != null) {
                if (current.col == col) {
                    if (k < columnNodes.length && columnNodes[k] == null) {
                        prev.next = current.next;
                    } else if (k < columnNodes.length) {
                        current.value = columnNodes[k].value;
                        current.row = columnNodes[k].row;
                        current.col = columnNodes[k].col;
                        prev = current;
                    }
                    k++;
                } else {
                    prev = current;
                }
                current = current.next;
            }
            first = dummy.next;
        }
        // Check if the move made any changes to the board
        if (successfulMove()) {
            addNewTale();
            setBoard();
            saveState();
            hasWon();

        }
    }

    public void moveDown() {
        // Put each column inside an array
        for (int col = 0; col < 4; col++) {
            Node[] columnNodes = new Node[4];

            // Extract nodes in the current column
            Node p = first;
            while (p != null) {
                if (p.col == col) {
                    columnNodes[p.row] = new Node(p.value, p.row, p.col);
                }
                p = p.next;
            }

            // Shift nodes to fill empty spaces
            for (int i = 2; i >= 0; i--) {
                if (columnNodes[i] != null) {
                    int j = i;
                    while (j < 3 && columnNodes[j + 1] == null) {
                        columnNodes[j + 1] = columnNodes[j];
                        columnNodes[j] = null;
                        columnNodes[j + 1].row = j + 1;
                        j++;
                    }
                }
            }

            // Merge nodes
            for (int i = 2; i >= 0; i--) {
                if (columnNodes[i] != null) {
                    if (columnNodes[i + 1] != null && columnNodes[i + 1].value == columnNodes[i].value) {
                        columnNodes[i + 1].value *= 2;
                        columnNodes[i] = null;
                        score.setText(Integer.toString(Integer.parseInt(score.getText()) + columnNodes[i + 1].value));
                    }
                }
            }

            // Shift nodes to fill empty spaces again after merging
            for (int i = 2; i >= 0; i--) {
                if (columnNodes[i] != null) {
                    int j = i;
                    while (j < 3 && columnNodes[j + 1] == null) {
                        columnNodes[j + 1] = columnNodes[j];
                        columnNodes[j] = null;
                        columnNodes[j + 1].row = j + 1;
                        j++;
                    }
                }
            }

            // Update the linked list to reflect the changes in columnNodes
            Node dummy = new Node(0, -1, -1);
            dummy.next = first;
            Node prev = dummy;
            Node current = first;
            int k = 3; // Start from the bottom
            while (current != null) {
                if (current.col == col) {
                    if (k >= 0 && columnNodes[k] == null) {
                        prev.next = current.next;
                    } else if (k >= 0) {
                        current.value = columnNodes[k].value;
                        current.row = columnNodes[k].row;
                        current.col = columnNodes[k].col;
                        prev = current;
                    }
                    k--;
                } else {
                    prev = current;
                }
                current = current.next;
            }
            first = dummy.next;
        }
        // Check if the move made any changes to the board
        if (successfulMove()) {
            addNewTale();
            setBoard();
            saveState();
            hasWon();

        }
    }


    public void moveLeft() {
        // Put each row inside an array
        for (int row = 0; row < 4; row++) {
            Node[] rowNodes = new Node[4];

            // Extract nodes in the current row
            Node p = first;
            while (p != null) {
                if (p.row == row) {
                    rowNodes[p.col] = new Node(p.value, p.row, p.col);
                }
                p = p.next;
            }

            // Shift nodes to fill empty spaces
            for (int i = 1; i < 4; i++) {
                if (rowNodes[i] != null) {
                    int j = i;
                    while (j > 0 && rowNodes[j - 1] == null) {
                        rowNodes[j - 1] = rowNodes[j];
                        rowNodes[j] = null;
                        rowNodes[j - 1].col = j - 1;
                        j--;
                    }
                }
            }

            // Merge nodes
            for (int i = 1; i < 4; i++) {
                if (rowNodes[i] != null) {
                    if (rowNodes[i - 1] != null && rowNodes[i - 1].value == rowNodes[i].value) {
                        rowNodes[i - 1].value *= 2;
                        rowNodes[i] = null;
                        score.setText(Integer.toString(Integer.parseInt(score.getText()) + rowNodes[i - 1].value));
                    }
                }
            }

            // Shift nodes to fill empty spaces again after merging
            for (int i = 1; i < 4; i++) {
                if (rowNodes[i] != null) {
                    int j = i;
                    while (j > 0 && rowNodes[j - 1] == null) {
                        rowNodes[j - 1] = rowNodes[j];
                        rowNodes[j] = null;
                        rowNodes[j - 1].col = j - 1;
                        j--;
                    }
                }
            }

            // Update the linked list to reflect the changes in rowNodes
            Node dummy = new Node(0, -1, -1);
            dummy.next = first;
            Node prev = dummy;
            Node current = first;
            int k = 0; // Start from the left
            while (current != null) {
                if (current.row == row) {
                    if (k < rowNodes.length && rowNodes[k] == null) {
                        prev.next = current.next;
                    } else if (k < rowNodes.length) {
                        current.value = rowNodes[k].value;
                        current.row = rowNodes[k].row;
                        current.col = rowNodes[k].col;
                        prev = current;
                    }
                    k++;
                } else {
                    prev = current;
                }
                current = current.next;
            }
            first = dummy.next;
        }
        // Check if the move made any changes to the board
        if (successfulMove()) {
            addNewTale();
            setBoard();
            saveState();
            hasWon();

        }
    }


    public void moveRight() {
        // Put each row inside an array
        for (int row = 0; row < 4; row++) {
            Node[] rowNodes = new Node[4];

            // Extract nodes in the current row
            Node p = first;
            while (p != null) {
                if (p.row == row) {
                    rowNodes[p.col] = new Node(p.value, p.row, p.col);
                }
                p = p.next;
            }

            // Shift nodes to fill empty spaces
            for (int i = 2; i >= 0; i--) {
                if (rowNodes[i] != null) {
                    int j = i;
                    while (j < 3 && rowNodes[j + 1] == null) {
                        rowNodes[j + 1] = rowNodes[j];
                        rowNodes[j] = null;
                        rowNodes[j + 1].col = j + 1;
                        j++;
                    }
                }
            }

            // Merge nodes
            for (int i = 2; i >= 0; i--) {
                if (rowNodes[i] != null) {
                    if (rowNodes[i + 1] != null && rowNodes[i + 1].value == rowNodes[i].value) {
                        rowNodes[i + 1].value *= 2;
                        rowNodes[i] = null;
                        score.setText(Integer.toString(Integer.parseInt(score.getText()) + rowNodes[i + 1].value));
                    }
                }
            }

            // Shift nodes to fill empty spaces again after merging
            for (int i = 2; i >= 0; i--) {
                if (rowNodes[i] != null) {
                    int j = i;
                    while (j < 3 && rowNodes[j + 1] == null) {
                        rowNodes[j + 1] = rowNodes[j];
                        rowNodes[j] = null;
                        rowNodes[j + 1].col = j + 1;
                        j++;
                    }
                }
            }

            // Update the linked list to reflect the changes in rowNodes
            Node dummy = new Node(0, -1, -1);
            dummy.next = first;
            Node prev = dummy;
            Node current = first;
            int k = 3; // Start from the right
            while (current != null) {
                if (current.row == row) {
                    if (k >= 0 && rowNodes[k] == null) {
                        prev.next = current.next;
                    } else if (k >= 0) {
                        current.value = rowNodes[k].value;
                        current.row = rowNodes[k].row;
                        current.col = rowNodes[k].col;
                        prev = current;
                    }
                    k--;
                } else {
                    prev = current;
                }
                current = current.next;
            }
            first = dummy.next;
        }
        // Check if the move made any changes to the board
        if (successfulMove()) {
            addNewTale();
            setBoard();
            saveState();
            hasWon();

        }
    }

    public void hasWon() {
        if(Integer.parseInt(score.getText()) == 2048){
            showMessage("YOU HAVE WON!");
        }
    }

    public void isGameOver() {
        // Check if any tile is empty
        for (int i = 0; i < 16; i++) {
            if (stackNode[top][i].value == 0) {
                return;
            }
        }

        // Check if any adjacent tiles can be merged
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                int currentValue = Integer.parseInt(labels[i][j].getText());
                if (currentValue == 0 || labels[i][j].getText() == "") continue;

                if (i - 1 >= 0 && currentValue == Integer.parseInt(labels[i - 1][j].getText())) {
                    return;
                }

                if (i + 1 <= 3 && currentValue == Integer.parseInt(labels[i + 1][j].getText())) {
                    return;
                }

                if (j - 1 >= 0 && currentValue == Integer.parseInt(labels[i][j - 1].getText())) {
                    return;
                }

                if (j + 1 <= 3 && currentValue == Integer.parseInt(labels[i][j + 1].getText())) {
                    return;
                }
            }
        }

        // If no empty tiles and no merges possible, game is over
        showMessage("GAME OVER!");
    }

}