module com.boomexs.binarytree {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.boomexs.binarytree to javafx.fxml;
    exports com.boomexs.binarytree;
}