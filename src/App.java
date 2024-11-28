import javax.swing.JFrame;

public class App {
    public static void main(String[] args) throws Exception {
        int tileSize = 32;
        int rows = 16;
        int cols = 16;
        int boardWidth = tileSize * cols;
        int boardHeight = tileSize * rows;

        JFrame frame = new JFrame("Space Invaders");
        frame.setSize(boardWidth, boardHeight);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);

        SpaceInvaders spaceInvaders = new SpaceInvaders();
        spaceInvaders.requestFocus();
        frame.add(spaceInvaders);
        frame.pack();
        frame.setVisible(true);
    }
}
