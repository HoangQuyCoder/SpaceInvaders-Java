import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.ArrayList;
import java.util.Random;

public class SpaceInvaders extends JPanel implements ActionListener, KeyListener {
    public class Block {
        int x;
        int y;
        int width;
        int height;
        Image img;
        boolean alive = true;
        boolean used = false;

        Block(int x, int y, int width, int height, Image img) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
            this.img = img;
        }
    }

    int tileSize = 32;
    int rows = 16;
    int cols = 16;
    int boardWidth = tileSize * cols;
    int boardHeight = tileSize * rows;

    Image shipImg;
    Image alienImg;
    Image alienCyanImg;
    Image alienMagentaImg;
    Image alienYellowImg;
    ArrayList<Image> alienImgArray;

    // ship
    int shipWidth = tileSize * 2;
    int shipHeight = tileSize;
    int shipX = tileSize * (cols / 2) - tileSize;
    int shipY = boardHeight - tileSize * 2;
    int shipVelocity = tileSize;
    Block ship;

    // aliens
    int alienWidth = tileSize * 2;
    int alienHeight = tileSize;
    int alienX = tileSize;
    int alienY = tileSize;
    int alienRows = 2;
    int alienColumns = 3;
    int alienCount = 0; // alien for defeat
    int alienVelocityX = 1;
    ArrayList<Block> alienArray;

    // bullets
    int bulletWidth = tileSize / 8;
    int bulletHeight = tileSize / 2;
    int bulletVelocityY = -10;
    ArrayList<Block> bulletArray;

    Timer gameLoop;
    int score = 0;
    boolean gameOver = false;

    public SpaceInvaders() {
        setPreferredSize(new Dimension(boardWidth, boardHeight));
        setBackground(Color.black);
        setFocusable(true);
        addKeyListener(this);

        // load img
        alienImg = new ImageIcon(getClass().getResource("./img/alien.png")).getImage();
        alienMagentaImg = new ImageIcon(getClass().getResource("./img/alien-magenta.png")).getImage();
        alienYellowImg = new ImageIcon(getClass().getResource("./img/alien-yellow.png")).getImage();
        alienCyanImg = new ImageIcon(getClass().getResource("./img/alien-cyan.png")).getImage();
        shipImg = new ImageIcon(getClass().getResource("./img/ship.png")).getImage();

        alienImgArray = new ArrayList<Image>();
        alienImgArray.add(alienImg);
        alienImgArray.add(alienMagentaImg);
        alienImgArray.add(alienYellowImg);
        alienImgArray.add(alienCyanImg);

        // ship
        ship = new Block(shipX, shipY, shipWidth, shipHeight, shipImg);

        // aliens
        alienArray = new ArrayList<Block>();
        createAliens();

        // bullets
        bulletArray = new ArrayList<Block>();

        // game timer
        gameLoop = new Timer(1000 / 60, this);
        gameLoop.start();
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }

    public void draw(Graphics g) {
        g.drawImage(ship.img, ship.x, ship.y, ship.width, ship.height, null);

        // aliens
        for (int i = 0; i < alienArray.size(); i++) {
            Block alien = alienArray.get(i);
            if (alien.alive) {
                g.drawImage(alien.img, alien.x, alien.y, alien.width, alien.height, null);
            }
        }

        // bullets
        g.setColor(Color.white);
        for (int i = 0; i < bulletArray.size(); i++) {
            Block bullet = bulletArray.get(i);
            if (!bullet.used) {
                // g.drawRect(bullet.x, bullet.y, bullet.width, bullet.height);
                g.fillRect(bullet.x, bullet.y, bullet.width, bullet.height);
            }
        }

        // score
        g.setColor(Color.red);
        g.setFont(new Font("Arial", Font.PLAIN, 32));
        if (gameOver) {
            g.drawString("Game Over: " + String.valueOf(score), 10, 35);
        } else {
            g.drawString(String.valueOf(score), 10, 35);
        }
    }

    public void move() {
        // alien
        for (int i = 0; i < alienArray.size(); i++) {
            Block alien = alienArray.get(i);
            if (alien.alive) {
                alien.x += alienVelocityX;

                // if alien touches the borders
                if (alien.x + alien.width >= boardWidth || alien.x <= 0) {
                    alienVelocityX *= -1;
                    alien.x += alienVelocityX * 2;

                    // move all aliens up by one row
                    for (int j = 0; j < alienArray.size(); j++) {
                        alienArray.get(j).y += alienHeight;
                    }
                }

                if (alien.y >= ship.y) {
                    gameOver = true;
                }
            }
        }

        // bullets
        for (int i = 0; i < bulletArray.size(); i++) {
            Block bullet = bulletArray.get(i);
            bullet.y += bulletVelocityY;

            // bullet collision with aliens
            for (int j = 0; j < alienArray.size(); j++) {
                Block alien = alienArray.get(j);
                if (!bullet.used && alien.alive && detectCollision(bullet, alien)) {
                    bullet.used = true;
                    alien.alive = false;
                    alienCount--;
                    score += 100;
                }
            }
        }

        // clear bullets
        while (bulletArray.size() > 0 && (bulletArray.get(0).used || bulletArray.get(0).y < 0)) {
            bulletArray.remove(0); // removes the first element of the array
        }

        // next level
        if (alienCount == 0) {
            // increase the number of aliens in columns and rows by 1
            score += alienColumns * alienRows * 100; // bonus points :)
            alienColumns = Math.min(alienColumns + 1, cols / 2 - 2); // cap at 16/2 -2 = 6
            alienRows = Math.min(alienRows + 1, rows - 6); // cap at 16-6 = 10
            alienArray.clear();
            bulletArray.clear();
            createAliens();
        }
    }

    // check collion two rectangle
    public boolean detectCollision(Block a, Block b) {
        return a.x < b.x + b.width &&
                a.x + a.width > b.x &&
                a.y < b.y + b.height &&
                a.y + a.height > b.y;
    }

    public void createAliens() {
        Random random = new Random();

        for (int r = 0; r < alienRows; r++) {
            for (int c = 0; c < alienColumns; c++) {
                int randomIndex = random.nextInt(alienImgArray.size());
                Block alien = new Block(alienX + c * alienWidth, alienY + r * alienHeight, alienWidth, alienHeight,
                        alienImgArray.get(randomIndex));
                alienArray.add(alien);
            }
        }
        alienCount = alienArray.size();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        move();
        repaint();
        if (gameOver) {
            gameLoop.stop();
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (gameOver) {
            ship.x = shipX;
            alienArray.clear();
            bulletArray.clear();
            score = 0;
            alienVelocityX = 1;
            alienColumns = 3;
            alienRows = 2;
            gameOver = false;
            createAliens();
            gameLoop.start();
        } else if (e.getKeyCode() == KeyEvent.VK_LEFT && ship.x - shipVelocity >= 0) {
            ship.x -= shipVelocity;
        } else if (e.getKeyCode() == KeyEvent.VK_RIGHT && ship.x + shipVelocity + ship.width <= boardWidth) {
            ship.x += shipVelocity;
        } else if (e.getKeyCode() == KeyEvent.VK_SPACE) {
            Block bullet = new Block(ship.x + shipWidth * 15 / 32, ship.y, bulletWidth, bulletHeight, null);
            bulletArray.add(bullet);
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }
}