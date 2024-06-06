import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.io.*;

import javax.sound.sampled.*;

public class SpaceShit extends JPanel implements ActionListener, KeyListener {
    private final Timer timer;
    private int naveX, naveY;
    private final int meteorX;
    private final int meteorY;
    private int radio;
    private boolean up, down, left, right;
    private boolean isMoving;
    private ArrayList<Laser> lasers;
    private Clip disparoSound, movimientoSound, musicaSound;

    public SpaceShit() {
        setPreferredSize(new Dimension(1336, 768));
        setBackground(Color.BLACK);
        setFocusable(true);
        addKeyListener(this);

        naveX = 150;
        naveY = 300;
        meteorX = 150;
        meteorY = 150;
        lasers = new ArrayList<>();

        timer = new Timer(5, this);
        timer.start();

        // Cargar sonidos
        cargarSonidos();
    }

    private void cargarSonidos() {
        try {
            // Cargar sonido de disparo
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File("disparo.wav"));
            disparoSound = AudioSystem.getClip();
            disparoSound.open(audioInputStream);

            // Cargar sonido de movimiento
            audioInputStream = AudioSystem.getAudioInputStream(new File("banshee.wav"));
            movimientoSound = AudioSystem.getClip();
            movimientoSound.open(audioInputStream);
            
            //Cargar musica
            audioInputStream = AudioSystem.getAudioInputStream(new File("musica.wav"));
            musicaSound = AudioSystem.getClip();
            musicaSound.open(audioInputStream);
        } catch (IOException | LineUnavailableException | UnsupportedAudioFileException ex) {
        }
    }

    // Método para reproducir sonido de disparo
    private void reproducirDisparo() {
            disparoSound.setFramePosition(0);
            disparoSound.start();
    }

    // Método para reproducir sonido de movimiento
    private void reproducirMovimiento() {
           movimientoSound.setFramePosition(0);
            movimientoSound.start();    
    }

     //   private void reproducirMusica() {
     //      musicaSound.setFramePosition(0);
      //      musicaSound.start();    
    
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Dibujar la nave
    int[] PunX = {naveX, naveX + 50, naveX + 100};
    int[] PunY = {naveY + 100, naveY, naveY + 100};
    int nPun = 3; // Número de vértices
    g.setColor (Color.yellow);
    g.fillPolygon(PunX, PunY, nPun);
    
        // Dibujar meteor
        g.setColor(Color.white);
        int diametro = 100;
        g.fillOval(meteorX, meteorY, diametro, diametro);
        
        // Dibujar los láseres
        for (Laser laser : lasers) {
            g.setColor(Color.GREEN);
            g.fillRect(laser.getX(), laser.getY(), 10, 15);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // Mover la nave según las teclas presionadas
        if (isMoving) {
            if (up) {
                naveY -= 5;
            }
            if (down) {
                naveY += 5;
            }
            if (left) {
                naveX -= 5;
            }
            if (right) {
                naveX += 5;
            }
        }

        // Verificar si la nave ha sobrepasado los límites de la ventana
        if (naveX < 0) {
            naveX = getWidth() - 75; // Aparecer en el lado derecho de la ventana
        } else if (naveX > getWidth() - 75) {
            naveX = 0; // Aparecer en el lado izquierdo de la ventana
        }
        if (naveY < 0) {
            naveY = getHeight() - 75; // Aparecer en la parte inferior de la ventana
        } else if (naveY > getHeight() - 75) {
            naveY = 0; // Aparecer en la parte superior de la ventana
        }

        // Mover los láseres
        for (int i = 0; i < lasers.size(); i++) {
            Laser laser = lasers.get(i);
            laser.move();
            if (laser.getY() < 0) {
                lasers.remove(i);
                i--;
            }
        }

        repaint();
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();
        if (key == KeyEvent.VK_UP) {
            up = true;
            isMoving = true;
            reproducirMovimiento();
        }
        if (key == KeyEvent.VK_DOWN) {
            down = true;
            isMoving = true;
            reproducirMovimiento();
        }
        if (key == KeyEvent.VK_LEFT) {
            left = true;
            isMoving = true;
            reproducirMovimiento();
        }
        if (key == KeyEvent.VK_RIGHT) {
            right = true;
            isMoving = true;
            reproducirMovimiento();
        }

        // Disparar láser al presionar la tecla de espacio
        if (key == KeyEvent.VK_SPACE) {
            lasers.add(new Laser(naveX + 45, naveY));
            reproducirDisparo();
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int key = e.getKeyCode();
        if (key == KeyEvent.VK_UP) {
            up = false;
        }
        if (key == KeyEvent.VK_DOWN) {
            down = false;
        }
        if (key == KeyEvent.VK_LEFT) {
            left = false;
        }
        if (key == KeyEvent.VK_RIGHT) {
            right = false;
        }

        // Detener el movimiento y el sonido si todas las teclas están sueltas
        if (!up && !down && !left && !right) {
            isMoving = false;
            if (movimientoSound != null) {
                movimientoSound.stop();
            }
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {}

    public static void main(String[] args) {
        JFrame frame = new JFrame("Space Shit");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(new SpaceShit());
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}

class Laser {
    private int x, y;

    public Laser(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void move() {
        y -= 5;
    }
}