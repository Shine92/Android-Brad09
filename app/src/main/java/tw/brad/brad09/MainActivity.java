package tw.brad.brad09;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class MainActivity extends AppCompatActivity {
    private MyPainter painter;
    private int[] bgs = {R.drawable.bg, R.drawable.bg1,
                R.drawable.bg2, R.drawable.bg3};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        painter = (MyPainter)findViewById(R.id.painter);
    }

    @Override
    public void finish() {
        painter.gameover();
        super.finish();
    }

    public void clear(View v){
        painter.clear();
    }
    public void undo(View v){
        painter.undo();
    }
    public void redo(View v){
        painter.redo();
    }
    public void changeBG(View v){
        painter.setBackgroundResource(
                bgs[(int)(Math.random()*4)]);
    }
}
