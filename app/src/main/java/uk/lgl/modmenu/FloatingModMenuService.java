/*
 * Credit:
 *
 * Octowolve - Mod menu: https://github.com/z3r0Sec/Substrate-Template-With-Mod-Menu
 * And hooking: https://github.com/z3r0Sec/Substrate-Hooking-Example
 * VanHoevenTR A.K.A Nixi: https://github.com/LGLTeam/VanHoevenTR_Android_Mod_Menu
 * MrIkso - Mod menu: https://github.com/MrIkso/FloatingModMenu
 * Rprop - https://github.com/Rprop/And64InlineHook
 * MJx0 A.K.A Ruit - KittyMemory: https://github.com/MJx0/KittyMemory
 * */

package uk.lgl.modmenu;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;

import android.text.Html;
import android.text.InputFilter;
import android.text.InputType;
import android.text.method.DigitsKeyListener;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;

import static uk.lgl.modmenu.StaticActivity.cacheDir;

public class FloatingModMenuService extends Service {
    private MediaPlayer FXPlayer;
    public View mFloatingView;
    private Button close;
    private Button kill;
    private LinearLayout mButtonPanel;
    public RelativeLayout mCollapsed;
    public LinearLayout mExpanded;
    private RelativeLayout mRootContainer;
    public WindowManager mWindowManager;
    public WindowManager.LayoutParams params;
    private LinearLayout patches;
    //private FrameLayout rootFrame;
    private ImageView startimage;
    private LinearLayout view1;
    private LinearLayout view2;
    private LinearLayout Btns;
    private AlertDialog alert;

    private native String Title();

    private native String Icon();

    private native boolean EnableSounds();

    private native int IconSize();

    public native void Changes(int feature, int value);

    private native String SliderString(int feature, int value);

    private native String[] getFeatureList();

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    //Override our Start Command so the Service doesnt try to recreate itself when the App is closed
    public int onStartCommand(Intent intent, int i, int i2) {
        return Service.START_NOT_STICKY;
    }

    //When this Class is called the code in this function will be executed
    @Override
    public void onCreate() {
        super.onCreate();
        //A little message for the user when he opens the app
        //Toast.makeText(this, Toast(), Toast.LENGTH_LONG).show();
        //Init Lib

        // When you change the lib name, change also on Android.mk file
        // Both must have same name
        System.loadLibrary("MyLibName");

        initFloating();

        final Handler handler = new Handler();
        handler.post(new Runnable() {
            public void run() {
                Thread();
                handler.postDelayed(this, 1000);
            }
        });
    }

    //Here we write the code for our Menu
    private void initFloating() {
        FrameLayout frameLayout = new FrameLayout(getBaseContext());
        RelativeLayout relativeLayout = new RelativeLayout(getBaseContext());
        // rootFrame = new FrameLayout(getBaseContext()); // Global markup
        // mRootContainer = new RelativeLayout(getBaseContext()); // Markup on which two markups of the icon and the menu itself will be placed
        mCollapsed = new RelativeLayout(getBaseContext()); // Markup of the icon (when the menu is minimized)
        mExpanded = new LinearLayout(getBaseContext()); // Menu markup (when the menu is expanded)
        patches = new LinearLayout(getBaseContext());
        Btns = new LinearLayout(getBaseContext());
        frameLayout.setLayoutParams(new FrameLayout.LayoutParams(-2, -2));
        relativeLayout.setLayoutParams(new FrameLayout.LayoutParams(-2, -2));
        mCollapsed.setLayoutParams(new RelativeLayout.LayoutParams(-2, -2));
        mCollapsed.setVisibility(View.VISIBLE);
        ImageView imageView = new ImageView(getBaseContext());
        startimage = imageView;

        imageView.setLayoutParams(new RelativeLayout.LayoutParams(-2, -2));
        // int applyDimension = (int) TypedValue.applyDimension(1, (float) 60, getResources().getDisplayMetrics());
        view1 = new LinearLayout(getBaseContext());
        view2 = new LinearLayout(getBaseContext());
        mButtonPanel = new LinearLayout(getBaseContext()); // Layout of option buttons (when the menu is expanded)

        //********** Gradients **********
        GradientDrawable gradientDrawable = new GradientDrawable();
        gradientDrawable.setShape(GradientDrawable.RECTANGLE);
        String str = "#ffff2323";
        gradientDrawable.setColor(Color.parseColor(str));
        gradientDrawable.setStroke(3, Color.parseColor(str));
        gradientDrawable.setCornerRadius(8.0f);

        GradientDrawable gradientDrawable2 = new GradientDrawable();
        gradientDrawable.setShape(GradientDrawable.RECTANGLE);
        gradientDrawable2.setColor(-1);
        gradientDrawable2.setStroke(3, -1);
        gradientDrawable2.setCornerRadius(8.0f);

        GradientDrawable gradientDrawable3 = new GradientDrawable();
        gradientDrawable3.setShape(GradientDrawable.RECTANGLE);
        gradientDrawable3.setColor(Color.parseColor(str));
        gradientDrawable3.setStroke(3, -1);
        gradientDrawable3.setCornerRadius(8.0f);

        frameLayout.setLayoutParams(new FrameLayout.LayoutParams(-1, -1));
        // mRootContainer.setLayoutParams(new FrameLayout.LayoutParams(-2, -2));
        mCollapsed.setLayoutParams(new RelativeLayout.LayoutParams(-2, -2));
        mCollapsed.setVisibility(View.VISIBLE);

        //********** Mod menu image **********
        startimage = new ImageView(getBaseContext());
        startimage.setLayoutParams(new RelativeLayout.LayoutParams(-2, -2));
        int applyDimension = (int) TypedValue.applyDimension(1, (float) IconSize(), getResources().getDisplayMetrics());
        startimage.getLayoutParams().height = applyDimension;
        startimage.getLayoutParams().width = applyDimension;
        startimage.requestLayout();
        startimage.setScaleType(ImageView.ScaleType.FIT_XY);
        byte[] decode = Base64.decode(Icon(), 0);
        startimage.setImageBitmap(BitmapFactory.decodeByteArray(decode, 0, decode.length));
        startimage.setImageAlpha(200);
        ((ViewGroup.MarginLayoutParams) startimage.getLayoutParams()).topMargin = convertDipToPixels(10);

        //********** Mod menu box **********
        mExpanded.setVisibility(View.GONE);
        mExpanded.setBackgroundColor(Color.parseColor("#FF0000"));
        mExpanded.setBackground(gradientDrawable);
        mExpanded.setGravity(17);
        mExpanded.setOrientation(LinearLayout.VERTICAL);
        mExpanded.setPadding(5, 0, 5, 0);
       /* if (dpi() > 400)
            mExpanded.setLayoutParams(new LinearLayout.LayoutParams(550, -2)); //-1
        if (dpi() > 350)
            mExpanded.setLayoutParams(new LinearLayout.LayoutParams(500, -2)); //-1
        else*/
        mExpanded.setLayoutParams(new LinearLayout.LayoutParams(400, -2)); //-1

        ScrollView scrollView = new ScrollView(getBaseContext());
        /*   if (dpi() > 400)
            scrollView.setLayoutParams(new LinearLayout.LayoutParams(-1, 450));
        else*/
        scrollView.setLayoutParams(new LinearLayout.LayoutParams(-1, 350));

        //********** Feature list **********
        patches.setLayoutParams(new LinearLayout.LayoutParams(-1, -1));
        patches.setBackgroundColor(Color.parseColor("#000000"));
        patches.setPadding(5, 5, 5, 5);
        patches.setBackground(gradientDrawable2);
        patches.setOrientation(LinearLayout.VERTICAL);

        this.Btns.setLayoutParams(new LinearLayout.LayoutParams(-1, -2));
        this.Btns.setBackgroundColor(0);
        this.Btns.setGravity(5);
        this.Btns.setPadding(0, 0, 5, 0);
        this.Btns.setOrientation(LinearLayout.HORIZONTAL);

        //********** Title text **********
        TextView textView = new TextView(getBaseContext());
        textView.setText(Title());
        if (dpi() > 400)
            textView.setTextSize(15.0f);
        else
            textView.setTextSize(18.0f);
        textView.setTextColor(-1);
        textView.setGravity(17);
        textView.setShadowLayer(12.0f, 0.0f, 0.0f, Color.parseColor("#000000"));
        textView.setPadding(0, 8, 0, 8);
        textView.setLayoutParams(new LinearLayout.LayoutParams(-1, -2));
        LinearLayout.LayoutParams layoutParams2 = new LinearLayout.LayoutParams(-2, -2);
        layoutParams2.gravity = 17;
        //textView.setLayoutParams(layoutParams2);

        //********** Close button **********
        close = new Button(this);
        close.setBackgroundColor(Color.parseColor("#1C2A35"));
        close.setText("CLOSE");
        close.setTextSize(11.0f);
        close.setAllCaps(false);
        close.setBackground(gradientDrawable3);
        close.setShadowLayer(8.0f, 0.0f, 0.0f, Color.parseColor("#000000"));
        close.setPadding(3, 3, 3, 3);
        close.setTextColor(Color.parseColor("#82CAFD"));
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(-2, dp(40));
        layoutParams.topMargin = 9;
        layoutParams.bottomMargin = 3;
        close.setLayoutParams(layoutParams);

        //********** Add views **********
        new LinearLayout.LayoutParams(-1, dp(25)).topMargin = dp(2);
        frameLayout.addView(relativeLayout);
        relativeLayout.addView(this.mCollapsed);
        relativeLayout.addView(this.mExpanded);
        mCollapsed.addView(startimage);
        mExpanded.addView(textView);
        mExpanded.addView(scrollView);
        scrollView.addView(patches);
        this.mExpanded.addView(this.Btns);
        this.Btns.addView(this.close);

        this.mFloatingView = frameLayout;
        if (Build.VERSION.SDK_INT >= 26) {
            params = new WindowManager.LayoutParams(-2, -2, 2038, 8, -3);
        } else {
            params = new WindowManager.LayoutParams(-2, -2, 2002, 8, -3);
        }
        params.gravity = 8388659;
        params.x = 0;
        params.y = 100;
        mWindowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        mWindowManager.addView(mFloatingView, params);
        RelativeLayout relativeLayout2 = mCollapsed;
        LinearLayout linearLayout = mExpanded;
        frameLayout.setOnTouchListener(onTouchListener());
        startimage.setOnTouchListener(onTouchListener());
        initMenuButton(relativeLayout2, linearLayout);
        CreateMenuList();
    }

    private View.OnTouchListener onTouchListener() {
        return new View.OnTouchListener() {
            final View collapsedView = mCollapsed;
            final View expandedView = mExpanded;
            private float initialTouchX;
            private float initialTouchY;
            private int initialX;
            private int initialY;

            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        initialX = params.x;
                        initialY = params.y;
                        initialTouchX = motionEvent.getRawX();
                        initialTouchY = motionEvent.getRawY();
                        return true;
                    case MotionEvent.ACTION_UP:
                        int rawX = (int) (motionEvent.getRawX() - initialTouchX);
                        int rawY = (int) (motionEvent.getRawY() - initialTouchY);

                        //The check for Xdiff <10 && YDiff< 10 because sometime elements moves a little while clicking.
                        //So that is click event.
                        if (rawX < 10 && rawY < 10 && isViewCollapsed()) {
                            //When user clicks on the image view of the collapsed layout,
                            //visibility of the collapsed layout will be changed to "View.GONE"
                            //and expanded view will become visible.
                            collapsedView.setVisibility(View.GONE);
                            expandedView.setVisibility(View.VISIBLE);
                            playSound(Uri.fromFile(new File(cacheDir + "OpenMenu.ogg")));
                            //Toast.makeText(FloatingModMenuService.this, Html.fromHtml(Toast()), Toast.LENGTH_SHORT).show();
                        }
                        return true;
                    case MotionEvent.ACTION_MOVE:
                        //Calculate the X and Y coordinates of the view.
                        params.x = initialX + ((int) (motionEvent.getRawX() - initialTouchX));
                        params.y = initialY + ((int) (motionEvent.getRawY() - initialTouchY));

                        //Update the layout with new X & Y coordinate
                        mWindowManager.updateViewLayout(mFloatingView, params);
                        return true;
                    default:
                        return false;
                }
            }
        };
    }

    private boolean hide = false;

    //Initialize event handlers for buttons, etc.
    private void initMenuButton(final View view2, final View view3) {
        startimage.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                view2.setVisibility(View.GONE);
                view3.setVisibility(View.VISIBLE);
            }
        });

        close.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if (hide) {
                    view2.setVisibility(View.VISIBLE);
                    view2.setAlpha(0);
                    view3.setVisibility(View.GONE);
                    Toast.makeText(view.getContext(), "Icon hidden. Remember the hidden icon position", Toast.LENGTH_LONG).show();
                } else {
                    view2.setVisibility(View.VISIBLE);
                    view2.setAlpha(0.95f);
                    view3.setVisibility(View.GONE);
                }
                playSound(Uri.fromFile(new File(cacheDir + "Back.ogg")));
                //Log.i("LGL", "Close");
            }
        });
    }

    private void CreateMenuList() {
        String[] listFT = getFeatureList();
        for (int i = 0; i < listFT.length; i++) {
            final int feature = i;
            String str = listFT[i];
            if (str.contains("SeekBar_")) {
                String[] split = str.split("_");
                addSeekBar(feature, split[1], Integer.parseInt(split[2]), Integer.parseInt(split[3]), new InterfaceInt() {
                    public void OnWrite(int i) {
                        Changes(feature, i);
                    }
                });
            } else if (str.contains("Button_")) {
                addButton(str.replace("Button_", ""), new InterfaceBtn() {
                    public void OnWrite() {
                        Changes(feature, 0);
                    }
                });
            } else if (str.contains("ButtonHide_")) {
                addButton(str.replace("ButtonHide_", ""), new InterfaceBtn() {
                    public void OnWrite() {
                        hide = !hide;
                    }
                });
            } else if (str.contains("Text_")) {
                addText(str.replace("Text_", ""));
            }
        }
    }

    public TextView addText(String str) {
        TextView textView = new TextView(this);
        textView.setText(str);
        textView.setTextColor(Color.parseColor("#000000"));
        textView.setTextSize(11.0f);
        textView.setShadowLayer(8.0f, 0.0f, 0.0f, Color.parseColor("#53AEFC"));
        textView.setGravity(3);
        textView.setLayoutParams(new LinearLayout.LayoutParams(-1, -2));
        textView.setPadding(5, 0, 0, 0);
        this.patches.addView(textView);
        return textView;
    }

    public void addButton(String feature, final InterfaceBtn interfaceBtn) {
        final GradientDrawable gradientDrawable = new GradientDrawable();
        gradientDrawable.setShape(GradientDrawable.RECTANGLE);
        String str2 = "#86FF5900";
        gradientDrawable.setColor(Color.parseColor(str2));
        gradientDrawable.setStroke(3, Color.parseColor(str2));
        gradientDrawable.setCornerRadius(8.0f);
        final GradientDrawable gradientDrawable2 = new GradientDrawable();
        gradientDrawable2.setShape(GradientDrawable.RECTANGLE);
        gradientDrawable2.setColor(0);
        gradientDrawable2.setStroke(3, Color.parseColor(str2));
        gradientDrawable2.setCornerRadius(8.0f);

        final Button button = new Button(this);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(-1, -2);
        layoutParams.setMargins(7, 5, 7, 5);
        button.setText(feature + " [OFF]");
        button.setTextColor(Color.parseColor("#000000"));
        button.setTextSize(11.0f);
        button.setShadowLayer(8.0f, 0.0f, 0.0f, Color.parseColor("#53AEFC"));
        button.setAllCaps(false);
        button.setBackground(gradientDrawable2);
        LinearLayout.LayoutParams layoutParams2 = new LinearLayout.LayoutParams(-1, dp(40));
        button.setPadding(3, 3, 3, 3);
        layoutParams2.bottomMargin = 5;
        button.setLayoutParams(layoutParams2);
        final String feature2 = feature;
        button.setOnClickListener(new View.OnClickListener() {
            private boolean isActive = true;

            public void onClick(View v) {
                interfaceBtn.OnWrite();
                if (isActive) {
                    playSound(Uri.fromFile(new File(cacheDir + "On.ogg")));
                    button.setText(feature2 + " [ON]");
                    button.setBackground(gradientDrawable);
                    isActive = false;
                    return;
                }
                playSound(Uri.fromFile(new File(cacheDir + "Off.ogg")));
                button.setText(feature2 + " [OFF]");
                button.setBackground(gradientDrawable2);
                isActive = true;
            }
        });
        patches.addView(button);
    }

    private void addSeekBar(final int featurenum, final String feature, final int prog, int max, final InterfaceInt interInt) {
        LinearLayout linearLayout = new LinearLayout(this);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(-1, -1);
        linearLayout.setPadding(10, 5, 0, 5);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setGravity(17);
        linearLayout.setLayoutParams(layoutParams);

        //Textview
        final TextView textView = new TextView(this);
        String str = SliderString(featurenum, 0);


        if (str != null) //Show text progress instead number
            textView.setText(Html.fromHtml("<font face='roboto'>" + feature + " : " + str + "</font>"));
        else  //If string is null, show number instead
            textView.setText(Html.fromHtml("<font face='roboto'>" + feature + " : " + prog + "</font>"));
        textView.setTextColor(Color.parseColor("#000000"));
        textView.setShadowLayer(8.0f, 0.0f, 0.0f, Color.parseColor("#53AEFC"));
        textView.setTextSize(11.0f);
        textView.setGravity(3);
        textView.setLayoutParams(new LinearLayout.LayoutParams(-1, -2));
        textView.setPadding(5, 0, 0, 0);

        //Seekbar
        SeekBar seekBar = new SeekBar(this);
        seekBar.setMax(max);
        seekBar.getProgressDrawable().setColorFilter(Color.parseColor("#ffff2323"), PorterDuff.Mode.MULTIPLY);
        seekBar.getThumb().setColorFilter(Color.parseColor("#ffff2323"), PorterDuff.Mode.MULTIPLY);
        seekBar.setPadding(25, 10, 35, 10);
        seekBar.setLayoutParams(new LinearLayout.LayoutParams(-1, -1));
        LinearLayout.LayoutParams layoutParams2 = new LinearLayout.LayoutParams(-1, -2);
        layoutParams2.bottomMargin = 10;
        seekBar.setLayoutParams(layoutParams2);
        seekBar.setProgress(prog);

        final TextView textView2 = textView;
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            public void onStopTrackingTouch(SeekBar seekBar) {
            }

            int l;

            public void onProgressChanged(SeekBar seekBar, int i, boolean z) {
                if (l < i) {
                    playSound(Uri.fromFile(new File(cacheDir + "SliderIncrease.ogg")));
                } else {
                    playSound(Uri.fromFile(new File(cacheDir + "SliderDecrease.ogg")));
                }
                l = i;
                String str = SliderString(featurenum, i);

                interInt.OnWrite(i);
                TextView textView = textView2;

                if (str != null)
                    textView.setText(Html.fromHtml("<font face='roboto'>" + feature + " : " + str + "</font>"));
                else
                    textView.setText(Html.fromHtml("<font face='roboto'>" + feature + " : " + i + "</font>"));
            }
        });

        linearLayout.addView(textView);
        linearLayout.addView(seekBar);
        patches.addView(linearLayout);
    }

    boolean delayed;

    public void playSound(Uri uri) {
        if (EnableSounds()) {
            if (!delayed) {
                delayed = true;
                if (FXPlayer != null) {
                    FXPlayer.stop();
                    FXPlayer.release();
                }
                FXPlayer = MediaPlayer.create(this, uri);
                if (FXPlayer != null)
                    //Volume reduced so sounds are not too loud
                    FXPlayer.setVolume(0.4f, 0.4f);
                FXPlayer.start();

                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        delayed = false;
                    }
                }, 100);
            }
        }
    }

    public boolean isViewCollapsed() {
        return mFloatingView == null || mCollapsed.getVisibility() == View.VISIBLE;
    }

    //For our image a little converter
    private int convertDipToPixels(int i) {
        return (int) ((((float) i) * getResources().getDisplayMetrics().density) + 0.5f);
    }

    private int dpi() {
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        return (int) (metrics.density * 160f);
    }

    private int dp(int i) {
        if (dpi() > 400)
            return (int) TypedValue.applyDimension(1, (float) i, getResources().getDisplayMetrics()) - 20;
        return (int) TypedValue.applyDimension(1, (float) i, getResources().getDisplayMetrics());
    }

    //Destroy our View
    public void onDestroy() {
        super.onDestroy();
        View view = mFloatingView;
        if (view != null) {
            mWindowManager.removeView(view);
        }
    }

    //Check if we are still in the game. If now our Menu and Menu button will dissapear
    private boolean isNotInGame() {
        RunningAppProcessInfo runningAppProcessInfo = new RunningAppProcessInfo();
        ActivityManager.getMyMemoryState(runningAppProcessInfo);
        return runningAppProcessInfo.importance != 100;
    }

    //Same as above so it wont crash in the background and therefore use alot of Battery life
    public void onTaskRemoved(Intent intent) {
        stopSelf();
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        super.onTaskRemoved(intent);
    }

    /* access modifiers changed from: private */
    public void Thread() {
        if (mFloatingView == null) {
            return;
        }
        if (isNotInGame()) {
            mFloatingView.setVisibility(View.INVISIBLE);
        } else {
            mFloatingView.setVisibility(View.VISIBLE);
        }
    }

    static void setCornerRadius(GradientDrawable gradientDrawable, float f) {
        gradientDrawable.setCornerRadius(f);
    }

    static void setCornerRadius(GradientDrawable gradientDrawable, float f, float f2, float f3, float f4) {
        gradientDrawable.setCornerRadii(new float[]{f, f, f2, f2, f3, f3, f4, f4});
    }

    private interface InterfaceBtn {
        void OnWrite();
    }

    private interface InterfaceInt {
        void OnWrite(int i);
    }

    private interface InterfaceBool {
        void OnWrite(boolean z);
    }

    private interface InterfaceStr {
        void OnWrite(String s);
    }
}
