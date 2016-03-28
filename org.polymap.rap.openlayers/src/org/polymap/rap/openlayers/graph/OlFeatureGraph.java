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
package org.polymap.rap.openlayers.graph;

import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;

import org.eclipse.swt.widgets.Display;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.MultiGraph;
import org.graphstream.stream.ProxyPipe;
import org.graphstream.stream.SinkAdapter;
import org.graphstream.ui.geom.Point3;
import org.graphstream.ui.layout.Layout;
import org.graphstream.ui.layout.springbox.implementations.SpringBox;
import org.graphstream.ui.view.Viewer;
import org.polymap.rap.openlayers.base.OlFeature;
import org.polymap.rap.openlayers.base.OlMap;
import org.polymap.rap.openlayers.geom.LineStringGeometry;
import org.polymap.rap.openlayers.geom.PointGeometry;
import org.polymap.rap.openlayers.source.VectorSource;
import org.polymap.rap.openlayers.style.StrokeStyle;
import org.polymap.rap.openlayers.style.StyleContainer;
import org.polymap.rap.openlayers.types.Color;
import org.polymap.rap.openlayers.types.Coordinate;
import org.polymap.rap.openlayers.types.Extent;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * Wraps a graph stream implementation to work easily with OlFeatures. Simple
 * construct a VectorSource in your map. After that create your OlFeatures and the
 * edges between them, by simply adding them to this class.
 * 
 * @author <a href="http://stundzig.it">Steffen Stundzig</a>
 */
public class OlFeatureGraph
        extends SinkAdapter {

    private static final double         GRAPHUNIT2COORD = 10000.0;

    private final VectorSource          vector;

    private final Map<String,OlFeature> nodes           = Maps.newHashMap();

    private final Map<String,OlFeature> edges           = Maps.newHashMap();

    private final OlMap                 map;

    private Graph                       graph;


    /**
     * Creates a graph which updates automatically all features in the vector source
     * and updates also the optimal extent in the map.
     * 
     * @param vector
     * @param map
     */
    public OlFeatureGraph( final VectorSource vector, final OlMap map ) {
        this.vector = vector;
        this.map = map;
    }


    /**
     * Adds or updates the feature to the graph with a default weight of 1.
     * 
     * @param feature
     */
    public void addOrUpdateNode( final OlFeature feature ) {
        addOrUpdateNode( feature, 1 );
    }


    /**
     * Adds or updates the feature to the graph with a specified weight.
     * 
     * @param feature
     * @param weight should be greater than 0
     */
    public void addOrUpdateNode( final OlFeature feature, int weight ) {
        if (!nodes.containsKey( feature.id.get() )) {
            nodes.put( feature.id.get(), feature );
            getInternalGraph().addNode( feature.id.get() );
            if (feature.geometry.get() == null
                    || !PointGeometry.class.isAssignableFrom( feature.geometry.get().getClass() )) {
                Coordinate coordinate = new Coordinate( 0.0, 0.0 );
                feature.geometry.set( new PointGeometry( coordinate ) );
            }
            vector.addFeature( feature );
        }
        getInternalGraph().getNode( feature.id.get() ).addAttribute( "layout.weight", weight );
    }


    /**
     * Creates or updates an undirected edge between both features with a default
     * weight of 1 and a default stroke style with a black line and zIndex 0.
     * 
     * If an edge is added twice, than the weight of both are added.
     * 
     * @param src
     * @param target
     */
    public void addOrUpdateEdge( final OlFeature src, final OlFeature target ) {
        addOrUpdateEdge( src, target,
                new StyleContainer().stroke.put( new StrokeStyle().color.put( new Color( "black" ) ).width.put( 2f ) ).zIndex
                        .put( 0f ),
                1 );
    }


    /**
     * Creates or updates an undirected edge between both features with the specified
     * weight and the specified style.
     * 
     * If an edge is added twice, than the weight of both are added.
     * 
     * The id of the edge is constructed with src.id + "_" + target.id
     * 
     * @param src
     * @param target
     * @param style
     * @param weight
     */
    public void addOrUpdateEdge( final OlFeature src, final OlFeature target, final StyleContainer style, int weight ) {
        final String edgeId = src.id.get() + "_" + target.id.get();
        OlFeature line = edges.get( edgeId );
        if (line == null) {
            line = new OlFeature( edgeId );
            line.geometry.set( new LineStringGeometry( ((PointGeometry)src.geometry.get()).coordinate.get(),
                    ((PointGeometry)target.geometry.get()).coordinate.get() ) );
            line.style.put( style );
            edges.put( edgeId, line );
            getInternalGraph().addEdge( edgeId, src.id.get(), target.id.get() );
            vector.addFeature( line );
            getInternalGraph().getEdge( edgeId ).addAttribute( "layout.weight", weight );
        }
        else {
            weight += getInternalGraph().getEdge( edgeId ).getAttribute( "layout.weight", Integer.class );
            getInternalGraph().getEdge( edgeId ).setAttribute( "layout.weight", weight );
        }
    }


    private Graph getInternalGraph() {
        if (graph == null) {
            graph = new MultiGraph( "" );
            Viewer viewer = new Viewer( graph, Viewer.ThreadingModel.GRAPH_IN_ANOTHER_THREAD );
            Layout layout = new SpringBox( false );// Eades84Layout();//HierarchicalLayout();//
                                                   // //new
                                                   // SpringBox( false );//
                                                   // LinLog(false);//.newLayoutAlgorithm();
            viewer.enableAutoLayout( layout );
            final Display display = Display.getCurrent();
            Executors.newSingleThreadExecutor().execute( () -> {
                readCoordinates( viewer, display );
            } );
        }
        return graph;
    }

    private Map<String,List<Double>> nodeCoordinates;


    private void readCoordinates( Viewer viewer, Display display ) {
        final ProxyPipe pipe = viewer.newViewerPipe();
        pipe.addAttributeSink( this );
        // layout.addAttributeSink( this );
        try {
            while (true) {
                nodeCoordinates = Maps.newHashMap();
                pipe.blockingPump();
                display.asyncExec( () -> {
                    nodeCoordinates.entrySet().forEach( entry -> {
                        Coordinate newCoordinate = new Coordinate( entry.getValue().get( 0 ) * GRAPHUNIT2COORD,
                                entry.getValue().get( 1 ) * GRAPHUNIT2COORD );
                        final OlFeature olFeature = nodes.get( entry.getKey() );
                        ((PointGeometry)olFeature.geometry.get()).coordinate.set( newCoordinate );
                        Node node = getInternalGraph().getNode( entry.getKey() );
                        node.getEachEdge().forEach( edge -> {
                            OlFeature line = edges.get( edge.getId() );
                            LineStringGeometry geometry = ((LineStringGeometry)line.geometry.get());
                            List<Coordinate> coordinates = geometry.coordinates.get();
                            coordinates.set( (edge.getId().startsWith( node.getId() )) ? 0 : 1, newCoordinate );
                            geometry.coordinates.set( coordinates );
                        } );
                    } );
                    // reset the map view to the max extent of the required drawing
                    // area
                    Point3 max = viewer.getGraphicGraph().getMaxPos();
                    Point3 min = viewer.getGraphicGraph().getMinPos();
                    Extent envelope = new Extent( min.x * GRAPHUNIT2COORD, min.y * GRAPHUNIT2COORD,
                            max.x * GRAPHUNIT2COORD, max.y * GRAPHUNIT2COORD );
                    map.view.get().fit( envelope, null );
                } );
                Thread.currentThread().sleep( 100 );
            }
        }
        catch (Exception e) {
            throw new RuntimeException( e );
        }
    }


    @Override
    public void nodeAttributeChanged( String sourceId, long timeId, String nodeId, String attribute, Object oldValue,
            Object newValue ) {
        if ("xyz".equals( attribute )) {
            List<Double> coordinate;
            try {
                // sometimes it's a Object[] or a Double[]
                Object[] position = (Object[])newValue;
                coordinate = Lists.newArrayList();
                coordinate.add( (Double)position[0] );
                coordinate.add( (Double)position[1] );
                coordinate.add( (Double)position[2] );
            }
            catch (ClassCastException cce) {
                try {
                    double[] position = (double[])newValue;
                    coordinate = Lists.newArrayList();
                    coordinate.add( position[0] );
                    coordinate.add( position[1] );
                    coordinate.add( position[2] );
                }
                catch (ClassCastException cce2) {
                    coordinate = Lists.newArrayList( (Double[])newValue );
                }
            }
            nodeCoordinates.put( nodeId, coordinate );
            // System.out.println( "node: " + nodeId + "@" + coordinate.get( 0 ) +
            // ":"
            // + coordinate.get( 1 ) + ":" + coordinate.get( 2 ) );
        }
    }
}