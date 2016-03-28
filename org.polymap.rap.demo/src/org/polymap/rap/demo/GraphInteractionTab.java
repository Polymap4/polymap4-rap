/*
 * polymap.org 
 * Copyright (C) 2016 individual contributors as indicated by the @authors tag. 
 * All rights reserved.
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
package org.polymap.rap.demo;

import java.util.Arrays;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.polymap.rap.openlayers.base.OlFeature;
import org.polymap.rap.openlayers.base.OlMap;
import org.polymap.rap.openlayers.control.ZoomSliderControl;
import org.polymap.rap.openlayers.format.GeoJSONFormat;
import org.polymap.rap.openlayers.graph.OlFeatureGraph;
import org.polymap.rap.openlayers.interaction.SelectInteraction;
import org.polymap.rap.openlayers.layer.VectorLayer;
import org.polymap.rap.openlayers.source.VectorSource;
import org.polymap.rap.openlayers.style.CircleStyle;
import org.polymap.rap.openlayers.style.FillStyle;
import org.polymap.rap.openlayers.style.StrokeStyle;
import org.polymap.rap.openlayers.style.StyleContainer;
import org.polymap.rap.openlayers.types.Attribution;
import org.polymap.rap.openlayers.types.Color;
import org.polymap.rap.openlayers.types.Extent;
import org.polymap.rap.openlayers.types.Projection;
import org.polymap.rap.openlayers.types.Projection.Units;
import org.polymap.rap.openlayers.view.View;

/**
 * 
 * @author <a href="http://stundzig.it">Steffen Stundzig</a>
 */
public class GraphInteractionTab
        extends DemoTab {

    private VectorSource source;

    private OlMap        map;


    public GraphInteractionTab() {
        super( "GraphInteraction" );
    }

    private final static Log log = LogFactory.getLog( GraphInteractionTab.class );


    @Override
    protected void createDemoControls( Composite parent ) {
        map = new OlMap( parent, SWT.MULTI | SWT.WRAP | SWT.BORDER,
                new View().projection.put( new Projection( "EPSG:3857", Units.m ) ).zoom.put( 12 ) );
        // should be refined after the final node event flush
        Extent envelope = new Extent( -100.0, -100.0, 100.0, 100.0 );
        map.view.get().fit( envelope, null );

        source = new VectorSource().format.put( new GeoJSONFormat() ).attributions
                .put( Arrays.asList( new Attribution( "Steffen Stundzig" ) ) );

        VectorLayer vector = new VectorLayer().style
                .put( new StyleContainer().fill.put( new FillStyle().color.put( new Color( 0, 0, 255, 0.1f ) ) ).stroke
                        .put( new StrokeStyle().color.put( new Color( "red" ) ).width.put( 1f ) ) ).source
                                .put( source );

        map.addLayer( vector );
        map.addControl( new ZoomSliderControl() );
        SelectInteraction selectInteraction = new SelectInteraction( vector );
        map.addInteraction( selectInteraction );
        selectInteraction.addEventListener( SelectInteraction.Event.select, event -> StatusBar.getInstance()
                .addInfo( parent, "Selected: " + event.properties().get( "selected" ).toString() ) );
    }


    @Override
    protected void createStyleControls( Composite parent ) {
        Composite group = new Composite( parent, SWT.NONE );
        group.setLayout( new GridLayout( 2, false ) );

        new Label( group, SWT.NONE ).setText( "" );

        Button center = new Button( group, SWT.BORDER );
        center.setText( "Add Features" );
        center.addSelectionListener( new SelectionAdapter() {

            private final OlFeatureGraph olFeatureGraph = new OlFeatureGraph( source, map );

            private final StyleContainer nodeStyle      = new StyleContainer().zIndex.put( 0f ).image
                    .put( new CircleStyle( 5.0f ).fill.put( new FillStyle().color.put( new Color( "red" ) ) ) );

            private int                  click          = 0;

            private final OlFeature      masterFeature  = new OlFeature( "P1" );

            private final OlFeature      masterFeature2 = new OlFeature( "P2" );

            private boolean              odd            = true;


            @Override
            public void widgetSelected( SelectionEvent e ) {

                if (click == 0) {
                    click = 1;
                    masterFeature.style.put( nodeStyle );
                    olFeatureGraph.addOrUpdateNode( masterFeature );

                    masterFeature2.style.put( nodeStyle );
                    olFeatureGraph.addOrUpdateNode( masterFeature2 );

                    olFeatureGraph.addOrUpdateEdge( masterFeature, masterFeature2 );
                }
                else {
                    final OlFeature feature2 = new OlFeature( null );
                    feature2.style.put( nodeStyle );
                    olFeatureGraph.addOrUpdateNode( feature2 );

                    if (odd) {
                        olFeatureGraph.addOrUpdateEdge( masterFeature, feature2 );
                        odd = false;
                    }
                    else {
                        olFeatureGraph.addOrUpdateEdge( masterFeature2, feature2 );
                        odd = true;
                    }
                }
            }
        } );
    }
}
