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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.rap.json.JsonArray;
import org.eclipse.rap.json.JsonObject;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.polymap.core.runtime.UIThreadExecutor;
import org.polymap.rap.openlayers.base.OlEventListener.PayLoad;
import org.polymap.rap.openlayers.base.OlFeature;
import org.polymap.rap.openlayers.base.OlMap;
import org.polymap.rap.openlayers.format.GeoJSONFormat;
import org.polymap.rap.openlayers.geom.PointGeometry;
import org.polymap.rap.openlayers.layer.Layer;
import org.polymap.rap.openlayers.layer.VectorLayer;
import org.polymap.rap.openlayers.source.VectorSource;
import org.polymap.rap.openlayers.style.CircleStyle;
import org.polymap.rap.openlayers.style.FillStyle;
import org.polymap.rap.openlayers.style.StrokeStyle;
import org.polymap.rap.openlayers.style.Style;
import org.polymap.rap.openlayers.types.Attribution;
import org.polymap.rap.openlayers.types.Color;
import org.polymap.rap.openlayers.types.Coordinate;

import com.google.common.collect.Lists;

/**
 * 
 * @author <a href="http://stundzig.it">Steffen Stundzig</a>
 *
 */
public class SelectInteractionTab
        extends DemoTab {

    private OlMap map;


    public SelectInteractionTab() {
        super( "SelectInteraction" );
    }

    private final static Log                   log              = LogFactory.getLog( SelectInteractionTab.class );

    private Map<Pair<Double,Double>,Boolean>   selected         = new HashMap<Pair<Double,Double>,Boolean>();

    private Map<Pair<Double,Double>,OlFeature> coords           = new HashMap<Pair<Double,Double>,OlFeature>();

    private Layer<VectorSource>                vector;

    private TableViewer                        tableViewer;

    private Layer<VectorSource>                vectorSelected;

    private ISelectionChangedListener selectionListener;


    @Override
    protected void createDemoControls( Composite parent ) {
        map = defaultMap( parent );

        VectorSource source = new VectorSource().format.put( new GeoJSONFormat() ).attributions.put( Arrays
                .asList( new Attribution( "Steffen Stundzig" ) ) );

        vector = new VectorLayer().style.put( new Style().fill.put( new FillStyle().color.put( new Color( 0, 0, 255,
                0.1f ) ) ).stroke.put( new StrokeStyle().color.put( new Color( "red" ) ).width.put( 1f ) ) ).source
                .put( source );

        OlFeature olFeature1 = new OlFeature();
        olFeature1.id.set( "Test1" );
        olFeature1.name.set( "Test1" );
        Coordinate coord1 = map.view.get().center.get();
        double coord1X = ((Double)((org.json.JSONArray)map.view.get().center.get().toJson()).get( 0 )); 
        double coord1Y = ((Double)((org.json.JSONArray)map.view.get().center.get().toJson()).get( 1 ));
        coords.put( Pair.of( coord1X, coord1Y ), olFeature1 );
        olFeature1.geometry.set( new PointGeometry( coord1 ) );
        olFeature1.style.put( new Style().stroke.put( new StrokeStyle().color.put( new Color( "green" ) ).width
                .put( 2f ) ).image.put( new CircleStyle( 5.0f ).fill.put( new FillStyle().color
                .put( new Color( "red" ) ) ) ) );
        source.addFeature( olFeature1 );

        OlFeature olFeature2 = new OlFeature();
        olFeature2.id.set( "Test2" );
        olFeature2.name.set( "Test2" );
        double coord2X = ((Double)((org.json.JSONArray)map.view.get().center.get().toJson()).get( 0 )) + 1000; 
        double coord2Y = ((Double)((org.json.JSONArray)map.view.get().center.get().toJson()).get( 1 )) + 1000;
        Coordinate coord2 = new Coordinate(coord2X, coord2Y);
        coords.put( Pair.of( coord2X, coord2Y ), olFeature2 );
        olFeature2.geometry.set( new PointGeometry( coord2 ) );
        olFeature2.style.put( new Style().stroke.put( new StrokeStyle().color.put( new Color( "green" ) ).width
                .put( 2f ) ).image.put( new CircleStyle( 5.0f ).fill.put( new FillStyle().color
                .put( new Color( "red" ) ) ) ) );
        source.addFeature( olFeature2 );

        map.addLayer( vector );

        PayLoad payload = new PayLoad();
        payload.add( "feature", "{}" );
        payload.add( "feature.pixel", "theEvent.pixel" );
        payload.add( "feature.coordinate", map.getJSObjRef().replace( "this.objs", "that.objs" )
                + ".getCoordinateFromPixel(theEvent.pixel)" );
        
        map.addEventListener( OlMap.Event.click, event -> {
            JsonObject json = event.properties();
            JsonObject feature = (JsonObject)json.get( "feature" );
            JsonArray coordinate = (JsonArray)feature.get( "coordinate" );
            handleSelectionEvent( coordinate.get( 0 ).asDouble(), coordinate.get( 1 ).asDouble(), true );
        });
        
        selectionListener = new ISelectionChangedListener() {

            @Override
            public void selectionChanged( SelectionChangedEvent event ) {
                IStructuredSelection selection = (IStructuredSelection)event.getSelection();
                Map.Entry<Pair<Double,Double>,OlFeature> first = (Map.Entry<Pair<Double,Double>,OlFeature>)selection
                        .getFirstElement();
                if(first != null) {
                    final List<String> fids = new ArrayList<String>();
                    fids.add( first.getValue().id.get() );
                    Pair<Double,Double> coord = first.getKey();
                    UIThreadExecutor.async( ( ) -> handleSelectionEvent( coord.getLeft(), coord.getRight(), false ),
                            UIThreadExecutor.logErrorMsg( "" ) );
                }
            }
        };
    }


    private void handleSelectionEvent( double x, double y, boolean selectionFromMap ) {
        double eX, eY;
        for (Entry<Pair<Double,Double>,OlFeature> entry : coords.entrySet()) {
            eX = entry.getKey().getLeft();
            eY = entry.getKey().getRight();

            if (vectorSelected != null) {
                map.removeLayer( vectorSelected );
                vectorSelected = null;
                selected.clear();
                tableViewer.setSelection( new StructuredSelection( Lists.newArrayList() ) );
            }

            if (Math.abs( x - eX ) <= 500
                    && Math.abs( y - eY ) <= 500) {
                if (!selected.containsKey( entry.getKey() ) || !selected.get( entry.getKey() )) {

                    VectorSource sourceSelected = new VectorSource().format.put( new GeoJSONFormat() ).attributions
                            .put( Arrays.asList( new Attribution( "Steffen Stundzig" ) ) );

                    vectorSelected = new VectorLayer().style.put( new Style().fill.put( new FillStyle().color
                            .put( new Color( 0, 0, 255, 0.1f ) ) ).stroke.put( new StrokeStyle().color.put( new Color(
                            "red" ) ).width.put( 1f ) ) ).source.put( sourceSelected );

                    OlFeature olFeatureSelected = new OlFeature();
                    olFeatureSelected.id.set( "Test1Selected" );
                    olFeatureSelected.name.set( "Test1" );
                    olFeatureSelected.geometry.set( new PointGeometry( new Coordinate(eX, eY) ) );
                    olFeatureSelected.style.put( new Style().stroke.put( new StrokeStyle().color.put( new Color(
                            "green" ) ).width.put( 2f ) ).image.put( new CircleStyle( 5.0f ).fill
                            .put( new FillStyle().color.put( new Color( "blue" ) ) ) ) );
                    sourceSelected.addFeature( olFeatureSelected );

                    map.addLayer( vectorSelected );

                    selected.put( entry.getKey(), true );
                    if(selectionFromMap) {
                        UIThreadExecutor.async( ( ) -> {
                            tableViewer.removeSelectionChangedListener( selectionListener );
                            List<Map.Entry<Pair<Double,Double>,OlFeature>> elements = new ArrayList<Map.Entry<Pair<Double,Double>,OlFeature>>();
                            elements.add( entry );
                            tableViewer.setSelection( new StructuredSelection( elements ), true );
                            tableViewer.addSelectionChangedListener( selectionListener );
                        }, UIThreadExecutor.logErrorMsg( "" ) );
                    }
                    return;
                }
            }
        }
    }


    @Override
    protected void createStyleControls( Composite parent ) {
        Composite container = new Composite( parent, SWT.NONE );
        container.setLayout( new GridLayout( 1, false ) );
        Composite composite = new Composite( container, SWT.NONE );
        composite.setLayoutData( new GridData( SWT.FILL, SWT.FILL, true, true, 1, 1 ) );
        TableColumnLayout layout = new TableColumnLayout();
        composite.setLayout( layout );

        tableViewer = new TableViewer( composite, SWT.BORDER | SWT.FULL_SELECTION );

        TableViewerColumn tableViewerColumn1 = new TableViewerColumn( tableViewer, SWT.NONE );
        TableColumn tableColumn1 = tableViewerColumn1.getColumn();
        tableColumn1.setText( "Longitude" );
        layout.setColumnData( tableColumn1, new ColumnWeightData( 2, 100, true ) );
        tableViewerColumn1.setLabelProvider( new ColumnLabelProvider() {

            @Override
            public String getText( Object element ) {
                Map.Entry<Pair<Double,Double>,OlFeature> entry = (Map.Entry<Pair<Double,Double>,OlFeature>)element;
                return String.valueOf( entry.getKey().getLeft());
            }
        } );

        TableViewerColumn tableViewerColumn2 = new TableViewerColumn( tableViewer, SWT.NONE );
        TableColumn tableColumn2 = tableViewerColumn2.getColumn();
        layout.setColumnData( tableColumn2, new ColumnWeightData( 2, 100, true ) );
        tableColumn2.setText( "Latitude" );
        tableViewerColumn2.setLabelProvider( new ColumnLabelProvider() {

            @Override
            public String getText( Object element ) {
                Map.Entry<Pair<Double,Double>,OlFeature> entry = (Map.Entry<Pair<Double,Double>,OlFeature>)element;
                return String.valueOf( entry.getKey().getRight());
            }
        } );

        TableViewerColumn tableViewerColumn3 = new TableViewerColumn( tableViewer, SWT.NONE );
        TableColumn tableColumn3 = tableViewerColumn3.getColumn();
        layout.setColumnData( tableColumn3, new ColumnWeightData( 2, 100, true ) );
        tableColumn3.setText( "Name" );
        tableViewerColumn3.setLabelProvider( new ColumnLabelProvider() {

            @Override
            public String getText( Object element ) {
                Map.Entry<Pair<Double,Double>,OlFeature> entry = (Map.Entry<Pair<Double,Double>,OlFeature>)element;
                return entry.getValue().name.get();
            }
        } );

        Table table = tableViewer.getTable();
        table.setHeaderVisible( true );
        table.setLinesVisible( true );

        tableViewer.setContentProvider( new ArrayContentProvider() );
        tableViewer.setInput( coords.entrySet().toArray() );
        tableViewer.addSelectionChangedListener( selectionListener );
    }
}
