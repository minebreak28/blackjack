package test;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;

import org.junit.Test;

import bj.BlackjackBasicStrategy;

public class blackjackTest {

    @Test
    public void testCalculateHandValue () throws Exception {
        final ArrayList<String> hand1 = new ArrayList<>( Arrays.asList( "Ace of Hearts", "Ace of Spades" ) );
        final ArrayList<String> hand2 = new ArrayList<>( Arrays.asList( "Ace of Hearts", "King of Spades" ) );
        final ArrayList<String> hand3 = new ArrayList<>( Arrays.asList( "2 of Hearts", "10 of Spades" ) );
        final ArrayList<String> hand4 = new ArrayList<>(
                Arrays.asList( "3 of Hearts", "Ace of Spades", "Ace of Spades", "King of Clubs" ) );
        final ArrayList<String> hand5 = new ArrayList<>(
                Arrays.asList( "8 of Hearts", "8 of Spades", "7 of Diamonds" ) );
        final String card = "Ace of Spades";
        final String card2 = "6 of Spades";
        final String card3 = "10 of Spades";
        final String card4 = "Jack of Spades";

        assertEquals( 12, BlackjackBasicStrategy.calculateHandValue( hand1 ) );
        assertEquals( 21, BlackjackBasicStrategy.calculateHandValue( hand2 ) );
        assertEquals( 12, BlackjackBasicStrategy.calculateHandValue( hand3 ) );
        assertEquals( 15, BlackjackBasicStrategy.calculateHandValue( hand4 ) );
        assertEquals( 23, BlackjackBasicStrategy.calculateHandValue( hand5 ) );

        assertEquals( 11, BlackjackBasicStrategy.calculateCardValue( card ) );
        assertEquals( 6, BlackjackBasicStrategy.calculateCardValue( card2 ) );
        assertEquals( 10, BlackjackBasicStrategy.calculateCardValue( card3 ) );
        assertEquals( 10, BlackjackBasicStrategy.calculateCardValue( card4 ) );
    }

    @Test
    public void testBJDecision () throws Exception {

    }

}
