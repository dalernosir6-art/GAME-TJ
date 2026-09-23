package tj.gametj.app;

import android.app.Activity;
import android.os.Bundle;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class MainActivity extends Activity {

    LinearLayout main;
    LinearLayout content;
    EditText search;
    TextView pageTitle;

    ArrayList<String> names = new ArrayList<>();
    ArrayList<String> games = new ArrayList<>();
    ArrayList<String> levels = new ArrayList<>();
    ArrayList<String> prices = new ArrayList<>();
    ArrayList<String> phones = new ArrayList<>();
    ArrayList<String> descriptions = new ArrayList<>();

    Set<Integer> favorites = new HashSet<>();

    int BLUE = Color.rgb(7, 20, 48);
    int CARD = Color.rgb(13, 32, 68);
    int CARD2 = Color.rgb(18, 42, 82);
    int WHITE = Color.WHITE;
    int MUTED = Color.rgb(175, 190, 215);
    int ACCENT = Color.rgb(65, 150, 255);
    int GREEN = Color.rgb(55, 205, 125);

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setStatusBarColor(BLUE);
        getWindow().setNavigationBarColor(Color.BLACK);

        loadDemoAccounts();
        showHome();
    }

    // =========================================================
    // HOME
    // =========================================================

    void showHome() {

        createBase("GAME TJ");

        TextView welcome = text(
                "Хуш омадед ба бозори аккаунтҳо",
                24,
                WHITE,
                true
        );

        content.addView(welcome);

        search = new EditText(this);
        search.setHint("🔍  Ҷустуҷӯи аккаунт...");
        search.setHintTextColor(MUTED);
        search.setTextColor(WHITE);
        search.setTextSize(16);
        search.setSingleLine(true);
        search.setPadding(dp(18), 0, dp(18), 0);

        GradientDrawable searchBg = bg(CARD2, 18);
        search.setBackground(searchBg);

        LinearLayout.LayoutParams searchParams =
                new LinearLayout.LayoutParams(
                        -1,
                        dp(55)
                );

        searchParams.topMargin = dp(18);
        content.addView(search, searchParams);

        search.setOnEditorActionListener((v, actionId, event) -> {
            showSearch(search.getText().toString());
            return true;
        });

        TextView catTitle = text(
                "Категорияҳо",
                20,
                WHITE,
                true
        );

        LinearLayout.LayoutParams ct =
                new LinearLayout.LayoutParams(-1, -2);
        ct.topMargin = dp(24);
        content.addView(catTitle, ct);

        LinearLayout categories = new LinearLayout(this);
        categories.setOrientation(LinearLayout.HORIZONTAL);

        addCategory(categories, "🎮 Ҳама", "Ҳама");
        addCategory(categories, "🔥 Free Fire", "Free Fire");
        addCategory(categories, "🔫 PUBG", "PUBG");
        addCategory(categories, "⚔️ Дигар", "Дигар");

        content.addView(categories);

        TextView newTitle = text(
                "🔥 Эълонҳои нав",
                20,
                WHITE,
                true
        );

        LinearLayout.LayoutParams nt =
                new LinearLayout.LayoutParams(-1, -2);
        nt.topMargin = dp(25);

        content.addView(newTitle, nt);

        showCards(content, names, games, levels, prices, phones, descriptions);

        addBottomNavigation(0);
    }

    // =========================================================
    // BASE LAYOUT
    // =========================================================

    void createBase(String title) {

        main = new LinearLayout(this);
        main.setOrientation(LinearLayout.VERTICAL);
        main.setBackgroundColor(BLUE);

        ScrollView scroll = new ScrollView(this);
        scroll.setFillViewport(true);

        content = new LinearLayout(this);
        content.setOrientation(LinearLayout.VERTICAL);
        content.setPadding(
                dp(18),
                dp(20),
                dp(18),
                dp(100)
        );

        scroll.addView(content);

        main.addView(scroll,
                new LinearLayout.LayoutParams(
                        -1,
                        0,
                        1
                )
        );

        setContentView(main);
    }

    // =========================================================
    // CATEGORIES
    // =========================================================

    void addCategory(
            LinearLayout row,
            String title,
            String filter
    ) {

        Button b = new Button(this);
        b.setText(title);
        b.setTextColor(WHITE);
        b.setTextSize(13);
        b.setAllCaps(false);
        b.setBackground(bg(CARD2, 16));

        LinearLayout.LayoutParams p =
                new LinearLayout.LayoutParams(
                        dp(120),
                        dp(48)
                );

        p.rightMargin = dp(8);

        row.addView(b, p);

        b.setOnClickListener(v -> {

            if (filter.equals("Ҳама")) {
                showHome();
            } else {
                showCategory(filter);
            }
        });
    }

    // =========================================================
    // SEARCH
    // =========================================================

    void showSearch(String query) {

        createBase("Ҷустуҷӯ");

        TextView title = text(
                "🔍 Натиҷаи ҷустуҷӯ",
                23,
                WHITE,
                true
        );

        content.addView(title);

        boolean found = false;

        for (int i = 0; i < names.size(); i++) {

            String all =
                    (names.get(i) + " " +
                    games.get(i) + " " +
                    descriptions.get(i))
                    .toLowerCase();

            if (all.contains(query.toLowerCase())) {

                addCard(
                        content,
                        i,
                        names.get(i),
                        games.get(i),
                        levels.get(i),
                        prices.get(i),
                        phones.get(i),
                        descriptions.get(i)
                );

                found = true;
            }
        }

        if (!found) {

            TextView empty = text(
                    "Ягон аккаунт ёфт нашуд.",
                    17,
                    MUTED,
                    false
            );

            empty.setGravity(Gravity.CENTER);
            content.addView(empty);
        }

        addBottomNavigation(1);
    }

    // =========================================================
    // CATEGORY
    // =========================================================

    void showCategory(String category) {

        createBase(category);

        TextView title = text(
                "🎮 " + category,
                24,
                WHITE,
                true
        );

        content.addView(title);

        boolean found = false;

        for (int i = 0; i < games.size(); i++) {

            if (games.get(i)
                    .toLowerCase()
                    .contains(category.toLowerCase())) {

                addCard(
                        content,
                        i,
                        names.get(i),
                        games.get(i),
                        levels.get(i),
                        prices.get(i),
                        phones.get(i),
                        descriptions.get(i)
                );

                found = true;
            }
        }

        if (!found) {

            TextView empty = text(
                    "Дар ин категория ҳоло эълон нест.",
                    17,
                    MUTED,
                    false
            );

            empty.setGravity(Gravity.CENTER);
            content.addView(empty);
        }

        addBottomNavigation(0);
    }

    // =========================================================
    // ACCOUNT CARDS
    // =========================================================

    void showCards(
            LinearLayout list,
            ArrayList<String> n,
            ArrayList<String> g,
            ArrayList<String> l,
            ArrayList<String> p,
            ArrayList<String> ph,
            ArrayList<String> d
    ) {

        for (int i = 0; i < n.size(); i++) {

            addCard(
                    list,
                    i,
                    n.get(i),
                    g.get(i),
                    l.get(i),
                    p.get(i),
                    ph.get(i),
                    d.get(i)
            );
        }
    }

    void addCard(
            LinearLayout list,
            int index,
            String name,
            String game,
            String level,
            String price,
            String phone,
            String description
    ) {

        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setPadding(
                dp(16),
                dp(16),
                dp(16),
                dp(16)
        );

        card.setBackground(bg(CARD, 18));

        LinearLayout.LayoutParams cp =
                new LinearLayout.LayoutParams(
                        -1,
                        -2
                );

        cp.topMargin = dp(12);

        list.addView(card, cp);

        LinearLayout top = new LinearLayout(this);
        top.setOrientation(LinearLayout.HORIZONTAL);
        top.setGravity(Gravity.CENTER_VERTICAL);

        TextView nameText = text(
                "🎮 " + name,
                19,
                WHITE,
                true
        );

        top.addView(
                nameText,
                new LinearLayout.LayoutParams(
                        0,
                        -2,
                        1
                )
        );

        Button fav = new Button(this);
        fav.setText(
                favorites.contains(index)
                        ? "♥"
                        : "♡"
        );

        fav.setTextSize(24);
        fav.setTextColor(WHITE);
        fav.setBackgroundColor(Color.TRANSPARENT);

        top.addView(
                fav,
                new LinearLayout.LayoutParams(
                        dp(55),
                        dp(55)
                )
        );

        fav.setOnClickListener(v -> {

            if (favorites.contains(index)) {
                favorites.remove(index);
                fav.setText("♡");
            } else {
                favorites.add(index);
                fav.setText("♥");
            }
        });

        card.addView(top);

        TextView gameText = text(
                "🕹 " + game + "     📊 Level " + level,
                15,
                MUTED,
                false
        );

        card.addView(gameText);

        TextView desc = text(
                description,
                15,
                MUTED,
                false
        );

        LinearLayout.LayoutParams dp1 =
                new LinearLayout.LayoutParams(-1, -2);

        dp1.topMargin = dp(8);

        card.addView(desc, dp1);

        TextView priceText = text(
                "💰 " + price + " сомонӣ",
                19,
                GREEN,
                true
        );

        LinearLayout.LayoutParams pp =
                new LinearLayout.LayoutParams(-1, -2);

        pp.topMargin = dp(10);

        card.addView(priceText, pp);

        Button detail = new Button(this);
        detail.setText("Дидани аккаунт →");
        detail.setTextColor(WHITE);
        detail.setAllCaps(false);
        detail.setBackground(bg(ACCENT, 14));

        LinearLayout.LayoutParams bp =
                new LinearLayout.LayoutParams(-1, dp(48));

        bp.topMargin = dp(12);

        card.addView(detail, bp);

        detail.setOnClickListener(
                v -> showDetails(
                        index,
                        name,
                        game,
                        level,
                        price,
                        phone,
                        description
                )
        );
    }

    // =========================================================
    // DETAILS
    // =========================================================

    void showDetails(
            int index,
            String name,
            String game,
            String level,
            String price,
            String phone,
            String description
    ) {

        createBase("Аккаунт");

        TextView title = text(
                "🎮 " + name,
                26,
                WHITE,
                true
        );

        content.addView(title);

        TextView info = text(
                "\n🕹 Бозӣ: " + game +
                "\n\n📊 Level: " + level +
                "\n\n💰 Нарх: " + price + " сомонӣ" +
                "\n\n📝 " + description +
                "\n\n📱 Телефон: " + phone,
                17,
                MUTED,
                false
        );

        LinearLayout.LayoutParams ip =
                new LinearLayout.LayoutParams(-1, -2);

        ip.topMargin = dp(20);

        content.addView(info, ip);

        Button favorite = new Button(this);

        favorite.setText(
                favorites.contains(index)
                        ? "♥ Аз дӯстдоштаҳо хориҷ кардан"
                        : "♡ Ба дӯстдоштаҳо"
        );

        favorite.setAllCaps(false);
        favorite.setTextColor(WHITE);
        favorite.setBackground(bg(CARD2, 14));

        content.addView(favorite);

        favorite.setOnClickListener(v -> {

            if (favorites.contains(index)) {

                favorites.remove(index);

                favorite.setText(
                        "♡ Ба дӯстдоштаҳо"
                );

            } else {

                favorites.add(index);

                favorite.setText(
                        "♥ Аз дӯстдоштаҳо хориҷ кардан"
                );
            }
        });

        Button contact = new Button(this);
        contact.setText("📱 Тамос гирифтан");
        contact.setAllCaps(false);
        contact.setTextColor(WHITE);
        contact.setBackground(bg(GREEN, 14));

        LinearLayout.LayoutParams contactP =
                new LinearLayout.LayoutParams(-1, dp(52));

        contactP.topMargin = dp(12);

        content.addView(contact, contactP);

        contact.setOnClickListener(v -> {

            Toast.makeText(
                    this,
                    "Рақами тамос: " + phone,
                    Toast.LENGTH_LONG
            ).show();
        });

        addBottomNavigation(0);
    }

    // =========================================================
    // FAVORITES
    // =========================================================

    void showFavorites() {

        createBase("Избранное");

        TextView title = text(
                "♥ Дӯстдоштаҳо",
                25,
                WHITE,
                true
        );

        content.addView(title);

        if (favorites.isEmpty()) {

            TextView empty = text(
                    "\nҲоло аккаунте ба дӯстдоштаҳо илова нашудааст.",
                    17,
                    MUTED,
                    false
            );

            content.addView(empty);

        } else {

            for (Integer i : favorites) {

                if (i < names.size()) {

                    addCard(
                            content,
                            i,
                            names.get(i),
                            games.get(i),
                            levels.get(i),
                            prices.get(i),
                            phones.get(i),
                            descriptions.get(i)
                    );
                }
            }
        }

        addBottomNavigation(2);
    }

    // =========================================================
    // ADD LISTING
    // =========================================================

    void showAddListing() {

        createBase("Илова кардани эълон");

        TextView title = text(
                "➕ Илова кардани аккаунт",
                24,
                WHITE,
                true
        );

        content.addView(title);

        EditText name = input("Номи аккаунт");
        EditText game = input("Бозӣ");
        EditText level = input("Level");
        EditText price = input("Нарх");
        EditText phone = input("Телефон");
        EditText desc = input("Тавсифи аккаунт");

        content.addView(name);
        content.addView(game);
        content.addView(level);
        content.addView(price);
        content.addView(phone);
        content.addView(desc);

        Button publish = new Button(this);

        publish.setText("✅ Нашр кардани эълон");
        publish.setTextColor(WHITE);
        publish.setAllCaps(false);
        publish.setBackground(bg(GREEN, 15));

        content.addView(publish);

        publish.setOnClickListener(v -> {

            if (name.getText().toString().trim().isEmpty()
                    || game.getText().toString().trim().isEmpty()
                    || price.getText().toString().trim().isEmpty()) {

                Toast.makeText(
                        this,
                        "Ном, бозӣ ва нархро пур кунед.",
                        Toast.LENGTH_SHORT
                ).show();

                return;
            }

            names.add(name.getText().toString());
            games.add(game.getText().toString());
            levels.add(level.getText().toString());
            prices.add(price.getText().toString());
            phones.add(phone.getText().toString());
            descriptions.add(desc.getText().toString());

            Toast.makeText(
                    this,
                    "Эълон илова шуд ✅",
                    Toast.LENGTH_SHORT
            ).show();

            showHome();
        });

        addBottomNavigation(3);
    }

    // =========================================================
    // PROFILE
    // =========================================================

    void showProfile() {

        createBase("Профиль");

        TextView avatar = text(
                "👤",
                55,
                WHITE,
                false
        );

        avatar.setGravity(Gravity.CENTER);

        content.addView(avatar);

        TextView title = text(
                "Профили ман",
                25,
                WHITE,
                true
        );

        title.setGravity(Gravity.CENTER);

        content.addView(title);

        Button myListings = new Button(this);
        myListings.setText("📦 Эълонҳои ман");
        myListings.setAllCaps(false);
        myListings.setTextColor(WHITE);
        myListings.setBackground(bg(CARD2, 15));

        content.addView(myListings);

        myListings.setOnClickListener(
                v -> showHome()
        );

        Button settings = new Button(this);
        settings.setText("⚙️ Танзимот");
        settings.setAllCaps(false);
        settings.setTextColor(WHITE);
        settings.setBackground(bg(CARD2, 15));

        content.addView(settings);

        settings.setOnClickListener(v ->
                Toast.makeText(
                        this,
                        "Танзимот баъдтар илова мешавад.",
                        Toast.LENGTH_SHORT
                ).show()
        );

        addBottomNavigation(4);
    }

    // =========================================================
    // BOTTOM NAVIGATION
    // =========================================================

    void addBottomNavigation(int selected) {

        LinearLayout nav = new LinearLayout(this);
        nav.setOrientation(LinearLayout.HORIZONTAL);
        nav.setGravity(Gravity.CENTER);
        nav.setPadding(5, 5, 5, 5);
        nav.setBackground(bg(Color.rgb(5, 14, 34), 0));

        String[] labels = {
                "⌂\nАсосӣ",
                "⌕\nҶустуҷӯ",
                "♥\nИзбранное",
                "＋\nИлова",
                "●\nПрофиль"
        };

        for (int i = 0; i < labels.length; i++) {

            final int position = i;

            Button b = new Button(this);

            b.setText(labels[i]);
            b.setTextSize(12);
            b.setAllCaps(false);
            b.setTextColor(
                    position == selected
                            ? Color.rgb(80, 170, 255)
                            : MUTED
            );

            b.setBackgroundColor(Color.TRANSPARENT);

            nav.addView(
                    b,
                    new LinearLayout.LayoutParams(
                            0,
                            dp(65),
                            1
                    )
            );

            b.setOnClickListener(v -> {

                if (position == 0) {
                    showHome();

                } else if (position == 1) {

                    createSearchPage();

                } else if (position == 2) {

                    showFavorites();

                } else if (position == 3) {

                    showAddListing();

                } else {

                    showProfile();
                }
            });
        }

        main.addView(
                nav,
                new LinearLayout.LayoutParams(
                        -1,
                        dp(70)
                )
        );
    }

    // =========================================================
    // SEARCH PAGE
    // =========================================================

    void createSearchPage() {

        createBase("Ҷустуҷӯ");

        EditText input = input("🔍 Номи аккаунт ё бозӣ...");

        content.addView(input);

        Button button = new Button(this);

        button.setText("Ҷустуҷӯ");
        button.setAllCaps(false);
        button.setTextColor(WHITE);
        button.setBackground(bg(ACCENT, 14));

        content.addView(button);

        button.setOnClickListener(v ->
                showSearch(input.getText().toString())
        );

        addBottomNavigation(1);
    }

    // =========================================================
    // INPUT
    // =========================================================

    EditText input(String hint) {

        EditText e = new EditText(this);

        e.setHint(hint);
        e.setHintTextColor(MUTED);
        e.setTextColor(WHITE);
        e.setTextSize(16);
        e.setSingleLine(false);
        e.setPadding(
                dp(15),
                dp(10),
                dp(15),
                dp(10)
        );

        e.setBackground(bg(CARD2, 14));

        LinearLayout.LayoutParams p =
                new LinearLayout.LayoutParams(
                        -1,
                        dp(55)
                );

        p.topMargin = dp(10);

        e.setLayoutParams(p);

        return e;
    }

    // =========================================================
    // DEMO ACCOUNTS
    // =========================================================

    void loadDemoAccounts() {

        names.clear();
        games.clear();
        levels.clear();
        prices.clear();
        phones.clear();
        descriptions.clear();

        names.add("VEXO FF");
        games.add("Free Fire");
        levels.add("Level 70");
        prices.add("850");
        phones.add("+992 900 00 00 00");
        descriptions.add(
                "Аккаунти пурқувват бо скинҳо ва либосҳои зиёд."
        );

        names.add("TJ PUBG");
        games.add("PUBG");
        levels.add("Level 62");
        prices.add("650");
        phones.add("+992 900 11 11 11");
        descriptions.add(
                "Аккаунти PUBG бо инвентари хуб."
        );
    }

    // =========================================================
    // TEXT
    // =========================================================

    TextView text(
            String value,
            float size,
            int color,
            boolean bold
    ) {

        TextView t = new TextView(this);

        t.setText(value);
        t.setTextSize(size);
        t.setTextColor(color);
        t.setPadding(0, dp(5), 0, dp(5));

        if (bold) {
            t.setTypeface(
                    Typeface.DEFAULT,
                    Typeface.BOLD
            );
        }

        return t;
    }

    // =========================================================
    // BACKGROUND
    // =========================================================

    GradientDrawable bg(
            int color,
            int radius
    ) {

        GradientDrawable g = new GradientDrawable();

        g.setColor(color);

        if (radius > 0) {
            g.setCornerRadius(dp(radius));
        }

        return g;
    }

    // =========================================================
    // DP
    // =========================================================

    int dp(int value) {

        return (int) (
                value *
                getResources()
                        .getDisplayMetrics()
                        .density
                + 0.5f
        );
    }
}
