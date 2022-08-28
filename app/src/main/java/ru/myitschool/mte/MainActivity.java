package ru.myitschool.mte;

import android.os.Bundle;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Arrays;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import ru.myitschool.mte.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    TextView tvResultObjects;
    WebView webContent;
    EditText etFindAuthor, etFindObject;
    Button btSearchAuthor, btSearchObject;
    Retrofit retrofit;

    /**
     * Uses API: The Metropolitan Museum of Art
     * https://metmuseum.github.io/
     * https://collectionapi.metmuseum.org/public/collection/v1/search?q=van%20gogh
     * https://collectionapi.metmuseum.org/public/collection/v1/objects/459123
     * https://images.metmuseum.org/CRDImages/rl/original/DT3154.jpg
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        webContent = binding.content.wvContent;
        webContent.getSettings().setBuiltInZoomControls(true);
        tvResultObjects = binding.content.tvResultObjects;
        etFindAuthor = binding.content.etFindAuthor;
        btSearchAuthor = binding.content.btSearchAuthor;
        etFindObject = binding.content.etFindObject;
        btSearchObject = binding.content.btSearchObject;

        retrofit = new Retrofit.Builder()
                .baseUrl("https://collectionapi.metmuseum.org/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        MuseumService service = retrofit.create(MuseumService.class);
        btSearchAuthor.setOnClickListener(view -> {
            tvResultObjects.setText("");
            webContent.loadUrl("about:blank");
            tvResultObjects.setText("");
            MainActivity.this.setTitle(R.string.app_name);

            Call<Objects> call = service.findObjectByAuthor(etFindAuthor.getText().toString());
            call.enqueue(new Callback<Objects>() {
                @Override
                public void onResponse(Call<Objects> call, Response<Objects> response) {
                    Objects res = response.body();
                    if (response.isSuccessful() && res.objectIDs != null) {
                        tvResultObjects.setText(Arrays.toString(res.objectIDs));
                    }
                }

                @Override
                public void onFailure(Call<Objects> call, Throwable t) {
                    Toast.makeText(
                            MainActivity.this,
                            getString(R.string.connection_problems),
                            Toast.LENGTH_SHORT
                    ).show();
                }
            });
            etFindAuthor.setText("");
        });
        btSearchObject.setOnClickListener(view -> {
            webContent.loadUrl("about:blank");
            tvResultObjects.setText("");
            try {
                Call<ArtObject> call = service
                        .getObject(Integer.parseInt(etFindObject.getText().toString()));
                call.enqueue(new Callback<>() {
                    @Override
                    public void onResponse(@NonNull Call<ArtObject> call,
                                           @NonNull Response<ArtObject> response) {
                        ArtObject res = response.body();
                        if (response.isSuccessful() && res != null) {
                            if (!res.primaryImage.isEmpty())
                                webContent.loadUrl(res.primaryImage);
                            else webContent.loadUrl(res.objectURL);
                            MainActivity.this.setTitle(res.repository);
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<ArtObject> call, @NonNull Throwable t) {
                        Toast.makeText(
                                MainActivity.this,
                                getString(R.string.connection_problems),
                                Toast.LENGTH_SHORT
                        ).show();
                    }
                });
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
            etFindObject.setText("");
        });
    }

}