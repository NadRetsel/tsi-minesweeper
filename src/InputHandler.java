import java.util.Arrays;
import java.util.Scanner;

public class InputHandler {

    private final Scanner reader;
    public InputHandler(){
        this.reader = new Scanner(System.in);
    }


    // Validate inputs to be an integer
    public int InputInteger(String input_message){
        int int_input = -1;

        boolean valid_input = false;
        // Keep asking until valid input given
        while(!valid_input){
            System.out.println(input_message);
            String input = this.reader.next();

            // Try to parse String -> int
            try {
                int_input = Integer.parseInt(input);
                valid_input = true;
            }
            catch(Exception e) {
                System.out.println("Input must be an integer. Please try again.");
            }
        }

        return int_input;
    }

    // Validate inputs to be within integer range
    public int InputInteger(String input_message, Integer min_bound, Integer max_bound){
        int int_input = -1;
        boolean within_range = false;
        // Keep asking until input within range given
        while(!within_range) {

            int_input = InputInteger(input_message); // Ensure integer is inputted

            try{
                if(min_bound != null && int_input < min_bound) throw new OutOfRangeException(); // Lower than minimum bound
                if(max_bound != null && int_input > max_bound) throw new OutOfRangeException(); // Higher than maximum bound;

                within_range = true; // Input within bounds
            }
            catch(OutOfRangeException e){
                System.out.println("Input of range. Please try again.");
            }
        }
        return int_input;
    }

    // Validate inputs to select from option list
    public String InputMenu(String input_message, String[] options){
        String options_input = "";

        boolean valid_input = false;
        // Keep asking until one from options chosen
        while(!valid_input) {
            System.out.println(input_message);
            options_input = reader.next().toUpperCase(); // Standardise input to upper case
            try{
                if(!Arrays.asList(options).contains(options_input)) throw new UnknownOptionException(); // Input not in option list
                valid_input = true; // Valid option selected
            }
            catch(UnknownOptionException e){
                System.out.println("Input not recognised. Please try again.");

            }
        }

        return options_input;
    }
}
