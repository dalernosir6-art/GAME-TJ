package tj.gametj.app;

import android.app.Activity;
import android.os.Bundle;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setGravity(Gravity.CENTER);
        root.setPadding(30, 30, 30, 30);
        root.setBackgroundColor(Color.rgb(0, 130, 90));

        TextView title = new TextView(this);
        title.setText("🎮 GAME TJ");
        title.setTextSize(32);
        title.setTextColor(Color.WHITE);
        title.setGravity(Gravity.CENTER);

        TextView subtitle = new TextView(this);
        subtitle.setText("Бозори масолеҳи сохтмонӣ");
        subtitle.setTextSize(20);
        subtitle.setTextColor(Color.WHITE);
        subtitle.setGravity(Gravity.CENTER);

        Button button = new Button(this);
        button.setText("Масолеҳи сохтмонӣ");

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(
                    MainActivity.this,
                    "Хуш омадед ба GAME TJ!",
                    Toast.LENGTH_SHORT
                ).show();
            }
        });

        root.addView(title);
        root.addView(subtitle);
        root.addView(button);

        setContentView(root);
    }
}
