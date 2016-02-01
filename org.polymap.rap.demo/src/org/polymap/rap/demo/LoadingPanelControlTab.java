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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Text;
import org.polymap.core.runtime.UIThreadExecutor;
import org.polymap.rap.openlayers.base.OlMap;
import org.polymap.rap.openlayers.control.LoadingPanel;
import org.polymap.rap.openlayers.control.LoadingPanel.ProgressModeType;
import org.polymap.rap.openlayers.control.LoadingPanel.WidgetType;
import org.polymap.rap.openlayers.layer.Layer;
import org.polymap.rap.openlayers.layer.TileLayer;
import org.polymap.rap.openlayers.source.MapQuestSource;
import org.polymap.rap.openlayers.source.TileSource;
import org.polymap.rap.openlayers.types.Coordinate;
import org.polymap.rap.openlayers.types.Projection;
import org.polymap.rap.openlayers.types.Projection.Units;
import org.polymap.rap.openlayers.view.View;

/**
 * 
 * @author <a href="http://stundzig.it">Steffen Stundzig</a>
 *
 */
public class LoadingPanelControlTab
        extends DemoTab {

    private OlMap map;


    public LoadingPanelControlTab() {
        super( "LoadingPanelControl" );
    }

    private final static Log log        = LogFactory.getLog( LoadingPanelControlTab.class );

    private LoadingPanel     control;

    private boolean          loading    = false;

    private int              tilesStart = 0;

    private int              tilesEnd   = 0;

    private ProgressBar      progressBar;

    private Text             text;


    @Override
    protected void createDemoControls( Composite parent ) {
        map = new OlMap( parent, SWT.MULTI | SWT.WRAP | SWT.BORDER,
                new View().projection.put( new Projection( "EPSG:3857", Units.m ) ).zoom
                        .put( 12 ).center.put( new Coordinate( 1401845.7269824906, 6666952.61751981 ) ) );
        Layer<TileSource> layer = new TileLayer().source.put( new MapQuestSource( MapQuestSource.Type.osm ) );
        map.addLayer( layer );
        control = new LoadingPanel( WidgetType.PROGRESS_BAR, ProgressModeType.TILE );
        map.addControl( control );
        layer.source.get().addEventListener( TileLayer.TileEvent.LOAD_START, event -> {
            if(!loading) {
                loading = true;
            }
            tilesStart += 1;
            if (control.progressMode.get().equals( "tile" )) {
                if ("progressbar".equals( control.widget.get() )) {
                    show();
                }
            }
        } );
        layer.source.get().addEventListener( TileLayer.TileEvent.LOAD_END, event -> {
            tilesEnd += 1;
            show();
            if(tilesEnd == tilesStart) {
                loading = false;
//                tilesStart = 0;
//                tilesEnd = 0;
            }
        } );
    }


    private int progress() {
        return tilesEnd == 0 ? 0 : new Double( tilesStart / tilesEnd ).intValue();
    }


    public void show() {
        UIThreadExecutor.async( () -> {
            int progress = this.progress() * 100;
            if (control.progressMode.get().equals( "tile" )) {
                String currentProgressStr = String.valueOf( progress ) + " %";
                text.setText( currentProgressStr );
                if ("progressbar".equals( control.widget.get() )) {
                    progressBar.setSelection( progress );
                }
            }
        } , e -> {
        } );
    }


    @Override
    protected void createStyleControls( Composite parent ) {
        parent.setLayout( new GridLayout( 2, false ) );
        GridData data1 = new GridData();
        data1.minimumWidth = 300;
        data1.horizontalAlignment = SWT.BEGINNING;
        progressBar = new ProgressBar( parent, SWT.BORDER );
        progressBar.setMinimum( 0 );
        progressBar.setMinimum( 100 );
        progressBar.setSelection( 0 );
        progressBar.setLayoutData( data1 );
        GridData data2 = new GridData();
        data2.minimumWidth = 100;
        data2.grabExcessHorizontalSpace = true;
        data2.horizontalAlignment = SWT.FILL;
        text = new Text( parent, SWT.NONE );
        text.setText( "Hello" );
        text.setLayoutData( data2 );
    }
}
