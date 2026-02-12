import java.io.*;
import java.util.*;

/**
 * @author Leonardo Lopez
 * @version 0.1.0
 * @Since 2/11/26
 **/
/**
 * Make a branch and start! :)
 */
public class Driver {
    public static void main(String[] args) {
        String filepath = "oiled.txt";
        Jotto game = new Jotto(filepath);
        game.play();
    }
}
