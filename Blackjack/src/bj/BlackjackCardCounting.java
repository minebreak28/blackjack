package bj;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * Blackjack using basic strategy, but also implements Hi-Lo system for card
 * counting. The bet amount changes based on the current count, where a 2-6
 * increases the count by one, 7-9 does not affect the count, and 10-A decrease
 * the count by one.
 *
 * @author Adam Wu
 *
 */
public class BlackjackCardCounting {

    private static double            playerProfit = 0;
    private static int               playerBets   = 0;
    private static int               dealerWins   = 0;
    private static int               playerWins   = 0;
    private static int               push         = 0;
    private static int               shoeIndex    = 0;
    private static ArrayList<String> shoe;
    private static int               runningCount = 0;

    private static double[][]        results      = new double[1000][5];

    /**
     * Initializes a shoe of 8 decks of cards, each containing 52 individual
     * cards, and then shuffling randomly
     *
     * @return a string array of the shuffled shoe
     */
    private static ArrayList<String> initializeShoe () {
        final ArrayList<String> deck = new ArrayList<String>( 52 * 8 );
        int index = 0;
        final String[] suits = { "Spades", "Hearts", "Diamonds", "Clubs" };
        final String[] ranks = { "Ace", "2", "3", "4", "5", "6", "7", "8", "9", "10", "Jack", "Queen", "King" };
        for ( int i = 0; i < 8; i++ ) {
            for ( final String suit : suits ) {
                for ( final String rank : ranks ) {
                    deck.add( index, rank + " of " + suit );
                    index++;
                }
            }
        }

        final ArrayList<String> cardList = new ArrayList<String>();
        for ( final String card : deck ) {
            cardList.add( card );
        }
        Collections.shuffle( cardList );
        return cardList;
    }

    /**
     * Calculates the value of a hand
     *
     * @param hand
     *            hand to be calculated
     * @return integer value of hand value
     */
    public static int calculateHandValue ( final ArrayList<String> hand ) {
        int value = 0;
        int aces = 0;
        // Calculate the value of the non-Ace cards in the hand
        for ( final String card : hand ) {
            final String rank = card.split( " " )[0];
            if ( rank.equals( "Ace" ) ) {
                aces++;
            }
            else if ( rank.equals( "King" ) || rank.equals( "Queen" ) || rank.equals( "Jack" ) ) {
                value += 10;
            }
            else {
                value += Integer.parseInt( rank );
            }
        }
        // Calculate the value of the Ace cards in the hand
        for ( int i = 0; i < aces; i++ ) {
            if ( value + 11 <= 21 ) {
                value += 11;
            }
            else {
                value += 1;
            }
        }

        return value;
    }

    /**
     * Calculates the value of a hand
     *
     * @param hand
     *            hand to be calculated
     * @return integer value of hand value
     */
    public static int calculateCardValue ( final String hand ) {

        final String rank = hand.split( " " )[0];
        if ( rank.equals( "Ace" ) ) {
            return 11;
        }
        else if ( rank.equals( "King" ) || rank.equals( "Queen" ) || rank.equals( "Jack" ) ) {
            return 10;
        }
        else {
            return Integer.parseInt( rank );
        }

    }

    /**
     * Checks to see if player and/or dealer has blackjack
     *
     * @param playerValue
     *            value of a player's hand
     * @param dealerValue
     *            value of the dealer's hand
     * @return true if one or both has blackjack
     */
    public static String checkBlackjack ( final int playerValue, final int dealerValue ) {
        if ( dealerValue == 21 ) {
            if ( playerValue != 21 ) { // Dealer has blackjack, and player does
                                       // not
                // dealer wins
                return "dealer";
            }
            else { // player also has blackjack
                return "push";
            }
        }
        if ( playerValue == 21 ) {// Player has blackjack while dealer does not
            return "player";
        }
        // no blackjack for either dealer or player
        return "no blackjack";
    }

    /**
     * Lets use know whether to hit, stand, or double based on the dealer's
     * upcard
     *
     * @param playerTotal
     *            player's total value
     * @param dealerUpCard
     *            dealer's upcard
     * @return hit, stand, or double
     */
    public static String getBasicStrategyDecision ( final int playerTotal, final int dealerUpCard ) {
        if ( playerTotal <= 8 ) {
            return "hit";
        }
        else if ( playerTotal == 9 ) {
            if ( dealerUpCard >= 3 && dealerUpCard <= 6 ) {
                return "double";
            }
            else {
                return "hit";
            }
        }
        else if ( playerTotal == 10 ) {
            if ( dealerUpCard >= 2 && dealerUpCard <= 9 ) {
                return "double";
            }
            else {
                return "hit";
            }
        }
        else if ( playerTotal == 11 ) {
            return "double";
        }
        else if ( playerTotal == 12 ) {
            if ( dealerUpCard >= 4 && dealerUpCard <= 6 ) {
                return "stand";
            }
            else {
                return "hit";
            }
        }
        else if ( playerTotal >= 13 && playerTotal <= 16 ) {
            if ( dealerUpCard >= 2 && dealerUpCard <= 6 ) {
                return "stand";
            }
            else {
                return "hit";
            }
        }
        else {
            return "stand";
        }
    }

    /**
     * Checks to see if the shoe is empty
     */
    private static void checkShoeIfEmpty () {
        if ( shoeIndex == shoe.size() ) {
            shoe = initializeShoe();
            shoeIndex = 0;
        }
    }

    /**
     * Returns a count based on the card
     *
     */
    private static void changeCount ( String card ) {
        final String rank = card.split( " " )[0];
        if ( rank.equals( "Ace" ) || rank.equals( "King" ) || rank.equals( "Queen" ) || rank.equals( "Jack" )
                || rank.equals( "10" ) ) {
            runningCount++;
        }
        else if ( rank.equals( "2" ) || rank.equals( "3" ) || rank.equals( "4" ) || rank.equals( "5" )
                || rank.equals( "6" ) ) {
            runningCount--;
        }

    }

    /**
     * Calculates bet amount based on true count (running count divided by how
     * many cards are left in the shoe)
     *
     * @return
     */
    public static int calculateBetAmount () {

        int decksLeftInShoe = 8 - ( shoeIndex / 52 ); // How many decks are
                                                      // remaining in the shoe,
                                                      // rounded up.
        int trueCount = runningCount / decksLeftInShoe;

        if ( trueCount <= 1 ) {
            return 1;
        }
        else if ( trueCount == 2 ) {
            return 2;
        }
        else if ( trueCount == 3 ) {
            return 3;
        }
        else if ( trueCount == 4 ) {
            return 4;
        }
        else {
            return 5;
        }

    }

    public static void play1000Hands () {
        shoe = initializeShoe();
        String cardAdded;

        // Plays 1000 hands of blackjack to basic strategy
        for ( int i = 0; i < 1000; i++ ) {
            checkShoeIfEmpty();
            int betAmount = calculateBetAmount();
            playerBets += betAmount;
            // Deal two cards to the player and two cards to the dealer
            final ArrayList<String> playerHand = new ArrayList<>();

            cardAdded = shoe.get( shoeIndex++ );
            changeCount( cardAdded );
            playerHand.add( cardAdded );
            checkShoeIfEmpty();
            cardAdded = shoe.get( shoeIndex++ );
            changeCount( cardAdded );
            playerHand.add( cardAdded );

            final ArrayList<String> dealerHand = new ArrayList<>();
            checkShoeIfEmpty();
            cardAdded = shoe.get( shoeIndex++ );
            changeCount( cardAdded );
            dealerHand.add( cardAdded );
            checkShoeIfEmpty();
            cardAdded = shoe.get( shoeIndex++ );
            changeCount( cardAdded );
            dealerHand.add( cardAdded );

            final int playerValue = calculateHandValue( playerHand );
            final int dealerValue = calculateHandValue( dealerHand );

            // First, check if dealer/player has a blackjack

            if ( checkBlackjack( playerValue, dealerValue ) == "dealer" ) {
                dealerWins++;
                playerProfit -= betAmount;
                continue;
            }
            else if ( checkBlackjack( playerValue, dealerValue ) == "push" ) {
                push++;
                continue;
            }
            else if ( checkBlackjack( playerValue, dealerValue ) == "player" ) {
                playerWins++;
                playerProfit += 1.5 * betAmount;
                continue;
            }

            // At this point, dealer/players dont have blackjack, so we can
            // proceed with the player

            while ( true ) {
                // Get the player's decision

                final String decision = getBasicStrategyDecision( calculateHandValue( playerHand ),
                        calculateCardValue( dealerHand.get( 0 ) ) );

                // Carry out the decision
                if ( decision.equals( "hit" ) ) {
                    checkShoeIfEmpty();
                    cardAdded = shoe.get( shoeIndex++ );
                    changeCount( cardAdded );
                    playerHand.add( cardAdded );

                    if ( calculateHandValue( playerHand ) > 21 ) {
                        dealerWins++;
                        playerProfit -= betAmount;
                        break;
                    }
                }
                else if ( decision.equals( "stand" ) ) {
                    while ( calculateHandValue( dealerHand ) < 17 ) {
                        checkShoeIfEmpty();
                        cardAdded = shoe.get( shoeIndex++ );
                        changeCount( cardAdded );
                        dealerHand.add( cardAdded );
                    }
                    final int dealerFinalValue = calculateHandValue( dealerHand );
                    final int playerFinalValue = calculateHandValue( playerHand );
                    if ( dealerFinalValue > 21 || dealerFinalValue < playerFinalValue ) {
                        playerWins++;
                        playerProfit += betAmount;
                    }
                    else if ( dealerFinalValue > playerFinalValue ) {
                        dealerWins++;
                        playerProfit -= betAmount;
                    }
                    else {
                        push++;
                    }
                    break;
                }
                else if ( decision.equals( "double" ) ) {
                    playerBets += betAmount;
                    checkShoeIfEmpty();
                    cardAdded = shoe.get( shoeIndex++ );
                    changeCount( cardAdded );
                    playerHand.add( cardAdded );
                    if ( calculateHandValue( playerHand ) > 21 ) {
                        dealerWins++;
                        playerProfit -= 2 * betAmount;
                        break;
                    }
                    while ( calculateHandValue( dealerHand ) < 17 ) {
                        checkShoeIfEmpty();
                        cardAdded = shoe.get( shoeIndex++ );
                        changeCount( cardAdded );
                        dealerHand.add( cardAdded );
                    }
                    final int dealerFinalValue = calculateHandValue( dealerHand );
                    final int playerFinalValue = calculateHandValue( playerHand );
                    if ( dealerFinalValue > 21 || dealerFinalValue < playerFinalValue ) {
                        playerWins++;
                        playerProfit += 2 * betAmount;
                    }
                    else if ( dealerFinalValue > playerFinalValue ) {
                        dealerWins++;
                        playerProfit -= 2 * betAmount;
                    }
                    else {
                        push++;
                    }
                    break;
                }
            }

        }
    }

    /**
     * Main function for the game
     *
     * @param args
     *            arguments
     */
    public static void main ( final String[] args ) throws Exception {

        for ( int i = 0; i < 1000; i++ ) {
            play1000Hands();
            results[i][0] = dealerWins;
            results[i][1] = playerWins;
            results[i][2] = push;
            results[i][3] = playerBets;
            results[i][4] = playerProfit;
            System.out.println( "Dealer wins: " + results[i][0] );
            System.out.println( "Player wins: " + results[i][1] );
            System.out.println( "Pushes: " + results[i][2] );
            System.out.println( "The player gambled a total of " + results[i][3] + " dollars." );
            if ( playerProfit >= 0 ) {
                System.out.println( "The player made a net profit of: " + results[i][4] + " dollars." );
            }
            else {
                System.out.println( "The player made a net loss of: " + ( results[i][4] * -1 ) + " dollars." );

            }
            dealerWins = 0;
            playerWins = 0;
            push = 0;
            playerBets = 0;
            playerProfit = 0;
            shoeIndex = 0;
            runningCount = 0;
        }

        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet spreadsheet = workbook.createSheet( " Card Counting Data " );
        XSSFRow row;
        Map<String, Object[]> studentData = new TreeMap<String, Object[]>();

        studentData.put( "1",
                new Object[] { "Round", "Dealer Wins", "Player Wins", "Pushes", "Total Bet", "Net Profit" } );

        for ( int i = 0; i < 1000; i++ ) {
            studentData.put( Integer.toString( i ),
                    new Object[] { i + 1, results[i][0], results[i][1], results[i][2], results[i][3], results[i][4] } );
        }
        Set<String> keyid = studentData.keySet();

        int rowid = 0;

        // writing the data into the sheets...

        for ( String key : keyid ) {

            row = spreadsheet.createRow( rowid++ );
            Object[] objectArr = studentData.get( key );
            int cellid = 0;

            for ( Object obj : objectArr ) {
                Cell cell = row.createCell( cellid++ );
                cell.setCellValue( (String) obj );
            }
        }

        // .xlsx is the format for Excel Sheets...
        // writing the workbook into the file...
        FileOutputStream out = new FileOutputStream( new File( "datasets/ccDataset.xlsx" ) );

        workbook.write( out );
        workbook.close();
        out.close();

    }

}
