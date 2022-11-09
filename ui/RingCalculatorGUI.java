package ui;

import model.Coords;
import model.IllegalCoordsException;
import model.RingCalculator;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.Serial;
import java.util.HashSet;

/**
 * Swing GUI used to let the user input coordinates of a stronghold and get the approximate coordinates of the
 * other Strongholds in the ring. The user can display these coordinates as nether travel coordinates and remove
 * them from the list once they have visited the Stronghold they lead to.
 */
public class RingCalculatorGUI extends JFrame implements ActionListener {
    /**
     * Serial Version ID
     */
    @Serial
    private static final long serialVersionUID = 1L;
    /**
     * Panel for the top portion of the GUI that displays information about the Stronghold and ring
     */
    private JPanel top;
    /**
     * Panel for the bottom portion of the GUI that displays the calculated coordinates and actions the user can
     * do with them
     */
    private JPanel bottom;
    /**
     * If selected, it will display the coordinates in nether travel coordinates
     */
    private JRadioButton netherRadBtn = new JRadioButton();
    /**
     * Where the user enters the Stronghold x coordinate
     */
    private final JTextField xField = new JTextField(5);
    /**
     * Where the user enters the Stronghold z coordinate
     */
    private final JTextField zField = new JTextField(5);
    /**
     * Button that calculates the coordinates of other Strongholds in the ring
     */
    private JButton calculateBtn;
    /**
     * Resets the table so the user can do another calculation
     */
    private JButton resetBtn;
    /**
     * Removes a single set of coordinates from the table
     */
    private JButton removeBtn;
    /**
     * Label - Found Stronghold coords
     */
    private final JLabel coordsLbl = new JLabel("Stronghold Coords: ");
    /**
     * Label - Found Stronghold x coordinate
     */
    private final JLabel xLbl = new JLabel("X = ");
    /**
     * Label - Found Stronghold z coordinate
     */
    private final JLabel zLbl = new JLabel("Z = ");
    /**
     * Label - Stronghold ring, is updated to display the ring number
     */
    private final JLabel ringLbl = new JLabel("Ring: ");
    /**
     * Label - Number of Strongholds in the ring, is updated to display the number of Strongholds in the ring
     */
    private final JLabel numStrongholdsLbl = new JLabel("Num Strongholds: ");
    /**
     * Label - Strongholds left in ring, is updated each time the user removes a set of coordinates from the table
     * and when the calculation is first done.
     */
    private final JLabel strongholdsLeftLbl = new JLabel("Strongholds left: ");
    /*Error message for when program closes unexpectedly*/
    private static final String ERRORMSG =
            "An unexpected error has occured, please report this and what steps would be needed to reproduce it.";
    /**
     * Title for error message box
     */
    private static final String ERRORTITLE = "That shouldn't happen!";
    /**
     * List of coordinates to display in the table
     */
    private HashSet<Coords> coords;
    /**
     * Boolean to flag whether to use nether coordinates
     */
    boolean nether = false;
    /**
     * Instance of RingCalculator to use for calculations
     */
    RingCalculator r;
    /**
     * Table that holds the calculated coordinates
     */
    private JTable coordsTbl;
    /**
     * Column headers for the coordinates table to separate calculated coordinates by quadrant
     */
    private final String[] colNames = {"+/+", "+/-", "-/+", "-/-"};

    /**
     * Whether the user has set a default option for if the Coords are not in a ring
     */
    private boolean setDefault = false;
    //The following are all variables related to the dialog boxes used when trying to calculate coords
    /**
     * The choice the user selected in the dialog box
     */
    private int choice;

    /**
     * Starts GUI
     */
    public RingCalculatorGUI() {
        Container c = getContentPane();
        setTitle("Stronghold Ring Calculator");
        setSize(600, 500);
        setLocation(50, 50);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        BoxLayout box = new BoxLayout(c, BoxLayout.Y_AXIS);
        c.setLayout(box);
        //construct panels
        setTop();
        setBottom();
        //add panels to container
        c.add(top);
        c.add(bottom);
        setVisible(true);
    }

    /**
     * Constructs the top JPanel of the GUI that displays information about the user-input coordinates and the
     * Stronghold ring that those coordinates are in
     */
    private void setTop() {
        //GridBagLayout constraints
        GridBagConstraints gbc = new GridBagConstraints();
        top = new JPanel();
        TitledBorder border = BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(EtchedBorder.LOWERED), "Stronghold Information");
        top.setBorder(border);
        GridBagLayout bag = new GridBagLayout();
        top.setLayout(bag);
        //set panels
        JPanel xCoord = new JPanel();
        xCoord.add(xLbl);
        xCoord.add(xField);
        JPanel zCoord = new JPanel();
        zCoord.add(zLbl);
        zCoord.add(zField);
        //Add label and panels to top row of GridBagLayout
        gbc.gridx = 0;
        gbc.gridy = 0; //sets "coordinates" of GBL (GridBagLayout) to add component to
        top.add(coordsLbl, gbc); //Adds component with respect to GBL
        gbc.gridx = 1;
        top.add(xCoord, gbc);
        gbc.gridx = 2;
        top.add(zCoord, gbc);
        //Add second row of GBL
        gbc.gridx = 0;
        gbc.gridy = 1;
        calculateBtn = new JButton("Calculate");
        calculateBtn.addActionListener(this);
        top.add(calculateBtn, gbc);
        gbc.gridx = 1;
        gbc.gridwidth = 2; //make it take up two spaces
        numStrongholdsLbl.setAlignmentX(LEFT_ALIGNMENT); //left align so text looks centered
        top.add(numStrongholdsLbl, gbc);
        //Add third row
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 1;
        top.add(ringLbl, gbc);
        gbc.gridx = 1;
        gbc.gridwidth = 2;
        strongholdsLeftLbl.setAlignmentX(LEFT_ALIGNMENT);
        top.add(strongholdsLeftLbl, gbc);
    }

    /**
     * Constructs bottom JPanel that includes the calculated coordinates table and functions the user can use to
     * alter the data that is displayed.
     */
    private void setBottom() {
        bottom = new JPanel();
        TitledBorder border = BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(EtchedBorder.LOWERED), "Calculated Information");
        bottom.setBorder(border);
        //Create JPanel with reset button and nether radio button
        JPanel functions = new JPanel();
        resetBtn = new JButton("Reset");
        resetBtn.addActionListener(this);
        functions.add(resetBtn);
        removeBtn = new JButton("Remove Coords");
        removeBtn.addActionListener(this);
        functions.add(removeBtn);
        netherRadBtn = new JRadioButton("Nether Coords");
        netherRadBtn.addActionListener(this);
        functions.add(netherRadBtn);
        functions.setAlignmentX(RIGHT_ALIGNMENT);
        //Set BoxLayout for bottom panel
        BoxLayout b = new BoxLayout(bottom, BoxLayout.Y_AXIS);
        bottom.setLayout(b);
        bottom.add(functions);
        coordsTbl = new JTable(15, 4) {
            @Serial
            private static final long serialVersionUID = 1L;

            //Override isCellEditable to make table not editable
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        coordsTbl.setPreferredScrollableViewportSize(new Dimension(500, 300));
        coordsTbl.setFillsViewportHeight(true);
        coordsTbl.setDragEnabled(false);
        coordsTbl.setRowSelectionAllowed(false);
        for (int i = 0; i < colNames.length; i++) {
            coordsTbl.getColumnModel().getColumn(i).setHeaderValue(colNames[i]);
        }
        //If a cell is selected and the delete key is pressed remove data from cell
        coordsTbl.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                JTable source = (JTable) e.getSource();
                int row = source.getSelectedRow();
                int col = source.getSelectedColumn();
                Object entry = null;
                entry = source.getValueAt(row, col);

                if ((KeyEvent.VK_DELETE == e.getKeyChar() || KeyEvent.VK_BACK_SPACE == e.getKeyChar())
                    && entry != null) {
                    remove(row, col);
                }
            }
        });
        JScrollPane scroll = new JScrollPane(coordsTbl);
        bottom.add(scroll);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        //calculate button
        if (e.getSource() == calculateBtn) {
            double x = 0;
            double z = 0;
            try {
                x = Double.parseDouble(xField.getText().trim());
                z = Double.parseDouble(zField.getText().trim());
                r = new RingCalculator(x, z, false);
                coords = r.calcStrongholds(nether);
                fillData(coords);
                update();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Invalid text input, please enter proper coordinates.",
                                              "Invalid input", JOptionPane.ERROR_MESSAGE);
                xField.setText("");
                zField.setText("");
            } catch (IllegalCoordsException ex) {
                /**
                 * Message the box displays
                 */
                String message;
                /**
                 * Title of the dialog box
                 */
                String title;
                if (!setDefault) {
                    title = "Invalid Coordinates";
                    message = """
                              The provided coordinates were not calculated to be in a Stronghold ring
                              Would you like to enter the ring manaully, have the program guess the ring, or try again?
                              """;
                    String[] options = {"Enter Manually", "Guess", "Try Again"};
                    choice = JOptionPane.showOptionDialog(this, message, title, JOptionPane.YES_NO_CANCEL_OPTION,
                                                          JOptionPane.WARNING_MESSAGE, null, options, options[2]);

                    //Possibly set default choice
                    String[] defaultOptions = {"No", "Yes"};
                    int defaultChoice = JOptionPane.showOptionDialog(this, "Would you like to set this as your default option?\n"
                                                                           + "Please note that this cannot be changed unless the program is restarted.",
                                                                     "Set default option?", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null,
                                                                     defaultOptions, defaultOptions[1]);
                    if (defaultChoice == 1)
                        setDefault = true;
                }
                //What to do based on choice
                if (choice == 0) { //Manually enter a ring
                    Integer[] ringChoices = {1, 2, 3, 4, 5, 6, 7, 8};
                    title = "Ring Selection";
                    message = "Please choose a ring that the Stronghold is in.";
                    Integer i = (Integer) JOptionPane.showInputDialog(this, title, message, JOptionPane.QUESTION_MESSAGE,
                                                                      null, ringChoices, ringChoices[0]);
                    try {
                        r = new RingCalculator(x, z, true);
                    } catch (IllegalCoordsException ex1) { //Unexpected exception
                        JOptionPane.showMessageDialog(this, ERRORMSG, ERRORTITLE, JOptionPane.ERROR_MESSAGE);
                        System.exit(1);
                    }
                    r.setRing(i);
                    coords = r.calcStrongholds(nether);
                    fillData(coords);
                    update();
                } else if (choice == 1) { //Guess the ring
                    try {
                        r = new RingCalculator(x, z, true);
                    } catch (IllegalCoordsException ex1) { //Unexpected exception
                        JOptionPane.showMessageDialog(this, ERRORMSG, ERRORTITLE, JOptionPane.ERROR_MESSAGE);
                        System.exit(1);
                    }
                    r.guessRing(x, z);
                    coords = r.calcStrongholds(nether);
                    fillData(coords);
                    update();
                } else { //Try again
                    xField.setText("");
                    zField.setText("");
                }
            }
        }
        //reset button
        else if (e.getSource() == resetBtn) {
            xField.setText("");
            zField.setText("");
            r = null;
            //scuffed way to clear the table but maintain number of rows
            DefaultTableModel model = (DefaultTableModel) coordsTbl.getModel();
            model.setNumRows(0);
            model.setNumRows(15);
            update();
        }
        //nether radio button
        else if (e.getSource() == netherRadBtn) {
            nether = netherRadBtn.isSelected();
            if (coords != null && r != null) {
                if (nether) {
                    for (Coords c : coords) {
                        c.convertNether();
                    }
                } else {
                    for (Coords c : coords) {
                        c.convertOverworld();
                    }
                }
                fillData(coords);
            }
        }
        //remove button
        else if (e.getSource() == removeBtn) {
            int row = coordsTbl.getSelectedRow();
            int col = coordsTbl.getSelectedColumn();
            if (row != -1 && col != -1 && null != coordsTbl.getValueAt(row, col))
                remove(row, col);
            coordsTbl.clearSelection();
        }
    }

    /**
     * Fills the coordinates table with the data in the given set
     *
     * @param set The set of values to fill the table with
     */
    private void fillData(HashSet<Coords> set) {
        if (set == null)
            return;
        int c1 = 0, c2 = 0, c3 = 0, c4 = 0; //counters to keep up with number of entries in each column
        for (Coords c : set) {
            switch (c.getQuadrant()) {
                case POSPOS -> coordsTbl.setValueAt(c.toString(), c1++, 0);
                case POSNEG -> coordsTbl.setValueAt(c.toString(), c2++, 1);
                case NEGPOS -> coordsTbl.setValueAt(c.toString(), c3++, 2);
                case NEGNEG -> coordsTbl.setValueAt(c.toString(), c4++, 3);
                default -> JOptionPane.showMessageDialog(this, ERRORMSG, ERRORTITLE,
                                                         JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * Updates the GUI with information about the number of Storngholds in the ring, number of remaining
     * Strongholds, and the ring number. For use when resetting the program or when removing a set of coordinates.
     */
    private void update() {
        if (r == null) {
            numStrongholdsLbl.setText("Num Strongholds: ");
            strongholdsLeftLbl.setText("Strongholds Left: ");
            ringLbl.setText("Ring: ");
        } else {
            numStrongholdsLbl.setText("Num Strongholds: " + r.getNumStrongholds());
            strongholdsLeftLbl.setText("Strongholds Left: " + coords.size());
            ringLbl.setText("Ring: " + r.getRing());
        }
    }

    /**
     * Removes the data from the cell in the table with the given row and column
     *
     * @param row The row of the cell to clear
     * @param col The column of the cell to clear
     */
    private void remove(int row, int col) {
        try {
            if (coordsTbl.getValueAt(row, col) != null) {
                String s = (String) coordsTbl.getValueAt(row, col);
                for (Coords c : coords) {
                    if (s.equals(c.toString())) {
                        coords.remove(c);
                        coordsTbl.setValueAt(null, row, col);

                        //clear data then refill - will work on better solution in future
                        DefaultTableModel model = (DefaultTableModel) coordsTbl.getModel();
                        model.setNumRows(0);
                        model.setNumRows(15);

                        if (coords.size() > 0) {
                            fillData(coords);
                        }
                        break;
                    }
                }
            }
            update();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Could not remove Coordinates", "Don't do that",
                                          JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Main method to start the GUI
     *
     * @param args Arguments to be passed from command line
     */
    public static void main(String[] args) {
        new RingCalculatorGUI();
    }
}
