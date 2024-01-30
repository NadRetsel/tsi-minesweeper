public class Grid {
     private final int rows, columns;
     private final Cell[][] grid_of_cells;

     private int cells_remaining;

     public Grid(int rows, int columns){
         this.rows = rows;
         this.columns = columns;

         this.grid_of_cells = CreateGrid();
         this.cells_remaining = this.rows * this.columns;
     }

     public Cell GetCell(int x, int y){
         return this.grid_of_cells[x][y];
     }
     public int GetCellsRemaining(){
         return this.cells_remaining;
     }


     // Create a 2D columns x rows array of empty cells
     public Cell[][] CreateGrid(){
         Cell[][] grid = new Cell[this.columns][this.rows];

         for(int x = 0; x < this.columns; x++){
             for(int y = 0; y < this.rows; y++){
                 grid[x][y] = new Cell(x,y);
             }
         }
         return grid;
     }

     // Reveal the current cell and its adjacent neighbours (unless flagged or marked)
     public void RevealAdjacentCells(Cell current_cell){

         // Reveal current cell
         current_cell.SetIsRevealed(true);
         --this.cells_remaining;

         int current_x = current_cell.GetX();
         int current_y = current_cell.GetY();

         //  Recursively reveal adjacent cells
         if(current_cell.GetBombsNear() == 0){
             for(int adjacent_x = current_x-1; adjacent_x <= current_x+1; adjacent_x++){
                 if(adjacent_x < 0 || adjacent_x >= this.columns) continue; // Don't check outside-of-grid

                 for(int adjacent_y = current_y-1; adjacent_y <= current_y+1; adjacent_y++){
                     if(adjacent_y < 0 || adjacent_y >= this.rows || (adjacent_x == current_x && adjacent_y == current_y)) continue; // Don't check outside-of-grid OR same cell

                     Cell adjacent_cell = this.grid_of_cells[adjacent_x][adjacent_y];
                     if(adjacent_cell.GetIsRevealed() || adjacent_cell.GetIsFlagged() || adjacent_cell.GetIsMarked()) continue; // Don't check if already revealed, flagged, or marked
                     RevealAdjacentCells(adjacent_cell);
                 }
             }
         }
     }



    // Build the string ASCII art of the grid, including a rows and columns label
    public String GridString(boolean game_in_progress){
        String grid_string = "";


        for(int y = 0; y < this.rows; y++){

            // Row labels
            String y_label_padding = " ";
            for(int i = 0; i < String.valueOf(this.rows).length() - String.valueOf(y+1).length(); i++)  y_label_padding += " "; // Padding to keep label algined
            grid_string += AnsiColours.WHITE_BACKGROUND_BRIGHT + AnsiColours.BLACK_BOLD + " " + (y+1) + y_label_padding + AnsiColours.RESET;

            for (int x = 0; x < this.columns; x++) {
                Cell c = this.grid_of_cells[x][y];

                grid_string += c.GetIsRevealed() // If cell revealed -> Show number or bomb / Otherwise -> Flagged, marked, empty
                        ? " " + (c.GetIsBomb()
                            ? AnsiColours.RED + "*"
                            :  c.GetBombsNear() == 0
                                ? AnsiColours.CYAN + "-"
                                : AnsiColours.YELLOW + c.GetBombsNear()) + AnsiColours.RESET + " "
                        : "[" + (c.GetIsBomb() && !game_in_progress
                            ? AnsiColours.RED + (c.GetIsFlagged() ? AnsiColours.GREEN_BACKGROUND : (c.GetIsMarked() ? AnsiColours.PURPLE_BACKGROUND : "")) + "*"
                            : c.GetIsFlagged()
                                ? AnsiColours.GREEN + "F"
                                : c.GetIsMarked()
                                    ? AnsiColours.PURPLE + "?"
                                    : " ") + AnsiColours.RESET + "]";

                // Column padding to keep aligned based on largest column label
                String column_padding = "";
                for(int i = 0; i < String.valueOf(this.columns).length(); i++)  column_padding += " ";
                grid_string += column_padding;
            }
            grid_string += "\n";
        }

        // Column labels
        String x_label = " " + " ";
        for(int i = 0; i < String.valueOf(this.rows).length(); i++)  x_label += " ";

        // Padding
        for(int x = 0; x < this.columns; x++){
            x_label += (x+1);
            for(int i = 0; i < 3 + String.valueOf(this.columns).length() - String.valueOf(x+1).length(); i++)  x_label += " ";
        }
        grid_string =  (AnsiColours.WHITE_BACKGROUND_BRIGHT + AnsiColours.BLACK_BOLD) + " " + x_label + AnsiColours.RESET + "\n" + grid_string; // Add column x labels to the string

        return grid_string;
    }

}
