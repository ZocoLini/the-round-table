package org.lebastudios.theroundtable.ui;

import com.sun.javafx.collections.ObservableListWrapper;
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
import lombok.Setter;
import org.lebastudios.theroundtable.database.Database;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

public class MultipleItemsListView<T> extends VBox
{
    private final Map<Node, IGrapahicView> graphicViewsMap = new HashMap<>();
    
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

    private ContentGenerator<T> contentGenerator;
    @Setter private ListCellNodeRecicler<T> listCellNodeRecicler;
    @Getter private int groupSize;

    /**
     * Main constructor of the class. It receives the list cell node recicler, the content generator and the group size.
     * @param listCellNodeRecicler The node recicler defines how to reuse the graphic nodes of the list cells to
     * avoid loading new ones
     * @param contentGenerator The content generator defines how to generate the content of the list view
     * @param groupSize The max size of the pages
     */
    public MultipleItemsListView(ListCellNodeRecicler<T> listCellNodeRecicler, ContentGenerator<T> contentGenerator,
            int groupSize)
    {
        this.listCellNodeRecicler = listCellNodeRecicler;
        this.contentGenerator = contentGenerator;
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

        listView.setCellFactory(new Callback<>()
        {
            @Override
            public ListCell<T> call(ListView<T> tListView)
            {
                return new ListCell<>()
                {
                    {
                        this.setOnMouseClicked(e ->
                        {
                            if (onItemSelected != null && !e.isConsumed())
                            {
                                e.consume();
                                onItemSelected.accept(getItem());
                            }
                        });
                    }

                    @Override
                    protected void updateItem(T item, boolean empty)
                    {
                        super.updateItem(item, empty);

                        if (empty || item == null)
                        {
                            setGraphic(null);
                            setText(null);
                            return;
                        }

                        IGrapahicView previousGraphic = null;
                        
                        if (getGraphic() != null)
                        {
                            previousGraphic = graphicViewsMap.get(getGraphic());
                        }

                        ListCellContent content = MultipleItemsListView.this.listCellNodeRecicler.updateView(previousGraphic, item);
                        
                        setText(content.text);

                        if (content.graphicView == null || content.graphicView.getRoot() == null)
                        {
                            setGraphic(null);
                            return;
                        }
                        
                        if (previousGraphic == null) 
                        {
                            graphicViewsMap.put(content.graphicView.getRoot(), content.graphicView);
                        }

                        setGraphic(content.graphicView().getRoot());
                    }
                };
            }
        });

        refresh();
    }

    public MultipleItemsListView(ListCellNodeRecicler<T> listCellNodeRecicler, ContentGenerator<T> contentGenerator)
    {
        this(listCellNodeRecicler, contentGenerator, 500);
    }

    public MultipleItemsListView(ContentGenerator<T> contentGenerator)
    {
        this(new ListCellNodeRecicler<>() {}, contentGenerator);
    }

    public MultipleItemsListView()
    {
        this(new ContentGenerator<>()
        {
            @Override
            public List<T> generateContent(int from, int to)
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
     * Set the content generator and calls the refresh method automatically
     * @param contentGenerator the content generator
     */
    public void setContentGenerator(ContentGenerator<T> contentGenerator)
    {
        this.contentGenerator = contentGenerator;
        refresh();
    }

    /**
     * Set the pagination size and calls the refresh method automatically
     * @param groupSize the max size of the pages
     */
    public void setGroupSize(int groupSize)
    {
        this.groupSize = groupSize;
        refresh();
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
        this.qty = contentGenerator.count();
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

        listView.setItems(new ObservableListWrapper<>(contentGenerator.generateContent(from, to)));

        final var toValue = Math.min(to, qty);
        final var fromValue = Math.min(from + 1, toValue);
        actualItemsLabel.setText(String.format("%d - %d", fromValue, toValue));
    }

    /**
     * Interface to generate the content of the list view.
     * @param <T> the type of the content defined in the list view
     */
    public interface ContentGenerator<T>
    {
        /**
         * Generate the content of the list view. The 'from' and the 'to' are inclusive. 
         * Ex.: For a groupSize of 500, the group 0 with 500 items has to be generated with from = 1 and to = 500.
         * @param from The first item to be generated
         * @param to The last item to be generated
         * @return The list of items to be shown in the list view
         */
        List<T> generateContent(int from, int to);

        /**
         * This method has to return the total number of items that can be generated.
         */
        long count();
    }

    public interface ListCellNodeRecicler<T>
    {
        /**
         * This method is called in every update of the list view.
         *
         * @param oldGraphic The old graphic interface that was shown in the list view. It can be null if the cell was empty.
         * @param item The item that has to be shown in the list cell
         *
         * @return The content of the list cell. It contains the grpahic node and the text to be shown in the cell.
         */
        default ListCellContent updateView(IGrapahicView oldGraphic, T item) { return new ListCellContent(oldGraphic, item.toString()); }
    }

    public record ListCellContent(IGrapahicView graphicView, String text){}

    public interface IGrapahicView
    { Node getRoot(); }
    
    public static class HQLContentGenerator<T, Q> implements ContentGenerator<T>
    {
        private final String hql;
        private final Class<Q> clazz;
        private final Function<Q, T> mapper;

        public HQLContentGenerator(String hql, Class<Q> clazz, Function<Q, T> mapper)
        {
            this.hql = hql;
            this.clazz = clazz;
            this.mapper = mapper;
        }

        @Override
        public List<T> generateContent(int from, int to)
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
