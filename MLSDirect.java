import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;


public class MLSDirect
{
    static FileWriter writer = null;


    public static void main( String args[] ) throws Exception
    {
        writer = new FileWriter( "Data.csv" );
        writer.write( "Team,Year,Player,POS,GP,GS,MINS,G,A,SHTS,SOG,GWG,PKG/A,HmG,RdG,G/90min,SC%" );
        writer.append( '\n' );

        for ( int season = 2007; season <= 2016; season++ )
        {
            getData( season );
        }
    }


    public static void getData( int season ) throws IOException
    {
        List<Object[]> list = new ArrayList<Object[]>();
        list.add( new Object[] { "Chicago Fire", 1207 } );
        list.add( new Object[] { "Chivas USA", 2079 } );
        list.add( new Object[] { "Colorado Rapids", 436 } );
        list.add( new Object[] { "Columbus Crew SC", 454 } );
        list.add( new Object[] { "D.C. United", 1326 } );
        list.add( new Object[] { "FC Dallas", 1903 } );
        list.add( new Object[] { "Houston Dynamo", 1897 } );
        list.add( new Object[] { "LA Galaxy", 1230 } );
        list.add( new Object[] { "Miami Fusion", 2 } );
        list.add( new Object[] { "Montreal Impact", 1616 } );
        list.add( new Object[] { "New England Revolution", 928 } );
        list.add( new Object[] { "New York City", 9668 } );
        list.add( new Object[] { "New York Red Bulls", 399 } );
        list.add( new Object[] { "Orlando City  SC", 6900 } );
        list.add( new Object[] { "Philadelphia Union", 5513 } );
        list.add( new Object[] { "Portland Timbers", 1581 } );
        list.add( new Object[] { "Real Salt Lake", 1899 } );
        list.add( new Object[] { "San Jose Earthquakes", 1131 } );
        list.add( new Object[] { "Seattle Sounders FC", 3500 } );
        list.add( new Object[] { "Sporting Kansas City", 421 } );
        list.add( new Object[] { "Tampa Bay Mutiny", 1 } );
        list.add( new Object[] { "Toronto FC", 2077 } );
        list.add( new Object[] { "Vancouver Whitecaps FC", 1708 } );
        for ( int cnt = 0; cnt < list.size(); cnt++ )
        {
            getNumPlayers( list.get( cnt ), season, 0 );
        }
    }


    public static void getNumPlayers( Object[] team, int season, int time ) throws IOException
    {
        String urlString = "http://www.mlssoccer.com/stats/season?franchise=" + team[1] + "&year=" + season
            + "&season_type=REG&group=goals&op=Search&form_id=mp7_stats_hub_build_filter_form&sort=desc&order=MINS";
        if ( time > 0 )
        {
            urlString += "&page=" + time;
        }
        // System.out.println( urlString );
        URL url = new URL( urlString );
        URLConnection con = url.openConnection();
        BufferedReader in = new BufferedReader( new InputStreamReader( con.getInputStream() ) );
        String inputLine;
        String result = "";
        while ( ( inputLine = in.readLine() ) != null )
        {
            result += inputLine;
            // System.out.println(inputLine);
        }

        Document doc = Jsoup.parse( result );

        Element table = doc.select( "table.responsive.no-more-tables.season_stats" ).first();
        boolean firstRow = false;
        for ( Element row : table.select( "tr" ) )
        {
            Elements tds = row.select( "td" );
            if ( tds.size() > 0 )
            {
                String csvStr = team[0] + "," + season + ",";
                if ( firstRow )
                {
                    csvStr += tds.get( 0 ).text() + ",";
                    System.out.println(tds.get( 0 ).attr( "a[href]" ));
                    for ( int cnt = 1; cnt < tds.size(); cnt++ )
                    {
                        csvStr += tds.get( cnt ).text() + ",";
                    }
                    writer.write( csvStr );
                    writer.append( '\n' );
                }
                else
                {
                    firstRow = true;
                }
            }
        }
        System.out.println( team[0] + " " + season );
        Elements nextPage = doc.select( "a[title=Go to next page]" );
        if ( nextPage.size() > 0 )
        {
            getNumPlayers( team, season, time + 1 );
        }
    }
}