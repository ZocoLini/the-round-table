package org.lebastudios.theroundtable.ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import lombok.Getter;
import lombok.Setter;
import org.lebastudios.theroundtable.database.Database;

import java.util.List;
import java.util.function.Function;

public class MultipleItemsListView<T, Q> extends VBox
{
    @Getter private final ListView<T> listView;
    private final Label actualItemsLabel;
    private final IconButton doubleLeft;
    private final IconButton left;
    private final IconButton right;
    private final IconButton doubleRight;

    private int actualGroup = 0;
    private int maxGroup;
    private Long qty;

    private String hql;
    private Class<Q> itemClass;
    private Function<Q, T> mapper;

    @Setter private int groupSize = 500;

    public MultipleItemsListView()
    {
        this.listView = new ListView<>();

        VBox.setVgrow(listView, Priority.ALWAYS);
        
        this.doubleLeft = new IconButton("double-left.png");
        doubleLeft.setOnAction(_ -> showContent(0));
        this.left = new IconButton("left.png");
        left.setOnAction(_ -> showContent(actualGroup - 1));
        this.right = new IconButton("right.png");
        right.setOnAction(_ -> showContent(actualGroup + 1));
        this.doubleRight = new IconButton("double-right.png");
        doubleRight.setOnAction(_ -> showContent(maxGroup));

        this.doubleLeft.setIconSize(26);
        this.left.setIconSize(26);
        this.right.setIconSize(26);
        this.doubleRight.setIconSize(26);
        
        this.actualItemsLabel = new Label();
        
        actualItemsLabel.setAlignment(Pos.CENTER);
        actualItemsLabel.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(actualItemsLabel, Priority.ALWAYS);

        HBox footer = new HBox(5, doubleLeft, left, actualItemsLabel, right, doubleRight);
        footer.setPadding(new Insets(5));
        footer.setSpacing(5);
        footer.setAlignment(Pos.CENTER);

        this.getChildren().addAll(listView, footer);
    }

    /**
     * Set the query to get the items to show.
     *
     * @param hql The query should start with the from clause
     */
    public void setQuery(String hql, Class<Q> clazz, Function<Q, T> mapper)
    {
        this.hql = hql;
        this.itemClass = clazz;
        this.mapper = mapper;
    }

    public void refresh()
    {
        refreshListView();
    }

    private void refreshListView()
    {
        Database.getInstance().connectQuery(session ->
        {
            qty = session.createQuery("select count(*) " + hql, Long.class).uniqueResult();
            if (qty == null) qty = 0L;
            maxGroup = (int) (qty / groupSize);
        });

        showContent(0);
    }

    private void showContent(int group)
    {
        if (group < 0)
        {
            throw new IllegalArgumentException("Group cannot be negative");
        }

        actualGroup = group;

        if (actualGroup == 0)
        {
            doubleLeft.setDisable(true);
            left.setDisable(true);

            right.setDisable(maxGroup == 0);
            doubleRight.setDisable(maxGroup == 0);
        }
        else
        {
            if (actualGroup == maxGroup)
            {
                doubleLeft.setDisable(false);
                left.setDisable(false);
                right.setDisable(true);
                doubleRight.setDisable(true);
            }
            else
            {
                doubleLeft.setDisable(false);
                left.setDisable(false);
                right.setDisable(false);
                doubleRight.setDisable(false);
            }
        }

        int from = group * groupSize;
        int to = from + groupSize;

        Database.getInstance().connectQuery(session ->
        {
            List<T> items = session.createQuery(hql, itemClass)
                    .setFirstResult(from)
                    .setMaxResults(to)
                    .list().stream()
                    .map(mapper)
                    .toList();

            listView.getItems().clear();
            listView.getItems().setAll(items);
        });

        actualItemsLabel.setText(String.format("%d - %d", from + 1, Math.min(to, qty)));
    }
}
