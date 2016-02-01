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

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.application.AbstractEntryPoint;
import org.eclipse.rap.rwt.client.service.BrowserNavigation;
import org.eclipse.rap.rwt.service.ServerPushSession;
import org.eclipse.rap.rwt.widgets.ClusteredSynchronizer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

/**
 * 
 * @author <a href="http://stundzig.it">Steffen Stundzig</a>
 *
 */
public class DemoEntryPoint
        extends AbstractEntryPoint {

    private final static Log log = LogFactory.getLog( DemoEntryPoint2.class );

    // final ServerPushSession pushSession = new ServerPushSession();

    private Tree             menu;

    private Composite        content;


    //
    // private DemoTab currentTab;

    @Override
    protected void createContents( Composite parent ) {
        // pushSession.start();
        // parent.addDisposeListener( e -> {
        // pushSession.stop();
        // } );
        parent.setLayout( new GridLayout( 2, false ) );
        Color backgroundColor = new Color( parent.getDisplay(), 0x31, 0x61, 0x9C );
        Composite header = new Composite( parent, SWT.NONE );
        header.setBackground( backgroundColor );
        header.setBackgroundMode( SWT.INHERIT_DEFAULT );
        header.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false, 2, 1 ) );
        Label label = new Label( header, SWT.NONE );
        label.setText( "Polymap OpenLayers RAP Demo" );
        label.setForeground( parent.getDisplay().getSystemColor( SWT.COLOR_WHITE ) );
        label.setBounds( 40, 30, 250, 30 );

        menu = new Tree( parent, SWT.FULL_SELECTION );
        menu.setLayoutData( new GridData( SWT.NONE, SWT.FILL, false, false, 1, 1 ) );
        content = new Composite( parent, SWT.NONE );
        // content.setLayout( new GridLayout( 1, false ) );
        content.setLayout( new StackLayout() );
        content.setLayoutData( new GridData( SWT.FILL, SWT.FILL, true, true, 1, 1 ) );

        StatusBar status = StatusBar.getInstance().create( parent, SWT.NONE );
        status.addInfo( parent, "Polymap RAP Openlayers Demo started..." );
        // status.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false, 2, 1
        // ) );
        status.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false, 2, 1 ) );

        fillTree( parent );
    }


    private void fillTree( Composite parent ) {
        final Map<String,TreeItem> exampleMap = new HashMap<String,TreeItem>();
        final BrowserNavigation navigation = RWT.getClient().getService( BrowserNavigation.class );
        for (DemoTab tab : createExampleTabs()) {
            TreeItem item = new TreeItem( menu, SWT.NONE );
            item.setText( tab.name() );
            item.setData( tab );
            exampleMap.put( tab.getId(), item );
        }
        menu.addListener( SWT.Selection, event -> {
            DemoTab tab = (DemoTab)event.item.getData();
            selectTab( tab );
            navigation.pushState( tab.getId(), null );
        } );
        navigation.addBrowserNavigationListener( event -> {
            TreeItem item = exampleMap.get( event.getState() );
            if (item != null) {
                menu.select( item );
                menu.showSelection();
                selectTab( (DemoTab)item.getData() );
            }
        } );
        menu.select( menu.getItem( 0 ) );
        selectTab( (DemoTab)menu.getItem( 0 ).getData() );
    }


    private void selectTab( DemoTab tab ) {
        final Composite next = tab.createContents( content );
        StackLayout layout = (StackLayout)content.getLayout();
        layout.topControl = next;
        content.layout( true, true );
    }


    private static DemoTab[] createExampleTabs() {
        return new DemoTab[] { new ClickControlTab(), new DrawInteractionTab(), 
                new ExtentTab(), new FeatureVectorTab(), 
                new LoadingPanelControlTab(), new ResolutionsTab(), 
                new SelectInteractionTab(), new ScaleLineControlTab(), 
                new ZoomControlTab(), new ZoomSliderControlTab() };
    }
}
