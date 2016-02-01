/*
 * polymap.org and individual contributors as indicated by the @authors tag.
 * Copyright (C) 2009-2015 
 * All rights reserved.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 */
package org.polymap.rap.openlayers.layer;

import org.polymap.core.runtime.config.Concern;
import org.polymap.core.runtime.config.Config2;
import org.polymap.rap.openlayers.base.OlPropertyConcern;
import org.polymap.rap.openlayers.source.TileSource;

/**
 * For layer sources that provide pre-rendered, tiled images in grids that are
 * organized by zoom levels for specific resolutions.
 * 
 * @see <a href="http://openlayers.org/en/master/apidoc/ol.layer.Tile.html">
 *      OpenLayers Doc</a>
 * @author <a href="http://www.polymap.de">Falko Bräutigam</a>
 * @author <a href="http://stundzig.it">Steffen Stundzig</a>
 */
public class TileLayer
        extends Layer<TileSource> {
    
    public enum TileEvent implements ILayerEvent {
        LOAD_START("tileloadstart"), LOAD_END("tileloadend"), LOAD_ERROR("tileloaderror");
        
        private final String eventName;
        
        TileEvent(String eventName) {
            this.eventName = eventName;
        }
        
        public String getEventName() {
            return eventName;
        }
    }    

    /**
     * Preload. Load low-resolution tiles up to preload levels. By default preload is
     * 0, which means no preloading.
     */
    @Concern(OlPropertyConcern.class)
    public Config2<TileLayer,Float>     preload;


    public TileLayer() {
        super( "ol.layer.Tile" );
    }   
}
