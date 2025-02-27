package it.polimi.ingsw.am43.ModelTests.CardTests;

import static org.junit.jupiter.api.Assertions.*;

import it.polimi.ingsw.am43.Model.Cards.Corner;
import it.polimi.ingsw.am43.Model.Enum.Symbol;
import org.junit.Test;

public class CornerTest {

    @Test
    public void testConstructor() {
        Symbol symbol = Symbol.ANIMAL;
        Corner corner = new Corner(symbol);
        assertEquals(symbol, corner.getSymbol());
    }

    @Test
    public void testIsResource() {
        Symbol symbol = Symbol.FUNGI;
        assertTrue(symbol.isResource());
        Symbol notRes = Symbol.INKWELL;
        assertFalse(notRes.isResource());
    }
}
