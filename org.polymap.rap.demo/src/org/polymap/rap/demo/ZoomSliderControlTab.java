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
import org.eclipse.swt.widgets.Composite;
import org.polymap.rap.openlayers.base.OlMap;
import org.polymap.rap.openlayers.control.ZoomSliderControl;

/**
 * 
 * @author <a href="http://stundzig.it">Steffen Stundzig</a>
 *
 */
public class ZoomSliderControlTab
        extends DemoTab {


    public ZoomSliderControlTab() {
        super( "ZoomSliderControl" );
    }

    private final static Log log = LogFactory.getLog( ZoomSliderControlTab.class );


    @Override
    protected void createDemoControls( Composite parent ) {
        OlMap map = defaultMap( parent );

        map.addControl( new ZoomSliderControl() );
    }



    @Override
    protected void createStyleControls( Composite parent ) {
        // TODO Auto-generated method stub

    }
}
