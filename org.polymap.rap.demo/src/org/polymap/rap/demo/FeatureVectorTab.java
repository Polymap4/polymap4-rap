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
import org.polymap.rap.openlayers.format.GeoJSONFormat;
import org.polymap.rap.openlayers.geom.PointGeometry;
import org.polymap.rap.openlayers.geom.PolygonGeometry;
import org.polymap.rap.openlayers.layer.VectorLayer;
import org.polymap.rap.openlayers.source.VectorSource;
import org.polymap.rap.openlayers.style.CircleStyle;
import org.polymap.rap.openlayers.style.FillStyle;
import org.polymap.rap.openlayers.style.StrokeStyle;
import org.polymap.rap.openlayers.style.Style;
import org.polymap.rap.openlayers.types.Attribution;
import org.polymap.rap.openlayers.types.Color;
import org.polymap.rap.openlayers.types.Coordinate;

/**
 * 
 * @author <a href="http://stundzig.it">Steffen Stundzig</a>
 *
 */
public class FeatureVectorTab
        extends DemoTab {

    private VectorSource source;

    private OlMap        map;


    public FeatureVectorTab() {
        super( "FeatureVector" );
    }

    private final static Log log = LogFactory.getLog( FeatureVectorTab.class );


    @Override
    protected void createDemoControls( Composite parent ) {
        map = defaultMap( parent );

        source = new VectorSource().format.put( new GeoJSONFormat() ).attributions.put( Arrays
                .asList( new Attribution( "Steffen Stundzig" ) ) );

        VectorLayer vector = new VectorLayer().style.put( new Style().fill
                .put( new FillStyle().color.put( new Color( 0, 0, 255, 0.1f ) ) ).stroke
                .put( new StrokeStyle().color.put( new Color( "red" ) ).width.put( 1f ) ) ).source
                .put( source );

        map.addLayer( vector );

        // vector.addEventListener(VectorSource.EVENT.addfeature, event ->
        // System.out.println(event.getProperties()));
    }


    @Override
    protected void createStyleControls( Composite parent ) {
        Composite group = new Composite( parent, SWT.NONE );
        group.setLayout( new GridLayout( 2, false ) );

        new Label( group, SWT.NONE ).setText( "" );

        Button center = new Button( group, SWT.BORDER );
        center.setText( "Add Features" );
        center.addSelectionListener( new SelectionAdapter() {

            @Override
            public void widgetSelected( SelectionEvent e ) {
                OlFeature feature = new OlFeature();
                feature.name.set( "Point" );
                feature.labelPoint.set( map.view.get().center.get() );
                feature.geometry.set( new PointGeometry( map.view.get().center.get() ) );
                feature.style.put( new Style().image.put( new CircleStyle( 5.0f ).fill
                        .put( new FillStyle().color.put( new Color( "red" ) ) ) ) );
                source.addFeature( feature );

                OlFeature feature2 = new OlFeature();
                feature2.name.set( "Polygon" );
                feature2.labelPoint.set( map.view.get().center.get() );
                feature2.geometry.set( new PolygonGeometry( new Coordinate( -7294043.592788689,
                        6683576.061026307 ),
                        new Coordinate( -7860876.838653959, 4709432.687495542 ), new Coordinate(
                                -4029865.9355645515, 4572610.869528064 ), new Coordinate(
                                -7294043.592788689, 6683576.061026307 ) ) );
                feature2.style.put( new Style().fill.put( new FillStyle().color.put( new Color(
                        "red" ) ) ).stroke.put( new StrokeStyle().color.put( new Color( "blue" ) ).width
                        .put( 10f ) ) );
                source.addFeatures( feature, feature2 );
            }
        } );

    }
}
