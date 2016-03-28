/*
 * polymap.org Copyright (C) 2009-2015 Polymap GmbH. All rights reserved.
 * 
 * This is free software; you can redistribute it and/or modify it under the terms of
 * the GNU Lesser General Public License as published by the Free Software
 * Foundation; either version 2.1 of the License, or (at your option) any later
 * version.
 * 
 * This software is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 */
package org.polymap.rap.demo;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.polymap.rap.openlayers.base.OlMap;
import org.polymap.rap.openlayers.format.GeoJSONFormat;
import org.polymap.rap.openlayers.interaction.DrawInteraction;
import org.polymap.rap.openlayers.layer.VectorLayer;
import org.polymap.rap.openlayers.source.VectorSource;
import org.polymap.rap.openlayers.style.FillStyle;
import org.polymap.rap.openlayers.style.StrokeStyle;
import org.polymap.rap.openlayers.style.StyleContainer;
import org.polymap.rap.openlayers.types.Attribution;
import org.polymap.rap.openlayers.types.Color;

/**
 * 
 * @author <a href="http://stundzig.it">Steffen Stundzig</a>
 *
 */
public class DrawInteractionTab
        extends DemoTab {

    private VectorSource source;

    private OlMap        map;


    public DrawInteractionTab() {
        super( "DrawInteraction" );
    }

    private final static Log log = LogFactory.getLog( DrawInteractionTab.class );


    @Override
    protected void createDemoControls( Composite parent ) {
        map = defaultMap( parent );

        source = new VectorSource()
                .format.put( new GeoJSONFormat() )
                .attributions.put( Arrays.asList( new Attribution( "Steffen Stundzig" ) ) );

        VectorLayer vector = new VectorLayer()
                .style.put( new StyleContainer()
                .fill.put( new FillStyle().color.put( new Color( 0, 0, 255, 0.1f ) ) )
                .stroke.put( new StrokeStyle().color.put( new Color( "red" ) ).width.put( 1f ) ) )
                .source.put( source );

        map.addLayer( vector );

        // vector.addEventListener(VectorSource.EVENT.addfeature, event ->
        // System.out.println(event.getProperties()));
    }


    @Override
    protected void createStyleControls( Composite parent ) {
        Composite group = new Composite( parent, SWT.NONE );
        group.setLayout( new GridLayout( 2, false ) );

        new Label( group, SWT.NONE ).setText( "Type" );

        final Combo combo = new Combo( group, SWT.READ_ONLY );
        combo.setItems( Arrays.stream( DrawInteraction.Type.values() )
                .map( type -> type.toString() ).toArray( String[]::new ) );
        combo.addSelectionListener( new SelectionAdapter() {

            Map<String,DrawInteraction> interactions       = new HashMap<String,DrawInteraction>();

            DrawInteraction             currentInteraction = null;


            @Override
            public void widgetSelected( SelectionEvent e ) {
                int index = combo.getSelectionIndex();
                String selection = combo.getItem( index );
                DrawInteraction di = interactions.get( selection );
                if (di == null) {
                    di = new DrawInteraction( source, DrawInteraction.Type.valueOf( selection ) );
                    di.addEventListener( DrawInteraction.Event.drawstart,
                            event -> System.out.println( event.properties() ) );
                    di.addEventListener( DrawInteraction.Event.drawend,
                            event -> System.out.println( event.properties() ) );
                    interactions.put( selection, di );
                }
                if (currentInteraction != null) {
                    map.removeInteraction( currentInteraction );
                }
                map.addInteraction( di );
                currentInteraction = di;
            }
        } );
        combo.select( 0 );
    }
}
