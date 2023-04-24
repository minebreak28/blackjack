package bj;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * Blackjack using basic strategy only
 *
 * @author Adam Wu
 *
 */
public class BlackjackBasicStrategy {

    private static double            playerProfit = 0;
    private static int               playerBets   = 0;
    private static int               dealerWins   = 0;
    private static int               playerWins   = 0;
    private static int               push         = 0;
    private static int               shoeIndex    = 0;
    private static ArrayList<String> shoe;
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

    public static void play1000Hands () {
        shoe = initializeShoe();

        // Plays 1000 hands of blackjack to basic strategy
        for ( int i = 0; i < 1000; i++ ) {
            checkShoeIfEmpty();
            playerBets++;
            // Deal two cards to the player and two cards to the dealer
            final ArrayList<String> playerHand = new ArrayList<>();
            playerHand.add( shoe.get( shoeIndex++ ) );
            checkShoeIfEmpty();
            playerHand.add( shoe.get( shoeIndex++ ) );
            final ArrayList<String> dealerHand = new ArrayList<>();
            checkShoeIfEmpty();
            dealerHand.add( shoe.get( shoeIndex++ ) );
            checkShoeIfEmpty();
            dealerHand.add( shoe.get( shoeIndex++ ) );

            final int playerValue = calculateHandValue( playerHand );
            final int dealerValue = calculateHandValue( dealerHand );

            // First, check if dealer/player has a blackjack

            if ( checkBlackjack( playerValue, dealerValue ) == "dealer" ) {
                dealerWins++;
                playerProfit--;
                continue;
            }
            else if ( checkBlackjack( playerValue, dealerValue ) == "push" ) {
                push++;
                continue;
            }
            else if ( checkBlackjack( playerValue, dealerValue ) == "player" ) {
                playerWins++;
                playerProfit += 1.5;
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
                    playerHand.add( shoe.get( shoeIndex++ ) );
                    if ( calculateHandValue( playerHand ) > 21 ) {
                        dealerWins++;
                        playerProfit--;
                        break;
                    }
                }
                else if ( decision.equals( "stand" ) ) {
                    while ( calculateHandValue( dealerHand ) < 17 ) {
                        checkShoeIfEmpty();
                        dealerHand.add( shoe.get( shoeIndex++ ) );
                    }
                    final int dealerFinalValue = calculateHandValue( dealerHand );
                    final int playerFinalValue = calculateHandValue( playerHand );
                    if ( dealerFinalValue > 21 || dealerFinalValue < playerFinalValue ) {
                        playerWins++;
                        playerProfit++;
                    }
                    else if ( dealerFinalValue > playerFinalValue ) {
                        dealerWins++;
                        playerProfit--;
                    }
                    else {
                        push++;
                    }
                    break;
                }
                else if ( decision.equals( "double" ) ) {
                    playerBets++;
                    checkShoeIfEmpty();
                    playerHand.add( shoe.get( shoeIndex++ ) );
                    if ( calculateHandValue( playerHand ) > 21 ) {
                        dealerWins++;
                        playerProfit = -2;
                        break;
                    }
                    while ( calculateHandValue( dealerHand ) < 17 ) {
                        checkShoeIfEmpty();
                        dealerHand.add( shoe.get( shoeIndex++ ) );
                    }
                    final int dealerFinalValue = calculateHandValue( dealerHand );
                    final int playerFinalValue = calculateHandValue( playerHand );
                    if ( dealerFinalValue > 21 || dealerFinalValue < playerFinalValue ) {
                        playerWins++;
                        playerProfit += 2;
                    }
                    else if ( dealerFinalValue > playerFinalValue ) {
                        dealerWins++;
                        playerProfit -= 2;
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
        }

        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet spreadsheet = workbook.createSheet( " Basic Strategy Data " );
        XSSFRow row;
        Font font = workbook.createFont();
        font.setBold( true );
        Map<String, Object[]> studentData = new TreeMap<String, Object[]>();

        studentData.put( "1",
                new Object[] { "Round", "Dealer Wins", "Player Wins", "Pushes", "Total Bet", "Net Profit" } );

        double totalProfit = 0;
        double averageProfit = 0;
        double averageWinPercent = 0;
        double averageLossPercent = 0;
        double averagePushPercent = 0;
        double dealerTotalWin = 0;
        double playerTotalWin = 0;
        double totalPush = 0;
        double totalPlays = 0;
        for ( int index = 0; index < 1000; index++ ) {
            String i = Integer.toString( index + 1 );

            // Let's add to the total bet amount
            // totalBet += results[index][3];
            // Let's add to dealer total win, player total win, and total plays.
            dealerTotalWin += results[index][0];
            playerTotalWin += results[index][1];
            totalPush += results[index][2];
            // Let's add to the total profit
            totalProfit += results[index][4];

            studentData.put( Integer.toString( index + 2 ), new Object[] { i, results[index][0], results[index][1],
                    results[index][2], results[index][3], results[index][4] } );
        }
        // Calculate total plays
        totalPlays = dealerTotalWin + playerTotalWin + totalPush;
        // Calculate average percentage of win, loss, and push
        averageWinPercent = playerTotalWin / totalPlays;
        averageLossPercent = dealerTotalWin / totalPlays;
        averagePushPercent = totalPush / totalPlays;
        // Calculate Average profit
        averageProfit = totalProfit / 1000;

        studentData.put( "1002", new Object[] { "Average Win Percentage:", averageWinPercent } );
        studentData.put( "1003", new Object[] { "Average Loss Percentage:", averageLossPercent } );
        studentData.put( "1004", new Object[] { "Average Push Percentage:", averagePushPercent } );
        studentData.put( "1005", new Object[] { "Average Profit:", averageProfit } );

        Set<String> keyid = studentData.keySet();

        // writing the data into the sheets...

        for ( String key : keyid ) {

            row = spreadsheet.createRow( Integer.parseInt( key ) - 1 );
            Object[] objectArr = studentData.get( key );
            int cellid = 0;

            for ( Object obj : objectArr ) {
                CellStyle style = workbook.createCellStyle();
                Cell cell = row.createCell( cellid++ );
                cell.setCellValue( obj.toString() );
                if ( Integer.parseInt( key ) >= 1002 ) {
                    style.setFont( font );
                    cell.setCellStyle( style );
                }

            }
        }

        // .xlsx is the format for Excel Sheets...
        // writing the workbook into the file...
        FileOutputStream out = new FileOutputStream( new File( "datasets\\bsDataset.xlsx" ) );

        workbook.write( out );
        workbook.close();
        out.close();

    }

}
