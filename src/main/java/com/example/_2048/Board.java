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
    private Node[][] stackNode = new Node[100][16];
    private static int countUndo = 5;
    private static int top = -1;
    //private Node[][] undoBoardForRedo
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
            initializeLabels();
            addNewTale();
            addNewTale();
            saveState();
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
                labels[i][j].setStyle("-fx-text-fill: #ffa200;");
                labels[i][j].setStyle("-fx-font-size: 15px;");
                board.add(labels[i][j], j,i);
            }
        }
    }

    //saving the boards state
    public void saveState (){
        Node p = first;
        top++;
        for (int i = 0; p != null; i++) {
            Node q = new Node(p.value,p.row,p.col);
            stackNode[top][i] = q;
            p = p.next;
        }
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
        // Save the current state to compare later
        saveState();

        // Put each column inside an array
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
        }

        // Update the linked list to remove nodes with value 0
        Node dummy = new Node(0, -1, -1);
        dummy.next = first;
        Node prev = dummy;
        Node current = first;

        while (current != null) {
            if (current.value == 0) {
                prev.next = current.next;
            } else {
                prev = current;
            }
            current = current.next;
        }

        first = dummy.next;

        // Check if the move made any changes to the board
        if (successfulMove()) {
            addNewTale();
        }
        setBoard();
    }



//    public void moveUp() {
//        //putting each column inside an array
//        for (int col = 0; col < 4; col++) {
//            Node[] columnNodes = new Node[4];
//
//            // Extract nodes in the current column
//            Node p = first;
//            while (p != null) {
//                if (p.col == col) {
//                    columnNodes[p.row] = p;
//                }
//                p = p.next;
//            }
//
//            // merge
//            for (int i = 3; i > 0; i--) {
//                if (columnNodes[i] != null) {
//                    if (columnNodes[i-1] != null && columnNodes[i].value == columnNodes[i-1].value) {
//                        columnNodes[i-1].value *= 2;
//                        columnNodes[i].value = 0;
//                        score.setText(Integer.toString(columnNodes[i-1].value + Integer.parseInt(score.getText())));
//                    }
//                }
//            }
//            //moving up all the tiles in the column
//            for (int i = 3; i > 0; i--) {
//                if (columnNodes[i] != null && (columnNodes[i - 1] == null || columnNodes[i-1].value == 0)) {
//                    columnNodes[i - 1] = columnNodes[i];
//                    columnNodes[i - 1].row = i - 1;
//                    columnNodes[i].value = 0;
//                }
//            }
//        }
//        // Update the linked list to remove the elements with 0 value after merging and moving upp the tiles
//        Node q = first;
//        Node prev = null;
//
//        if (first != null && first.value == 0) {
//            first = first.next;
//        }
//        while (q != null) {
//            while (q.next != null && q.next.value == 0) {
//                q.next = q.next.next; // Skip the node with value 0
//            }
//            prev = q;
//            q = q.next;
//        }
//
//        //check if the move made any changes to the board
//        if(successfulMove()) {
//            addNewTale();
//        }
//        setBoard();
//    }


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

