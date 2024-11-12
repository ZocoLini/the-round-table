package org.lebastudios.theroundtable.ui;

import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import lombok.Getter;
import lombok.Setter;

import java.util.function.Consumer;

public class SearchBox extends HBox
{
    private final TextField searchField;
    private final IconButton searchButton;
    
    @Setter @Getter private Consumer<String> onSearch = s -> {System.out.println(1243);};
    
    public SearchBox()
    {
        super();
        
        searchField = new TextField();
        searchField.setPromptText("Search...");
        
        this.getStyleClass().addAll(searchField.getStyleClass());
        searchField.getStyleClass().clear();
        
        searchButton = new IconButton();
        searchButton.setIconName("find.png");
        searchButton.setIconSize(16);
        
        searchButton.setOnAction(e -> onSearch.accept(searchField.getText()));
        
        this.setSpacing(5);
        getChildren().addAll(searchField, searchButton);
        
        this.addEventHandler(KeyEvent.KEY_PRESSED, e -> {
            if (e.getCode() == KeyCode.ENTER)
            {
                onSearch.accept(searchField.getText());
                e.consume();
            }
        });
    }
}
