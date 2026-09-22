package tj.gametj.app;

import android.app.Activity;
import android.os.Bundle;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.widget.*;

public class MainActivity extends Activity {

    LinearLayout root;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        showHome();
    }

    void showHome() {
        root = new LinearLayout(this);
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
        welcome.setText("Хуш омадед ба бозори акаунтҳо");
        welcome.setTextSize(25);
        welcome.setTextColor(Color.WHITE);
        welcome.setGravity(Gravity.CENTER);
        welcome.setPadding(0, 20, 0, 30);

        Button accounts = new Button(this);
        accounts.setText("АКАУНТҲО");
        accounts.setTextSize(20);

        accounts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAccounts();
            }
        });

        root.addView(title);
        root.addView(welcome);
        root.addView(accounts);

        setContentView(root);
    }

    void showAccounts() {

        ScrollView scroll = new ScrollView(this);

        LinearLayout list = new LinearLayout(this);
        list.setOrientation(LinearLayout.VERTICAL);
        list.setPadding(20, 30, 20, 30);
        list.setBackgroundColor(Color.rgb(245, 245, 245));

        TextView title = new TextView(this);
        title.setText("🎮 АКАУНТҲО");
        title.setTextSize(30);
        title.setTextColor(Color.rgb(0, 128, 96));
        title.setGravity(Gravity.CENTER);
        title.setPadding(0, 10, 0, 30);

        list.addView(title);

        addAccount(list, "🔥 Free Fire — Level 70",
                "Ҳисоб бо либосҳои зиёд", "250 сомонӣ");

        addAccount(list, "💎 Free Fire — Level 65",
                "Diamond ва коллексияи хуб", "180 сомонӣ");

        addAccount(list, "⚡ PUBG Mobile",
                "Level баланд, ашёҳои зиёд", "300 сомонӣ");

        Button back = new Button(this);
        back.setText("← БАРГАШТАН");
        back.setTextSize(18);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showHome();
            }
        });

        list.addView(back);

        scroll.addView(list);
        setContentView(scroll);
    }

    void addAccount(LinearLayout list, String name,
                    String description, String price) {

        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setPadding(25, 25, 25, 25);
        card.setBackgroundColor(Color.WHITE);

        TextView nameText = new TextView(this);
        nameText.setText(name);
        nameText.setTextSize(22);
        nameText.setTextColor(Color.BLACK);

        TextView descText = new TextView(this);
        descText.setText(description);
        descText.setTextSize(17);
        descText.setTextColor(Color.DKGRAY);
        descText.setPadding(0, 10, 0, 10);

        TextView priceText = new TextView(this);
        priceText.setText("💰 " + price);
        priceText.setTextSize(20);
        priceText.setTextColor(Color.rgb(0, 128, 96));

        Button viewButton = new Button(this);
        viewButton.setText("ДИДАНИ АКАУНТ");

        card.addView(nameText);
        card.addView(descText);
        card.addView(priceText);
        card.addView(viewButton);

        list.addView(card);

        Space space = new Space(this);
        list.addView(space,
                new LinearLayout.LayoutParams(1, 20));
    }
}
