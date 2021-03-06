package pl.politechnika.szczesny.words_world_client.adapters;

import android.app.Application;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import pl.politechnika.szczesny.words_world_client.R;
import pl.politechnika.szczesny.words_world_client.utils.SessionUtils;
import pl.politechnika.szczesny.words_world_client.utils.Utils;
import pl.politechnika.szczesny.words_world_client.models.Language;
import pl.politechnika.szczesny.words_world_client.network.ApiManager;
import pl.politechnika.szczesny.words_world_client.viewmodel.LanguageViewModel;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LanguagesAdapter extends RecyclerView.Adapter<LanguagesAdapter.LanguageViewHolder> {
    private List<Language> languages;
    private Application application;
    private LanguageViewModel lvm;

    public LanguagesAdapter(Application application, LanguageViewModel lvm) {
        this.application = application;
        this.lvm = lvm;
    }

    @NonNull
    @Override
    public LanguagesAdapter.LanguageViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.fragment_lang_item, viewGroup, false);
        return new LanguageViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final LanguagesAdapter.LanguageViewHolder languageViewHolder, final int i) {
        final Language lang = languages.get(i);
        final Boolean isSubscribed = lang.getSubscribed();
        if (isSubscribed) {
            languageViewHolder.changeSub.setImageResource(R.drawable.ic_delete_subscription);
        } else {
            languageViewHolder.changeSub.setImageResource(R.drawable.ic_add_subscription);
        }
        languageViewHolder.changeSub.setBackgroundColor(Color.WHITE);

        languageViewHolder.changeSub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String token = SessionUtils.getToken(application);

                if (isSubscribed) {
                    ApiManager.getInstance().unsubscribeLanguage(token, lang.getId(), new Callback<Void>() {
                        @Override
                        public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                            if (response.isSuccessful()) {
                                Toast.makeText(application.getBaseContext(), "Odsubskrybowano", Toast.LENGTH_LONG).show();
                                lvm.refreshData();
                            } else {
                                Toast.makeText(application.getBaseContext(), "Błąd", Toast.LENGTH_LONG).show();

                            }
                        }

                        @Override
                        public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                            Log.d("ERROR", t.getMessage());
                        }
                    });
                } else {
                    ApiManager.getInstance().subscribeLanguage(token, lang.getId(), new Callback<Void>() {
                        @Override
                        public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                            if (response.isSuccessful()) {
                                Toast.makeText(application.getBaseContext(), "Zasubskrybowano", Toast.LENGTH_LONG).show();
                                lvm.refreshData();
                            } else {
                                Toast.makeText(application.getBaseContext(), "Błąd", Toast.LENGTH_LONG).show();

                            }
                        }

                        @Override
                        public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                            Log.d("ERROR", t.getMessage());
                        }
                    });
                }
            }
        });

        String emojiFlag = Utils.returnFlagEmojiForLanguage(lang) + "   " + lang.getName();
        languageViewHolder.name.setText(emojiFlag);
    }

    @Override
    public int getItemCount() {
        return languages == null ? 0 : languages.size();
    }

    public void setLanguages(List<Language> languages) {
        this.languages = languages;
        notifyDataSetChanged();
    }

    static class LanguageViewHolder extends RecyclerView.ViewHolder {
        CardView cv;
        TextView name;
        ImageButton changeSub;

        LanguageViewHolder(View itemView) {
            super(itemView);
            cv = itemView.findViewById(R.id.cv);
            name = itemView.findViewById(R.id.name);
            changeSub = itemView.findViewById(R.id.change_sub);
        }
    }
}
