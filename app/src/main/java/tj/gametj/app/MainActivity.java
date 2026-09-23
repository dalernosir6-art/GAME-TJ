package tj.gametj.app;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.*;

import java.util.ArrayList;

public class MainActivity extends Activity {

    LinearLayout root;
    SharedPreferences prefs;

    ArrayList<String> names = new ArrayList<>();
    ArrayList<String> games = new ArrayList<>();
    ArrayList<String> levels = new ArrayList<>();
    ArrayList<String> prices = new ArrayList<>();
    ArrayList<String> phones = new ArrayList<>();
    ArrayList<String> descriptions = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Ранги Status Bar
        getWindow().setStatusBarColor(Color.BLACK);
        getWindow().setNavigationBarColor(Color.BLACK);

        prefs = getSharedPreferences("GAME_TJ_DATA", MODE_PRIVATE);

        loadAccounts();

        // Аввал Loading Screen
        showLoading();
    }

    // =========================================================
    // LOADING SCREEN
    // =========================================================

    void showLoading() {

        LinearLayout loadingLayout = new LinearLayout(this);
        loadingLayout.setOrientation(LinearLayout.VERTICAL);
        loadingLayout.setGravity(Gravity.CENTER);
        loadingLayout.setBackgroundColor(Color.BLACK);

        // Логотип
        ImageView logo = new ImageView(this);

        try {
            logo.setImageResource(R.mipmap.ic_launcher);
        } catch (Exception e) {
            logo.setImageResource(android.R.drawable.ic_menu_gallery);
        }

        int logoSize = dp(180);

        LinearLayout.LayoutParams logoParams =
                new LinearLayout.LayoutParams(logoSize, logoSize);

        logoParams.gravity = Gravity.CENTER;
        logo.setLayoutParams(logoParams);
        logo.setScaleType(ImageView.ScaleType.CENTER_INSIDE);

        // =====================================================
        // АНИМАТСИЯИ ЛОГО
        // Равшан мешавад -> хомӯштар -> боз равшан
        // Ҳамзамон каме калон/хурд мешавад
        // =====================================================

        AlphaAnimation fade =
                new AlphaAnimation(0.45f, 1.0f);

        fade.setDuration(850);
        fade.setRepeatMode(Animation.REVERSE);
        fade.setRepeatCount(Animation.INFINITE);

        ScaleAnimation scale =
                new ScaleAnimation(
                        0.92f,
                        1.05f,
                        0.92f,
                        1.05f,
                        Animation.RELATIVE_TO_SELF,
                        0.5f,
                        Animation.RELATIVE_TO_SELF,
                        0.5f
                );

        scale.setDuration(850);
        scale.setRepeatMode(Animation.REVERSE);
        scale.setRepeatCount(Animation.INFINITE);

        AnimationSet animationSet = new AnimationSet(true);
        animationSet.addAnimation(fade);
        animationSet.addAnimation(scale);

        logo.startAnimation(animationSet);

        // Номи барнома
        TextView title = new TextView(this);
        title.setText("GAME TJ");
        title.setTextSize(30);
        title.setTextColor(Color.WHITE);
        title.setGravity(Gravity.CENTER);
        title.setTypeface(null, android.graphics.Typeface.BOLD);

        LinearLayout.LayoutParams titleParams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );

        titleParams.topMargin = dp(18);
        title.setLayoutParams(titleParams);

        // Матни Loading
        TextView loadingText = new TextView(this);
        loadingText.setText("Бор карда мешавад...");
        loadingText.setTextSize(16);
        loadingText.setTextColor(Color.LTGRAY);
        loadingText.setGravity(Gravity.CENTER);

        LinearLayout.LayoutParams textParams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );

        textParams.topMargin = dp(8);
        loadingText.setLayoutParams(textParams);

        // Нуқтаҳои loading
        TextView dots = new TextView(this);
        dots.setText("●  ●  ●");
        dots.setTextSize(14);
        dots.setTextColor(Color.WHITE);
        dots.setGravity(Gravity.CENTER);

        LinearLayout.LayoutParams dotsParams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );

        dotsParams.topMargin = dp(15);
        dots.setLayoutParams(dotsParams);

        AlphaAnimation dotsAnimation =
                new AlphaAnimation(0.25f, 1.0f);

        dotsAnimation.setDuration(700);
        dotsAnimation.setRepeatMode(Animation.REVERSE);
        dotsAnimation.setRepeatCount(Animation.INFINITE);

        dots.startAnimation(dotsAnimation);

        loadingLayout.addView(logo);
        loadingLayout.addView(title);
        loadingLayout.addView(loadingText);
        loadingLayout.addView(dots);

        setContentView(loadingLayout);

        // Баъд аз Loading ба Home мегузарад
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                logo.clearAnimation();
                dots.clearAnimation();

                showHome();
            }
        }, 3000);
    }

    // =========================================================
    // HOME
    // =========================================================

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
        welcome.setText("Хуш омадед ба бозори аккаунтҳо");
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
        admin.setTextSize(18);
        admin.setOnClickListener(v -> showAdminLogin());

        root.addView(title);
        root.addView(welcome);
        root.addView(accounts);
        root.addView(admin);

        setContentView(root);
    }

    // =========================================================
    // ADMIN LOGIN
    // =========================================================

    void showAdminLogin() {

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(30, 50, 30, 30);
        layout.setGravity(Gravity.CENTER);

        TextView title = new TextView(this);
        title.setText("🔐 Панели админ");
        title.setTextSize(28);
        title.setGravity(Gravity.CENTER);

        EditText password = new EditText(this);
        password.setHint("PIN-код");
        password.setInputType(2);

        Button login = new Button(this);
        login.setText("ВОЙТИ");

        Button back = new Button(this);
        back.setText("← БАРГАШТАН");

        login.setOnClickListener(v -> {

            if (password.getText().toString().equals("1234")) {

                showAdminPanel();

            } else {

                Toast.makeText(
                        this,
                        "PIN-код нодуруст аст",
                        Toast.LENGTH_SHORT
                ).show();
            }
        });

        back.setOnClickListener(v -> showHome());

        layout.addView(title);
        layout.addView(password);
        layout.addView(login);
        layout.addView(back);

        setContentView(layout);
    }

    // =========================================================
    // ADMIN PANEL
    // =========================================================

    void showAdminPanel() {

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(25, 30, 25, 30);

        TextView title = new TextView(this);
        title.setText("⚙️ ПАНЕЛИ АДМИН");
        title.setTextSize(28);
        title.setTextColor(Color.rgb(0, 128, 96));
        title.setGravity(Gravity.CENTER);

        Button add = new Button(this);
        add.setText("➕ ИЛОВА КАРДАНИ АКАУНТ");

        Button accounts = new Button(this);
        accounts.setText("📋 РӮЙХАТИ АКАУНТҲО");

        Button back = new Button(this);
        back.setText("← БАРГАШТАН");

        add.setOnClickListener(v -> showAddAccount());

        accounts.setOnClickListener(v -> showAccounts());

        back.setOnClickListener(v -> showHome());

        layout.addView(title);
        layout.addView(add);
        layout.addView(accounts);
        layout.addView(back);

        setContentView(layout);
    }

    // =========================================================
    // ADD ACCOUNT
    // =========================================================

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
        EditText game = field("Бозӣ (Free Fire / PUBG)");
        EditText level = field("Level");
        EditText price = field("Нарх бо сомонӣ");
        EditText phone = field("Телефон / WhatsApp");

        EditText description = field("Маълумоти аккаунт");
        description.setMinLines(4);

        Button publish = new Button(this);
        publish.setText("✅ НАШР КАРДАН");

        Button back = new Button(this);
        back.setText("← БАРГАШТАН");

        publish.setOnClickListener(v -> {

            if (name.getText().toString().trim().isEmpty()
                    || game.getText().toString().trim().isEmpty()
                    || price.getText().toString().trim().isEmpty()) {

                Toast.makeText(
                        this,
                        "Ном, бозӣ ва нархро пур кунед",
                        Toast.LENGTH_SHORT
                ).show();

                return;
            }

            saveAccount(
                    name.getText().toString(),
                    game.getText().toString(),
                    level.getText().toString(),
                    price.getText().toString(),
                    phone.getText().toString(),
                    description.getText().toString()
            );

            Toast.makeText(
                    this,
                    "✅ Аккаунт нашр шуд",
                    Toast.LENGTH_SHORT
            ).show();

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

    // =========================================================
    // FIELD
    // =========================================================

    EditText field(String hint) {

        EditText edit = new EditText(this);

        edit.setHint(hint);
        edit.setTextSize(17);
        edit.setPadding(15, 15, 15, 15);

        return edit;
    }

    // =========================================================
    // SAVE ACCOUNT
    // =========================================================

    void saveAccount(
            String name,
            String game,
            String level,
            String price,
            String phone,
            String description) {

        int count = prefs.getInt("count", 0);

        prefs.edit()
                .putString("name_" + count, name)
                .putString("game_" + count, game)
                .putString("level_" + count, level)
                .putString("price_" + count, price)
                .putString("phone_" + count, phone)
                .putString("description_" + count, description)
                .putInt("count", count + 1)
                .apply();

        loadAccounts();
    }

    // =========================================================
    // LOAD ACCOUNTS
    // =========================================================

    void loadAccounts() {

        names.clear();
        games.clear();
        levels.clear();
        prices.clear();
        phones.clear();
        descriptions.clear();

        int count = prefs.getInt("count", 0);

        for (int i = 0; i < count; i++) {

            names.add(
                    prefs.getString("name_" + i, "")
            );

            games.add(
                    prefs.getString("game_" + i, "")
            );

            levels.add(
                    prefs.getString("level_" + i, "")
            );

            prices.add(
                    prefs.getString("price_" + i, "")
            );

            phones.add(
                    prefs.getString("phone_" + i, "")
            );

            descriptions.add(
                    prefs.getString("description_" + i, "")
            );
        }
    }

    // =========================================================
    // ACCOUNTS LIST
    // =========================================================

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

        list.addView(title);

        if (names.size() == 0) {

            TextView empty = new TextView(this);
            empty.setText(
                    "Ҳоло ягон аккаунт илова нашудааст."
            );

            empty.setTextSize(18);
            empty.setGravity(Gravity.CENTER);
            empty.setPadding(0, 30, 0, 30);

            list.addView(empty);

        } else {

            for (int i = 0; i < names.size(); i++) {

                addAccount(
                        list,
                        names.get(i),
                        games.get(i),
                        levels.get(i),
                        prices.get(i),
                        phones.get(i),
                        descriptions.get(i)
                );
            }
        }

        Button back = new Button(this);
        back.setText("← БАРГАШТАН");

        back.setOnClickListener(v -> showHome());

        list.addView(back);

        scroll.addView(list);

        setContentView(scroll);
    }

    // =========================================================
    // ACCOUNT CARD
    // =========================================================

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
        nameText.setTextSize(22);

        TextView gameText = new TextView(this);
        gameText.setText("🕹 Бозӣ: " + game);
        gameText.setTextSize(17);

        TextView levelText = new TextView(this);
        levelText.setText("📊 Level: " + level);
        levelText.setTextSize(17);

        TextView descText = new TextView(this);
        descText.setText("📝 " + description);
        descText.setTextSize(16);

        TextView priceText = new TextView(this);
        priceText.setText("💰 " + price + " сомонӣ");
        priceText.setTextSize(20);
        priceText.setTextColor(Color.rgb(0, 128, 96));

        TextView phoneText = new TextView(this);
        phoneText.setText("📱 " + phone);
        phoneText.setTextSize(17);

        card.addView(nameText);
        card.addView(gameText);
        card.addView(levelText);
        card.addView(descText);
        card.addView(priceText);
        card.addView(phoneText);

        list.addView(card);

        Space space = new Space(this);

        list.addView(
                space,
                new LinearLayout.LayoutParams(
                        1,
                        20
                )
        );
    }

    // =========================================================
    // DP
    // =========================================================

    int dp(int value) {

        float density =
                getResources()
                        .getDisplayMetrics()
                        .density;

        return (int) (value * density + 0.5f);
    }
}
