package eric.jackson.stoveControl;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

public class settingsActivity extends AppCompatActivity {
    public static final String KEY_PREF_DNS = "key_stove_DNS";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportFragmentManager().beginTransaction().replace(android.R.id.content, new SettingsFragment()).commit();
    }
}
