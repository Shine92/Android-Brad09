package tw.brad.brad09;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by brad on 16/5/27.
 */
public class MyPainter extends View {
    private LinkedList<LinkedList<HashMap<String,Float>>> lines, recycle;
    private Paint paint, paintBall;
    private Bitmap bmpBg, bmpBall;
    private Resources res;
    private boolean isInit;
    private float viewW, viewH, ballW, ballH;
    private Matrix matrix;
    private GestureDetector gd;
    private float ballX, ballY, dx, dy;
    private Timer timer;
    private Dir ballDir;

    // 列舉
    private enum Dir {
        STOP, UP, DOWN, LEFT, RIGHT
    }

    public MyPainter(Context context, AttributeSet attrs){
        super(context, attrs);

        timer = new Timer();

        paint = new Paint();
        paint.setColor(Color.YELLOW);
        paint.setStrokeWidth(4);

        paintBall = new Paint();
        paintBall.setAlpha(128);

        lines =  new LinkedList<>();
        recycle =  new LinkedList<>();
        matrix = new Matrix();
        setBackgroundColor(Color.BLACK);

        //setBackgroundResource(R.drawable.bg);
        res = context.getResources();

        gd = new GestureDetector(context, new MyGDListener());


    }

    private void init(){
        viewW = getWidth();
        viewH = getHeight();
        bmpBg = BitmapFactory.decodeResource(res, R.drawable.bg9);
        bmpBg = toNewBmp(bmpBg, viewW*10, viewH);

        bmpBall = BitmapFactory.decodeResource(res, R.drawable.ball);
        bmpBall = toNewBmp(bmpBall, ballW = viewW/12, ballH = viewW/12);

        ballX = ballY = 100;
        dx = dy = 4;

        timer.schedule(new UREyeTask(),0,33);
        timer.schedule(new BallTask(), 3000,33);

        ballDir = Dir.STOP;

        isInit = true;
    }

    private class MyGDListener
            extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onDown(MotionEvent e) {
            Log.i("brad", "onDown");
            return true; //super.onDown(e);
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2,
                               float vX, float vY) {
            if (Math.abs(vY) > Math.abs(vX)){
                if (vY <0 ){
                    ballDir = Dir.UP;
                }else{
                    ballDir = Dir.DOWN;
                }
            }else {
                if (vX<0){
                    ballDir = Dir.LEFT;
                }else{
                    ballDir = Dir.RIGHT;
                }
            }
            Log.i("brad", "onFling:" + vX + " x " + vY);
            return super.onFling(e1, e2, vX, vY);
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            Log.i("brad", "onScroll");
            return super.onScroll(e1, e2, distanceX, distanceY);
        }
    }

    private class UREyeTask extends TimerTask {
        @Override
        public void run() {
            postInvalidate();
        }
    }

    private class BallTask extends TimerTask {
        @Override
        public void run() {
//            switch (ballDir){
//                case UP: ballY += -10; break;
//                case DOWN: ballY += 10; break;
//                case LEFT: ballX += -10; break;
//                case RIGHT: ballX += 10; break;
//            }
            if (ballX<0 || ballX + ballW > viewW){
                dx *= -1;
            }else if (ballY<0 || ballY + ballH > viewH){
                dy *= -1;
            }
            ballX += dx;
            ballY += dy;
        }
    }

    private Bitmap toNewBmp(Bitmap source, float w, float h){
        matrix.reset();
        matrix.postScale(w/source.getWidth(), h/source.getHeight());
        Bitmap temp = Bitmap.createBitmap(source,
                0,0,source.getWidth(), source.getHeight(), matrix, false);
        return temp;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (!isInit) init();

        canvas.drawBitmap(bmpBg, 0, 0, null);
        canvas.drawBitmap(bmpBall, ballX, ballY, null);

        for (LinkedList<HashMap<String,Float>> line: lines) {
            for (int i = 1; i < line.size(); i++) {
                HashMap<String, Float> p0 = line.get(i - 1);
                HashMap<String, Float> p1 = line.get(i);
                canvas.drawLine(p0.get("x"), p0.get("y"),
                        p1.get("x"), p1.get("y"),
                        paint);
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
//        float ex = event.getX(), ey = event.getY();
//        if (event.getAction()==MotionEvent.ACTION_DOWN) {
//            doDown(ex, ey);
//        }else if (event.getAction() == MotionEvent.ACTION_MOVE){
//            doMove(ex, ey);
//        }
        //return true; // super.onTouchEvent(event);
        Log.i("brad", "onTouch");
        return gd.onTouchEvent(event);
    }

    private void doDown(float x, float y){
        recycle.clear();
        LinkedList<HashMap<String,Float>> line = new LinkedList<>();
        HashMap<String, Float> point = new HashMap<>();
        point.put("x", x); point.put("y", y);
        line.add(point);
        lines.add(line);
    }
    private void doMove(float x, float y){
        HashMap<String, Float> point = new HashMap<>();
        point.put("x", x); point.put("y", y);
        lines.getLast().add(point);
        invalidate();
    }

    public void clear(){
        lines.clear();
        invalidate();
    }
    public void undo(){
        if (lines.size()>0) {
            recycle.add(lines.removeLast());
            invalidate();
        }
    }
    public void redo(){
        if (recycle.size()>0) {
            lines.add(recycle.removeLast());
            invalidate();
        }
    }

    public void gameover(){
        timer.cancel();
        timer.purge();
        timer = null;
    }
}
