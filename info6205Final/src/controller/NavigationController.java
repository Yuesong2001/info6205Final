package controller;

import javafx.scene.Scene;

import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import adt.MyStack;
import adt.impl.ArrayStack;

public class NavigationController {
    private Scene scene;
    private Pane currentPane;
    private ArrayStack<Pane> paneStack;

    public NavigationController() {
        this.paneStack = new ArrayStack<>();
    }

    // 把 scene 的设置改为单独的方法
    public void setScene(Scene scene) {
        this.scene = scene;
        this.currentPane = (Pane) scene.getRoot();
    }

    public void pushPane(Pane newPane) {
        if (currentPane != null) {
            paneStack.push(currentPane);
        }
        currentPane = newPane;
        scene.setRoot(newPane);
    }

    public void popPane() {
        if (!paneStack.isEmpty()) {
            currentPane = paneStack.pop();
            scene.setRoot(currentPane);
        } else {
            System.out.println("No more panes in stack.");
        }
    }
}


