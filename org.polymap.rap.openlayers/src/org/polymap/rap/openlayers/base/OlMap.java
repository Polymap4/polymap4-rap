/*
 * polymap.org 
 * Copyright 2009-2018, Polymap GmbH. All rights reserved.
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
package org.polymap.rap.openlayers.base;

import java.util.List;
import java.util.Optional;

import org.json.JSONArray;
import org.json.JSONObject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.google.common.collect.Lists;

import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Layout;

import org.eclipse.rap.rwt.widgets.WidgetUtil;

import org.polymap.core.runtime.config.Config2;
import org.polymap.core.runtime.config.Immutable;
import org.polymap.core.runtime.config.Mandatory;

import org.polymap.rap.openlayers.base.OlPropertyConcern.Unquoted;
import org.polymap.rap.openlayers.control.Control;
import org.polymap.rap.openlayers.interaction.Interaction;
import org.polymap.rap.openlayers.layer.Layer;
import org.polymap.rap.openlayers.source.Source;
import org.polymap.rap.openlayers.types.Coordinate;
import org.polymap.rap.openlayers.types.Pixel;
import org.polymap.rap.openlayers.view.View;

/**
 * The map is the core component of OpenLayers. For a map to render, a view, one or
 * more layers are needed.
 * <p/>
 * The Javascript and also the CSS for the map is loaded on demand and with a fully
 * working layout. To change the default theme, add another CSS file as resource
 * <strong>ol/css/ol.css</strong>.
 * <p/>
 * This could be done in the application configuration like:
 * <pre>
 * application.addResource( &quot;ol/css/ol.css&quot;, resourceName -&gt; {
 *     return load( &quot;./resources/css/my-ol.css&quot; );
 * } );
 * </pre>
 * 
 * @see <a href="http://openlayers.org/en/v3.20.1/apidoc/ol.Map.html">OpenLayers Doc</a>
 * 
 * @author <a href="http://www.polymap.de">Falko Bräutigam</a>
 * @author <a href="http://mapzone.io">Steffen Stundzig</a>
 */
public class OlMap
        extends OlObject {

    private final static Log log = LogFactory.getLog( OlMap.class );

    /** 
     * Event types of {@link OlMap}. 
     */
    public enum Event {
        LAYERGROUP( "change:layerGroup" ),
        SIZE( "change:size" ),
        TARGET( "change:target" ),
        VIEW( "change:view" ),
        /** @see ClickEventPayload */
        CLICK( "click" ),
        /** @see BoxEventPayload */
        BOXSTART( "boxstart" ),
        /** @see BoxEventPayload */
        BOXEND( "boxend" ),
        /** @see PointerEventPayload */
        POINTERMOVE( "pointermove" );

        private String name;

        private Event( String name ) {
            this.name = name;
        }

        public String toString() {
            return name;
        }
    }

    /**
     * The coordinates of an {@link Event#CLICK} event. 
     * @see Event#CLICK
     */
    public static class ClickEventPayload
            extends OlEventPayload {

        public static Optional<ClickEventPayload> findIn( OlEvent ev ) {
            JSONObject json = ev.properties().optJSONObject( "click" );
            return Optional.ofNullable( json != null ? new ClickEventPayload( json ) : null );
        }

        // receive ****************************************
        
        private JSONObject          json;

        protected ClickEventPayload( JSONObject json ) {
            this.json = json;
        }

        public Coordinate coordinate() {
            JSONArray coord = json.getJSONArray( "coordinate" );
            return new Coordinate( coord.getDouble( 0 ), coord.getDouble( 1 ) );
        }
        
        public Pixel pixel() {
            JSONArray pixel = json.getJSONArray( "pixel" );
            return new Pixel( pixel.getInt( 0 ), pixel.getInt( 1 ) );
        }
        
        // send *******************************************
        
        public ClickEventPayload() {
            super();
        }
        
        @Override
        public List<Variable> variables() {
            return Lists.newArrayList(
                    new Variable( "click", "{}" ),
                    new Variable( "click.pixel", "theEvent.pixel" ),
                    new Variable( "click.coordinate", jsObjRef() + ".getCoordinateFromPixel(theEvent.pixel)" ) );
        }
    }


    /**
     * The coordinates of an {@link Event#POINTERMOVE} event. 
     * @see Event#POINTERMOVE
     */
    public static class PointerEventPayload
            extends OlEventPayload {

        public static Optional<PointerEventPayload> findIn( OlEvent ev ) {
            JSONObject json = ev.properties().optJSONObject( "pointer" );
            return Optional.ofNullable( json != null ? new PointerEventPayload( json ) : null );
        }

        // receive ****************************************
        
        private JSONObject          json;

        protected PointerEventPayload( JSONObject json ) {
            this.json = json;
        }

        public Coordinate coordinate() {
            JSONArray coord = json.getJSONArray( "coordinate" );
            return new Coordinate( coord.getDouble( 0 ), coord.getDouble( 1 ) );
        }
        
        public Pixel pixel() {
            JSONArray pixel = json.getJSONArray( "pixel" );
            return new Pixel( pixel.getInt( 0 ), pixel.getInt( 1 ) );
        }
        
        // send *******************************************
        
        public PointerEventPayload() {
            super();
        }
        
        @Override
        public List<Variable> variables() {
            return Lists.newArrayList(
                    new Variable( "pointer", "{}" ),
                    new Variable( "pointer.pixel", "theEvent.pixel" ),
                    new Variable( "pointer.coordinate", jsObjRef() + ".getCoordinateFromPixel(theEvent.pixel)" ) );
        }
    }

    
    // instance *******************************************
    
    @Mandatory
    @Immutable
    public Config2<OlMap,View>               view;

    private Config2<OlMap,List<Interaction>> interactions;

    @Mandatory
    @Immutable
    private Config2<OlMap,Unquoted>          target;

    private Composite                        widget;


    public OlMap( Composite parent, int style, View view ) {
        super( "ol.Map" );
        this.view.set( view );
        view.setMap( this );

        widget = new Composite( parent, style );
        widget.setTouchEnabled( true );
        widget.setLayout( new Layout() {
            @Override
            protected void layout( Composite composite, boolean flushCache ) {
                log.debug( "layout " + composite + ", " + flushCache );
                update();
            }
            @Override
            protected Point computeSize( Composite composite, int wHint, int hHint, boolean flushCache ) {
                log.debug( "computeSize " + composite + ", " + wHint + ", " + ", " + hHint + flushCache );
                return new Point( 1, 1 );
            }
        } );
        target.set( new Unquoted( "this.createDiv('" + WidgetUtil.getId( widget ) + "')" ) );
//        call( "this.obj.on('propertychange', function() {console.log('propertychange on map');})" );
//        addEventListener( "propertychange", event -> {
//            log.info( event.properties() );
//        }, new PayLoad().add( "event", "{}" ).add( "event.key", "theEvent.key" ).add( "event.value", "theEvent.newValue" ).add( "event.oldValue", "theEvent.target.get(theEvent.key)" ) );

    }


    public Composite getControl() {
        return widget;
    }


    /**
     * Adds the given layer to the top of this map.
     */
    public void addLayer( Layer<? extends Source> layer ) {
        call( "addLayer", layer );
        layer.onSetMap( this );
    }


    public void removeLayer( Layer<? extends Source> layer ) {
        call( "removeLayer", layer );
        layer.onUnsetMap( this );
    }


    // /**
    // * Move the given layer to the specified (zero-based) index in the layer
    // * list, changing its z-index in the map display. Use map.getLayerIndex() to
    // * find out the current index of a layer. Note that this cannot (or at least
    // * should not) be effectively used to raise base layers above overlays.
    // *
    // * @param layer
    // * @param index
    // */
    // public void setLayerIndex(Layer layer, int index) {
    // callObjFunction("setLayerIndex", layer, index);
    // }

    public void addControl( Control control ) {
        call( "addControl", control );
        control.map.set( this );
    }


    public void removeControl( Control control ) {
        call( "removeControl", control );
    }


    public void addInteraction( Interaction interaction ) {
        call( "addInteraction", interaction );
    }


    public void removeInteraction( Interaction interaction ) {
        call( "removeInteraction", interaction );
    }


    // /**
    // * This property is what allows OpenLayers to know what scale things are
    // * being rendered at, which is important for scale-based methods of zooming
    // * and the Scale display control.
    // *
    // * @param units
    // * The map units. Defaults to "degrees". Possible values are
    // * "degrees" (or "dd"), "m", "ft", "km", "mi", "inches".
    // */
    // public void setUnits(String units) {
    // this.units = units;
    // setAttribute("units", units);
    // }
    //
    // public void updateSize() {
    // execute(new Stringer("setTimeout( function() {", getJSObjRef(),
    // ".updateSize();", "}, 500 );").toString());
    // }


    /**
     * @param event One of the {@link Event} types.
     * @param payload {@link ClickEventPayload}, {@link BoxEventPayload},
     *        {@link PointerEventPayload} according to the event type or any other
     *        appropriate payload.
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


    private void update() {
        call( "var that = this.obj; setTimeout( function() { that.updateSize();}, 10);" );
    }


    @Override
    public void dispose() {
        // TODO clear the widget and the map
        super.dispose();
    }


    public void render() {
        call( "this.obj.render();" );
    }
}
