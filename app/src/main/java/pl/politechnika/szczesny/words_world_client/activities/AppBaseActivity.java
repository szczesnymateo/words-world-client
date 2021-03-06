package pl.politechnika.szczesny.words_world_client.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.google.android.material.navigation.NavigationView;

import java.util.Objects;


import androidx.appcompat.app.AppCompatActivity;
import butterknife.BindView;
import butterknife.ButterKnife;
import pl.politechnika.szczesny.words_world_client.R;

import static pl.politechnika.szczesny.words_world_client.utils.SharedPreferencesUtils.flushUserSP;

public class AppBaseActivity extends AppCompatActivity implements MenuItem.OnMenuItemClickListener {
    @BindView(R.id.view_stub) FrameLayout view_stub;
    @BindView(R.id.navigation_view) NavigationView navigation_view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_app_base);
        ButterKnife.bind(this);

        Menu drawerMenu = navigation_view.getMenu();
        for(int i = 0; i < drawerMenu.size(); i++) {
            drawerMenu.getItem(i).setOnMenuItemClickListener(this);
        }
    }

    @Override
    public void setContentView(int layoutResID) {
        if (view_stub != null) {
            LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
            ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT);
            View stubView = Objects.requireNonNull(inflater).inflate(layoutResID, view_stub, false);
            view_stub.addView(stubView, lp);
        }
    }

    @Override
    public void setContentView(View view) {
        if (view_stub != null) {
            ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT);
            view_stub.addView(view, lp);
        }
    }

    @Override
    public void setContentView(View view, ViewGroup.LayoutParams params) {
        if (view_stub != null) {
            view_stub.addView(view, params);
        }
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        Intent intent = new Intent();
        switch (item.getItemId()) {
            case R.id.nav_about:
                intent = new Intent(getBaseContext(), AboutActivity.class);
                break;
            case R.id.nav_friends:
                intent = new Intent(getBaseContext(), FriendsSearchActivity.class);
                break;
            case R.id.nav_languages:
                intent = new Intent(getBaseContext(), LanguagesActivity.class);
                break;
            case R.id.nav_taboo:
                intent = new Intent(getBaseContext(), PreTabooSettingsActivity.class);
                break;
            case R.id.nav_dictionary:
                intent = new Intent(getBaseContext(), TranslateActivity.class);
                break;
        }
        view_stub.getContext().startActivity(intent);
        return false;
    }

    public void logout(View view) {
        flushUserSP(getApplication());

        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(intent);
    }
}
