import javax.swing.*;
import javax.swing.text.Position;
import java.awt.*;
import java.awt.event.*;
import java.awt.event.MouseListener;
import java.util.*;
import java.util.Timer;
import java.util.Vector;
public class Minesweeper implements MouseListener
{
    private JFrame frame;
    private JPanel panel, top_panel;
    private JButton button[][], restart_button;
    private JLabel bomb_label, time_label;
    private int frame_width = 500, frame_height = 500;
    private int button_width = 10, button_height = 10;
    private int timer_count = 0, bomb_count = 10, flag_count=bomb_count;
    private int map[][], map_aroundBomb[][];
    private boolean timer_enable = true;
    private boolean buttonIsPress[][] = new boolean[button_width][button_height];
    private ImageIcon icon = new ImageIcon(this.getClass().getResource("/flag.png"));
    public Minesweeper()
    {
        createFrame();
    }

    public void restart()
    {
        timer_count = 0;
        bomb_count = 10;
        timer_enable = true;
        flag_count = bomb_count;
        bomb_label.setText("Bomb Count : "+Integer.toString(flag_count));
        for(int i=0 ; i<button_width ; i++)
            for(int j=0 ; j<button_height ; j++)
            {
                buttonIsPress[i][j] = false;
                button[i][j].setText("");
                button[i][j].setBackground(Color.WHITE);
                button[i][j].setIcon(null);
            }
        createMap();
        aroundBomb();
    }

    public void createMap()
    {
        int count = 0;
        map = new int[button_width][button_height];
        while(count!=10)
        {
            int x = (int)(Math.random()*button_width);
            int y = (int)(Math.random()*button_height);
            if(map[x][y]==0)
            {
                map[x][y]=1;
                count++;
            }
        }
    }

    public void createTopPanel()
    {
        top_panel = new JPanel();
        top_panel.setLayout(null);
        top_panel.setPreferredSize(new Dimension(0, 50));
        bomb_label = new JLabel();
        time_label = new JLabel();
        restart_button = new JButton("Restart");
        restart_button.setActionCommand("r");
        restart_button.addMouseListener(this);
        bomb_label.setText("Bomb Count : "+Integer.toString(flag_count));
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                if(timer_enable)
                {
                    time_label.setText("Time : "+Integer.toString(timer_count++));
                }
            }
        };
        new Timer().scheduleAtFixedRate(task,0,1000);
        bomb_label.setBounds(frame_width/6,0, 100,50);
        restart_button.setBounds((frame_width/2)-50,12,100,25);
        time_label.setBounds(2*frame_width/3,0,100,50);
        top_panel.add(bomb_label);
        top_panel.add(restart_button);
        top_panel.add(time_label);
    }

    public void createBombPanel()
    {
        panel = new JPanel();
        panel.setLayout(new GridLayout(button_width,button_height));
        button = new JButton[button_width][button_height];
        for(int i=0 ; i<button_width ; i++)
        {
            for(int j=0 ; j<button_height ; j++)
            {
                button[i][j] = new JButton();
                button[i][j].setBackground(Color.WHITE);
                button[i][j].setActionCommand(i+" "+j);
                button[i][j].addMouseListener(this);
                panel.add(button[i][j]);
            }
        }
    }

    public void createFrame()
    {
        createTopPanel();
        createBombPanel();
        createMap();
        aroundBomb();
        frame = new JFrame("Minesweeper");
        frame.add(top_panel,BorderLayout.NORTH);
        frame.add(panel);
        frame.setSize(frame_width,frame_height);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    public void aroundBomb()
    {
        map_aroundBomb = new int[button_width][button_height];
        for(int i=0 ; i<button_width ; i++)
        {
            for(int j = 0 ; j<button_height ; j++)
            {
                if(map[i][j]==1)
                    map_aroundBomb[i][j] = -1;
                else
                {
                    for(int m=-1 ; m<=1 ; m++)
                    {
                        for(int n=-1 ; n<=1 ; n++)
                        {
                            int row = i+m;
                            int col = j+n;
                            if((row>=0 && row<button_width && col>=0 && col<button_height) && map[row][col]==1)
                                map_aroundBomb[i][j]++;
                        }
                    }
                }
            }
        }
    }

    public boolean isWin()
    {
        int count = 0;
        for(int i =0 ; i<button_width ; i++)
            for(int j=0 ; j<button_height ; j++)
                if(map[i][j]==1 && button[i][j].getIcon()!=null)
                    count++;
        if(count == bomb_count)
            return true;
        return false;
    }

    @Override
    public void mouseClicked(MouseEvent e)
    {
        String command[] = ((JButton)e.getSource()).getActionCommand().split(" ");
        if(command[0].equals("r"))
        {
            restart();
        }
        else
        {
            int row = Integer.parseInt(command[0]);
            int col = Integer.parseInt(command[1]);
            if(e.getButton()==MouseEvent.BUTTON1)
            {
                if(map[row][col] == 1 && buttonIsPress[row][col]==false)
                {
                    //按到炸彈
                    button[row][col].setBackground(Color.RED);
                    for(int i=0 ; i<button_width ; i++)
                        for(int j=0 ; j<button_height ; j++)
                            if(map[i][j]==1)
                                button[i][j].setText("*");
                    JOptionPane.showMessageDialog(frame, "game over");
                    restart();
                }
                else if(map_aroundBomb[row][col] == 0 && buttonIsPress[row][col] == false)
                {
                     Vector<position>vector = new Vector<position>();
                     vector.add(new position(row, col));
                     for(int i= 0 ; i<vector.size() ; i++)
                     {
                         for(int m=-1 ; m<=1 ; m++)
                         {
                             for(int n=-1 ; n<=1 ; n++)
                             {
                                 int tempRow = vector.get(i).getRow()+m;
                                 int tempCol = vector.get(i).getCol()+n;
                                 if((tempRow>=0 && tempRow<button_width && tempCol>=0 && tempCol<button_height) && map_aroundBomb[tempRow][tempCol]==0)
                                 {
                                     boolean flag = false;
                                     for(int k = 0 ; k<vector.size() ; k++)
                                     {
                                         if(tempRow == vector.get(k).getRow() && tempCol == vector.get(k).getCol())
                                         {
                                             flag = true;
                                             break;
                                         }
                                     }
                                     if(flag ==false)
                                     {
                                         vector.add(new position(tempRow, tempCol));
                                     }
                                 }
                             }
                         }
                     }
                     for(int i=0 ; i<vector.size() ; i++)
                     {
                         for(int m=-1 ; m<=1 ; m++)
                         {
                             for(int n=-1 ; n<=1 ; n++)
                             {
                                 int tempRow = vector.get(i).getRow()+m;
                                 int tempCol = vector.get(i).getCol()+n;
                                 if((tempRow>=0 && tempRow<button_width && tempCol>=0 && tempCol<button_height))
                                 {
                                    if(map_aroundBomb[tempRow][tempCol]!=0)
                                        button[tempRow][tempCol].setText(Integer.toString(map_aroundBomb[tempRow][tempCol]));
                                    button[tempRow][tempCol].setBackground(Color.GRAY);
                                    buttonIsPress[tempRow][tempCol] = true;
                                    button[tempRow][tempCol].setIcon(null);
                                 }
                             }
                         }
                     }
                }
                else if(buttonIsPress[row][col]==false)
                {
                    button[row][col].setBackground(Color.GRAY);
                    buttonIsPress[row][col] = true;
                    int count = map_aroundBomb[row][col];
                    if(count!=0)
                        button[row][col].setText(Integer.toString(count));
                }

            }
            else if(e.getButton() == MouseEvent.BUTTON3 && button[row][col].getBackground()!=Color.GRAY)
            {
                if(button[row][col].getIcon()!=null)
                {
                    button[row][col].setIcon(null);
                    buttonIsPress[row][col]=false;
                    flag_count++;
                    bomb_label.setText("Bomb Count : "+Integer.toString(flag_count));
                }
                else if(flag_count>0)
                {
                    Image temp = icon.getImage().getScaledInstance(button[row][col].getWidth(),
                            button[row][col].getHeight(), icon.getImage().SCALE_DEFAULT);
                    icon = new ImageIcon(temp);
                    button[row][col].setIcon(icon);
                    buttonIsPress[row][col]=true;
                    flag_count--;
                    bomb_label.setText("Bomb Count : "+Integer.toString(flag_count));
                }
            }
            if(isWin())
                JOptionPane.showMessageDialog(frame, "You Win!!!");
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {

    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    class position
    {
        private int row, col;
        position(int row, int col)
        {
            this.row = row;
            this.col = col;
        }
        public int getRow(){return row;}
        public int getCol(){return col;}

    }
}
