package pl.politechnika.szczesny.words_world_client.activities;

import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Toast;

import java.util.Calendar;

import androidx.appcompat.app.AppCompatActivity;
import mehdi.sakout.aboutpage.AboutPage;
import mehdi.sakout.aboutpage.Element;
import pl.politechnika.szczesny.words_world_client.R;

public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        View aboutPage = new AboutPage(this)
                .setDescription("Aplikacja zostałą stworzona przez Mateusza Szczęsnego, na potrzeby pracy dyplomowej.\n Specjalne podziękowania dla Vectors Market, autora ikony aplikacji.")
                .addItem(new Element().setTitle("Version 1.0"))
                .addGroup("Skontaktuj się z nami")
                .addEmail("matty.szczesny@gmail.com")
                .addWebsite("https://o-and-m.ovh")
                .addGitHub("mateusz-szczesny")
                .addItem(getCopyRightsElement())
                .create();

        setContentView(aboutPage);
    }

    private Element getCopyRightsElement() {
        Element copyRightsElement = new Element();
        final String copyrights = String.format(getString(R.string.copy_right), Calendar.getInstance().get(Calendar.YEAR));
        copyRightsElement.setTitle(copyrights);
        copyRightsElement.setIconDrawable(R.drawable.about_icon_copy_right);
        copyRightsElement.setIconTint(mehdi.sakout.aboutpage.R.color.about_item_icon_color);
        copyRightsElement.setIconNightTint(android.R.color.white);
        copyRightsElement.setGravity(Gravity.CENTER);
        copyRightsElement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(AboutActivity.this, copyrights, Toast.LENGTH_SHORT).show();
            }
        });
        return copyRightsElement;
    }
}
