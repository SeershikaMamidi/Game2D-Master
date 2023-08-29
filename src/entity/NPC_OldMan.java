package entity;

import com.company.GamePanel;

import java.util.Random;


public class NPC_OldMan extends Entity{
    public NPC_OldMan(GamePanel gp){
        super(gp);

        direction = "down";
        speed = 1;

        getImage();
        setDialogue();
    }
    public void getImage(){

        up1 = setup("/npc/oldman_up_1",gp.tileSize,gp.tileSize);
        up2 = setup("/npc/oldman_up_2",gp.tileSize,gp.tileSize);
        down1 = setup("/npc/oldman_down_1",gp.tileSize,gp.tileSize);
        down2 = setup("/npc/oldman_down_2",gp.tileSize,gp.tileSize);
        left1 = setup("/npc/oldman_left_1",gp.tileSize,gp.tileSize);
        left2 = setup("/npc/oldman_left_2",gp.tileSize,gp.tileSize);
        right1 = setup("/npc/oldman_right_1",gp.tileSize,gp.tileSize);
        right2 = setup("/npc/oldman_right_2",gp.tileSize,gp.tileSize);
    }

    //DIALOGUE TEXT
     public void setDialogue(){
        dialogues[0] = "HI, I AM WIZARD";
        dialogues[1] = "MY JOB IS TO PROTECT \nTHE LAND OF ASMAKITITES";
        dialogues[2] = "I AM BIT OLD BUT STILL \nCAN HANDLE CHUMPS LIKE YOU";
        dialogues[3] = "BE MY FRIEND OR MY ENEMY? \nBUT NOT A STRANGER ";

    }
    //setting behavior of the npc
    public void setAction(){
        actionLockCounter++;
        if(actionLockCounter == 120){
            Random random = new Random();
            int i = random.nextInt(100)+1; // pickup a number from 1 to 100

            if(i<=25){
                direction = "up";
            }
            if(i > 25 && i<= 50){
                direction = "down";
            }
            if(i>50 && i<= 75){
                direction = "left";
            }
            if (i>75 && i<=100){
                direction = "right";
            }
            actionLockCounter = 0;

        }


    }
    public void speak(){
        // doing it to customize for character specifics
        super.speak();

    }
}
