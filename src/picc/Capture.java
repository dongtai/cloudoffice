package picc;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.URL;
import java.net.URLConnection;

import javax.imageio.ImageIO;
import javax.swing.JApplet;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

import netscape.javascript.JSObject;

public class Capture extends JApplet
  implements ActionListener, KeyListener, FocusListener
{
  /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
private JButton start;
  private JButton cancel;
  private BufferedImage get;
  private String url = "http://localhost:8888/uploadpic";
  private String other;

  public Capture()
  {
    doStart();
  }

  private void doStart()
  {
    try
    {
      setVisible(false);
      Robot ro = new Robot();
      Toolkit tk = Toolkit.getDefaultToolkit();
      Dimension di = tk.getScreenSize();
      Rectangle rec = new Rectangle(0, 0, di.width, di.height);
      BufferedImage bi = ro.createScreenCapture(rec);
      JFrame jf = new JFrame();
      Temp temp = new Temp(jf, bi, di.width, di.height);
      jf.getContentPane().add(temp, "Center");

      jf.getContentPane().addKeyListener(this);
      jf.setUndecorated(true);
      jf.setSize(di);
      jf.setVisible(true);
      jf.setAlwaysOnTop(true);
    }
    catch (Exception exe)
    {
      exe.printStackTrace();
    }
  }

  public void keyTyped(KeyEvent e) {
    if (e.getKeyCode() == 27)
    {
      System.exit(0);
    }
  }

  public void keyPressed(KeyEvent e) {
    if (e.getKeyCode() == 27)
    {
      System.exit(0);
    }
  }

  public void keyReleased(KeyEvent e) {
    if (e.getKeyCode() == 27)
    {
      System.exit(0);
    }
  }

  public void actionPerformed(ActionEvent ae)
  {
    if (ae.getSource() == this.start)
      doStart();
    else if (ae.getSource() == this.cancel)
      System.exit(0);
    else ae.getSource();
  }

  public void close()
  {
    System.exit(0);
  }

  public void init() {
    String tempurl = getParameter("url");
    if (tempurl != null)
    {
      this.url = tempurl;
    }
    this.other = getParameter("other");
    if (this.other == null)
    {
      this.other = "";
    }
    addFocusListener(this);
    addKeyListener(this);
  }

  public static void main(String[] args) {
    new Capture();
  }

  public void focusGained(FocusEvent e)
  {
  }

  public void focusLost(FocusEvent e)
  {
  }

  private class Temp extends JPanel
    implements MouseListener, MouseMotionListener
  {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private BufferedImage bi;
    private int width;
    private int height;
    private int startX;
    private int startY;
    private int endX;
    private int endY;
    private int tempX;
    private int tempY;
    private JFrame jf;
    private Rectangle select;
    private Cursor cs;
    private States current;
    private Rectangle[] rec;
    private int currentX;
    private int currentY;
    private Point p;
    private boolean showTip;
    final Capture this$0;

    private void initRecs()
    {
      this.rec = new Rectangle[8];
      for (int i = 0; i < this.rec.length; i++)
        this.rec[i] = new Rectangle();
    }

    public void paintComponent(Graphics g)
    {
      g.drawImage(this.bi, 0, 0, this.width, this.height, this);
      g.setColor(Color.RED);
      g.drawLine(this.startX, this.startY, this.endX, this.startY);
      g.drawLine(this.startX, this.endY, this.endX, this.endY);
      g.drawLine(this.startX, this.startY, this.startX, this.endY);
      g.drawLine(this.endX, this.startY, this.endX, this.endY);
      int x = this.startX >= this.endX ? this.endX : this.startX;
      int y = this.startY >= this.endY ? this.endY : this.startY;
      this.select = new Rectangle(x, y, Math.abs(this.endX - this.startX), Math.abs(this.endY - this.startY));
      int x1 = (this.startX + this.endX) / 2;
      int y1 = (this.startY + this.endY) / 2;
      g.fillRect(x1 - 2, this.startY - 2, 5, 5);
      g.fillRect(x1 - 2, this.endY - 2, 5, 5);
      g.fillRect(this.startX - 2, y1 - 2, 5, 5);
      g.fillRect(this.endX - 2, y1 - 2, 5, 5);
      g.fillRect(this.startX - 2, this.startY - 2, 5, 5);
      g.fillRect(this.startX - 2, this.endY - 2, 5, 5);
      g.fillRect(this.endX - 2, this.startY - 2, 5, 5);
      g.fillRect(this.endX - 2, this.endY - 2, 5, 5);
      this.rec[0] = new Rectangle(x - 5, y - 5, 10, 10);
      this.rec[1] = new Rectangle(x1 - 5, y - 5, 10, 10);
      this.rec[2] = new Rectangle((this.startX <= this.endX ? this.endX : this.startX) - 5, y - 5, 10, 10);
      this.rec[3] = new Rectangle((this.startX <= this.endX ? this.endX : this.startX) - 5, y1 - 5, 10, 10);
      this.rec[4] = new Rectangle((this.startX <= this.endX ? this.endX : this.startX) - 5, (this.startY <= this.endY ? this.endY : this.startY) - 5, 10, 10);
      this.rec[5] = new Rectangle(x1 - 5, (this.startY <= this.endY ? this.endY : this.startY) - 5, 10, 10);
      this.rec[6] = new Rectangle(x - 5, (this.startY <= this.endY ? this.endY : this.startY) - 5, 10, 10);
      this.rec[7] = new Rectangle(x - 5, y1 - 5, 10, 10);
      if (this.showTip)
      {
        g.setColor(Color.CYAN);
        g.fillRect(this.p.x, this.p.y, 400, 20);
        g.setColor(Color.RED);
        g.drawRect(this.p.x, this.p.y, 400, 20);
        g.setColor(Color.BLACK);
        g.drawString("按住鼠标左键不放选择截图区,双击左键截图并保存,单击鼠标右键退出程序!", this.p.x, this.p.y + 15);
      }
    }

    private void initSelect(States state)
    {
      switch (Capture._cls1.$SwitchMap$picc$States[state.ordinal()])
      {
      case 1:
        this.currentX = 0;
        this.currentY = 0;
        break;
      case 2:
        this.currentX = (this.endX <= this.startX ? 1 : 3);
        this.currentY = 0;
        break;
      case 3:
        this.currentX = (this.endX <= this.startX ? 3 : 1);
        this.currentY = 0;
        break;
      case 4:
        this.currentX = 0;
        this.currentY = (this.startY <= this.endY ? 2 : 4);
        break;
      case 5:
        this.currentX = 0;
        this.currentY = (this.startY <= this.endY ? 4 : 2);
        break;
      case 6:
        this.currentY = (this.startY <= this.endY ? 2 : 4);
        this.currentX = (this.endX <= this.startX ? 1 : 3);
        break;
      case 7:
        this.currentY = (this.startY <= this.endY ? 2 : 4);
        this.currentX = (this.endX <= this.startX ? 3 : 1);
        break;
      case 8:
        this.currentY = (this.startY <= this.endY ? 4 : 2);
        this.currentX = (this.endX <= this.startX ? 1 : 3);
        break;
      case 9:
        this.currentY = (this.startY <= this.endY ? 4 : 2);
        this.currentX = (this.endX <= this.startX ? 3 : 1);
        break;
      default:
        this.currentX = 0;
        this.currentY = 0;
      }
    }

    public void mouseMoved(MouseEvent me)
    {
      doMouseMoved(me);
      initSelect(this.current);
      if (this.showTip)
      {
        this.p = me.getPoint();
        repaint();
      }
    }

    private void doMouseMoved(MouseEvent me)
    {
      if (this.select.contains(me.getPoint()))
      {
        setCursor(new Cursor(13));
        this.current = States.MOVE;
      }
      else {
        States[] st = States.values();
        for (int i = 0; i < this.rec.length; i++) {
          if (!this.rec[i].contains(me.getPoint()))
            continue;
          this.current = st[i];
          setCursor(st[i].getCursor());
          return;
        }

        setCursor(this.cs);
        this.current = States.DEFAULT;
      }
    }

    public void mouseExited(MouseEvent mouseevent)
    {
    }

    public void mouseEntered(MouseEvent mouseevent)
    {
    }

    public void mouseDragged(MouseEvent me)
    {
      int x = me.getX();
      int y = me.getY();
      if (this.current == States.MOVE)
      {
        this.startX += x - this.tempX;
        this.startY += y - this.tempY;
        this.endX += x - this.tempX;
        this.endY += y - this.tempY;
        this.tempX = x;
        this.tempY = y;
      }
      else if ((this.current == States.EAST) || (this.current == States.WEST))
      {
        if (this.currentX == 1)
        {
          this.startX += x - this.tempX;
          this.tempX = x;
        }
        else {
          this.endX += x - this.tempX;
          this.tempX = x;
        }
      }
      else if ((this.current == States.NORTH) || (this.current == States.SOUTH))
      {
        if (this.currentY == 2)
        {
          this.startY += y - this.tempY;
          this.tempY = y;
        }
        else {
          this.endY += y - this.tempY;
          this.tempY = y;
        }
      }
      else if ((this.current == States.NORTH_EAST) || (this.current == States.NORTH_EAST) || (this.current == States.SOUTH_EAST) || (this.current == States.SOUTH_WEST))
      {
        if (this.currentY == 2)
        {
          this.startY += y - this.tempY;
          this.tempY = y;
        }
        else {
          this.endY += y - this.tempY;
          this.tempY = y;
        }
        if (this.currentX == 1)
        {
          this.startX += x - this.tempX;
          this.tempX = x;
        }
        else {
          this.endX += x - this.tempX;
          this.tempX = x;
        }
      }
      else {
        this.startX = this.tempX;
        this.startY = this.tempY;
        this.endX = me.getX();
        this.endY = me.getY();
      }
      repaint();
    }

    public void mousePressed(MouseEvent me)
    {
      this.showTip = false;
      this.tempX = me.getX();
      this.tempY = me.getY();
    }

    public void mouseReleased(MouseEvent me)
    {
      if (me.isPopupTrigger())
        if (this.current == States.MOVE)
        {
          this.showTip = true;
          this.p = me.getPoint();
          this.startX = 0;
          this.startY = 0;
          this.endX = 0;
          this.endY = 0;
          repaint();
        }
        else {
          this.jf.dispose();
        }
    }

    public void mouseClicked(MouseEvent me)
    {
      if (me.isMetaDown())
      {
        System.exit(0);
      }
      if (me.getClickCount() == 2)
      {
        Point p = me.getPoint();
        if (this.select.contains(p))
          if ((this.select.x + this.select.width < getWidth()) && (this.select.y + this.select.height < getHeight()))
          {
            Capture.this.get = this.bi.getSubimage(this.select.x, this.select.y, this.select.width, this.select.height);
            try
            {
              writeDataByte(Capture.this.url, Capture.this.get);
            }
            catch (Exception e)
            {
              e.printStackTrace();
            }
            this.jf.dispose();
            JSObject.getWindow(this$0).eval("javascript:ExtTalk.screen()");
            System.exit(0);
          }
          else {
            int wid = this.select.width;
            int het = this.select.height;
            if (this.select.x + this.select.width >= getWidth())
              wid = getWidth() - this.select.x;
            if (this.select.y + this.select.height >= getHeight())
              het = getHeight() - this.select.y;
            Capture.this.get = this.bi.getSubimage(this.select.x, this.select.y, wid, het);
            this.jf.dispose();
            System.exit(0);
          }
      }
    }

    private void writeDataByte(String serverURL, BufferedImage data)
    {
    	System.out.println("开始传送");
      try {
        URL url = new URL(serverURL);
        URLConnection con = url.openConnection();

        con.setDoInput(true);
        con.setDoOutput(true);
        con.setRequestProperty("Content-Type", "application/octet-stream");
        DataOutputStream out = new DataOutputStream(con.getOutputStream());
        ImageIO.write(Capture.this.get, "JPG", out);

        out.close();
        DataInputStream in2 = new DataInputStream(con.getInputStream());
        in2.close();
      }
      catch (Exception e)
      {
        e.printStackTrace();
      }
    }

    public Temp(JFrame jf, BufferedImage bi, int width, int height)
    {
      this.this$0 = Capture.this;
      this.select = new Rectangle(0, 0, 0, 0);
      this.cs = new Cursor(1);
      this.current = States.DEFAULT;
      this.p = new Point();
      this.showTip = true;
      this.jf = jf;
      this.bi = bi;
      this.width = width;
      this.height = height;
      addMouseListener(this);
      addMouseMotionListener(this);
      addKeyListener(new KeyAdapter() {
        public void keyPressed(KeyEvent e) {
          if (e.getKeyCode() == 27)
          {
            System.exit(0);
          }
        }
      });
      jf.addKeyListener(new KeyAdapter() {
        public void keyPressed(KeyEvent e) {
          if (e.getKeyCode() == 27)
          {
            System.exit(0);
          }
        }
      });
      initRecs();
    }
  }

  static class _cls1
  {
    static final int[] $SwitchMap$picc$States = new int[States.values().length];

    static {
      try { $SwitchMap$picc$States[States.DEFAULT.ordinal()] = 1;
      } catch (NoSuchFieldError localNoSuchFieldError1)
      {
      }
      try {
        $SwitchMap$picc$States[States.EAST.ordinal()] = 2;
      }
      catch (NoSuchFieldError localNoSuchFieldError2) {
      }
      try {
        $SwitchMap$picc$States[States.WEST.ordinal()] = 3;
      }
      catch (NoSuchFieldError localNoSuchFieldError3) {
      }
      try {
        $SwitchMap$picc$States[States.NORTH.ordinal()] = 4;
      }
      catch (NoSuchFieldError localNoSuchFieldError4) {
      }
      try {
        $SwitchMap$picc$States[States.SOUTH.ordinal()] = 5;
      }
      catch (NoSuchFieldError localNoSuchFieldError5) {
      }
      try {
        $SwitchMap$picc$States[States.NORTH_EAST.ordinal()] = 6;
      }
      catch (NoSuchFieldError localNoSuchFieldError6) {
      }
      try {
        $SwitchMap$picc$States[States.NORTH_WEST.ordinal()] = 7;
      }
      catch (NoSuchFieldError localNoSuchFieldError7) {
      }
      try {
        $SwitchMap$picc$States[States.SOUTH_EAST.ordinal()] = 8;
      }
      catch (NoSuchFieldError localNoSuchFieldError8) {
      }
      try {
        $SwitchMap$picc$States[States.SOUTH_WEST.ordinal()] = 9;
      }
      catch (NoSuchFieldError localNoSuchFieldError9)
      {
      }
    }
  }
}