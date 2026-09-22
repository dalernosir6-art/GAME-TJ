package tj.gametj.app;

import android.app.Activity;
import android.os.Bundle;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.widget.*;

public class MainActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setGravity(Gravity.CENTER);
        root.setPadding(25, 40, 25, 40);
        root.setBackgroundColor(Color.rgb(0, 128, 96));

        TextView title = new TextView(this);
        title.setText("🎮 GAME TJ");
        title.setTextSize(32);
        title.setTextColor(Color.WHITE);
        title.setGravity(Gravity.CENTER);

        TextView welcome = new TextView(this);
        welcome.setText("Хуш омадед ба бозори аккаунтҳо");
        welcome.setTextSize(24);
        welcome.setTextColor(Color.WHITE);
        welcome.setGravity(Gravity.CENTER);
        welcome.setPadding(0, 20, 0, 30);

        Button accounts = new Button(this);
        accounts.setText("АККАУНТҲО");
        accounts.setTextSize(18);

        accounts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(
                    MainActivity.this,
                    "Рӯйхати аккаунтҳо кушода мешавад",
                    Toast.LENGTH_SHORT
                ).show();
            }
        });

        root.addView(title);
        root.addView(welcome);
        root.addView(accounts);

        setContentView(root);
    }
}
