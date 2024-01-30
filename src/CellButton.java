import javax.swing.*;

public class CellButton extends JButton { // Change class name
    private final Cell cell;
    public CellButton(String action_command, String label, Cell cell) {
        super(label);
        this.setActionCommand(action_command);
        this.cell = cell;
    }

    public Cell GetCell(){
        return this.cell;
    }

}