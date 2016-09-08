import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.FileWriter;
import java.io.IOException;


/**
 * Gets squad info for MLS teams from Transfermarkt. Transfermarket's data looks
 * like it has a few discrepancies so far, so MLSDirect.java will try to get the
 * squad's number of players and number of playing players direct from
 * MLSSoccer.com.
 *
 * @author Gautam Sarkar
 * @version Sep 7, 2016
 */
public class Test
{
    /**
     * Stores data in ArrayList of Strings with key being team name,year.
     */
    static HashMap<String, ArrayList<String>> data = new HashMap<String, ArrayList<String>>();

    /**
     * Writes out the info to a csv file.
     */
    static FileWriter writer = null;


    /**
     * Main method.
     * 
     * @param args
     *            not used
     * @throws IOException
     */
    public static void main( String args[] ) throws IOException
    {
        writer = new FileWriter( "MLSDataTransfermarkt.csv" );
        writer.write(
            "Year,Team,Nickname,Players in squad,Average Age,Foreign Players,Market Value,Average Market Value,Coaches,Players Used,Matches,W,D,L,GF,GA,+/-,Points" );
        writer.append( '\n' );
        for ( int season = 2010; season <= 2015; season++ )
        {
            getData( season );
        }
        printData();
    }


    /**
     * Prints out data to the csv file.
     * 
     * @throws IOException
     */
    public static void printData() throws IOException
    {
        for ( Map.Entry entry : data.entrySet() )
        {
            String csvStr = (String)entry.getKey();
            ArrayList<String> half2 = (ArrayList<String>)entry.getValue();
            for ( String stat : half2 )
            {
                csvStr += "," + stat;
            }
            writer.write( csvStr );
            writer.append( '\n' );
        }
        writer.close();
    }


    /**
     * Scrapes data for the given season (The season id is the real season minus
     * one).
     * 
     * @param season
     * @throws IOException
     */
    public static void getData( int season ) throws IOException
    {
        getAverageAge( season );
        getNumPlayers( season );
        getPoints( season );
    }


    /**
     * Gets average age of players used by every team in a given season.
     * 
     * @param season
     *            the season
     * @throws IOException
     */
    public static void getAverageAge( int season ) throws IOException
    {
        String url = "http://www.transfermarkt.com/major-league-soccer/startseite/wettbewerb/MLS1/plus/?saison_id="
            + season;
        Document doc = Jsoup.connect( url )
            .userAgent( "Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:25.0) Gecko/20100101 Firefox/25.0" )
            .referrer( "http://www.google.com" )
            .get();
        Element table = doc.select( "table.items" ).first();
        season++;

        boolean firstRow = false;
        for ( Element row : table.select( "tr" ) )
        {
            Elements tds = row.select( "td" );
            if ( tds.size() > 0 )
            {
                if ( firstRow )
                {
                    String half1 = season + "," + tds.get( 1 ).text();
                    ArrayList<String> half2 = new ArrayList<String>();
                    for ( int cnt = 2; cnt < tds.size() - 2; cnt++ )
                    {
                        half2.add( tds.get( cnt ).text().replace( ",", "." ) );
                    }
                    data.put( half1, half2 );
                }
                else
                {
                    firstRow = true;
                }
            }
        }
    }


    /**
     * Gets the number of players used by every team in a given season.
     * 
     * @param season
     *            the season
     * @throws IOException
     */
    public static void getNumPlayers( int season ) throws IOException
    {
        String url = "http://www.transfermarkt.com/major-league-soccer/eingesetztespieler/wettbewerb/MLS1/plus/?saison_id="
            + season + "&filter=alle";
        Document doc = Jsoup.connect( url )
            .userAgent( "Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:25.0) Gecko/20100101 Firefox/25.0" )
            .referrer( "http://www.google.com" )
            .get();
        Element table = doc.select( "table.items" ).first();
        season++;

        for ( Element row : table.select( "tr" ) )
        {
            Elements tds = row.select( "td" );
            if ( tds.size() > 0 )
            {
                String half1 = season + "," + tds.get( 1 ).text();
                ArrayList<String> numbers = data.get( half1 );
                ArrayList<String> half2 = new ArrayList<String>();
                for ( int cnt = 2; cnt < tds.size(); cnt++ )
                {
                    half2.add( tds.get( cnt ).text().replace( ",", "." ) );
                }
                numbers.addAll( half2 );
            }
        }
    }


    /**
     * Gets the points of every team in a given season.
     * 
     * @param season
     *            the season
     * @throws IOException
     */
    public static void getPoints( int season ) throws IOException
    {
        season++;
        String url = "http://www.transfermarkt.com/major-league-soccer/jahrestabelle/wettbewerb/MLS1/saison_id/"
            + season;
        Document doc = Jsoup.connect( url )
            .userAgent( "Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:25.0) Gecko/20100101 Firefox/25.0" )
            .referrer( "http://www.google.com" )
            .get();
        Element table = doc.select( "table" ).get( 3 );

        boolean firstRow = false;
        for ( Element row : table.select( "tr" ) )
        {
            Elements tds = row.select( "td" );
            if ( tds.size() > 0 )
            {
                ArrayList<String> numbers = null;
                for ( Map.Entry entry : data.entrySet() )
                {
                    String csvStr = (String)entry.getKey();
                    ArrayList<String> half2 = (ArrayList<String>)entry.getValue();
                    if ( half2.get( 0 ).equals( tds.get( 2 ).text() ) )
                    {
                        if ( ( ( (String)entry.getKey() ).substring( 0, 4 ) ).equals( season + "" ) )
                        {
                            System.out.println( half2.get( 0 ) + " " + season );
                            numbers = (ArrayList<String>)entry.getValue();
                        }
                    }
                }
                ArrayList<String> half2 = new ArrayList<String>();
                for ( int cnt = 3; cnt < tds.size(); cnt++ )
                {
                    half2.add( tds.get( cnt ).text().replace( ",", "." ).replace( ":", "," ) );
                }
                numbers.addAll( half2 );
            }
        }
    }
}