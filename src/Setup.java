import java.awt.event.*;
import javax.swing.*;

public class Setup extends JFrame implements ActionListener {

    private final InputHandler input_handler = new InputHandler();
    private final JFrame frame;
    private final JPanel panel;
    private final JTextField rows_input, columns_input, bombs_input;
    private final JLabel error_message;
    private final String[] grid_options = {"Easy (8x8 - 10 bombs)", "Medium (16x16 - 40 bombs)", "Hard (16x30 - 99 bombs)", "Custom Grid"};


    public Setup(){
        this.frame = new JFrame();
        this.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        this.panel  = new JPanel();
        this.panel.setLayout(new BoxLayout(this.panel, BoxLayout.Y_AXIS));

        this.rows_input = new JTextField();
        this.columns_input = new JTextField();
        this.bombs_input = new JTextField();
        this.error_message = new JLabel(" ");
        SetupGame();

    }


    // Let the user select a grid to play
    public void SetupGame(){
        JLabel label = new JLabel("===== WELCOME TO MINESWEEPER =====");
        this.panel.add(label);

        JButton button = new JButton(this.grid_options[0]); // Easy
        button.addActionListener(this);
        this.panel.add(button);

        button = new JButton(this.grid_options[1]); // Medium
        button.addActionListener(this);
        this.panel.add(button);

        button = new JButton(this.grid_options[2]); // Hard
        button.addActionListener(this);
        this.panel.add(button);

        label = new JLabel("Number of rows: ");
        this.panel.add(label);
        this.panel.add(rows_input);

        label = new JLabel("Number of columns: ");
        this.panel.add(label);
        this.panel.add(columns_input);

        label = new JLabel("Number of bombs: ");
        this.panel.add(label);
        this.panel.add(bombs_input);

        this.panel.add(this.error_message);

        button = new JButton(this.grid_options[3]); // Custom
        button.addActionListener(this);
        this.panel.add(button);

        this.frame.add(this.panel);
        this.frame.pack();
        this.frame.setVisible(true);

        /*
        while(true) {

            System.out.println("===== WELCOME TO MINESWEEPER =====");
            String[] grid_options = {"EASY", "MEDIUM", "HARD", "CUSTOM"};
            int grid_input = SelectGrid(grid_options);

            Game game = null;
            switch (grid_input) {
                case 0 -> game = new Game(8, 8, 10); // EASY
                case 1 -> game = new Game(16, 16, 40); // MEDIUM
                case 2 -> game = new Game(16, 30, 99); // HARD
                case 3 -> { // CUSTOM
                    int custom_rows = input_handler.InputInteger("Enter number of rows (minimum of 1): ", 1, null);
                    int custom_columns = input_handler.InputInteger("Enter number of columns (minimum of " + (custom_rows == 1 ? 2 : 1) + "): ", (custom_rows == 1 ? 2 : 1), null);

                    int max_bombs = custom_columns * custom_rows - 1;
                    int custom_bombs = input_handler.InputInteger("Enter number of bombs (minimum of 1, maximum of " + max_bombs + "):", 0, max_bombs);

                    game = new Game(custom_rows, custom_columns, custom_bombs);
                }
            }
            game.PlayGame(); // Begin game
        }
        */


    }

    @Override
    public void actionPerformed(ActionEvent a) {
        String difficulty = a.getActionCommand();
        if(difficulty.equals(grid_options[0])) new Game(8,8,10); // Easy
        if(difficulty.equals(grid_options[1])) new Game(16,16, 40); // Medium
        if(difficulty.equals(grid_options[2])) new Game(16, 30, 99); // Hard

        if(difficulty.equals(grid_options[3])){ // Custom
            try{
                int rows_num = Integer.parseInt(this.rows_input.getText());
                int columns_num = Integer.parseInt(this.columns_input.getText());
                int bombs_num = Integer.parseInt(this.bombs_input.getText());

                int bombs_max = rows_num*columns_num - 1;

                this.error_message.setText("Generating grid...");

                // Check all inputs are valid
                if(rows_num == 1 && columns_num == 1 || (rows_num < 1 || columns_num < 1)) this.error_message.setText("Minimum grid size is 1x2 or 2x1.");
                else if(bombs_num < 1) this.error_message.setText("Minimum bomb count size is 1.");
                else if(bombs_num > bombs_max) this.error_message.setText("Maximum bomb count size is " + bombs_max +".");
                else new Game(rows_num, columns_num, bombs_num);
            }
            catch(Exception e) {
                this.error_message.setText("Inputs must be integers.");
            }
        }
    }


    public int SelectGrid(String[] menu_options){
        int menu_input = -1;
        boolean menu_confirm = false;

        while(!menu_confirm) {
            menu_input = input_handler.InputInteger("""
                            Choose your grid:
                            0 - EASY (10x10 - 10 bombs)
                            1 - MEDIUM (16x16 - 40 bombs)
                            2 - HARD (30x16 - 99 bombs)
                            3 - CUSTOM
                            Please select [0-3]""",
                    0, 3);

            // Confirm menu selection
            String[] options = {"Y", "N"};
            String confirm_input = input_handler.InputMenu(("You have chosen " + menu_options[menu_input] + ". Is this correct? [Y/N]"),
                    options);

            if(confirm_input.equals("Y")) menu_confirm = true;
        }
        return menu_input;
    }


}
