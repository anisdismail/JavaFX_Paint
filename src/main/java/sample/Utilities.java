package sample;

import javafx.stage.FileChooser;
import java.io.File;

public class Utilities
{
    static FileChooser Chooser = new FileChooser();

    public static String BrowseForFile( String windowTitle )
    {
        Chooser.setTitle(windowTitle);
        Chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PNG file","*")) ;

        File file = Chooser.showOpenDialog(null) ;

        return file != null ? file.getAbsolutePath() : null ;
    }

    public static String SaveFileLocation ( String windowTitle )
    {
        Chooser.setTitle(windowTitle);

        File file = Chooser.showSaveDialog(null) ;

        return file != null ? file.getAbsolutePath() : null ;
    }

    public static String TruncateIfLonger ( String s , int length )
    {
        if ( s.length() > length + 3 )
        {
            return "..." + ( s.substring( s.length() - length - 3)) ;
        }

        return s;
    }

}