package org.polymap.rap.openlayers.base;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.*;

import org.json.JSONArray;
import org.junit.Test;
import org.polymap.rap.openlayers.base.OlEventListener.PayLoad;
import org.polymap.rap.openlayers.base.OlPropertyConcern.Unquoted;
import org.polymap.rap.openlayers.view.View;

public class OlObjectTest
        extends OlObjectBaseTest {

    @Test
    public void testSimpleCreate() {
        OlObject ol = new OlObject( "foo" ) {
        };
        assertFalse( ol.isCreated() );
        String ref = generateReference( ol );
        ol.getJSObjRef();
        assertTrue( ol.isCreated() );

        assertCall( "this.objs['" + ref + "']=new foo({});" );
        assertEquals( "this.objs['" + ref + "']", ol.getJSObjRef() );
        assertEquals( ref, ol.getObjRef() );
    }


    @Test(expected = IllegalArgumentException.class)
    public void testSimpleCreateFail1() {
        OlObject ol = new OlObject( null ) {
        };
        ol.create();
    }


    @Test(expected = IllegalArgumentException.class)
    public void testSimpleCreateFail2() {
        OlObject ol = new OlObject( OlObject.UNKNOWN_CLASSNAME ) {
        };
        ol.create();
    }


    @Test
    public void testSimplecall() {
        OlObject ol = new OlObject( "foo" ) {
        };
        String ref = generateReference( ol );
        ol.call( "doSomething();" );
        assertCall( "this.objs['" + ref + "']=new foo({});" );
        assertCall( "this.obj=this.objs['" + ref + "']; doSomething();" );
    }


    @Test
    public void testSimplecallInt() {
        OlObject ol = new OlObject( "foo" ) {
        };
        String ref = generateReference( ol );
        ol.call( "doSomething", 5 );
        assertCall( "this.objs['" + ref + "']=new foo({});" );
        assertCall( "this.obj=this.objs['" + ref + "']; this.obj.doSomething(5);" );
    }


    @Test
    public void testSimplecallBoolean() {
        OlObject ol = new OlObject( "foo" ) {
        };
        String ref = generateReference( ol );
        ol.call( "doSomething", true );
        assertCall( "this.objs['" + ref + "']=new foo({});" );
        assertCall( "this.obj=this.objs['" + ref + "']; this.obj.doSomething(true);" );
    }


    @Test
    public void testSimplecallBoolean2() {
        OlObject ol = new OlObject( "foo" ) {
        };
        String ref = generateReference( ol );
        ol.call( "doSomething", Boolean.FALSE );
        assertCall( "this.objs['" + ref + "']=new foo({});" );
        assertCall( "this.obj=this.objs['" + ref + "']; this.obj.doSomething(false);" );
    }


    @Test
    public void testSimplecallDouble() {
        OlObject ol = new OlObject( "foo" ) {
        };
        String ref = generateReference( ol );
        ol.call( "doSomething", 5.0 );
        assertCall( "this.objs['" + ref + "']=new foo({});" );
        assertCall( "this.obj=this.objs['" + ref + "']; this.obj.doSomething(5.0);" );
    }


    @Test
    public void testSimplecallString() {
        OlObject ol = new OlObject( "foo" ) {
        };
        String ref = generateReference( ol );
        ol.call( "doSomething", "bar" );
        assertCall( "this.objs['" + ref + "']=new foo({});" );
        assertCall( "this.obj=this.objs['" + ref + "']; this.obj.doSomething('bar');" );
    }


    @Test
    public void testSimplecallOlObject() {
        OlObject ol = new OlObject( "foo" ) {
        };
        String ref = generateReference( ol );

        OlObject ol2 = new OlObject( "bar" ) {
        };
        String ref2 = generateReference( ol2 );

        ol.call( "add", ol2 );
        assertCall( "this.objs['" + ref2 + "']=new bar({});" );
        assertCall( "this.objs['" + ref + "']=new foo({});" );
        assertCall( "this.obj=this.objs['" + ref + "']; this.obj.add(this.objs['" + ref2 + "']);" );
    }


    @Test(expected = IllegalArgumentException.class)
    public void testSimplecallUnknownObject() {
        OlObject ol = new OlObject( "foo" ) {
        };
        ol.call( "add", new Object() );
    }


    @Test
    public void testSetAttributeString() {
        OlObject ol = new OlObject( "foo" ) {
        };
        String ref = generateReference( ol );

        ol.setAttribute( "key", "value" );
        assertCall( "this.objs['" + ref + "']=new foo({});" );
        assertCall( "this.obj=this.objs['" + ref + "']; this.obj.set('key','value');" );
    }


    @Test
    public void testSetAttributeEnum() {
        OlObject ol = new OlObject( "foo" ) {
        };
        String ref = generateReference( ol );

        ol.setAttribute( "key", View.Event.center );
        assertCall( "this.objs['" + ref + "']=new foo({});" );
        assertCall( "this.obj=this.objs['" + ref + "']; this.obj.set('key','center');" );
    }


    @Test
    public void testSetAttributeUnquoted() {
        OlObject ol = new OlObject( "foo" ) {
        };
        String ref = generateReference( ol );

        Unquoted un = new Unquoted( "<foo>" );
        ol.setAttribute( "key", un );
        assertCall( "this.objs['" + ref + "']=new foo({});" );
        assertCall( "this.obj=this.objs['" + ref + "']; this.obj.set('key',<foo>);" );
    }


    @Test
    public void testSetAttributeJsonArray() {
        OlObject ol = new OlObject( "foo" ) {
        };
        String ref = generateReference( ol );
        JSONArray arr = new JSONArray();
        arr.put( "one" );
        arr.put( 2.0f );
        arr.put( true );
        ol.setAttribute( "key", arr );
        assertCall( "this.objs['" + ref + "']=new foo({});" );
        assertCall( "this.obj=this.objs['" + ref + "']; this.obj.set('key',[\"one\",2,true]);" );
    }


    @Test
    public void testEventListener() {
        OlObject ol = new OlObject( "foo" ) {
        };
        ol.setOsh( osh );
        OlEventListener listener = mock( OlEventListener.class );
        PayLoad payload = new PayLoad();
        payload.add( "p1_key", "p1_value" );

        assertEquals( 1, payload.values().size() );
        assertEquals( "p1_key", payload.values().get( 0 ).key() );
        assertEquals( "p1_value", payload.values().get( 0 ).value() );

        ol.addEventListener( "event", listener, payload );

        verify( osh ).registerEventListener( ol, "event", listener, payload );

        OlEvent event = new OlEvent( ol, "event", null );
        assertEquals( ol, event.getSource() );
        assertEquals( "event", event.name() );
        assertNull( event.properties() );

        ol.handleEvent( event );
        ol.handleEvent( event );
        ol.handleEvent( event );

        ol.removeEventListener( "event", listener );
        verify( osh ).unregisterEventListener( ol, "event", listener );

        ol.handleEvent( event );
        ol.handleEvent( event );

        verify( listener, times( 3 ) ).handleEvent( event );

    }
}
