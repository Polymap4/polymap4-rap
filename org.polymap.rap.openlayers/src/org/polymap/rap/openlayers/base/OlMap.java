/*
 * polymap.org Copyright 2009-2013, Polymap GmbH. All rights reserved.
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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.rap.rwt.widgets.WidgetUtil;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Layout;
import org.polymap.core.runtime.config.Config2;
import org.polymap.core.runtime.config.Immutable;
import org.polymap.core.runtime.config.Mandatory;
import org.polymap.rap.openlayers.base.OlEventListener.PayLoad;
import org.polymap.rap.openlayers.base.OlPropertyConcern.Unquoted;
import org.polymap.rap.openlayers.control.Control;
import org.polymap.rap.openlayers.interaction.Interaction;
import org.polymap.rap.openlayers.layer.Layer;
import org.polymap.rap.openlayers.source.Source;
import org.polymap.rap.openlayers.view.View;

/**
 * The Javascript and also the CSS for the map is loaded on demand and with a fully
 * working layout. To change the default theme, add another CSS file as resource
 * <strong>ol/css/ol.css<strong>.
 * <p/>
 * 
 * This could be done in the application configuration like:
 * 
 * <pre>
 * application.addResource( &quot;ol/css/ol.css&quot;, resourceName -&gt; {
 *     return load( &quot;./resources/css/my-ol.css&quot; );
 * } );
 * </pre>
 * 
 * @see <a href="http://openlayers.org/en/master/apidoc/ol.Map.html">Open Layers API
 *      Doc</a>
 * 
 * @author <a href="http://www.polymap.de">Falko Bräutigam</a>
 * @author <a href="http://mapzone.io">Steffen Stundzig</a>
 */
public class OlMap
        extends OlObject {

    private final static Log log = LogFactory.getLog( OlMap.class );


    public enum Event {
        layerGroup("change:layerGroup"), size("change:size"), target("change:target"), view("change:view"),
        click("click"), boxstart("boxstart"), boxend("boxend");
        
        private String eventName;
        
        Event(String eventName) {
            this.eventName = eventName;
        }
        
        public String getEventName() {
            return eventName;
        }
    }

    @Mandatory
    @Immutable
    public Config2<OlMap,View>      view;
    
    private Config2<OlMap,List<Interaction>> interactions;

    @Mandatory
    @Immutable
    private Config2<OlMap,Unquoted> target;

    private Composite               widget;


    public OlMap( Composite parent, int style, View view ) {
        super( "ol.Map" );
        this.view.set( view );
//        this.interactions.set( new ArrayList<Interaction>() );
        view.setMap( this );

        widget = new Composite( parent, style );
        widget.setLayout( new Layout() {

            private static final long serialVersionUID = 1L;

            @Override
            protected void layout( Composite composite, boolean flushCache ) {
                update();
            }

            @Override
            protected Point computeSize( Composite composite, int wHint, int hHint, boolean flushCache ) {
                return new Point( wHint, hHint );
            }
        } );
        this.target.set( new Unquoted( "this.createDiv('" + WidgetUtil.getId( widget ) + "')" ) );
    }

    
    public Composite getControl() {
        return widget;
    }
    
//    
//    @SuppressWarnings("unchecked")
//    public <T extends Interaction> T getInteraction( Class<T> clazz ) {
//        if(interactions.get() != null) {
//            for(Interaction interaction : interactions.get()) {
//                if(clazz.isAssignableFrom( interaction.getClass() )) {
//                    return (T) interaction;
//                }
//            }
//        }
//        return null;
//    }    

    
    /**
     * Adds the given layer to the top of this map.
     */
    public void addLayer( Layer<? extends Source> layer ) {
        call( "addLayer", layer );
    }


    public void removeLayer( Layer<? extends Source> layer ) {
        call( "removeLayer", layer );
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
     * The event contains the new center, resolution and rotation
     * 
     * @param event
     * @param listener
     */
    public void addEventListener( Event event, OlEventListener listener ) {
        PayLoad payload = null;
        if(event == Event.click) {
            payload = new PayLoad();
            payload.add( "feature", "{}" );
            payload.add( "feature.pixel", "theEvent.pixel" );
            payload.add( "feature.coordinate", "that.objs['" + getObjRef() + "'].getCoordinateFromPixel(theEvent.pixel)" );
        } else if(event == Event.boxstart || event == Event.boxend) {
            payload = new PayLoad();
            payload.add( "feature", "{}" );
            payload.add( "feature.pixel", "that.objs['" + getObjRef() + "'].getPixelFromCoordinate(theEvent.coordinate)" );
            payload.add( "feature.coordinate", "theEvent.coordinate" );
        }
        addEventListener( event.getEventName(), listener, payload );
    }

    public void removeEventListener( Event event, OlEventListener listener ) {
        removeEventListener( event.getEventName(), listener );
    }

    
    private void update() {
        call( "this.obj.updateSize();" );
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
