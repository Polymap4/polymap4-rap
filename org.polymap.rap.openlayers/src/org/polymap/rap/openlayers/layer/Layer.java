/*
 * polymap.org Copyright (C) 2009-2014, Polymap GmbH. All rights reserved.
 * 
 * This is free software; you can redistribute it and/or modify it under the terms of
 * the GNU Lesser General Public License as published by the Free Software
 * Foundation; either version 3 of the License, or (at your option) any later
 * version.
 * 
 * This software is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 */
package org.polymap.rap.openlayers.layer;

import org.polymap.core.runtime.config.Concern;
import org.polymap.core.runtime.config.Config2;
import org.polymap.core.runtime.config.Immutable;
import org.polymap.core.runtime.config.Mandatory;

import org.polymap.rap.openlayers.base.OlMap;
import org.polymap.rap.openlayers.base.OlPropertyConcern;
import org.polymap.rap.openlayers.source.Source;

/**
 * Abstract base class; normally only used for creating subclasses and not
 * instantiated in apps. A visual representation of raster or vector map data. Layers
 * group together those properties that pertain to how the data is to be displayed,
 * irrespective of the source of that data.
 * 
 * Layers are usually added to a map with ol.Map#addLayer. Components like
 * ol.interaction.Select use unmanaged layers internally. These unmanaged layers are
 * associated with the map using ol.layer.Layer#setMap instead.
 * 
 * A generic change event is fired when the state of the source changes.
 * 
 * @see <a href="http://openlayers.org/en/master/apidoc/ol.layer.Layer.html">
 *      OpenLayers Doc</a>
 * @author <a href="http://www.polymap.de">Falko Br�utigam</a>
 * @author <a href="http://stundzig.it">Steffen Stundzig</a>
 */
public abstract class Layer<S extends Source>
        extends Base {

    @Immutable
    @Mandatory
    @Concern( OlPropertyConcern.class )
    public Config2<Layer<S>,S> source;


    protected Layer( String jsClassname ) {
        super( jsClassname );
    }


    /**
     * Refresh this layer by re-fetching data from the server.
     * 
     * @deprecated This adds a param ("t") with current time to the request params;
     *             this works only for WMS. Introduces cache problems as the t param is not
     *             saved between sessions. Better implement proper ETag/Last-modified.
     */
    public void refresh() {
        // adding the param t to the URL (of the image) causes the browser
        // to re-fetch it, bypassing all local and intermediate caches
        call( "var source = this.obj.getSource();" +
                "var params = source.getParams();" +
                "params.t = new Date().getMilliseconds();" +
                "source.updateParams(params);" );
    }


    /**
     * only for internal use, to give the layer the opportunity to register
     * additional stuff on the map
     *
     * @param map the current map
     */
    public void onSetMap( OlMap map ) {
        source.get().onSetMap( map );
    }


    /**
     * only for internal use, to give the layer the opportunity to unregister
     * additional stuff on the map
     *
     * @param map the current map
     */
    public void onUnsetMap( OlMap map ) {
        source.get().onUnsetMap( map );
    }
}
