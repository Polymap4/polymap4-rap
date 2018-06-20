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

import org.eclipse.swt.widgets.Composite;
import org.polymap.rap.openlayers.base.OlEventListener;
import org.polymap.rap.openlayers.base.OlMap;
import org.polymap.rap.openlayers.base.OlMap.ClickEventPayload;
import org.polymap.rap.openlayers.types.Coordinate;
import org.polymap.rap.openlayers.types.Pixel;

/**
 * 
 * @author <a href="http://stundzig.it">Steffen Stundzig</a>
 */
public class ClickControlTab
        extends DemoTab {

    public ClickControlTab() {
        super( "ClickControl" );
    }


    @Override
    protected void createDemoControls( Composite parent ) {
        OlMap map = defaultMap( parent );
        map.addEventListener( OlMap.Event.CLICK, (OlEventListener)ev -> {
            ClickEventPayload.findIn( ev ).ifPresent( payload -> {
                Pixel pixel = payload.pixel();
                Coordinate coord = payload.coordinate();
                StatusBar.getInstance().addInfo( parent,
                        String.format( "%s - pixel clicked: (x=%s, y=%s) => coordinate=(x=%s, y=%s)", name(),
                                pixel.x, pixel.y, coord.x, coord.y ) );                
            });
        }, new OlMap.ClickEventPayload() );
    }


    @Override
    protected void createStyleControls( Composite parent ) {
    }
}
