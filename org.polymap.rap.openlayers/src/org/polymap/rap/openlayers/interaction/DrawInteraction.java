/*
 * polymap.org 
 * Copyright (C) 2009-2018, Polymap GmbH. All rights reserved.
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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.json.JSONObject;

import com.google.common.collect.Lists;

import org.polymap.core.runtime.config.Concern;
import org.polymap.core.runtime.config.Config;
import org.polymap.core.runtime.config.Immutable;

import org.polymap.rap.openlayers.base.Jsonable;
import org.polymap.rap.openlayers.base.OlEvent;
import org.polymap.rap.openlayers.base.OlEventPayload;
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

    /**
     * 
     */
    public enum Type implements Jsonable {
        Point, LineString, Polygon, Circle;

        @Override
        public Object toJson() {
            return "/** @type {ol.geom.GeometryType} */ '" + name() + "'";
        }
    }

    
    /**
     * The event types of {@link DrawInteraction}. 
     */
    public enum Event {
        ACTIVE( "change:active" ),
        /** @see DrawendEventPayload */
        DRAWEND( "drawend" ), 
        DRAWSTART( "drawstart" );

        private String name;

        private Event( String name ) {
            this.name = name;
        }

        public String toString() {
            return name;
        }
    }


    /**
     * The coordinates of an {@link Event#DRAWEND} event. 
     * @see Event#DRAWEND
     */
    public static class DrawendEventPayload
            extends OlEventPayload {

        public static Optional<DrawendEventPayload> findIn( OlEvent ev ) {
            JSONObject json = ev.properties().optJSONObject( "draw" );
            return Optional.ofNullable( json != null ? new DrawendEventPayload( json ) : null );
        }

        // receive ****************************************
        
        private JSONObject          json;

        protected DrawendEventPayload( JSONObject json ) {
            this.json = json;
        }

//        public Coordinate coordinate() {
//            JSONArray coord = json.getJSONArray( "coordinate" );
//            return new Coordinate( coord.getDouble( 0 ), coord.getDouble( 1 ) );
//        }
//        
//        public Pixel pixel() {
//            JSONArray pixel = json.getJSONArray( "pixel" );
//            return new Pixel( pixel.getInt( 0 ), pixel.getInt( 1 ) );
//        }
        
        // send *******************************************
        
        public DrawendEventPayload() {
        }
        
        @Override
        public List<Variable> variables() {
            ArrayList<Variable> result = Lists.newArrayList(
                    new Variable( "draw", "{}" ),
                    new Variable( "draw.type", "theEvent.feature.getGeometry().getType()" ),
                    new Variable( "draw.extent", "theEvent.feature.getGeometry().getExtent()" ) );
 
            if (((DrawInteraction)belongsTo).type.get().equals( Type.Circle )) {
                result.add( new Variable( "draw.center", "theEvent.feature.getGeometry().getCenter()" ) );
                result.add( new Variable( "draw.radius", "theEvent.feature.getGeometry().getRadius()" ) );
            }
            else {
                result.add( new Variable( "draw.coordinates", "theEvent.feature.getGeometry().getCoordinates()" ) );
            }
            return result;
        }
    }

    // instance *******************************************
    
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
     * @param event One of the {@link Event} types.
     * @param payload {@link DrawendEventPayload} or any other appropriate payload.
     */
    @Override
    public void addEventListener( Object event, Object annotated, OlEventPayload... payload ) {
        super.addEventListener( event, annotated, payload );
    }


    /**
     * @param event One of the {@link Event} types.
     */
    @Override
    public void removeEventListener( Object event, Object annotated ) {
        super.removeEventListener( event, annotated );
    }

}
