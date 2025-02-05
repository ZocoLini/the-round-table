package org.lebastudios.theroundtable.ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.CacheHint;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.util.Callback;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.lebastudios.theroundtable.database.Database;
import org.lebastudios.theroundtable.logs.Logs;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

public class MultipleItemsListView<T> extends VBox
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

    @Setter private Consumer<T> onItemSelected;

    @Setter private ItemsGenerator<T> itemsGenerator;
    @Setter private Function<MultipleItemsListView<T>, ICellRecicler<T>> cellReciclerGenerator;
    @Setter @Getter private int groupSize;

    /**
     * Main constructor of the class. It receives the list cell node recicler, the content generator and the group
     * size.
     *
     * @param cellReciclerGenerator The node recicler defines how to reuse the graphic nodes of the list cells to
     *         avoid loading new ones
     * @param itemsGenerator The content generator defines how to generate the content of the list view
     * @param groupSize The max size of the pages
     */
    public MultipleItemsListView(Function<MultipleItemsListView<T>, ICellRecicler<T>> cellReciclerGenerator, ItemsGenerator<T> itemsGenerator,
            int groupSize)
    {
        this.cellReciclerGenerator = cellReciclerGenerator;
        this.itemsGenerator = itemsGenerator;
        this.groupSize = groupSize;

        this.listView = new ListView<>();
        listView.setCache(true);
        listView.setCacheHint(CacheHint.SPEED);

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

        listView.getSelectionModel().selectedItemProperty().addListener((_, oldValue, newValue) ->
        {
            if (onItemSelected == null || newValue == null || newValue == oldValue) return;

            onItemSelected.accept(newValue);
        });

        listView.setCellFactory(new Callback<>()
        {
            @Override
            public ListCell<T> call(ListView<T> tListView)
            {
                return new ListCell<>()
                {
                    private final ICellRecicler<T> cellRecicler;
                    
                    {
                        if (MultipleItemsListView.this.cellReciclerGenerator == null)
                        {
                            Logs.getInstance().log(Logs.LogType.WARNING, "multipleItemsListView has no " +
                                    "cellViewGenerator");
                            cellRecicler = null;
                        }
                        else
                        {
                            cellRecicler = MultipleItemsListView.this.cellReciclerGenerator.apply(MultipleItemsListView.this);
                        }
                        
                        this.setCache(true);
                        this.setCacheHint(CacheHint.SPEED);
                        this.setCacheShape(true);
                    }
                    
                    @Override
                    protected void updateItem(T item, boolean empty)
                    {
                        super.updateItem(item, empty);

                        if (empty || item == null || cellRecicler == null)
                        {
                            setGraphic(null);
                            setText(null);
                            return;
                        }

                        cellRecicler.update(item);
                        
                        setText(cellRecicler.getText());
                        setGraphic(cellRecicler.getGraphic());
                    }
                };
            }
        });

        refresh();
    }

    public MultipleItemsListView(Function<MultipleItemsListView<T>, ICellRecicler<T>> cellReciclerGenerator, ItemsGenerator<T> itemsGenerator)
    {
        this(cellReciclerGenerator, itemsGenerator, 500);
    }

    public MultipleItemsListView(ItemsGenerator<T> itemsGenerator)
    {
        this(null, itemsGenerator);
    }

    public MultipleItemsListView()
    {
        this(new ItemsGenerator<>()
        {
            @Override
            public List<T> generateItems(int from, int to)
            {
                return List.of();
            }

            @Override
            public long count()
            {
                return 0;
            }
        });
    }

    /**
     * Refresh the content of the list view
     */
    public void refresh()
    {
        refreshListView();
    }

    private void refreshListView()
    {
        this.qty = itemsGenerator.count();
        this.maxGroup = (int) (qty / groupSize);
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
        int to = (int) Math.min(from + groupSize, qty);

        listView.getItems().clear();
        listView.getItems().addAll(itemsGenerator.generateItems(from, to));
        
        final var toValue = Math.min(to, qty);
        final var fromValue = Math.min(from + 1, toValue);
        actualItemsLabel.setText(String.format("%d - %d", fromValue, toValue));
    }

    public interface ICellRecicler<T>
    {
        void update(@NonNull T item);

        Node getGraphic();

        String getText();
    }

    /**
     * Interface to generate the content of the list view.
     *
     * @param <T> the type of the content defined in the list view
     */
    public interface ItemsGenerator<T>
    {
        /**
         * Generate the content of the list view. The 'from' and the 'to' are inclusive. Ex.: For a groupSize of 500,
         * the group 0 with 500 items has to be generated with from = 1 and to = 500.
         *
         * @param from The first item to be generated
         * @param to The last item to be generated
         *
         * @return The list of items to be shown in the list view
         */
        List<T> generateItems(int from, int to);

        /**
         * This method has to return the total number of items that can be generated.
         */
        long count();
    }

    public static class HQLItemsGenerator<T, Q> implements ItemsGenerator<T>
    {
        private final String hql;
        private final Class<Q> clazz;
        private final Function<Q, T> mapper;

        public HQLItemsGenerator(String hql, Class<Q> clazz, Function<Q, T> mapper)
        {
            this.hql = hql;
            this.clazz = clazz;
            this.mapper = mapper;
        }

        @Override
        public List<T> generateItems(int from, int to)
        {
            return Database.getInstance().connectQuery(session ->
            {
                return session.createQuery(hql, clazz)
                        .setFirstResult(from)
                        .setMaxResults(to)
                        .list().stream()
                        .map(mapper)
                        .toList();
            });
        }

        @Override
        public long count()
        {
            return Database.getInstance().connectQuery(session ->
            {
                return session.createQuery("select count(*) " + hql, Long.class).uniqueResult();
            });
        }
    }
}
