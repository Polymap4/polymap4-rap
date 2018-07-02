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
package org.polymap.rap.openlayers.view;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.json.JSONArray;
import org.json.JSONObject;

import org.polymap.core.runtime.config.Concern;
import org.polymap.core.runtime.config.Config2;
import org.polymap.core.runtime.config.Mandatory;

import org.polymap.rap.openlayers.base.OlEvent;
import org.polymap.rap.openlayers.base.OlEventPayload;
import org.polymap.rap.openlayers.base.OlMap;
import org.polymap.rap.openlayers.base.OlObject;
import org.polymap.rap.openlayers.base.OlPropertyConcern;
import org.polymap.rap.openlayers.types.Coordinate;
import org.polymap.rap.openlayers.types.Extent;
import org.polymap.rap.openlayers.types.Projection;
import org.polymap.rap.openlayers.types.Size;

/**
 * Represents a simple 2D view of the map. This is the object to
 * act upon to change the center, resolution, and rotation of the map.
 * 
 * @see <a href="http://openlayers.org/en/master/apidoc/ol.View.html">OpenLayers
 *      Doc</a>
 * @author <a href="http://stundzig.it">Steffen Stundzig</a>
 * @author <a href="http://www.polymap.de">Falko Bräutigam</a>
 */
public class View
        extends OlObject {

    /**
     * Events of {@link View}.
     */
    public enum Event {
        /** @see ExtentEventPayload */
        CENTER( "center" ), 
        /** @see ExtentEventPayload */
        RESOLUTION( "resolution" ), 
        /** @see ExtentEventPayload */
        ROTATION( "rotation" );
        
        private String name;

        private Event( String name ) {
            this.name = name;
        }

        public String toString() {
            return name;
        }
    }

    /**
     * Payload of {@link View} {@link Event} types or any other event type.
     * 
     * @see Event#CENTER
     * @see Event#RESOLUTION
     * @see Event#ROTATION
     */
    public static class ExtentEventPayload
            extends OlEventPayload {

        public static Optional<ExtentEventPayload> findIn( OlEvent ev ) {
            Object extent = ev.properties().opt( "extent" );
            return Optional.ofNullable( extent != null ? new ExtentEventPayload( ev.properties() ) : null );
        }
      
        // send *******************************************

        protected OlMap         map;
        
        public ExtentEventPayload() {
        }

        @Override
        public List<Variable> variables() {
            return Collections.singletonList( new Variable( 
                    "extent", jsObjRef() + ".calculateExtent(that.objs['" + map.getObjRef() + "'].getSize())" ) );
        }
        
        // receive ****************************************
        
        private JSONObject      json;
        
        protected ExtentEventPayload( JSONObject json ) {
            this.json = json;
        }

        public Extent extent() {
            JSONArray extent = json.getJSONArray( "extent" );
            return new Extent( 
                    extent.getDouble( 0 ), extent.getDouble( 2 ), 
                    extent.getDouble( 1 ), extent.getDouble( 3 ) );
        }
        
        public double resolution() {
            // XXX whare this comes from?
            return json.getDouble( "resolution" );
        }

    }


    // instance *******************************************
    
    /**
     * The initial center for the view. The coordinate system for the center is
     * specified with the projection option. Default is undefined, and layer sources
     * will not be fetched if this is not set.
     */
    @Concern(OlPropertyConcern.class)
    public Config2<View,Coordinate>   center;

    /**
     * The maximum resolution used to determine the resolution constraint. It is used
     * together with minResolution (or maxZoom) and zoomFactor. If unspecified it is
     * calculated in such a way that the projection's validity extent fits in a
     * 256x256 px tile. If the projection is Spherical Mercator (the default) then
     * maxResolution defaults to 40075016.68557849 / 256 = 156543.03392804097.
     */
    @Concern(OlPropertyConcern.class)
    public Config2<View,Float>       maxResolution;

    /**
     * The minimum resolution used to determine the resolution constraint. It is used
     * together with maxResolution (or minZoom) and zoomFactor. If unspecified it is
     * calculated assuming 29 zoom levels (with a factor of 2). If the projection is
     * Spherical Mercator (the default) then minResolution defaults to
     * 40075016.68557849 / 256 / Math.pow(2, 28) = 0.0005831682455839253.
     */
    @Concern(OlPropertyConcern.class)
    public Config2<View,Float>       minResolution;

    /**
     * The maximum zoom level used to determine the resolution constraint. It is used
     * together with {@link #maxZoom} (or {@link #maxResolution}) and zoomFactor.
     * Default is 28. Note that if {@link #minResolution} is also provided, it is
     * given precedence over maxZoom.
     */
    @Concern(OlPropertyConcern.class)
    public Config2<View,Integer>      maxZoom;

    /**
     * The minimum zoom level used to determine the resolution constraint. It is used
     * together with maxZoom (or minResolution) and zoomFactor. Default is 0. Note
     * that if maxResolution is also provided, it is given precedence over minZoom.
     */
    @Concern(OlPropertyConcern.class)
    public Config2<View,Integer>      minZoom;

    /**
     * The projection. Default is EPSG:3857 (Spherical Mercator).
     */
    @Concern(OlPropertyConcern.class)
    public Config2<View,Projection>   projection;

    /**
     * The initial resolution for the view. The units are projection units per pixel
     * (e.g. meters per pixel). An alternative to setting this is to set zoom.
     * Default is undefined, and layer sources will not be fetched if neither this
     * nor zoom are defined.
     */
    @Concern(OlPropertyConcern.class)
    public Config2<View,Float>       resolution;

    /**
     * Resolutions to determine the resolution constraint. If set the maxResolution,
     * minResolution, minZoom, maxZoom, and zoomFactor options are ignored.
     * 
     * Implementation Note: Resolutions must be added in descending order. And
     * Resolutions must be set before adding the layers.
     */
    @Concern(OlPropertyConcern.class)
    public Config2<View,List<Float>> resolutions;

    /**
     * The initial rotation for the view in radians (positive rotation clockwise).
     * Default is 0.
     */
    @Concern(OlPropertyConcern.class)
    public Config2<View,Float>       rotation;

    /**
     * Only used if resolution is not defined. Zoom level used to calculate the
     * initial resolution for the view.
     */
    @Concern(OlPropertyConcern.class)
    public Config2<View,Integer>      zoom;

    /**
     * The zoom factor used to determine the resolution constraint. Default is 2.
     */
    @Concern(OlPropertyConcern.class)
    public Config2<View,Integer>      zoomFactor;

    // /** experimental
    // * The extent that constrains the {@link #center}, in other words, center
    // cannot
    // * be set outside this extent. Default is undefined.
    // */
    // @Concern(OlPropertyConcern.class)
    // public Config2<View,Extent> extent;

    private OlMap                     map;


    /**
     * Constructs a new instance.
     *
     * @param initializers Initialize at least all {@link Mandatory} properties.
     */
    public View() {
        super( "ol.View" );
    }


    /**
     * @param event One of the {@link Event} types.
     * @param payload {@link ExtentEventPayload} according to the event type or any
     *        other appropriate payload.
     */
    @Override
    public void addEventListener( Object event, Object annotated, OlEventPayload... payload ) {
        for (OlEventPayload entry : payload) {
            if (entry instanceof ExtentEventPayload) {
                ((ExtentEventPayload)entry).map = map;
            }
        }
        super.addEventListener( "change:" + ((Event)event).toString(), annotated, payload );
    }

    @Override
    public void removeEventListener( Object event, Object annotated ) {
        super.removeEventListener( "change:" + ((Event)event).toString(), annotated );
    }


//    /**
//     * 
//     * @param event
//     * @param listener <b>Weakly</b> referenced by {@link EventManager}.
//     */
//    public void addPropertyChangeListener( OlEventListener listener ) {
//        addEventListener( "propertychange", listener, null );
//    }


    /**
     * Fit the given geometry or extent based on the given map size and border. The
     * size is pixel dimensions of the box to fit the extent into. In most cases you
     * will want to use the map size, that is map.getSize(). Takes care of the map
     * angle.
     * 
     * @param geometry ol.geom.SimpleGeometry | ol.Extent Geometry.
     * @param size ol.Size Box pixel size, if null the default map size is used.
     */
    public void fit( Extent geometry, Size size ) {
        if (size == null) {
            // call fit(geometry, map.getSize());
            call( "this.obj.fit(" + geometry.toJson() + ", " + this.map.getJSObjRef()
                    + ".getSize());" );
        }
        else {
            // call fit(geometry, size);
            call( "this.obj.fit(" + geometry.toJson() + ", " + size.toJson() + ");" );
        }
    }


    /**
     * must only be called from the OlMap
     * 
     * @param map
     */
    public void setMap( OlMap map ) {
        this.map = map;
    }

}
