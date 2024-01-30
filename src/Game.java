import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.LinkedList;
import java.util.Random;

public class Game implements ActionListener, ItemListener {
    private final InputHandler input_handler = new InputHandler();
    private final LinkedList<Cell> cells_flagged;

    private final Grid grid;
    private final String[] action_options = {"Reveal", "Flag / Unflag", "Mark / Unmark"};
    private String action_selected;
    private final int rows, columns, number_of_bombs;
    private boolean first_move, game_in_progress;

    private final JFrame frame;
    private final JPanel panel_game, panel_game_grid;
    private final JLabel flag_count_label, cells_left_label;


    public Game(int rows, int columns, int number_of_bombs){
        this.rows = rows;
        this.columns = columns;
        this.number_of_bombs = number_of_bombs;

        this.grid = new Grid(this.rows,this.columns);
        this.first_move = true;
        this.game_in_progress = true;
        this.cells_flagged = new LinkedList<>();

        this.action_selected = "Reveal";

        this.frame = new JFrame();
        this.panel_game = new JPanel();
        this.panel_game_grid = new JPanel();
        this.flag_count_label = new JLabel(" ");
        this.cells_left_label = new JLabel(" ");


        CreateGame();
    }

    public void CreateGame(){
        this.panel_game.setLayout(new BoxLayout(this.panel_game, BoxLayout.Y_AXIS));
        this.panel_game_grid.setLayout(new GridLayout(this.rows, this.columns));

        // Create grid of buttons
        for(int y = 0; y < this.rows; y++){
            for(int x = 0; x < this.columns; x++){
                CellButton button = new CellButton("Cell", null, this.grid.GetCell(x,y));
                button.addActionListener(this);
                this.panel_game_grid.add(button);
            }
        }

        // Radio buttons to select REVEAL, FLAG, MARK
        JPanel action_panel = new JPanel();
        ButtonGroup action_group = new ButtonGroup();

        JRadioButton action_select = new JRadioButton(this.action_options[0], true);
        action_select.addItemListener(this);
        action_group.add(action_select);
        action_panel.add(action_select);

        action_select = new JRadioButton(this.action_options[1], false);
        action_select.addItemListener(this);
        action_group.add(action_select);
        action_panel.add(action_select);

        action_select = new JRadioButton(this.action_options[2], false);
        action_select.addItemListener(this);
        action_group.add(action_select);
        action_panel.add(action_select);

        this.cells_left_label.setText(this.grid.GetCellsRemaining() + " cells remaining.");
        this.flag_count_label.setText("0 / " + this.number_of_bombs + " flags used.");

        this.panel_game.add(action_panel);
        this.panel_game.add(cells_left_label);
        this.panel_game.add(flag_count_label);
        this.panel_game.add(this.panel_game_grid);

        this.frame.add(this.panel_game);

        this.frame.pack();
        this.frame.setVisible(true);
    }

    public void QuitGame(){
        this.frame.dispatchEvent(new WindowEvent(this.frame, WindowEvent.WINDOW_CLOSING));
    }

    public void UpdateGrid(){
        this.cells_left_label.setText(this.grid.GetCellsRemaining() + " cells remaining.");
        this.flag_count_label.setText(this.cells_flagged.size() + " / " + this.number_of_bombs + " flags used.");

        // Add button to quit/close the game when end
        if(!this.game_in_progress){
            JButton quit_game = new JButton("Quit Game");
            quit_game.addActionListener(this);
            this.panel_game.add(quit_game, 0);
        }

        // Rebuild grid
        this.panel_game_grid.removeAll();

        for(int y = 0; y < this.rows; y++){
            for(int x = 0; x < this.columns; x++){
                Cell cell = this.grid.GetCell(x,y);
                if(!cell.GetIsRevealed()){ // Not yet selected -> Button
                    CellButton button = new CellButton("Cell", cell.GetLabel(), cell);

                    if(cell.GetIsFlagged()) button.setForeground(Color.GREEN); // Flag = Green
                    else if(cell.GetIsMarked()) button.setForeground(Color.MAGENTA); // Mark = Magenta

                    if(cell.GetIsBomb() && !this.game_in_progress) button.setText("X"); // Show bombs when game ends

                    if(this.game_in_progress) button.addActionListener(this); // Only enable buttons during game
                    this.panel_game_grid.add(button);

                }
                else{ // Revealed -> Label
                    JLabel label = new JLabel();
                    label.setText(cell.GetIsBomb() ? "X" : cell.GetLabel());
                    label.setHorizontalAlignment(SwingConstants.CENTER);

                    this.panel_game_grid.add(label);
                }
            }
        }
        this.frame.setVisible(true);
        System.out.println("Updating");
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        switch(e.getActionCommand()){
            case "Cell" -> {
                CellButton cell_button = (CellButton) e.getSource();
                Cell cell_selected = cell_button.GetCell();

                if(this.action_selected.equals(this.action_options[0])) RevealCell(cell_selected);
                else if(this.action_selected.equals(this.action_options[1])) FlagCell(cell_selected);
                else if(this.action_selected.equals(this.action_options[2])) MarkCell(cell_selected);
                else System.out.println("Should not be here... CELL SELECT");

                UpdateGrid();
            }
            case "Quit Game" -> QuitGame();
        }

    }
    @Override
    public void itemStateChanged(ItemEvent e) {
        JRadioButton radio_button = (JRadioButton) e.getItem();
        if(radio_button.isSelected()) this.action_selected = radio_button.getText(); // Change variable to currently selected
    }

    public void PlayGame(){

        this.game_in_progress = true;
        String[] menu_options = {"REVEAL", "FLAG/UNFLAG", "MARK/UNMARK"};
        while(this.game_in_progress){

            // Display grid and stats
            System.out.println(this.grid.GridString(true));
            System.out.println("There are " + this.grid.GetCellsRemaining() + " cells remaining.");
            System.out.println(this.cells_flagged.size() + "/" + this.number_of_bombs + " cells flagged.");

            int menu_input = SelectAction(menu_options); // Choose to REVEAL, FLAG, MARK
            int[] coords = SelectCoords(); // Select cell to do action

            Cell cell_selected = this.grid.GetCell(coords[0], coords[1]);
            switch(menu_input){
                case 0 -> RevealCell(cell_selected); // REVEALING
                case 1 -> FlagCell(cell_selected); // FLAGGING
                case 2 -> MarkCell(cell_selected); // MARKING

            }
        }

        // Reveal grid and (in)correctly flagged cells
        System.out.println(this.grid.GridString(false));

        System.out.println("Correctly flagged:");
        for(Cell c : cells_flagged) if(c.GetIsBomb()) System.out.println("(" + c.GetX() + "," + c.GetY() + ")");

        System.out.println("Incorrectly flagged:");
        for(Cell c : cells_flagged) if(!c.GetIsBomb()) System.out.println("(" + c.GetX() + "," + c.GetY() + ")");
    }


    // Select to REVEAL, FLAG, MARK
    public int SelectAction(String[] menu_options){
        int menu_input = -1;
        boolean menu_confirm = false;
        while(!menu_confirm) {
            menu_input = input_handler.InputInteger("""
                            Would you like to...
                            0 - REVEAL
                            1 - FLAG/UNFLAG to indicate a potential bomb
                            2 - MARK/UNMARK to place a question mark
                            Please select [0-2]""",
                    0, 2);

            // Confirm menu selection
            String[] options = {"Y", "N"};
            String confirm_input = input_handler.InputMenu(("You have chosen to " + menu_options[menu_input] + ". Is this correct? [Y/N]"),
                    options);

            if(confirm_input.equals("Y")) menu_confirm = true;
        }
        return menu_input;
    }
    // Select coords to perform action
    public int[] SelectCoords(){
        int input_x = -1;
        int input_y = -1;
        boolean coord_confirm = false;
        while(!coord_confirm) {
            input_x = input_handler.InputInteger("Enter your x-coordinate: ", 1, this.columns) - 1;
            input_y = input_handler.InputInteger("Enter your y-coordinate: ", 1, this.rows) - 1;

            String[] options = {"Y", "N"};
            String confirm_input = input_handler.InputMenu(("You have chosen (" + (input_x+1) + "," + (input_y+1) +"). Is this correct? [Y/N]"), options);

            if(confirm_input.equals("Y")) coord_confirm = true;
        }
        return new int[]{input_x, input_y};
    }




    public void RevealCell(Cell cell_selected){

        // Populate grid with bombs if on first move
        if(this.first_move){
            PopulateGrid(cell_selected);
            this.first_move = false;
        }

        // Cell is already revealed -> Do nothing
        if(cell_selected.GetIsRevealed()){
            System.out.println("Already revealed.");
            return;
        }

        // Cell is already flagged -> Do nothing
        if(cell_selected.GetIsFlagged()){
            System.out.println("Cannot reveal. Cell is flagged.");
            return;
        }

        // Cell is already marked -> Do nothing
        if(cell_selected.GetIsMarked()){
            System.out.println("Cannot reveal. Cell is marked.");
            return;
        }

        // Cell is bomb -> End game
        if(cell_selected.GetIsBomb()){

            this.grid.RevealAdjacentCells(cell_selected);
            System.out.println("Bomb exploded. GAME OVER.");
            this.game_in_progress = false;
            JOptionPane.showMessageDialog(null, "Bomb exploded. GAME OVER");
            return;
        }

        // Reveal selected and adjacent cells if not bomb or flagged
        if(cell_selected.GetBombsNear() >= 0){
            this.grid.RevealAdjacentCells(cell_selected);

            // End game if cells left = number of bombs
            if(this.grid.GetCellsRemaining() == this.number_of_bombs){
                System.out.println("Ending game. ALL BOMBS FOUND.");
                this.game_in_progress = false;
                JOptionPane.showMessageDialog(null, "All bombs found. GAME OVER");
            }
            return;
        }

        System.out.println("Shouldn't be here (REVEALING)");

    }
    public void FlagCell(Cell cell_selected){

        // Cell is already revealed -> Do nothing
        if(cell_selected.GetIsRevealed()){
            System.out.println("Cannot flag cell. Already revealed.");
            return;
        }

        // Cell is already flagged -> Unflag
        if(cell_selected.GetIsFlagged()){
            System.out.println("Unflagging cell.");
            cell_selected.SetIsFlagged(false);
            cells_flagged.remove(cell_selected);
            return;
        }

        // Cell is not yet flagged -> Check if maximum flags -> Flag and unmark if needed
        if(!cell_selected.GetIsFlagged()){
            if(this.cells_flagged.size() >= this.number_of_bombs){
                System.out.println("Cannot flag cell. Maximum number of flags reached. Unflag another flagged cell first.");
                return;
            }

            cell_selected.SetIsMarked(false);

            System.out.println("Flagging cell.");
            cell_selected.SetIsFlagged(true);
            this.cells_flagged.add(cell_selected);

            return;
        }

        System.out.println("Shouldn't be here... (FLAGGING)");
    }
    public void MarkCell(Cell cell_selected){

        // Cell is already revealed -> Do nothing
        if(cell_selected.GetIsRevealed()){
            System.out.println("Cannot mark cell. Already revealed.");
            return;
        }

        // Cell is already marked -> Unmark
        if(cell_selected.GetIsMarked()){
            System.out.println("Unmarking cell.");
            cell_selected.SetIsMarked(false);
            return;
        }

        // Cell is not yet marked -> Automatically unflag and mark
        if(!cell_selected.GetIsMarked()){

            if(cell_selected.GetIsFlagged()){
                cells_flagged.remove(cell_selected);
                cell_selected.SetIsFlagged(false);
            }

            System.out.println("Marking cell.");
            cell_selected.SetIsMarked(true);

            return;
        }

        System.out.println("Shouldn't be here... (MARKING)");
    }




    // Randomly plant bombs
    public void PopulateGrid(Cell first_cell){
        int bombs_planted = 0;
        Random rand = new Random();

        // Convert 2D grid into a 1D list
        LinkedList<Cell> possible_cells = new LinkedList<>();
        LinkedList<Cell> adjacent_cells = new LinkedList<>(); // Backup list
        for(int y = 0; y < this.rows; y++){
            for(int x = 0; x < this.columns; x++){
                if(Math.abs(x - first_cell.GetX()) <= 1 && Math.abs(y - first_cell.GetY()) <= 1){
                    adjacent_cells.add(this.grid.GetCell(x,y));
                }
                else{
                    possible_cells.add(this.grid.GetCell(x,y));
                }
            }
        }

        possible_cells.remove(first_cell); // Don't place bomb on first cell
        adjacent_cells.remove(first_cell);
        LinkedList<Cell> select_from = possible_cells;

        // Repeat until bomb threshold reached from list of available cells
        while(bombs_planted < this.number_of_bombs){

            if(possible_cells.isEmpty()) select_from = adjacent_cells; // Use adjacent cells if no other cell possible

            // Select random cell to plant bomb
            int random_ind = rand.nextInt(select_from.size());
            Cell random_cell = select_from.get(random_ind);

            int random_x = random_cell.GetX();
            int random_y = random_cell.GetY();

            select_from.remove(random_ind); // Remove from remaining possible cells
            random_cell.SetIsBomb(true); // Plant bomb on selected cell

            // Update adjacent counters
            for(int x = random_x-1; x <= random_x+1; x++){
                if(x < 0 || x >= this.columns) continue; // Skip out-of-bounds

                for(int y = random_y-1; y <= random_y+1; y++){
                    if(y < 0 || y >= this.rows) continue; // Skip out-of-bounds

                    this.grid.GetCell(x,y).IncrementBombsNear();
                }
            }
            ++bombs_planted; // Update bomb counter
        }
    }



}
