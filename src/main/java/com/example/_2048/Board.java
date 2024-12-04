package com.example._2048;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.fxml.Initializable;
import java.net.URL;
import java.util.Random;
import java.util.ResourceBundle;

public class Board implements Initializable{
    private Node first ;
    private Label[][] labels = new Label[4][4];
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


    public Board() {
    }
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            initializeLabels();
            addNewTile();
            addNewTile();
            board.setOnKeyPressed(event -> {
                switch (event.getCode()) {
                    case UP:
                        moveUp();
                        break;
                    case DOWN:
                        moveDown();
                        break;
                    case LEFT:
                        moveLeft();
                        break;
                    case RIGHT:
                        moveRight();
                        break;
                }
            });
            board.setFocusTraversable(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void initializeLabels() {
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                labels[i][j] = new Label("");
                //chera kar nemikone
                labels[i][j].setStyle("-fx-alignment: center;");
                labels[i][j].setStyle("-fx-text-fill: blue;");
                labels[i][j].setStyle("-fx-font-size: 12px;");
                board.add(labels[i][j], j, i);
            }
        }
    }

    public void addNewTile() {
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
        setBoard();

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



    // Implement the move functions, merge logic, and other necessary methods
    public void moveUp() {
        //putting each column inside an array
        for (int col = 0; col < 4; col++) {
            Node[] columnNodes = new Node[4];

            // Extract nodes in the current column
            Node p = first;
            while (p != null) {
                if (p.col == col) {
                    columnNodes[p.row] = p;
                }
                p = p.next;
            }

            // merge
            for (int i = 3; i > 0; i--) {
                if (columnNodes[i] != null) {
                    if (columnNodes[i].value == columnNodes[i-1].value) {
                        columnNodes[i-1].value *= 2;
                        columnNodes[i] = null;
                        score.setText(Integer.toString(columnNodes[i-1].value));
                    }
                }
            }
            //moving up all the tiles in the column
            for (int i = 3; i > 0; i--) {
                if (columnNodes[i] != null && columnNodes[i - 1] == null) {
                    columnNodes[i - 1] = columnNodes[i];
                    columnNodes[i - 1].row = i - 1;

                }
            }

            // Update the linked list to remove the elements with null value
            Node prev = null;
            for (int i = 0; i < 4; i++) {
                if (columnNodes[i] != null && columnNodes[i].value == 0) {
                    // Remove the node from the linked list
                    if (prev != null) {
                        prev.next = columnNodes[i].next;
                    } else {
                        first = columnNodes[i].next;
                    }
                } else {
                    prev = columnNodes[i];
                }
            }
        }
        addNewTile();
        setBoard();
    }


    public void moveDown() {
        // Implementation of move down functionality
    }

    public void moveLeft() {
        // Implementation of move left functionality
    }

    public void moveRight() {
        // Implementation of move right functionality
    }

    public boolean hasWon() {
        // Implementation of winning condition
        return false;
    }

    public boolean isGameOver() {
        // Implementation of game over condition
        return false;
    }

    public void undo() {
        // Implementation of undo functionality
    }

    public void redo() {
        // Implementation of redo functionality
    }
}

