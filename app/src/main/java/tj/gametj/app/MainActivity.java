package tj.gametj.app;

import android.app.Activity;
import android.os.Bundle;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
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
        welcome.setTextSize(23);
        welcome.setTextColor(Color.WHITE);
        welcome.setGravity(Gravity.CENTER);
        welcome.setPadding(0, 20, 0, 30);

        Button accounts = new Button(this);
        accounts.setText("🎮 АКАУНТҲО");
        accounts.setTextSize(20);

        accounts.setOnClickListener(v -> showAccounts());

        Button admin = new Button(this);
        admin.setText("🔐 АДМИН");

        admin.setOnClickListener(v -> showAdmin());

        root.addView(title);
        root.addView(welcome);
        root.addView(accounts);
        root.addView(admin);

        setContentView(root);
    }

    void showAdmin() {

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(30, 50, 30, 30);

        TextView title = new TextView(this);
        title.setText("🔐 ПАНЕЛИ АДМИН");
        title.setTextSize(28);
        title.setGravity(Gravity.CENTER);

        EditText pin = new EditText(this);
        pin.setHint("PIN-код");
        pin.setInputType(2);

        Button login = new Button(this);
        login.setText("ВОЙТИ");

        Button back = new Button(this);
        back.setText("← БАРГАШТАН");

        login.setOnClickListener(v -> {

            if (pin.getText().toString().equals("1234")) {
                showAdminPanel();
            } else {
                Toast.makeText(this,
                        "PIN нодуруст аст",
                        Toast.LENGTH_SHORT).show();
            }
        });

        back.setOnClickListener(v -> showHome());

        layout.addView(title);
        layout.addView(pin);
        layout.addView(login);
        layout.addView(back);

        setContentView(layout);
    }

    void showAdminPanel() {

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(25, 40, 25, 25);

        TextView title = new TextView(this);
        title.setText("⚙️ ПАНЕЛИ АДМИН");
        title.setTextSize(28);
        title.setGravity(Gravity.CENTER);

        Button add = new Button(this);
        add.setText("➕ ИЛОВА КАРДАНИ АКАУНТ");

        Button back = new Button(this);
        back.setText("← БАРГАШТАН");

        add.setOnClickListener(v -> showAddAccount());

        back.setOnClickListener(v -> showHome());

        layout.addView(title);
        layout.addView(add);
        layout.addView(back);

        setContentView(layout);
    }

    void showAddAccount() {

        ScrollView scroll = new ScrollView(this);

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(25, 30, 25, 30);

        TextView title = new TextView(this);
        title.setText("➕ ИЛОВА КАРДАНИ АКАУНТ");
        title.setTextSize(26);
        title.setGravity(Gravity.CENTER);

        EditText name = field("Номи аккаунт");
        EditText game = field("Бозӣ");
        EditText level = field("Level");
        EditText price = field("Нарх бо сомонӣ");
        EditText phone = field("Телефон / WhatsApp");
        EditText description = field("Маълумоти аккаунт");

        Button publish = new Button(this);
        publish.setText("✅ НАШР КАРДАН");

        Button back = new Button(this);
        back.setText("← БАРГАШТАН");

        publish.setOnClickListener(v -> {

            if (name.getText().toString().trim().isEmpty()) {
                Toast.makeText(this,
                        "Номи аккаунтро навис",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            if (game.getText().toString().trim().isEmpty()) {
                Toast.makeText(this,
                        "Номи бозиро навис",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            if (price.getText().toString().trim().isEmpty()) {
                Toast.makeText(this,
                        "Нархро навис",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            getPreferences(MODE_PRIVATE)
                    .edit()
                    .putString("name", name.getText().toString())
                    .putString("game", game.getText().toString())
                    .putString("level", level.getText().toString())
                    .putString("price", price.getText().toString())
                    .putString("phone", phone.getText().toString())
                    .putString("description", description.getText().toString())
                    .apply();

            Toast.makeText(this,
                    "✅ Аккаунт нашр шуд",
                    Toast.LENGTH_SHORT).show();

            showAccounts();
        });

        back.setOnClickListener(v -> showAdminPanel());

        layout.addView(title);
        layout.addView(name);
        layout.addView(game);
        layout.addView(level);
        layout.addView(price);
        layout.addView(phone);
        layout.addView(description);
        layout.addView(publish);
        layout.addView(back);

        scroll.addView(layout);
        setContentView(scroll);
    }

    EditText field(String text) {

        EditText e = new EditText(this);
        e.setHint(text);
        e.setTextSize(17);
        e.setPadding(15, 15, 15, 15);

        return e;
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

        String name = getPreferences(MODE_PRIVATE)
                .getString("name", "");

        if (name.isEmpty()) {

            TextView empty = new TextView(this);
            empty.setText("Ҳоло аккаунт нест.");
            empty.setTextSize(20);
            empty.setGravity(Gravity.CENTER);

            list.addView(empty);

        } else {

            String game = getPreferences(MODE_PRIVATE)
                    .getString("game", "");

            String level = getPreferences(MODE_PRIVATE)
                    .getString("level", "");

            String price = getPreferences(MODE_PRIVATE)
                    .getString("price", "");

            String phone = getPreferences(MODE_PRIVATE)
                    .getString("phone", "");

            String description = getPreferences(MODE_PRIVATE)
                    .getString("description", "");

            addAccount(
                    list,
                    name,
                    game,
                    level,
                    price,
                    phone,
                    description
            );
        }

        Button back = new Button(this);
        back.setText("← БАРГАШТАН");

        back.setOnClickListener(v -> showHome());

        list.addView(back);

        scroll.addView(list);
        setContentView(scroll);
    }

    void addAccount(
            LinearLayout list,
            String name,
            String game,
            String level,
            String price,
            String phone,
            String description) {

        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setPadding(25, 25, 25, 25);
        card.setBackgroundColor(Color.WHITE);

        TextView nameText = new TextView(this);
        nameText.setText("🎮 " + name);
        nameText.setTextSize(25);
        nameText.setTextColor(Color.BLACK);

        TextView gameText = new TextView(this);
        gameText.setText("🕹 Бозӣ: " + game);
        gameText.setTextSize(18);

        TextView levelText = new TextView(this);
        levelText.setText("📊 Level: " + level);
        levelText.setTextSize(18);

        TextView descText = new TextView(this);
        descText.setText("📝 " + description);
        descText.setTextSize(17);

        TextView priceText = new TextView(this);
        priceText.setText("💰 " + price + " сомонӣ");
        priceText.setTextSize(22);
        priceText.setTextColor(Color.rgb(0, 128, 96));

        TextView phoneText = new TextView(this);
        phoneText.setText("📱 " + phone);
        phoneText.setTextSize(18);

        Button viewButton = new Button(this);
        viewButton.setText("ДИДАНИ АКАУНТ");

        viewButton.setOnClickListener(v -> {

            showAccountDetails(
                    name,
                    game,
                    level,
                    price,
                    phone,
                    description
            );
        });

        card.addView(nameText);
        card.addView(gameText);
        card.addView(levelText);
        card.addView(descText);
        card.addView(priceText);
        card.addView(phoneText);
        card.addView(viewButton);

        list.addView(card);
    }

    void showAccountDetails(
            String name,
            String game,
            String level,
            String price,
            String phone,
            String description) {

        ScrollView scroll = new ScrollView(this);

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(25, 30, 25, 30);

        TextView title = new TextView(this);
        title.setText("🎮 " + name);
        title.setTextSize(30);
        title.setTextColor(Color.rgb(0, 128, 96));
        title.setGravity(Gravity.CENTER);

        TextView info = new TextView(this);

        info.setText(
                "🕹 Бозӣ: " + game +
                "\n\n📊 Level: " + level +
                "\n\n📝 " + description +
                "\n\n💰 " + price + " сомонӣ" +
                "\n\n📱 " + phone
        );

        info.setTextSize(19);
        info.setPadding(0, 30, 0, 30);

        Button contact = new Button(this);
        contact.setText("📞 ТАМОС БО ФУРӮШАНДА");

        contact.setOnClickListener(v -> {

            if (!phone.isEmpty()) {

                Intent intent = new Intent(
                        Intent.ACTION_DIAL,
                        Uri.parse("tel:" + phone)
                );

                startActivity(intent);

            } else {

                Toast.makeText(
                        this,
                        "Рақами телефон нест",
                        Toast.LENGTH_SHORT
                ).show();
            }
        });

        Button back = new Button(this);
        back.setText("← БАРГАШТАН");

        back.setOnClickListener(v -> showAccounts());

        layout.addView(title);
        layout.addView(info);
        layout.addView(contact);
        layout.addView(back);

        scroll.addView(layout);
        setContentView(scroll);
    }
}
