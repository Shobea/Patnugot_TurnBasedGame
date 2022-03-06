package com.example.patnugot_turnbasedgame;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.pm.ActivityInfo;
import android.content.res.ColorStateList;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.Objects;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    Button btnSlash, btnStun, btnDrink,btnTry;
    TextView txtHeroName, txtHeroHP, txtMonsterName, txtMonsterHP, txtAttackDetails;
    ProgressBar barHeroHealth, barMonsterHealth;

    String heroName = "Davis";
    int heroHealth = 2000;
    int heroMinDamage = 100;
    int heroMaxDamage = 150;
    int heroMaxHealth=2000;
    int heroMinHealth=0;
    int heroPercentHP;

    String monsterName = "Titanoboa";
    int monsterHealth = 3200;
    int monsterMinDamage = 70;
    int monsterMaxDamage = 100;
    int monsterMaxHealth=3200;
    int monsterMinHealth=0;
    int monsterPercentHP;

    int potionHealth=200;
    int potionMax=3;
    int extra;

    int stunMax=3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        Objects.requireNonNull(getSupportActionBar()).hide();
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        txtHeroHP = findViewById(R.id.numHeroHealth);
        txtMonsterHP = findViewById(R.id.numMonsterHealth);
        txtHeroName = findViewById(R.id.heroName);
        txtMonsterName = findViewById(R.id.monsterName);
        txtAttackDetails=findViewById(R.id.attackDetails);

        btnSlash = findViewById(R.id.slash);
        btnSlash.setOnClickListener(this::onClick);
        btnStun = findViewById(R.id.stun);
        btnStun.setOnClickListener(this::onClick);
        btnDrink = findViewById(R.id.drinkPotion);
        btnDrink.setOnClickListener(this::onClick);
        btnTry=findViewById(R.id.tryAgain);
        btnTry.setOnClickListener(this::onClick);

        barHeroHealth=findViewById(R.id.heroHPBar);
        barHeroHealth.setMax(100);
        barMonsterHealth=findViewById(R.id.monsterHPBar);
        barMonsterHealth.setMax(100);

        txtHeroName.setText(heroName);
        txtMonsterName.setText(monsterName);

        txtHeroHP.setText(String.valueOf(heroHealth));
        txtMonsterHP.setText(String.valueOf(monsterHealth));
        txtAttackDetails.setText("");

    }
    @SuppressLint("SetTextI18n")
    public void onClick(View view) {

        Random randomizer= new Random();
        int heroDamage= randomizer.nextInt(heroMaxDamage-heroMinDamage)+heroMinDamage;
        int monsterDamage= randomizer.nextInt(monsterMaxDamage-monsterMinDamage)+monsterMinDamage;

        switch (view.getId()) {
            case R.id.slash: //hero will attack monster
                heroAnimation();
                monsterHealth=monsterHealth-heroDamage;
                txtAttackDetails.setText("Davis dealt " +heroDamage +" damage!");
                healthBars();
                if (monsterHealth<0){
                    txtAttackDetails.setText("Hero is victorious!");
                    txtMonsterHP.setText(String.valueOf(monsterMinHealth));
                    buttonsDisappear();
                }
                if (heroHealth>=1){ // if hero is alive, there will be a short pause, then the monster will attack the hero
                    final Handler handler = new Handler();
                    Timer pause = new Timer();
                    pause.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            handler.post(() -> { //if monster is dead
                                if (monsterHealth<=0){
                                    txtAttackDetails.setText("Titanoboa has been defeated!");
                                    txtMonsterHP.setText(String.valueOf(monsterMinHealth));
                                }
                                else { // if monster is alive, monster will attack
                                    monsterAnimation();
                                    heroHealth = heroHealth - monsterDamage;
                                    txtHeroHP.setText(String.valueOf(heroHealth));
                                    txtAttackDetails.setText("Titanoboa dealt " + monsterDamage + " damage!");
                                    healthBars();
                                    if (heroHealth<=0){ //after monster attacks and hero dies
                                        txtAttackDetails.setText("Hero has been defeated!");
                                        healthBars();
                                        buttonsDisappear();
                                        txtHeroHP.setText(String.valueOf(heroMinHealth));
                                    }
                                }});
                        }
                    }, 800);}
               break;
            case R.id.tryAgain: // once monster/hero health reaches 0, this button will appear
                heroHealth = 2000;
                monsterHealth = 3200;
                txtHeroHP.setText(String.valueOf(heroHealth));
                txtMonsterHP.setText(String.valueOf(monsterHealth));
                txtAttackDetails.setText("");
                buttonsAppear();
                healthBars();
                break;

            case R.id.drinkPotion: // 200hp is replenished
                heroHealth = heroHealth + potionHealth;
                healthBars();
                potionMax--;
                btnDrink.setText(">Drink Potion(" + (potionMax) + ")");
                if (potionMax == 0) { //hero can only use potion 3 times
                    btnDrink.setVisibility(View.GONE);
                    btnDrink.setText("Drink Potion (3)");
                    potionMax = 3;
                }
                if (heroHealth>=heroMaxHealth) { // if health exceeds max health
                    heroHealth=heroHealth-potionHealth;
                    extra=heroMaxHealth-heroHealth;
                    heroHealth=heroHealth+extra;
                    txtAttackDetails.setText("Davis health has increased by "+extra+"!");
                    txtHeroHP.setText(String.valueOf(heroHealth));
                }
                else if (heroHealth<=1800){
                    txtAttackDetails.setText("Davis' health has increased by "+potionHealth);
                    txtHeroHP.setText(String.valueOf(heroHealth));
                }
                break;
            case R.id.stun: // 50/50 chance for enemy to lose turn
                java.util.Random random = new java.util.Random();
                int stunNum = random.nextInt(2) + 1;
                if (stunNum == 1) {
                    monsterHealth=monsterHealth-300;
                    txtMonsterHP.setText(String.valueOf(monsterHealth));
                    txtAttackDetails.setText("Titanoboa has been stunned!\nIt loses its turn and its\nhealth has been decreased by 300!");
                    stunMax--;
                    btnStun.setText(">Stun(" + (stunMax) + ")");
                    heroAnimation();
                    healthBars();
                } else {
                    monsterAnimation();
                    if (heroHealth >= 1) {
                        monsterAnimation();
                        heroHealth = heroHealth - monsterDamage;
                        txtHeroHP.setText(String.valueOf(heroHealth));
                        healthBars();
                        txtAttackDetails.setText("Titanoboa dealt " + monsterDamage + " damage!");
                    } else {
                        txtHeroHP.setText(String.valueOf(heroMinHealth));
                        txtAttackDetails.setText("Hero has been defeated!");
                        buttonsDisappear();
                    }
                }
                if (stunMax == 0) {
                    btnStun.setVisibility(View.GONE);
                    btnStun.setText(">Stun(3)");
                    stunMax = 3;
                }
                break;
        }
        }
    public void buttonsDisappear (){ //call this method for buttons to disappear
        btnSlash.setVisibility(View.GONE);
        btnStun.setVisibility(View.GONE);
        btnDrink.setVisibility(View.GONE);
        btnTry.setVisibility(View.VISIBLE);
    }
    public void buttonsAppear (){ //call this method for buttons to appear
        btnSlash.setVisibility(View.VISIBLE);
        btnStun.setVisibility(View.VISIBLE);
        btnDrink.setVisibility(View.VISIBLE);
        btnTry.setVisibility(View.GONE);
    }
    public void heroAnimation () { //call this method for hero to move
        ImageView img=findViewById(R.id.heroImg);
        Animation animation1=
                AnimationUtils.loadAnimation(getApplicationContext(),
                        R.anim.blink);
        img.startAnimation(animation1);
        animation1.setDuration(1000);
        animation1.setRepeatCount(1);
    }
    public void monsterAnimation(){ //call this method for monster to move
        ImageView img2=findViewById(R.id.monsterImg);
        Animation animation2=
                AnimationUtils.loadAnimation(getApplicationContext(),
                        R.anim.blink);
        img2.startAnimation(animation2);
        animation2.setDuration(1000);
        animation2.setRepeatCount(1);
    }
    public void healthBars(){ //call this method for health bars to adjust

        txtHeroHP.setText(String.valueOf(heroHealth));
        txtMonsterHP.setText(String.valueOf(monsterHealth));
        heroPercentHP = heroHealth * 100 / heroMaxHealth;
        monsterPercentHP = monsterHealth * 100 / monsterMaxHealth;
        barHeroHealth.setProgress( heroPercentHP, true);
        barMonsterHealth.setProgress( monsterPercentHP, true);


        //for hero health bar to change color
        if (heroPercentHP > 30 && heroPercentHP <= 75 ) {
            barHeroHealth.setProgressTintList(ColorStateList.valueOf(getResources().getColor(R.color.yellow)));// for the r.color thingy kay go to colors and make some color
        } else if ( heroPercentHP >= 0 && heroPercentHP <= 30 ) {
            barHeroHealth.setProgressTintList(ColorStateList.valueOf(getResources().getColor(R.color.red)));
        } else {
            barHeroHealth.setProgressTintList(ColorStateList.valueOf((getResources().getColor(R.color.green))));
        }

        //for monster health bar to change color
        if (monsterPercentHP > 30 && monsterPercentHP <= 75 ) {
            barMonsterHealth.setProgressTintList(ColorStateList.valueOf(getResources().getColor(R.color.yellow)));// for the r.color thingy kay go to colors and make some color
        } else if ( monsterPercentHP >= 0 && monsterPercentHP <= 30 ) {
            barMonsterHealth.setProgressTintList(ColorStateList.valueOf(getResources().getColor(R.color.red)));
        } else {
            barMonsterHealth.setProgressTintList(ColorStateList.valueOf((getResources().getColor(R.color.green))));
        }}}



