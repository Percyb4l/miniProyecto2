module com.example.sudoku { // Use el nombre de su módulo si es diferente
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics; // Inclúyalo por si acaso

    // EXPORTAR el paquete de la clase principal (el lanzador)
    exports com.example.sudoku;

    // ABRIR los paquetes de controladores para que el FXML Loader pueda instanciarlos
    // El loader.getController() necesita esta línea:
    opens com.example.sudoku.controller to javafx.fxml;

    // Abrir el paquete de vistas si las clases Stage están allí
    opens com.example.sudoku.view to javafx.fxml;

}