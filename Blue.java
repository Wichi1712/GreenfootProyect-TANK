import greenfoot.*;
public class Blue extends Actor
{
    short t,count;
    boolean upKeyDown;
    int movt;
    byte health=3;
    short s[]={3,2,0,1};
    int previousChoice1=-1;
    private static final Color transparent = new Color(0,0,0,0);
    private GreenfootImage background;
    static final int m=100;
    short r[]={695,105};
    short r2[]={105,495};
    byte firstShootCount=0;
    public Blue()
    {
      setRotation(180);
      background = getImage();
    }
    
    protected void addedToWorld(World w)
    {
     //checks collision before adding   
     int rand=Greenfoot.getRandomNumber(r.length);
     int rand2=Greenfoot.getRandomNumber(r2.length);
     int i=0;
     while(getOneIntersectingObject(Actor.class)!=null && i++<m)
     {
         setLocation(r[rand], r2[rand2]);
     }
     if(i>=m)
     {
         w.removeObject(this);
         return;
     }
    }
    
    public void act() 
    {   
        DuoHelp();
        edgeCollision();
        ClickDuo();
        getPower();
        healthBar();
        decreaseHealth();
        death();
    } 
    
    public void DuoHelp()
    {
        MyWorld world1 = (MyWorld) getWorld();
        if(world1.solo==1)
        {
          if(count==0)
          {
            if(world1.counter1.sec==2157)
            {
              firstShootCount=1;
              world1.getBackground().drawImage(new GreenfootImage(" P_1 press W\n   P_2 press UP ",30,Color.GREEN , Color.BLACK),295, 536);
              world1.getBackground().drawImage(new GreenfootImage(" P_1 press W\n   P_2 press UP ",30,Color.YELLOW , new Color(0,0,0,0)),295, 537);
            }
          }else if(firstShootCount==1 && count>=1 && Greenfoot.isKeyDown("up") )
          {
              world1.setBackground(new GreenfootImage("Ground.png"));
              firstShootCount=2;
          }
        }
    }
    
    public void edgeCollision()
    {
        int w=getWorld().getWidth();
        int h=getWorld().getHeight();
        
        if(getX()>w-37)
          setLocation(w-37, getY());
        if(getY()>h-37)
          setLocation(getX(),h-37);
        if(getX()<37)
          setLocation(37, getY());
        if(getY()<37)
          setLocation(getX(), 37);
    }
    
    public void ClickDuo()
    {
        MyWorld world1 = (MyWorld) getWorld();
        
        //move
        int ds=0;
        if(Greenfoot.isKeyDown("up"))
        {
          movt++;
        }else {turn(t);movt=0;}
        if(movt>7)
        {
         ds++;
        }
        if(ds!=0)
        {
           move(ds*5);
        }
        
        //turn
        if(count % 2==0)
        {
            t=4;
        }else t=-4;
        
        //collision
        Actor BSheild=getOneIntersectingObject(RedShield.class);
        Actor B=getOneIntersectingObject(Red.class);
        if(!getObjectsInRange(48, Stone.class).isEmpty()
        || isTouching(BlueBot.class)
        || isTouching(RedBot.class)
        || Greenfoot.isKeyDown("up") && BSheild    !=null
        || Greenfoot.isKeyDown("up") && B          !=null)
        {
          move(-ds*5);
          count=0;
        }
        
        //shoot
        if(getWorld()!=null && getWorld().getObjects(Blue.class).size()>0 && upKeyDown != Greenfoot.isKeyDown("up"))
        {
          upKeyDown= !upKeyDown;
          if(upKeyDown)
          {
            count+=1;
            BlueProjectile bProjectile=new BlueProjectile();
            getWorld().addObject(bProjectile, getX(),getY());
            bProjectile.setRotation(getRotation());
          }
        }
        
        //increase enemy score when it touches bullet
        if(isTouching(RedProjectile.class))
        {
         world1.counter1.bscore++;
        }
        
        // Speed_IncOrDec
        Actor SpeedDec=getOneIntersectingObject(SpeedDec.class);
        if(SpeedDec!=null)
          move(-ds*3);
        
        Actor SpeedInc=getOneIntersectingObject(SpeedInc.class);
        if(SpeedInc !=null)
        {
           move(ds*16);
        }
    }
    
    public void getPower()
    {
        MyWorld world1 = (MyWorld) getWorld();
        
        //generates random power
        if(isTouching(Crate.class))
        {
           int size=getWorld().getObjects(BlueShield.class).size();
           int rand=Greenfoot.getRandomNumber(s.length-size-previousChoice1< 0 ? 0:2);
           rand+=size;
           if(previousChoice1>=0 && rand >= previousChoice1)rand++;
           if(rand==0 && world1.getObjects(BlueShield.class).isEmpty())
           {
           world1.addObject(new BlueShield(),getX(),getY());
           }
           else
           {
           if(rand==1)getWorld().addObject(new MiniBlue(5,45),400,300 );
           if(rand==2)getWorld().addObject(new BlueHealth(),400,300 );
           if(rand==3)getWorld().addObject(new BlueScore(world1.counter),400,300);
           }
           int n2=s[rand];
           previousChoice1=rand;
        }
    }
    
    public void healthBar()
    {
        if(getWorld()!=null && getWorld().getObjects(Blue.class).size()>0 && getWorld().getObjects(BlueHealthBar.class).size()>0)
        {
             Blue blue = getWorld().getObjects(Blue.class).get(0);
             BlueHealthBar blueHealthBar = getWorld().getObjects(BlueHealthBar.class).get(0);
             GreenfootImage image = new GreenfootImage(background);
             if(blue.health==3)
             {
               blueHealthBar.setImage("full.png");
             }
             if(blue.health==2)
             {
                blueHealthBar.setImage("half.png");
             }
             if(blue.health==1)
             {
               blueHealthBar.setImage("dead.png");
             }
        }
    }
    
    public void decreaseHealth()
    {
        Actor RedProjectile=getOneIntersectingObject(RedProjectile.class);
        if(RedProjectile !=null)
        {
          health-=1;
          getWorld().removeObject(RedProjectile);
        }
    }
    
    public void death()
    {
        MyWorld world1 = (MyWorld) getWorld();
        Actor box=getOneObjectAtOffset(0, 0,Stone.class);
        if(health ==0 || box!=null)
        {
            world1.addObject(new Explosion(), getX(), getY());
            getWorld().removeObject(this);
        }
    }
}