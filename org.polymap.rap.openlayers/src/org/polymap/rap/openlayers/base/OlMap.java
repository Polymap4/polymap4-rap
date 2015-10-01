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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.rap.rwt.widgets.WidgetUtil;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Layout;
import org.polymap.core.runtime.config.Config2;
import org.polymap.core.runtime.config.Immutable;
import org.polymap.core.runtime.config.Mandatory;
import org.polymap.rap.openlayers.base.OlPropertyConcern.Unquoted;
import org.polymap.rap.openlayers.control.Control;
import org.polymap.rap.openlayers.interaction.DrawInteraction;
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


    public enum EVENT {
        layerGroup, size, target, view
    }

    @Mandatory
    @Immutable
    public Config2<OlMap,View>      view;

    @Mandatory
    @Immutable
    private Config2<OlMap,Unquoted> target;

    private Composite               widget;


    public OlMap( Composite parent, int style, View view ) {
        super( "ol.Map" );
        this.view.set( view );
        view.setMap( this );

        widget = new Composite( parent, style );
        widget.setLayout( new Layout() {

            private static final long serialVersionUID = 1L;


            @Override
            protected void layout( Composite composite, boolean flushCache ) {
                update();
            }


            @Override
            protected Point computeSize( Composite composite, int wHint, int hHint,
                    boolean flushCache ) {
                return new Point( wHint, hHint );
            }
        } );
        this.target.set( new Unquoted( "this.createDiv('" + WidgetUtil.getId( widget ) + "')" ) );
    }


    public void setLayoutData( Object layoutData ) {
        widget.setLayoutData( layoutData );
    }

    public Composite getControl() {
        return widget;
    }

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


    public void addInteraction( DrawInteraction di ) {
        call( "addInteraction", di );
    }


    public void removeInteraction( DrawInteraction di ) {
        call( "removeInteraction", di );
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
    public void addEventListener( EVENT event, OlEventListener listener ) {
        addEventListener( "change:" + event.name(), listener, null );
    }


    public void removeEventListener( EVENT event, OlEventListener listener ) {
        removeEventListener( "change:" + event.name(), listener );
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
