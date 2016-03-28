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

import org.polymap.core.runtime.config.Concern;
import org.polymap.core.runtime.config.Immutable;
import org.polymap.core.runtime.config.Config;
import org.polymap.rap.openlayers.base.Jsonable;
import org.polymap.rap.openlayers.base.OlEventListener;
import org.polymap.rap.openlayers.base.OlEventListener.PayLoad;
import org.polymap.rap.openlayers.base.OlPropertyConcern;
import org.polymap.rap.openlayers.source.VectorSource;

/**
 * Interaction that allows drawing geometries.
 * 
 * @see <a href="http://openlayers.org/en/master/apidoc/ol.interaction.Draw.html">
 *      OpenLayers Doc</a>
 * @author <a href="http://stundzig.it">Steffen Stundzig</a>
 */
public class DrawInteraction
        extends Interaction {

    public enum Event {
        active( "change:active" ), drawend( "drawend" ), drawstart( "drawstart" );

        private String value;


        private Event( String value ) {
            this.value = value;
        }


        public String getValue() {
            return this.value;
        }
    }


    public enum Type implements Jsonable {
        Point, LineString, Polygon, Circle;

        @Override
        public Object toJson() {
            return "/** @type {ol.geom.GeometryType} */ '" + name() + "'";
        }
    }

    // TODO more properties

    @Immutable
    @Concern(OlPropertyConcern.class)
    public Config<VectorSource> source;

    @Immutable
    @Concern(OlPropertyConcern.class)
    public Config<Type>         type;


    public DrawInteraction( VectorSource source, Type type ) {
        super( "ol.interaction.Draw" );
        this.source.set( source );
        this.type.set( type );
    }


    /**
     * 
     * @param event
     * @param listener <b>Weakly</b> referenced by {@link EventManager}.
     */
    public void addEventListener( Event event, OlEventListener listener ) {
        PayLoad payload = new PayLoad();
        if (event == Event.drawend) {
            payload.add( "feature", "{}" );
            payload.add( "feature.type", "theEvent.feature.getGeometry().getType()" );
            payload.add( "feature.extent", "theEvent.feature.getGeometry().getExtent()" );
            if (type.get().equals( Type.Circle )) {
                payload.add( "feature.center",
                        "theEvent.feature.getGeometry().getCenter()" );
                payload.add( "feature.radius",
                        "theEvent.feature.getGeometry().getRadius()" );
            }
            else {
                payload.add( "feature.coordinates",
                        "theEvent.feature.getGeometry().getCoordinates()" );
            }
        }
        addEventListener( event.getValue(), listener, payload );
    }


    public void removeEventListener( Event event, OlEventListener listener ) {
        removeEventListener( event.getValue(), listener );
    }
}
