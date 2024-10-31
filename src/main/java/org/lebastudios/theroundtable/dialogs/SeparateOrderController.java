package org.lebastudios.theroundtable.dialogs;

import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.stage.Modality;
import org.controlsfx.control.ListSelectionView;
import org.lebastudios.theroundtable.Launcher;
import org.lebastudios.theroundtable.controllers.StageController;
import org.lebastudios.theroundtable.database.entities.Order;
import org.lebastudios.theroundtable.database.entities.Product;
import org.lebastudios.theroundtable.locale.LangFileLoader;
import org.lebastudios.theroundtable.ui.StageBuilder;

import java.math.BigDecimal;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

public class SeparateOrderController extends StageController<SeparateOrderController>
{
    private final Order originalOrder;
    private final HashMap<Product, BigDecimal> originalProducts;
    private final BiConsumer<Order, Order> acceptSeparation;
    @FXML private ListSelectionView<Map.Entry<Product, BigDecimal>> selectionView;

    public SeparateOrderController(Order order, BiConsumer<Order, Order> acceptSeparation)
    {
        originalOrder = order;
        originalProducts = new HashMap<>(order.getProducts());
        this.acceptSeparation = acceptSeparation;
    }

    private static void moveProductQty(Product product, BigDecimal quantity,
            ListView<Map.Entry<Product, BigDecimal>> origin,
            ListView<Map.Entry<Product, BigDecimal>> output)
    {
        addProductToList(product, quantity.negate(), origin);
        addProductToList(product, quantity, output);
    }

    private static void addProductToList(Product product, BigDecimal quantity,
            ListView<Map.Entry<Product, BigDecimal>> listView)
    {
        var list = listView.getItems();
        var index = indexOfProduct(product, listView);

        if (index == -1)
        {
            list.add(Map.entry(product, quantity));
            return;
        }

        var entry = list.get(index);
        list.remove(index);

        var newEntry = Map.entry(product, entry.getValue().add(quantity));
        if (newEntry.getValue().compareTo(BigDecimal.ZERO) != 0)
        {
            list.add(index, newEntry);
        }
    }

    private static int indexOfProduct(Product product,
            ListView<Map.Entry<Product, BigDecimal>> listView)
    {
        var list = listView.getItems();
        for (var i = 0; i < list.size(); i++)
        {
            if (list.get(i).getKey().equals(product)) return i;
        }

        return -1;
    }

    @FXML @Override protected void initialize()
    {
        setCustomListCell();

        customAvailableButtons();

        updateListViews();
    }

    private void setCustomListCell()
    {
        selectionView.setCellFactory(param -> new ListCell<>()
        {
            @Override
            public void updateItem(Map.Entry<Product, BigDecimal> item, boolean empty)
            {
                super.updateItem(item, empty);

                if (item == null || empty)
                {
                    setText("");
                }
                else
                {
                    setText(item.getValue().intValueExact() + " - " + item.getKey().getName());
                }
            }
        });
    }

    private void customAvailableButtons()
    {
        selectionView.getActions().clear();

        selectionView.getActions().add(new MoveOneUnitToTarget());

        selectionView.getActions().add(new MoveAllUnitToTarget());

        selectionView.getActions().add(new MoveAllEntriesToTarget());

        selectionView.getActions().add(new MoveOneUnitToSource());

        selectionView.getActions().add(new MoveAllUnitToSource());

        selectionView.getActions().add(new MoveAllEntriesToSource());
    }

    private void updateListViews()
    {
        var list = new ArrayList<>(originalProducts.entrySet());
        selectionView.setSourceItems(FXCollections.observableArrayList(list));
    }

    @FXML
    private void accept()
    {
        var generatedOrder = new Order();
        generatedOrder.setOrderName(originalOrder.getOrderName() + " (Separated)");

        for (var variable : selectionView.getTargetItems())
        {
            generatedOrder.addProduct(variable.getKey(), variable.getValue(), false);
        }

        acceptSeparation.accept(originalOrder, generatedOrder);
        cancel();
    }

    @Override
    protected void customizeStageBuilder(StageBuilder stageBuilder)
    {
        stageBuilder.setModality(Modality.APPLICATION_MODAL);
    }

    @FXML
    private void cancel()
    {
        close();
    }

    @Override
    public Class<?> getBundleClass()
    {
        return Launcher.class;
    }

    @Override
    public URL getFXML()
    {
        return SeparateOrderController.class.getResource("separateOrder.fxml");
    }
    
    @Override
    public String getTitle()
    {
        return LangFileLoader.getTranslation("tiltle.separateorderdialog");
    }

    private static class MoveOneUnitToTarget
            extends ListSelectionView.ListSelectionAction<Map.Entry<Product, BigDecimal>>
    {
        public MoveOneUnitToTarget()
        {
            super(null, ">");
        }

        @Override
        public void initialize(ListView<Map.Entry<Product, BigDecimal>> sourceListView,
                ListView<Map.Entry<Product, BigDecimal>> targetListView)
        {
            disabledProperty().bind(Bindings.isEmpty(sourceListView.getSelectionModel().getSelectedItems()));
            setEventHandler(ae ->
            {
                var entry = sourceListView.getSelectionModel().getSelectedItem();
                if (entry == null) return;

                moveProductQty(entry.getKey(), BigDecimal.ONE, sourceListView, targetListView);
            });
        }
    }

    private static class MoveAllUnitToTarget
            extends ListSelectionView.ListSelectionAction<Map.Entry<Product, BigDecimal>>
    {
        public MoveAllUnitToTarget()
        {
            super(null, ">>");
        }

        @Override
        public void initialize(ListView<Map.Entry<Product, BigDecimal>> sourceListView,
                ListView<Map.Entry<Product, BigDecimal>> targetListView)
        {
            disabledProperty().bind(Bindings.isEmpty(sourceListView.getSelectionModel().getSelectedItems()));
            setEventHandler(ae ->
            {
                var entry = sourceListView.getSelectionModel().getSelectedItem();
                if (entry == null) return;

                moveProductQty(entry.getKey(), entry.getValue(), sourceListView, targetListView);
            });
        }
    }

    private static class MoveAllEntriesToTarget
            extends ListSelectionView.ListSelectionAction<Map.Entry<Product, BigDecimal>>
    {
        public MoveAllEntriesToTarget()
        {
            super(null, ">>>");
        }

        @Override
        public void initialize(ListView<Map.Entry<Product, BigDecimal>> sourceListView,
                ListView<Map.Entry<Product, BigDecimal>> targetListView)
        {
            disabledProperty().bind(Bindings.isEmpty(sourceListView.getItems()));
            setEventHandler(ae ->
            {
                var auxList = new ArrayList<>(sourceListView.getItems());

                auxList.forEach(entry ->
                        moveProductQty(entry.getKey(), entry.getValue(), sourceListView, targetListView)
                );
            });
        }
    }

    private static class MoveOneUnitToSource
            extends ListSelectionView.ListSelectionAction<Map.Entry<Product, BigDecimal>>
    {
        public MoveOneUnitToSource()
        {
            super(null, "<");
        }

        @Override
        public void initialize(ListView<Map.Entry<Product, BigDecimal>> sourceListView,
                ListView<Map.Entry<Product, BigDecimal>> targetListView)
        {
            disabledProperty().bind(Bindings.isEmpty(targetListView.getSelectionModel().getSelectedItems()));
            setEventHandler(ae ->
            {
                var entry = targetListView.getSelectionModel().getSelectedItem();
                if (entry == null) return;

                moveProductQty(entry.getKey(), BigDecimal.ONE, targetListView, sourceListView);
            });
        }
    }

    private static class MoveAllUnitToSource
            extends ListSelectionView.ListSelectionAction<Map.Entry<Product, BigDecimal>>
    {
        public MoveAllUnitToSource()
        {
            super(null, "<<");
        }

        @Override
        public void initialize(ListView<Map.Entry<Product, BigDecimal>> sourceListView,
                ListView<Map.Entry<Product, BigDecimal>> targetListView)
        {
            disabledProperty().bind(Bindings.isEmpty(targetListView.getSelectionModel().getSelectedItems()));
            setEventHandler(ae ->
            {
                var entry = targetListView.getSelectionModel().getSelectedItem();
                if (entry == null) return;

                moveProductQty(entry.getKey(), entry.getValue(), targetListView, sourceListView);
            });
        }
    }

    private static class MoveAllEntriesToSource
            extends ListSelectionView.ListSelectionAction<Map.Entry<Product, BigDecimal>>
    {
        public MoveAllEntriesToSource()
        {
            super(null, "<<<");
        }

        @Override
        public void initialize(ListView<Map.Entry<Product, BigDecimal>> sourceListView,
                ListView<Map.Entry<Product, BigDecimal>> targetListView)
        {
            disabledProperty().bind(Bindings.isEmpty(targetListView.getItems()));
            setEventHandler(ae ->
            {
                var auxList = new ArrayList<>(targetListView.getItems());

                auxList.forEach(entry ->
                        moveProductQty(entry.getKey(), entry.getValue(), targetListView, sourceListView)
                );
            });
        }
    }
}
