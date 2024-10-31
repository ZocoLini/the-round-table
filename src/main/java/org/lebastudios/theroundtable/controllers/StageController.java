package org.lebastudios.theroundtable.controllers;

import org.lebastudios.theroundtable.ui.SceneBuilder;
import org.lebastudios.theroundtable.ui.StageBuilder;

import java.util.function.Consumer;

public abstract class StageController<T extends Controller<T>> extends Controller<T>
{
    public void instantiate(Consumer<T> acceptController, boolean shouldWait)
    {
        StageBuilder stageBuilder = getDefaultStageBuilder();
        
        acceptController.accept(getController());
        customizeStageBuilder(stageBuilder);
        
        if (shouldWait) 
        {
            stageBuilder.build().showAndWait();
        }
        else
        {
            stageBuilder.build().show();
        }
    }
    
    public final void instantiate(boolean shouldWait)
    {
        instantiate(_ -> {}, shouldWait);
    }
    
    public final void instantiate()
    {
        instantiate(_ -> {}, false);
    }
    
    private StageBuilder getDefaultStageBuilder()
    {
        return new StageBuilder(getDefaultSceneBuilder().build())
                .setTitle(getTitle());
    }
    
    private SceneBuilder getDefaultSceneBuilder()
    {
        var sceneBuilder = new SceneBuilder(getParent());
        
        customizeSceneBuilder(sceneBuilder);
        
        return sceneBuilder;
    }
    
    protected void customizeSceneBuilder(SceneBuilder sceneBuilder) {}
    protected void customizeStageBuilder(StageBuilder stageBuilder) {}
    
    public abstract String getTitle();
    
    protected final void close()
    {
        getStage().close();
    }
}
