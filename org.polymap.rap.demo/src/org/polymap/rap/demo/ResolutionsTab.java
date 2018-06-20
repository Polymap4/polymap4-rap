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
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.polymap.rap.openlayers.base.OlMap;
import org.polymap.rap.openlayers.control.ZoomControl;
import org.polymap.rap.openlayers.layer.TileLayer;
import org.polymap.rap.openlayers.source.MapQuestSource;
import org.polymap.rap.openlayers.types.Coordinate;
import org.polymap.rap.openlayers.types.Projection;
import org.polymap.rap.openlayers.types.Projection.Units;
import org.polymap.rap.openlayers.view.View;

/**
 * 
 * @author <a href="http://stundzig.it">Steffen Stundzig</a>
 *
 */
public class ResolutionsTab
        extends DemoTab {

    public ResolutionsTab() {
        super( "Resolutions" );
    }


    @Override
    protected void createDemoControls( Composite parent ) {
        View view = new View().projection.put( new Projection( "EPSG:3857", Units.m ) ).center
                .put( new Coordinate( 1401845.7269824906, 6666952.61751981 ) ).resolution
                        .put( 4.5f );

        OlMap map = new OlMap( parent, SWT.MULTI | SWT.WRAP | SWT.BORDER, view );

        map.view.get().resolutions.set( Arrays.asList( 100.0f, 50.0f, 20.0f, 4.5f, 2.0f, 1.0f ) );

        map.addLayer( new TileLayer().source.put( new MapQuestSource( MapQuestSource.Type.osm ) ) );

        map.addControl( new ZoomControl() );
        
//        map.view.get().addPropertyChangeListener( event -> {
//            StatusBar.getInstance().addInfo( parent,
//                    name() + ": " + event.properties().toString() );
//        } );

    }


    @Override
    protected void createStyleControls( Composite parent ) {
        // TODO Auto-generated method stub

    }
}
