package org.polymap.rap.openlayers.control;

import org.polymap.core.runtime.config.Config2;

public class LoadingPanel extends Control {
    
    public enum WidgetType {
        PROGRESS_BAR("progressbar"), CIRCLE("animatedgif");
        
        private final String widgetName;
        
        WidgetType(String widgetName) {
            this.widgetName = widgetName;
        }
        
        public String getWidgetName() {
            return widgetName;
        }
    }
    


    public enum ProgressModeType {
        TILE("tile"), LAYER("layer");
        
        private final String progressModeName;
        
        ProgressModeType(String progressModeName) {
            this.progressModeName = progressModeName;
        }
        
        public String getProgressModeName() {
            return progressModeName;
        }
    }
    
    public Config2<LoadingPanel, String> widget;
    
    public Config2<LoadingPanel, String> progressMode;
    
    public Config2<LoadingPanel, Boolean> showPanel;
    
    /**
     * CSS class name
     */
    public Config2<LoadingPanel, String> className;
    
    public LoadingPanel(WidgetType widgetType, ProgressModeType progressModeType) {
        super("ol.control.LoadingPanel");
        widget.set( widgetType.getWidgetName() );
        progressMode.set( progressModeType.getProgressModeName() );
    }
    
    @Override
    protected void create() {
        if(!showPanel.isPresent()) {
            showPanel.set( true );
        }
        super.create();
    }
}
