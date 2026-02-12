import java.io.*;
import java.util.*;

/**
 * @author Leonardo Lopez
 * @version 0.1.0
 * @Since 1/29/26
 **/
public class Jotto {
    public Jotto(String filename) { // constructor
        this.filename = filename;
        readWords();
    }
    // constant fields
    private static final boolean DEBUG = false;
    private static final int WORD_SIZE = 5;

    // Members
    private final ArrayList<String> playGuesses = new ArrayList<>();
    private final ArrayList<String> playWords = new ArrayList<>();
    private final ArrayList<String> wordList = new ArrayList<>();

    private String filename;
    private String currentWord;
    private int score = 0;

    // implementations

    // Selects a random unused word
    public boolean pickWord() {
        if (playWords.size() == wordList.size()) {
            System.out.println("You've guessed them all!");
            return false;
        }
        Random rand = new Random();
        currentWord = wordList.get(rand.nextInt(wordList.size()));
        if (playWords.contains(currentWord)) {
            return pickWord();
        }
        playWords.add(currentWord);
        if (DEBUG) {
            System.out.println(currentWord);
        }
        return true;
    }

    // Returns formatted list of available words
    public String showWordList() {
        StringBuilder sb = new StringBuilder("Current word list:\n");
        for (String word : wordList) {
            sb.append(word).append("\n");
        }
        return sb.toString();
    }

    // Shows player guesses and optionally updates file
    public ArrayList<String> showPlayerGuesses() {
        Scanner scan = new Scanner(System.in);
        if (playGuesses.isEmpty()) {
            System.out.println("No guesses yet");
            return playGuesses;
        }
        System.out.println("Current player guesses:");
        for (String guess : playGuesses) {
            System.out.println(guess);
        }
        System.out.println("Would you like to add the words to the word list? (y/n)");
        String input = scan.nextLine().trim().toLowerCase();
        if (input.equals("y")) {
            System.out.println("Updating word list.");
            updateWordList();
            System.out.println(showWordList());
        }
        return playGuesses;
    }

    // Reads words from file into wordList (no duplicates)
    public ArrayList<String> readWords() {
        try {
            File file = new File(filename);
            Scanner scan = new Scanner(file);

            while (scan.hasNextLine()) {
                String word = scan.nextLine().trim().toLowerCase();
                if (!wordList.contains(word)) {
                    wordList.add(word);
                }
            }
            scan.close();
        } catch (Exception e) {
            System.out.println("Couldn't open " + filename);
        }
        return wordList;
    }

    // Main menu loop for the game
    public void play() {
        Scanner scan = new Scanner(System.in);
        String input;
        System.out.println("Welcome to the game.");
        while (true) {
            System.out.println("Current Score: " + score);
            System.out.println("=-=-=-=-=-=-=-=-=-=-=");
            System.out.println("Choose one of the following:");
            System.out.println("1:\t Start the game");
            System.out.println("2:\t See the word list");
            System.out.println("3:\t See the chosen words");
            System.out.println("4:\t Show Player guesses");
            System.out.println("zz to exit");
            System.out.println("=-=-=-=-=-=-=-=-=-=-=");
            System.out.print("What is your choice: ");

            input = scan.nextLine().trim().toLowerCase();
            if (input.equals("1") || input.equals("one")) {
                if (!pickWord()) {
                    showPlayerGuesses();
                } else {
                    int roundScore = guess();
                    score += roundScore;
                    System.out.println();
                    System.out.println("Your score is " + score);
                }
            } else if (input.equals("2") || input.equals("two")) {
                System.out.println(showWordList());
            } else if (input.equals("3") || input.equals("three")) {
                System.out.println(showPlayedWords());
            } else if (input.equals("4") || input.equals("four")) {
                System.out.println(showPlayerGuesses());
            } else if (input.equals("zz")) {
                System.out.println("Final score: " + score);
                System.out.println("Thank you for playing!");
                break;
            } else {
                System.out.println("I don't know what \"" + input + "\" is.");
            }
            System.out.println("Press enter to continue");
            scan.nextLine();
            System.out.println();
        }
    }

    // Handles one round of guessing
    public int guess() {
        ArrayList<String> currentGuesses = new ArrayList<>();
        Scanner scan = new Scanner(System.in);

        int roundScore = WORD_SIZE + 1;
        String wordGuess;

        while (true) {
            System.out.println("Current Score: " + roundScore);
            System.out.print("What is your guess (q to quit):");

            wordGuess = scan.nextLine().trim().toLowerCase();

            if (wordGuess.equals("q")) {
                roundScore = Math.min(roundScore, 0);
                break;
            }

            if (wordGuess.length() != WORD_SIZE) {
                System.out.println("Word must be 5 characters (" +
                        wordGuess + " is " + wordGuess.length() + ")");
                continue;
            }
            addPlayerGuess(wordGuess);
            if (wordGuess.equals(currentWord)) {
                System.out.println("DING! DING!! DING!!! the word was " + currentWord);
                currentGuesses.add(wordGuess);
                displayGuessTable(currentGuesses);
                return roundScore;
            }
            int letterCount = getLetterCount(wordGuess);
            System.out.println(wordGuess + " has a Jotto score of " + letterCount);
            currentGuesses.add(wordGuess);
            displayGuessTable(currentGuesses);
            roundScore--;
            System.out.println();
        }
        return roundScore;
    }

    // Returns number of matching unique letters
    public int getLetterCount(String wordGuess) {
        if (wordGuess.equals(currentWord)) {
            return WORD_SIZE;
        }
        int count = 0;
        ArrayList<Character> letters = new ArrayList<>();

        for (char c : currentWord.toCharArray()) {
            if (!letters.contains(c)) {
                letters.add(c);
            }
        }
        for (char c : wordGuess.toCharArray()) {
            if (letters.contains(c)) {
                letters.remove((Character) c);
                count++;
            }
        }
        return count;
    }

    // Returns formatted list of words already played
    public String showPlayedWords() {
        if (playWords.isEmpty()) {
            return "No words have been played";
        }
        StringBuilder sb = new StringBuilder("Current list of played words:\n");
        for (String word : playWords) {
            sb.append(word).append("\n");
        }
        return sb.toString();
    }

    // Adds guess if not already stored
    public boolean addPlayerGuess(String wordGuess) {
        if (!playGuesses.contains(wordGuess)) {
            playGuesses.add(wordGuess);
            return true;
        }
        return false;
    }

    // Writes updated word list back to file
    void updateWordList() {
        try {
            for (String guess : playGuesses) {
                if (!wordList.contains(guess)) {
                    wordList.add(guess);
                }
            }
            FileWriter writer = new FileWriter(filename);
            for (String word : wordList) {
                writer.write(word + "\n");
            }
            writer.close();
        } catch (Exception e) {
            System.out.println("Error writing to file.");
        }
    }

    // Displays guesses and their Jotto scores
    private void displayGuessTable(ArrayList<String> guesses) {

        System.out.println("Guess\t\tScore");

        for (String guess : guesses) {
            System.out.println(guess + "\t\t" + getLetterCount(guess));
        }

        System.out.println();
    }
}
