package pl.politechnika.szczesny.words_world_client.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.ResponseBody;
import pl.politechnika.szczesny.words_world_client.R;
import pl.politechnika.szczesny.words_world_client.models.Language;
import pl.politechnika.szczesny.words_world_client.models.Statistics;
import pl.politechnika.szczesny.words_world_client.models.Translation;
import pl.politechnika.szczesny.words_world_client.network.GoogleTranslate;
import pl.politechnika.szczesny.words_world_client.viewmodel.LanguageViewModel;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TranslateActivity extends AppCompatActivity {

    @BindView(R.id.src_language) Spinner _srcLanguage;
    @BindView(R.id.target_language) Spinner _targetLanguage;
    @BindView(R.id.to_translate) TextView _textToTranslate;
    @BindView(R.id.translated_text) TextView _translatedText;
    @BindView(R.id.voice) ImageButton _speakText;
    @BindView(R.id.translate) Button _translate;
    private TextToSpeech tts;
    private final List<Language> list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_translate);
        ButterKnife.bind(this);

        LanguageViewModel languageViewModel = ViewModelProviders.of(this).get(LanguageViewModel.class);
        languageViewModel.getLanguages().observe(this, new Observer<List<Language>>() {
            @Override
            public void onChanged(List<Language> languages) {
                initSpinners(languages);
            }
        });
    }

    private void initSpinners(List<Language> languages){
        Language noLanguage = new Language();
        noLanguage.setName("Wybierz język...");
        noLanguage.setLanguageCode("");
        list.add(noLanguage);
        list.addAll(languages != null ? languages : new ArrayList<Language>());

        ArrayAdapter<Language> spinnerArrayAdapter = new ArrayAdapter<>(
                getApplicationContext(), R.layout.spinner_item, list);
        spinnerArrayAdapter.setDropDownViewResource(R.layout.spinner_item);

        _srcLanguage.setAdapter(spinnerArrayAdapter);
        _targetLanguage.setAdapter(spinnerArrayAdapter);

        _targetLanguage.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                setUpNewLocaleForTTF((Language)adapterView.getAdapter().getItem(i));
                translate(view);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    public void speak(View view) {
        final String text = _translatedText.getText().toString();

        if (!"".equals(text.trim())) {
            tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, null);

        }
    }

    public void meaning(View view) {
        final String text = _translatedText.getText().toString();

        if (!"".equals(text.trim())) {
            Intent intent = new Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://www.google.com/search?q=" + text.toLowerCase() + "+znaczenie"));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
    }

    private void setUpNewLocaleForTTF(final Language language) {

        tts = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != TextToSpeech.ERROR) {
                    tts.setLanguage(new Locale(language.getLanguageCode()));
                }
            }
        });
    }

    public void translate(View view) {
        final String toTranslate = _textToTranslate.getText().toString();
        final String srcLang = ((Language)_srcLanguage.getSelectedItem()).getLanguageCode();
        final String trgLang = ((Language)_targetLanguage.getSelectedItem()).getLanguageCode();

        if (!"".equals(srcLang) && !"".equals(trgLang)) {
            if (!"".equals(toTranslate.trim())) {
                makeTranslateCall(toTranslate, srcLang, trgLang);
            } else {
                setTranslatedText("");
            }
        } else {
            Toast.makeText(getBaseContext(), "Musisz wybrać język!", Toast.LENGTH_LONG).show();
        }
    }

    private void makeTranslateCall(final String text, final String source, final String target) {
        GoogleTranslate.getInstance().translate(
                text, source, target,
                new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                        if (response.isSuccessful()) {
                            try {
                                String body = response.body() != null ? response.body().string() : null;
                                if (body != null && !"".equals(body.trim())) {
                                    JSONObject responseBody = new JSONObject(body);
                                    JSONArray array = responseBody.getJSONObject("data").getJSONArray("translations");

                                    Type listType = new TypeToken<ArrayList<Translation>>(){}.getType();
                                    ArrayList<Translation> translations = new Gson().fromJson(array.toString(), listType);

                                    if (translations != null && !translations.isEmpty()) {
                                        onTranslateSuccess(translations);
                                    } else {
                                        onTranslateFailed();
                                    }
                                } else {
                                    onTranslateFailed();
                                }
                            } catch (JSONException | IOException | NullPointerException e) {
                                e.printStackTrace();
                                onTranslateFailed();
                            }
                        } else {
                            if (source != null && source.equals(target)) {
                                setTranslatedText(text);
                            } else {
                                onTranslateFailed();
                            }
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                        Log.d("API ERROR", t.getMessage());
                    }
                }
        );
    }

    private void onTranslateFailed() {
        Toast.makeText(getBaseContext(), "Nie można przetłumaczyć", Toast.LENGTH_LONG).show();
    }

    private void onTranslateSuccess(ArrayList<Translation> translations) {
        StringBuilder output = new StringBuilder();
        for (Translation t : translations) {
            output.append(t.getTranslatedText());
        }

        Statistics.getInstance().incrementTranslatedWordsCounter();
        setTranslatedText(output.toString());
    }

    private void setTranslatedText(String text) {
        _translatedText.setText(text);
    }

    @Override
    public void onBackPressed() {
        Statistics.getInstance().pushStatistics(getApplication());
        this.finish();
    }
}
