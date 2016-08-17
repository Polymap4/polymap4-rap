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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.polymap.core.ui.FormDataFactory;
import org.polymap.rap.openlayers.base.OlMap;
import org.polymap.rap.openlayers.types.Extent;
import org.polymap.rap.openlayers.types.Projection;
import org.polymap.rap.openlayers.types.Projection.Units;
import org.polymap.rap.openlayers.view.View;

/**
 * 
 * @author <a href="http://stundzig.it">Steffen Stundzig</a>
 *
 */
public class ExtentTab
        extends DemoTab {

    public ExtentTab() {
        super( "Extent" );
    }

    private final static Log log = LogFactory.getLog( ExtentTab.class );


    @Override
    protected void createDemoControls( Composite parent ) {

        OlMap map = defaultMap( parent );

        Projection epsg3857 = new Projection( "EPSG:3857", Units.m );
        Extent envelope = new Extent( 1380000.0, 6690000.0, 1390000.0, 6680000.0 );

        map.view.get().projection.set( epsg3857 );
        map.view.get().addEventListener( View.Event.center,
                event -> System.out.println( event.properties().toString() ) );
        map.view.get().fit( envelope, null );
    }


    @Override
    protected void createStyleControls( Composite parent ) {
        // TODO Auto-generated method stub

    }
}
