package org.polymap.rap.openlayers.base;

import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

@RunWith(MockitoJUnitRunner.class)
public abstract class OlObjectBaseTest {

    @Mock
    protected OlSessionHandler osh;

    private InOrder            inOrder;


    @Before
    public void setUp() {
        inOrder = inOrder( osh );
    }


    protected String generateReference( OlObject ol ) {
        ol.setOsh( osh );
        String ref = "ol" + ol.hashCode();
        when( osh.newReference( ol ) ).thenReturn( ref );
        doAnswer( new Answer<Void>() {

            @Override
            public Void answer( InvocationOnMock invocation ) throws Throwable {
                System.out.println( "call: " + invocation.getArgumentAt( 0, OlCommand.class ).getCommand() );
                return null;
            }
        } ).when( osh ).call( anyObject() );

        return ref;
    }


    protected void assertCall( String expected ) {
        inOrder.verify( osh ).call( new OlCommand( expected ) );
    }
}
