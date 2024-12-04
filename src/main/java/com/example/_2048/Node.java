package com.example._2048;

class Node {
    int value;
    int row;
    int col;
    Node next;

    public Node(int value, int row, int col) {
        this.value = value;
        this.row = row;
        this.col = col;
        this.next = null;
    }

}
