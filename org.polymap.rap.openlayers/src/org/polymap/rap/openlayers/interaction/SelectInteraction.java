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
package org.polymap.rap.openlayers.interaction;

import java.util.Arrays;
import java.util.Collection;

import org.polymap.core.runtime.config.Concern;
import org.polymap.core.runtime.config.Config2;
import org.polymap.core.runtime.config.Immutable;
import org.polymap.rap.openlayers.base.OlEventListener;
import org.polymap.rap.openlayers.base.OlEventListener.PayLoad;
import org.polymap.rap.openlayers.base.OlPropertyConcern;
import org.polymap.rap.openlayers.layer.VectorLayer;

/**
 * Interaction for selecting vector features. By default, selected features are
 * styled differently, so this interaction can be used for visual highlighting, as
 * well as selecting features for other actions, such as modification or output.
 * There are three ways of controlling which features are selected: using the browser
 * event as defined by the condition and optionally the toggle, add/remove, and multi
 * options; a layers filter; and a further feature filter using the filter option.
 * 
 * Selected features are added to an internal unmanaged layer.
 * 
 * 
 * 
 * @see <a href="http://openlayers.org/en/master/apidoc/ol.interaction.Select.html">
 *      OpenLayers Doc</a>
 * @author <a href="http://stundzig.it">Steffen Stundzig</a>
 */
public class SelectInteraction
        extends Interaction {

    public enum Event {
        select("select");

        private String value;


        private Event( String value ) {
            this.value = value;
        }


        public String getValue() {
            return this.value;
        }
    }
//
//
//    public enum Type implements Jsonable {
//        Point, LineString, Polygon, Circle;
//
//        @Override
//        public Object toJson() {
//            return "/** @type {ol.geom.GeometryType} */ '" + name() + "'";
//        }
//    }

    // TODO more properties

    @Immutable
    @Concern(OlPropertyConcern.class)
    public Config2<SelectInteraction, Collection<VectorLayer>> layers;
//
//    @Immutable
//    @Concern(OlPropertyConcern.class)
//    public Config<Type>         type;


    public SelectInteraction( VectorLayer... layers) {
        super( "ol.interaction.Select" );
        this.layers.set( Arrays.asList( layers ) );
    }


    public void addEventListener( Event event, OlEventListener listener ) {
        PayLoad payload = new PayLoad();
        payload.add( "selected", "theEvent.selected != null ? theEvent.selected.map( function(feature) {return feature.get(\"id\");}) : {}" );
        addEventListener( event.getValue(), listener, payload );
    }


    public void removeEventListener( Event event, OlEventListener listener ) {
        removeEventListener( event.getValue(), listener );
    }
}
