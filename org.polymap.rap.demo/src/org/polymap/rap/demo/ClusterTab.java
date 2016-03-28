/*
 * polymap.org
 * Copyright (C) 2009-2015 Polymap GmbH. All rights reserved.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 */
package org.polymap.rap.demo;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.swt.widgets.Composite;
import org.polymap.rap.openlayers.base.OlFeature;
import org.polymap.rap.openlayers.base.OlMap;
import org.polymap.rap.openlayers.format.GeoJSONFormat;
import org.polymap.rap.openlayers.geom.PointGeometry;
import org.polymap.rap.openlayers.layer.ClusterLayer;
import org.polymap.rap.openlayers.layer.Layer;
import org.polymap.rap.openlayers.source.ClusterSource;
import org.polymap.rap.openlayers.source.VectorSource;
import org.polymap.rap.openlayers.style.CircleStyle;
import org.polymap.rap.openlayers.style.FillStyle;
import org.polymap.rap.openlayers.style.StrokeStyle;
import org.polymap.rap.openlayers.style.Style;
import org.polymap.rap.openlayers.style.StyleContainer;
import org.polymap.rap.openlayers.types.Attribution;
import org.polymap.rap.openlayers.types.Color;
import org.polymap.rap.openlayers.types.Coordinate;

/**
 * 
 * @author <a href="http://stundzig.it">Steffen Stundzig</a>
 *
 */
public class ClusterTab
        extends DemoTab {

    private OlMap map;


    public ClusterTab() {
        super( "ClusterTab" );
    }

    private final static Log log = LogFactory.getLog( ClusterTab.class );

    private Layer<VectorSource> vector;

    private Map<Pair<Double, Double>, OlFeature> coords = new HashMap<Pair<Double, Double>, OlFeature>();

    @Override
    protected void createDemoControls( Composite parent ) {
        map = defaultMap( parent );

        VectorSource source = new VectorSource().format.put( new GeoJSONFormat() ).attributions.put( Arrays
                .asList( new Attribution( "Steffen Stundzig" ) ) );

        ClusterSource clusterSource = new ClusterSource().distance.put(40d).source.put(source).format.put( new GeoJSONFormat() ).attributions.put( Arrays
                .asList( new Attribution( "Steffen Stundzig" ) ) );

        vector = new ClusterLayer().source.put( clusterSource );

        OlFeature olFeature1 = new OlFeature("Test1");
        olFeature1.name.set( "Test1" );
        Coordinate coord1 = map.view.get().center.get();
        double coord1X = ((Double)((org.json.JSONArray)map.view.get().center.get().toJson()).get( 0 )); 
        double coord1Y = ((Double)((org.json.JSONArray)map.view.get().center.get().toJson()).get( 1 ));
        coords.put( Pair.of( coord1X, coord1Y ), olFeature1 );
        olFeature1.geometry.set( new PointGeometry( coord1 ) );
        olFeature1.style.put( new StyleContainer().stroke.put( new StrokeStyle().color.put( new Color( "green" ) ).width
                .put( 2f ) ).image.put( new CircleStyle( 5.0f ).fill.put( new FillStyle().color
                .put( new Color( "red" ) ) ) ) );
        source.addFeature( olFeature1 );

        OlFeature olFeature2 = new OlFeature("Test2");
        olFeature2.name.set( "Test2" );
        double coord2X = ((Double)((org.json.JSONArray)map.view.get().center.get().toJson()).get( 0 )) + 1000; 
        double coord2Y = ((Double)((org.json.JSONArray)map.view.get().center.get().toJson()).get( 1 )) + 1000;
        Coordinate coord2 = new Coordinate(coord2X, coord2Y);
        coords.put( Pair.of( coord2X, coord2Y ), olFeature2 );
        olFeature2.geometry.set( new PointGeometry( coord2 ) );
        olFeature2.style.put( new StyleContainer().stroke.put( new StrokeStyle().color.put( new Color( "green" ) ).width
                .put( 2f ) ).image.put( new CircleStyle( 5.0f ).fill.put( new FillStyle().color
                .put( new Color( "red" ) ) ) ) );
        source.addFeature( olFeature2 );
        
        
        map.addLayer( vector );
    }


    @Override
    protected void createStyleControls( Composite parent ) {
    }
}
