package tj.gametj.app;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Shader;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class MainActivity extends Activity {

    private static final int PICK_IMAGE = 1001;

    private static final int WHITE = Color.WHITE;
    private static final int RED = Color.rgb(240, 45, 60);
    private static final int GREEN = Color.rgb(0, 220, 115);
    private static final int CYAN = Color.rgb(45, 190, 255);
    private static final int MUTED = Color.rgb(155, 180, 215);
    private static final int CARD = Color.rgb(7, 28, 62);

    private FrameLayout root;
    private FrameLayout content;
    private FrameLayout networkOverlay;

    private SharedPreferences prefs;
    private ConnectivityManager connectivityManager;
    private ConnectivityManager.NetworkCallback networkCallback;

    private final ArrayList<Account> accounts = new ArrayList<>();
    private final Set<Integer> favorites = new HashSet<>();
    private final ArrayDeque<Page> history = new ArrayDeque<>();

    private Page currentPage = new Page("home", -1, "");
    private String selectedImageUri = null;
    private boolean firstLoading = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        prefs = getSharedPreferences("GAME_TJ", MODE_PRIVATE);
        connectivityManager =
                (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);

        loadAccounts();
        loadFavorites();
        showInitialLoading();
    }

    // ---------------- INITIAL LOADING ----------------

    private void showInitialLoading() {
        FrameLayout page = new FrameLayout(this);
        page.setBackgroundColor(Color.BLACK);

        page.addView(new GalaxyView(this),
                new FrameLayout.LayoutParams(-1, -1));

        LinearLayout box = vertical();
        box.setGravity(Gravity.CENTER_HORIZONTAL);

        ImageView logo = new ImageView(this);
        int logoId = getResources().getIdentifier(
                "ic_launcher", "mipmap", getPackageName());
        if (logoId != 0) logo.setImageResource(logoId);

        box.addView(logo, new LinearLayout.LayoutParams(dp(145), dp(145)));

        TextView title = text("GAME TJ", 30, WHITE);
        title.setGravity(Gravity.CENTER);
        box.addView(title);

        TextView sub = text("БОЗОРИ АККАУНТҲОИ БОЗӢ", 12, CYAN);
        sub.setGravity(Gravity.CENTER);
        box.addView(sub);

        DotView dots = new DotView(this);
        box.addView(dots, new LinearLayout.LayoutParams(dp(100), dp(50)));

        TextView wait = text("Пайвастшавӣ ба интернет...", 14, WHITE);
        wait.setGravity(Gravity.CENTER);
        box.addView(wait);

        FrameLayout.LayoutParams bp =
                new FrameLayout.LayoutParams(-1, -2, Gravity.CENTER);
        bp.leftMargin = dp(25);
        bp.rightMargin = dp(25);
        page.addView(box, bp);

        setContentView(page);

        AlphaAnimation pulse = new AlphaAnimation(.35f, 1f);
        pulse.setDuration(750);
        pulse.setRepeatMode(AlphaAnimation.REVERSE);
        pulse.setRepeatCount(AlphaAnimation.INFINITE);
        logo.startAnimation(pulse);

        dots.start();

        if (hasInternet()) {
            page.postDelayed(() -> {
                firstLoading = false;
                showHome(false);
            }, 1000);
        } else {
            registerNetworkCallback();
        }
    }

    private boolean hasInternet() {
        try {
            Network n = connectivityManager.getActiveNetwork();
            if (n == null) return false;
            NetworkCapabilities c =
                    connectivityManager.getNetworkCapabilities(n);
            return c != null &&
                    c.hasCapability(
                            NetworkCapabilities.NET_CAPABILITY_INTERNET);
        } catch (Exception e) {
            return false;
        }
    }

    private void registerNetworkCallback() {
        if (networkCallback != null) return;

        networkCallback = new ConnectivityManager.NetworkCallback() {
            @Override
            public void onAvailable(Network network) {
                runOnUiThread(() -> {
                    if (firstLoading) {
                        unregisterNetworkCallback();
                        firstLoading = false;
                        getWindow().getDecorView().postDelayed(
                                () -> showHome(false), 700);
                    } else {
                        hideNetworkLoading();
                    }
                });
            }

            @Override
            public void onLost(Network network) {
                runOnUiThread(() -> {
                    if (!firstLoading) showNetworkLoading();
                });
            }
        };

        try {
            NetworkRequest request = new NetworkRequest.Builder()
                    .addCapability(
                            NetworkCapabilities.NET_CAPABILITY_INTERNET)
                    .build();
            connectivityManager.registerNetworkCallback(
                    request, networkCallback);
        } catch (Exception ignored) {
        }
    }

    private void unregisterNetworkCallback() {
        if (networkCallback == null) return;
        try {
            connectivityManager.unregisterNetworkCallback(networkCallback);
        } catch (Exception ignored) {
        }
        networkCallback = null;
    }

    // ---------------- ROOT ----------------

    private void createRoot() {
        root = new FrameLayout(this);
        root.addView(new GalaxyView(this),
                new FrameLayout.LayoutParams(-1, -1));

        content = new FrameLayout(this);
        root.addView(content,
                new FrameLayout.LayoutParams(-1, -1));

        setContentView(root);
    }

    // ---------------- HOME ----------------

    private void showHome(boolean push) {
        if (push) history.push(currentPage);
        currentPage = new Page("home", -1, "");

        createRoot();

        LinearLayout main = vertical();
        ScrollView scroll = new ScrollView(this);
        LinearLayout body = vertical();
        body.setPadding(dp(16), dp(16), dp(16), dp(90));

        LinearLayout header = horizontal();
        TextView menu = text("☰", 27, WHITE);
        header.addView(menu, new LinearLayout.LayoutParams(dp(45), dp(50)));

        LinearLayout titleBox = vertical();
        TextView title = text("GAME TJ", 24, WHITE);
        title.setTypeface(null, 1);
        titleBox.addView(title);
        titleBox.addView(text("Бозори аккаунтҳои бозӣ", 11, CYAN));
        header.addView(titleBox,
                new LinearLayout.LayoutParams(0, -2, 1));
        header.addView(text("☆", 28, WHITE),
                new LinearLayout.LayoutParams(dp(45), dp(50)));
        body.addView(header);

        EditText search = edit("Ҷустуҷӯи аккаунт ё бозӣ...");
        body.addView(search, fieldParams());
        search.setOnEditorActionListener((v, a, e) -> {
            showSearch(search.getText().toString().trim(), true);
            return true;
        });

        LinearLayout banner = vertical();
        banner.setPadding(dp(18), dp(15), dp(18), dp(15));
        banner.setBackground(roundGradient(
                Color.rgb(8, 50, 105), Color.rgb(16, 105, 185), 22));

        TextView b1 = text("FREE FIRE", 25, WHITE);
        b1.setTypeface(null, 1);
        banner.addView(b1);
        banner.addView(text("Аккаунтҳои бозӣ бо акс ва маълумоти пурра",
                12, MUTED));

        Button open = button("Ба категория →");
        LinearLayout.LayoutParams op =
                new LinearLayout.LayoutParams(dp(165), dp(42));
        op.topMargin = dp(12);
        banner.addView(open, op);
        open.setOnClickListener(v -> showCategory("Free Fire", true));

        body.addView(banner,
                new LinearLayout.LayoutParams(-1, dp(145)));

        addSectionTitle(body, "Категорияҳо", "Ҳама",
                v -> showCategories(true));

        LinearLayout cats = horizontal();
        cats.addView(categoryCard("🔥", "Free Fire"), weight());
        cats.addView(categoryCard("🎯", "PUBG"), weight());
        cats.addView(categoryCard("🎮", "Дигар"), weight());
        body.addView(cats);

        addSectionTitle(body, "Эълонҳои нав", "Ҳама",
                v -> showSearch("", true));

        for (int i = 0; i < Math.min(accounts.size(), 8); i++)
            body.addView(accountCard(i));

        if (accounts.isEmpty()) {
            TextView empty = text(
                    "Ҳоло эълоне нест.\nЭълони аввалро илова кунед.",
                    15, MUTED);
            empty.setGravity(Gravity.CENTER);
            body.addView(empty,
                    new LinearLayout.LayoutParams(-1, dp(180)));
        }

        scroll.addView(body);
        main.addView(scroll,
                new LinearLayout.LayoutParams(-1, 0, 1));
        main.addView(bottomNav(0));
        content.addView(main);
    }

    // ---------------- CATEGORIES ----------------

    private void showCategories(boolean push) {
        if (push) history.push(currentPage);
        currentPage = new Page("categories", -1, "");
        createRoot();

        LinearLayout main = vertical();
        main.addView(topBar("Категорияҳо", v -> goBack()));

        LinearLayout body = vertical();
        body.setPadding(dp(16), dp(10), dp(16), dp(30));

        body.addView(categoryLarge("🔥", "Free Fire",
                "Аккаунтҳои Free Fire"), largeParams());
        body.addView(categoryLarge("🎯", "PUBG Mobile",
                "Аккаунтҳои PUBG"), largeParams());
        body.addView(categoryLarge("🎮", "Дигар бозиҳо",
                "Дигар аккаунтҳо"), largeParams());

        ScrollView s = new ScrollView(this);
        s.addView(body);
        main.addView(s, new LinearLayout.LayoutParams(-1, 0, 1));
        main.addView(bottomNav(-1));
        content.addView(main);
    }

    private void showCategory(String category, boolean push) {
        if (push) history.push(currentPage);
        currentPage = new Page("category", -1, category);
        createRoot();

        LinearLayout main = vertical();
        main.addView(topBar(category, v -> goBack()));

        LinearLayout body = vertical();
        body.setPadding(dp(16), dp(10), dp(16), dp(30));

        for (int i = 0; i < accounts.size(); i++) {
            if (category.equals("Free Fire") &&
                    accounts.get(i).game.equalsIgnoreCase("Free Fire"))
                body.addView(accountCard(i));
            if (category.equals("PUBG") &&
                    accounts.get(i).game.equalsIgnoreCase("PUBG"))
                body.addView(accountCard(i));
        }

        ScrollView s = new ScrollView(this);
        s.addView(body);
        main.addView(s, new LinearLayout.LayoutParams(-1, 0, 1));
        main.addView(bottomNav(-1));
        content.addView(main);
    }

    // ---------------- SEARCH ----------------

    private void showSearch(String query, boolean push) {
        if (push) history.push(currentPage);
        currentPage = new Page("search", -1, query);
        createRoot();

        LinearLayout main = vertical();
        main.addView(topBar("Ҷустуҷӯ", v -> goBack()));

        EditText search = edit("Ҷустуҷӯ...");
        search.setText(query);
        LinearLayout.LayoutParams sp = fieldParams();
        sp.leftMargin = dp(16);
        sp.rightMargin = dp(16);
        main.addView(search, sp);

        LinearLayout body = vertical();
        body.setPadding(dp(16), 0, dp(16), dp(30));
        String q = query.toLowerCase();

        for (int i = 0; i < accounts.size(); i++) {
            Account a = accounts.get(i);
            if (q.isEmpty() ||
                    a.title.toLowerCase().contains(q) ||
                    a.game.toLowerCase().contains(q))
                body.addView(accountCard(i));
        }

        ScrollView s = new ScrollView(this);
        s.addView(body);
        main.addView(s, new LinearLayout.LayoutParams(-1, 0, 1));
        main.addView(bottomNav(1));
        content.addView(main);

        search.setOnEditorActionListener((v, id, event) -> {
            showSearch(search.getText().toString().trim(), false);
            return true;
        });
    }

    // ---------------- FAVORITES ----------------

    private void showFavorites(boolean push) {
        if (push) history.push(currentPage);
        currentPage = new Page("favorites", -1, "");
        createRoot();

        LinearLayout main = vertical();
        main.addView(topBar("Избранное", v -> goBack()));

        LinearLayout body = vertical();
        body.setPadding(dp(16), dp(10), dp(16), dp(30));

        for (Integer i : favorites)
            if (i >= 0 && i < accounts.size())
                body.addView(accountCard(i));

        if (favorites.isEmpty()) {
            TextView e = text("♡\n\nҲоло эълони интихобшуда нест.",
                    16, MUTED);
            e.setGravity(Gravity.CENTER);
            body.addView(e,
                    new LinearLayout.LayoutParams(-1, dp(220)));
        }

        ScrollView s = new ScrollView(this);
        s.addView(body);
        main.addView(s, new LinearLayout.LayoutParams(-1, 0, 1));
        main.addView(bottomNav(2));
        content.addView(main);
    }

    // ---------------- ADD LISTING ----------------

    private void showAddListing(boolean push) {
        if (push) history.push(currentPage);
        currentPage = new Page("add", -1, "");
        createRoot();

        LinearLayout main = vertical();
        main.addView(topBar("Илова кардани эълон", v -> goBack()));

        ScrollView s = new ScrollView(this);
        LinearLayout body = vertical();
        body.setPadding(dp(16), dp(10), dp(16), dp(30));

        EditText title = edit("Номи аккаунт *");
        EditText game = edit("Бозӣ: Free Fire / PUBG *");
        EditText level = edit("Level *");
        EditText price = edit("Нарх (сомонӣ) *");
        EditText phone = edit("Телефон *");
        EditText desc = edit("Тавсифи аккаунт *");
        desc.setSingleLine(false);
        desc.setMinLines(4);
        desc.setGravity(Gravity.TOP);

        body.addView(title, fieldParams());
        body.addView(game, fieldParams());
        body.addView(level, fieldParams());
        body.addView(price, fieldParams());
        body.addView(phone, fieldParams());
        body.addView(desc, fieldParams());

        TextView required = text("Акс — ҳатмӣ *", 15, WHITE);
        required.setTypeface(null, 1);
        body.addView(required);

        ImageView preview = new ImageView(this);
        preview.setScaleType(ImageView.ScaleType.CENTER_CROP);
        preview.setBackground(roundGradient(
                Color.rgb(6, 28, 60),
                Color.rgb(10, 48, 90), 18));

        if (selectedImageUri != null) {
            try {
                preview.setImageURI(Uri.parse(selectedImageUri));
            } catch (Exception ignored) {}
        }

        LinearLayout.LayoutParams pp =
                new LinearLayout.LayoutParams(-1, dp(210));
        pp.topMargin = dp(8);
        pp.bottomMargin = dp(10);
        body.addView(preview, pp);

        Button choose = button("🖼 Интихоби акс аз Галерея");
        body.addView(choose,
                new LinearLayout.LayoutParams(-1, dp(50)));

        choose.setOnClickListener(v -> {
            Intent i = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            i.setType("image/*");
            i.addCategory(Intent.CATEGORY_OPENABLE);
            i.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION |
                    Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
            startActivityForResult(i, PICK_IMAGE);
        });

        Button publish = button("➕ Нашри эълон");
        LinearLayout.LayoutParams pub =
                new LinearLayout.LayoutParams(-1, dp(54));
        pub.topMargin = dp(16);
        body.addView(publish, pub);

        publish.setOnClickListener(v -> {
            if (selectedImageUri == null) {
                Toast.makeText(this,
                        "Бе акс эълон нашр намешавад!",
                        Toast.LENGTH_LONG).show();
                return;
            }

            if (title.getText().toString().trim().isEmpty() ||
                    game.getText().toString().trim().isEmpty() ||
                    level.getText().toString().trim().isEmpty() ||
                    price.getText().toString().trim().isEmpty() ||
                    phone.getText().toString().trim().isEmpty()) {
                Toast.makeText(this,
                        "Майдонҳои ҳатмиро пур кунед!",
                        Toast.LENGTH_LONG).show();
                return;
            }

            showNetworkLoading();

            main.postDelayed(() -> {
                accounts.add(0, new Account(
                        title.getText().toString().trim(),
                        game.getText().toString().trim(),
                        level.getText().toString().trim(),
                        price.getText().toString().trim(),
                        phone.getText().toString().trim(),
                        desc.getText().toString().trim(),
                        selectedImageUri
                ));

                saveAccounts();
                selectedImageUri = null;
                hideNetworkLoading();

                Toast.makeText(this,
                        "Эълон нашр шуд ✓",
                        Toast.LENGTH_LONG).show();

                history.clear();
                showHome(false);
            }, 700);
        });

        s.addView(body);
        main.addView(s,
                new LinearLayout.LayoutParams(-1, 0, 1));
        content.addView(main);
    }

    @Override
    protected void onActivityResult(int requestCode,
                                    int resultCode,
                                    Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE &&
                resultCode == RESULT_OK &&
                data != null &&
                data.getData() != null) {

            Uri uri = data.getData();

            try {
                getContentResolver().takePersistableUriPermission(
                        uri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
            } catch (Exception ignored) {}

            selectedImageUri = uri.toString();

            Toast.makeText(this,
                    "Акс интихоб шуд ✓",
                    Toast.LENGTH_SHORT).show();

            showAddListing(false);
        }
    }

    // ---------------- PROFILE ----------------

    private void showProfile(boolean push) {
        if (push) history.push(currentPage);
        currentPage = new Page("profile", -1, "");
        createRoot();

        LinearLayout main = vertical();
        main.addView(topBar("Профил", v -> goBack()));

        LinearLayout body = vertical();
        body.setGravity(Gravity.CENTER_HORIZONTAL);
        body.setPadding(dp(16), dp(20), dp(16), dp(30));

        TextView avatar = text("👤", 58, WHITE);
        avatar.setGravity(Gravity.CENTER);
        avatar.setBackground(roundGradient(
                Color.rgb(7, 65, 130),
                Color.rgb(15, 120, 205), 100));
        body.addView(avatar,
                new LinearLayout.LayoutParams(dp(110), dp(110)));

        TextView name = text("GAME TJ", 23, WHITE);
        name.setTypeface(null, 1);
        name.setGravity(Gravity.CENTER);
        body.addView(name);

        body.addView(text("Профили истифодабаранда",
                13, MUTED));

        Button ads = button("📦 Эълонҳои ман");
        Button fav = button("♡ Избранное");

        body.addView(ads, menuParams());
        body.addView(fav, menuParams());

        ads.setOnClickListener(v -> showMyAds(true));
        fav.setOnClickListener(v -> showFavorites(true));

        ScrollView s = new ScrollView(this);
        s.addView(body);
        main.addView(s, new LinearLayout.LayoutParams(-1, 0, 1));
        main.addView(bottomNav(4));
        content.addView(main);
    }

    private void showMyAds(boolean push) {
        if (push) history.push(currentPage);
        currentPage = new Page("myads", -1, "");
        createRoot();

        LinearLayout main = vertical();
        main.addView(topBar("Эълонҳои ман", v -> goBack()));

        LinearLayout body = vertical();
        body.setPadding(dp(16), dp(10), dp(16), dp(30));

        for (int i = 0; i < accounts.size(); i++)
            body.addView(accountCard(i));

        ScrollView s = new ScrollView(this);
        s.addView(body);
        main.addView(s, new LinearLayout.LayoutParams(-1, 0, 1));
        content.addView(main);
    }

    // ---------------- ACCOUNT CARD / DETAIL ----------------

    private View accountCard(final int index) {
        Account a = accounts.get(index);

        LinearLayout card = vertical();
        card.setPadding(dp(10), dp(10), dp(10), dp(10));
        card.setBackground(roundGradient(
                Color.rgb(8, 31, 68),
                Color.rgb(5, 22, 50), 18));

        LinearLayout.LayoutParams cp =
                new LinearLayout.LayoutParams(-1, dp(108));
        cp.bottomMargin = dp(10);

        LinearLayout row = horizontal();

        ImageView image = new ImageView(this);
        image.setScaleType(ImageView.ScaleType.CENTER_CROP);

        if (a.imageUri != null && !a.imageUri.isEmpty()) {
            try {
                image.setImageURI(Uri.parse(a.imageUri));
            } catch (Exception ignored) {}
        }

        if (image.getDrawable() == null) {
            int logoId = getResources().getIdentifier(
                    "ic_launcher", "mipmap", getPackageName());
            if (logoId != 0) image.setImageResource(logoId);
        }

        row.addView(image,
                new LinearLayout.LayoutParams(dp(84), dp(84)));

        LinearLayout info = vertical();
        info.setPadding(dp(12), 0, dp(4), 0);

        TextView t = text(a.title, 16, WHITE);
        t.setTypeface(null, 1);
        info.addView(t);

        info.addView(text(
                a.game + " • Level " + a.level, 12, MUTED));
        info.addView(text(a.price + " сомонӣ", 15, WHITE));
        info.addView(text("● Онлайн", 11, GREEN));

        row.addView(info,
                new LinearLayout.LayoutParams(0, -1, 1));

        TextView heart = text(
                favorites.contains(index) ? "♥" : "♡",
                27,
                favorites.contains(index) ? RED : WHITE);
        heart.setGravity(Gravity.CENTER);
        row.addView(heart,
                new LinearLayout.LayoutParams(dp(42), dp(84)));

        card.addView(row);
        card.setOnClickListener(v -> showAccount(index, true));

        heart.setOnClickListener(v -> {
            if (favorites.contains(index))
                favorites.remove(index);
            else
                favorites.add(index);
            saveFavorites();
            refreshCurrent();
        });

        return card;
    }

    private void showAccount(int index, boolean push) {
        if (index < 0 || index >= accounts.size()) return;
        if (push) history.push(currentPage);
        currentPage = new Page("detail", index, "");
        createRoot();

        Account a = accounts.get(index);

        LinearLayout main = vertical();
        main.addView(topBar("Аккаунт", v -> goBack()));

        ScrollView s = new ScrollView(this);
        LinearLayout body = vertical();
        body.setPadding(dp(16), dp(10), dp(16), dp(30));

        ImageView image = new ImageView(this);
        image.setScaleType(ImageView.ScaleType.CENTER_CROP);
        if (a.imageUri != null && !a.imageUri.isEmpty()) {
            try { image.setImageURI(Uri.parse(a.imageUri)); }
            catch (Exception ignored) {}
        }
        body.addView(image,
                new LinearLayout.LayoutParams(-1, dp(240)));

        TextView title = text(a.title, 25, WHITE);
        title.setTypeface(null, 1);
        body.addView(title);

        body.addView(text(
                a.game + " • Level " + a.level, 14, MUTED));
        body.addView(text(
                a.price + " сомонӣ", 23, WHITE));
        body.addView(text("● Онлайн", 12, GREEN));
        body.addView(text(
                "\nТавсиф:\n" + a.description, 14, WHITE));
        body.addView(text(
                "\nТелефон: " + a.phone, 14, MUTED));

        Button fav = button(
                favorites.contains(index)
                        ? "♥ Аз избранное хориҷ кардан"
                        : "♡ Ба избранное");
        body.addView(fav, menuParams());

        fav.setOnClickListener(v -> {
            if (favorites.contains(index))
                favorites.remove(index);
            else
                favorites.add(index);
            saveFavorites();
            showAccount(index, false);
        });

        Button contact = button("📞 Тамос гирифтан");
        body.addView(contact, menuParams());
        contact.setOnClickListener(v ->
                Toast.makeText(this,
                        "Телефон: " + a.phone,
                        Toast.LENGTH_LONG).show());

        s.addView(body);
        main.addView(s, new LinearLayout.LayoutParams(-1, 0, 1));
        content.addView(main);
    }

    // ---------------- BOTTOM NAV ----------------

    private View bottomNav(int selected) {
        LinearLayout nav = horizontal();
        nav.setPadding(dp(4), dp(4), dp(4), dp(6));
        nav.setBackgroundColor(Color.argb(238, 2, 10, 28));

        String[] labels = {
                "⌂\nАсосӣ", "⌕\nҶустуҷӯ", "♡\nИзбранное",
                "+\nИлова", "♙\nПрофил"
        };

        for (int i = 0; i < labels.length; i++) {
            final int n = i;
            TextView item = text(labels[i], 12,
                    selected == i ? CYAN : MUTED);
            item.setGravity(Gravity.CENTER);
            nav.addView(item,
                    new LinearLayout.LayoutParams(0, dp(62), 1));

            item.setOnClickListener(v -> {
                history.clear();
                if (n == 0) showHome(false);
                else if (n == 1) showSearch("", false);
                else if (n == 2) showFavorites(false);
                else if (n == 3) showAddListing(false);
                else showProfile(false);
            });
        }
        return nav;
    }

    // ---------------- NETWORK OVERLAY ----------------

    private void showNetworkLoading() {
        if (root == null || networkOverlay != null) return;

        networkOverlay = new FrameLayout(this);
        networkOverlay.setBackgroundColor(
                Color.argb(175, 0, 4, 15));
        networkOverlay.setClickable(true);

        NetworkSpinner spinner = new NetworkSpinner(this);
        networkOverlay.addView(spinner,
                new FrameLayout.LayoutParams(
                        dp(100), dp(100), Gravity.CENTER));

        TextView t = text(
                "Пайвастшавӣ ба интернет...", 13, WHITE);
        t.setGravity(Gravity.CENTER);

        FrameLayout.LayoutParams tp =
                new FrameLayout.LayoutParams(
                        -1, dp(40), Gravity.CENTER);
        tp.topMargin = dp(125);
        networkOverlay.addView(t, tp);

        root.addView(networkOverlay,
                new FrameLayout.LayoutParams(-1, -1));

        registerNetworkCallback();
    }

    private void hideNetworkLoading() {
        if (networkOverlay != null && root != null) {
            root.removeView(networkOverlay);
            networkOverlay = null;
        }
        unregisterNetworkCallback();
    }

    // ---------------- BACK ----------------

    private void goBack() {
        if (history.isEmpty()) {
            showHome(false);
            return;
        }
        Page p = history.pop();
        renderPage(p);
    }

    private void renderPage(Page p) {
        currentPage = p;
        if (p.type.equals("home")) showHome(false);
        else if (p.type.equals("categories")) showCategories(false);
        else if (p.type.equals("category")) showCategory(p.data, false);
        else if (p.type.equals("search")) showSearch(p.data, false);
        else if (p.type.equals("favorites")) showFavorites(false);
        else if (p.type.equals("add")) showAddListing(false);
        else if (p.type.equals("profile")) showProfile(false);
        else if (p.type.equals("myads")) showMyAds(false);
        else if (p.type.equals("detail")) showAccount(p.index, false);
        else showHome(false);
    }

    @Override
    public void onBackPressed() {
        if (firstLoading) {
            super.onBackPressed();
            return;
        }
        if (!history.isEmpty()) goBack();
        else super.onBackPressed();
    }

    // ---------------- STORAGE ----------------

    private void saveAccounts() {
        SharedPreferences.Editor e = prefs.edit();
        e.putInt("count", accounts.size());

        for (int i = 0; i < accounts.size(); i++) {
            Account a = accounts.get(i);
            e.putString("title_" + i, a.title);
            e.putString("game_" + i, a.game);
            e.putString("level_" + i, a.level);
            e.putString("price_" + i, a.price);
            e.putString("phone_" + i, a.phone);
            e.putString("desc_" + i, a.description);
            e.putString("image_" + i,
                    a.imageUri == null ? "" : a.imageUri);
        }
        e.apply();
    }

    private void loadAccounts() {
        accounts.clear();
        int count = prefs.getInt("count", 0);

        if (count == 0) {
            // Sample cards only. Real user listings must have an image.
            accounts.add(new Account(
                    "VEXO FF", "Free Fire", "70", "850",
                    "+992 900 00 00 00",
                    "Намунаи эълон барои намоиши дизайн.", ""));
            accounts.add(new Account(
                    "TJ PUBG", "PUBG", "62", "650",
                    "+992 900 00 00 00",
                    "Намунаи эълони PUBG.", ""));
            return;
        }

        for (int i = 0; i < count; i++) {
            accounts.add(new Account(
                    prefs.getString("title_" + i, ""),
                    prefs.getString("game_" + i, ""),
                    prefs.getString("level_" + i, ""),
                    prefs.getString("price_" + i, ""),
                    prefs.getString("phone_" + i, ""),
                    prefs.getString("desc_" + i, ""),
                    prefs.getString("image_" + i, "")
            ));
        }
    }

    private void saveFavorites() {
        StringBuilder s = new StringBuilder();
        for (Integer i : favorites) {
            if (s.length() > 0) s.append(",");
            s.append(i);
        }
        prefs.edit().putString("favorites", s.toString()).apply();
    }

    private void loadFavorites() {
        favorites.clear();
        String s = prefs.getString("favorites", "");
        if (s.isEmpty()) return;
        for (String p : s.split(",")) {
            try { favorites.add(Integer.parseInt(p)); }
            catch (Exception ignored) {}
        }
    }

    private void refreshCurrent() {
        renderPage(currentPage);
    }

    // ---------------- UI HELPERS ----------------

    private LinearLayout vertical() {
        LinearLayout l = new LinearLayout(this);
        l.setOrientation(LinearLayout.VERTICAL);
        l.setBackgroundColor(Color.TRANSPARENT);
        return l;
    }

    private LinearLayout horizontal() {
        LinearLayout l = new LinearLayout(this);
        l.setOrientation(LinearLayout.HORIZONTAL);
        l.setGravity(Gravity.CENTER_VERTICAL);
        l.setBackgroundColor(Color.TRANSPARENT);
        return l;
    }

    private TextView text(String s, int size, int color) {
        TextView t = new TextView(this);
        t.setText(s);
        t.setTextSize(size);
        t.setTextColor(color);
        return t;
    }

    private EditText edit(String hint) {
        EditText e = new EditText(this);
        e.setHint(hint);
        e.setHintTextColor(Color.rgb(115, 145, 180));
        e.setTextColor(WHITE);
        e.setTextSize(14);
        e.setPadding(dp(15), 0, dp(15), 0);
        e.setBackground(roundGradient(
                Color.rgb(7, 28, 62),
                Color.rgb(8, 38, 78), 15));
        return e;
    }

    private Button button(String s) {
        Button b = new Button(this);
        b.setText(s);
        b.setTextColor(WHITE);
        b.setTextSize(13);
        b.setAllCaps(false);
        b.setBackground(roundGradient(
                Color.rgb(8, 91, 210),
                Color.rgb(18, 145, 255), 16));
        return b;
    }

    private GradientDrawable roundGradient(
            int c1, int c2, int radius) {
        GradientDrawable g =
                new GradientDrawable(
                        GradientDrawable.Orientation.LEFT_RIGHT,
                        new int[]{c1, c2});
        g.setCornerRadius(dp(radius));
        return g;
    }

    private LinearLayout.LayoutParams fieldParams() {
        LinearLayout.LayoutParams p =
                new LinearLayout.LayoutParams(-1, dp(52));
        p.bottomMargin = dp(10);
        return p;
    }

    private LinearLayout.LayoutParams menuParams() {
        LinearLayout.LayoutParams p =
                new LinearLayout.LayoutParams(-1, dp(52));
        p.topMargin = dp(10);
        return p;
    }

    private LinearLayout.LayoutParams weight() {
        LinearLayout.LayoutParams p =
                new LinearLayout.LayoutParams(0, dp(98), 1);
        p.setMargins(dp(4), dp(4), dp(4), dp(4));
        return p;
    }

    private LinearLayout.LayoutParams largeParams() {
        LinearLayout.LayoutParams p =
                new LinearLayout.LayoutParams(-1, dp(88));
        p.bottomMargin = dp(10);
        return p;
    }

    private void addSectionTitle(
            LinearLayout parent, String left, String right,
            View.OnClickListener listener) {
        LinearLayout row = horizontal();
        row.setPadding(0, dp(15), 0, dp(7));

        TextView l = text(left, 19, WHITE);
        l.setTypeface(null, 1);
        row.addView(l,
                new LinearLayout.LayoutParams(0, dp(38), 1));

        TextView r = text(right, 12, CYAN);
        r.setGravity(Gravity.CENTER);
        r.setOnClickListener(listener);
        row.addView(r,
                new LinearLayout.LayoutParams(dp(55), dp(38)));

        parent.addView(row);
    }

    private View categoryCard(String icon, String name) {
        LinearLayout c = vertical();
        c.setGravity(Gravity.CENTER);
        c.setPadding(dp(4), dp(4), dp(4), dp(4));
        c.setBackground(roundGradient(
                Color.rgb(7, 29, 63),
                Color.rgb(10, 48, 90), 17));

        TextView i = text(icon, 28, WHITE);
        i.setGravity(Gravity.CENTER);
        c.addView(i);

        TextView n = text(name, 12, WHITE);
        n.setGravity(Gravity.CENTER);
        c.addView(n);

        c.setOnClickListener(v -> {
            if (name.equals("Free Fire"))
                showCategory("Free Fire", true);
            else if (name.equals("PUBG"))
                showCategory("PUBG", true);
            else
                showSearch("", true);
        });
        return c;
    }

    private View categoryLarge(
            String icon, String title, String sub) {
        LinearLayout c = horizontal();
        c.setPadding(dp(14), dp(8), dp(14), dp(8));
        c.setBackground(roundGradient(
                Color.rgb(7, 29, 63),
                Color.rgb(10, 48, 90), 18));

        TextView i = text(icon, 34, WHITE);
        c.addView(i,
                new LinearLayout.LayoutParams(dp(60), dp(70)));

        LinearLayout info = vertical();
        TextView t = text(title, 18, WHITE);
        t.setTypeface(null, 1);
        info.addView(t);
        info.addView(text(sub, 12, MUTED));

        c.addView(info,
                new LinearLayout.LayoutParams(0, -2, 1));

        c.setOnClickListener(v -> {
            if (title.equals("Free Fire"))
                showCategory("Free Fire", true);
            else if (title.equals("PUBG Mobile"))
                showCategory("PUBG", true);
            else
                showSearch("", true);
        });
        return c;
    }

    private View topBar(String title, View.OnClickListener back) {
        LinearLayout bar = horizontal();
        bar.setPadding(dp(5), dp(5), dp(8), dp(5));

        TextView b = text("‹", 38, WHITE);
        b.setGravity(Gravity.CENTER);
        b.setOnClickListener(back);
        bar.addView(b,
                new LinearLayout.LayoutParams(dp(50), dp(55)));

        TextView t = text(title, 21, WHITE);
        t.setTypeface(null, 1);
        bar.addView(t,
                new LinearLayout.LayoutParams(0, dp(55), 1));
        return bar;
    }

    private int dp(int v) {
        return (int) (v * getResources()
                .getDisplayMetrics().density + .5f);
    }

    // ---------------- DATA ----------------

    private static class Account {
        String title, game, level, price, phone, description, imageUri;

        Account(String title, String game, String level,
                String price, String phone,
                String description, String imageUri) {
            this.title = title;
            this.game = game;
            this.level = level;
            this.price = price;
            this.phone = phone;
            this.description = description;
            this.imageUri = imageUri;
        }
    }

    private static class Page {
        String type, data;
        int index;

        Page(String type, int index, String data) {
            this.type = type;
            this.index = index;
            this.data = data;
        }
    }

    // ---------------- GALAXY ----------------

    private class GalaxyView extends View {
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        float[] x = new float[110];
        float[] y = new float[110];
        float[] r = new float[110];

        GalaxyView(Context c) {
            super(c);
            for (int i = 0; i < 110; i++) {
                x[i] = (float) Math.random();
                y[i] = (float) Math.random();
                r[i] = .6f + (float) Math.random() * 2f;
            }
        }

        @Override
        protected void onDraw(Canvas c) {
            int w = getWidth(), h = getHeight();

            paint.setShader(new LinearGradient(
                    0, 0, w, h,
                    new int[]{
                            Color.rgb(1, 6, 20),
                            Color.rgb(4, 17, 45),
                            Color.rgb(1, 5, 18)
                    },
                    null, Shader.TileMode.CLAMP));
            c.drawRect(0, 0, w, h, paint);
            paint.setShader(null);

            paint.setColor(Color.argb(28, 50, 110, 255));
            c.drawCircle(w * .72f, h * .22f,
                    w * .40f, paint);

            paint.setColor(Color.argb(20, 150, 60, 255));
            c.drawCircle(w * .18f, h * .70f,
                    w * .34f, paint);

            for (int i = 0; i < x.length; i++) {
                paint.setColor(Color.argb(
                        90 + (i * 37) % 150,
                        220, 235, 255));
                c.drawCircle(x[i] * w, y[i] * h, r[i], paint);
            }
        }
    }

    // ---------------- DOTS / SPINNER ----------------

    private class DotView extends View {
        Paint p = new Paint(Paint.ANTI_ALIAS_FLAG);
        ValueAnimator animator;
        float value;

        DotView(Context c) { super(c); }

        void start() {
            animator = ValueAnimator.ofFloat(0, 1);
            animator.setDuration(1200);
            animator.setRepeatCount(ValueAnimator.INFINITE);
            animator.addUpdateListener(a -> {
                value = (float) a.getAnimatedValue();
                invalidate();
            });
            animator.start();
        }

        @Override
        protected void onDraw(Canvas c) {
            float cx = getWidth() / 2f;
            float cy = getHeight() / 2f;
            drawDot(c, cx - dp(18), cy, RED);
            drawDot(c, cx, cy, WHITE);
            drawDot(c, cx + dp(18), cy, GREEN);
        }

        private void drawDot(Canvas c, float x, float y, int color) {
            p.setColor(color);
            c.drawCircle(x, y, dp(5), p);
        }
    }

    private class NetworkSpinner extends View {
        Paint p = new Paint(Paint.ANTI_ALIAS_FLAG);
        ValueAnimator animator;
        float angle;

        NetworkSpinner(Context c) {
            super(c);
            animator = ValueAnimator.ofFloat(0, 360);
            animator.setDuration(900);
            animator.setRepeatCount(ValueAnimator.INFINITE);
            animator.addUpdateListener(a -> {
                angle = (float) a.getAnimatedValue();
                invalidate();
            });
            animator.start();
        }

        @Override
        protected void onDraw(Canvas c) {
            float cx = getWidth() / 2f;
            float cy = getHeight() / 2f;

            c.save();
            c.rotate(angle, cx, cy);

            float r1 = dp(22);
            float r2 = dp(32);

            p.setStyle(Paint.Style.STROKE);
            p.setStrokeWidth(dp(2));
            p.setStrokeCap(Paint.Cap.ROUND);

            for (int i = 0; i < 12; i++) {
                int alpha = 70 + i * 15;
                if (alpha > 255) alpha = 255;
                p.setColor(Color.argb(alpha, 255, 255, 255));

                double a = Math.toRadians(i * 30);
                float x1 = cx + (float)Math.cos(a) * r1;
                float y1 = cy + (float)Math.sin(a) * r1;
                float x2 = cx + (float)Math.cos(a) * r2;
                float y2 = cy + (float)Math.sin(a) * r2;
                c.drawLine(x1, y1, x2, y2, p);
            }

            p.setStyle(Paint.Style.FILL);
            c.restore();
        }
    }

    @Override
    protected void onDestroy() {
        unregisterNetworkCallback();
        super.onDestroy();
    }
}
